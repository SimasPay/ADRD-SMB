package com.dimo.PayByQR.QrStore.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.model.RespondJson;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.QrStore.utility.UtilDb;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.FailedActivity;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.activity.PaymentSuccessActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceModel;
import com.dimo.PayByQR.utils.CheckPaymentThread;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.HashMap;

/**
 * Created by dimo on 1/11/16.
 */
public class StoreKonfirmasiActivity  extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private InvoiceModel invoiceModel;
    private Thread checkPaymentThread;
    private SharedPreferences sharedPreferences;
    private CartData cartData;

    // Data
    //List cartList;
    LinearLayout listBasketContainer;

    // amounts
    int shippingFee = 0;
    int paidAmount = 0;

    DIMOButton btkonfimrasi;
    ImageView btnBack;
    TextView txtTitle;
    private ProgressBar loader;
    private boolean isPaymentProcessRun = false;
    // global var
    String MerchantCode, MerchantName;
    String strCheckout;
    HashMap<String, String> storeNameToId;
    DIMOTextView teShippingfee, teTotalBayar, tePickupMethode, teName, teEmail, teTelepon, teCity , teAddress, teStrCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_konfirmasi);

        listener = PayByQRSDK.getListener();

        MerchantCode = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID);
        MerchantName = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD);
        strCheckout = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_KONFIRMASI);
        storeNameToId = (HashMap<String, String>)getIntent().getSerializableExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_STORES);

        btkonfimrasi = (DIMOButton) findViewById(R.id.activity_store_btn_konfirmasi);
        loader = (ProgressBar) findViewById(R.id.activity_store_confirm_loader);
        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtTitle.setText(MerchantName);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listBasketContainer = (LinearLayout) findViewById(R.id.activity_store_cart_list);
        teShippingfee = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_fee);
        teTotalBayar = (DIMOTextView) findViewById(R.id.activity_store_confirm_total_paid);
        tePickupMethode = (DIMOTextView) findViewById(R.id.activity_store_confirm_pickup_method);
        teName = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_name);
        teEmail = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_email);
        teTelepon = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_phone);
        teCity = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_city);
        teAddress = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_addr);
        teStrCity = (DIMOTextView) findViewById(R.id.act_store_konfirmasi_city_str);

        loader.setVisibility(View.GONE);
        btkonfimrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqCheckout();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initActivity();

        if(checkPaymentThread!=null) {
            ((CheckPaymentThread) checkPaymentThread).stopPolling();
        }
    }

    private void initActivity() {
        cartData = QRStoreDBUtil.getCartsByMerchant(this, MerchantCode);

        //load cart list
        for(int i=0;i<cartData.carts.size();i++){
            View vi = LayoutInflater.from(this).inflate(R.layout.act_keranjang_each, null);

            DIMOTextView txQuantity = (DIMOTextView) vi.findViewById(R.id.act_item_quantity);
            DIMOTextView txName = (DIMOTextView) vi.findViewById(R.id.act_goods_name);
            DIMOTextView txTotal = (DIMOTextView) vi.findViewById(R.id.act_item_total);

            GoodsData goodsData = cartData.carts.get(i);

            txQuantity.setText(String.valueOf(goodsData.qtyInCart));
            txName.setText(goodsData.goodsName);
            txTotal.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString((int)(goodsData.price-goodsData.discountAmount) * goodsData.qtyInCart)));

            listBasketContainer.addView(vi);
        }

        String[] arFromCheckout = strCheckout.split(QrStoreDefine.LAZIES_PADD);
        for (int i=0;i<arFromCheckout.length;i++) {
            if (PayByQRProperties.isDebugMode())
                Log.d("init Konfirm", ""+i+"==>"+arFromCheckout[i] );
        }

        teName.setText(arFromCheckout[2]);
        teEmail.setText(arFromCheckout[3]);
        teTelepon.setText(arFromCheckout[4]);
        tePickupMethode.setText(arFromCheckout[1]);

        if( tePickupMethode.getText().toString().contains("TOKO")) {
            teStrCity.setText(getString(R.string.tx_store_pick_store));
            teCity.setText(arFromCheckout[7]);
            teAddress.setText(arFromCheckout[8]);
        } else {
            teStrCity.setText(getString(R.string.tx_store_pick_city));
            teCity.setText(arFromCheckout[5]);
            teAddress.setText(arFromCheckout[6]);
        }

        shippingFee = Integer.parseInt(arFromCheckout[0]);
        paidAmount = cartData.totalAmount + shippingFee;
        teShippingfee.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(shippingFee)));
        teTotalBayar.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(paidAmount)));
    }

    private InvoiceModel loadInvoiceModel(String invoiceId) {
        invoiceModel = new InvoiceModel();
        invoiceModel.invoiceID = invoiceId;
        invoiceModel.originalAmount = paidAmount;
        invoiceModel.paidAmount = paidAmount;
        invoiceModel.amountOfDiscount = 0;
        invoiceModel.numberOfCoupons = 0;
        invoiceModel.merchantName = cartData.merchantName;
        invoiceModel.discountType = "";
        invoiceModel.loyaltyProgramName = "";
        invoiceModel.tipAmount = 0;
        invoiceModel.pointsRedeemed = 0;
        invoiceModel.amountRedeemed = 0;
        return invoiceModel;
    }

    private void goToOriSdk(String invoiceId) {
        if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "invoiceId "+ invoiceId + " and Amount:"+ paidAmount);

        listener.callbackPayInvoice(loadInvoiceModel(invoiceId));
        doPayment();
    }

    private void reqCheckout() {
        String srtCity, pickupMethod, pickupStoreId;
        if (tePickupMethode.getText().toString().contains("ALAMAT")) {
            srtCity = teCity.getText().toString();
            pickupMethod = "ALAMAT";
            pickupStoreId = "";
        } else {
            srtCity = "";
            pickupMethod = "TOKO";
            pickupStoreId = storeNameToId.get(teCity.getText().toString());
        }

        String strJson = QrStoreUtil.getStringJson(cartData.carts, teName.getText().toString(), teEmail.getText().toString(),
                teAddress.getText().toString(), "", srtCity, teTelepon.getText().toString(), MerchantCode,
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_TRANS_ID, ""), paidAmount, pickupMethod, pickupStoreId);
        new GetStoreCheckout(cartData.merchantURL, strJson).execute();
    }

    private void reqPaymentPass(String status) {
        String strJson = QrStoreUtil.getStringPayment(invoiceModel.invoiceID, MerchantCode, sharedPreferences.getString(QrStoreDefine.SHARED_PREF_TRANS_ID, ""), status);
        new RequestPaymentResult(cartData.merchantURL, strJson).execute();
    }

    private class GetStoreCheckout extends AsyncTask<Void, Void, String> {
        String serverURL;
        String paramsheader;
        ProgressDialog progressDialog;

        public GetStoreCheckout (String serverURL, String param) {
            this.serverURL = serverURL;
            this.paramsheader = param;

            progressDialog = new ProgressDialog(StoreKonfirmasiActivity.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_login_waiting));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getQrGeneric(serverURL, paramsheader, QrStoreDefine.PASS_CHEKCOUT);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                String invoiceID = DIMOService.parseQrCheckout(StoreKonfirmasiActivity.this, s);
                goToOriSdk(invoiceID);
            } catch (PayByQRException e) {
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else if(e.getErrorCode() == Constant.ERROR_CODE_OUT_OF_STOCK) {
                        DIMOUtils.showAlertDialog(StoreKonfirmasiActivity.this, null, e.getErrorMessage(),
                                getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.putExtra(Constant.INTENT_EXTRA_QRSTORE_CUST_NAME, teName.getText().toString());
                                        intent.putExtra(Constant.INTENT_EXTRA_QRSTORE_CUST_EMAIL, teEmail.getText().toString());
                                        intent.putExtra(Constant.INTENT_EXTRA_QRSTORE_CUST_PHONE, teTelepon.getText().toString());
                                        setResult(Constant.ACTIVITY_RESULT_QRSTORE_CHECKOUT_ERROR, intent);
                                        finish();
                                    }
                                }, null, null);
                    } else {
                        DIMOUtils.showAlertDialog(StoreKonfirmasiActivity.this, null, e.getErrorMessage(), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                    }
                }
            }
        }
    }

    private class RequestPaymentResult extends AsyncTask<Void, Void, String> {
        String serverURL;
        String paramsheader;

        public RequestPaymentResult (String serverURL, String param) {
            this.serverURL = serverURL;
            this.paramsheader = param;
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getQrGeneric(serverURL, paramsheader, QrStoreDefine.PASS_PAID);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                String result = DIMOService.parseQrPayment(getApplicationContext(), s);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "RequestPaymentResult: "+result);
            }catch (PayByQRException e){
                e.printStackTrace();
                if (PayByQRProperties.isDebugMode()) Log.e("RHIO", "RequestPaymentResult ERROR: "+e.getErrorMessage());
            }
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
            int code = intent.getIntExtra(Constant.INTENT_EXTRA_NOTIFY_CODE, 0);
            String desc = intent.getStringExtra(Constant.INTENT_EXTRA_NOTIFY_DESC);
            if(PayByQRProperties.isUsingCustomDialog()){
                if(code == Constant.STATUS_CODE_PAYMENT_SUCCESS) reqPaymentPass("OK");
                else reqPaymentPass("NOK");
                closeSDK(false, true, code, desc);
            }else{
                if(code == Constant.STATUS_CODE_PAYMENT_SUCCESS){
                    reqPaymentPass("OK");
                    Intent intentSuccess = new Intent(StoreKonfirmasiActivity.this, PaymentSuccessActivity.class);
                    startActivityForResult(intentSuccess, 0);
                }else{
                    reqPaymentPass("NOK");
                    goToFailedScreen(getString(R.string.text_payment_failed), desc, Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkPaymentThread!=null) {
            ((CheckPaymentThread) checkPaymentThread).stopPolling();
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
                        data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC));
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
            overridePendingTransition(R.anim.fade_in, R.anim.out_to_bottom);
        }
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    private void closeSDK(boolean isCloseSDK, boolean isShowCustomDialog, int code, String desc){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        intent.putExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, isShowCustomDialog);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, code);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC, desc);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    private void doPayment(){
        isPaymentProcessRun = true;
        btkonfimrasi.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.VISIBLE);
        if (PayByQRProperties.isPolling()) {
            checkPaymentThread = new CheckPaymentThread(this, invoiceModel.invoiceID, endInvoicePayed);
            checkPaymentThread.start();
        }
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(getApplicationContext(), FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(getApplicationContext(), NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    @SuppressLint("HandlerLeak")
    private Handler endInvoicePayed = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isPaymentProcessRun = false;
            String transId = sharedPreferences.getString(QrStoreDefine.SHARED_PREF_TRANS_ID, "");
            QRStoreDBUtil.removeAllCartsByMerchant(StoreKonfirmasiActivity.this, cartData.merchantCode);
            if (msg.what == Constant.MESSAGE_END_OK) {
                checkPaymentThread = null;
                Log.d("Store konfirmasi ", "sukses" + transId);
                reqPaymentPass("OK");

                if(PayByQRProperties.isUsingCustomDialog()) {
                    closeSDK(false, true, Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success));
                }else {
                    Intent intentSuccess = new Intent(StoreKonfirmasiActivity.this, PaymentSuccessActivity.class);
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
                reqPaymentPass("NOK");
                String errorMessage = msg.getData().getString(Constant.INTENT_EXTRA_ERROR_DETAIL);
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, Constant.ERROR_CODE_PAYMENT_FAILED, errorMessage);
                }else {
                    if(errorMessage.contains(getString(R.string.error_connection_message))){
                        goToNoConnectionScreen();
                    }else {
                        goToFailedScreen(getString(R.string.text_payment_failed), errorMessage, Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
                    }
                }
            }else if(msg.what == Constant.MESSAGE_END_TIME_OUT){
                reqPaymentPass("NOK");
                String errorMessage = msg.getData().getString(Constant.INTENT_EXTRA_ERROR_DETAIL);
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, Constant.ERROR_CODE_TIME_OUT, errorMessage);
                }else {
                    goToFailedScreen(getString(R.string.text_payment_timeout), errorMessage, Constant.REQUEST_CODE_ERROR_TIME_OUT);
                }
            }else{
                reqPaymentPass("NOK");
                goToFailedScreen(getString(R.string.text_payment_failed), getString(R.string.error_unknown), Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED);
            }
        }

    };

}
