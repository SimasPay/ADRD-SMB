package com.dimo.PayByQR.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Rhio on 10/28/15.
 */
public class DIMOUtils {

    public static void showAlertDialog(Context ctx, String title, String message, String positiveText,
                                       DialogInterface.OnClickListener positiveListener,
                                       String negativeText, DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(null != title) builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        if(null != positiveText) {
            if (null == positiveListener) {
                positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
            }
            builder.setPositiveButton(positiveText, positiveListener);
        }

        if(null != negativeText) {
            if (null == negativeListener) {
                negativeListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
            }
            builder.setNegativeButton(negativeText, negativeListener);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static String formatAmount(String rawAmount){
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat idrFormat = new DecimalFormat(newPattern);
        String formatted = ""+idrFormat.format(Long.parseLong(rawAmount));

        if(formatted.charAt(formatted.length()-3) == ',' || formatted.charAt(formatted.length()-3) == '.'){
            formatted = formatted.substring(0, formatted.length()-3);
        }

        return formatted;
    }

    public static String getDateFromMillisecond(long time) {
        Calendar cal = Calendar.getInstance(new Locale("in_ID"));
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMMM yyyy", cal).toString();
        return date;
    }

    /**
     * Check in AndroidManifestFile if user has defined a specified permission
     *
     * @param ctx
     *            current application context
     * @param permissionName
     *            permission (ex: Manifest.permission.WRITE_EXTERNAL_STORAGE)
     * @return true or false
     */
    public boolean doesUserHavePermission(Context ctx, String permissionName) {
        int result = ctx.checkCallingOrSelfPermission(permissionName);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check in AndroidManifestFile if user has defined a specified uses-feature
     * @param ctx
     *            current application context
     * @param permissionName
     *            permission (ex: Manifest.permission.WRITE_EXTERNAL_STORAGE)
     * @return
     */
    public boolean doesUserHaveUsesFeature(Context ctx, String permissionName) {
        return ctx.getPackageManager().hasSystemFeature(permissionName);
    }
}
