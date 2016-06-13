package com.dimo.PayByQR.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOTextView;
import com.dimo.PayByQR.view.ProgressBarAnimation;

/**
 * Created by Rhio on 6/1/16.
 */
public class AlternateDialog extends DialogFragment {
    private int statusCode;
    private String content, rawJSON;
    private LoyaltyModel loyaltyModel;
    private Handler handler;

    private DIMOButton btnOK, btnLoyalty;
    private TextView txtTitle, txtContent, txtFidelitizinfo1, txtFidelitizinfo2, txtFidelitizinfo3;
    private DIMOTextView txtPointBalance, txtPointMax, txtPointZero, txtVoucherGenerated, txtVoucherAmount;
    private ProgressBar progressBar;
    private View progressLine;
    private LinearLayout fidelitizBlock;
    private RelativeLayout progressBlock, voucherBoxBlock;
    private ProgressBarAnimation mProgressAnimation;
    private int animFrom = 0, animTo = 0, animTemp = 0, numFrom = 0, numTo = 0, numTemp = 0;
    private int MAX_PROGRESS = 1000;
    private boolean voucherAnimateState = false;

    public AlternateDialog(int statusCode, String content, LoyaltyModel loyaltyModel, String rawJSON, Handler handler){
        this.statusCode = statusCode;
        this.content = content;
        this.loyaltyModel = loyaltyModel;
        this.rawJSON = rawJSON;
        this.handler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.dialog_alternate, container, false);

        txtTitle = (TextView) rootView.findViewById(R.id.dialog_alternate_title);
        txtContent = (TextView) rootView.findViewById(R.id.dialog_alternate_text_content);
        txtContent.setText(content);

        fidelitizBlock = (LinearLayout) rootView.findViewById(R.id.dialog_alternate_loyalty_block);
        if(statusCode == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
            if (null != loyaltyModel && PayByQRProperties.isUsingLoyalty()) {
                String strCouponValue = "";
                int pointToGo;
                if (loyaltyModel.pointsGenerated > 0) {
                    txtFidelitizinfo1 = (TextView) rootView.findViewById(R.id.dialog_alternate_text_fidelitiz1);
                    txtFidelitizinfo2 = (TextView) rootView.findViewById(R.id.dialog_alternate_text_fidelitiz2);
                    txtFidelitizinfo3 = (TextView) rootView.findViewById(R.id.dialog_alternate_text_fidelitiz3);
                    txtPointZero = (DIMOTextView) rootView.findViewById(R.id.dialog_alternate_fidelitiz_point_0);
                    txtPointBalance = (DIMOTextView) rootView.findViewById(R.id.dialog_alternate_fidelitiz_point_balance);
                    txtPointMax = (DIMOTextView) rootView.findViewById(R.id.dialog_alternate_fidelitiz_point_max);
                    txtVoucherGenerated = (DIMOTextView) rootView.findViewById(R.id.dialog_alternate_voucher_generated);
                    txtVoucherAmount = (DIMOTextView) rootView.findViewById(R.id.dialog_alternate_voucher_amount);
                    progressBar = (ProgressBar) rootView.findViewById(R.id.dialog_alternate_fidelitiz_progress);
                    btnLoyalty = (DIMOButton) rootView.findViewById(R.id.dialog_alternate_btn_loyalty);
                    progressBlock = (RelativeLayout) rootView.findViewById(R.id.dialog_alternate_fidelitiz_progress_block);
                    voucherBoxBlock = (RelativeLayout) rootView.findViewById(R.id.dialog_alternate_voucher_block);
                    progressLine = rootView.findViewById(R.id.dialog_alternate_fidelitiz_progress_line);

                    if (loyaltyModel.rewardType.equals(LoyaltyProgramModel.REWARD_TYPE_CASH))
                        strCouponValue = "Rp " + DIMOUtils.formatAmount(Integer.toString(loyaltyModel.couponValue)) + ",-";
                    else
                        strCouponValue = loyaltyModel.couponValue + "%";

                    txtVoucherAmount.setText(strCouponValue);

                    pointToGo = loyaltyModel.pointAmountForCoupon - loyaltyModel.pointsBalance;

                    String poinInfo1 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(loyaltyModel.pointsGenerated)));
                    Spannable info1 = new SpannableString(getString(R.string.text_info_fidelitiz1) + " " + poinInfo1);
                    info1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.theme_text_over_basic_bg)), 0, info1.toString().indexOf(poinInfo1) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    info1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.progress_bar_progress)), info1.toString().indexOf(poinInfo1), info1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txtFidelitizinfo1.setText(info1);

                    String poinInfo2 = "";
                    int progress = 0;
                    progressBar.setMax(MAX_PROGRESS);
                    if (loyaltyModel.couponsGenerated == 0) {
                        poinInfo2 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(loyaltyModel.pointsBalance)));

                        String poinInfo3 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointToGo)));
                        Spannable info3 = new SpannableString(poinInfo3 + " " + getString(R.string.text_info_fidelitiz3));
                        info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.progress_bar_progress)), 0, poinInfo3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        info3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.theme_text_over_basic_bg)), poinInfo3.length() + 1, info3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txtFidelitizinfo3.setText(info3);

                        float tempFrom = (float) (loyaltyModel.pointsBalance - loyaltyModel.pointsGenerated) / (float) loyaltyModel.pointAmountForCoupon;
                        animFrom = (int) (tempFrom * MAX_PROGRESS);
                        float tempTo = (float) loyaltyModel.pointsBalance / (float) loyaltyModel.pointAmountForCoupon;
                        animTo = (int) (tempTo * MAX_PROGRESS);
                        animTemp = 0;
                        progressBar.setProgress(animFrom);
                        numFrom = loyaltyModel.pointsBalance - loyaltyModel.pointsGenerated;
                        numTo = loyaltyModel.pointsBalance;
                        numTemp = 0;

                        txtVoucherGenerated.setVisibility(View.GONE);
                    } else {
                        int totalPointBalance = loyaltyModel.pointsBalance + (loyaltyModel.couponsGenerated * loyaltyModel.pointAmountForCoupon);
                        poinInfo2 = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(totalPointBalance)));

                        txtFidelitizinfo3.setText(getString(R.string.text_info_fidelitiz3_get_voucher));
                        txtVoucherGenerated.setText("x" + loyaltyModel.couponsGenerated);
                        txtVoucherGenerated.setVisibility(View.GONE);

                        if (loyaltyModel.pointsBalance == 0) {
                            //reach max progress and get x coupons
                            progress = MAX_PROGRESS;
                            float tempFrom = (float) (loyaltyModel.pointAmountForCoupon - loyaltyModel.pointsGenerated) / (float) loyaltyModel.pointAmountForCoupon;
                            animFrom = (int) (tempFrom * MAX_PROGRESS);
                            animTo = MAX_PROGRESS;
                            animTemp = 0;
                            progressBar.setProgress(animFrom);
                            numFrom = loyaltyModel.pointAmountForCoupon - loyaltyModel.pointsGenerated;
                            numTo = loyaltyModel.pointAmountForCoupon;
                            numTemp = 0;
                        } else {
                            //exceed max progress and get x coupons
                            float tempFrom = (float) (totalPointBalance - loyaltyModel.pointsGenerated) / (float) loyaltyModel.pointAmountForCoupon;
                            animFrom = (int) (tempFrom * MAX_PROGRESS);
                            animTo = MAX_PROGRESS;
                            float tempAnim = (float) (loyaltyModel.pointsBalance) / (float) loyaltyModel.pointAmountForCoupon;
                            animTemp = (int) (tempAnim * MAX_PROGRESS);
                            progressBar.setProgress(animFrom);
                            numFrom = totalPointBalance - loyaltyModel.pointsGenerated;
                            numTo = loyaltyModel.pointAmountForCoupon;
                            numTemp = loyaltyModel.pointsBalance;
                        }
                    }
                    Spannable info2 = new SpannableString(getString(R.string.text_info_fidelitiz2) + " " + poinInfo2);
                    info2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.theme_text_over_basic_bg)), 0, info2.toString().indexOf(poinInfo2) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    info2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.progress_bar_progress)), info2.toString().indexOf(poinInfo2), info2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txtFidelitizinfo2.setText(info2);

                    txtPointZero.setText(getString(R.string.text_poin, "0"));
                    txtPointBalance.setText(getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(loyaltyModel.pointsBalance))));
                    txtPointMax.setText(getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(loyaltyModel.pointAmountForCoupon))));

                    btnLoyalty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentLoyalty = new Intent(getContext(), LoyaltyDetailActivity.class);
                            intentLoyalty.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS, rawJSON);
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
                            if (PayByQRProperties.isDebugMode())
                                Log.d("RHIO", "animFrom: " + animFrom);
                            if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animTo: " + animTo);
                            if (PayByQRProperties.isDebugMode())
                                Log.d("RHIO", "animTemp: " + animTemp);
                            if (PayByQRProperties.isDebugMode())
                                Log.d("RHIO", "numFrom: " + numFrom);
                            if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numTo: " + numTo);
                            mProgressAnimation = new ProgressBarAnimation(getContext(), progressBar,
                                    txtPointBalance, progressLine, animFrom, animTo, numFrom, numTo);
                            mProgressAnimation.setDuration(1000);
                            mProgressAnimation.setAnimationListener(progressbarAnimationListener);
                            progressBar.startAnimation(mProgressAnimation);
                        }
                    });
                } else {
                    fidelitizBlock.setVisibility(View.GONE);
                }
            } else {
                fidelitizBlock.setVisibility(View.GONE);
            }
        } else {
            fidelitizBlock.setVisibility(View.GONE);
        }

        btnOK = (DIMOButton) rootView.findViewById(R.id.dialog_alternate_btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);*/

        return dialog;
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

                mProgressAnimation = new ProgressBarAnimation(getContext(), progressBar, txtPointBalance,
                        progressLine, animFrom, animTo, numFrom, numTo);
                mProgressAnimation.setDuration(1000);
                mProgressAnimation.setAnimationListener(progressbarAnimationListener);
                progressBar.startAnimation(mProgressAnimation);
            }

            if(loyaltyModel.couponsGenerated > 0 && !voucherAnimateState){
                voucherAnimateState = true;
                Animation zoomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.success_get_voucher);
                txtVoucherGenerated.setVisibility(View.VISIBLE);
                voucherBoxBlock.startAnimation(zoomAnim);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

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

    public void dismissDialog() {
        PayByQRSDKListener listener = PayByQRSDK.getListener();
        if (statusCode == Constant.STATUS_CODE_PAYMENT_SUCCESS) {
            boolean isClose = listener.callbackTransactionStatus(Constant.STATUS_CODE_PAYMENT_SUCCESS, content);
            if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) closeSDK(true);
            else closeSDK(isClose);
        } else {
            if (Constant.REQUEST_CODE_ERROR_INVALID_QR == statusCode || Constant.ERROR_CODE_INVALID_QR == statusCode) {
                closeSDK(listener.callbackInvalidQRCode());
            } else if (Constant.REQUEST_CODE_ERROR_CONNECTION == statusCode || Constant.ERROR_CODE_CONNECTION == statusCode) {
                closeSDK(false);
            } else if (Constant.REQUEST_CODE_ERROR_UNKNOWN == statusCode || Constant.ERROR_CODE_UNKNOWN_ERROR == statusCode) {
                boolean isClose = listener.callbackUnknowError();
                if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY)
                    closeSDK(true);
                else closeSDK(isClose);
            } else if (Constant.REQUEST_CODE_ERROR_PAYMENT_FAILED == statusCode || Constant.ERROR_CODE_PAYMENT_FAILED == statusCode) {
                boolean isClose = listener.callbackTransactionStatus(Constant.ERROR_CODE_PAYMENT_FAILED, content);
                if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) closeSDK(true);
                else closeSDK(isClose);
            } else if (Constant.REQUEST_CODE_ERROR_TIME_OUT == statusCode || Constant.ERROR_CODE_TIME_OUT == statusCode) {
                boolean isClose = listener.callbackTransactionStatus(Constant.ERROR_CODE_TIME_OUT, content);
                if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY)
                    closeSDK(true);
                else closeSDK(isClose);
            } else if (Constant.REQUEST_CODE_ERROR_AUTHENTICATION == statusCode || Constant.ERROR_CODE_AUTHENTICATION == statusCode) {
                listener.callbackAuthenticationError();
                closeSDK(true);
            } else if (Constant.REQUEST_CODE_ERROR_MINIMUM_TRX == statusCode) {
                closeSDK(false);
            } else if (Constant.REQUEST_CODE_ERROR_QRSTORE == statusCode) {
                closeSDK(false);
            } else {
                boolean isClose = listener.callbackUnknowError();
                if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP || PayByQRSDK.getModule() == PayByQRSDK.MODULE_LOYALTY)
                    closeSDK(true);
                else closeSDK(isClose);
            }
        }
        dismiss();
    }

    private void closeSDK(boolean isCloseSDK){
        Message message = handler.obtainMessage(Constant.MESSAGE_END_OK);
        message.getData().putBoolean(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        handler.sendMessage(message);
    }
}
