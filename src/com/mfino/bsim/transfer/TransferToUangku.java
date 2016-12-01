package com.mfino.bsim.transfer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.receivers.IncomingSMS;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TransferToUangku extends AppCompatActivity implements IncomingSMS.AutoReadSMSListener {
	private Button btn_ok;
	private EditText pinValue, destAccountNo, amountValue;
	private AlertDialog.Builder alertbox;
	private String responseXml;
	ValueContainer valueContainer;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	SharedPreferences settings, settings2;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;
	static AlertDialog dialogBuilder, alertError;
	static boolean isExitActivity = false;
	LinearLayout otplay, otp2lay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_other_bank_details);
		
		settings2 = getSharedPreferences(LOG_TAG, 0);
		settings2.edit().putString("ActivityName", "TransferToUangku").commit();
        if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		context = this;
		IncomingSMS.setListener(this);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		mobileNumber = settings.getString("mobile", "");
		settings.edit().putString("ActivityName", "TransferToUangku").commit();
		settings.edit().putBoolean("isAutoSubmit", false).commit();
		Log.d(LOG_TAG, "Transfer : TransferToUangku");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(TransferToUangku.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		TextView destAcountTxt = (TextView) findViewById(R.id.fundTransfer_otherBank_destAc);
		TextView amountTxt = (TextView) findViewById(R.id.fundTransfer_otherBank_amount);
		alertbox = new AlertDialog.Builder(TransferToUangku.this, R.style.MyAlertDialogStyle);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		// screeTitle.setText(bundle.getString("TAG_NAME"));

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText("Transfer to UANGKU");
			// screeTitle.setText(getResources().getString(R.string.eng_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.eng_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.eng_amount));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {
			screeTitle.setText("Transfer ke UANGKU");
			// screeTitle.setText(getResources().getString(R.string.bahasa_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.bahasa_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@SuppressLint({ "NewApi", "HandlerLeak" })
			@Override
			public void onClick(View arg0) {

				// System.out.println("Testing>>>"+bundle.getString("code"));
				boolean networkCheck = ConfigurationUtil.isConnectingToInternet(context);
				if (!networkCheck) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond),
								context);
					} else {
						ConfigurationUtil.networkDisplayDialog(
								getResources().getString(R.string.bahasa_serverNotRespond), context);
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
				} else if (pinValue.getText().length() < 4) {

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
					alertbox.show();
				} else {
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

					/** Set Paramenters for Call Service. */

					valueContainer = new ValueContainer();
					valueContainer.setTransactionName(Constants.TRANSACTION_Uangku_INQUIRY);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setDestinationBankAccount(destAccountNo.getText().toString().trim());
					valueContainer.setSourcePin(pinValue.getText().toString().trim());
					valueContainer.setAmount(amountValue.getText().toString().trim());
					// valueContainer.setBankCode(bundle.getString("code").trim());
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setDestinationPocketCode("2");
					valueContainer.setServiceName(Constants.SERVICE_BANK);
					valueContainer.setTransferType("toUnagku");

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, TransferToUangku.this);

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = new ProgressDialog(TransferToUangku.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.eng_loading));
						dialog.show();
					} else {
						dialog = new ProgressDialog(TransferToUangku.this, R.style.MyAlertDialogStyle);
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
								int msgCode = 0;
								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}

								if (!(msgCode == 72)) {
									if (responseContainer.getMsg() == null) {

										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
										}

									} else {
										alertbox.setMessage(responseContainer.getMsg());
									}

									if (msgCode == 631) {
										alertbox.setMessage(responseContainer.getMsg());
										alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int arg1) {
												dialog.dismiss();
												finish();
												Intent intent = new Intent(getBaseContext(), LoginScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											}
										});
										alertbox.show();
									} else {
										alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0, int arg1) {
												Intent intent = new Intent(getBaseContext(), HomeScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
												pinValue.setText("");
											}
										});
										alertbox.show();
									}
								} else if (msgCode == 631) {
									alertbox.setMessage(responseContainer.getMsg());
									alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int arg1) {
											Intent intent = new Intent(getBaseContext(), LoginScreen.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										}
									});
									alertbox.show();
								} else {
									try {
										System.out.println("MFA MODE.." + responseContainer.getMfaMode());
										if (responseContainer.getMfaMode() == null) {
											valueContainer.setMfaMode("NONE");
										} else {
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}

									} catch (Exception e1) {
										System.out.println("Testing>>MFAMODE>>exception");
										valueContainer.setMfaMode("NONE");
									}
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										Log.e("MFA MODE..", responseContainer.getMfaMode() + "");
										dialog.dismiss();
										Log.d("Widy-Debug", "Dialog OTP Required show");
										settings.edit().putString("Sctl", responseContainer.getSctl()).commit();
										settings2 = getSharedPreferences(LOG_TAG, 0);
										settings2.edit().putString("ActivityName", "TransferToUangku").commit();
										showOTPRequiredDialog(pinValue.getText().toString().trim(),
												responseContainer.getCustName(), responseContainer.getDestMDN(),
												responseContainer.getAccountNumber(), responseContainer.getMsg(),
												responseContainer.getDestBank(), responseContainer.getAmount(),
												amountValue.getText().toString(), responseContainer.getMfaMode(),
												responseContainer.getEncryptedParentTxnId(),
												responseContainer.getEncryptedTransferId());
									} else {
										/**
										Intent intent = new Intent(TransferToUangku.this, TransferToUnagkuConfirmation.class);
										intent.putExtra("SRCPOCKETCODE", "2");
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("CUST_NAME", responseContainer.getCustName());
										// intent.putExtra("DEST_NUMBER",responseContainer.getDestMDN());
										intent.putExtra("DEST_BANK", responseContainer.getDestBank());
										intent.putExtra("DEST_ACCOUNT_NUM", responseContainer.getAccountNumber());
										intent.putExtra("AMOUNT", responseContainer.getAmount());
										intent.putExtra("AMT", amountValue.getText().toString());
										intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
										intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
										intent.putExtra("MSG", responseContainer.getMsg());
										intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										**/
									}
								}

								pinValue.setText("");

							} else {

								dialog.dismiss();

								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
								}
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										dialog.dismiss();
										Intent intent = new Intent(getBaseContext(), HomeScreen.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);

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
								Log.e(LOG_TAG, "====RESPONSE=========" + responseXml);
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

	/** Empty Field Validation. */
	private boolean isRequiredFieldEmpty() {

		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);

		if (!(destAccountNo.getText().toString().equals("")) && !(amountValue.getText().toString().equals(""))
				&& !(pinValue.getText().toString().equals(""))) {
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

	public void errorOTP() {
		AlertDialog.Builder builderError = new AlertDialog.Builder(TransferToUangku.this, R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.eng_otpfailed));
			builderError.setMessage(getResources().getString(R.string.eng_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							settings2 = getSharedPreferences(LOG_TAG, 0);
							settings2.edit().putString("ActivityName", "ExitTransferToUangku").commit();
							Intent intent = new Intent(TransferToUangku.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		} else {
			builderError.setTitle(getResources().getString(R.string.bahasa_otpfailed));
			builderError.setMessage(getResources().getString(R.string.bahasa_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							settings2 = getSharedPreferences(LOG_TAG, 0);
							settings2.edit().putString("ActivityName", "ExitTransferToUangku").commit();
							Intent intent = new Intent(TransferToUangku.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		}
		alertError = builderError.create();
		if (!isFinishing()) {
			alertError.show();
		}
	}

	public void showOTPRequiredDialog(final String PIN, final String custName, final String MDN,
			final String accountNumber, final String message, final String destBank, final String amount,
			final String AMT, final String mfaMode, final String EncryptedParentTxnId,
			final String EncryptedTransferId) {
		LayoutInflater inflater = getLayoutInflater();
		final ViewGroup nullParent = null;
		View dialoglayout = inflater.inflate(R.layout.new_otp_dialog, nullParent, false);
		dialogBuilder = new AlertDialog.Builder(TransferToUangku.this, R.style.MyAlertDialogStyle).create();
		dialogBuilder.setCanceledOnTouchOutside(false);
		dialogBuilder.setTitle("");
		dialogBuilder.setCancelable(false);
		dialogBuilder.setView(dialoglayout);

		// EditText OTP
		otplay = (LinearLayout) dialoglayout.findViewById(R.id.halaman1);
		otp2lay = (LinearLayout) dialoglayout.findViewById(R.id.halaman2);
		otp2lay.setVisibility(View.GONE);
		TextView manualotp = (TextView) dialoglayout.findViewById(R.id.manualsms_lbl);
		TextView waitingsms = (TextView) dialoglayout.findViewById(R.id.waitingsms_lbl);
		Button cancel_otp = (Button) dialoglayout.findViewById(R.id.cancel_otp);
		waitingsms.setText("Menunggu SMS Kode Verifikasi di Nomor " + mobileNumber + "\n");
		manualotp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				otplay.setVisibility(View.GONE);
				otp2lay.setVisibility(View.VISIBLE);
			}
		});
		edt = (EditText) dialoglayout.findViewById(R.id.otp_value);

		Log.d(LOG_TAG, "otpValue : " + edt.getText().toString());

		// Timer
		final TextView timer = (TextView) dialoglayout.findViewById(R.id.otp_timer);
		// 120detik
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
		cancel_otp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogBuilder.dismiss();
				settings2 = getSharedPreferences(LOG_TAG, 0);
				settings2.edit().putString("ActivityName", "CancelTtransferToUangku").commit();
				if (myTimer != null) {
					myTimer.cancel();
				}
			}
		});
		final Button ok_otp = (Button) dialoglayout.findViewById(R.id.ok_otp);
		ok_otp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				settings2 = getSharedPreferences(LOG_TAG, 0);
				settings2.edit().putString("ActivityName", "ExitTransferToUangku").commit();
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
					dialogBuilder.cancel();
				} else {
					String actName = settings2.getString("ActivityName", "");
					Log.d(LOG_TAG, "ActivityName : " + actName);
					if (actName.equals("TransferToUangku")) {
						Intent intent = new Intent(TransferToUangku.this, TransferToUnagkuConfirmation.class);
						intent.putExtra("SRCPOCKETCODE", "2");
						intent.putExtra("PIN", PIN);
						intent.putExtra("CUST_NAME", custName);
						intent.putExtra("DEST_BANK", destBank);
						intent.putExtra("DEST_ACCOUNT_NUM", accountNumber);
						intent.putExtra("AMOUNT", amount);
						intent.putExtra("AMT", AMT);
						intent.putExtra("PTFNID", EncryptedParentTxnId);
						intent.putExtra("TFNID", EncryptedTransferId);
						intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
						intent.putExtra("MSG", message);
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("MFA_MODE", mfaMode);
						intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
					if (myTimer != null) {
						myTimer.cancel();
					}
				}
			}
		});
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
					ok_otp.setEnabled(false);
				} else {
					// Something into edit text. Enable the button.
					ok_otp.setEnabled(true);
				}
				Boolean isAutoSubmit = settings.getBoolean("isAutoSubmit", false);
				if ((edt.getText().length() > 3) && (isAutoSubmit == true)) {
					if (myTimer != null) {
						myTimer.cancel();
					}
					settings2 = getSharedPreferences(LOG_TAG, 0);
			        String actName = settings2.getString("ActivityName", "");
			        Log.d(LOG_TAG, "ActivityName : " + actName);
			        if (actName.equals("TransferToUangku")) {
			        	Intent intent = new Intent(TransferToUangku.this, TransferToUnagkuConfirmation.class);
						intent.putExtra("SRCPOCKETCODE", "2");
						intent.putExtra("PIN", PIN);
						intent.putExtra("CUST_NAME", custName);
						intent.putExtra("DEST_BANK", destBank);
						intent.putExtra("DEST_ACCOUNT_NUM", accountNumber);
						intent.putExtra("AMOUNT", amount);
						intent.putExtra("AMT", AMT);
						intent.putExtra("PTFNID", EncryptedParentTxnId);
						intent.putExtra("TFNID", EncryptedTransferId);
						intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
						intent.putExtra("MSG", message);
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("MFA_MODE", mfaMode);
						intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent); 
			        }
				}
			}
		});
		dialogBuilder.show();
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
		settings2.edit().putString("ActivityName", "ExitTransferToUangku").commit();
		if (dialogBuilder != null) {
			dialogBuilder.dismiss();
		}
		if (alertError != null) {
			alertError.dismiss();
		}
	}

}
