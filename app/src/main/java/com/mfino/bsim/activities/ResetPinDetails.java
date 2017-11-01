package com.mfino.bsim.activities;

import android.annotation.SuppressLint;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.handset.security.CryptoService;

/** @author pramod */

public class ResetPinDetails extends Activity {
	/** Called when the activity is first created. */
	private Button okButton;
	private EditText otp, pin, confirmPin;
	private AlertDialog.Builder alertbox;
	private ValueContainer valueContainer;
	private Bundle bundle;
	TextView resentOTP;
	private String responseXml;
	SharedPreferences languageSettings,encrptionKeys;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reset_pin_details);
		context=this;

		// Header code...
		TextView screeTitle = (TextView) findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) findViewById(R.id.back);
		// Button home=(Button)headerContainer.findViewById(R.id.home_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		/*
		 * home.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub startActivity(new
		 * Intent(ActivationDetails.this,HomeScreen.class)); } });
		 */

		bundle = getIntent().getExtras();
		otp = (EditText) findViewById(R.id.otpEditText);
		pin = (EditText) findViewById(R.id.mpinEditText);
		confirmPin = (EditText) findViewById(R.id.remPinEditText);
		okButton = (Button) findViewById(R.id.okButton);
		alertbox = new AlertDialog.Builder(ResetPinDetails.this, R.style.MyAlertDialogStyle);
		
		TextView textViewinNewPin=(TextView)findViewById(R.id.textView_newPin);
		TextView textVieConfirmPin=(TextView)findViewById(R.id.textView_confirmNewPin);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES",	0);
		//Language Option..
				languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
				selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
				
				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					
					screeTitle.setText(getResources().getString(R.string.eng_resetPin));
					textViewinNewPin.setText(getResources().getString(R.string.eng_newPin));
					textVieConfirmPin.setText(getResources().getString(R.string.eng_confimPin));
					back.setBackgroundResource(R.drawable.back_button);
					okButton.setText(getResources().getString(R.string.eng_submit));

				} else {
					
					screeTitle.setText(getResources().getString(R.string.bahasa_resetPin));
					textViewinNewPin.setText(getResources().getString(R.string.bahasa_newPin));
					textVieConfirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
					back.setBackgroundResource(R.drawable.back_button);
					okButton.setText(getResources().getString(R.string.bahasa_submit));
				}
		okButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("HandlerLeak")
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
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

								}
							});
					alertbox.show();

				} /*else if (otp.getText().toString().length() < 8) {
					alertbox.setMessage("Pin Number must be 6 digits long. ");
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
				}*/ else if (pin.getText().toString().length() < 6) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				} else if (confirmPin.getText().toString().length() < 6) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_confirmPinLenth));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_confirmPinLenth));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				}else if(!(pin.getText().toString().equals(confirmPin.getText().toString())))
				{
					 if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_mPinNotMatch));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_mPinNotMatch));
						}
	                 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
	                  public void onClick(DialogInterface arg0, int arg1) {
	                    
	       		      }
	                 });
	                 alertbox.show();
				} else {

					/**
					 * Call Activation Disclosure here and pass value of Mobile
					 * Number, Activation Key and PIN
					 */
					
					String module=encrptionKeys.getString("MODULE", "NONE");
					String exponent=encrptionKeys.getString("EXPONENT", "NONE");
					String confirmPinKey=CryptoService.encryptWithPublicKey(module, exponent,confirmPin.getText().toString().trim().getBytes());
					String newPin=CryptoService.encryptWithPublicKey(module, exponent, pin.getText().toString().trim().getBytes());
					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					valueContainer.setSourceMdn(bundle.getString("MDN"));
					valueContainer.setTransactionName(Constants.TRANSACTION_RESET_PIN);
					valueContainer.setOTP(otp.getText().toString().trim());
					
					//Without RSA
					/* valueContainer.setActivationConfirmPin(bundle.getString("PIN"));
					 valueContainer.setActivationNewPin(bundle.getString("PIN"));*/
					 //RSA
					 valueContainer.setActivationConfirmPin(newPin);
					 valueContainer.setActivationNewPin(newPin);

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ResetPinDetails.this);


					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = new ProgressDialog(ResetPinDetails.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.eng_loading));
						dialog.show();
					} else {
						dialog = new ProgressDialog(ResetPinDetails.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.bahasa_loading));
						dialog.show();
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {

								XMLParser obj = new XMLParser();

								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
								} catch (Exception e) {

									e.printStackTrace();
								}

								dialog.dismiss();

								if (responseContainer.getMsg() == null) {
									if (selectedLanguage.equalsIgnoreCase("ENG")) {
										alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
									} else {
										alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
									}
									alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface arg0,int arg1) {

										}
									});
									alertbox.show();

								} else {

									Intent intent = new Intent(ResetPinDetails.this,ActivationConfirmation.class);
									intent.putExtra("MSG",responseContainer.getMsg());
									intent.putExtra("SCREEN", "ResetPin");
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
									public void onClick(DialogInterface arg0,int arg1) {

									}
								});
								alertbox.show();
							}

						}
					};

					final Thread checkUpdate = new Thread() {

						public void run() {

							try {
								responseXml = webServiceHttp.getResponseSSLCertificatation();
								/** Service call for Activation */
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

	private boolean isRequiredFieldEmpty() {

		otp = (EditText) findViewById(R.id.otpEditText);
		pin = (EditText) findViewById(R.id.mpinEditText);
		confirmPin = (EditText) findViewById(R.id.remPinEditText);
		if (!(otp.getText().toString().equals(""))&& !(pin.getText().toString().equals(""))	&& !(confirmPin.getText().toString().equals(""))) {
			return false;
		} else {

			return true;
		}
	}

	@SuppressLint("HandlerLeak")
	public void resendOTP() {

		/** Set Parameters for Activation Service. */

		valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setSourceMdn(bundle.getString("MDN"));
		valueContainer.setTransactionName(Constants.TRANSACTION_RESEND_OTP);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(
				valueContainer, ResetPinDetails.this);

		final ProgressDialog dialog = ProgressDialog.show(ResetPinDetails.this,
				"  Bank Sinarmas               ", "Loading....   ", true);

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {

					XMLParser obj = new XMLParser();

					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						System.out.println("Testing>>>Message>>Activation>>"
								+ responseContainer.getMsg());
					} catch (Exception e) {

						e.printStackTrace();
					}

					dialog.dismiss();

					if (responseContainer.getMsg() == null) {
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
						}

						alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0,int arg1) {

							}
						});
						alertbox.show();

					} else {

						/*
						 * Intent intent = new Intent( ActivationDetails.this,
						 * ActivationConfirmation.class); intent.putExtra("MSG",
						 * responseContainer .getMsg());
						 * intent.putExtra("SCREEN", "OTP");
						 * startActivity(intent);
						 */
						dialog.dismiss();
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_checkSMS));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_checkSMS));
						}
						alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0,int arg1) {

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
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				}

			}
		};

		final Thread checkUpdate = new Thread() {

			public void run() {

				try {
					responseXml = webServiceHttp
							.getResponseSSLCertificatation();
					/** Service call for Activation */
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();

	}

}
