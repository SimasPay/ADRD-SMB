package com.mfino.bsim.account;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ChangePin extends AppCompatActivity {

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
	SharedPreferences settings;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pin);
		context = ChangePin.this;

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		screeTitle.setText("CHANGE PIN");
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);
		home.setVisibility(View.GONE);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		mobileNumber = settings.getString("mobile", "");
		settings.edit().putString("ActivityName", "ChangePin").commit();
		settings.edit().putBoolean("isAutoSubmit", false).commit();
		Log.d(LOG_TAG, "Account : ChangePin");

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String required;
				if (savedInstanceState == null) {
					Bundle extras = getIntent().getExtras();
					if (extras == null) {
						required = null;
					} else {
						required = extras.getString("REQUIRED");
						if (required.equals("yes")) {
							forceChangePINDialog();
						} else {
							Intent intent = new Intent(ChangePin.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					}
				} else {
					required = (String) savedInstanceState.getSerializable("STRING_I_NEED");
				}
				finish();
			}
		});
		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String required;
				if (savedInstanceState == null) {
					Bundle extras = getIntent().getExtras();
					if (extras == null) {
						required = null;
					} else {
						required = extras.getString("REQUIRED");
						if (required.equals("yes")) {
							forceChangePINDialog();
						} else {
							Intent intent = new Intent(ChangePin.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					}
				} else {
					required = (String) savedInstanceState.getSerializable("STRING_I_NEED");
				}
				finish();
			}
		});
		home.setVisibility(View.GONE);
		oldpinValue = (EditText) findViewById(R.id.oldPinEditText);
		newpinValue = (EditText) findViewById(R.id.newpinEditText);
		confirmNewPinValue = (EditText) findViewById(R.id.reNewPinEditText);

		TextView lodPin = (TextView) findViewById(R.id.textView_oldPin);
		TextView newPin = (TextView) findViewById(R.id.textView_newPin);
		TextView confirmPin = (TextView) findViewById(R.id.textView_confirmNewPin);
		btn_ok = (Button) findViewById(R.id.okButton);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_changePin));
			lodPin.setText(getResources().getString(R.string.eng_oldPin));
			newPin.setText(getResources().getString(R.string.eng_newPin));
			confirmPin.setText(getResources().getString(R.string.eng_confimPin));
			btn_ok.setText(getResources().getString(R.string.eng_submit));
		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_changePin));
			lodPin.setText(getResources().getString(R.string.bahasa_oldPin));
			newPin.setText(getResources().getString(R.string.bahasa_newPin));
			confirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));
		}

		alertbox = new AlertDialog.Builder(ChangePin.this, R.style.MyAlertDialogStyle);

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@SuppressLint({ "NewApi", "HandlerLeak" })
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
					alertbox.setMessage(" Fields can't be empty ");
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					});
					alertbox.show();

				} else if (!newpinValue.getText().toString().equals(confirmNewPinValue.getText().toString())) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_mPinNotMatch));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_mPinNotMatch));
					}

					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							newpinValue.setText("");
							confirmNewPinValue.setText("");
						}
					});
					alertbox.show();
				} else if (newpinValue.getText().length() < 6) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinActivation));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinActivation));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							newpinValue.setText("");
							confirmNewPinValue.setText("");
						}
					});
					alertbox.show();
				} else if(confirmNewPinValue.getText().length() < 6){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_confirmPinLenth));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_confirmPinLenth));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							newpinValue.setText("");
							confirmNewPinValue.setText("");
						}
					});
					alertbox.show();
				}else {
					int currentapiVersion = android.os.Build.VERSION.SDK_INT;
					if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
						if ((checkCallingOrSelfPermission(
								android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
								&& checkCallingOrSelfPermission(
										Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

							requestPermissions(new String[] { Manifest.permission.READ_SMS,
									android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS },
									109);
						}
					}

					/** Set Service Parameters for Change PIN */

					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					valueContainer.setTransactionName(Constants.TRANSACTION_CHANGEPIN);
					settings = getSharedPreferences("LOGIN_PREFERECES", 0);
					mobileNumber = settings.getString("mobile", "");
					valueContainer.setSourceMdn(mobileNumber);
					valueContainer.setSourcePin(oldpinValue.getText().toString());
					valueContainer.setNewPin(newpinValue.getText().toString());
					valueContainer.setConfirmPin(newpinValue.getText().toString());

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ChangePin.this);

					final ProgressDialog dialog = new ProgressDialog(ChangePin.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage("Loading....   ");
					dialog.show();

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
									alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dlg, int arg1) {
											dlg.dismiss();
										}
									});
									alertbox.show();
								} else if (responseContainer.getMsgCode().equals("631")) {
									alertbox.setMessage(responseContainer.getMsg());
									alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dlg, int arg1) {
											dlg.dismiss();
											Intent intent = new Intent(getBaseContext(), LoginScreen.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										}
									});
									alertbox.show();
								} else if (responseContainer.getMsgCode().equals("2039")) {
									try {
										System.out.println("MFA MODE.." + responseContainer.getMfaMode());
										valueContainer.setMfaMode(responseContainer.getMfaMode());
									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}

									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										Log.e("MFA MODE..", responseContainer.getMfaMode() + "");
										dialog.dismiss();
										Log.d("Widy-Debug", "Dialog OTP Required show");
										settings.edit().putString("Sctl", responseContainer.getSctl()).commit();
										showOTPRequiredDialog(responseContainer.getMsg(),
												responseContainer.getMfaMode(), responseContainer.getSctl(),
												oldpinValue.getText().toString(), newpinValue.getText().toString());
									} else {
										Constants.SOURCE_MDN_PIN = bundle.getString("NPIN");
										System.out.println("Testing>>Without OTP>>" + responseContainer.getMsg());
										Intent intent = new Intent(ChangePin.this, ConfirmationScreen.class);
										intent.putExtra("MSG", responseContainer.getMsg());
										intent.putExtra("OPIN", oldpinValue.getText().toString());
										intent.putExtra("NPIN", newpinValue.getText().toString());
										intent.putExtra("mdn", settings.getString("mobile", ""));
										intent.putExtra("REQUIRED", "yes");
										intent.putExtra("CONFIRM_NPIN", confirmNewPinValue.getText().toString());
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);

									}

								} else {
									alertbox.setMessage(responseContainer.getMsg());
									alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dlg, int arg1) {
											dlg.dismiss();
											finish();
										}
									});
									alertbox.show();
								}
							} else {
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
								}
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dlg, int arg1) {
										dlg.dismiss();
										finish();
									}
								});
								alertbox.show();
							}
						}
					};

					final Thread checkUpdate = new Thread() {
						/**
						 * Service call and getting response as XML in String.
						 */
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

		if (!(oldpinValue.getText().toString().equals("")) && !(newpinValue.getText().toString().equals(""))
				&& !(confirmNewPinValue.getText().toString().equals(""))) {
			return false;
		} else {
			return true;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 109) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.e("if_permission", "*********");

			} else {
				Log.e("elseeeee_permission", "*********");

			}
		}
	}

	public void recivedSms(String message) {
		try {
			Log.d(LOG_TAG, "isi SMS : " + message);
			if (message.contains("Kode Simobi Anda ")
					|| message.toLowerCase(Locale.getDefault()).contains("kode simobi anda ")) {
				Log.d(LOG_TAG, "konten sms : indonesia");
				otpValue = message
						.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
						.trim();
				sctl = message.substring(message.indexOf(":") + 1, message.indexOf(")"));
			} else if (message.contains("Your Simobi Code is ")
					|| message.toLowerCase(Locale.getDefault()).contains("your simobi code is ")) {
				Log.d(LOG_TAG, "konten sms : english");
				otpValue = message
						.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
						.trim();
				sctl = message.substring(message.indexOf("(ref no: ") + new String("(ref no: ").length(),
						message.indexOf(")"));
			}
			Log.d(LOG_TAG, "OPT code : " + otpValue + ", sctl : " + sctl);
			edt.setText(otpValue);
		} catch (Exception e) {

		}
	}

	public void forceChangePINDialog() {
		AlertDialog.Builder builderError = new AlertDialog.Builder(ChangePin.this, R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.Simobi_app_name));
			builderError.setMessage(getResources().getString(R.string.eng_changepin));
		} else {
			builderError.setTitle(getResources().getString(R.string.Simobi_app_name));
			builderError.setMessage(getResources().getString(R.string.bahasa_changepin));
		}
		builderError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alertError = builderError.create();
		if (!((Activity) context).isFinishing()) {
			alertError.show();
		}
	}

	public void errorOTP() {
		AlertDialog.Builder builderError = new AlertDialog.Builder(ChangePin.this, R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.eng_otpfailed));
			builderError.setMessage(getResources().getString(R.string.eng_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(ChangePin.this, LoginScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		} else {
			builderError.setTitle(getResources().getString(R.string.bahasa_otpfailed));
			builderError.setMessage(getResources().getString(R.string.bahasa_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(ChangePin.this, LoginScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		}
		AlertDialog alertError = builderError.create();
		if (!((Activity) context).isFinishing()) {
			alertError.show();
		}
	}

	public void showOTPRequiredDialog(final String message, final String mfaMode, final String sctl,
			final String oldPin, final String newPin) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChangePin.this, R.style.MyAlertDialogStyle);
		LayoutInflater inflater = ChangePin.this.getLayoutInflater();
		final ViewGroup nullParent = null;
		dialogBuilder.setCancelable(false);
		final View dialogView = inflater.inflate(R.layout.otp_dialog, nullParent);
		dialogBuilder.setView(dialogView);

		// EditText OTP
		edt = (EditText) dialogView.findViewById(R.id.otp_value);
		edt.setText(otpValue);
		final String otpValue_new = edt.getText().toString();
		Log.d(LOG_TAG, "otpValue_new : " + otpValue_new + ", otpValue : " + otpValue);

		// Timer
		final TextView timer = (TextView) dialogView.findViewById(R.id.otp_timer);
		// 120 detik
		final CountDownTimer myTimer = new CountDownTimer(120000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				NumberFormat f = new DecimalFormat("00");
				timer.setText(
						f.format(millisUntilFinished / 60000) + ":" + f.format(millisUntilFinished % 60000 / 1000));
			}

			@Override
			public void onFinish() {
				errorOTP();
				timer.setText("00:00");
			}
		};
		myTimer.start();

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialogBuilder.setTitle(getResources().getString(R.string.eng_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.eng_otprequired_desc_1) + "" + mobileNumber + " "
					+ getResources().getString(R.string.eng_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					if (myTimer != null) {
						myTimer.cancel();
					}
				}
			});
		} else {
			dialogBuilder.setTitle(getResources().getString(R.string.bahasa_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.bahasa_otprequired_desc_1) + "" + mobileNumber
					+ " " + getResources().getString(R.string.bahasa_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					if (myTimer != null) {
						myTimer.cancel();
					}
				}
			});
		}
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
				} else {
					if (myTimer != null) {
						myTimer.cancel();
					}
					Intent intent = new Intent(ChangePin.this, ChangePinConfirm.class);
					intent.putExtra("MSG", message);
					intent.putExtra("OTP", edt.getText().toString());
					intent.putExtra("SCTL", sctl);
					intent.putExtra("MFA_MODE", mfaMode);
					intent.putExtra("OPIN", oldPin);
					intent.putExtra("NPIN", newPin);
					intent.putExtra("mdn", settings.getString("mobile", ""));
					intent.putExtra("REQUIRED", "yes");
					intent.putExtra("CONFIRM_NPIN", newPin);
					Bundle extras = getIntent().getExtras();
					if(extras!=null){
						String required = extras.getString("REQUIRED");
						if (required.equals("yes")) {
							intent.putExtra("REQUIRED", "yes");
						}
					}
					startActivity(intent);
				}
			}
		});
		final AlertDialog b = dialogBuilder.create();
		b.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		b.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		b.show();
		((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		edt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// Check if edittext is empty
				if (TextUtils.isEmpty(s)) {
					// Disable ok button
					((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					// Something into edit text. Enable the button.
					((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				}
				Boolean isAutoSubmit = settings.getBoolean("isAutoSubmit", false);
				if ((edt.getText().length() > 3) && (isAutoSubmit == true)) {
					if (myTimer != null) {
						myTimer.cancel();
					}
					Intent intent = new Intent(ChangePin.this, ChangePinConfirm.class);
					intent.putExtra("MSG", message);
					intent.putExtra("OTP", edt.getText().toString());
					intent.putExtra("SCTL", sctl);
					intent.putExtra("MFA_MODE", mfaMode);
					intent.putExtra("OPIN", oldPin);
					intent.putExtra("NPIN", newPin);
					intent.putExtra("mdn", settings.getString("mobile", ""));
					intent.putExtra("REQUIRED", "yes");
					intent.putExtra("CONFIRM_NPIN", newPin);
					startActivity(intent);
				}

			}
		});
	}

	@Override
	public void onBackPressed() {
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			String required = extras.getString("REQUIRED");
			if(extras.getString("REQUIRED")!=null){
				if (required.equals("yes")) {
					//forceChangePINDialog();
				}
			}else{
				Intent intent = new Intent(ChangePin.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}else{
			Intent intent = new Intent(ChangePin.this, HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle extras = getIntent().getExtras();
			if(extras!=null){
				String required = extras.getString("REQUIRED");
				if(extras.getString("REQUIRED")!=null){
					if (required.equals("yes")) {
						//forceChangePINDialog();
					}
				}else{
					Intent intent = new Intent(ChangePin.this, HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}else{
				Intent intent = new Intent(ChangePin.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
