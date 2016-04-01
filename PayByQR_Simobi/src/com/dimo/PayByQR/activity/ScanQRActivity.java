package com.dimo.PayByQR.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.Merchant;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.UtilDb;
import com.dimo.PayByQR.QrStore.view.StoreDetailActivity;
import com.dimo.PayByQR.QrStore.view.StoreMenuActivity;
import com.dimo.PayByQR.QrStore.view.StoreMenuMerchant;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.UserAPIKeyListener;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceStatusResponse;
import com.dimo.PayByQR.model.LoginResponse;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOTextView;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanQRActivity extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private CompoundBarcodeView barcodeView;
    private CaptureManager captureManager;
    private LinearLayout btnFlash;
    private ImageView btnClose, imgFlash;
    private TextView txtFlash;
    private boolean flashOn = false;
    private boolean isUserKeyProcess = false, isLoginProcess = false;
    private Thread checkLoginStateThread;
    private ProgressDialog waitDialog;

    private final String invoiceTagStartFlashiz = "flashiz.com";
    private final String invoiceTagStartDimo = "dimo.co.id";
    private final String invoiceTagPayment1 = "/fr/infos";
    private final String invoiceTagPayment2 = "/en/infos";
    private final String invoiceTagOffline = "/offline";

    private DIMOTextView btMerchant;
    private LinearLayout btnStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            
            View statusBar = findViewById(R.id.activity_scanqr_systemBar);
            statusBar.getLayoutParams().height = getStatusBarHeight();
            statusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_action_scanQR_transparent));
        }*/

        listener = PayByQRSDK.getListener();

        barcodeView = (CompoundBarcodeView) findViewById(R.id.activity_scanqr_barcodeView);
        btnFlash = (LinearLayout) findViewById(R.id.activity_scanqr_btn_flash);
        imgFlash = (ImageView) findViewById(R.id.activity_scanqr_img_flash);
        txtFlash = (TextView) findViewById(R.id.activity_scanqr_txt_flash);
        btnClose = (ImageView) findViewById(R.id.activity_scanqr_btn_close);

        btMerchant = (DIMOTextView) findViewById(R.id.item_merchant_qty);
        btnStore = (LinearLayout) findViewById(R.id.activity_scanqr_btn_store);

        barcodeView.getViewFinder().setVisibility(View.GONE);
        barcodeView.getStatusView().setVisibility(View.GONE);
        barcodeView.setTorchListener(torchListener);

        captureManager = new CaptureManager(this, barcodeView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);

        if (hasFlash()) {
            btnFlash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchFlashlight();
                }
            });
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSDK();
            }
        });

        waitDialog = new ProgressDialog(ScanQRActivity.this);
        waitDialog.setMessage(getString(R.string.progressdialog_message_login_waiting));
        waitDialog.setCancelable(false);
        checkUserAPIKey();
        if(checkLoginStateThread!=null) {
            ((LoginWaitingThread) checkLoginStateThread).stopWaiting();
        }
    }


    private void goToMerchant() {
        Intent intent = new Intent(ScanQRActivity.this, StoreMenuMerchant.class);
        startActivityForResult(intent, 0);
    }

    private void checkMerchant() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: 3/15/16 Change to getAllCart() for showing total item in Cart instead of total Merchant in Cart
                final ArrayList<CartData> cartDatas = QRStoreDBUtil.getAllMerchantCarts(ScanQRActivity.this, false);

                if (cartDatas.size() == 0)
                    btnStore.setVisibility(View.GONE);
                else {
                    btnStore.setVisibility(View.VISIBLE);
                    btMerchant.setText(String.valueOf(cartDatas.size()));
                    btnStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(cartDatas.size() > 1){
                                goToMerchant();
                            }else{
                                Intent intent = new Intent(ScanQRActivity.this, StoreMenuActivity.class);
                                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, cartDatas.get(0).merchantCode);
                                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, cartDatas.get(0).merchantName);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(closeSDKBroadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION_CLOSE_SDK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeSDKBroadcastReceiver);
    }

    @Override
    protected void onResume(){
        super.onResume();
        PayByQRProperties.setSDKContext(this);
        captureManager.onResume();
        barcodeView.setTorchOff();
        barcodeView.decodeSingle(barcodeCallback);
        checkMerchant();
    }

    @Override
    protected void onPause(){
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        captureManager.onDestroy();
        if(checkLoginStateThread!=null) {
            ((LoginWaitingThread) checkLoginStateThread).stopWaiting();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult barcodeResult) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);

            String barcodeText = barcodeResult.getText();
            if(PayByQRProperties.isDebugMode()) Log.d("COLO", "BarcodeResult: "+barcodeText);

            boolean isOfflineQR = false, isQRStore = false;
            String invoiceID = null;
            if(barcodeText.contains(invoiceTagStartFlashiz) || barcodeText.contains(invoiceTagStartDimo) || barcodeText.contains(QrStoreDefine.QRSTORE_STRING)){
                if(barcodeText.contains(invoiceTagPayment1) || barcodeText.contains(invoiceTagPayment2) || barcodeText.contains(invoiceTagOffline) || barcodeText.contains(QrStoreDefine.QRSTORE_STRING)){
                    //check if is Offline QR
                    if(barcodeText.contains(invoiceTagOffline)) isOfflineQR = true;
                    if(PayByQRProperties.isDebugMode()) Log.d("COLO", "isOfflineQR ? "+isOfflineQR);

                    //check if is QR Store
                    if (barcodeText.contains(QrStoreDefine.QRSTORE_STRING)) isQRStore = true;
                    if(PayByQRProperties.isDebugMode()) Log.d("COLO", "isQRStore ? "+isQRStore);

                    String[] splits = barcodeText.split("/");
                    invoiceID = splits[splits.length-1];
                    if (isQRStore) invoiceID = barcodeText;
                    if(PayByQRProperties.isDebugMode()) Log.d("COLO", "InvoiceID: "+invoiceID);
                }
            }

            waitDialog.show();
            checkLoginStateThread = new LoginWaitingThread(invoiceID, isOfflineQR, isQRStore);
            checkLoginStateThread.start();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> list) {}
    };

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK();
        }
    };

    @Override
    public void onBackPressed() {
        closeSDK();
    }

    private void closeSDK(){
        listener.callbackSDKClosed();
        finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.scale_down_to_center);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            if(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true)) {
                closeSDK();
            }else{
                if (data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, false)){
                    listener.callbackShowDialog(ScanQRActivity.this, data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                            data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC));
                }
            }
        }else if(Constant.ACTIVITY_RESULT_NO_CONNECTION == resultCode){
            checkUserAPIKey();
            if(checkLoginStateThread!=null) {
                ((LoginWaitingThread) checkLoginStateThread).stopWaiting();
            }
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        if(PayByQRProperties.isDebugMode()) Log.d("COLO", "Status Bar Height: "+result);
        return result;
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void switchFlashlight() {
        if (!flashOn) {
            barcodeView.setTorchOn();
        } else {
            barcodeView.setTorchOff();
        }
        flashOn = !flashOn;
    }

    private CompoundBarcodeView.TorchListener torchListener = new CompoundBarcodeView.TorchListener() {

        @Override
        public void onTorchOn() {
            txtFlash.setText(getString(R.string.button_flash_on));
            txtFlash.setTextColor(ContextCompat.getColor(ScanQRActivity.this, R.color.txt_scanQR_flash_on));
            imgFlash.setImageResource(R.drawable.ico_flash_on);
        }

        @Override
        public void onTorchOff() {
            txtFlash.setText(getString(R.string.button_flash_off));
            txtFlash.setTextColor(ContextCompat.getColor(ScanQRActivity.this, R.color.theme_text_over_basic_bg));
            imgFlash.setImageResource(R.drawable.ico_flash);
        }
    };

    private void checkUserAPIKey(){
        PayByQRProperties.setLoginState(PayByQRProperties.LoginState.GETTING_USER_KEY);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.callbackGenerateUserAPIKey(mUserAPIKeyListener);
                    }
                });
            }
        }, 500);
    }

    UserAPIKeyListener mUserAPIKeyListener = new UserAPIKeyListener(){
        @Override
        public void setUserAPIKey(final String userAPIKey) {
            PayByQRProperties.setUserAPIKey(userAPIKey);
            if(null != userAPIKey){
                new LoginTask().execute();
            }else{
                PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);
            }
            isUserKeyProcess = true;
        }
    };

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            PayByQRProperties.setLoginState(PayByQRProperties.LoginState.LOGGING_IN);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return DIMOService.doLogin(ScanQRActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                LoginResponse loginResponse = DIMOService.parseJSONLogin(ScanQRActivity.this, s);
                if(loginResponse.result.equals("success")){
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.LOGGED_IN);
                }else{
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);
                }
            } catch (PayByQRException e) {
                if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE_NO_CONNECTION);
                } else {
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);
                }
            }
            isLoginProcess = true;

        }
    }

    private class LoginWaitingThread extends Thread {
        private boolean run = true;
        private static final int DELAY = 1000; // 1 s
        private int counter = 0;
        private int maxCounter = 30;

        private String invoiceId;
        private boolean isOfflineQR, isQRStore;

        public LoginWaitingThread(String invoiceId, boolean isOfflineQR, boolean isQRStore) {
            this.invoiceId = invoiceId;
            this.isOfflineQR = isOfflineQR;
            this.isQRStore = isQRStore;
        }

        @Override
        public void run() {
            while (run) {
                if(PayByQRProperties.isDebugMode()) Log.d("COLO", "Login State: "+ PayByQRProperties.getLoginState().name());
                if(counter < maxCounter) {
                    if(PayByQRProperties.getLoginState() == PayByQRProperties.LoginState.LOGGED_IN) {
                        if(null != waitDialog) waitDialog.dismiss();
                        run = false;
                        checkLoginStateThread = null;
                        Intent intent;
                        if(null != invoiceId) {
                            if (isOfflineQR)
                                intent = new Intent(ScanQRActivity.this, OfflineQRActivity.class);
                            else if(isQRStore)
                                intent = new Intent(ScanQRActivity.this, StoreDetailActivity.class);
                            else
                                intent = new Intent(ScanQRActivity.this, InvoiceDetailActivity.class);
                            intent.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, invoiceId);
                            startActivityForResult(intent, 0);
                        }else{
                            intent = new Intent(ScanQRActivity.this, FailedActivity.class);
                            intent.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, getString(R.string.error_invalid_qr_title));
                            intent.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, getString(R.string.error_invalid_qr_detail_notFound));
                            startActivityForResult(intent, Constant.REQUEST_CODE_ERROR_INVALID_QR);
                        }
                        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
                    }else if(PayByQRProperties.getLoginState() == PayByQRProperties.LoginState.NOT_AUTHENTICATE) {
                        if(null != waitDialog) waitDialog.dismiss();
                        run = false;
                        goToFailedScreen();
                    }else if(PayByQRProperties.getLoginState() == PayByQRProperties.LoginState.NOT_AUTHENTICATE_NO_CONNECTION) {
                        if(null != waitDialog) waitDialog.dismiss();
                        run = false;
                        if(PayByQRProperties.isUsingCustomDialog()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.callbackShowDialog(ScanQRActivity.this, Constant.ERROR_CODE_CONNECTION, getString(R.string.error_connection_message)+" "+getString(R.string.error_connection_detail));
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    barcodeView.decodeSingle(barcodeCallback);
                                                }
                                            });
                                        }
                                    }, 5000);
                                }
                            });
                        }else {
                            Intent intentFailed = new Intent(ScanQRActivity.this, NoConnectionActivity.class);
                            startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
                        }
                    }else{
                        try {
                            Thread.sleep(DELAY);
                        } catch (InterruptedException e) {
                            if(null != waitDialog) waitDialog.dismiss();
                            run = false;
                        }
                    }
                    counter++;
                }else{
                    if(null != waitDialog) waitDialog.dismiss();
                    run = false;
                    goToFailedScreen();
                }
            }
        }

        public synchronized void stopWaiting(){
            if(null != waitDialog) waitDialog.dismiss();
            this.run = false;
        }
    }

    private void goToFailedScreen(){
        checkLoginStateThread = null;
        if(PayByQRProperties.isUsingCustomDialog()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.callbackShowDialog(ScanQRActivity.this, Constant.ERROR_CODE_AUTHENTICATION, getString(R.string.error_authentication));
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    barcodeView.decodeSingle(barcodeCallback);
                                }
                            });
                        }
                    }, 5000);
                }
            });
        }else {
            Intent intentFailed = new Intent(ScanQRActivity.this, FailedActivity.class);
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, getString(R.string.text_not_authenticate));
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, getString(R.string.error_authentication));
            intentFailed.putExtra(Constant.INTENT_EXTRA_REQUEST_CODE, Constant.REQUEST_CODE_ERROR_AUTHENTICATION);
            startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_AUTHENTICATION);
        }
    }
}
