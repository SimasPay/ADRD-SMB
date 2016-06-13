package com.dimo.PayByQR.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceModel;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.dimo.PayByQR.utils.CheckPaymentStatusTask;
import com.dimo.PayByQR.utils.CheckPaymentThread;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOEditText;

public class TipActivity extends DIMOBaseActivity {
    private PayByQRSDKListener listener;
    private InvoiceModel invoiceModel;
    private TextView txtTitle, txtPaidAmount, txtOriginalAmount, txtTipProposition1, txtTipProposition2;
    private EditText editTipAmount;
    private ImageView btnBack;
    private ImageButton btnClear, btnProposition1, btnProposition2;
    private DIMOButton btnPay;
    private ProgressBar loader;
    private int suggestedTipAmount, tipProposition1, tipProposition2, tipAmount, totalAmount;
    private CheckPaymentThread checkPaymentThread;
    private boolean isPaymentProcessRun = false;
    private AlternateDialog alternateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        listener = PayByQRSDK.getListener();

        Intent intent = getIntent();
        invoiceModel = new InvoiceModel();
        invoiceModel.invoiceID = intent.getStringExtra(Constant.INTENT_EXTRA_INVOICE_ID);
        invoiceModel.merchantName = intent.getStringExtra(Constant.INTENT_EXTRA_MERCHANT_NAME);
        invoiceModel.discountType = intent.getStringExtra(Constant.INTENT_EXTRA_DISCOUNT_TYPE);
        invoiceModel.loyaltyProgramName = intent.getStringExtra(Constant.INTENT_EXTRA_LOYALTY_PROGRAM_NAME);
        invoiceModel.originalAmount = intent.getIntExtra(Constant.INTENT_EXTRA_ORIGINAL_AMOUNT, 0);
        invoiceModel.paidAmount = intent.getIntExtra(Constant.INTENT_EXTRA_PAID_AMOUNT, 0);
        invoiceModel.numberOfCoupons = intent.getIntExtra(Constant.INTENT_EXTRA_NUMBER_OF_COUPONS, 0);
        invoiceModel.amountOfDiscount = intent.getIntExtra(Constant.INTENT_EXTRA_AMOUNT_OF_DISCOUNT, 0);
        invoiceModel.pointsRedeemed = intent.getIntExtra(Constant.INTENT_EXTRA_POINT_REDEEMED, 0);
        invoiceModel.amountRedeemed = intent.getIntExtra(Constant.INTENT_EXTRA_AMOUNT_REDEEMED, 0);

        suggestedTipAmount = intent.getIntExtra(Constant.INTENT_EXTRA_TIP_SUGGESTION, 0);
        tipProposition1 = intent.getIntExtra(Constant.INTENT_EXTRA_TIP_PROPOSITION_1, 0);
        tipProposition2 = intent.getIntExtra(Constant.INTENT_EXTRA_TIP_PROPOSITION_2, 0);
        tipAmount = suggestedTipAmount;
        totalAmount = invoiceModel.paidAmount + tipAmount;
        invoiceModel.tipAmount = tipAmount;

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtOriginalAmount = (TextView) findViewById(R.id.activity_tip_originalAmount);
        txtPaidAmount = (TextView) findViewById(R.id.activity_tip_paidAmount);
        txtTipProposition1 = (TextView) findViewById(R.id.activity_tip_amount_proposition1);
        txtTipProposition2 = (TextView) findViewById(R.id.activity_tip_amount_proposition2);
        editTipAmount = (EditText) findViewById(R.id.activity_tip_amount_edit);
        btnClear = (ImageButton) findViewById(R.id.activity_tip_btn_clear);
        btnProposition1 = (ImageButton) findViewById(R.id.activity_tip_btn_tip_proposition1);
        btnProposition2 = (ImageButton) findViewById(R.id.activity_tip_btn_tip_proposition2);
        btnPay = (DIMOButton) findViewById(R.id.activity_tip_btn_pay);
        loader = (ProgressBar) findViewById(R.id.activity_tip_loader);

        btnPay.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
        txtTitle.setText(getString(R.string.text_header_title_tip));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editTipAmount.addTextChangedListener(textWatcherTip);

        txtOriginalAmount.setText(DIMOUtils.formatAmount(Integer.toString(invoiceModel.paidAmount)));
        txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(totalAmount)));
        txtTipProposition1.setText("+ " + getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(tipProposition1)));
        txtTipProposition2.setText("+ " + getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(tipProposition2)));

        if(tipAmount < 0 || tipAmount > invoiceModel.paidAmount){
            totalAmount = invoiceModel.paidAmount;
            invoiceModel.tipAmount = 0;
        }

        if(checkAmount(tipAmount)){
            editTipAmount.setText("" + tipAmount);
        }else{
            tipAmount = 0;
            editTipAmount.setText("");
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipAmount = 0;
                editTipAmount.setText("");
            }
        });

        btnProposition1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tipAmountTemp = tipAmount + tipProposition1;
                if (checkAmount(tipAmountTemp)) {
                    editTipAmount.setText("" + tipAmountTemp);
                }
            }
        });

        btnProposition2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tipAmountTemp = tipAmount + tipProposition2;
                if (checkAmount(tipAmountTemp)) {
                    editTipAmount.setText("" + tipAmountTemp);
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callbackPayInvoice(invoiceModel);
                doPayment();
            }
        });

        if(checkPaymentThread!=null) {
            checkPaymentThread.stopPolling();
        }
    }

    TextWatcher textWatcherTip = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            String userInput = "" + s.toString().replaceAll("["+getString(R.string.text_detail_currency)+",.\\s]", "");
            StringBuilder cashAmountBuilder = new StringBuilder(userInput);
            if(cashAmountBuilder.length() == 0) cashAmountBuilder.append("0");

            if (checkAmount(Integer.parseInt(cashAmountBuilder.toString()))) {
                editTipAmount.removeTextChangedListener(this);

                editTipAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(cashAmountBuilder.toString()));
                Selection.setSelection(editTipAmount.getText(), editTipAmount.getText().length());
                txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(totalAmount)));

                editTipAmount.addTextChangedListener(this);
            }
        }
    };

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
                        new CheckPaymentStatusTask(TipActivity.this, invoiceModel.invoiceID, new Handler(){
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

                                    closeSDK(false, true, Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success), loyaltyModel);
                                }
                            }
                        }).execute();
                    } else {
                        closeSDK(false, true, code, desc, null);
                    }
                } else {
                    if (isDefaultLayout) {
                        if (code == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
                            new CheckPaymentStatusTask(TipActivity.this, invoiceModel.invoiceID, null).execute();
                        } else {
                            goToFailedScreen(getString(R.string.text_payment_failed), desc, Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
                        }
                    } else {
                        if (code == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
                            new CheckPaymentStatusTask(TipActivity.this, invoiceModel.invoiceID, new Handler(){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            if (data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, false)){
                closeSDK(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, false),
                        data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, true),
                        data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                        data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC),
                        (LoyaltyModel) data.getParcelableExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY));
            }else{
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

    @Override
    public void onBackPressed() {
        if(!isPaymentProcessRun) {
            finish();
            overridePendingTransition(R.anim.flip_from_middle, R.anim.flip_to_middle);
        }
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
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
        finish();
    }

    private void doPayment(){
        isPaymentProcessRun = true;
        btnPay.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        if (PayByQRProperties.isPolling()) {
            checkPaymentThread = new CheckPaymentThread(this, invoiceModel.invoiceID, endInvoicePayed);
            checkPaymentThread.start();
        }
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(TipActivity.this, FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(TipActivity.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
    }

    private boolean checkAmount(int tipAmount){
        if(tipAmount >= 0 && tipAmount <= invoiceModel.paidAmount){
            this.tipAmount = tipAmount;
            invoiceModel.tipAmount = tipAmount;
            totalAmount = invoiceModel.paidAmount + tipAmount;
            return true;
        }else{
            int amountTemp = invoiceModel.tipAmount;
            editTipAmount.setText("" + amountTemp);

            DIMOUtils.showAlertDialog(this, null, getString(R.string.text_info_tip_max, DIMOUtils.formatAmount(Integer.toString(invoiceModel.paidAmount))), getString(R.string.alertdialog_posBtn_ok), null, null, null);
            return false;
        }
    }

    private void refreshTipAmountView(){
        int amountTemp = invoiceModel.tipAmount;
        editTipAmount.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(amountTemp)));
        txtPaidAmount.setText(DIMOUtils.formatAmount(Integer.toString(totalAmount)));
        if(PayByQRProperties.isDebugMode()) Log.d("COLO", "Tip Amount: "+tipAmount);
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

                    closeSDK(false, true, Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success), loyaltyModel);
                }else {
                    Intent intentSuccess = new Intent(TipActivity.this, PaymentSuccessActivity.class);
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
                    closeSDK(false, true, Constant.ERROR_CODE_PAYMENT_FAILED, errorMessage, null);
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
                    closeSDK(false, true, Constant.ERROR_CODE_TIME_OUT, errorMessage, null);
                }else {
                    goToFailedScreen(getString(R.string.text_payment_timeout), errorMessage, Constant.REQUEST_CODE_ERROR_TIME_OUT);
                }
            }
        }

    };

    private Handler alternateDialogHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.MESSAGE_END_OK) {
                closeSDK(msg.getData().getBoolean(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true));
            }
        }
    };
}
