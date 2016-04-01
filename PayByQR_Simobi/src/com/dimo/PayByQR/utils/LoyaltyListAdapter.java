package com.dimo.PayByQR.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.LoyaltyDetailActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoyaltyListResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rhio on 12/2/15.
 */
public class LoyaltyListAdapter extends RecyclerView.Adapter<LoyaltyListAdapter.ViewHolder> implements Filterable{
    private ArrayList<LoyaltyListResponse> mLoyaltyDatas, mLoyaltyDatasRaw;
    private Activity mActivity;
    private Context mContext;
    private LoyaltyFilter loyaltyFilter;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTxtTitle, mTxtDesc, mTxtExpired, mTxtDiscountAmount;
        public LinearLayout mLayoutDiscount;
        public View mContainer;

        public ViewHolder(View v) {
            super(v);
            mContainer = v;
            mImageView = (ImageView) v.findViewById(R.id.item_loyalty_image);
            mTxtTitle = (TextView) v.findViewById(R.id.item_loyalty_title);
            mTxtDesc = (TextView) v.findViewById(R.id.item_loyalty_detail);
            mTxtExpired = (TextView) v.findViewById(R.id.item_loyalty_expired);
            mTxtDiscountAmount = (TextView) v.findViewById(R.id.item_loyalty_discount_amount);
            mLayoutDiscount = (LinearLayout) v.findViewById(R.id.item_loyalty_discount_block);
        }
    }

    public LoyaltyListAdapter(Activity mActivity, ArrayList<LoyaltyListResponse> mLoyaltyDatas){
        this.mActivity = mActivity;
        this.mLoyaltyDatas = mLoyaltyDatas;
        this.mLoyaltyDatasRaw = mLoyaltyDatas;
        mContext = mActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_loyalty, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final LoyaltyListResponse data = mLoyaltyDatas.get(position);

        holder.mTxtTitle.setText(data.label);
        holder.mTxtExpired.setText(DIMOUtils.getDateFromMillisecond(data.startDate) + " - " + DIMOUtils.getDateFromMillisecond(data.endDate));

        if(data.logo == null) {
            holder.mImageView.setImageResource(R.drawable.loyalty_list_no_image);
        }else{
            //convert binaries to image
            byte[] result = new byte[data.logo.size()];
            for (int i = 0; i < data.logo.size(); i++) {
                result[i] = data.logo.get(i).byteValue();
            }

            InputStream is = new ByteArrayInputStream(result);
            Bitmap scaledBit = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 300, 300, true);
            holder.mImageView.setImageBitmap(scaledBit);
        }

        if(LoyaltyListResponse.LOYALTY_TYPE_DISCOUNT.equals(data.loyaltyProgramType)){
            holder.mTxtDesc.setText("");
            holder.mTxtDesc.setVisibility(View.GONE);

            holder.mTxtDiscountAmount.setText(data.discountAmount + "%");
            holder.mLayoutDiscount.setVisibility(View.VISIBLE);
            setViewMargin(holder.mImageView, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_loyalty_list_image_top),
                    mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), 0);
        }else{
            String amountPerPoint = "";
            if(data.expenseType.equals("MONEY")) amountPerPoint = mContext.getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(data.amountPerPoint));
            else if(data.expenseType.equals("VISIT")) amountPerPoint = "";

            String voucher = "";
            if(LoyaltyListResponse.REWARD_TYPE_CASH.equals(data.rewardType)) voucher = mContext.getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(data.discountAmount));
            else if(LoyaltyListResponse.REWARD_TYPE_PERCENT.equals(data.rewardType)) voucher = data.discountAmount + "%";

            holder.mTxtDesc.setText(mContext.getString(R.string.text_loyalty_list_desc, DIMOUtils.formatAmount(Integer.toString(data.pointPerExpense)), amountPerPoint, DIMOUtils.formatAmount(Integer.toString(data.pointAmountForCoupon)), voucher));
            holder.mTxtDesc.setVisibility(View.VISIBLE);

            holder.mTxtDiscountAmount.setText("");
            holder.mLayoutDiscount.setVisibility(View.GONE);
            setViewMargin(holder.mImageView, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), 0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ordinary Intent for launching a new activity
                Intent intent = new Intent(mContext, LoyaltyDetailActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_FIDELITIZ_JSON, data.rawJSON);
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "Loyalty Detail json 1:\n" + data.rawJSON);

                // Get the transition name from the string
                Pair<View, String> pairImage = Pair.create((View) holder.mImageView, mContext.getString(R.string.transition_name_loyalty_list_image));
                Pair<View, String> pairTitle = Pair.create((View) holder.mTxtTitle, mContext.getString(R.string.transition_name_loyalty_list_title));
                Pair<View, String> pairDesc = Pair.create((View) holder.mTxtDesc, mContext.getString(R.string.transition_name_loyalty_list_desc));

                ActivityOptionsCompat options;
                if (LoyaltyListResponse.LOYALTY_TYPE_DISCOUNT.equals(data.loyaltyProgramType)) {
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairTitle);
                } else {
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairImage, pairTitle, pairDesc);
                }

                //Start the Intent
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });

        //setAnimation(holder.mContainer, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLoyaltyDatas.size();
    }

    @Override
    public Filter getFilter() {
        if (loyaltyFilter == null)
            loyaltyFilter = new LoyaltyFilter();

        return loyaltyFilter;
    }

    public void setViewMargin(View v, int left, int top, int right, int bottom){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        v.setLayoutParams(params);
    }

    /**
     * Here is the key method to apply the animation
     */
    /*private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }*/

    private class LoyaltyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = mLoyaltyDatasRaw;
                results.count = mLoyaltyDatasRaw.size();
            } else {
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "start filtering: "+constraint);
                // We perform filtering operation
                ArrayList<LoyaltyListResponse> nLoyaltyList = new ArrayList<LoyaltyListResponse>();

                for (LoyaltyListResponse p : mLoyaltyDatasRaw) {
                    //do the filter based on Title/label
                    //if (p.label.toUpperCase().contains(constraint.toString().toUpperCase()))
                    if(p.label.toUpperCase().matches(".*\\b" + constraint.toString().toUpperCase() + ".*?\\b.*"))
                        nLoyaltyList.add(p);
                }

                results.values = nLoyaltyList;
                results.count = nLoyaltyList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            /*if (results.count == 0)
                notifyDataSetChanged();
            else {*/
                mLoyaltyDatas = (ArrayList<LoyaltyListResponse>) results.values;
                notifyDataSetChanged();
            //}
        }
    }

}
