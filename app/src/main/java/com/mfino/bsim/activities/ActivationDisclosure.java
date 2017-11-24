package com.mfino.bsim.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.receivers.IncomingSMS;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.handset.security.CryptoService;

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
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ActivationDisclosure extends AppCompatActivity implements IncomingSMS.AutoReadSMSListener {
	private ValueContainer valueContainer;
	private Bundle bundle;
	private String responseXml;
	private AlertDialog.Builder alertbox;
	private String otpValue;
	SharedPreferences languageSettings, encrptionKeys;
	String selectedLanguage;
	ProgressDialog dialog;
	String newPin, bankPin, cardPan;
	Context context;
	DBHelper mydb;
	String session = "false";
	SharedPreferences settings, settings2;
	String f_mdn;
	ArrayList<String> array_session = new ArrayList<String>();
	String session_value;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;
	static boolean isExitActivity = false;
	static AlertDialog otpDialogS, alertError;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activation_disclosure);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		f_mdn = settings.getString("mobile", "");
		mobileNumber = settings.getString("mobile", "");
		settings.edit().putString("ActivityName", "ActivationDisclosure").apply();
		settings.edit().putBoolean("isAutoSubmit", false).apply();
		Log.d(LOG_TAG, "Activation : ActivationDisclosure");
		context = this;
		mydb = new DBHelper(ActivationDisclosure.this);
		settings2 = getSharedPreferences(LOG_TAG, 0);
		settings2.edit().putString("ActivityName", "ActivationDisclosure").apply();
		if (android.os.Build.VERSION.SDK_INT > 14) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		IncomingSMS.setListener(ActivationDisclosure.this);

		// Header code...
		TextView screeTitle = (TextView) findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) findViewById(R.id.back);
		TextView disclosure = (TextView) findViewById(R.id.terms_conditions);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ActivationDisclosure.this.finish();
			}
		});

		bundle = getIntent().getExtras();
		alertbox = new AlertDialog.Builder(ActivationDisclosure.this, R.style.MyAlertDialogStyle);
		/* Called when the activity is first created. */
		Button agreeButton = (Button) findViewById(R.id.agreeButton);
		Button decline = (Button) findViewById(R.id.decline);

		// Public key
		encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES", 0);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
				disclosure.setText(
						Html.fromHtml(getResources().getString(R.string.eng_disclosure), Html.FROM_HTML_MODE_LEGACY));
			} else {
				disclosure.setText(Html.fromHtml(getResources().getString(R.string.eng_disclosure)));
			}
			screeTitle.setText(getResources().getString(R.string.eng_activationDisclosure));
			agreeButton.setText(getResources().getString(R.string.eng_agree));
			decline.setText(getResources().getString(R.string.eng_cancel));
			back.setBackgroundResource(R.drawable.back_button);

		} else {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
				disclosure.setText(
						Html.fromHtml(getResources().getString(R.string.bahasa_disclosure), Html.FROM_HTML_MODE_LEGACY));
			} else {
				disclosure.setText(Html.fromHtml(getResources().getString(R.string.bahasa_disclosure)));
			}
			screeTitle.setText(getResources().getString(R.string.bahasa_activationDisclosure));
			agreeButton.setText(getResources().getString(R.string.bahasa_agree));
			decline.setText(getResources().getString(R.string.bahasa_cancel));
		}
		decline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivationDisclosure.this, ActivationHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		agreeButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint({ "NewApi", "HandlerLeak" })
			@Override
			public void onClick(View arg0) {

				/* Set Parameters for Activation Service. */

				String module = encrptionKeys.getString("MODULE", "NONE");
				String exponent = encrptionKeys.getString("EXPONENT", "NONE");
				//String confirmPin = CryptoService.encryptWithPublicKey(module, exponent,bundle.getString("CONFIRM_PIN").getBytes());
				newPin = CryptoService.encryptWithPublicKey(module, exponent, bundle.getString("PIN").getBytes());
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
					if ((checkCallingOrSelfPermission(
							android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
							&& checkCallingOrSelfPermission(
									Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

						requestPermissions(new String[] { Manifest.permission.READ_SMS,
								android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS }, 109);
					}
				}

				if (bundle.getString("ACTIVATION_TYPE").equals("Activation")) {

					System.out.println("Testing>>Activation>>>OTP>>" + bundle.getString("OTP"));
					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					valueContainer.setSourceMdn(bundle.getString("MDN"));
					valueContainer.setTransactionName(Constants.TRANSACTION_ACTIVATION);
					valueContainer.setOTP(bundle.getString("OTP"));
					// Without RSA
					/*
					 * valueContainer.setActivationConfirmPin(bundle.getString(
					 * "PIN"));
					 * valueContainer.setActivationNewPin(bundle.getString("PIN"
					 * ));
					 */
					// RSA
					valueContainer.setActivationConfirmPin(newPin);
					valueContainer.setActivationNewPin(newPin);

				} else {

					bankPin = CryptoService.encryptWithPublicKey(module, exponent,
							bundle.getString("SOURCE_PIN").getBytes());
					cardPan = CryptoService.encryptWithPublicKey(module, exponent,
							bundle.getString("CARD_PAN").getBytes());
					System.out.println("<<<Testing1>>" + bankPin);
					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					valueContainer.setSourceMdn(bundle.getString("MDN"));
					valueContainer.setTransactionName(Constants.TRANSACTION_REACTIVATION);

					// Without RSA
					/*
					 * valueContainer.setActivationConfirmPin(bundle.getString(
					 * "PIN"));
					 * valueContainer.setActivationNewPin(bundle.getString("PIN"
					 * ));
					 * valueContainer.setCardPan(bundle.getString("CARD_PAN"));
					 * valueContainer.setBankPin(bundle.getString("SOURCE_PIN"))
					 * ;
					 */
					// RSA
					valueContainer.setActivationConfirmPin(newPin);
					valueContainer.setActivationNewPin(newPin);
					valueContainer.setCardPan(cardPan);
					ValueContainer.setBankPin(bankPin);
				}

				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ActivationDisclosure.this);

				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					dialog = new ProgressDialog(ActivationDisclosure.this, R.style.MyAlertDialogStyle);
					dialog.setTitle("Bank Sinarmas");
					dialog.setCancelable(false);
					dialog.setMessage(getResources().getString(R.string.eng_loading));
					dialog.show();
				} else {
					dialog = new ProgressDialog(ActivationDisclosure.this, R.style.MyAlertDialogStyle);
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

								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {

									}
								});
								alertbox.show();

							} else if (responseContainer.getMsgCode().equals("2040")
									|| responseContainer.getMsgCode().equals("2041")) {

								// Constants.SOURCE_MDN_PIN =
								// oldpinValue.getText().toString();
								dialog.dismiss();

								try {
									System.out.println("MFA MODE.." + responseContainer.getMfaMode());
									valueContainer.setMfaMode(responseContainer.getMfaMode());
								} catch (Exception e1) {
									valueContainer.setMfaMode("NONE");
								}

								if (valueContainer.getMfaMode().equalsIgnoreCase("OTP")) {
									Log.e("MFA MODE..", responseContainer.getMfaMode() + "");
									dialog.dismiss();
									Log.d("Widy-Debug", "Dialog OTP Required show");
									settings.edit().putString("Sctl", responseContainer.getSctl()).apply();
									if (bundle.getString("ACTIVATION_TYPE").equals("Activation")) {
										showOTPRequiredDialog(responseContainer.getMsg(), "", "",
												responseContainer.getMfaMode(), responseContainer.getSctl(),
												bundle.getString("ACTIVATION_TYPE"), bundle.getString("MDN"),
												bundle.getString("OTP"), newPin);
									} else {
										showOTPRequiredDialog(responseContainer.getMsg(), cardPan, bankPin,
												responseContainer.getMfaMode(), responseContainer.getSctl(),
												bundle.getString("ACTIVATION_TYPE"), bundle.getString("MDN"),
												bundle.getString("OTP"), newPin);
									}
								} else {
									Intent intent = new Intent(ActivationDisclosure.this, ActivationConfirmation.class);
									intent.putExtra("MSG", responseContainer.getMsg());
									intent.putExtra("SCREEN", "Activation");
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}
							} else {
								alertbox.setMessage(responseContainer.getMsg());
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int arg1) {
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
								public void onClick(DialogInterface dialog, int arg1) {
									dialog.dismiss();
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
						} catch (Exception e) {
							responseXml = null;
						}
						handler.sendEmptyMessage(0);
					}
				};
				checkUpdate.start();

			}
		});
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

	public void errorOTP() {
		AlertDialog.Builder builderError = new AlertDialog.Builder(ActivationDisclosure.this,
				R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.eng_otpfailed));
			builderError.setMessage(getResources().getString(R.string.eng_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							settings2 = getSharedPreferences(LOG_TAG, 0);
							settings2.edit().putString("ActivityName", "ExitActivationDisclosure").apply();
							isExitActivity = true;
							Intent intent = new Intent(ActivationDisclosure.this, LandingScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		} else {
			builderError.setTitle(getResources().getString(R.string.bahasa_otpfailed));
			builderError.setMessage(getResources().getString(R.string.bahasa_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							settings2 = getSharedPreferences(LOG_TAG, 0);
							settings2.edit().putString("ActivityName", "ExitActivationDisclosure").apply();
							isExitActivity = true;
							Intent intent = new Intent(ActivationDisclosure.this, LandingScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		}
		alertError = builderError.create();
		if (!((Activity) context).isFinishing()) {
			alertError.show();
		}
	}

	public void showOTPRequiredDialog(final String message, final String cardpan, final String sourcePin,
			final String mfaMode, final String sctl, final String activation_type, final String MDN,
			final String activation_otp, final String newPin) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ActivationDisclosure.this,
				R.style.MyAlertDialogStyle);
		LayoutInflater inflater = this.getLayoutInflater();
		final ViewGroup nullParent = null;
		View viewTitle=inflater.inflate(R.layout.custom_header_otp, nullParent, false);
		ProgressBar progBar = (ProgressBar)viewTitle.findViewById(R.id.progressbar_otp);
		if (progBar.getIndeterminateDrawable() != null) {
			progBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.SRC_IN);
		}
		dialogBuilder.setCustomTitle(viewTitle);
		dialogBuilder.setCancelable(false);
		final View dialogView = inflater.inflate(R.layout.otp_dialog, nullParent);
		dialogBuilder.setView(dialogView);

		// EditText OTP
		edt = (EditText) dialogView.findViewById(R.id.otp_value);
		Log.d(LOG_TAG, "otpValue : " + otpValue);

		// Timer
		final TextView timer = (TextView) dialogView.findViewById(R.id.otp_timer);
		// 120 detik
		final CountDownTimer countTimer = new CountDownTimer(120000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				NumberFormat f = new DecimalFormat("00");
				timer.setText(
						f.format(millisUntilFinished / 60000) + ":" + f.format(millisUntilFinished % 60000 / 1000));
			}

			@Override
			public void onFinish() {
				otpDialogS.cancel();
				otpDialogS.dismiss();
				errorOTP();
				timer.setText("00:00");
			}
		};
		countTimer.start();

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialogBuilder.setTitle(getResources().getString(R.string.eng_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.eng_otprequired_desc_1) + "" + mobileNumber + " "
					+ getResources().getString(R.string.eng_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					if (countTimer != null) {
						countTimer.cancel();
					}
					settings2 = getSharedPreferences(LOG_TAG, 0);
					settings2.edit().putString("ActivityName", "ExitActivationDisclosure").commit();
				}
			});
		} else {
			dialogBuilder.setTitle(getResources().getString(R.string.bahasa_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.bahasa_otprequired_desc_1) + "" + mobileNumber
					+ " " + getResources().getString(R.string.bahasa_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					if (countTimer != null) {
						countTimer.cancel();
					}
					settings2 = getSharedPreferences(LOG_TAG, 0);
					settings2.edit().putString("ActivityName", "ExitActivationDisclosure").apply();
				}
			});
		}
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
				} else {
					if (countTimer != null) {
						countTimer.cancel();
					}
					if (cardpan.equals("") && sourcePin.equals("")) {
						Intent intent = new Intent(ActivationDisclosure.this, ActivationConfirm.class);
						intent.putExtra("MSG", message);
						intent.putExtra("SCREEN", "Activation");
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("ACTIVATION_OTP", activation_otp);
						intent.putExtra("PIN", newPin);
						intent.putExtra("CONFIRM_PIN", newPin);
						intent.putExtra("MDN", MDN);
						intent.putExtra("ACTIVATION_TYPE", activation_type);
						intent.putExtra("SCTL", sctl);
						intent.putExtra("MFA_MODE", mfaMode);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					} else {
						Intent intent = new Intent(ActivationDisclosure.this, ActivationConfirm.class);
						intent.putExtra("MSG", message);
						intent.putExtra("SCREEN", "Activation");
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("MDN", MDN);

						// With RSA
						intent.putExtra("CARD_PAN", cardpan);
						intent.putExtra("PIN", newPin);
						intent.putExtra("SOURCE_PIN", sourcePin);
						intent.putExtra("ACTIVATION_TYPE", activation_type);
						intent.putExtra("SCTL", sctl);
						intent.putExtra("MFA_MODE", mfaMode);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}

				}
			}
		});
		otpDialogS = dialogBuilder.create();
		otpDialogS.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		otpDialogS.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		otpDialogS.show();
		((AlertDialog) otpDialogS).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
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
					((AlertDialog) otpDialogS).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					// Something into edit text. Enable the button.
					((AlertDialog) otpDialogS).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				}
				Boolean isAutoSubmit = settings.getBoolean("isAutoSubmit", false);
				if ((edt.getText().length() > 3) && (isAutoSubmit == true)) {
					if (countTimer != null) {
						countTimer.cancel();
					}
					settings2 = getSharedPreferences(LOG_TAG, 0);
					String actName = settings2.getString("ActivityName", "");
					Log.d(LOG_TAG, "ActivityName : " + actName);
					if (actName.equals("ActivationDisclosure")) {
						if (cardpan.equals("") && sourcePin.equals("")) {
							Intent intent = new Intent(ActivationDisclosure.this, ActivationConfirm.class);
							intent.putExtra("MSG", message);
							intent.putExtra("SCREEN", "Activation");
							intent.putExtra("OTP", edt.getText().toString());
							intent.putExtra("ACTIVATION_OTP", activation_otp);
							intent.putExtra("PIN", newPin);
							intent.putExtra("CONFIRM_PIN", newPin);
							intent.putExtra("MDN", MDN);
							intent.putExtra("ACTIVATION_TYPE", activation_type);
							intent.putExtra("SCTL", sctl);
							intent.putExtra("MFA_MODE", mfaMode);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						} else {
							Intent intent = new Intent(ActivationDisclosure.this, ActivationConfirm.class);
							intent.putExtra("MSG", message);
							intent.putExtra("SCREEN", "Activation");
							intent.putExtra("OTP", edt.getText().toString());
							intent.putExtra("MDN", MDN);

							// With RSA
							intent.putExtra("CARD_PAN", cardpan);
							intent.putExtra("PIN", newPin);
							intent.putExtra("SOURCE_PIN", sourcePin);
							intent.putExtra("ACTIVATION_TYPE", activation_type);
							intent.putExtra("SCTL", sctl);
							intent.putExtra("MFA_MODE", mfaMode);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					}
				}

			}
		});
	}

	@Override
	public void onReadSMS(String otp) {
		Log.d(LOG_TAG, "otp from SMS: " + otp);
		edt.setText(otp);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		settings2 = getSharedPreferences(LOG_TAG, 0);
		settings2.edit().putString("ActivityName", "ExitActivationDisclosure").commit();
		isExitActivity = true;
		if (otpDialogS != null) {
			otpDialogS.dismiss();
		}
		if (alertError != null) {
			alertError.dismiss();
		}
	}

}
