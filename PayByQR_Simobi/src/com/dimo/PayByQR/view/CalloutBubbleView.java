package com.dimo.PayByQR.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.dimo.PayByQR.R;

/**
 * Created by Rhio on 11/12/15.
 */
public class CalloutBubbleView extends View {

    private int dividerColor;
    private Paint paint;
    private Path path;
    private float viewWidth, viewHeight, cornerRadius, cornerWidth, triangleSize, strokeWidth;
    private RectF oval;

    public CalloutBubbleView(Context context) {
        super(context);
        init(context);
    }

    public CalloutBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CalloutBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalloutBubbleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        oval = new RectF();

        strokeWidth = context.getResources().getDimensionPixelSize(R.dimen.callout_bubble_line);
        cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.callout_bubble_corner);
        triangleSize = context.getResources().getDimensionPixelSize(R.dimen.callout_bubble_triangle_width);
        cornerWidth = cornerRadius * 2;

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.tip_accent));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();

        oval.set(strokeWidth/2, strokeWidth/2, cornerWidth+(strokeWidth/2), cornerWidth+(strokeWidth/2));
        canvas.drawArc(oval, 180, 90, false, paint);
        canvas.drawLine(cornerRadius+(strokeWidth/2), strokeWidth/2, viewWidth-cornerRadius-(strokeWidth/2), strokeWidth/2, paint);
        oval.set(viewWidth-cornerWidth-(strokeWidth/2), strokeWidth/2, viewWidth-(strokeWidth/2), cornerWidth+(strokeWidth/2));
        canvas.drawArc(oval, 270, 90, false, paint);
        canvas.drawLine(viewWidth-(strokeWidth/2), cornerRadius+(strokeWidth/2), viewWidth-(strokeWidth/2), viewHeight-cornerRadius-(strokeWidth/2)-triangleSize, paint);
        oval.set(viewWidth-cornerWidth-(strokeWidth/2), viewHeight-cornerWidth-(strokeWidth/2)-triangleSize, viewWidth-(strokeWidth/2), viewHeight-(strokeWidth/2)-triangleSize);
        canvas.drawArc(oval, 0, 90, false, paint);
        canvas.drawLine(viewWidth-cornerRadius-(strokeWidth/2), viewHeight-(strokeWidth/2)-triangleSize, (viewWidth/2)+(triangleSize/2), viewHeight-(strokeWidth/2)-triangleSize, paint);
        canvas.drawLine((viewWidth/2)-(triangleSize/2), viewHeight-(strokeWidth/2)-triangleSize, cornerRadius+(strokeWidth/2), viewHeight-(strokeWidth/2)-triangleSize, paint);
        oval.set(strokeWidth/2, viewHeight-cornerWidth-(strokeWidth/2)-triangleSize, cornerWidth+(strokeWidth/2), viewHeight-(strokeWidth/2)-triangleSize);
        canvas.drawArc(oval, 90, 90, false, paint);
        canvas.drawLine(strokeWidth/2, viewHeight-cornerRadius-(strokeWidth/2)-triangleSize, strokeWidth/2, cornerRadius+(strokeWidth/2), paint);

        canvas.drawLine((viewWidth/2)-(triangleSize/2), viewHeight-(strokeWidth/2)-triangleSize, viewWidth/2, viewHeight-(strokeWidth/2), paint);
        canvas.drawLine((viewWidth/2)+(triangleSize/2), viewHeight-(strokeWidth/2)-triangleSize, viewWidth/2, viewHeight-(strokeWidth/2), paint);

        super.onDraw(canvas);
    }

}