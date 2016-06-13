package com.dimo.PayByQR.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.view.StoreMenuActivity;
import com.dimo.PayByQR.QrStore.view.StoreMenuMerchant;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.UserAPIKeyListener;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoginResponse;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.utils.barcode.CameraManager;
import com.dimo.PayByQR.view.DIMOTextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScanQRActivity extends DIMOBaseActivity implements SurfaceHolder.Callback{
    private PayByQRSDKListener listener;
    private LinearLayout btnFlash;
    private ImageView btnClose, imgFlash, imgSuccessAddToCart, imgSuccessAddToCartAnimate;
    private TextView txtFlash;
    private boolean flashOn = false;
    private boolean isUserKeyProcess = false, isLoginProcess = false, isInitCameraProcess = false, isAnimationProcess = false;
    private Thread checkLoginStateThread;
    private ProgressDialog waitDialog, qrstoreDialog;

    private final String invoiceTagStartFlashiz = "flashiz.com";
    private final String invoiceTagStartDimo = "dimo.co.id";
    private final String invoiceTagPayment1 = "/fr/infos";
    private final String invoiceTagPayment2 = "/en/infos";
    private final String invoiceTagOffline = "/offline";

    private DIMOTextView txtCartQty, txtCartQtyAnimate;
    private RelativeLayout btnStore;
    private LinearLayout footerServiceBy, layoutAnimate;
    private SurfaceView cameraPreview;
    private CameraManager cameraManager;
    private ImageScanner mScanner;
    private boolean hasSurface;

    static {
        System.loadLibrary("iconv");
    }

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

        btnFlash = (LinearLayout) findViewById(R.id.activity_scanqr_btn_flash);
        imgFlash = (ImageView) findViewById(R.id.activity_scanqr_img_flash);
        txtFlash = (TextView) findViewById(R.id.activity_scanqr_txt_flash);
        btnClose = (ImageView) findViewById(R.id.activity_scanqr_btn_close);
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);

        txtCartQty = (DIMOTextView) findViewById(R.id.item_merchant_qty);
        txtCartQtyAnimate = (DIMOTextView) findViewById(R.id.item_merchant_qty_animate);
        btnStore = (RelativeLayout) findViewById(R.id.activity_scanqr_btn_store);
        footerServiceBy = (LinearLayout) findViewById(R.id.activity_scanqr_serviceby);
        imgSuccessAddToCart = (ImageView) findViewById(R.id.activity_scanqr_img_success_addtocart);
        imgSuccessAddToCartAnimate = (ImageView) findViewById(R.id.activity_scanqr_img_success_addtocart_animate);
        layoutAnimate = (LinearLayout) findViewById(R.id.activity_scanqr_store_animation_layout);
        layoutAnimate.setVisibility(View.GONE);

        // Initialize the camera instance
        cameraManager = new CameraManager(this, barcodeCallback);
        hasSurface = false;

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

        qrstoreDialog = new ProgressDialog(ScanQRActivity.this);
        qrstoreDialog.setMessage(getString(R.string.progressdialog_message_get_store_item_detail));
        qrstoreDialog.setCancelable(false);

        checkUserAPIKey();
        if(checkLoginStateThread!=null) {
            ((LoginWaitingThread) checkLoginStateThread).stopWaiting();
        }
    }

    private void initCamera(SurfaceView surfaceView, boolean oneTime) {
        if (surfaceView == null || surfaceView.getHolder() == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager.isOpen()) {
            return;
        }

        try {
            surfaceView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            cameraManager.openDriver(surfaceView);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            cameraManager.startPreview();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            e.printStackTrace();

            if(null != cameraManager) cameraManager.closeDriver();

            if (oneTime) {
                initCamera(surfaceView, false);
            } else {
                return;
            }
        }

        mScanner = new ImageScanner();
        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        mScanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        mScanner.setConfig(Symbol.NONE, Config.X_DENSITY, 3);
        mScanner.setConfig(Symbol.NONE, Config.Y_DENSITY, 3);

        surfaceView.setBackgroundColor(Color.TRANSPARENT);
        isInitCameraProcess = false;
    }

    private void cleanCameraInstance() {
        if (cameraManager.isOpen()) {
            cameraManager.stopPreview();
            cameraManager.closeDriver();
            if (!hasSurface) {
                SurfaceHolder surfaceHolder = cameraPreview.getHolder();
                surfaceHolder.removeCallback(this);
            }
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
                final ArrayList<CartData> cartMerchantDatas = QRStoreDBUtil.getAllMerchantCarts(ScanQRActivity.this, false);
                final ArrayList<GoodsData> cartDatas = QRStoreDBUtil.getAllCarts(ScanQRActivity.this);

                int totalCartSize = 0;
                for(int i=0;i<cartDatas.size();i++){
                    totalCartSize += cartDatas.get(i).qtyInCart;
                }

                if (totalCartSize == 0) {
                    btnStore.setVisibility(View.GONE);
                    footerServiceBy.setVisibility(View.VISIBLE);
                } else {
                    btnStore.setVisibility(View.VISIBLE);
                    footerServiceBy.setVisibility(View.GONE);
                    btnStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!isInitCameraProcess && !isAnimationProcess) {
                                if (cartMerchantDatas.size() > 1) {
                                    goToMerchant();
                                } else {
                                    Intent intent = new Intent(ScanQRActivity.this, StoreMenuActivity.class);
                                    intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, cartMerchantDatas.get(0).merchantCode);
                                    intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, cartMerchantDatas.get(0).merchantName);
                                    startActivityForResult(intent, 0);
                                }
                            }
                        }
                    });
                }
                txtCartQty.setText(String.valueOf(totalCartSize));
                txtCartQtyAnimate.setText(String.valueOf(totalCartSize));
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
        isInitCameraProcess = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SurfaceHolder surfaceHolder = cameraPreview.getHolder();
                        if (hasSurface) {
                            // The activity was paused but not stopped, so the surface still exists.
                            // Therefore surfaceCreated() won't be called, so init the camera here.
                            initCamera(cameraPreview, true);
                        } else {
                            // Install the callback and wait for surfaceCreated() to init the camera.
                            surfaceHolder.addCallback(ScanQRActivity.this);
                            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                            initCamera(cameraPreview, true);
                        }
                        checkMerchant();
                    }
                });
            }
        }, 500);

    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            cameraManager.stopPreview();
            cameraManager.closeDriver();

            if (!hasSurface) {
                SurfaceHolder surfaceHolder = cameraPreview.getHolder();
                surfaceHolder.removeCallback(this);
            }
        } catch (RuntimeException e) {
            // Can be already released
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cleanCameraInstance();
        if(checkLoginStateThread!=null) {
            ((LoginWaitingThread) checkLoginStateThread).stopWaiting();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    private Camera.PreviewCallback barcodeCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            if (mScanner == null) {
                return;
            }

            int result = mScanner.scanImage(barcode);

            if (result != 0) {
                onPause();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);

                String barcodeText = "";
                SymbolSet symbols = mScanner.getResults();
                for (Symbol sym : symbols) {
                    barcodeText = sym.getData();
                }
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
        }
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
                            data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC),
                            (LoyaltyModel) data.getParcelableExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY));
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
            cameraManager.setTorch(true);
            txtFlash.setText(getString(R.string.button_flash_on));
            txtFlash.setTextColor(ContextCompat.getColor(ScanQRActivity.this, R.color.txt_scanQR_flash_on));
            imgFlash.setImageResource(R.drawable.ico_flash_on);
        } else {
            cameraManager.setTorch(false);
            txtFlash.setText(getString(R.string.button_flash_off));
            txtFlash.setTextColor(ContextCompat.getColor(ScanQRActivity.this, R.color.theme_text_over_basic_bg));
            imgFlash.setImageResource(R.drawable.ico_flash);
        }
        flashOn = !flashOn;
    }

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

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
                        if(null != invoiceId) {
                            if(isQRStore){
                                //intent = new Intent(ScanQRActivity.this, StoreDetailActivity.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new GetGoodsDetail(invoiceId).execute();
                                    }
                                });
                            }else{
                                Intent intent;
                                if (isOfflineQR)
                                    intent = new Intent(ScanQRActivity.this, OfflineQRActivity.class);
                                else
                                    intent = new Intent(ScanQRActivity.this, InvoiceDetailActivity.class);

                                intent.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, invoiceId);
                                startActivityForResult(intent, 0);
                                overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
                            }
                        }else{
                            Intent intent = new Intent(ScanQRActivity.this, FailedActivity.class);
                            intent.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, getString(R.string.error_invalid_qr_title));
                            intent.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, getString(R.string.error_invalid_qr_detail_notFound));
                            startActivityForResult(intent, Constant.REQUEST_CODE_ERROR_INVALID_QR);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
                        }
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
                                    listener.callbackShowDialog(ScanQRActivity.this, Constant.ERROR_CODE_CONNECTION, getString(R.string.error_connection_message)+" "+getString(R.string.error_connection_detail), null);
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onResume();
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
                    listener.callbackShowDialog(ScanQRActivity.this, Constant.ERROR_CODE_AUTHENTICATION, getString(R.string.error_authentication), null);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onResume();
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

    private class GetGoodsDetail extends AsyncTask<Void, Void, String> {
        String URL;
        GoodsData goodsData;

        public GetGoodsDetail(String URL){
            this.URL = URL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            qrstoreDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getCartDetail(URL);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != qrstoreDialog) qrstoreDialog.dismiss();
            try {
                goodsData = DIMOService.parseJSONGoodsDetail(ScanQRActivity.this, s);

                GoodsData goodsInCart = QRStoreDBUtil.getGoodsFromCart(ScanQRActivity.this, goodsData.id, goodsData.merchantCode);
                if(null != goodsInCart){
                    goodsData.qtyInCart = goodsInCart.qtyInCart;
                    goodsData.merchantURL = getMerchantURL(URL);
                }else{
                    goodsData.qtyInCart = 0;
                    goodsData.merchantURL = getMerchantURL(URL);
                }
                goodsData.printLogData();

                //handle no stock
                if(goodsData.stock <= 0){
                    DIMOUtils.showAlertDialog(ScanQRActivity.this, null, getString(R.string.error_no_stock), getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onResume();
                                }
                            }, null, null);
                } else {
                    onItemQtyChange();
                }
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    listener.callbackShowDialog(ScanQRActivity.this, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else if (e.getErrorCode() == Constant.ERROR_CODE_INVALID_QR) {
                        goToFailedScreen(getString(R.string.error_invalid_qr_title), e.getErrorMessage(), Constant.REQUEST_CODE_ERROR_INVALID_QR);
                    } else if (e.getErrorCode() == Constant.ERROR_CODE_INVALID_GOODS) {
                        DIMOUtils.showAlertDialog(ScanQRActivity.this, null, getString(R.string.error_item_not_valid),
                                getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onResume();
                                    }
                                }, null, null);
                    } else {
                        goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                    }
                }
            }
        }

        private void goToFailedScreen(String title, String errorDetail, int requestCode){
            Intent intentFailed = new Intent(ScanQRActivity.this, FailedActivity.class);
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
            startActivityForResult(intentFailed, requestCode);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        }

        private void goToNoConnectionScreen(){
            Intent intentFailed = new Intent(ScanQRActivity.this, NoConnectionActivity.class);
            startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        }

        private String getMerchantURL(String goodsURL){
            return goodsURL.substring(0, goodsURL.indexOf("qrstore")) + "qrstore/api/v2/";
        }

        public void onItemQtyChange() {
            int maxQtyToAdd;
            if(goodsData.maxQuantity == 0){
                maxQtyToAdd = goodsData.stock;
                if (goodsData.qtyInCart < maxQtyToAdd) {
                    goodsData.qtyInCart++;

                    QRStoreDBUtil.addGoodsToCart(ScanQRActivity.this, goodsData);
                    animateAddToCart();
                } else {
                    String errorMsg = getString(R.string.error_max_stock, goodsData.stock);
                    if(goodsData.qtyInCart > 0)
                        errorMsg = errorMsg + getString(R.string.error_max_goods_in_cart, goodsData.qtyInCart);

                    DIMOUtils.showAlertDialog(ScanQRActivity.this, null, errorMsg, getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onResume();
                                }
                            }, null, null);
                }
            }else {
                maxQtyToAdd = Math.min(goodsData.stock, goodsData.maxQuantity);
                if (goodsData.qtyInCart < maxQtyToAdd) {
                    goodsData.qtyInCart++;

                    QRStoreDBUtil.addGoodsToCart(ScanQRActivity.this, goodsData);
                    animateAddToCart();
                } else {
                    String errorMsg = "";
                    if(goodsData.stock < goodsData.maxQuantity){
                        errorMsg = getString(R.string.error_max_stock, goodsData.stock);
                    }else{
                        errorMsg = getString(R.string.error_max_qty, goodsData.maxQuantity);
                    }

                    if(goodsData.qtyInCart > 0)
                        errorMsg = errorMsg + getString(R.string.error_max_goods_in_cart, goodsData.qtyInCart);

                    DIMOUtils.showAlertDialog(ScanQRActivity.this, null, errorMsg, getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onResume();
                                }
                            }, null, null);
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void animateAddToCart(){
            isAnimationProcess = true;
            btnStore.setVisibility(View.VISIBLE);
            layoutAnimate.setVisibility(View.VISIBLE);

            Animation zoomAnim = AnimationUtils.loadAnimation(ScanQRActivity.this, R.anim.success_add_to_cart);
            zoomAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    final Rect startBounds = new Rect();
                    final Rect finalBounds = new Rect();

                    imgSuccessAddToCart.getGlobalVisibleRect(startBounds);
                    txtCartQtyAnimate.getGlobalVisibleRect(finalBounds);

                    int moveXto = -((startBounds.left + (startBounds.width()/2)) - (finalBounds.left + (finalBounds.width()/2)));
                    int moveYto = -((startBounds.top + (startBounds.height()/2)) - (finalBounds.top + (finalBounds.height()/2)));

                    if(PayByQRProperties.isDebugMode()){
                        Log.d("RHIO", "moveXTo: "+moveXto);
                        Log.d("RHIO", "moveYTo: "+moveYto);
                    }

                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator.ofFloat(imgSuccessAddToCartAnimate, View.TRANSLATION_X, 0, moveXto))
                       .with(ObjectAnimator.ofFloat(imgSuccessAddToCartAnimate, View.TRANSLATION_Y, 0, moveYto))
                       .with(ObjectAnimator.ofFloat(imgSuccessAddToCartAnimate, View.SCALE_X, 1f, 0.15f))
                       .with(ObjectAnimator.ofFloat(imgSuccessAddToCartAnimate, View.SCALE_Y, 1f, 0.15f));
                    set.setDuration(800);
                    set.setInterpolator(new AccelerateDecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            imgSuccessAddToCart.setVisibility(View.GONE);
                            imgSuccessAddToCartAnimate.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            imgSuccessAddToCart.setVisibility(View.GONE);
                            imgSuccessAddToCartAnimate.setVisibility(View.GONE);
                            layoutAnimate.setVisibility(View.GONE);
                            onResume();
                            checkMerchant();
                            isAnimationProcess = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }
                    });
                    set.start();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            imgSuccessAddToCart.setVisibility(View.VISIBLE);
            imgSuccessAddToCart.clearAnimation();
            imgSuccessAddToCart.startAnimation(zoomAnim);
        }
    }
}
