package com.mfino.bsim.transfer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TransferToUangku extends AppCompatActivity {
	private Button btn_ok;
	private EditText pinValue, destAccountNo, amountValue;
	private AlertDialog.Builder alertbox;
	private String responseXml;
	ValueContainer valueContainer;
	String otpValue, sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	SharedPreferences settings;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;
	static AlertDialog otpDialogS;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_other_bank_details);
		context = this;

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
								/** Parse response xml. */
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
											alertbox.setMessage(
													getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(
													getResources().getString(R.string.bahasa_serverNotRespond));
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
												dialog.dismiss();

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
											dialog.dismiss();
											finish();
											Intent intent = new Intent(getBaseContext(), LoginScreen.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										}
									});
									alertbox.show();
								} else {

									// dialog.dismiss();
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
										showOTPRequiredDialog(pinValue.getText().toString().trim(),
												responseContainer.getCustName(), responseContainer.getDestMDN(),
												responseContainer.getAccountNumber(), responseContainer.getMsg(),
												responseContainer.getDestBank(), responseContainer.getAmount(),
												amountValue.getText().toString(), responseContainer.getMfaMode(),
												responseContainer.getEncryptedParentTxnId(),
												responseContainer.getEncryptedTransferId());
										/**
										 * try {
										 * 
										 * // final ProgressDialog dialog1 = //
										 * ProgressDialog.show(
										 * ToOtherBankDetails.this, //
										 * " Banksinarmas ", "Please Wait // for
										 * SMS.... ", true); Long
										 * startTimeInMillis = new
										 * java.util.Date().getTime();
										 * 
										 * while (true) { Thread.sleep(2000);
										 * System.out.println(
										 * "Testing>>inside Loop"); final Uri
										 * SMS_INBOX =
										 * Uri.parse("content://sms/inbox");
										 * Cursor c =
										 * getContentResolver().query(SMS_INBOX,
										 * null, null, null, "DATE desc");
										 * 
										 * c.moveToFirst(); for (int i = 0; i <
										 * 10; i++) { String body =
										 * c.getString(c.getColumnIndexOrThrow(
										 * "body")) .toString().trim();
										 * 
										 * if (body.contains("Kode Simobi Anda")
										 * && body.contains(responseContainer.
										 * getSctl())) {
										 * 
										 * otpValue = body.substring( new
										 * String("Kode Simobi Anda ").length(),
										 * body.indexOf("(no ref")); sctl =
										 * body.substring(body.indexOf(":") + 1,
										 * body.indexOf(")")); break;
										 * 
										 * } else if (body.contains(
										 * "Your Simobi Code is") &&
										 * body.contains(responseContainer.
										 * getSctl())) {
										 * 
										 * otpValue = body.substring( new
										 * String("Your Simobi Code is "
										 * ).length(), body.indexOf("(ref"));
										 * sctl = body.substring( body.indexOf(
										 * "(ref no: ") + new String("(ref no: "
										 * ).length(), body.indexOf(")"));
										 * break; } else { c.moveToNext(); }
										 * 
										 * } c.close();
										 * 
										 * if (!(otpValue == null)) {
										 * System.out.println("Testing>>SCTL");
										 * break; } else {
										 * 
										 * System.out.println(
										 * "Testing>>SCTL>>else"); if (new
										 * java.util.Date().getTime() -
										 * startTimeInMillis >=
										 * Constants.MFA_CONNECTION_TIMEOUT) {
										 * System.out.println(
										 * "Testing>>TimeOut>>"); break; }
										 * 
										 * }
										 * 
										 * }
										 * 
										 * if (otpValue == null) { //
										 * dialog1.dismiss();
										 * System.out.println(
										 * "Testing>>OTP>>null");
										 * 
										 * if
										 * (selectedLanguage.equalsIgnoreCase(
										 * "ENG")) {
										 * 
										 * alertbox.setMessage(
										 * getResources().getString(R.string.
										 * eng_transactionFail)); } else {
										 * alertbox.setMessage(
										 * getResources().getString(R.string.
										 * bahasa_transactionFail)); }
										 * 
										 * alertbox.setNeutralButton("OK", new
										 * DialogInterface.OnClickListener() {
										 * public void onClick(DialogInterface
										 * arg0, int arg1) { dialog.dismiss();
										 * 
										 * } }); alertbox.show(); } else { //
										 * dialog1.dismiss(); dialog.dismiss();
										 * System.out.println(
										 * "Testing>>OTP>>not null"); Intent
										 * intent = new
										 * Intent(TransferToUangku.this,
										 * TransferToUnagkuConfirmation.class);
										 * intent.putExtra("SRCPOCKETCODE",
										 * "2"); intent.putExtra("PIN",
										 * pinValue.getText().toString());
										 * intent.putExtra("CUST_NAME",
										 * responseContainer.getCustName()); //
										 * intent.putExtra("DEST_NUMBER",
										 * responseContainer.getDestMDN());
										 * intent.putExtra("DEST_BANK",
										 * responseContainer.getDestBank());
										 * intent.putExtra("DEST_ACCOUNT_NUM",
										 * responseContainer.getAccountNumber())
										 * ; intent.putExtra("AMOUNT",
										 * responseContainer.getAmount());
										 * intent.putExtra("AMT",
										 * amountValue.getText().toString());
										 * intent.putExtra("PTFNID",
										 * responseContainer.
										 * getEncryptedParentTxnId());
										 * intent.putExtra("TFNID",
										 * responseContainer.
										 * getEncryptedTransferId());
										 * intent.putExtra("TRANSFER_TYPE",
										 * valueContainer.getTransferType());
										 * intent.putExtra("MSG",
										 * responseContainer.getMsg());
										 * intent.putExtra("OTP", otpValue);
										 * intent.putExtra("MFA_MODE",
										 * responseContainer.getMfaMode());
										 * intent.putExtra("TRANSFER_TYPE",
										 * valueContainer.getTransferType());
										 * intent.setFlags(Intent.
										 * FLAG_ACTIVITY_CLEAR_TOP);
										 * startActivity(intent); }
										 * 
										 * } catch (Exception e) {
										 * System.out.println(
										 * "Testing>>exception>>"); }
										 **/
									} else {

										Intent intent = new Intent(TransferToUangku.this,
												TransferToUnagkuConfirmation.class);
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
						/** Service call inside the thread. */
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
					|| message.toLowerCase(Locale.getDefault()).contains("your simobi code is")) {
				Log.d(LOG_TAG, "konten sms : english");
				otpValue = message
						.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
						.trim();
				sctl = message.substring(message.indexOf("(ref no: ") + new String("(ref no: ").length(),
						message.indexOf(")"));
			}
			// Log.d(LOG_TAG, "OPT code : " + otpValue + ", sctl : " + sctl);
			edt.setText(otpValue);
		} catch (Exception e) {

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
							dialog.cancel();
							dialog.dismiss();
							Intent intent = new Intent(TransferToUangku.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							TransferToUangku.this.finish();
						}
					});
		} else {
			builderError.setTitle(getResources().getString(R.string.bahasa_otpfailed));
			builderError.setMessage(getResources().getString(R.string.bahasa_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							dialog.dismiss();
							Intent intent = new Intent(TransferToUangku.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							TransferToUangku.this.finish();
						}
					});
		}
		AlertDialog alertError = builderError.create();
		if (!((Activity) context).isFinishing()) {
			alertError.show();
		}
	}

	public void showOTPRequiredDialog(final String PIN, final String custName, final String MDN,
			final String accountNumber, final String message, final String destBank, final String amount,
			final String AMT, final String mfaMode, final String EncryptedParentTxnId,
			final String EncryptedTransferId) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TransferToUangku.this, R.style.MyAlertDialogStyle);
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
		edt.setText(otpValue);
		final String otpValue_new = edt.getText().toString();
		Log.d(LOG_TAG, "otpValue_new : " + otpValue_new + ", otpValue : " + otpValue);

		// Timer
		final TextView timer = (TextView) dialogView.findViewById(R.id.otp_timer);
		// 120 detik
		final CountDownTimer countTimer = new CountDownTimer(120000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// timer.setText(millisUntilFinished/60000 +":"+
				// (millisUntilFinished/1000));
				/**
				 * timer.setText(String.format(Locale.getDefault(),
				 * "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(
				 * millisUntilFinished),
				 * TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
				 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
				 * millisUntilFinished))));
				 **/
				NumberFormat f = new DecimalFormat("00");
				timer.setText(
						f.format(millisUntilFinished / 60000) + ":" + f.format(millisUntilFinished % 60000 / 1000));
			}

			@Override
			public void onFinish() {
				// info.setVisibility(View.GONE);
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
					dialog.cancel();
					dialog.dismiss();
					if (countTimer != null) {
						countTimer.cancel();
					}
				}
			});
		} else {
			dialogBuilder.setTitle(getResources().getString(R.string.bahasa_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.bahasa_otprequired_desc_1) + "" + mobileNumber
					+ " " + getResources().getString(R.string.bahasa_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
					dialog.dismiss();
					if (countTimer != null) {
						countTimer.cancel();
					}
				}
			});
		}
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
					otpDialogS.cancel();
				} else {
					if (countTimer != null) {
						countTimer.cancel();
					}
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
					TransferToUangku.this.finish();
				}
			}
		});
		otpDialogS = dialogBuilder.create();
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
					TransferToUangku.this.finish();
				}

			}
		});
	}

}
