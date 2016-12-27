package com.mfino.bsim.receivers;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

@TargetApi(23)
public class IncomingSMS extends BroadcastReceiver {

	private static final String LOG_TAG = "SIMOBI";
	private static SharedPreferences settings;
	public static AutoReadSMSListener listener;
	private static String otpValue, sctl;
	
    public static void setListener(AutoReadSMSListener listener) {
    	IncomingSMS.listener = listener;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive");
		settings = context.getSharedPreferences("LOGIN_PREFERECES",	0);
		//String sctl = settings.getString("Sctl", "");
		final Bundle bundle = intent.getExtras();
		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				Object [] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdusObj.length; i++) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		                String format = bundle.getString("format");
		                messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
		            }
		            else {
		            	messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		            }
					String message = messages[i].getMessageBody();
					Log.d(LOG_TAG, "msg : " + message);
					try {
						if (message.toLowerCase(Locale.getDefault()).contains("kode simobi anda ") 
								|| message.toLowerCase(Locale.getDefault()).contains("your simobi code is ")) {
							settings.edit().putBoolean("isAutoSubmit", true).commit();
								otpValue = message
										.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
										.trim();
								sctl = message.substring(message.indexOf(":") + 1, message.indexOf(")"));

							Log.d(LOG_TAG, "OPT code : " + otpValue + ", sctl : " + sctl);
							if (listener != null) {
                                listener.onReadSMS(otpValue);
                            }
							
						}else{
							settings.edit().putBoolean("isAutoSubmit", false).commit();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		} catch (Exception e) {
			Log.d(LOG_TAG, "Error: " + e);
		}
	}
	
	public interface AutoReadSMSListener {
        void onReadSMS(String otp);
    }


}