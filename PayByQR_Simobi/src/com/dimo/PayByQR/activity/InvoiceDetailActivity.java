package com.dimo.PayByQR.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceDetailResponse;
import com.dimo.PayByQR.model.InvoiceModel;
import com.dimo.PayByQR.model.InvoiceStatusResponse;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.dimo.PayByQR.utils.CheckPaymentStatusTask;
import com.dimo.PayByQR.utils.CheckPaymentThread;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;

public class InvoiceDetailActivity extends DIMOBaseActivity {
    private PayByQRSDKListener listener;
    private String invoiceID, PayInAppURLCallback, voucherAmount = "", offlineQRAmount;
    private InvoiceModel invoiceModel;
    private TextView txtTitle, txtPaidAmount, txtMerchant, txtOriginalAmount, txtDiscountAmount,
            txtVoucherAmount, txtVoucherCount, txtPaymentPlainInfo, txtVoucherUsed, txtTipInfo,
            txtGunakanSekarang, txtMaxRedeemInfo;
    private ImageView btnBack;
    private DIMOButton btnPay, btnTip;
    private ImageButton btnVoucherPlus, btnVoucherMin;
    private ProgressBar loader;
    private LinearLayout layoutButtonAction, layoutDiscountPercentage;
    private RelativeLayout layoutDiscountDetail, layoutVoucherDetail, layoutMaxRedeemInfo;
    private CheckPaymentThread checkPaymentThread;
    private int mOriginalAmount=0, mPaidAmount=0, mAmountOfDiscount=0, voucherCount=0, voucherUsed=0,
            maxVoucherUsed=0, maxVoucherAmount=0, voucherAmountInt=0;
    private InvoiceDetailResponse.TipDetail tipDetail;
    private boolean isPaymentProcessRun = false;
    private AlternateDialog alternateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.d("RHIO", "Screen density: " + metrics.densityDpi);
        Log.d("RHIO", "Screen width px: " + metrics.widthPixels);
        Log.d("RHIO", "Screen height px: "+metrics.heightPixels);
        Log.d("RHIO", "Screen xdpi: "+metrics.xdpi);
        Log.d("RHIO", "Screen ydpi: "+metrics.ydpi);*/

        listener = PayByQRSDK.getListener();

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtPaidAmount = (TextView) findViewById(R.id.activity_invoiceDetail_paidAmount);
        txtMerchant = (TextView) findViewById(R.id.activity_invoiceDetail_merchant);
        btnPay = (DIMOButton) findViewById(R.id.activity_invoiceDetail_btn_pay);
        loader = (ProgressBar) findViewById(R.id.activity_invoiceDetail_loader);
        layoutButtonAction = (LinearLayout) findViewById(R.id.activity_invoiceDetail_btn_block);
        txtOriginalAmount = (TextView) findViewById(R.id.activity_invoiceDetail_originalAmount);
        txtDiscountAmount = (TextView) findViewById(R.id.activity_invoiceDetail_discount_amount);
        layoutDiscountDetail = (RelativeLayout) findViewById(R.id.activity_invoiceDetail_originalAmount_layout);
        layoutMaxRedeemInfo = (RelativeLayout) findViewById(R.id.activity_invoiceDetail_maxRedeemInfo_layout);
        layoutVoucherDetail = (RelativeLayout) findViewById(R.id.activity_invoiceDetail_voucher_layout);
        txtMaxRedeemInfo = (TextView) findViewById(R.id.activity_invoiceDetail_maxRedeemInfo_text);
        txtPaymentPlainInfo = (TextView) findViewById(R.id.activity_invoiceDetail_info);
        txtVoucherAmount = (TextView) findViewById(R.id.activity_invoiceDetail_voucher_amount);
        txtVoucherCount = (TextView) findViewById(R.id.activity_invoiceDetail_voucher_count);
        txtVoucherUsed = (TextView) findViewById(R.id.activity_invoiceDetail_voucher_used);
        txtGunakanSekarang = (TextView) findViewById(R.id.activity_invoiceDetail_txt_voucherGunakan);
        btnVoucherPlus = (ImageButton) findViewById(R.id.activity_invoiceDetail_voucher_box_plus);
        btnVoucherMin = (ImageButton) findViewById(R.id.activity_invoiceDetail_voucher_box_min);
        layoutDiscountPercentage = (LinearLayout) findViewById(R.id.activity_invoiceDetail_discount_text);
        txtTipInfo = (TextView) findViewById(R.id.activity_invoiceDetail_info_tip);
        btnTip = (DIMOButton) findViewById(R.id.activity_invoiceDetail_btn_tip);

        layoutButtonAction.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
        txtTitle.setText(getString(R.string.text_header_title_confirm));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceModel.originalAmount = mOriginalAmount;
                invoiceModel.paidAmount = mPaidAmount;
                invoiceModel.numberOfCoupons = voucherUsed;
                invoiceModel.amountOfDiscount = mAmountOfDiscount;
                invoiceModel.tipAmount = 0;
                invoiceModel.pointsRedeemed = 0;
                invoiceModel.amountRedeemed = 0;
                if(invoiceModel.discountType.equals(LoyaltyProgramModel.LOYALTY_TYPE_POINTS) ||
                        invoiceModel.discountType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH) ||
                        invoiceModel.discountType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)){
                    if(voucherUsed == 0)
                        invoiceModel.discountType = "";
                }else{
                    if(invoiceModel.discountType.length()>20)
                        invoiceModel.discountType = invoiceModel.discountType.substring(0, 20);
                }

                listener.callbackPayInvoice(invoiceModel);
                doPayment();
            }
        });

        btnTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(invoiceModel.discountType.equals(LoyaltyProgramModel.LOYALTY_TYPE_POINTS) ||
                        invoiceModel.discountType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH) ||
                        invoiceModel.discountType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)){
                    if(voucherUsed == 0)
                        invoiceModel.discountType = "";
                }else{
                    if(invoiceModel.discountType.length()>20)
                        invoiceModel.discountType = invoiceModel.discountType.substring(0, 20);
                }
                Intent intentTip = new Intent(InvoiceDetailActivity.this, TipActivity.class);
                intentTip.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, invoiceID);
                intentTip.putExtra(Constant.INTENT_EXTRA_MERCHANT_NAME, invoiceModel.merchantName);
                intentTip.putExtra(Constant.INTENT_EXTRA_DISCOUNT_TYPE, invoiceModel.discountType);
                intentTip.putExtra(Constant.INTENT_EXTRA_LOYALTY_PROGRAM_NAME, invoiceModel.loyaltyProgramName);
                intentTip.putExtra(Constant.INTENT_EXTRA_ORIGINAL_AMOUNT, mOriginalAmount);
                intentTip.putExtra(Constant.INTENT_EXTRA_PAID_AMOUNT, mPaidAmount);
                intentTip.putExtra(Constant.INTENT_EXTRA_NUMBER_OF_COUPONS, voucherUsed);
                intentTip.putExtra(Constant.INTENT_EXTRA_AMOUNT_OF_DISCOUNT, mAmountOfDiscount);
                intentTip.putExtra(Constant.INTENT_EXTRA_POINT_REDEEMED, 0);
                intentTip.putExtra(Constant.INTENT_EXTRA_AMOUNT_REDEEMED, 0);
                if(null != tipDetail) {
                    intentTip.putExtra(Constant.INTENT_EXTRA_TIP_SUGGESTION, tipDetail.suggestedAmount);
                    intentTip.putExtra(Constant.INTENT_EXTRA_TIP_PROPOSITION_1, tipDetail.firstProposition);
                    intentTip.putExtra(Constant.INTENT_EXTRA_TIP_PROPOSITION_2, tipDetail.secondProposition);
                }
                startActivityForResult(intentTip, 0);
                overridePendingTransition(R.anim.flip_from_middle, R.anim.flip_to_middle);
            }
        });

        btnVoucherMin.setSelected(false);
        btnVoucherPlus.setSelected(true);

        invoiceModel = new InvoiceModel();
        invoiceID = getIntent().getStringExtra(Constant.INTENT_EXTRA_INVOICE_ID);
        PayInAppURLCallback = getIntent().getStringExtra(Constant.INTENT_EXTRA_INAPP_URL_CALLBACK);
        offlineQRAmount = getIntent().getStringExtra(Constant.INTENT_EXTRA_ORIGINAL_AMOUNT);
        new GetInvoiceDetailTask(invoiceID).execute();

        if(checkPaymentThread!=null) {
            checkPaymentThread.stopPolling();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(closeSDKBroadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION_CLOSE_SDK));
        LocalBroadcastManager.getInstance(this).registerReceiver(notifyTrxBroadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION_NOTIFY_TRX));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeSDKBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyTrxBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        PayByQRProperties.setSDKContext(this);
        super.onResume();
    }

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK(true);
        }
    };

    BroadcastReceiver notifyTrxBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(PayByQRProperties.isPolling() && !checkPaymentThread.isPollingRun()){
                //do nothing
            } else {
                if (null != checkPaymentThread) checkPaymentThread.stopPolling();

                final int code = intent.getIntExtra(Constant.INTENT_EXTRA_NOTIFY_CODE, 0);
                final String desc = intent.getStringExtra(Constant.INTENT_EXTRA_NOTIFY_DESC);
                boolean isDefaultLayout = intent.getBooleanExtra(Constant.INTENT_EXTRA_NOTIFY_LAYOUT, false);

                if (PayByQRProperties.isUsingCustomDialog()) {
                    if (code == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
                        new CheckPaymentStatusTask(InvoiceDetailActivity.this, invoiceID, new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == Constant.MESSAGE_END_OK) {
                                    LoyaltyModel loyaltyModel = null;
                                    if(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false)) {
                                        loyaltyModel = new LoyaltyModel();
                                        loyaltyModel.loyaltyProgramLabel = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TITLE);
                                        loyaltyModel.pointsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED);
                                        loyaltyModel.pointsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE);
                                        loyaltyModel.couponsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED);
                                        loyaltyModel.couponsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE);
                                        loyaltyModel.pointAmountForCoupon = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON);
                                        loyaltyModel.couponValue = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE);
                                        loyaltyModel.rewardType = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TYPE);
                                    }

                                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                                        listener.callbackShowDialog(InvoiceDetailActivity.this, Constant.STATUS_CODE_PAYMENT_SUCCESS,
                                                getString(R.string.text_payment_success), loyaltyModel);
                                    } else {
                                        closeSDK(false, true, Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success), loyaltyModel);
                                    }
                                }
                            }
                        }).execute();
                    } else {
                        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                            listener.callbackShowDialog(InvoiceDetailActivity.this, code, desc, null);
                        } else {
                            closeSDK(false, true, code, desc, null);
                        }
                    }
                } else {
                    if(isDefaultLayout) {
                        if (code == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
                            new CheckPaymentStatusTask(InvoiceDetailActivity.this, invoiceID, null).execute();
                        } else {
                            goToFailedScreen(getString(R.string.text_payment_failed), desc, Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
                        }
                    } else {
                        if (code == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
                            new CheckPaymentStatusTask(InvoiceDetailActivity.this, invoiceID, new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == Constant.MESSAGE_END_OK) {
                                        LoyaltyModel loyaltyModel = null;
                                        if(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false)) {
                                            loyaltyModel = new LoyaltyModel();
                                            loyaltyModel.loyaltyProgramLabel = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TITLE);
                                            loyaltyModel.pointsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED);
                                            loyaltyModel.pointsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE);
                                            loyaltyModel.couponsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED);
                                            loyaltyModel.couponsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE);
                                            loyaltyModel.pointAmountForCoupon = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON);
                                            loyaltyModel.couponValue = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE);
                                            loyaltyModel.rewardType = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TYPE);
                                        }

                                        alternateDialog = new AlternateDialog(code, desc, loyaltyModel,
                                                msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS), alternateDialogHandler);
                                        alternateDialog.setCancelable(false);
                                        alternateDialog.show(getSupportFragmentManager(), "DIALOG_ALTERNATE");
                                        loader.setVisibility(View.GONE);
                                    }
                                }
                            }).execute();
                        } else {
                            alternateDialog = new AlternateDialog(code, desc, null, null, alternateDialogHandler);
                            alternateDialog.setCancelable(false);
                            alternateDialog.show(getSupportFragmentManager(), "DIALOG_ALTERNATE");
                            loader.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkPaymentThread!=null) {
            checkPaymentThread.stopPolling();
        }
    }

    @Override
    public void onBackPressed() {
        if(!isPaymentProcessRun) {
            DIMOUtils.showAlertDialog(this, "", getString(R.string.text_transaction_canceled_by_user),
                    getString(R.string.button_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.callbackUserHasCancelTransaction();
                            if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP)
                                listener.callbackSDKClosed();
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.out_to_bottom);
                        }
                    }, getString(R.string.button_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            if (data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, false)){
                if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                    listener.callbackShowDialog(InvoiceDetailActivity.this,
                            data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                            data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC),
                            (LoyaltyModel) data.getParcelableExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY));
                } else {
                    closeSDK(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, false),
                            data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, true),
                            data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                            data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC),
                            (LoyaltyModel) data.getParcelableExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY));
                }
            }else{
                /*if(null != alternateDialog && alternateDialog.isVisible())
                    alternateDialog.dismissDialog();
                else*/
                    closeSDK(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true));
            }
        }else if(Constant.ACTIVITY_RESULT_NO_CONNECTION == resultCode){
            closeSDK(false);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(Constant.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
            payInAppCallback(PayInAppURLCallback);
            listener.callbackSDKClosed();
        }
        finish();
    }

    private void closeSDK(boolean isCloseSDK, boolean isShowCustomDialog, int code, String desc, LoyaltyModel loyaltyModel){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        intent.putExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, isShowCustomDialog);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, code);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC, desc);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY, loyaltyModel);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
            payInAppCallback(PayInAppURLCallback);
            listener.callbackSDKClosed();
        }
        finish();
    }

    private void doPayment(){
        isPaymentProcessRun = true;
        layoutButtonAction.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        if (PayByQRProperties.isPolling()) {
            checkPaymentThread = new CheckPaymentThread(this, invoiceID, endInvoicePayed);
            checkPaymentThread.start();
        }
    }

    private class GetInvoiceDetailTask extends AsyncTask<Void, Void, String> {
        String invoiceId;
        ProgressDialog progressDialog;

        public GetInvoiceDetailTask(String invoiceId){
            this.invoiceId = invoiceId;
            progressDialog = new ProgressDialog(InvoiceDetailActivity.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_get_invoice_detail));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getInvoiceDetail(InvoiceDetailActivity.this, invoiceId, offlineQRAmount);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                InvoiceDetailResponse invoiceDetail = DIMOService.parseJSONInvoiceDetail(InvoiceDetailActivity.this, s);
                invoiceID = invoiceDetail.invoiceId;
                mOriginalAmount = invoiceDetail.amount;
                txtMerchant.setText(invoiceDetail.receiver.toUpperCase());

                //Loyalty Section
                String discountType = "", loyaltyProgramName = "";
                if(null != invoiceDetail.currentLoyaltyProgram.result && invoiceDetail.currentLoyaltyProgram.result.equals("success")){
                    if(PayByQRProperties.isDebugMode()) Log.d("COLO", "Merchant has Loyalty Program");
                    if(PayByQRProperties.isUsingLoyalty()){
                        if(PayByQRProperties.isDebugMode()) Log.d("COLO", "Host app is using Loyalty Module");
                        if(!invoiceDetail.hasLoyaltyCard){
                            if(PayByQRProperties.isDebugMode()) Log.d("COLO", "User does not have loyaltyCard");
                            new JoinLoyaltyProgramTask(""+invoiceDetail.currentLoyaltyProgram.loyaltyProgram.loyaltyProgramId).execute();
                        }

                        final LoyaltyProgramModel loyaltyProgramModel = invoiceDetail.currentLoyaltyProgram.loyaltyProgram;
                        discountType = "";
                        loyaltyProgramName = loyaltyProgramModel.label;

                        //determine loyalty type
                        if(loyaltyProgramModel.loyaltyProgramType.equals(LoyaltyProgramModel.LOYALTY_TYPE_DISCOUNT)){
                            if(invoiceDetail.permanentPercentageDiscount > 0) {
                                discountType = loyaltyProgramModel.loyaltyProgramType;
                                mAmountOfDiscount = mOriginalAmount - invoiceDetail.correctedInvoiceAmountWithPercentage;

                                layoutDiscountDetail.setVisibility(View.VISIBLE);
                                layoutMaxRedeemInfo.setVisibility(View.GONE);
                                txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                                layoutVoucherDetail.setVisibility(View.GONE);
                                txtDiscountAmount.setText((int) invoiceDetail.permanentPercentageDiscount + "%");
                            } else {
                                if (loyaltyProgramModel.minTransAmountForDiscount > 0 && invoiceDetail.amount < loyaltyProgramModel.minTransAmountForDiscount) {
                                    txtMaxRedeemInfo.setText(getString(R.string.text_info_min_trx_for_disc,
                                            DIMOUtils.formatAmount(Integer.toString(loyaltyProgramModel.minTransAmountForDiscount)),
                                            "" + loyaltyProgramModel.permanentPercentageDiscount));

                                    layoutDiscountDetail.setVisibility(View.GONE);
                                    layoutMaxRedeemInfo.setVisibility(View.VISIBLE);
                                    txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                                    layoutVoucherDetail.setVisibility(View.GONE);
                                } else {
                                    if (loyaltyProgramModel.maxRedeem > 0) {
                                        if (loyaltyProgramModel.isMaxRedeemDaily)
                                            txtMaxRedeemInfo.setText(getString(R.string.text_info_max_redeem_daily, "" + loyaltyProgramModel.maxRedeem));
                                        else
                                            txtMaxRedeemInfo.setText(getString(R.string.text_info_max_redeem_event, "" + loyaltyProgramModel.maxRedeem));

                                        layoutDiscountDetail.setVisibility(View.GONE);
                                        layoutMaxRedeemInfo.setVisibility(View.VISIBLE);
                                        txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                                        layoutVoucherDetail.setVisibility(View.GONE);
                                    } else {
                                        layoutDiscountDetail.setVisibility(View.GONE);
                                        layoutMaxRedeemInfo.setVisibility(View.GONE);
                                        txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                                        layoutVoucherDetail.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }else if(loyaltyProgramModel.loyaltyProgramType.equals(LoyaltyProgramModel.LOYALTY_TYPE_POINTS)){
                            if(invoiceDetail.couponList.length>0) {
                                //has loyalty, has voucher
                                if(PayByQRProperties.isDebugMode()) Log.d("COLO", "User has Voucher");
                                voucherCount = invoiceDetail.couponList.length;
                                voucherAmountInt = loyaltyProgramModel.discountAmount;

                                if (loyaltyProgramModel.rewardType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH)) {
                                    discountType = LoyaltyProgramModel.REWARD_TYPE_CASH;
                                    voucherAmount = getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(""+voucherAmountInt);

                                    double maxVoucherAmountTemp = (double)(mOriginalAmount-PayByQRProperties.getMinimumTransaction())/(double)voucherAmountInt;
                                    //maxVoucherAmount = (int) Math.ceil(maxVoucherAmountTemp);
                                    maxVoucherAmount = (int) maxVoucherAmountTemp;
                                    maxVoucherUsed = Math.min(voucherCount, maxVoucherAmount);

                                    if(PayByQRProperties.isDebugMode()){
                                        Log.d("COLO", "maxVoucherAmountTemp = "+maxVoucherAmountTemp);
                                        Log.d("COLO", "maxVoucherAmount = "+maxVoucherAmount);
                                        Log.d("COLO", "maxVoucherUsed = "+maxVoucherUsed);
                                    }

                                    int temp = mOriginalAmount - voucherAmountInt;
                                    if(temp < PayByQRProperties.getMinimumTransaction())
                                        btnVoucherPlus.setSelected(false);
                                } else if (loyaltyProgramModel.rewardType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)) {
                                    discountType = LoyaltyProgramModel.REWARD_TYPE_PERCENT;
                                    voucherAmount = voucherAmountInt+"%";
                                    maxVoucherUsed = 1;
                                    maxVoucherAmount = 1;

                                    int temp = mOriginalAmount - ((mOriginalAmount * voucherAmountInt)/100);
                                    if(temp < PayByQRProperties.getMinimumTransaction())
                                        btnVoucherPlus.setSelected(false);
                                }
                                txtVoucherAmount.setText(voucherAmount);
                                txtVoucherCount.setText(voucherAmount+" x "+voucherCount);

                                btnVoucherMin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeVoucher(loyaltyProgramModel.rewardType);
                                    }
                                });
                                btnVoucherPlus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addVoucher(loyaltyProgramModel.rewardType);
                                    }
                                });
                                layoutDiscountDetail.setVisibility(View.GONE);
                                layoutMaxRedeemInfo.setVisibility(View.GONE);
                                txtPaymentPlainInfo.setVisibility(View.GONE);
                                layoutVoucherDetail.setVisibility(View.VISIBLE);
                            }else{
                                //has loyalty, but user doesn't have any voucher
                                mAmountOfDiscount = 0;
                                layoutDiscountDetail.setVisibility(View.GONE);
                                layoutMaxRedeemInfo.setVisibility(View.GONE);
                                txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                                layoutVoucherDetail.setVisibility(View.GONE);
                            }
                        }
                    }else{
                        //has loyalty, but issuer turn off Loyalty Module
                        mAmountOfDiscount = 0;
                        layoutDiscountDetail.setVisibility(View.GONE);
                        layoutMaxRedeemInfo.setVisibility(View.GONE);
                        txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                        layoutVoucherDetail.setVisibility(View.GONE);
                    }
                }else{
                    //Payment Plain
                    mAmountOfDiscount = 0;
                    layoutDiscountDetail.setVisibility(View.GONE);
                    layoutMaxRedeemInfo.setVisibility(View.GONE);
                    txtPaymentPlainInfo.setVisibility(View.VISIBLE);
                    layoutVoucherDetail.setVisibility(View.GONE);
                }
                mPaidAmount = mOriginalAmount - mAmountOfDiscount;
                txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(mPaidAmount)));
                txtOriginalAmount.setText(DIMOUtils.formatAmount(Integer.toString(mOriginalAmount)));

                //Tip Section
                if(invoiceDetail.tipEnabled && PayByQRProperties.isUsingTip()){
                    tipDetail = invoiceDetail.tip;
                    txtTipInfo.setVisibility(View.VISIBLE);
                    btnTip.setVisibility(View.VISIBLE);
                }else{
                    txtTipInfo.setVisibility(View.GONE);
                    btnTip.setVisibility(View.GONE);
                }

                invoiceModel.invoiceID = invoiceDetail.invoiceId;
                invoiceModel.merchantName = invoiceDetail.receiver;
                invoiceModel.discountType = discountType;
                invoiceModel.loyaltyProgramName = loyaltyProgramName;

                if(mPaidAmount < PayByQRProperties.getMinimumTransaction()){
                    //go to Minimum Payment Error
                    if(PayByQRProperties.isUsingCustomDialog()){
                        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                            listener.callbackShowDialog(InvoiceDetailActivity.this, Constant.REQUEST_CODE_ERROR_MINIMUM_TRX,
                                    getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), null);
                        } else {
                            closeSDK(false, true, Constant.REQUEST_CODE_ERROR_MINIMUM_TRX, getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), null);
                        }
                    }else {
                        goToFailedScreen(getString(R.string.text_payment_failed), getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), Constant.REQUEST_CODE_ERROR_MINIMUM_TRX);
                    }
                }
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                        listener.callbackShowDialog(InvoiceDetailActivity.this, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                    } else {
                        closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                    }
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else if (e.getErrorCode() == Constant.ERROR_CODE_INVALID_QR) {
                        goToFailedScreen(getString(R.string.error_invalid_qr_title), e.getErrorMessage(), Constant.REQUEST_CODE_ERROR_INVALID_QR);
                    } else {
                        goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                    }
                }
            }
        }
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(InvoiceDetailActivity.this, FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(InvoiceDetailActivity.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void addVoucher(String voucherType){
        if(null == checkPaymentThread || !checkPaymentThread.isAlive()) {
            if (btnVoucherPlus.isSelected()) {
                btnVoucherMin.setSelected(true);

                voucherUsed++;
                voucherCount--;
                if (voucherUsed == maxVoucherUsed) {
                    txtGunakanSekarang.setVisibility(View.INVISIBLE);
                    btnVoucherPlus.setSelected(false);
                }

                if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH)) {
                    mAmountOfDiscount = voucherUsed * voucherAmountInt;
                    layoutDiscountPercentage.setVisibility(View.GONE);
                } else if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)) {
                    mAmountOfDiscount = (mOriginalAmount * voucherAmountInt) / 100;
                    txtDiscountAmount.setText(voucherAmountInt + "%");
                    layoutDiscountPercentage.setVisibility(View.VISIBLE);
                }
                mPaidAmount = mOriginalAmount - mAmountOfDiscount;
                if(mPaidAmount<0){
                    mPaidAmount = 0;
                    mAmountOfDiscount = mOriginalAmount;
                }

                txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(mPaidAmount)));
                txtOriginalAmount.setText(DIMOUtils.formatAmount(Integer.toString(mOriginalAmount)));

                layoutDiscountDetail.setVisibility(View.VISIBLE);
                txtVoucherCount.setText(voucherAmount + " x " + voucherCount);
                txtVoucherUsed.setText("x" + voucherUsed);
            } else {
                if (voucherCount > 0) {
                    if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH)) {
                        DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_voucher_min_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                        //DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_voucher_max_cash), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                    } else if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)) {
                        if (voucherUsed > 0) {
                            DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_voucher_max_percentage), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                        }else{
                            DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_voucher_min_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                        }
                    }
                }else{
                    DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_voucher_max), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                }
            }
        }
    }

    private void removeVoucher(String voucherType){
        if(null == checkPaymentThread || !checkPaymentThread.isAlive()) {
            if (btnVoucherMin.isSelected() && voucherUsed > 0) {
                txtGunakanSekarang.setVisibility(View.VISIBLE);
                btnVoucherPlus.setSelected(true);

                voucherUsed--;
                voucherCount++;
                if (voucherUsed == 0) {
                    btnVoucherMin.setSelected(false);
                    layoutDiscountDetail.setVisibility(View.GONE);
                }

                if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH)) {
                    mAmountOfDiscount = voucherUsed * voucherAmountInt;
                } else if (voucherType.equals(LoyaltyProgramModel.REWARD_TYPE_PERCENT)) {
                    mAmountOfDiscount = 0;
                }
                mPaidAmount = mOriginalAmount - mAmountOfDiscount;
                txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(mPaidAmount)));
                txtOriginalAmount.setText(DIMOUtils.formatAmount(Integer.toString(mOriginalAmount)));

                txtVoucherCount.setText(voucherAmount + " x " + voucherCount);
                txtVoucherUsed.setText("x" + voucherUsed);
            }
        }
    }

    private class JoinLoyaltyProgramTask extends AsyncTask<Void, Void, String> {
        String programID;
        ProgressDialog progressDialog;

        public JoinLoyaltyProgramTask(String programID){
            this.programID = programID;
            progressDialog = new ProgressDialog(InvoiceDetailActivity.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_join_loyalty_program));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.joinLoyaltyProgram(InvoiceDetailActivity.this, programID);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try{
                int loyaltyCardId = DIMOService.parseJSONJoinLoyaltyProgram(InvoiceDetailActivity.this, s);
                new GetInvoiceDetailTask(invoiceID).execute();
            } catch (PayByQRException e) {
                if(PayByQRProperties.isUsingCustomDialog()){
                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                        listener.callbackShowDialog(InvoiceDetailActivity.this, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                    } else {
                        closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                    }
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler endInvoicePayed = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isPaymentProcessRun = false;
            if (msg.what == Constant.MESSAGE_END_OK) {
                checkPaymentThread = null;
                if(PayByQRProperties.isUsingCustomDialog()){
                    LoyaltyModel loyaltyModel = null;
                    if(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false)) {
                        loyaltyModel = new LoyaltyModel();
                        loyaltyModel.loyaltyProgramLabel = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TITLE);
                        loyaltyModel.pointsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED);
                        loyaltyModel.pointsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE);
                        loyaltyModel.couponsGenerated = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED);
                        loyaltyModel.couponsBalance = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE);
                        loyaltyModel.pointAmountForCoupon = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON);
                        loyaltyModel.couponValue = msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE);
                        loyaltyModel.rewardType = msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TYPE);
                    }

                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                        listener.callbackShowDialog(InvoiceDetailActivity.this, Constant.STATUS_CODE_PAYMENT_SUCCESS,
                                getString(R.string.text_payment_success), loyaltyModel);
                    } else {
                        closeSDK(false, true, Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success), loyaltyModel);
                    }
                }else {
                    Intent intentSuccess = new Intent(InvoiceDetailActivity.this, PaymentSuccessActivity.class);
                    if(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false)){
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, true);
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS, msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_TYPE, msg.getData().getString(Constant.INTENT_EXTRA_FIDELITIZ_TYPE));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED));
                        intentSuccess.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE, msg.getData().getInt(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE));
                    }
                    startActivityForResult(intentSuccess, 0);
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
                }
            }else if(msg.what == Constant.MESSAGE_END_ERROR){
                String errorMessage = msg.getData().getString(Constant.INTENT_EXTRA_ERROR_DETAIL);
                if(PayByQRProperties.isUsingCustomDialog()){
                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                        listener.callbackShowDialog(InvoiceDetailActivity.this, Constant.ERROR_CODE_PAYMENT_FAILED, errorMessage, null);
                    } else {
                        closeSDK(false, true, Constant.ERROR_CODE_PAYMENT_FAILED, errorMessage, null);
                    }
                }else {
                    if(errorMessage.contains(getString(R.string.error_connection_message))){
                        goToNoConnectionScreen();
                    }else {
                        goToFailedScreen(getString(R.string.text_payment_failed), errorMessage, Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
                    }
                }
            }else if(msg.what == Constant.MESSAGE_END_TIME_OUT){
                String errorMessage = msg.getData().getString(Constant.INTENT_EXTRA_ERROR_DETAIL);
                if(PayByQRProperties.isUsingCustomDialog()){
                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP){
                        listener.callbackShowDialog(InvoiceDetailActivity.this, Constant.ERROR_CODE_TIME_OUT, errorMessage, null);
                    } else {
                        closeSDK(false, true, Constant.ERROR_CODE_TIME_OUT, errorMessage, null);
                    }
                }else {
                    goToFailedScreen(getString(R.string.text_payment_timeout), errorMessage, Constant.REQUEST_CODE_ERROR_TIME_OUT);
                }
            }
        }

    };

    private void payInAppCallback(String URLScheme){
        try{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URLScheme));
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(i);
        } catch (ActivityNotFoundException notFoundException){
            notFoundException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler alternateDialogHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.MESSAGE_END_OK) {
                closeSDK(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true));
            }
        }
    };
}
