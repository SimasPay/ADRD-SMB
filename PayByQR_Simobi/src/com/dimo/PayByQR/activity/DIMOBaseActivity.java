package com.dimo.PayByQR.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;

import java.util.Locale;

/**
 * Created by Rhio on 5/26/16.
 */
public class DIMOBaseActivity extends AppCompatActivity {
    private Locale locale = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = "in";
        if(PayByQRProperties.getSDKLocale() == PayByQRSDK.SDKLocale.ENGLISH) {
            lang = "en";
        } else if(PayByQRProperties.getSDKLocale() == PayByQRSDK.SDKLocale.INDONESIAN) {
            lang = "in";
        }

        if (!config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
