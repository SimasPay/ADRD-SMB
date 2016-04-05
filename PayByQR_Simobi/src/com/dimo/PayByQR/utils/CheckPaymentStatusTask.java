package com.dimo.PayByQR.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;

import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.PaymentSuccessActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceStatusResponse;

/**
 * Created by Rhio on 3/21/16.
 */
public class CheckPaymentStatusTask extends AsyncTask<Void, Void, String> {
    private String invoiceID;
    private Activity activity;

    public CheckPaymentStatusTask (Activity activity, String invoiceID) {
        this.invoiceID = invoiceID;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        return DIMOService.getInvoiceStatus(activity, invoiceID);
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            InvoiceStatusResponse statusResponse = DIMOService.parseJSONInvoiceStatus(activity, response);
            if (statusResponse.status.equals("PAID") || statusResponse.status.equals("PAID_AND_CHECKED")) {
                Intent intentSuccess = new Intent(activity, PaymentSuccessActivity.class);
                if (null != statusResponse.fidelitizInfo) {
                    InvoiceStatusResponse.FidelitizInfo info = statusResponse.fidelitizInfo;
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, true);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS, statusResponse.rawJSON);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_TYPE, info.rewardType);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE, info.pointsBalance);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED, info.pointsGenerated);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON, info.pointAmountForCoupon);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE, info.couponsBalance);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED, info.couponsGenerated);
                    intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE, info.couponValue);
                }
                activity.startActivityForResult(intentSuccess, 0);
                activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
            } else {
                Intent intentSuccess = new Intent(activity, PaymentSuccessActivity.class);
                activity.startActivityForResult(intentSuccess, 0);
                activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
            }
        } catch (Exception e) {
            Intent intentSuccess = new Intent(activity, PaymentSuccessActivity.class);
            activity.startActivityForResult(intentSuccess, 0);
            activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        }
    }
}