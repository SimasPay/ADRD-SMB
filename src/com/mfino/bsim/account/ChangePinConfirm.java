package com.mfino.bsim.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ChangePinConfirm extends Activity {

	private Button btn_confirm, btn_cancel;
	private TextView tvConfirmMsg, aditionalInfo;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	SharedPreferences languageSettings, settings;
	String selectedLanguage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);
		System.out.println("Testing>>Confirmation Screen>");

		// Header code...
		/*
		 * View headerContainer = findViewById(R.id.header); TextView screeTitle
		 * = (TextView) headerContainer.findViewById(R.id.screenTitle);
		 * screeTitle.setText("CONFIRM"); Button back = (Button)
		 * headerContainer.findViewById(R.id.back); Button home = (Button)
		 * headerContainer.findViewById(R.id.home_button); aditionalInfo =
		 * (TextView) findViewById(R.id.aditional_info);
		 * 
		 * back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * finish(); } }); back.setVisibility(View.GONE);
		 * 
		 * home.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * Intent intent=new Intent(ChangePinConfirm.this,HomeScreen.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 */
		aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		bundle = getIntent().getExtras();

		btn_confirm = (Button) findViewById(R.id.confirmButton);
		btn_cancel = (Button) findViewById(R.id.cancelButton);
		tvConfirmMsg = (TextView) findViewById(R.id.tv_Confirm_info);
		alertbox = new AlertDialog.Builder(ChangePinConfirm.this, R.style.MyAlertDialogStyle);
		tvConfirmMsg.setText(bundle.getString("MSG"));
		aditionalInfo.setVisibility(View.GONE);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			// screeTitle.setText(getResources().getString(R.string.eng_confirm));
			btn_confirm.setText(getResources().getString(R.string.eng_confirm));
			btn_cancel.setText(getResources().getString(R.string.eng_cancel));

		} else {

			// screeTitle.setText(getResources().getString(R.string.bahasa_confirm));
			// home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			btn_confirm.setText(getResources().getString(R.string.bahasa_confirm));
			btn_cancel.setText(getResources().getString(R.string.bahasa_cancel));

		}

		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {

				// Success with mfa otp
				/**
				 * Set Service Parameters for Change PIN
				 */
				System.out.println("MFA MODE" + bundle.getString("MFA_MODE"));
				valueContainer = new ValueContainer();
				valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
				valueContainer.setTransactionName(Constants.TRANSACTION_CHANGEPIN);
				valueContainer.setMfaMode(bundle.getString("MFA_MODE"));
				valueContainer.setSctl(bundle.getString("SCTL"));
				settings = getSharedPreferences("LOGIN_PREFERECES", 0);
				String mobileNumber = settings.getString("mobile", "");
				valueContainer.setSourceMdn(mobileNumber);
				//valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
				valueContainer.setSourcePin(bundle.getString("OPIN"));
				valueContainer.setNewPin(bundle.getString("NPIN"));
				valueContainer.setConfirmPin(bundle.getString("CONFIRM_NPIN"));
				valueContainer.setOTP(bundle.getString("OTP"));
				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ChangePinConfirm.this);

				final ProgressDialog dialog = new ProgressDialog(ChangePinConfirm.this, R.style.MyAlertDialogStyle);
				dialog.setCancelable(false);
				dialog.setTitle("Bank Sinarmas");
				dialog.setMessage("Loading....   ");
				dialog.show();
				
				final Handler handler = new Handler() {

					public void handleMessage(Message msg) {

						if (responseXml != null) {

							XMLParser obj = new XMLParser();
							/**
							 * Parsing the Xml Response
							 */
							EncryptedResponseDataContainer responseContainer = null;
							try {
								responseContainer = obj.parse(responseXml);
							} catch (Exception e) {

								// //e.printStackTrace();
							}

							dialog.dismiss();

							if (responseContainer.getMsg() == null) {

								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_transactionFail));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_transactionFail));
								}
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {

									}
								});
								alertbox.show();
							} else if (responseContainer.getMsgCode().equals("26")) {
								settings = getSharedPreferences("LOGIN_PREFERECES", 0);
								settings.edit().putString("pin", bundle.getString("NPIN")).commit();
								System.out.println("New Pin is>>" + settings.getString("NPIN", ""));
								Constants.SOURCE_MDN_PIN = bundle.getString("NPIN");
								Intent intent = new Intent(ChangePinConfirm.this, ConfirmationScreen.class);
								intent.putExtra("MSG", responseContainer.getMsg());
								intent.putExtra("SCREEN", "ChangePin");
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								settings.edit().putBoolean("alreadyChangePin", true).commit();
							} else {
								if(responseContainer.getMsgCode().equals("2000")){
									if (selectedLanguage.equalsIgnoreCase("ENG")) {
										alertbox.setMessage("You have entered incorrect code. Please try again and ensure that you enter the correct code.");
									} else {
										alertbox.setMessage("Kode yang Anda masukkan salah. Silakan coba lagi dan pastikan Anda memasukkan kode yang benar.");
									}
									Bundle extras = getIntent().getExtras();
									String required= extras.getString("REQUIRED");
									if(required.equals("yes")){
										alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int arg1) {
												dialog.dismiss();
												finish();
												Intent intent = new Intent(ChangePinConfirm.this, ChangePin.class);
												intent.putExtra("mdn", settings.getString("mobile", ""));
												intent.putExtra("REQUIRED", "yes");
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											}
										});
									}else{
										alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int arg1) {
												dialog.dismiss();
												finish();
											}
										});
									}
								}else{
									alertbox.setMessage(responseContainer.getMsg());
									alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int arg1) {
											dialog.dismiss();
											finish();
										}
									});
								}
								
								alertbox.show();
							}

						} else {

							dialog.dismiss();
							if (selectedLanguage.equalsIgnoreCase("ENG")) {
								alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
							} else {
								alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
							}
							alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {

									finish();
								}
							});
							alertbox.show();
						}
					}
				};

				final Thread checkUpdate = new Thread() {

					public void run() {
						/** Service calling in thread. */
						try {
							responseXml = webServiceHttp.getResponseSSLCertificatation();
						} catch (Exception e) {
							responseXml = null;
						}
						handler.sendEmptyMessage(0);
					}
				};
				checkUpdate.start();

			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(ChangePinConfirm.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

}
