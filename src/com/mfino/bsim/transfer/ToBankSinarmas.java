package com.mfino.bsim.transfer;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ToBankSinarmas extends AppCompatActivity {

	private Button btn_ok;
	private EditText pinValue, creditNoValue, amountValue;
	private AlertDialog.Builder alertbox;
	private String billerAmount;
	private String responseXml;
	ValueContainer valueContainer;
	String bankAccount;
	String otpValue, sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	SharedPreferences settings;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_bank_sinarmas);
		context = this;
		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		// screeTitle.setText("TO BANKSINARMAS");
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		mobileNumber = settings.getString("mobile", "");
		settings.edit().putString("ActivityName", "ToBankSinarmas").commit();
		settings.edit().putBoolean("isAutoSubmit", false).commit();
		Log.d(LOG_TAG, "Transfer : ToBankSinarmas");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});

		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ToBankSinarmas.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		creditNoValue = (EditText) findViewById(R.id.ed_creditACValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		TextView destAcountTxt = (TextView) findViewById(R.id.fundTransfer_toSinarmas_creditAC);
		TextView amountTxt = (TextView) findViewById(R.id.fundTransfer_toSinarmas_amount);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			screeTitle.setText(getResources().getString(R.string.eng_toBankSinarmas));
			destAcountTxt.setText(getResources().getString(R.string.eng_credit_AC_No));
			amountTxt.setText(getResources().getString(R.string.eng_amount));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {

			screeTitle.setText(getResources().getString(R.string.bahasa_toBankSinarmas));
			destAcountTxt.setText(getResources().getString(R.string.bahasa_credit_AC_No));
			amountTxt.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}

		alertbox = new AlertDialog.Builder(ToBankSinarmas.this, R.style.MyAlertDialogStyle);

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@SuppressLint({ "NewApi", "HandlerLeak" })
			@Override
			public void onClick(View arg0) {

				if (isRequiredFieldEmpty()) {

					boolean networkCheck = ConfigurationUtil.isConnectingToInternet(context);
					if (!networkCheck) {
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							ConfigurationUtil.networkDisplayDialog(
									getResources().getString(R.string.eng_serverNotRespond), context);
						} else {
							ConfigurationUtil.networkDisplayDialog(
									getResources().getString(R.string.bahasa_serverNotRespond), context);
						}

					} else if (selectedLanguage.equalsIgnoreCase("ENG")) {
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
					bankAccount = creditNoValue.getText().toString().trim();
					valueContainer = new ValueContainer();
					valueContainer.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setDestinationBankAccount(bankAccount);
					valueContainer.setSourcePin(pinValue.getText().toString().trim());
					valueContainer.setAmount(amountValue.getText().toString().trim());
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setDestinationPocketCode("2");
					valueContainer.setServiceName(Constants.SERVICE_BANK);
					valueContainer.setTransferType("toBankSinarmas");

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ToBankSinarmas.this);
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = new ProgressDialog(ToBankSinarmas.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.eng_loading));
						dialog.show();
						/**
						dialog = ProgressDialog.show(ToBankSinarmas.this, "  Bank Sinarmas               ",
								getResources().getString(R.string.eng_loading), true);
								**/
					} else {
						dialog = new ProgressDialog(ToBankSinarmas.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.bahasa_loading));
						dialog.show();
						/**
						dialog = ProgressDialog.show(ToBankSinarmas.this, "  Bank Sinarmas               ",
								getResources().getString(R.string.bahasa_loading), true);
								**/
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {
								/** Parse response xml. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
									Log.e("___responseContainer_code__msgCode___", responseContainer + "");
								} catch (Exception e) {

									e.printStackTrace();
								}

								// dialog.dismiss();

								int msgCode = 0;
								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
									Log.e("___msg_code__msgCode___", msgCode + "");
								} catch (Exception e) {
									msgCode = 0;
								}
								System.out.println("Testing>>message code>>>" + msgCode);
								if (!(msgCode == 72)) {
									Log.e("msg_code__if", msgCode + "");
									System.out.println("Testing>>not result>>>" + msgCode);
									if (responseContainer.getMsg() == null) {
										Log.e("___responseContainer.getMsg()e___", responseContainer.getMsg() + "");
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(
													getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(
													getResources().getString(R.string.bahasa_serverNotRespond));
										}
									} else {
										Log.e("________________responseContainer.getMsg()=========___",
												responseContainer.getMsg() + "");

										alertbox.setMessage(responseContainer.getMsg());
									}

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

								} else {
									System.out.println("Testing>>else>>>");
									Log.e("___else_72___", "llllllllllllllllllllllllllllllllllllllllll");

									try {
										System.out.println("Testing>>MFAMODE>>>" + responseContainer.getMfaMode());
										Log.e("___1---------.getMfaMode()___", responseContainer.getMfaMode() + "");

										if (responseContainer.getMfaMode() == null) {
											Log.e("___222---------.getMfaMode()___",
													responseContainer.getMfaMode() + "");

											valueContainer.setMfaMode("NONE");
										} else {
											Log.e("___33333333---------.getMfaMode()___",
													responseContainer.getMfaMode() + "");

											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}

									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
										Log.e("___444444---------.getMfaMode()___",
												responseContainer.getMfaMode() + "");
									}
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										Log.e("MFA MODE..", responseContainer.getMfaMode() + "");
										dialog.dismiss();
										Log.d("Widy-Debug", "Dialog OTP Required show");
										settings.edit().putString("Sctl", responseContainer.getSctl()).commit();
										showOTPRequiredDialog(pinValue.getText().toString(), responseContainer.getCustName(),
												responseContainer.getDestMDN(), responseContainer.getAccountNumber(),
												responseContainer.getMsg(), responseContainer.getDestBank(),
												responseContainer.getAmount(), responseContainer.getMfaMode(),
												responseContainer.getEncryptedParentTxnId(),
												responseContainer.getEncryptedTransferId());

										/**
										 * try {
										 * 
										 * // dialog.dismiss(); // final
										 * ProgressDialog dialog1 = //
										 * ProgressDialog.show(ToBankSinarmas.
										 * this, // " Banksinarmas ", "Please
										 * Wait // for SMS.... ", true); Long
										 * startTimeInMillis = new
										 * java.util.Date().getTime();
										 * 
										 * while (true) {
										 * 
										 * Thread.sleep(2000);
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
										 * Log.e("bodyyyyyyyyyy..", body + "");
										 * String number =
										 * c.getString(c.getColumnIndexOrThrow(
										 * "address")) .toString();
										 * Log.e("number,,,,,,number..", number
										 * + ""); if (body.contains(
										 * "Kode Simobi Anda") &&
										 * body.contains(responseContainer.
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
										 * System.out.println("Testing>>OTP>>" +
										 * otpValue); if (otpValue == null) { //
										 * dialog1.dismiss();
										 * System.out.println(
										 * "Testing>>OTP>>null");
										 * dialog.dismiss(); } else {
										 * dialog.dismiss(); } } catch
										 * (Exception e) { System.out.println(
										 * "Testing>>exception>>"); }
										 **/
									} else {

										System.out.println("Testing>>OTP else>>>");
										/*
										 * Intent intent = new
										 * Intent(ToBankSinarmas.this,
										 * ConfirmAddReceiver.class);
										 * System.out.print("Testing>>name>>"+
										 * responseContainer.getCustName());
										 * System.out.print("Testing>numbaer>>>"
										 * +responseContainer.getDestMDN());
										 * System.out.print("Testing>>ac>>"+
										 * responseContainer.getAccountNumber())
										 * ; intent.putExtra("PIN",
										 * pinValue.getText().toString());
										 * intent.putExtra("MSG",
										 * responseContainer.getMsg());
										 * intent.putExtra("CUST_NAME",
										 * responseContainer.getCustName());
										 * //intent.putExtra("DEST_NUMBER",
										 * responseContainer.getDestMDN());
										 * intent.putExtra("DEST_BANK",
										 * responseContainer.getDestBank());
										 * intent.putExtra("DEST_ACCOUNT_NUM",
										 * responseContainer.getAccountNumber())
										 * ; intent.putExtra("AMOUNT",
										 * responseContainer.getAmount());
										 * intent.putExtra("DEST", bankAccount);
										 * intent.putExtra("AMT", billerAmount);
										 * intent.putExtra("PTFNID",
										 * responseContainer.
										 * getEncryptedParentTxnId());
										 * intent.putExtra("TFNID",
										 * responseContainer.
										 * getEncryptedTransferId());
										 * intent.putExtra("TRANSFER_TYPE",
										 * valueContainer.getTransferType());
										 */

										Intent intent = new Intent(ToBankSinarmas.this, ConfirmAddReceiver.class);
										System.out.print("Testing>>name>>" + responseContainer.getCustName());
										System.out.print("Testing>numbaer>>>" + responseContainer.getDestMDN());
										System.out.print("Testing>>ac>>" + responseContainer.getAccountNumber());
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("MSG", responseContainer.getMsg());
										intent.putExtra("CUST_NAME", responseContainer.getCustName());
										// intent.putExtra("DEST_NUMBER",
										// responseContainer.getDestMDN());
										intent.putExtra("DEST_BANK", responseContainer.getDestBank());
										intent.putExtra("DEST_ACCOUNT_NUM", responseContainer.getAccountNumber());
										intent.putExtra("AMOUNT", responseContainer.getAmount());
										intent.putExtra("DEST", bankAccount);
										intent.putExtra("AMT", billerAmount);
										// intent.putExtra("OTP", otpValue);
										intent.putExtra("MFA_MODE", responseContainer.getMfaMode());
										intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
										intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
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

		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		creditNoValue = (EditText) findViewById(R.id.ed_creditACValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);

		if (!(pinValue.getText().toString().equals("")) && !(creditNoValue.getText().toString().equals(""))
				&& !(amountValue.getText().toString().equals(""))) {
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
			if (message.contains("Kode Simobi Anda ") || message.toLowerCase(Locale.getDefault()).contains("kode simobi anda ")) {
				Log.d(LOG_TAG, "konten sms : indonesia");
				otpValue = message.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "),
						message.indexOf("(")).trim();
				sctl = message.substring(message.indexOf(":") + 1, message.indexOf(")"));
			} else if (message.contains("Your Simobi Code is ") || message.toLowerCase(Locale.getDefault()).contains("your simobi code is ")) {
				Log.d(LOG_TAG, "konten sms : english");
				otpValue = message.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "),
						message.indexOf("(")).trim();
				sctl = message.substring(message.indexOf("(ref no: ") + new String("(ref no: ").length(),
						message.indexOf(")"));
			}
			Log.d(LOG_TAG, "OPT code : " + otpValue + ", sctl : " + sctl);
			edt.setText(otpValue);
		} catch (Exception e) {

		}
	}

	public void errorOTP() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ToBankSinarmas.this, R.style.MyAlertDialogStyle);
		builder.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builder.setTitle("OTP Verification Failed");
			builder.setMessage("Please enter the code within specified time limit.").setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(ToBankSinarmas.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		} else {
			builder.setTitle("Verifikasi OTP Gagal");
			builder.setMessage("Silakan masukan kode OTP sebelum batas waktu yang ditentukan").setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(ToBankSinarmas.this, HomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});
		}
		AlertDialog alert = builder.create();
		if(!((Activity) context).isFinishing())
		{
			alert.show();
		}
	}

	public void showOTPRequiredDialog(final String PIN, final String custName, final String MDN, final String accountNumber,
			final String message, final String destBank, final String amount, final String mfaMode,
			final String EncryptedParentTxnId, final String EncryptedTransferId) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ToBankSinarmas.this, R.style.MyAlertDialogStyle);
		LayoutInflater inflater = this.getLayoutInflater();
		final ViewGroup nullParent = null;
		dialogBuilder.setCancelable(false);
		final View dialogView = inflater.inflate(R.layout.otp_dialog, nullParent);
		dialogBuilder.setView(dialogView);

		// EditText OTP
		edt = (EditText) dialogView.findViewById(R.id.otp_value);
		//edt.setText(otpValue);
		//final String otpValue_new = edt.getText().toString();
		Log.d(LOG_TAG, "otpValue : " + otpValue);

		// Timer
		final TextView timer = (TextView) dialogView.findViewById(R.id.otp_timer);
		// 120detik
		new CountDownTimer(120000, 1000) {
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
		}.start();

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialogBuilder.setTitle(getResources().getString(R.string.eng_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.eng_otprequired_desc_1) + "" + mobileNumber + " "
					+ getResources().getString(R.string.eng_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
		} else {
			dialogBuilder.setTitle(getResources().getString(R.string.bahasa_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.bahasa_otprequired_desc_1) + "" + mobileNumber
					+ " " + getResources().getString(R.string.bahasa_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
		}
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
				} else {
					Intent intent = new Intent(ToBankSinarmas.this, ConfirmAddReceiver.class);
					intent.putExtra("PIN", PIN);
					intent.putExtra("MSG", message);
					intent.putExtra("CUST_NAME", custName);
					// intent.putExtra("DEST_NUMBER",
					// responseContainer.getDestMDN());
					intent.putExtra("DEST_BANK", destBank);
					intent.putExtra("DEST_ACCOUNT_NUM", accountNumber);
					intent.putExtra("AMOUNT", amount);
					intent.putExtra("DEST", bankAccount);
					intent.putExtra("AMT", billerAmount);
					intent.putExtra("OTP", edt.getText().toString());
					intent.putExtra("MFA_MODE", mfaMode);
					intent.putExtra("PTFNID", EncryptedParentTxnId);
					intent.putExtra("TFNID", EncryptedTransferId);
					intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
					startActivity(intent);
					/**
					Log.d(LOG_TAG, "PIN : " + PIN);
					Log.d(LOG_TAG, "MSG : " + message);
					Log.d(LOG_TAG, "CUST_NAME : " + custName);
					Log.d(LOG_TAG, "DEST_BANK : " + destBank);
					Log.d(LOG_TAG, "DEST_ACCOUNT_NUM : " + accountNumber);
					Log.d(LOG_TAG, "AMOUNT : " + amount);
					Log.d(LOG_TAG, "DEST : " + bankAccount);
					Log.d(LOG_TAG, "AMT : " + billerAmount);
					Log.d(LOG_TAG, "OTP : " + edt.getText().toString());
					Log.d(LOG_TAG, "MFA_MODE : " + mfaMode);
					Log.d(LOG_TAG, "PTFNID : " + EncryptedParentTxnId);
					Log.d(LOG_TAG, "TFNID : " + EncryptedTransferId);
					Log.d(LOG_TAG, "TRANSFER_TYPE : " + valueContainer.getTransferType());
					**/
				}
			}
		});
		final AlertDialog b = dialogBuilder.create();
		b.show();
		((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		edt.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before,
		            int count) {
		    }
		
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count,
		            int after) {
		    }
		
		    @Override
		    public void afterTextChanged(Editable s) {
		        // Check if edittext is empty
		        if (TextUtils.isEmpty(s)) {
		            // Disable ok button
		            ((AlertDialog) b).getButton(
		                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		        } else {
		            // Something into edit text. Enable the button.
		            ((AlertDialog) b).getButton(
		                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
		        }
		        Boolean isAutoSubmit = settings.getBoolean("isAutoSubmit", false);
		        if((edt.getText().length()>3) && (isAutoSubmit == true)){
		        	Intent intent = new Intent(ToBankSinarmas.this, ConfirmAddReceiver.class);
		        	intent.putExtra("PIN", PIN);
					intent.putExtra("MSG", message);
					intent.putExtra("CUST_NAME", custName);
					intent.putExtra("DEST_BANK", destBank);
					intent.putExtra("DEST_ACCOUNT_NUM", accountNumber);
					intent.putExtra("AMOUNT", amount);
					intent.putExtra("DEST", bankAccount);
					intent.putExtra("AMT", billerAmount);
					intent.putExtra("OTP", edt.getText().toString());
					intent.putExtra("MFA_MODE", mfaMode);
					intent.putExtra("PTFNID", EncryptedParentTxnId);
					intent.putExtra("TFNID", EncryptedTransferId);
					intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
					startActivity(intent);
		        }
		
		    }
		});
	}
}
