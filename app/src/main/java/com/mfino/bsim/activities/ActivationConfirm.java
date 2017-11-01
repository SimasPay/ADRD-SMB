package com.mfino.bsim.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ActivationConfirm extends Activity {

	private Button btn_confirm, btn_cancel;
	private TextView tvConfirmMsg, aditionalInfo;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	SharedPreferences languageSettings;
	String selectedLanguage; 
	Context context;

	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);
		context=this;
		System.out.println("Testing>>Confirmation Screen>");

		// Header code...
		/*View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		screeTitle.setText("CONFIRM");
		Button back = (Button) headerContainer.findViewById(R.id.back);
		Button home = (Button) headerContainer.findViewById(R.id.home_button);
		aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});
		
		back.setVisibility(View.GONE);
		
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(ActivationConfirm.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
*/
		bundle = getIntent().getExtras();
		aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		btn_confirm = (Button) findViewById(R.id.confirmButton);
		btn_cancel = (Button) findViewById(R.id.cancelButton);

		tvConfirmMsg = (TextView) findViewById(R.id.tv_Confirm_info);
		alertbox = new AlertDialog.Builder(ActivationConfirm.this, R.style.MyAlertDialogStyle);
		tvConfirmMsg.setText(bundle.getString("MSG"));
		aditionalInfo.setVisibility(View.GONE);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			//screeTitle.setText(getResources().getString(R.string.eng_confirm));
			btn_confirm.setText(getResources().getString(R.string.eng_confirm));
			btn_cancel.setText(getResources().getString(R.string.eng_cancel));

		} else {
			
			//screeTitle.setText(getResources().getString(R.string.bahasa_confirm));
			btn_confirm.setText(getResources().getString(R.string.bahasa_confirm));
			btn_cancel.setText(getResources().getString(R.string.bahasa_cancel));

		}
		
		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Success with mfa otp
				/**
				 * Set Service Parameters for Change
				 * PIN
				 */

				System.out.println("MFA MODE"+bundle.getString("MFA_MODE"));
				
			       if(bundle.getString("ACTIVATION_TYPE").equals("Activation")){
			        	 valueContainer = new ValueContainer();
						 valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
						 valueContainer.setSourceMdn(bundle.getString("MDN"));
						 valueContainer.setTransactionName(Constants.TRANSACTION_ACTIVATION);
						 valueContainer.setOTP(bundle.getString("ACTIVATION_OTP"));
						 valueContainer.setActivationOTP(bundle.getString("ACTIVATION_OTP"));
						 valueContainer.setActivationConfirmPin(bundle.getString("PIN"));
						 valueContainer.setActivationNewPin(bundle.getString("PIN"));
						
			        	
			        }else{
			        	
			        	 valueContainer = new ValueContainer();
						 valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
						 valueContainer.setSourceMdn(bundle.getString("MDN"));
						 valueContainer.setTransactionName(Constants.TRANSACTION_REACTIVATION);
						 valueContainer.setCardPan(bundle.getString("CARD_PAN"));
						 valueContainer.setSourcePin(bundle.getString("SOURCE_PIN"));
						 valueContainer.setActivationNewPin(bundle.getString("PIN"));
						 valueContainer.setActivationConfirmPin(bundle.getString("PIN"));
						 
			        }
			       System.out.println("Testing>>>>mfa Otp>"+bundle.getString("MFA_MODE"));
			       	valueContainer.setOTP(bundle.getString("ACTIVATION_OTP"));
					valueContainer.setMfaMode(bundle.getString("MFA_MODE"));
					valueContainer.setMfaOTP(bundle.getString("OTP"));
					valueContainer.setSctl(bundle.getString("SCTL"));
				
				
				final WebServiceHttp webServiceHttp= new WebServiceHttp(valueContainer,ActivationConfirm.this);

				final ProgressDialog dialog = ProgressDialog.show(ActivationConfirm.this,"  Bank Sinarmas               ","Loading....   ",true);

				final Handler handler = new Handler() {

					public void handleMessage(
							Message msg) {

						if (responseXml != null) {

							XMLParser obj = new XMLParser();
							/**
							 * Parsing the Xml
							 * Response
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
									
									alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));

								} else {
									
									alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
								}

								alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0,int arg1) {

											}
										});
								alertbox.show();
								
							} else if (responseContainer.getMsgCode().equals("2033")||responseContainer.getMsgCode().equals("52")) {
								
								Constants.SOURCE_MDN_PIN = bundle.getString("NPIN");
								Intent intent = new Intent(ActivationConfirm.this,ActivationConfirmation.class);
								intent.putExtra("MSG",responseContainer.getMsg());
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							} else {

								alertbox.setMessage(responseContainer.getMsg());
								alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0,int arg1) {
												finish();

											}
										});
								alertbox.show();
							}

						} else {
							
							dialog.dismiss();
							if (selectedLanguage.equalsIgnoreCase("ENG")) {
								alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
							} else {
								alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
							}

							alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface arg0,int arg1) {

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

				Intent intent = new Intent(ActivationConfirm.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

}
