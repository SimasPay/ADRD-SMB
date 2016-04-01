package com.dimo.PayByQR.QrStore.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.ScanQRActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.ProgressBarAnimation;

/**
 * Created by san on 1/23/16.
 */
public class StoreSuccess extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private DIMOButton btnOK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        listener = PayByQRSDK.getListener();
        btnOK = (DIMOButton) findViewById(R.id.activity_success_btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                gotoScan();

            }
        });


    }

    private void gotoScan()
    {
        // onBackPressed();
        Intent intent = new Intent(StoreSuccess.this,ScanQRActivity.class);
        startActivityForResult(intent,0);
        finish();
    }

    private Animation.AnimationListener progressbarAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

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

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK(true);
        }
    };

    @Override
    public void onBackPressed() {
        boolean isClose = listener.callbackTransactionStatus(Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success));
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) closeSDK(true);
        else closeSDK(isClose);
        super.onBackPressed();
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
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) listener.callbackSDKClosed();
        finish();
    }


}
