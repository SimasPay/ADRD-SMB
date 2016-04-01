package com.dimo.PayByQR.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOTextView;
import com.dimo.PayByQR.view.ProgressBarAnimation;

public class PaymentSuccessActivity extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private DIMOButton btnOK, btnLoyalty;
    private TextView txtFidelitizinfo1, txtFidelitizinfo2, txtFidelitizinfo3;
    private DIMOTextView txtPointBalance, txtPointMax, txtPointZero, txtVoucherGenerated, txtVoucherAmount;
    private ProgressBar progressBar;
    private View progressLine;
    private LinearLayout fidelitizBlock;
    private RelativeLayout progressBlock, voucherBoxBlock;
    private ProgressBarAnimation mProgressAnimation;
    private int animFrom = 0, animTo = 0, animTemp = 0, numFrom = 0, numTo = 0, numTemp = 0;
    private int MAX_PROGRESS = 1000;
    private int couponsGenerated = 0;
    private boolean voucherAnimateState = false;

   // private boolean isQrStore=false;
   // private String strQrStore="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        listener = PayByQRSDK.getListener();

        fidelitizBlock = (LinearLayout) findViewById(R.id.activity_success_loyalty_block);
        if(getIntent().getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO, false) && PayByQRProperties.isUsingLoyalty()){
            String rewardType="", strCouponValue="";
            int pointsBalance, pointsGenerated, pointAmountForCoupon, couponsBalance, couponValue, pointToGo;

            final String rawJSONSuccess = getIntent().getStringExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS);
            rewardType = getIntent().getStringExtra(Constant.INTENT_EXTRA_FIDELITIZ_TYPE);
            pointsBalance = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_BALANCE, -1);
            pointsGenerated = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_GENERATED, -1);
            pointAmountForCoupon = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON, -1);
            couponsBalance = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE, -1);
            couponsGenerated = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED, -1);
            couponValue = getIntent().getIntExtra(Constant.INTENT_EXTRA_FIDELITIZ_COUPON_VALUE, -1);

           /*  add here to qr Store */
        //    strQrStore=getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_KONFIRMASI_CHECKOUT);

         //   if (!QrStoreUtil.isNullContent(strQrStore) && strQrStore!=null)
          //         isQrStore=true;




            if(pointsGenerated > 0) {
                txtFidelitizinfo1 = (TextView) findViewById(R.id.activity_success_text_fidelitiz1);
                txtFidelitizinfo2 = (TextView) findViewById(R.id.activity_success_text_fidelitiz2);
                txtFidelitizinfo3 = (TextView) findViewById(R.id.activity_success_text_fidelitiz3);
                txtPointZero = (DIMOTextView) findViewById(R.id.activity_success_fidelitiz_point_0);
                txtPointBalance = (DIMOTextView) findViewById(R.id.activity_success_fidelitiz_point_balance);
                txtPointMax = (DIMOTextView) findViewById(R.id.activity_success_fidelitiz_point_max);
                txtVoucherGenerated = (DIMOTextView) findViewById(R.id.activity_success_voucher_generated);
                txtVoucherAmount = (DIMOTextView) findViewById(R.id.activity_success_voucher_amount);
                progressBar = (ProgressBar) findViewById(R.id.activity_success_fidelitiz_progress);
                btnLoyalty = (DIMOButton) findViewById(R.id.activity_success_btn_loyalty);
                progressBlock = (RelativeLayout) findViewById(R.id.activity_success_fidelitiz_progress_block);
                voucherBoxBlock = (RelativeLayout) findViewById(R.id.activity_success_voucher_block);
                progressLine = findViewById(R.id.activity_success_fidelitiz_progress_line);

                if (rewardType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH))
                    strCouponValue = "Rp " + DIMOUtils.formatAmount(Integer.toString(couponValue)) + ",-";
                else
                    strCouponValue = couponValue + "%";

                txtVoucherAmount.setText(strCouponValue);

                pointToGo = pointAmountForCoupon - pointsBalance;

                String poinInfo1 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointsGenerated)));
                Spannable info1 = new SpannableString(getString(R.string.text_info_fidelitiz1) + " " + poinInfo1);
                info1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.theme_text_over_basic_bg)), 0, info1.toString().indexOf(poinInfo1)-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                info1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), info1.toString().indexOf(poinInfo1), info1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtFidelitizinfo1.setText(info1);

                String poinInfo2 = "";
                int progress = 0;
                progressBar.setMax(MAX_PROGRESS);
                if (couponsGenerated == 0) {
                    poinInfo2 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointsBalance)));

                    String poinInfo3 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointToGo)));
                    String voucherInfo3 = getString(R.string.text_voucher_balance, "", strCouponValue);
                    Spannable info3 = new SpannableString(poinInfo3 + " " + getString(R.string.text_info_fidelitiz3));
                    info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), 0, poinInfo3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.theme_text_over_basic_bg)), poinInfo3.length()+1, info3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), info3.toString().indexOf(voucherInfo3), info3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txtFidelitizinfo3.setText(info3);

                    float tempFrom = (float) (pointsBalance - pointsGenerated) / (float) pointAmountForCoupon;
                    animFrom = (int) (tempFrom * MAX_PROGRESS);
                    float tempTo = (float) pointsBalance / (float) pointAmountForCoupon;
                    animTo = (int) (tempTo * MAX_PROGRESS);
                    animTemp = 0;
                    progressBar.setProgress(animFrom);
                    numFrom = pointsBalance - pointsGenerated;
                    numTo = pointsBalance;
                    numTemp = 0;

                    /*progressBar.setProgress(pointsBalance - pointsGenerated);
                    animFrom = pointsBalance - pointsGenerated;
                    animTo = pointsBalance;
                    animTemp = 0;*/

                    txtVoucherGenerated.setVisibility(View.GONE);
                } else {
                    int totalPointBalance = pointsBalance + (couponsGenerated * pointAmountForCoupon);
                    poinInfo2 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(totalPointBalance)));

                    /*String poinInfo3 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(couponsGenerated * pointAmountForCoupon)));
                    String voucherInfo3 = getString(R.string.text_voucher_balance, Integer.toString(couponsGenerated)+"x ", strCouponValue);
                    Spannable info3 = new SpannableString(poinInfo3 + " " + getString(R.string.text_info_fidelitiz3_get_voucher) + " " + voucherInfo3);
                    info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), 0, poinInfo3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.theme_text_over_basic_bg)), poinInfo3.length()+1, info3.toString().indexOf(voucherInfo3)-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), info3.toString().indexOf(voucherInfo3), info3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    */
                    txtFidelitizinfo3.setText(getString(R.string.text_info_fidelitiz3_get_voucher));
                    txtVoucherGenerated.setText("x"+couponsGenerated);
                    txtVoucherGenerated.setVisibility(View.GONE);

                    if (pointsBalance == 0) {
                        //reach max progress and get x coupons
                        progress = MAX_PROGRESS;
                        float tempFrom = (float) (pointAmountForCoupon - pointsGenerated) / (float) pointAmountForCoupon;
                        animFrom = (int) (tempFrom * MAX_PROGRESS);
                        animTo = MAX_PROGRESS;
                        animTemp = 0;
                        progressBar.setProgress(animFrom);
                        numFrom = pointAmountForCoupon - pointsGenerated;
                        numTo = pointAmountForCoupon;
                        numTemp = 0;

                        /*progressBar.setProgress(pointAmountForCoupon - pointsGenerated);
                        animFrom = pointAmountForCoupon - pointsGenerated;
                        animTo = pointAmountForCoupon;
                        animTemp = 0;*/
                        //txtPointBalance.setVisibility(View.INVISIBLE);
                    } else {
                        //exceed max progress and get x coupons
                        float tempFrom = (float) (totalPointBalance - pointsGenerated) / (float) pointAmountForCoupon;
                        animFrom = (int) (tempFrom * MAX_PROGRESS);
                        animTo = MAX_PROGRESS;
                        float tempAnim = (float) (pointsBalance) / (float) pointAmountForCoupon;
                        animTemp = (int) (tempAnim * MAX_PROGRESS);
                        progressBar.setProgress(animFrom);
                        numFrom = totalPointBalance - pointsGenerated;
                        numTo = pointAmountForCoupon;
                        numTemp = pointsBalance;

                        /*progress = totalPointBalance;
                        progressBar.setProgress(totalPointBalance - pointsGenerated);
                        animFrom = totalPointBalance - pointsGenerated;
                        animTo = pointAmountForCoupon;
                        animTemp = totalPointBalance - pointAmountForCoupon;*/
                        //txtPointBalance.setVisibility(View.VISIBLE);
                    }
                }
                Spannable info2 = new SpannableString(getString(R.string.text_info_fidelitiz2) + " " + poinInfo2);
                info2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.theme_text_over_basic_bg)), 0, info2.toString().indexOf(poinInfo2) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                info2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_bar_progress)), info2.toString().indexOf(poinInfo2), info2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtFidelitizinfo2.setText(info2);

                txtPointZero.setText(getString(R.string.text_poin, "0"));
                txtPointBalance.setText(getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointsBalance))));
                txtPointMax.setText(getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointAmountForCoupon))));

                btnLoyalty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentLoyalty = new Intent(PaymentSuccessActivity.this, LoyaltyDetailActivity.class);
                        intentLoyalty.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS, rawJSONSuccess);
                        startActivityForResult(intentLoyalty, 0);
                    }
                });
                fidelitizBlock.setVisibility(View.VISIBLE);

                final int finalProgress = progress;
                progressBlock.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Ensure you call it only once :
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            progressBlock.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            progressBlock.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        setPointBalancePosition();
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animFrom: " + animFrom);
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animTo: " + animTo);
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animTemp: " + animTemp);
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numFrom: " + numFrom);
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numTo: " + numTo);
                        mProgressAnimation = new ProgressBarAnimation(PaymentSuccessActivity.this, progressBar,
                                txtPointBalance, progressLine, animFrom, animTo, numFrom, numTo);
                        mProgressAnimation.setDuration(1000);
                        mProgressAnimation.setAnimationListener(progressbarAnimationListener);
                        progressBar.startAnimation(mProgressAnimation);
                    }
                });
            }else{
                fidelitizBlock.setVisibility(View.GONE);
            }
        }else{
            fidelitizBlock.setVisibility(View.GONE);
        }

        btnOK = (DIMOButton) findViewById(R.id.activity_success_btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    private Animation.AnimationListener progressbarAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            if(animTemp > 0){
                txtPointBalance.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                setPointBalancePosition();
                animFrom = 0;
                numFrom = 0;

                if(animTemp > progressBar.getMax()){
                    animTo = progressBar.getMax();
                    animTemp = animTemp - progressBar.getMax();
                    numTemp = numTemp - numTo;
                }else{
                    animTo = animTemp;
                    animTemp = 0;
                    numTo = numTemp;
                    numTemp = 0;
                }

                mProgressAnimation = new ProgressBarAnimation(PaymentSuccessActivity.this, progressBar, txtPointBalance,
                                            progressLine, animFrom, animTo, numFrom, numTo);
                mProgressAnimation.setDuration(1000);
                mProgressAnimation.setAnimationListener(progressbarAnimationListener);
                progressBar.startAnimation(mProgressAnimation);
            }

            if(couponsGenerated > 0 && !voucherAnimateState){
                voucherAnimateState = true;
                Animation zoomAnim = AnimationUtils.loadAnimation(PaymentSuccessActivity.this, R.anim.success_get_voucher);
                txtVoucherGenerated.setVisibility(View.VISIBLE);
                voucherBoxBlock.startAnimation(zoomAnim);
            }
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

    private void setPointBalancePosition(){
        int maxW = progressBar.getWidth();
        int contentW = txtPointBalance.getWidth();
        if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "max width: "+maxW);
        if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "content width: "+contentW);

        int currProgressPx = (int) (((float)progressBar.getProgress()/(float)progressBar.getMax()) * progressBar.getWidth());
        if(currProgressPx < (contentW/2)){
            translateTextView(0);
        }else if(currProgressPx > (maxW - (contentW/2))){
            translateTextView(maxW - contentW);
        }else{
            translateTextView(currProgressPx-(contentW/2));
        }
    }

    private void translateTextView(int value){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtPointBalance.getLayoutParams();
        params.setMargins(value, 0, 0, 0);
        txtPointBalance.setLayoutParams(params);
    }
}
