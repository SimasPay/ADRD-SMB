package com.dimo.PayByQR.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.dimo.PayByQR.R;

/**
 * Created by Rhio on 11/12/15.
 */
public class DiagonalLineView extends View {

    private int dividerColor;
    private Paint paint;

    public DiagonalLineView(Context context) {
        super(context);
        init(context);
    }

    public DiagonalLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DiagonalLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiagonalLineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        dividerColor = ContextCompat.getColor(context, R.color.txt_discount_amount);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(dividerColor);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.image_size_diagonal_line));

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), 0, paint);
        super.onDraw(canvas);
    }

}