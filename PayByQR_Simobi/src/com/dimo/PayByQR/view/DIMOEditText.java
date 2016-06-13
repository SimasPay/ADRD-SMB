package com.dimo.PayByQR.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.dimo.PayByQR.R;
import com.dimo.PayByQR.utils.FontCache;

/**
 * Created by Rhio on 11/15/15.
 */
public class DIMOEditText extends EditText{

    public DIMOEditText(Context context) {
        super(context);
        init(context, null);
    }

    public DIMOEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DIMOEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_dimo_view_DIMOEditText);
        String font = a.getString(R.styleable.com_dimo_view_DIMOEditText_font);
        setCustomFont(font, context);
        a.recycle();
    }

    public void setCustomFont(String font, Context context) {
        if(font == null)
            return;

        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            setTypeface(tf);
        }
    }
}
