package com.mfino.bsim.utils;

import com.mfino.bsim.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class AndroidPermissions {

    /**
     * Id to identify a SMS permission request.
     */
    public static final int REQUEST_READ_SMS = 1;

    //private static String[] PERMISSIONS_READ_SMS = {Manifest.permission.READ_SMS};


    public static AndroidPermissions instance;

    public static AndroidPermissions getInstance() {
        if (instance == null) {
            instance = new AndroidPermissions();
        }
        return instance;
    }

    public boolean checkReadSmsPermission(Context thisActivity) {
        if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public void displaySmsPermissionAlert(Activity thisActivity) {
        // Should we show an explanation?
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void displayAlert(final Activity context, final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
      if (position == REQUEST_READ_SMS) {
            builder1.setTitle(context.getResources().getString(R.string.sms));
            builder1.setMessage(context.getResources().getString(R.string.sms_des));
        }
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                     if (position == REQUEST_READ_SMS) {
                            displaySmsPermissionAlert(context);
                        }
                    }
                });

        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
}