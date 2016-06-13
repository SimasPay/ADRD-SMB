package com.dimo.PayByQR.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dimo.PayByQR.R;
import com.dimo.PayByQR.utils.FontCache;

/**
 * Created by Rhio on 11/15/15.
 */
public class DIMOTextView extends TextView{

    public DIMOTextView(Context context) {
        super(context);
        //if(!isInEditMode()) {
            init(context, null);
        //}
    }

    public DIMOTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DIMOTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_dimo_view_DIMOTextView);
        String font = a.getString(R.styleable.com_dimo_view_DIMOTextView_font);
        setCustomFont(font, context);
        a.recycle();
    }

    public void setCustomFont(String font, Context context) {
        if(font == null)
            return;

        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            setTypeface(tf);
            //setLineSpacing(8.0f, 1.0f);
        }
    }


}
