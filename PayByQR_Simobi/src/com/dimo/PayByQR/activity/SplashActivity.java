package com.dimo.PayByQR.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dimo.PayByQR.EULAFragmentListener;
import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRPreference;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.view.StoreMenuActivity;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.UserAPIKeyListener;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoginResponse;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends DIMOBaseActivity implements EULAFragmentListener{
    private PayByQRSDKListener listener;
    private Fragment eulaFragment;
    private int module;
    private String invoiceID, PayInAppURLCallback;

    private String FRAGMENT_EULA_TAG = "FRAGMENT_EULA_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView loadingImage = (ImageView) findViewById(R.id.activity_splash_loading);
        loadingImage.setBackgroundResource(R.drawable.loading_splash_animation);
        AnimationDrawable loadingAnimation = (AnimationDrawable) loadingImage.getBackground();
        loadingAnimation.start();

        listener = PayByQRSDK.getListener();
        PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);

        module = getIntent().getIntExtra(Constant.INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_PAYMENT);
        invoiceID = getIntent().getStringExtra(Constant.INTENT_EXTRA_INVOICE_ID);
        PayInAppURLCallback = getIntent().getStringExtra(Constant.INTENT_EXTRA_INAPP_URL_CALLBACK);

        //set splash view based on module
        if(module == PayByQRSDK.MODULE_IN_APP){
            ImageView logo = (ImageView) findViewById(R.id.activity_splash_logo);
            logo.setImageResource(R.drawable.logo_payinapp);

            LinearLayout footer = (LinearLayout) findViewById(R.id.activity_splash_footer);
            footer.setVisibility(View.GONE);
        }

        checkEULA();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            closeSDK();
        }else if(Constant.ACTIVITY_RESULT_NO_CONNECTION == resultCode){
            checkUserAPIKey();
        }

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
    protected void onResume() {
        PayByQRProperties.setSDKContext(this);
        super.onResume();
    }

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK();
        }
    };

    private void checkEULA(){
        //Check for EULA State
        //if true -> Check UserAPIKey
        //if false -> callbackS
        if(PayByQRPreference.readEULAStatePrefs(this)){
            if(module == PayByQRSDK.MODULE_PAYMENT)
                checkCameraPermission();
            else
                checkUserAPIKey();
        }else{
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            eulaFragment = listener.callbackShowEULA();
            if(null == eulaFragment){
                //show default EULA
                eulaFragment = DefaultEULAFragment.newInstance("http://www.dimo.co.id");
            }

            fragmentTransaction.add(R.id.fragment_eula_layout, eulaFragment, FRAGMENT_EULA_TAG);
            fragmentTransaction.setCustomAnimations(R.anim.pop_in_top, R.anim.pop_out_bottom);
            //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
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
        }, 1000);
    }

    UserAPIKeyListener mUserAPIKeyListener = new UserAPIKeyListener(){
        @Override
        public void setUserAPIKey(final String userAPIKey) {
            //PayByQRPreference.saveUserAPIKeyPref(SplashActivity.this, userAPIKey);
            PayByQRProperties.setUserAPIKey(userAPIKey);
            if(null != userAPIKey){
                doLoginTask();
            }else{
                if(PayByQRProperties.isUsingCustomDialog()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.callbackShowDialog(SplashActivity.this, Constant.ERROR_CODE_AUTHENTICATION, getString(R.string.error_authentication), null);
                        }
                    });
                }else {
                    goToFailedScreen(getString(R.string.text_not_authenticate_splash), getString(R.string.error_authentication), Constant.REQUEST_CODE_ERROR_AUTHENTICATION);
                }
            }
        }
    };

    private void doLoginTask(){
        new LoginTask().execute();
    }

    @Override
    public void setEULAState(boolean eulaState) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(eulaFragment);
        fragmentTransaction.commit();

        PayByQRPreference.saveEULAStatePref(this, eulaState);
        PayByQRProperties.setEULAState(eulaState);

        if(eulaState){
            if(module == PayByQRSDK.MODULE_PAYMENT)
                checkCameraPermission();
            else
                checkUserAPIKey();
        }else{
            closeSDK();
        }
    }

    private void closeSDK(){
        listener.callbackSDKClosed();
        finish();
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
            return DIMOService.doLogin(SplashActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                LoginResponse loginResponse = DIMOService.parseJSONLogin(SplashActivity.this, s);
                if(loginResponse.result.equals("success")){
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.LOGGED_IN);
                    if(module == PayByQRSDK.MODULE_PAYMENT)
                        checkCameraPermission();
                    else if(module == PayByQRSDK.MODULE_LOYALTY)
                        goToLoyaltyScreen();
                    else if(module == PayByQRSDK.MODULE_IN_APP)
                        goToInvoiceDetailScreen();
                    else if(module == PayByQRSDK.MODULE_QRSTORE)
                        goToQrStoreScreen();
                }else{
                    PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);
                    if(PayByQRProperties.isUsingCustomDialog()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.callbackShowDialog(SplashActivity.this, Constant.ERROR_CODE_AUTHENTICATION, getString(R.string.error_authentication), null);
                            }
                        });
                    }else {
                        goToFailedScreen(getString(R.string.text_not_authenticate_splash), getString(R.string.error_authentication), Constant.REQUEST_CODE_ERROR_AUTHENTICATION);
                    }
                }
            } catch (final PayByQRException e) {
                e.printStackTrace();
                PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE);
                if(PayByQRProperties.isUsingCustomDialog()){
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE_NO_CONNECTION);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.callbackShowDialog(SplashActivity.this, Constant.ERROR_CODE_CONNECTION, e.getErrorMessage() + " " + e.getErrorDetail(), null);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.callbackShowDialog(SplashActivity.this, Constant.ERROR_CODE_AUTHENTICATION, getString(R.string.error_authentication), null);
                            }
                        });
                    }
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        PayByQRProperties.setLoginState(PayByQRProperties.LoginState.NOT_AUTHENTICATE_NO_CONNECTION);
                        goToNoConnectionScreen();
                    } else {
                        goToFailedScreen(getString(R.string.text_not_authenticate_splash), getString(R.string.error_authentication), Constant.REQUEST_CODE_ERROR_AUTHENTICATION);
                    }
                }
            }
        }
    }

    private void checkCameraPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCamera != PackageManager.PERMISSION_GRANTED && permissionStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_REQUEST_ALL);
        } else if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSIONS_REQUEST_CAMERA);
        } else if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_REQUEST_STORAGE);
        } else{
            goToScanScreen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToScanScreen();
                } else {
                    DIMOUtils.showAlertDialog(this, getString(R.string.alertdialog_title_info), getString(R.string.alertdialog_message_permission_denied, getString(R.string.text_camera)),
                    getString(R.string.alertdialog_posBtn_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }, null, null);
                }
                break;
            }

            case Constant.PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToScanScreen();
                } else {
                    DIMOUtils.showAlertDialog(this, getString(R.string.alertdialog_title_info), getString(R.string.alertdialog_message_permission_denied, getString(R.string.text_storage)),
                            getString(R.string.alertdialog_posBtn_close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }, null, null);
                }
                break;
            }

            case Constant.PERMISSIONS_REQUEST_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    goToScanScreen();
                } else {
                    String permission = "";
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) permission = getString(R.string.text_camera_storage);
                    else if(grantResults[0] != PackageManager.PERMISSION_GRANTED) permission = getString(R.string.text_camera);
                    else if(grantResults[1] != PackageManager.PERMISSION_GRANTED) permission = getString(R.string.text_storage);

                    DIMOUtils.showAlertDialog(this, getString(R.string.alertdialog_title_info), getString(R.string.alertdialog_message_permission_denied, permission),
                            getString(R.string.alertdialog_posBtn_close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }, null, null);
                }
                break;
            }
        }
    }

    public void goToScanScreen(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, ScanQRActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }, 1000);
    }

    public void goToQrStoreScreen(){
        Intent intentLoyalty = new Intent(SplashActivity.this, StoreMenuActivity.class);
        startActivity(intentLoyalty);
        finish();
    }

    public void goToLoyaltyScreen() {
        Intent intentLoyalty = new Intent(SplashActivity.this, LoyaltyActivity.class);
        startActivity(intentLoyalty);
        finish();
    }

    private void goToInvoiceDetailScreen() {
        Intent intentInApp = new Intent(SplashActivity.this, InvoiceDetailActivity.class);
        intentInApp.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, invoiceID);
        if(null != PayInAppURLCallback) intentInApp.putExtra(Constant.INTENT_EXTRA_INAPP_URL_CALLBACK, PayInAppURLCallback);
        startActivity(intentInApp);
        finish();
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(SplashActivity.this, FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(SplashActivity.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

}
