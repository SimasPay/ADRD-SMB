package com.dimo.PayByQR.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.dimo.PayByQR.R;

/**
 * Created by Rhio on 11/12/15.
 */
public class TriangleView extends View {
    private Paint paint;
    private Path path;

    public TriangleView(Context context) {
        super(context);
        init(context);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        /*paint.setAntiAlias(true);
        paint.setColor(dividerColor);
        paint.setStrokeWidth(resources.getDimension(R.dimen.image_size_diagonal_line));*/

        paint.setStrokeWidth(4);
        paint.setColor(ContextCompat.getColor(context, R.color.store_detail_info_background));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0, getHeight());
        path.lineTo(getWidth()/2, 0);
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.close();

        canvas.drawPath(path, paint);
    }

}