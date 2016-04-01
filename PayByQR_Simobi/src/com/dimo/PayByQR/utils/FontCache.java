package com.dimo.PayByQR.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Rhio on 11/15/15.
 */
public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}