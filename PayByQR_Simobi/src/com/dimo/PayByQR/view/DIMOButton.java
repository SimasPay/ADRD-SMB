package com.dimo.PayByQR.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.utils.FontCache;

/**
 * Created by Rhio on 9/11/15.
 */
public class DIMOButton extends Button {

    private static final int SIZE_NORMAL = 0;
    private static final int SIZE_SMALL = 1;
    private static final int SIZE_BIG = 2;

    private int bgColor;
    private int buttonSize;
    private String font;

    public DIMOButton(Context context) {
        super(context);
        //if(!isInEditMode()) {
            init(context, null);
        //}
    }

    public DIMOButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //if(!isInEditMode()) {
            init(context, attrs);
        //}
    }

    public DIMOButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //if(!isInEditMode()) {
            init(context, attrs);
        //}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Resources resources = getResources();
        int minW = 0, minH = 0;
        if (buttonSize == SIZE_NORMAL){
            minW = (int) resources.getDimension(R.dimen.button_min_width);
            minH = (int) resources.getDimension(R.dimen.button_normal_height);
        } else if (buttonSize == SIZE_SMALL){
            minW = (int) resources.getDimension(R.dimen.button_min_width);
            minH = (int) resources.getDimension(R.dimen.button_small_height);
        }else if (buttonSize == SIZE_BIG){
            minW = (int) resources.getDimension(R.dimen.button_min_width);
            minH = (int) resources.getDimension(R.dimen.button_big_height);
        }
        int w = Math.max(minW, getMeasuredWidth());
        int h = Math.max(minH, getMeasuredHeight());
        setMeasuredDimension(w, minH);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.com_dimo_view_DIMOButton);
            // background shape
            bgColor = a.getColor(R.styleable.com_dimo_view_DIMOButton_bgColor, ContextCompat.getColor(context, R.color.theme_colorPrimary));
            buttonSize = a.getInt(R.styleable.com_dimo_view_DIMOButton_buttonSize, 0);
            font = a.getString(R.styleable.com_dimo_view_DIMOButton_font);
            if (PayByQRProperties.isDebugMode()){
                Log.d("DIMOButton", "button bgColor " + bgColor);
                Log.d("DIMOButton", "button size " + buttonSize);
                Log.d("DIMOButton", "button font " + font);
            }
            a.recycle();
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            setBackgroundDrawable(createBackgroundShape(bgColor, buttonSize));
        }else {
            setBackground(createBackgroundShape(bgColor, buttonSize));
        }

        setCustomFont(font, context);
        initCompoundDrawableSize();
        setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
    }

    private Drawable createBackgroundShape(int color, int buttonSize){
        GradientDrawable background = new GradientDrawable();
        if (buttonSize == SIZE_NORMAL) {
            background.setCornerRadius(getResources().getDimension(R.dimen.button_normal_corner_radius));
            setTextSize(getResources().getInteger(R.integer.text_dimoButton_normal));
        }else if (buttonSize == SIZE_SMALL){
            background.setCornerRadius(getResources().getDimension(R.dimen.button_small_corner_radius));
            setTextSize(getResources().getInteger(R.integer.text_dimoButton_small));
        }else if (buttonSize == SIZE_BIG){
            background.setCornerRadius(getResources().getDimension(R.dimen.button_big_corner_radius));
            setTextSize(getResources().getInteger(R.integer.text_dimoButton_big));
        }
        background.setColor(color);
        return background;
    }

    public void setCustomFont(String font, Context context) {
        if(font == null)
            return;

        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            setTypeface(tf);
        }
    }

    private void initCompoundDrawableSize() {
        int mDrawableWidth = 0, mDrawableHeight = 0;
        if (buttonSize == SIZE_NORMAL){
            mDrawableWidth = (int) getResources().getDimension(R.dimen.button_normal_drawable_size);
            mDrawableHeight = (int) getResources().getDimension(R.dimen.button_normal_drawable_size);
        } else if (buttonSize == SIZE_SMALL){
            mDrawableWidth = (int) getResources().getDimension(R.dimen.button_small_drawable_size);
            mDrawableHeight = (int) getResources().getDimension(R.dimen.button_small_drawable_size);
        }else if (buttonSize == SIZE_BIG){
            mDrawableWidth = (int) getResources().getDimension(R.dimen.button_big_drawable_size);
            mDrawableHeight = (int) getResources().getDimension(R.dimen.button_big_drawable_size);
        }

        if (PayByQRProperties.isDebugMode()) Log.d("DIMOButton", "drawable size: " + mDrawableWidth);

        Drawable[] drawables = getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable == null) {
                continue;
            }

            Rect realBounds = drawable.getBounds();
            float scaleFactor = realBounds.height() / (float) realBounds.width();

            float drawableWidth = realBounds.width();
            float drawableHeight = realBounds.height();

            if (mDrawableWidth > 0) {
                // save scale factor of image
                if (drawableWidth > mDrawableWidth) {
                    drawableWidth = mDrawableWidth;
                    drawableHeight = drawableWidth * scaleFactor;
                }
            }
            if (mDrawableHeight > 0) {
                // save scale factor of image

                if (drawableHeight > mDrawableHeight) {
                    drawableHeight = mDrawableHeight;
                    drawableWidth = drawableHeight / scaleFactor;
                }
            }

            realBounds.right = realBounds.left + Math.round(drawableWidth);
            realBounds.bottom = realBounds.top + Math.round(drawableHeight);

            drawable.setBounds(realBounds);
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }
}
