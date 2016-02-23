/* Created by Srikanth gr.
 */

package com.mfino.bsim.flashiz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mobey.android.api.intern.FlashizServices;
import com.mobey.android.api.intern.FlashizUrlBuilder;
import com.mobey.android.api.model.Error;
import com.mobey.android.fragment.home.banksdk.BankSDKLoadingListener;
import com.mobey.fragment.abs.SDKLinkFragmentActivity;
import com.mobey.fragment.intern.listener.BankSDKCallBackListener;
import com.mobey.fragment.intern.listener.SDKCallBackListener;
import com.neopixl.fragment.NPFragment;

public class QRPayment extends SDKLinkFragmentActivity implements
		SDKCallBackListener, BankSDKCallBackListener {

	// Declare

	Context context;
	SharedPreferences languageSettings, settings;
	// FlashiZ
	String selectedLanguage;

	public String userApiKey, otp, parentTxnId, txnId, SctlI = "";
	String mInVoiceId, mAmount, mMarchantName, mfa;
	ProgressDialog dialogCon;
	String otpValue, sctl;
	String mfaMode;
	String pin;
	String loyalityName;
	String discountAmount;
	String discountType;
	String numberOfCoupuns;
	String QRBillerCode = "QRFLASHIZ";
	int msgCode;
	Editor editor;

	Button gantiPin, lupaPin, gantiMail, gantiSecretQuestion, editProfile,
			customerCareSerivce, isiSaldo, tarikTunai, RegistrasiUangku,
			Dimopay, TandC, Kebijakan;
	String sqExist, securityQestion, resetPinByEmail, resetPinByCSR,
			emailVerfied;
	ProgressDialog dialog;
	private String responseXml;
	private AlertDialog.Builder alertbox;
	EditText pinValue;
	ValueContainer valueContainer;

	@Override
	protected void onResume() {
		Log.e("-Nagendra palepu------", "---onResume------");

		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("-Nagendra palepu------", "---onPause------");
		// unregisterReceiver(broadcastReceiver);
	}

	/*
	 * BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intenta) {
	 * 
	 * String body = intenta.getExtras().getString("message"); Log.e("=-------",
	 * "---------------" + body);
	 * 
	 * if (body.contains("Kode Simobi Anda") && body.contains(SctlI)) {
	 * 
	 * otpValue = body.substring( new String("Kode Simobi Anda ").length(),
	 * body.indexOf("(no ref")); sctl = body.substring(body.indexOf(":") + 1,
	 * body.indexOf(")")); handler2.removeCallbacks(runnable);
	 * billPayConfirmation();
	 * 
	 * } else if (body.contains("Your Simobi Code is") && body.contains(SctlI))
	 * {
	 * 
	 * otpValue = body.substring( new String("Your Simobi Code is ").length(),
	 * body.indexOf("(ref")); sctl = body.substring(body.indexOf("(ref no: ") +
	 * new String("(ref no: ").length(), body.indexOf(")"));
	 * handler2.removeCallbacks(runnable); billPayConfirmation(); }
	 * 
	 * } };
	 */

	Handler handler2 = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			dialog.dismiss();
			if (otpValue == null) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.prompts, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				alertDialogBuilder.setView(promptsView);
				alertDialogBuilder.setCancelable(false);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
				final Button confirm = (Button) promptsView
						.findViewById(R.id.btn_EnterPin_Ok);
				final Button cancel = (Button) promptsView
						.findViewById(R.id.btn_EnterPin_cancel);
				final AlertDialog alertDialog = alertDialogBuilder.create();

				confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// dialog.cancel();
						alertDialog.dismiss();
						InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						otpValue = userInput.getText().toString();
						billPayConfirmation();
					}
				});

				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// dialog.cancel();
						alertDialog.dismiss();
						Intent intent = new Intent(getBaseContext(),
								HomeScreen.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				});

				alertDialog.show();
				alertDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.argb(0, 200,
								200, 200)));

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		context = this;
		alertbox = new AlertDialog.Builder(context);
		settings = getSharedPreferences("LOGIN_PREFERECES",
				Context.MODE_PRIVATE);

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",
				Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		settings = getSharedPreferences("LOGIN_PREFERECES",
				Context.MODE_PRIVATE);
		// OTP Flow
		settings.edit().putBoolean("OTP_SCREEN", false).commit();
		userApiKey = settings.getString("userApiKey", "NONE");
		Log.e("userApiKey---2222222------", userApiKey);

		getSupportActionBar().hide();
		// pin = this.getIntent().getExtras().getString("PIN");
		if (userApiKey.equalsIgnoreCase("NONE")) {
			Log.e("userApiKey---33333--3333------", userApiKey);

			getUserAPIKey();
		} else {

			getSupportActionBar().show();

			// Flashiz SDK start https://sandbox.flashiz.co.id
			// FlashizServices.setFlashizServerURL("https://uat.flashiz.co.id");
			// FlashizServices.setFlashizServerURL("https://dev.flashiz.co.id");
			 FlashizServices
			 .setFlashizServerURL(FlashizUrlBuilder.BANK_SDK_SERVER_SANDBOX_URL);
			/*FlashizServices
					.setFlashizServerURL(FlashizUrlBuilder.BANK_SDK_SERVER_PROD_URL);*/
			// FlashizServices.setFlashizServerURL("my.flashiz.co.id");//production
			startBankSDK((BankSDKCallBackListener) getContext(),
					R.id.activity_sample_rl, null);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("###############", "****************");

		if (requestCode == 1) {
			if (resultCode == 2) {

				pin = data.getExtras().getString("PIN");
				Log.e("----------",
						"=-------" + data.getExtras().getString("PIN"));
				userApiKey = settings.getString("userApiKey", "NONE");
				billPayInquiry(mInVoiceId, pin, mAmount, mMarchantName,
						loyalityName, discountAmount, discountType,
						numberOfCoupuns);
			}
		}
	}

	/*******************************************************************************
	 * Flashiz Callback methods
	 *******************************************************************************/
	@Override
	public boolean callBackTransactionReject() {
		return false;
	}

	@Override
	public boolean callBackTransactionSuccess() {
		getSupportActionBar().hide();
		openMainMenu();
		return true;
	}

	@Override
	public void callBackLostConnexion() {
	}

	@Override
	public boolean callBackInvalidQRCode() {
		getSupportActionBar().hide();
		// openMainMenu();
		SDKLinkFragmentActivity.resetActionBar();
		// openMainMenu();
		finish();
		return false;
	}

	@Override
	public boolean callBackUnknowError(Error error) {
		getSupportActionBar().hide();
		SDKLinkFragmentActivity.resetActionBar();
		// openMainMenu();
		finish();
		return false;
	}

	@Override
	public void callBackCloseSDK() {
		getSupportActionBar().hide();
		SDKLinkFragmentActivity.resetActionBar();
		finish();

	}

	@Override
	public boolean callBackUserHasCancelTransaction() {
		return false;
	}

	@Override
	public void callBackGenerateUserKey(final BankSDKLoadingListener listener) {
		Log.e("-000000000000===Ramya___111", "" + userApiKey);

		if (userApiKey.equalsIgnoreCase("NONE")) {
			listener.registerUserApiKey(null);

		} else {
			Log.e("-000000000000===Ramya", "" + userApiKey);
			listener.registerUserApiKey(userApiKey);
			/*
			 * UserSession.getInstance(getContext()).setUserKey(userApiKey);
			 * userKeyAvailable();
			 */
		}

	}

	@Override
	public void callBackPayInvoice(String invoiceID, double amount,
			double discountedAmount, int numberOfCoupons1, String merchantName,
			String discountType1, String loyaltyProgramName,
			double amountOfDiscount) {

		loyalityName = loyaltyProgramName;
		discountAmount = amountOfDiscount + "";
		discountType = discountType1 + "";
		mInVoiceId = invoiceID;
		mAmount = discountedAmount + "";
		mMarchantName = merchantName;
		numberOfCoupuns = numberOfCoupons1 + "";
		
		Log.e("mInVoiceId--------", mInVoiceId);
		// Log.e("pin--------", pin);
		Log.e("mAmount-------", mAmount);
		Log.e("mMarchantName--------", mMarchantName);
		Log.e("loyalityName---------", loyalityName);
		Log.e("discountAmount----------", discountAmount);
		Log.e("discountType---------", discountType);
		Log.e("numberOfCoupuns", numberOfCoupuns);

		if(settings.getString("invoiceId", "").equals("")){
			settings.edit().putString("invoiceId", mInVoiceId).commit();
		}
		Intent i = new Intent(this, QRPin1.class);
		startActivityForResult(i, 1);
		/*
		 * billPayInquiry(mInVoiceId, pin, mAmount, mMarchantName, loyalityName,
		 * discountAmount, discountType, numberOfCoupuns);
		 */
	}

	@Override
	public int callBackTimeBeforeStartPoll() {
		return 5000;
	}

	@Override
	public int callBackTimeBetweenEachPoll() {
		return 1000;
	}

	@Override
	public void callBackUserHasCancelledEula() {

	}

	@Override
	public void callBackUserHasConfirmEula() {
	}

	@Override
	public boolean debugMode() {
		return false;
	}

	@Override
	public NPFragment showEULA() {
		// You can use this pre-configured EULA fragment
		// return BankSDKEulaFragment.newInstance(this,
		// "http://www.google.com");
		/**
		 * Or define a new EULA fragment by yourself (extends with NPFragment
		 * class !) Don't forget to take the BankSDKCallBackListener !!!
		 * 
		 * 
		 * SDKLinkFragmentActivity activity = (SDKLinkFragmentActivity)
		 * getActivity(); if(activity != null){
		 * if(BBAppManager.isInDebugMode()){ NPLog.d("User cancel CGU"); }
		 * UserSession.getInstance().setUserKey(""); activity.closeSDK();
		 * getListener().callBackUserHasCancelledEula(); }else{
		 * NPLog.e("can't start fragment (activity = null"); }
		 * 
		 * If user accept:
		 * 
		 * SDKLinkFragmentActivity activity = (SDKLinkFragmentActivity)
		 * getActivity(); if(activity != null){
		 * if(BBAppManager.isInDebugMode()){ NPLog.d("User accept CGU"); }
		 * UserSession.getInstance().setUserEulaState(true);
		 * getListener().callBackUserHasConfirmEula();
		 * activity.removeFragment(getMySelfFragment()); }else{
		 * NPLog.e("can't start fragment (activity = null"); }
		 * 
		 * You can found an full example in MyCustomEULA class
		 */
		return MyCustomEULA.newInstance(this);
	}

	// Get User API Key
	private void getUserAPIKey() {

		valueContainer = new ValueContainer();
		// valueContainer.setContext(QRPayment.this);
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setTransactionName(Constants.TRANSACTION_USER_APIKEY);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(
				valueContainer, QRPayment.this);

		dialog = ProgressDialog.show(QRPayment.this,
				"  Mohon tunggu sebentar        ",
				getResources().getString(R.string.bahasa_loading), true);

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {

					XMLParser obj = new XMLParser();
					/** Parsing of response. */
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer
								.getMsgCode());
						System.out.println(">>>>>>>>"
								+ responseContainer.getMsg());

					} catch (Exception e) {

						msgCode = 0;
					}

					dialog.dismiss();
					if (msgCode == 2103) {
						userApiKey = responseContainer.getUserApiKey();
						settings.edit()
								.putString("userApiKey",
										responseContainer.getUserApiKey())
								.commit();
						// UserAPI Key
						userApiKey = settings.getString("userApiKey", "NONE");
						Log.e("userApiKey----111111-----", userApiKey);

						getSupportActionBar().show();

						// Flashiz SDK start https://sandbox.flashiz.co.id
						// FlashizServices.setFlashizServerURL("https://uat.flashiz.co.id");
					
						// FlashizServices.setFlashizServerURL("https://dev.flashiz.co.id");
						 FlashizServices
						 .setFlashizServerURL(FlashizUrlBuilder.BANK_SDK_SERVER_SANDBOX_URL);
						/*FlashizServices
								.setFlashizServerURL(FlashizUrlBuilder.BANK_SDK_SERVER_PROD_URL);*/
						startBankSDK((BankSDKCallBackListener) getContext(),
								R.id.activity_sample_rl, null);
						/*
						 * Intent intent = new Intent(QRPin.this, QRPin.class);
						 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						 * intent.putExtra("SCREEN", "HOME");
						 * startActivity(intent);
						 */
					} else {
						userApiKey = "NONE";
						String massage = null;
						try {
							massage = responseContainer.getMsg();
						} catch (Exception e) {
							e.printStackTrace();
							massage = getResources().getString(
									R.string.bahasa_serverNotRespond);
						}
						alertbox = new AlertDialog.Builder(context);
						alertbox.setMessage(massage);
						alertbox.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,
											int arg1) {
										finish();
									}
								});
						alertbox.show();
					}

				} else {
					dialog.dismiss();
					alertbox = new AlertDialog.Builder(context);
					alertbox.setMessage(getResources().getString(
							R.string.bahasa_serverNotRespond));
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									finish();
								}
							});
					alertbox.show();
				}

			}
		};

		final Thread checkUpdate = new Thread() {
			/**
			 * Service call in thread in and getting response as xml in string.
			 */
			public void run() {

				try {
					responseXml = webServiceHttp
							.getResponseSSLCertificatation();
				} catch (Exception e) {
					responseXml = null;

				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();
	}

	public void displayDialog(String msg) {
		alertbox = new AlertDialog.Builder(QRPayment.this);
		alertbox.setMessage(msg);
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		alertbox.show();
	}

	@Override
	public void onBackPressed() {
		/*
		 * getSupportActionBar().hide();
		 * SDKLinkFragmentActivity.resetActionBar(); openMainMenu();
		 */
		finish();
	}

	/*******************************************************************************
	 * Flashiz Bill Payment Inquiry
	 *******************************************************************************/

	// BillPay inquiry
	private void billPayInquiry(String invoiceID, String pin, String amount,
			String merchantName, String loyalityName, String discountAmount,
			String discountType, String numberofCoupuns) {
		// registerReceiver(broadcastReceiver, new
		// IntentFilter("com.mfino.bsim"));
		valueContainer = new ValueContainer();
		valueContainer.setContext(QRPayment.this);
		valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
		valueContainer
				.setTransactionName(Constants.TRANSACTION_QR_BILLPAYMENT_INQUIRY);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePin(pin);
		valueContainer.setSourcePocketCode(getResources().getString(
				R.string.source_packet_code));
		valueContainer.setAmount(amount);
		valueContainer.setPaymentMode(Constants.TRANSACTION_QR_PAYMENT);
		try {
			merchantName = URLEncoder.encode(merchantName,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		    Log.d("TEST", merchantName);		
		    valueContainer.setMerchantName(merchantName.replace(" ", "+"));
		valueContainer.setBillerCode(QRBillerCode);
		
		valueContainer.setBillNo(invoiceID);
		valueContainer.setUserApiKey(settings.getString("userApiKey", "NONE"));
		valueContainer.setChannelId("7");
		// New Parameters.
		//valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));
		
		try {
			loyalityName = URLEncoder.encode(loyalityName,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));
		valueContainer.setDiscountAmount(discountAmount);
		valueContainer.setDiscountType(discountType);
		valueContainer.setNumberofCoupuns(numberofCoupuns);
		Log.e("====================",
				"==================" + valueContainer.toString());
		final WebServiceHttp webServiceHttp = new WebServiceHttp(
				valueContainer, QRPayment.this);
		dialog = ProgressDialog.show(QRPayment.this,
				"  Banksinarmas                ",
				getResources().getString(R.string.eng_loading), true);

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				if (responseXml != null) {
					/** Parse the response. */
					XMLParser obj = new XMLParser();
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer
								.getMsgCode());
					} catch (Exception e) {
						msgCode = 0;
						// e.printStackTrace();
					}

					// dialog.dismiss();
					if (!((msgCode == 72) || (msgCode == 2109) || (msgCode == 713))) {
						Log.e("msg_code", msgCode + "--11--------");
						if (msgCode == 631) {
							Log.e("msg_code", msgCode + "--22--------");

							alertbox = new AlertDialog.Builder(QRPayment.this);
							alertbox.setMessage(responseContainer.getMsg());
							alertbox.setNeutralButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface arg0, int arg1) {

											Intent intent = new Intent(
													getBaseContext(),
													LoginScreen.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											finish();
										}
									});
							alertbox.show();

						} else if (msgCode == 29) {
							// dialog.dismiss();
							Log.e("msg_code", msgCode + "--33--------");

							alertbox = new AlertDialog.Builder(QRPayment.this);
							alertbox.setMessage(responseContainer.getMsg());
							alertbox.setNeutralButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface arg0, int arg1) {
											/*
											 * Intent intent = new Intent(
											 * QRPayment.this,
											 * HomeScreen.class);
											 * intent.setFlags
											 * (Intent.FLAG_ACTIVITY_CLEAR_TOP);
											 * startActivity(intent);
											 */
											// closeSDK();
											Intent intent = new Intent(
													QRPayment.this,
													QRPayment.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											openMainMenu();
											finish();
										}
									});
							alertbox.show();

						} else {
							displayDialog(responseContainer.getMsg());
						}

					} else {

						try {
							// System.out.println("Testing>>mfa>>"+responseContainer.getMfaMode());
							if (responseContainer.getMfaMode()
									.equalsIgnoreCase("OTP")) {

								mfa = responseContainer.getMfaMode();
								// valueContainer.setMfaMode(mfa);
							} else {
								// valueContainer.setMfaMode("NONE");
								mfa = "NONE";
							}

						} catch (Exception e1) {

							// valueContainer.setMfaMode("NONE");
							mfa = "NONE";
						}
						if (mfa.equalsIgnoreCase("OTP")) {
							try {

								// final ProgressDialog dialog1 =
								// ProgressDialog.show(SampleActivity.this,
								// "  Banksinarmas               ",
								// "Please Wait for SMS....   ", true);
								parentTxnId = responseContainer
										.getEncryptedParentTxnId();
								txnId = responseContainer
										.getEncryptedTransferId();
								SctlI = responseContainer.getSctl();
								Long startTimeInMillis = new java.util.Date()
										.getTime();
								while (true) {

									Thread.sleep(2000);
									final Uri SMS_INBOX = Uri
											.parse("content://sms/inbox");
									Cursor c = getContentResolver().query(
											SMS_INBOX, null, null, null,
											"DATE desc");
									c.moveToFirst();
									for (int i = 0; i < 10; i++) {
										String body = c
												.getString(
														c.getColumnIndexOrThrow("body"))
												.toString().trim();
										String number = c
												.getString(
														c.getColumnIndexOrThrow("address"))
												.toString();

										if (body.contains("Kode Simobi Anda")
												&& body.contains(responseContainer
														.getSctl())) {

											otpValue = body
													.substring(
															new String(
																	"Kode Simobi Anda ")
																	.length(),
															body.indexOf("(no ref"));
											sctl = body.substring(
													body.indexOf(":") + 1,
													body.indexOf(")"));
											dialog.dismiss();

											break;

										} else if (body
												.contains("Your Simobi Code is")
												&& body.contains(responseContainer
														.getSctl())) {

											otpValue = body
													.substring(
															new String(
																	"Your Simobi Code is ")
																	.length(),
															body.indexOf("(ref"));
											sctl = body
													.substring(
															body.indexOf("(ref no: ")
																	+ new String(
																			"(ref no: ")
																			.length(),
															body.indexOf(")"));
											break;
										} else {
											c.moveToNext();
										}
									}
									c.close();

									if (!(otpValue == null)) {

										break;
									} else {
										Log.e("=============",
												"=============nagendra"
														+ (new java.util.Date()
																.getTime()
																- startTimeInMillis >= Constants.MFA_CONNECTION_TIMEOUT));
										if (new java.util.Date().getTime()
												- startTimeInMillis >= Constants.MFA_CONNECTION_TIMEOUT) {

											/*
											 * handler2.removeCallbacks(runnable)
											 * ; handler2.postDelayed(runnable,
											 * 1000);
											 */
											break;
										}
									}

								}

								if (otpValue == null) {
									// dialog.dismiss();
									System.out.println("Testing>>OTP>>null");

									if (selectedLanguage
											.equalsIgnoreCase("ENG")) {
										// dialog.dismiss();
										displayDialog(getResources().getString(
												R.string.eng_transactionFail));

									} else {
										dialog.dismiss();
										displayDialog(getResources()
												.getString(
														R.string.bahasa_transactionFail));
									}
								} else {
									dialog.dismiss();
									billPayConfirmation();
								}

								// handler2.removeCallbacks(runnable);
								// handler2.postDelayed(runnable, 20000);

							} catch (Exception e) {
								if (!(otpValue == null)) {
									billPayConfirmation();
								} else {

									/*
									 * handler2.removeCallbacks(runnable);
									 * handler2.postDelayed(runnable, 30000);
									 */
								}
							}
						} else {
							if (otp == null) {

								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									// dialog.dismiss();
									displayDialog(getResources().getString(
											R.string.eng_transactionFail));

								} else {
									// dialog.dismiss();
									displayDialog(getResources().getString(
											R.string.bahasa_transactionFail));
								}
							}

							dialog.dismiss();
							otp = otpValue;
							parentTxnId = responseContainer
									.getEncryptedParentTxnId();
							txnId = responseContainer.getEncryptedTransferId();
							billPayConfirmation();

						}
					}

				} else {

					dialog.dismiss();
					displayDialog(getResources().getString(
							R.string.bahasa_serverNotRespond));
				}

			}
		};

		final Thread checkUpdate = new Thread() {
			public void run() {
				try {
					responseXml = webServiceHttp
							.getResponseSSLCertificatation();
					Log.e("=========", "====Nagendra Palepu========="
							+ responseXml);
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();

	}

	/*******************************************************************************
	 * Flashiz Bill Payment Confirmation
	 *******************************************************************************/

	private void billPayConfirmation() {

		/** Set Parameters for Service calling . */
		valueContainer = new ValueContainer();
		valueContainer.setContext(QRPayment.this);
		valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
		valueContainer.setTransactionName(Constants.TRANSACTION_QR_PAYMENT);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePocketCode(getResources().getString(
				R.string.source_packet_code));
		valueContainer.setConfirmed("true");
		valueContainer.setAmount(mAmount);
		valueContainer.setSourcePocketCode(getResources().getString(
				R.string.source_packet_code));
		valueContainer.setPaymentMode(Constants.TRANSACTION_QR_PAYMENT);
		//valueContainer.setMerchantName(mMarchantName.replace(" ", "+"));
		try {
			mMarchantName = URLEncoder.encode(mMarchantName,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		    Log.d("TEST", mMarchantName);		
		    valueContainer.setMerchantName(mMarchantName.replace(" ", "+"));
		valueContainer.setBillerCode(QRBillerCode);
		valueContainer.setBillNo(mInVoiceId);
		valueContainer.setUserApiKey(settings.getString("userApiKey", "NONE"));
		valueContainer.setParentTxnId(parentTxnId);
		valueContainer.setTransferId(txnId);

		// New Parameters.
		//valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));
		
		try {
			loyalityName = URLEncoder.encode(loyalityName,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));
	
		valueContainer.setDiscountAmount(discountAmount);
		valueContainer.setDiscountType(discountType);
		valueContainer.setNumberofCoupuns(numberOfCoupuns);

		try {
			if (mfa.equalsIgnoreCase("OTP")) {

				valueContainer.setOTP(otpValue);
				valueContainer.setMfaMode(mfa);
			}
		} catch (Exception e1) {

		}

		final WebServiceHttp webServiceHttp = new WebServiceHttp(
				valueContainer, QRPayment.this);
		dialogCon = ProgressDialog.show(QRPayment.this,
				"   Banksinarmas                 ",
				getResources().getString(R.string.bahasa_loading), true);

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {
					XMLParser obj = new XMLParser();
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer
								.getMsgCode());
					} catch (Exception e) {

						e.printStackTrace();
						msgCode = 0;
					}
					dialogCon.dismiss();

					if (!((Integer.parseInt(responseContainer.getMsgCode()) == 2111) || (Integer
							.parseInt(responseContainer.getMsgCode()) == 715))) {
						displayDialog(responseContainer.getMsg());

					} else {
						// Hand over control to Flashiz
						dialogCon.dismiss();

					}

				} else {

					dialogCon.dismiss();
					displayDialog(getResources().getString(
							R.string.bahasa_serverNotRespond));
				}

			}
		};

		final Thread checkUpdate = new Thread() {
			public void run() {

				try {
					responseXml = webServiceHttp
							.getResponseSSLCertificatation();
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};

		checkUpdate.start();

	}

	public void openMainMenu() {

		Intent inent = new Intent(QRPayment.this, HomeScreen.class);
		inent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(inent);
		finish();
	}

}
