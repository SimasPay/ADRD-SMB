package com.mfino.bsim.billpayment;

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

import com.mfino.bsim.activities.ConfirmationScreen;
import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class BillPaymentConfirm extends Activity {

	private String responseXml;
	ValueContainer valueContainer;
	private Bundle bundle;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);

		// Header code...
		/*View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		Button back = (Button) headerContainer.findViewById(R.id.back);
		Button home = (Button) headerContainer.findViewById(R.id.home_button);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		back.setVisibility(View.GONE);
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BillPaymentConfirm.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});*/

		bundle = getIntent().getExtras();
		TextView tvConfirmInfo = findViewById(R.id.tv_Confirm_info);
		TextView aditionalInfo = findViewById(R.id.aditional_info);
		Button btn_confirm = findViewById(R.id.confirmButton);
		Button btn_cancel = findViewById(R.id.cancelButton);
		//LinearLayout ccPaymentLayout=(LinearLayout)findViewById(R.id.isCreditCardLayout);
		alertbox = new AlertDialog.Builder(BillPaymentConfirm.this, R.style.MyAlertDialogStyle);
   /*     if (bundle.getBoolean("IS_CCPAYMENT")) {
			ccPaymentLayout.setVisibility(View.VISIBLE);
			RadioButton check=(RadioButton)findViewById(R.id.radioButton1);
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					
				}
			});
		}*/
		tvConfirmInfo.setText(bundle.getString("MSG"));
		
		//Language Option..
			languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",0);
			selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage != null) {
			if (selectedLanguage.equalsIgnoreCase("ENG")) {

				//screeTitle.setText(getResources().getString(R.string.eng_confirm));
				/*btn_confirm.setBackgroundResource(R.drawable.confirm);
				btn_cancel.setBackgroundResource(R.drawable.cancel);*/
				btn_confirm.setText(getResources().getString(R.string.eng_confirm));
				btn_cancel.setText(getResources().getString(R.string.eng_cancel));
				/*home.setBackgroundResource(R.drawable.home_icon1);
				back.setBackgroundResource(R.drawable.back_button);*/

			} else {

				//screeTitle.setText(getResources().getString(R.string.bahasa_confirm));
				/*btn_confirm.setBackgroundResource(R.drawable.bahasa_confirm);
				btn_cancel.setBackgroundResource(R.drawable.bahasa_cancel);*/

				btn_confirm.setText(getResources().getString(R.string.eng_confirm));
				btn_cancel.setText(getResources().getString(R.string.eng_cancel));
				/*home.setBackgroundResource(R.drawable.bahasa_home_icon1);
				back.setBackgroundResource(R.drawable.bahasa_back_button);*/

			}
		}

		try {
			if (bundle.getString("ADITIONAL_INFO").length() <= 0|| bundle.getString("ADITIONAL_INFO").equalsIgnoreCase("null")) {

				aditionalInfo.setVisibility(View.GONE);

			} else {
				
				aditionalInfo.setVisibility(View.VISIBLE);
				String adInfo = bundle.getString("ADITIONAL_INFO");
				StringBuilder sb = new StringBuilder();
				String delimiter = "\\|";
				String temp[] = new String[0];
				if (adInfo != null) {
					temp = adInfo.split(delimiter);
				}

				for (String aTemp : temp) sb.append(aTemp).append("\n");

				aditionalInfo.setText(sb.toString());
			}
		} catch (Exception e) {

			aditionalInfo.setVisibility(View.GONE);
		}
		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {

				/* Set Parameters for Service calling . */
				valueContainer = new ValueContainer();
				
				valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
				valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT);
				valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
				valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
				valueContainer.setConfirmed("true");
				valueContainer.setAmount(bundle.getString("AMT"));
				valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
				valueContainer.setPaymentMode(bundle.getString("SELECTED_PAYMENT_MODE"));
				valueContainer.setBillNo(bundle.getString("BILLERNUM"));
				valueContainer.setParentTxnId(bundle.getString("PTFNID"));
				valueContainer.setTransferId(bundle.getString("TFNID"));
				
				try {
					if (bundle.getString("MFA_MODE").equalsIgnoreCase("OTP")) {
						
						valueContainer.setOTP(bundle.getString("OTP"));
						valueContainer.setMfaMode(bundle.getString("MFA_MODE"));
					}
				} catch (Exception ignored) {

				}


				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, BillPaymentConfirm.this);
				
				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					dialog = new ProgressDialog(BillPaymentConfirm.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.eng_loading));
					dialog.show();
				} else {
					dialog = new ProgressDialog(BillPaymentConfirm.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.bahasa_loading));
					dialog.show();
				}
				
				final Handler handler = new Handler() {

					public void handleMessage(Message msg) {

						if (responseXml != null) {
							/* Parse the response xml. */
							XMLParser obj = new XMLParser();
							EncryptedResponseDataContainer responseContainer = null;
							try {
								responseContainer = obj.parse(responseXml);
							} catch (Exception e) {

								e.printStackTrace();
							}

							dialog.dismiss();

							try {
								if (responseContainer != null) {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								}
							} catch (Exception e) {
								msgCode = 0;
							}

							if (responseXml == null||msgCode==0) {
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
								}
								alertbox.setNeutralButton("OK", (arg01, arg1) -> {

									Intent intent = new Intent(	getBaseContext(),PaymentHome.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);

								});
								alertbox.show();
							} else {
								Intent intent = new Intent(BillPaymentConfirm.this,ConfirmationScreen.class);
								intent.putExtra("MSG",responseContainer.getMsg());
								intent.putExtra("ADITIONAL_INFO",responseContainer.getAditionalInfo());
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}


						} else {

							dialog.dismiss();
							if (selectedLanguage.equalsIgnoreCase("ENG")) {
								alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
							} else {
								alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
							}
							alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface arg0, int arg1) {

											Intent intent = new Intent(	getBaseContext(),PaymentHome.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);

										}
									});
							alertbox.show();
						}

					}
				};

				final Thread checkUpdate = new Thread() {
					/** Service calling in this thread. */
					public void run() {

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

		/* Cancel button event handling. */
		btn_cancel.setOnClickListener(arg0 -> {

			Intent intent = new Intent(BillPaymentConfirm.this,HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		});
	}

}
