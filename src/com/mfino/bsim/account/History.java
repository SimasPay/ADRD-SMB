package com.mfino.bsim.account;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfino.bsim.Confirmation_History;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.transfer.ToBankSinarmas;

public class History extends Activity {

	public static final String BANK_HISTORY_MSGCODE = "67";
	public static final String EMONEY_HISTORY_MSGCODE = "39";
	public static final String EMONEY_BALACE_MSGCODE = "274";
	public static final String BANK_BALACE_MSGCODE = "4";
	private Button btn_ok;
	private EditText pinValue;
	private Bundle bundle;
	private AlertDialog.Builder alertbox;
	ValueContainer valueContainer;
	public String responseXml = null;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_pin);
		context=this;

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(History.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		bundle = getIntent().getExtras();

		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_history));
			home.setBackgroundResource(R.drawable.home_icon1);
			back.setBackgroundResource(R.drawable.back_button);

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_history));
			//home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			//back.setBackgroundResource(R.drawable.bahasa_back_button);

		}
		
		alertbox = new AlertDialog.Builder(History.this, R.style.MyAlertDialogStyle);

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_noInterent), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_noInternet), context);
					}
									
				}else if (isRequiredFieldEmpty()) {

				 if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

								}
							});
					alertbox.show();

				} else {

					/** Set Parameters for service calling. */
					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_BANK);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setSourcePin(pinValue.getText().toString());
					valueContainer.setTransactionName(Constants.TRANSACTION_HISTORY);
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, History.this);


					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = new ProgressDialog(History.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.eng_loading));
						dialog.show();
					} else {
						dialog = new ProgressDialog(History.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.bahasa_loading));
						dialog.show();}
					
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {

								XMLParser obj = new XMLParser();
								/** Parsing of response. */
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

													Intent intent = new Intent(getBaseContext(),HomeScreen.class);
													intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(intent);
													pinValue.setText("");

												}
											});
									alertbox.show();

								} else if (!responseContainer.getMsgCode()
										.equals(BANK_HISTORY_MSGCODE)) {

									alertbox.setMessage(responseContainer
											.getMsg());
									alertbox.setNeutralButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface arg0,
														int arg1) {

													Intent intent = new Intent(
															getBaseContext(),
															HomeScreen.class);
													intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(intent);
													pinValue.setText("");

												}
											});
									alertbox.show();

								} else {

									System.out.println("Testing>History>>"+responseContainer.getMsg());
									Intent intent = new Intent(History.this,
											Confirmation_History.class);
									intent.putExtra("MSG",
											responseContainer.getMsg());
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}

								pinValue.setText("");

							}else {


								dialog.dismiss();
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
								}
								alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,int arg1) {

									}
								});
								alertbox.show();
							
							}
							
							

						}
					};

					final Thread checkUpdate = new Thread() {
						/**
						 * Service call in thread in and getting response as xml
						 * in string.
						 */
						public void run() {

							try {
								responseXml = webServiceHttp
										.getResponseSSLCertificatation();
							} catch (Exception e) {
								responseXml = null;
							}
							handler.sendEmptyMessage(0);
						}
					};
					checkUpdate.start();

				}

			}
		});

	}

	/** Empty Field Validation */
	private boolean isRequiredFieldEmpty() {

		pinValue = (EditText) findViewById(R.id.ed_pinValue);

		if (!(pinValue.getText().toString().equals(""))) {
			return false;
		} else {
			return true;
		}
	}

}
