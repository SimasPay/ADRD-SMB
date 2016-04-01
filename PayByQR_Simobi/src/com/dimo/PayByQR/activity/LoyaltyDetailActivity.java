package com.dimo.PayByQR.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceStatusResponse;
import com.dimo.PayByQR.model.LoyaltyListResponse;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.utils.DividerItemDecoration;
import com.dimo.PayByQR.utils.LoyaltyListAdapter;
import com.dimo.PayByQR.view.ProgressBarAnimation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoyaltyDetailActivity extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private TextView txtTitle, txtTitleDiscount;
    private ImageView btnBack;
    private LinearLayout layoutDiscount;
    private ScrollView layoutPoint;
    private LoyaltyListResponse mLoyaltyData;
    private int balance=0, couponLength=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty_detail);

        // set an exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().setExitTransition(new Slide(Gravity.LEFT));
        }

        listener = PayByQRSDK.getListener();

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        layoutPoint = (ScrollView) findViewById(R.id.activity_loyalty_detail_layout_point);
        layoutDiscount = (LinearLayout) findViewById(R.id.activity_loyalty_detail_layout_discount);

        txtTitle.setText(getString(R.string.text_header_title_loyalty_detail));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String json = getIntent().getStringExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON);
        String jsonSuccess = getIntent().getStringExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS);

        if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "Loyalty Detail json 2:\n"+json);
        if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "Loyalty Detail json success:\n"+jsonSuccess);

        Gson gson = new Gson();
        if(null != json) {
            //Page opened from LoyaltyActivity
            mLoyaltyData = gson.fromJson(json, LoyaltyListResponse.class);
        }else{
            //Page opened from PaymentSuccessActivity
            InvoiceStatusResponse invoiceStatusResponse = gson.fromJson(jsonSuccess, InvoiceStatusResponse.class);
            mLoyaltyData = new LoyaltyListResponse();
            mLoyaltyData.loyaltyProgramType = LoyaltyListResponse.LOYALTY_TYPE_POINTS;
            mLoyaltyData.label = invoiceStatusResponse.fidelitizInfo.loyaltyProgramLabel;
            mLoyaltyData.logo = invoiceStatusResponse.fidelitizInfo.logo;
            mLoyaltyData.expenseType = invoiceStatusResponse.fidelitizInfo.expenseType;
            mLoyaltyData.amountPerPoint = invoiceStatusResponse.fidelitizInfo.amountPerPoint;
            mLoyaltyData.rewardType = invoiceStatusResponse.fidelitizInfo.rewardType;
            mLoyaltyData.discountAmount = invoiceStatusResponse.fidelitizInfo.couponValue;
            mLoyaltyData.pointPerExpense = invoiceStatusResponse.fidelitizInfo.pointPerExpense;
            mLoyaltyData.pointAmountForCoupon = invoiceStatusResponse.fidelitizInfo.pointAmountForCoupon;
            mLoyaltyData.startDate = invoiceStatusResponse.fidelitizInfo.startDate;
            mLoyaltyData.endDate = invoiceStatusResponse.fidelitizInfo.endDate;
            mLoyaltyData.membership = invoiceStatusResponse.fidelitizInfo.membership;
            mLoyaltyData.fidelitizInfo = null;
            balance = invoiceStatusResponse.fidelitizInfo.pointsBalance;
            couponLength = invoiceStatusResponse.fidelitizInfo.couponsBalance;

            TextView btnActionDone = (TextView) findViewById(R.id.header_bar_action_done);
            btnActionDone.setVisibility(View.VISIBLE);
            btnActionDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isClose = listener.callbackTransactionStatus(Constant.STATUS_CODE_PAYMENT_SUCCESS, getString(R.string.text_payment_success));
                    if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) closeSDK(true);
                    else closeSDK(isClose);
                }
            });
        }

        if(mLoyaltyData.loyaltyProgramType.equals(LoyaltyListResponse.LOYALTY_TYPE_DISCOUNT)){
            initDiscountLayout();
        }else{
            initPointLayout();
        }
    }

    public void initDiscountLayout(){
        layoutPoint.setVisibility(View.GONE);
        layoutDiscount.setVisibility(View.VISIBLE);

        txtTitleDiscount = (TextView) findViewById(R.id.activity_loyalty_detail_title_discount);
        TextView txtInfoExpired = (TextView) findViewById(R.id.activity_loyalty_detail_discount_info_expired);
        TextView txtInfoMinimum = (TextView) findViewById(R.id.activity_loyalty_detail_discount_info_minimum);
        TextView txtInfoMaxDiscount = (TextView) findViewById(R.id.activity_loyalty_detail_discount_info_max_discount);
        LinearLayout layoutMembership = (LinearLayout) findViewById(R.id.activity_loyalty_detail_discount_info_membership_root);

        txtTitleDiscount.setText(mLoyaltyData.label);

        String mInfoExpired = "", mInfoMinimum = "", mInfoMaxDiscount = "";
        if(mLoyaltyData.startDate > 0 && mLoyaltyData.endDate > 0)
            mInfoExpired = DIMOUtils.getDateFromMillisecond(mLoyaltyData.startDate) + " - " + DIMOUtils.getDateFromMillisecond(mLoyaltyData.endDate);
        else
            mInfoExpired = getString(R.string.text_loyalty_detail_info_empty, "masa berlaku");
        txtInfoExpired.setText(mInfoExpired);

        if(mLoyaltyData.minTransAmountForDiscount > 0)
            mInfoMinimum = getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.minTransAmountForDiscount));
        else
            mInfoMinimum = getString(R.string.text_loyalty_detail_info_empty, "minimum pembelian");
        txtInfoMinimum.setText(mInfoMinimum);

        if(mLoyaltyData.maxDiscountAmount > 0)
            mInfoMaxDiscount = getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.maxDiscountAmount));
        else
            mInfoMaxDiscount = getString(R.string.text_loyalty_detail_info_empty, "maksimum diskon");
        txtInfoMaxDiscount.setText(mInfoMaxDiscount);

        if(mLoyaltyData.membership.length > 0) {
            for (int i = 0 ; i < mLoyaltyData.membership.length ; i++){
                View membershipItemView = LayoutInflater.from(this).inflate(R.layout.item_list_loyalty_info_location, layoutMembership, false);
                TextView locationEmpty = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_empty);
                LinearLayout locationVisible = (LinearLayout) membershipItemView.findViewById(R.id.item_loyalty_detail_location_visible);
                TextView txtLocationTitle = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_title);
                //TextView txtLocationAddr = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_addr);
                txtLocationTitle.setText(mLoyaltyData.membership[i].memberBrandName+", "+mLoyaltyData.membership[i].address1+", "+mLoyaltyData.membership[i].city);
                //txtLocationAddr.setText(mLoyaltyData.membership[i].address1+", "+mLoyaltyData.membership[i].city);

                locationEmpty.setVisibility(View.GONE);
                locationVisible.setVisibility(View.VISIBLE);

                layoutMembership.addView(membershipItemView);
            }
        }else {
            View membershipItemView = LayoutInflater.from(this).inflate(R.layout.item_list_loyalty_info_location, layoutMembership, true);
            TextView locationEmpty = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_empty);
            LinearLayout locationVisible = (LinearLayout) membershipItemView.findViewById(R.id.item_loyalty_detail_location_visible);
            locationEmpty.setVisibility(View.VISIBLE);
            locationVisible.setVisibility(View.GONE);
        }
    }

    public void initPointLayout(){
        layoutPoint.setVisibility(View.VISIBLE);
        layoutDiscount.setVisibility(View.GONE);

        ImageView logo = (ImageView) findViewById(R.id.activity_loyalty_detail_image);
        TextView txtTitle = (TextView) findViewById(R.id.activity_loyalty_detail_title);
        TextView txtDesc = (TextView) findViewById(R.id.activity_loyalty_detail_desc);
        TextView txtPointToGo = (TextView) findViewById(R.id.activity_loyalty_detail_point_to_go);
        TextView txtVoucherCount = (TextView) findViewById(R.id.activity_loyalty_detail_voucher_generated);
        TextView txtVoucherAmount = (TextView) findViewById(R.id.activity_loyalty_detail_voucher_amount);
        TextView txtExpired = (TextView) findViewById(R.id.activity_loyalty_detail_point_info_expired);
        TextView txtPointZero = (TextView) findViewById(R.id.activity_loyalty_detail_point_0);
        LinearLayout layoutMembership = (LinearLayout) findViewById(R.id.activity_loyalty_detail_point_info_membership_root);
        final TextView txtPointBalance = (TextView) findViewById(R.id.activity_loyalty_detail_point_balance);
        TextView txtPointMax = (TextView) findViewById(R.id.activity_loyalty_detail_point_max);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_loyalty_detail_progress);
        final View progressLine = findViewById(R.id.activity_loyalty_detail_progress_line);

        txtTitle.setText(mLoyaltyData.label);

        //init Image Logo
        if(mLoyaltyData.logo == null) {
            logo.setImageResource(R.drawable.loyalty_list_no_image);
        }else{
            //convert binaries to image
            byte[] result = new byte[mLoyaltyData.logo.size()];
            for (int i = 0; i < mLoyaltyData.logo.size(); i++) {
                result[i] = mLoyaltyData.logo.get(i).byteValue();
            }

            InputStream is = new ByteArrayInputStream(result);
            Bitmap scaledBit = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 500, 500, true);
            logo.setImageBitmap(scaledBit);
        }

        //build string for Description
        String amountPerPoint = "";
        if(mLoyaltyData.expenseType.equals("MONEY")) amountPerPoint = getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.amountPerPoint));
        else if(mLoyaltyData.expenseType.equals("VISIT")) amountPerPoint = "";

        String voucherInString = "";
        if(LoyaltyListResponse.REWARD_TYPE_CASH.equals(mLoyaltyData.rewardType)) voucherInString = getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.discountAmount));
        else if(LoyaltyListResponse.REWARD_TYPE_PERCENT.equals(mLoyaltyData.rewardType)) voucherInString = mLoyaltyData.discountAmount + "%";

        txtDesc.setText(getString(R.string.text_loyalty_list_desc, DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.pointPerExpense)), amountPerPoint, DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.pointAmountForCoupon)), voucherInString));

        //check if fidelitizInfo is null, then set to default value
        if(null != mLoyaltyData.fidelitizInfo){
            balance = mLoyaltyData.fidelitizInfo.balance;
            couponLength = mLoyaltyData.fidelitizInfo.coupons.length;
        }

        //build String for Point to Go
        int pointToGo = mLoyaltyData.pointAmountForCoupon - balance;
        String poinInfo = getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(pointToGo)));
        String voucherInfo = getString(R.string.text_voucher_balance, "", voucherInString);
        Spannable infoPointToGo = new SpannableString(poinInfo + " " + getString(R.string.text_loyalty_detail_point_to_go) + " " + voucherInfo);
        infoPointToGo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), 0, poinInfo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        infoPointToGo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.loyalty_list_desc)), poinInfo.length() + 1, infoPointToGo.toString().indexOf(voucherInfo)-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        infoPointToGo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), infoPointToGo.toString().indexOf(voucherInfo), infoPointToGo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtPointToGo.setText(infoPointToGo);

        //build String for Total Voucher
        txtVoucherCount.setText("x" + couponLength);
        txtVoucherAmount.setText(voucherInString);

        //build String for Expired date
        txtExpired.setText(DIMOUtils.getDateFromMillisecond(mLoyaltyData.startDate) + " - " + DIMOUtils.getDateFromMillisecond(mLoyaltyData.endDate));

        //build String for Location info
        if(mLoyaltyData.membership.length > 0) {
            for (int i = 0 ; i < mLoyaltyData.membership.length ; i++){
                View membershipItemView = LayoutInflater.from(this).inflate(R.layout.item_list_loyalty_info_location, layoutMembership, false);
                TextView locationEmpty = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_empty);
                LinearLayout locationVisible = (LinearLayout) membershipItemView.findViewById(R.id.item_loyalty_detail_location_visible);
                TextView txtLocationTitle = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_title);
                //TextView txtLocationAddr = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_addr);
                txtLocationTitle.setText(mLoyaltyData.membership[i].memberBrandName+", "+mLoyaltyData.membership[i].address1+", "+mLoyaltyData.membership[i].city);
                //txtLocationAddr.setText(mLoyaltyData.membership[i].address1+", "+mLoyaltyData.membership[i].city);

                locationEmpty.setTextSize(getResources().getInteger(R.integer.text_size_16));
                txtLocationTitle.setTextSize(getResources().getInteger(R.integer.text_size_16));

                locationEmpty.setVisibility(View.GONE);
                locationVisible.setVisibility(View.VISIBLE);

                layoutMembership.addView(membershipItemView);
            }
        }else {
            View membershipItemView = LayoutInflater.from(this).inflate(R.layout.item_list_loyalty_info_location, layoutMembership, true);
            TextView locationEmpty = (TextView) membershipItemView.findViewById(R.id.item_loyalty_detail_location_empty);
            LinearLayout locationVisible = (LinearLayout) membershipItemView.findViewById(R.id.item_loyalty_detail_location_visible);
            locationEmpty.setVisibility(View.VISIBLE);
            locationVisible.setVisibility(View.GONE);
        }

        //build String for 0, balance and max point
        txtPointZero.setText(getString(R.string.text_poin, "0"));
        txtPointBalance.setText(getString(R.string.text_poin, "0"));
        txtPointMax.setText(getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString(mLoyaltyData.pointAmountForCoupon))));

        //animate the progress bar
        progressBar.setMax(1000);
        final RelativeLayout progressBlock = (RelativeLayout) findViewById(R.id.activity_loyalty_detail_progress_block);
        progressBlock.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    progressBlock.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    progressBlock.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int animFrom = 0;
                float tempTo = (float) balance / (float) mLoyaltyData.pointAmountForCoupon;
                int animTo = (int) (tempTo * progressBar.getMax());
                int animTemp = 0;
                progressBar.setProgress(animFrom);
                int numFrom = 0;
                int numTo = balance;
                int numTemp = 0;

                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animFrom: " + animFrom);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animTo: " + animTo);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "animTemp: " + animTemp);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numFrom: " + numFrom);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numTo: " + numTo);
                ProgressBarAnimation mProgressAnimation = new ProgressBarAnimation(LoyaltyDetailActivity.this, progressBar,
                        txtPointBalance, progressLine, animFrom, animTo, numFrom, numTo);
                mProgressAnimation.setDuration(1000);
                //mProgressAnimation.setAnimationListener(progressbarAnimationListener);
                progressBar.startAnimation(mProgressAnimation);
            }
        });
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        PayByQRProperties.setSDKContext(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
