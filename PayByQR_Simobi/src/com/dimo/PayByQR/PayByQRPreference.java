package com.dimo.PayByQR;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Rhio on 10/28/15.
 */
public class PayByQRPreference {
    private static final String DIMO_PREF = "com.dimo.PayByQR.Preference";
    //private static final String DIMO_PREF_USERKEY = "com.dimo.PayByQR.Preference.UserKey";
    private static final String DIMO_PREF_EULASTATE = "com.dimo.PayByQR.Preference.EULAState";

    /*public static void saveUserAPIKeyPref(Context context, String userAPIKey){
        SharedPreferences prefs = context.getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DIMO_PREF_USERKEY, userAPIKey);
        editor.commit();
    }

    public static String readUserAPIKeyPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
        return prefs.getString(DIMO_PREF_USERKEY, null);
    }*/

    public static void saveEULAStatePref(Context context, boolean eulaState){
        SharedPreferences prefs = context.getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(DIMO_PREF_EULASTATE, eulaState);
        editor.commit();
    }

    public static boolean readEULAStatePrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(DIMO_PREF_EULASTATE, false);
    }

    /*private static boolean isDebugMode;
    private static boolean isPolling;
    private static boolean isUsingCustomDialog;
    private static PayByQRSDK.ServerURL serverURL;*/
}
