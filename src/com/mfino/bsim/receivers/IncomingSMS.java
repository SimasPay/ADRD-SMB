package com.mfino.bsim.receivers;

import java.util.Locale;

import com.mfino.bsim.account.ChangePin;
import com.mfino.bsim.billpayment.PaymentDetails;
import com.mfino.bsim.flashiz.QRPayment2;
import com.mfino.bsim.purchase.PurchaseDetails;
import com.mfino.bsim.transfer.ToBankSinarmas;
import com.mfino.bsim.transfer.ToOtherBankDetails;
import com.mfino.bsim.transfer.TransferToUangku;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSMS extends BroadcastReceiver {

	public static final String LOG_TAG = "SIMOBI-TO-BankSinarmas";
	private SharedPreferences settings;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive");
		settings = context.getSharedPreferences("LOGIN_PREFERECES",	0);
		String fragName = settings.getString("ActivityName", "");
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
					try {
						//if (message.contains("Kode Simobi Anda") && message.contains(sctl)) {
						if (message.contains("Kode Simobi Anda ") || message.contains("Your Simobi Code is ")
								|| message.toLowerCase(Locale.getDefault()).contains("kode simobi anda ") 
								|| message.toLowerCase(Locale.getDefault()).contains("your simobi code is ")) {
							settings.edit().putBoolean("isAutoSubmit", true).commit();
							if (fragName.equals("ToBankSinarmas")) {
								Log.d(LOG_TAG, "SMS diterima utk OTP BankSinarmas");
								ToBankSinarmas Sms = new ToBankSinarmas();
								Sms.recivedSms(message);
							}else if(fragName.equals("ToOtherBankDetails")){
								Log.d(LOG_TAG, "SMS diterima utk OTP BankLainnya");
								ToOtherBankDetails Sms = new ToOtherBankDetails();
								Sms.recivedSms(message);
							}else if(fragName.equals("TransferToUangku")){
								Log.d(LOG_TAG, "SMS diterima utk OTP TransferToUangku");
								TransferToUangku Sms = new TransferToUangku();
								Sms.recivedSms(message);
							}else if(fragName.equals("PurchaseDetails")){
								Log.d(LOG_TAG, "SMS diterima utk OTP PurchaseDetails");
								PurchaseDetails Sms = new PurchaseDetails();
								Sms.recivedSms(message);
							}else if(fragName.equals("ChangePin")){
								Log.d(LOG_TAG, "SMS diterima utk OTP ChangePin");
								ChangePin Sms = new ChangePin();
								Sms.recivedSms(message);
							}else if(fragName.equals("PaymentDetails")){
								Log.d(LOG_TAG, "SMS diterima utk OTP PaymentDetails");
								PaymentDetails Sms = new PaymentDetails();
								Sms.recivedSms(message);
							}else if(fragName.equals("QRPayment2")){
								Log.d(LOG_TAG, "SMS diterima utk OTP QRPayment2");
								QRPayment2 Sms = new QRPayment2();
								Sms.recivedSms(message);
							}
						}
					} catch (Exception e) {
					}

				}
			}

		} catch (Exception e) {

		}
	}

}