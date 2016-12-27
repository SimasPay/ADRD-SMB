package com.mfino.bsim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

/** @author pramod */

public class ActivationDetails extends Activity {
	/** Called when the activity is first created. */
	private Button okButton, resentOTP;
	private EditText otp, pin, confirmPin;
	private AlertDialog.Builder alertbox;
	private ValueContainer valueContainer;
	private Bundle bundle;
	// TextView mobileNumText;
	private String responseXml;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activation_details);
		context = this;

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		bundle = getIntent().getExtras();
		TextView otpText = (TextView) findViewById(R.id.textView3);
		// mobileNumber = (EditText)findViewById(R.id.mobileEditText);
		otp = (EditText) findViewById(R.id.activationKeyEditText);
		pin = (EditText) findViewById(R.id.pinEditText);
		confirmPin = (EditText) findViewById(R.id.rePinEditText);

		// mobileNumber.setText(bundle.getString("MDN"));
		okButton = (Button) findViewById(R.id.okButton);
		resentOTP = (Button) findViewById(R.id.resendOTP);
		alertbox = new AlertDialog.Builder(ActivationDetails.this, R.style.MyAlertDialogStyle);
		// resentOTP.setTextColor(Color.parseColor("#3C3ABC"));
		TextView textViewinNewPin = (TextView) findViewById(R.id.textView_newPin);
		TextView textVieConfirmPin = (TextView) findViewById(R.id.textView_confirmNewPin);
		// textVieConfirmPin.setTypeface(Typeface.SANS_SERIF);
		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			// mobileNumText.setText(getResources().getString(R.string.eng_mobileNumber));
			screeTitle.setText(getResources().getString(R.string.eng_activation));
			resentOTP.setText(getResources().getString(R.string.eng_resend_otp));
			textViewinNewPin.setText(getResources().getString(R.string.eng_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.eng_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.eng_submit));
			otpText.setText("Enter Your varification code");

		} else {
			// mobileNumText.setText(getResources().getString(R.string.bahasa_mobileNumber));
			screeTitle.setText(getResources().getString(R.string.bahasa_activation));
			resentOTP.setText(getResources().getString(R.string.bahasa_resend_otp));
			textViewinNewPin.setText(getResources().getString(R.string.bahasa_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.bahasa_submit));
			otpText.setText("Masukan Kode Verifikasi");
		}

		resentOTP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				resendOTP();
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

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

				}
				/*
				 * else if(otp.getText().toString().trim().length()<8) {
				 * alertbox.setMessage("Pin Number must be 6 digits long. ");
				 * alertbox.setNeutralButton("OK", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface arg0, int arg1) {
				 * 
				 * } }); alertbox.show(); }
				 */
				else if (pin.getText().toString().trim().length() < 6) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinActivation));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinActivation));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
					alertbox.show();
				} else if (confirmPin.getText().toString().trim().length() < 6) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_confirmPinLenth));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_confirmPinLenth));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
					alertbox.show();
				} else if (!(pin.getText().toString().equals(confirmPin.getText().toString()))) {
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

					Intent intent = new Intent(ActivationDetails.this, ActivationDisclosure.class);
					intent.putExtra("MDN", bundle.getString("MDN"));
					intent.putExtra("OTP", otp.getText().toString());
					intent.putExtra("PIN", pin.getText().toString());
					intent.putExtra("CONFIRM_PIN", confirmPin.getText().toString());
					intent.putExtra("ACTIVATION_TYPE", "Activation");
					startActivity(intent);

				}
			}
		});

	}

	private boolean isRequiredFieldEmpty() {

		// mobileNumber = (EditText)findViewById(R.id.mobileEditText);
		otp = (EditText) findViewById(R.id.activationKeyEditText);
		pin = (EditText) findViewById(R.id.pinEditText);
		confirmPin = (EditText) findViewById(R.id.rePinEditText);

		if (!(otp.getText().toString().equals("")) && !(pin.getText().toString().equals(""))
				&& !(confirmPin.getText().toString().equals(""))) {
			return false;
		} else {

			return true;
		}
	}

	public void resendOTP() {

		/** Set Parameters for Activation Service. */
		valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setSourceMdn(bundle.getString("MDN"));
		valueContainer.setTransactionName(Constants.TRANSACTION_RESEND_OTP);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ActivationDetails.this);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialog = new ProgressDialog(ActivationDetails.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.eng_loading));
			dialog.show();
		} else {
			dialog = new ProgressDialog(ActivationDetails.this, R.style.MyAlertDialogStyle);
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
						System.out.println("Testing>>>Message>>Activation>>" + responseContainer.getMsg());
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
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						alertbox.show();

					} else {

						/*
						 * if(Integer.parseInt(responseContainer.getMsgCode())==
						 * 811){ //one Timepin not allowed for this MDN }else {}
						 */
						dialog.dismiss();
						if (selectedLanguage.equalsIgnoreCase("ENG")) {

							alertbox.setMessage(getResources().getString(R.string.eng_checkSMS));

						} else {

							alertbox.setMessage(getResources().getString(R.string.bahasa_checkSMS));
						}
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								dialog.dismiss();
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
