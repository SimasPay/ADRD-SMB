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

public class ActivationDetails extends Activity {
	private EditText pin, confirmPin;
	private AlertDialog.Builder alertbox;
	private Bundle bundle;
	// TextView mobileNumText;
	private String responseXml, otp;
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
		TextView screeTitle = findViewById(R.id.screenTitle);
		ImageButton back = findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		bundle = getIntent().getExtras();
		//TextView otpText = findViewById(R.id.textView3);
		// mobileNumber = (EditText)findViewById(R.id.mobileEditText);
		//otp = (EditText) findViewById(R.id.activationKeyEditText);
		pin = findViewById(R.id.pinEditText);
		confirmPin = findViewById(R.id.rePinEditText);

		// mobileNumber.setText(bundle.getString("MDN"));
		/* Called when the activity is first created. */
		Button okButton = findViewById(R.id.okButton);
		//Button resentOTP = findViewById(R.id.resendOTP);
		alertbox = new AlertDialog.Builder(ActivationDetails.this, R.style.MyAlertDialogStyle);
		// resentOTP.setTextColor(Color.parseColor("#3C3ABC"));
		TextView textViewinNewPin = findViewById(R.id.textView_newPin);
		TextView textVieConfirmPin = findViewById(R.id.textView_confirmNewPin);
		// textVieConfirmPin.setTypeface(Typeface.SANS_SERIF);
		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_activation));
			//resentOTP.setText(getResources().getString(R.string.eng_resend_otp));
			textViewinNewPin.setText(getResources().getString(R.string.eng_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.eng_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.eng_submit));
			//otpText.setText("Enter Your varification code");

		} else {
			// mobileNumText.setText(getResources().getString(R.string.bahasa_mobileNumber));
			screeTitle.setText(getResources().getString(R.string.bahasa_activation));
			//resentOTP.setText(getResources().getString(R.string.bahasa_resend_otp));
			textViewinNewPin.setText(getResources().getString(R.string.bahasa_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.bahasa_submit));
			//otpText.setText("Masukan Kode Verifikasi");
		}

		/*
		resentOTP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resendOTP();
			}
		});
		*/

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

					/*
					 * Call Activation Disclosure here and pass value of Mobile
					 * Number, Activation Key and PIN
					 */

					Intent intent = new Intent(ActivationDetails.this, ActivationDisclosure.class);
					intent.putExtra("MDN", bundle.getString("MDN"));
					intent.putExtra("OTP", bundle.getString("otp"));
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
		//otp = (EditText) findViewById(R.id.activationKeyEditText);
		pin = findViewById(R.id.pinEditText);
		confirmPin = findViewById(R.id.rePinEditText);

		return !(!(pin.getText().toString().equals(""))
				&& !(confirmPin.getText().toString().equals("")));
	}

	public void resendOTP() {

		/* Set Parameters for Activation Service. */
		ValueContainer valueContainer = new ValueContainer();
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
