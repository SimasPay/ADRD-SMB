package com.dimo.PayByQR.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceStatusResponse;

/**
 * Created by Rhio on 11/19/15.
 */
public class CheckPaymentThread extends Thread {
    private String invoiceId;
    private Handler endHandler;
    private Context context;

    private boolean run = true;
    private static final int POLLING_DELAY = 1000; // 1 s
    private int counter = 0;
    private int maxCounter = 30;

    public CheckPaymentThread(Context context, String invoiceId, Handler endHandler) {
        this.context = context;
        this.invoiceId = invoiceId;
        this.endHandler = endHandler;
    }

    @Override
    public void run() {
        String response = null;
        InvoiceStatusResponse statusResponse = null;
        while (run) {
            if(counter < maxCounter) {
                try {
                    response = DIMOService.getInvoiceStatus(context, invoiceId);
                    statusResponse = DIMOService.parseJSONInvoiceStatus(context, response);
                    if (statusResponse.status.equals("PAID") || statusResponse.status.equals("PAID_AND_CHECKED")) {
                        if(run) {
                            run = false;
                            Message message = endHandler.obtainMessage(Constant.MESSAGE_END_OK);
                            if (null != statusResponse.fidelitizInfo) {
                                InvoiceStatusResponse.FidelitizInfo info = statusResponse.fidelitizInfo;
                                message.getData().putBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, true);
                                message.getData().putString(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS, statusResponse.rawJSON);
                                message.getData().putString(Constant.INTENT_EXTRA_FIDELITIZ_TITLE, info.loyaltyProgramLabel);
                                message.getData().putString(Constant.INTENT_EXTRA_FIDELITIZ_TYPE, info.rewardType);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE, info.pointsBalance);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED, info.pointsGenerated);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON, info.pointAmountForCoupon);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE, info.couponsBalance);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED, info.couponsGenerated);
                                message.getData().putInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE, info.couponValue);
                            } else
                                message.getData().putBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false);

                            endHandler.sendMessage(message);
                        }
                    } else if (statusResponse.status.equals("CANCELLED")){
                        if(run) {
                            run = false;
                            Message message = endHandler.obtainMessage(Constant.MESSAGE_END_ERROR);
                            message.getData().putString(Constant.INTENT_EXTRA_ERROR_DETAIL, context.getString(R.string.error_transaction_canceled));
                            endHandler.sendMessage(message);
                        }
                    } else if (statusResponse.status.equals("OUTDATED")){
                        if(run) {
                            run = false;
                            Message message = endHandler.obtainMessage(Constant.MESSAGE_END_ERROR);
                            message.getData().putString(Constant.INTENT_EXTRA_ERROR_DETAIL, context.getString(R.string.error_transaction_expired));
                            endHandler.sendMessage(message);
                        }
                    } else {
                        try {
                            Thread.sleep(POLLING_DELAY);
                        } catch (InterruptedException e) {
                            run = false;
                        }
                    }
                } catch (PayByQRException e) {
                    if(run) {
                        run = false;
                        Message message = endHandler.obtainMessage(Constant.MESSAGE_END_ERROR);
                        message.getData().putString(Constant.INTENT_EXTRA_ERROR_DETAIL, e.getErrorMessage() + " " + e.getErrorDetail());
                        endHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    if(run) {
                        run = false;
                        Message message = endHandler.obtainMessage(Constant.MESSAGE_END_ERROR);
                        message.getData().putString(Constant.INTENT_EXTRA_ERROR_DETAIL, e.getMessage());
                        endHandler.sendMessage(message);
                    }
                }
                counter++;
            }else{
                if(run) {
                    run = false;
                    Message message = endHandler.obtainMessage(Constant.MESSAGE_END_TIME_OUT);
                    message.getData().putString(Constant.INTENT_EXTRA_ERROR_DETAIL, context.getString(R.string.error_max_retry));
                    endHandler.sendMessage(message);
                }
            }
        }
    }

    public synchronized void stopPolling(){
        this.run=false;
    }

    public boolean isPollingRun(){
        return run;
    }
}