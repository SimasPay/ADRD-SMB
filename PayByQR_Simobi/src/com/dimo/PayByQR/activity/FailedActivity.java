package com.dimo.PayByQR.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.view.DIMOButton;

public class FailedActivity extends DIMOBaseActivity {
    private PayByQRSDKListener listener;
    private ImageView icon;
    private TextView txtErrorHeader, txtErrorDetail;
    private DIMOButton btnOK;
    private RelativeLayout layoutErrorDetail;
    private String errorHeader, errorDetail;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed);

        listener = PayByQRSDK.getListener();

        icon = (ImageView) findViewById(R.id.activity_failed_icon);
        txtErrorHeader = (TextView) findViewById(R.id.activity_failed_text_header);
        txtErrorDetail = (TextView) findViewById(R.id.activity_failed_text_error_detail);
        layoutErrorDetail = (RelativeLayout) findViewById(R.id.activity_failed_error_detail_block);
        btnOK = (DIMOButton) findViewById(R.id.activity_failed_btn_OK);

        errorHeader = getIntent().getStringExtra(Constant.INTENT_EXTRA_ERROR_HEADER);
        errorDetail = getIntent().getStringExtra(Constant.INTENT_EXTRA_ERROR_DETAIL);

        if(null != errorHeader) txtErrorHeader.setText(errorHeader);
        if(null != errorDetail){
            txtErrorDetail.setText(errorDetail);
            layoutErrorDetail.setVisibility(View.VISIBLE);
        } else {
            layoutErrorDetail.setVisibility(View.GONE);
        }

        requestCode = getIntent().getIntExtra(Constant.INTENT_EXTRA_REQUEST_CODE, 0);

        //set Icon for every case
        if(Constant.REQUEST_CODE_ERROR_INVALID_QR == requestCode){
            icon.setImageResource(R.drawable.ic_question);
        }else if(Constant.REQUEST_CODE_ERROR_UNKNOWN == requestCode || Constant.REQUEST_CODE_ERROR_TIME_OUT == requestCode
                || Constant.REQUEST_CODE_ERROR_AUTHENTICATION == requestCode){
            icon.setImageResource(R.drawable.ic_exclamation);
        }else if(Constant.REQUEST_CODE_ERROR_MINIMUM_TRX == requestCode){
            icon.setImageResource(R.drawable.ic_failed);
        }

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnOKClick(requestCode);
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

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onBtnOKClick(requestCode);
        super.onBackPressed();
    }

    private void onBtnOKClick(int reqCode){
        if(0 != reqCode){

            if(Constant.REQUEST_CODE_ERROR_INVALID_QR == reqCode || Constant.ERROR_CODE_INVALID_QR == reqCode){
                closeSDK(listener.callbackInvalidQRCode());
            }else if(Constant.REQUEST_CODE_ERROR_CONNECTION == reqCode || Constant.ERROR_CODE_CONNECTION == reqCode){
                closeSDK(false);
            }else if(Constant.REQUEST_CODE_ERROR_UNKNOWN == reqCode || Constant.ERROR_CODE_UNKNOWN_ERROR == reqCode){
                boolean isClose = listener.callbackUnknowError();
                if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY) closeSDK(true);
                else closeSDK(isClose);
            }else if(Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED == reqCode || Constant.ERROR_CODE_PAYMENT_FAILED == reqCode){
                boolean isClose = listener.callbackTransactionStatus(Constant.ERROR_CODE_PAYMENT_FAILED, errorDetail);
                if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) closeSDK(true);
                else closeSDK(isClose);
            }else if(Constant.REQUEST_CODE_ERROR_TIME_OUT == reqCode || Constant.ERROR_CODE_TIME_OUT == reqCode){
                boolean isClose = listener.callbackTransactionStatus(Constant.ERROR_CODE_TIME_OUT, errorDetail);
                if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY) closeSDK(true);
                else closeSDK(isClose);
            }else if(Constant.REQUEST_CODE_ERROR_AUTHENTICATION == reqCode || Constant.ERROR_CODE_AUTHENTICATION == reqCode){
                listener.callbackAuthenticationError();
                closeSDK(true);
            }else if(Constant.REQUEST_CODE_ERROR_MINIMUM_TRX == reqCode){
                closeSDK(false);
            }else if(Constant.REQUEST_CODE_ERROR_QRSTORE == reqCode){
                closeSDK(false);
            }else{
                boolean isClose = listener.callbackUnknowError();
                if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY) closeSDK(true);
                else closeSDK(isClose);
            }
        }else{
            boolean isClose = listener.callbackUnknowError();
            if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY) closeSDK(true);
            else closeSDK(isClose);
        }
    }
}
