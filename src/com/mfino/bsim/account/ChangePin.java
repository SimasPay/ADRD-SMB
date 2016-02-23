package com.mfino.bsim.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
public class ChangePin extends Activity{
	
	
	private Button btn_ok;
	private EditText oldpinValue, newpinValue, confirmNewPinValue;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	public String otpValue, sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	Context context;
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pin);
		context=this;
		
		//Header code...
		 View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	     screeTitle.setText("CHANGE PIN");
	     Button back=(Button)headerContainer.findViewById(R.id.back);
	     Button home=(Button)headerContainer.findViewById(R.id.home_button);
	     
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	     home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ChangePin.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		oldpinValue = (EditText)findViewById(R.id.oldPinEditText);
		newpinValue = (EditText)findViewById(R.id.newpinEditText);
		confirmNewPinValue = (EditText)findViewById(R.id.reNewPinEditText);
		
		TextView lodPin=(TextView)findViewById(R.id.textView_oldPin);
		TextView newPin=(TextView)findViewById(R.id.textView_newPin);
		TextView confirmPin=(TextView)findViewById(R.id.textView_confirmNewPin);
		btn_ok = (Button) findViewById(R.id.okButton);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_changePin));
			lodPin.setText(getResources().getString(R.string.eng_oldPin));
			newPin.setText(getResources().getString(R.string.eng_newPin));
			confirmPin.setText(getResources().getString(R.string.eng_confimPin));
			home.setBackgroundResource(R.drawable.home_icon1);
			back.setBackgroundResource(R.drawable.back_button);
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_changePin));
			lodPin.setText(getResources().getString(R.string.bahasa_oldPin));
			newPin.setText(getResources().getString(R.string.bahasa_newPin));
			confirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
			//home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));


		}
		
		alertbox = new AlertDialog.Builder(this);

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

					alertbox.setMessage(" Fields can't be empty ");
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener()
					{
								public void onClick(DialogInterface arg0,int arg1) {

								}
							});
					alertbox.show();

				} else if (!newpinValue.getText().toString().equals(confirmNewPinValue.getText().toString())) 
				{
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_mPinNotMatch));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_mPinNotMatch));
					}
					
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

									newpinValue.setText("");
									confirmNewPinValue.setText("");

								}
							});
					alertbox.show();
					
				} else if (newpinValue.getText().length() < 4) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

									newpinValue.setText("");
									confirmNewPinValue.setText("");

								}
							});
					alertbox.show();
				} else {



					/** Set Service Parameters for Change PIN */

					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					valueContainer.setTransactionName(Constants.TRANSACTION_CHANGEPIN);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setSourcePin(oldpinValue.getText().toString());
					valueContainer.setNewPin(newpinValue.getText().toString());
					valueContainer.setConfirmPin(newpinValue.getText().toString());

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ChangePin.this);

					final ProgressDialog dialog = ProgressDialog.show(ChangePin.this, "  Banksinarmas               ","Loading....   ", true);

					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {

								XMLParser obj = new XMLParser();
								/** Parsing the Xml Response */
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
									alertbox.setNeutralButton("OK",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0,int arg1) {

												}
											});
									alertbox.show();
								} else if (responseContainer.getMsgCode().equals("2039")) {
									
									
									//Constants.SOURCE_MDN_PIN = oldpinValue.getText().toString();
									dialog.dismiss();

									try {
										System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
										valueContainer.setMfaMode(responseContainer.getMfaMode());
									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}
									
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										
										/********************************************** 2FA factor code start ****************************************************************/
										
										try {

											final ProgressDialog dialog1 = ProgressDialog.show(ChangePin.this,"  Banksinarmas               ","Please Wait for SMS....   ",true);
											Long startTimeInMillis = new java.util.Date().getTime();

											while (true) {
												
												Thread.sleep(2000);
												System.out.println("Testing>>inside Loop");
												final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
												Cursor c = getContentResolver().query(SMS_INBOX, null,null, null,"DATE desc");

												c.moveToFirst();
												for (int i = 0; i < 10; i++) {
													String body = c.getString(c.getColumnIndexOrThrow("body")).toString().trim();
													String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
													
													if(body.contains("Kode Simobi Anda")&&body.contains(responseContainer.getSctl())){
														
														otpValue=body.substring(new String("Kode Simobi Anda ").length(), body.indexOf("(no ref"));
														sctl=body.substring(body.indexOf(":")+1, body.indexOf(")"));
														break;
														
													}else if(body.contains("Your Simobi Code is")&&body.contains(responseContainer.getSctl())){
														
														otpValue=body.substring(new String("Your Simobi Code is ").length(), body.indexOf("(ref"));
														sctl=body.substring(body.indexOf("(ref no: ")+new String("(ref no: ").length(), body.indexOf(")"));
														break;
													}else{
														c.moveToNext();
													}
													
												}
												c.close();

												if (!(otpValue == null)) {
													System.out.println("Testing>>SCTL");
													break;
												} else {

													System.out.println("Testing>>SCTL>>else");
													
													if (new java.util.Date().getTime()- startTimeInMillis >= 60000) {
														
														System.out.println("Testing>>TimeOut>>");
														break;
													}

												}

											}
											System.out.println("Testing>>OTP>>"+ otpValue);
											if (otpValue == null) {
												
												dialog1.dismiss();
												System.out.println("Testing>>OTP>>null");
												if (selectedLanguage.equalsIgnoreCase("ENG")) {
													alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
												} else {
													alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
												}
												alertbox.setNeutralButton(
														"OK",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																finish();
															}
														});
												alertbox.show();
											} else {
												dialog1.dismiss();
												
												Intent intent = new Intent(ChangePin.this,ChangePinConfirm.class);
												intent.putExtra("MSG",responseContainer.getMsg());
												intent.putExtra("OTP",otpValue);
												intent.putExtra("SCTL",responseContainer.getSctl());
												intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
												intent.putExtra("OPIN",oldpinValue.getText().toString());
												intent.putExtra("NPIN",newpinValue.getText().toString());
												intent.putExtra("CONFIRM_NPIN",newpinValue.getText().toString());
												startActivity(intent);
											}

										} catch (Exception e) {
											// dialog1.dismiss();
											System.out.println("Testing>>exception>>>>>"+e);
										}
										
										/********************************************** 2FA factor code end ****************************************************************/
										
									} else {

										Constants.SOURCE_MDN_PIN = bundle.getString("NPIN");
										System.out.println("Testing>>Without OTP>>"+responseContainer.getMsg());
										Intent intent = new Intent(ChangePin.this,ConfirmationScreen.class);
										intent.putExtra("MSG",responseContainer.getMsg());
										intent.putExtra("OPIN",oldpinValue.getText().toString());
										intent.putExtra("NPIN",newpinValue.getText().toString());
										intent.putExtra("CONFIRM_NPIN",confirmNewPinValue.getText().toString());
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);

									}

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
											public void onClick(DialogInterface arg0, int arg1) {

												finish();
											}
										});
								alertbox.show();
							}
						}
					};

					final Thread checkUpdate = new Thread() {
						/** Service call and getting response as XML in String. */
						public void run() {
							System.out.println("Testing>>>Thread");
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
			}
		});

	}
	
    /** Empty field validation. */
	private boolean isRequiredFieldEmpty() {

		oldpinValue = (EditText) findViewById(R.id.oldPinEditText);
		newpinValue = (EditText) findViewById(R.id.newpinEditText);
		confirmNewPinValue = (EditText) findViewById(R.id.reNewPinEditText);

		if (!(oldpinValue.getText().toString().equals(""))&& !(newpinValue.getText().toString().equals(""))	&& !(confirmNewPinValue.getText().toString().equals(""))) {
			return false;
		} else {
			return true;
		}
	}
}
