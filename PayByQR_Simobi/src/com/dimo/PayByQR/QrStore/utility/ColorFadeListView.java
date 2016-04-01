package com.dimo.PayByQR.QrStore.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by san on 1/16/16.
 */
public class ColorFadeListView extends ListView
{
    // fade to green by default
    private static int mFadeColor = 0xFF00FF00;
    public ColorFadeListView(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }
    public ColorFadeListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
        setFadingEdgeLength(30);
        setVerticalFadingEdgeEnabled(true);
    }
    @Override
    public int getSolidColor()
    {
        return mFadeColor;
    }
    public void setFadeColor( int fadeColor )
    {
        mFadeColor = fadeColor;
    }
    public int getFadeColor()
    {
        return mFadeColor;
    }
}