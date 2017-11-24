package com.mfino.bsim.activities;

import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.mfino.bsim.activities.HomeScreen.LOG_TAG;

public class ActivationHome extends Activity {
	/** Called when the activity is first created. */
	private AlertDialog.Builder alertbox;
	private String responseXml;
	EditText mdn, otp;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activation_medium);
		context = this;

		// Header code...
		// View headerContainer = findViewById(R.id.header);
		TextView screeTitle = findViewById(R.id.screenTitle);
		ImageButton back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				ActivationHome.this.finish();
			}
		});

		alertbox = new AlertDialog.Builder(ActivationHome.this, R.style.MyAlertDialogStyle);
		mdn = findViewById(R.id.mobileEditText);
		otp = findViewById(R.id.activationKeyEditText);
		Button activation = findViewById(R.id.okButton);
		Button resendOTP = findViewById(R.id.resendOTP);
		TextView mdnText = findViewById(R.id.textView2);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			mdnText.setText(getResources().getString(R.string.eng_mobileNumber));
			screeTitle.setText(getResources().getString(R.string.eng_activation));
			activation.setText(getResources().getString(R.string.eng_submit));
			back.setBackgroundResource(R.drawable.back_button);

		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_activation));
			back.setBackgroundResource(R.drawable.back_button);
			mdnText.setText(getResources().getString(R.string.bahasa_mobileNumber));
			activation.setText(getResources().getString(R.string.bahasa_submit));
		}

		resendOTP.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				resendOTP();
			}
		});


		activation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean networkCheck = ConfigurationUtil.isConnectingToInternet(context);
				if (!networkCheck) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_noInterent),
								context);
					} else {
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_noInternet),
								context);
					}

				} else if (isRequiredFieldEmpty()) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
					alertbox.show();

				} else {
					registrationMedium();
					/*
					Intent intent = new Intent(ActivationHome.this, ActivationDetails.class);
					intent.putExtra("MDN", mdn.getText().toString());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					*/
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	public void registrationMedium() {
		/* Set Parameters for Activation Service. */
		ValueContainer valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setSourceMdn(mdn.getText().toString());
		Log.d(LOG_TAG, "otp:"+otp.getText().toString());
		valueContainer.setOTP(otp.getText().toString());
		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		settings.edit().putString("mobile", mdn.getText().toString()).apply();
		valueContainer.setTransactionName(Constants.TRANSACTION_REGISTRATION_MEDIUM);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ActivationHome.this);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialog = new ProgressDialog(ActivationHome.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.eng_loading));
			dialog.show();
		} else {
			dialog = new ProgressDialog(ActivationHome.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.bahasa_loading));
			dialog.show();
		}
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {

					XMLParser obj = new XMLParser();
					System.out.println("Testing>>>" + responseXml);
					Log.e("---------------XML_RESPONSE---------------------", responseXml);
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
					} catch (Exception e) {

						e.printStackTrace();
					}

					dialog.dismiss();

					if ((responseContainer != null ? responseContainer.getRegistrationMedium() : null) == null) {

						assert responseContainer != null;
						alertbox.setMessage(responseContainer.getMsg());
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						alertbox.show();

					} else {

						// Check activation Medium here....
						/*
						 * System.out.println("Testing>>>"+
						 * responseContainer.getRegistrationMedium());
						 * if(responseContainer.getIsActivated().toString().
						 * equals("true")){ Intent intent = new
						 * Intent(ActivationHome.this, ResetPinDetails.class);
						 * intent.putExtra("MDN", mdn.getText().toString());
						 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						 * startActivity(intent);
						 * 
						 * }else{
						 */

						if (responseContainer.getStatus().equalsIgnoreCase("Active")) {

							if (responseContainer.getResetPinRequested().equalsIgnoreCase("true")) {

								Intent intent = new Intent(ActivationHome.this, ResetPinDetails.class);
								intent.putExtra("MDN", mdn.getText().toString());
								intent.putExtra("otp", otp.getText().toString());
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							} else {

								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_contactCustomerCare));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_contactCustomerCare));
								}
								alertbox.setNeutralButton("ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {

										Intent intent = new Intent(ActivationHome.this, LoginScreen.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
									}
								});
								alertbox.show();

							}

						} else {
							if (responseContainer.getRegistrationMedium().equalsIgnoreCase("BulkUpload")
									&& responseContainer.getIsActivated().equalsIgnoreCase("false")) {
								Intent intent = new Intent(ActivationHome.this, ReActivationDetails.class);
								intent.putExtra("MDN", mdn.getText().toString());
								intent.putExtra("otp", otp.getText().toString());
								startActivity(intent);

							} /*
								 * else if(responseContainer.getIsActivated().
								 * equalsIgnoreCase("true")) { Intent intent =
								 * new Intent(ActivationHome.this,
								 * ResetPinDetails.class);
								 * intent.putExtra("MDN",
								 * mdn.getText().toString());
								 * intent.setFlags(Intent.
								 * FLAG_ACTIVITY_CLEAR_TOP);
								 * startActivity(intent); }
								 */else {
								Intent intent = new Intent(ActivationHome.this, ActivationDetails.class);
								intent.putExtra("MDN", mdn.getText().toString());
								intent.putExtra("otp", otp.getText().toString());
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						}
						// }

					}

				} else {

					dialog.dismiss();
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

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
					/* Service call for Activation */
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();

	}

	private boolean isRequiredFieldEmpty() {
		mdn = findViewById(R.id.mobileEditText);
		otp = findViewById(R.id.activationKeyEditText);
		return !(!(mdn.getText().toString().equals("")) && !(otp.getText().toString().equals("")));
	}

	public void resendOTP() {

		if(mdn.getText().toString().equals("")){
			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				alertbox.setMessage("Please input your MDN");
			}else{
				alertbox.setMessage("Silahkan masukkan nomor MDN anda");
			}
			alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			alertbox.show();
		}else{
			/* Set Parameters for Activation Service. */
			ValueContainer valueContainer = new ValueContainer();
			valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
			valueContainer.setSourceMdn(mdn.getText().toString());
			valueContainer.setTransactionName(Constants.TRANSACTION_RESEND_OTP);

			final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ActivationHome.this);

			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				dialog = new ProgressDialog(ActivationHome.this, R.style.MyAlertDialogStyle);
				dialog.setTitle("Bank Sinarmas");
				dialog.setCancelable(false);
				dialog.setMessage(getResources().getString(R.string.eng_loading));
				dialog.show();
			} else {
				dialog = new ProgressDialog(ActivationHome.this, R.style.MyAlertDialogStyle);
				dialog.setTitle("Bank Sinarmas");
				dialog.setCancelable(false);
				dialog.setMessage(getResources().getString(R.string.bahasa_loading));
				dialog.show();
			}

			@SuppressLint("HandlerLeak") final Handler handler = new Handler() {

				public void handleMessage(Message msg) {

					if (responseXml != null) {

						XMLParser obj = new XMLParser();

						EncryptedResponseDataContainer responseContainer = null;
						try {
							responseContainer = obj.parse(responseXml);
							System.out.println("Testing>>>Message>>Activation>>" + responseContainer.getMsg());
						} catch (Exception e) {

							e.printStackTrace();
						}

						dialog.dismiss();

						if (responseContainer != null) {
							if (responseContainer.getMsg() == null) {

								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
								}
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {

									}
								});
								alertbox.show();

							} else {
								dialog.dismiss();
								alertbox.setMessage(responseContainer.getMsg());
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										dialog.dismiss();
									}
								});
								alertbox.show();
							}
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
					/* Service call for Activation */
					} catch (Exception e) {
						responseXml = null;
					}
					handler.sendEmptyMessage(0);
				}
			};
			checkUpdate.start();
		}
	}

}
