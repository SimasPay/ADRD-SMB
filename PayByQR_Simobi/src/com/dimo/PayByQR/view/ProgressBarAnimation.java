package com.dimo.PayByQR.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.utils.DIMOUtils;

/**
 * Created by Rhio on 11/26/15.
 */
public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private TextView txtPointBalance;
    private View line;
    private float from, numFrom;
    private float to, numTo;
    private float currProgressPixel;
    private int anchor, progressWidth;
    private Context ctx;

    public ProgressBarAnimation(Context ctx, ProgressBar progressBar, TextView txtPointBalance, View line, float from, float to, float numFrom, float numTo) {
        super();
        this.ctx = ctx;
        this.progressBar = progressBar;
        this.txtPointBalance = txtPointBalance;
        this.line = line;
        this.from = from;
        this.to = to;
        this.numFrom = numFrom;
        this.numTo = numTo;

        progressWidth = progressBar.getWidth();
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "progressWidth: " + progressWidth);
        anchor = txtPointBalance.getWidth()/2;
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "anchor: " + anchor);

        this.txtPointBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                anchor = ProgressBarAnimation.this.txtPointBalance.getWidth()/2;
                if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "anchor: " + anchor);
            }
        });
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "interpolatedTime: " + interpolatedTime);

        float progressStep = from + (to - from) * interpolatedTime;
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "progressStep: " + progressStep);
        progressBar.setProgress((int) progressStep);

        float numberStep = numFrom + (numTo - numFrom) * interpolatedTime;
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "numberStep: " + numberStep);
        txtPointBalance.setText(ctx.getString(R.string.text_poin, DIMOUtils.formatAmount(Integer.toString((int) numberStep))));

        currProgressPixel = ((float)progressBar.getProgress()/(float)progressBar.getMax()) * progressWidth;
        float translationStep = (progressStep/(float)progressBar.getMax()) * progressWidth;
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "currProgressPixel: " + currProgressPixel);
        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "translationStep: " + translationStep);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) line.getLayoutParams();
        params.setMargins((int)currProgressPixel, 0, 0, 0);
        line.setLayoutParams(params);

        if(currProgressPixel < anchor){
            currProgressPixel = translationStep;
        }else if(currProgressPixel >= (progressWidth-anchor)){
            int x = progressWidth-txtPointBalance.getWidth();
            translateTextView(x);
        }else{
            translateTextView((int) currProgressPixel - anchor);
        }

    }

    private void translateTextView(int value){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtPointBalance.getLayoutParams();

        if(progressBar.getProgress() == progressBar.getMax()){
            params.setMargins(0, 0, 0, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }else{
            params.setMargins(value, 0, 0, 0);
        }

        txtPointBalance.setLayoutParams(params);
    }

}
