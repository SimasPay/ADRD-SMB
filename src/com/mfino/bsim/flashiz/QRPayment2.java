package com.mfino.bsim.flashiz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDK.SDKLocale;
import com.dimo.PayByQR.PayByQRSDK.ServerURL;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.UserAPIKeyListener;
import com.dimo.PayByQR.model.InvoiceModel;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class QRPayment2 extends AppCompatActivity implements PayByQRSDKListener {
	private PayByQRSDK payByQRSDK;
	private int module;
	private String userApiKey, PayInAppInvoiceID, PayInAppURLCallback;
	private String otp = "", parentTxnId, txnId;
	private ValueContainer valueContainer;
	private String responseXml;
	private int msgCode;
	private static String mInVoiceId, mAmount, mMarchantName, loyalityName, discountAmount, discountType,
			numberOfCoupuns, redeemAmount, redeemPoints, tipAmount;
	private String mfa;
	private String QRBillerCode = "QRFLASHIZ";
	private SharedPreferences languageSettings, settings;
	String selectedLanguage;
	// private AlertDialog.Builder alertbox;
	// private ProgressDialog dialog;
	// ProgressDialog dialogCon;
	String otpValue = "", mpin = "", sctl;
	String mfaMode;
	String mobileNumber;
	public static final String LOG_TAG = "SIMOBI";
	static EditText edt;
	private Context context;
	private String DIMO_PREF = "com.mfino.bsim.paybyqr.Preference";
	private String DIMO_PREF_USERKEY = "com.mfino.bsim.paybyqr.UserKey";
	public static final String QR_STORE_DB = "com.mfino.bsim.QrStore.db";
	public static final String INTENT_EXTRA_MODULE = "com.mfino.bsim.paybyqr.module";
	public static final String INTENT_EXTRA_INVOICE_ID = "com.mfino.bsim.paybyqr.invoiceID";
	public static final String INTENT_EXTRA_URL_CALLBACK = "com.mfino.bsim.paybyqr.URLCallback";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		mobileNumber = settings.getString("mobile", "");
		settings.edit().putString("ActivityName", "QRPayment2").commit();
		settings.edit().putBoolean("isAutoSubmit", false).commit();
		Log.d(LOG_TAG, "PayByQR : QRPayment2");
		context = this;
		payByQRSDK = new PayByQRSDK(this, this);
		payByQRSDK.setServerURL(ServerURL.SERVER_URL_DEV);
		// payByQRSDK.setServerURL(ServerURL.SERVER_URL_LIVE);
		payByQRSDK.setIsUsingCustomDialog(false);
		payByQRSDK.setIsPolling(false);
		DBHelper mydb = new DBHelper(this);
		Cursor rs = mydb.getFlashizData();
		Log.e("countttt", rs.getCount() + "");
		if (rs.getCount() != 0) {

			while (rs.moveToNext()) {

				String session_value = rs.getString(rs.getColumnIndex("session_value"));

				if (session_value.equalsIgnoreCase("false")) {
					payByQRSDK.setEULAState(false);
				} else {
					payByQRSDK.setEULAState(true);
				}
			}
		} else {
			Log.e("Nodata_founddd", "*******************");

			// Log.e("cursor-----count_****************",
			// rs2.getCount()+"");
		}

		payByQRSDK.setMinimumTransaction(500);

		Log.e("eula_state_qr2", payByQRSDK.getEULAState() + "");

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		settings = getSharedPreferences("LOGIN_PREFERECES", Context.MODE_PRIVATE);
		userApiKey = settings.getString("userApiKey", "NONE");
		Log.e("userApiKey---2222222------", userApiKey);

		if (selectedLanguage.equalsIgnoreCase("ENG"))
			payByQRSDK.setSDKLocale(SDKLocale.ENGLISH);
		else
			payByQRSDK.setSDKLocale(SDKLocale.INDONESIAN);

		module = getIntent().getIntExtra(INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_PAYMENT);
		if (module == PayByQRSDK.MODULE_IN_APP) {
			PayInAppInvoiceID = getIntent().getStringExtra(INTENT_EXTRA_INVOICE_ID);
			PayInAppURLCallback = getIntent().getStringExtra(INTENT_EXTRA_URL_CALLBACK);

			payByQRSDK.startSDK(module, PayInAppInvoiceID, PayInAppURLCallback);
		} else {
			payByQRSDK.startSDK(module);
		}

	}

	/**
	 * private void resetQrStore() { SharedPreferences prefs =
	 * getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
	 * SharedPreferences.Editor editor = prefs.edit();
	 * editor.remove(QR_STORE_DB); editor.commit(); }
	 **/

	/**
	 * private void resetUserKey() { SharedPreferences prefs =
	 * getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
	 * SharedPreferences.Editor editor = prefs.edit();
	 * editor.remove(DIMO_PREF_USERKEY); editor.commit();
	 * 
	 * payByQRSDK.setEULAState(false); resetQrStore(); }
	 **/

	// Get User API Key
	@SuppressLint("HandlerLeak")
	private void getUserAPIKey(final UserAPIKeyListener generateUserAPIKeyHandler) {
		valueContainer = new ValueContainer();
		// valueContainer.setContext(QRPayment.this);
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setTransactionName(Constants.TRANSACTION_USER_APIKEY);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, QRPayment2.this);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (responseXml != null) {
					XMLParser obj = new XMLParser();
					/** Parsing of response. */
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer.getMsgCode());
						System.out.println(">>>>>>>>" + responseContainer.getMsg());
					} catch (Exception e) {
						msgCode = 0;
					}

					// String userApiKey;
					if (msgCode == 2103) {
						userApiKey = responseContainer.getUserApiKey();
						SharedPreferences prefs = getSharedPreferences(DIMO_PREF, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(DIMO_PREF_USERKEY, userApiKey);
						editor.commit();

						Log.e("userApiKey----111111-----", userApiKey);
						generateUserAPIKeyHandler.setUserAPIKey(userApiKey);
					} else {
						generateUserAPIKeyHandler.setUserAPIKey(null);
					}
				} else {
					generateUserAPIKeyHandler.setUserAPIKey(null);
				}
			}
		};

		final Thread checkUpdate = new Thread() {
			/**
			 * Service call in thread in and getting response as xml in string.
			 */
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

	/*
	 * SDK CALLBACK
	 */

	@Override
	public Fragment callbackShowEULA() {
		return MyCustomEULA.newInstance();
	}

	@Override
	public void callbackEULAStateChanged(boolean state) {
	}

	@Override
	public void callbackGenerateUserAPIKey(UserAPIKeyListener generateUserAPIKeyHandler) {
		if (userApiKey.equalsIgnoreCase("NONE")) {
			Log.d("Simobi", "userKey not exist. Generating new userKey...");
			getUserAPIKey(generateUserAPIKeyHandler);
		} else {
			Log.d("Simobi", "userKey exist: " + userApiKey);
			generateUserAPIKeyHandler.setUserAPIKey(userApiKey);
		}
	}

	@Override
	public boolean callbackInvalidQRCode() {
		return false;
	}

	@Override
	public void callbackUserHasCancelTransaction() {
		if (PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) {
			setResult(RESULT_CANCELED);
		}
	}

	@Override
	public void callbackPayInvoice(InvoiceModel invoiceModel) {
		String debug = "callbackPayInvoice:" + "\n- invoiceID: " + invoiceModel.invoiceID + "\n- merchantName: "
				+ invoiceModel.merchantName + "\n- originalAmount: " + invoiceModel.originalAmount + "\n- paidAmount: "
				+ invoiceModel.paidAmount + "\n- amountOfDiscount: " + invoiceModel.amountOfDiscount
				+ "\n- discountType: " + invoiceModel.discountType + "\n- numberOfCoupons: "
				+ invoiceModel.numberOfCoupons + "\n- loyaltyProgramName: " + invoiceModel.loyaltyProgramName
				+ "\n- tipAmount: " + invoiceModel.tipAmount + "\n- pointsRedeemed: " + invoiceModel.pointsRedeemed
				+ "\n- amountRedeemed: " + invoiceModel.amountRedeemed;

		Log.d("Simobi", debug);

		loyalityName = invoiceModel.loyaltyProgramName;
		discountAmount = invoiceModel.amountOfDiscount + "";
		discountType = invoiceModel.discountType + "";
		mInVoiceId = invoiceModel.invoiceID;
		mAmount = invoiceModel.paidAmount + "";
		mMarchantName = invoiceModel.merchantName;
		numberOfCoupuns = invoiceModel.numberOfCoupons + "";
		redeemAmount = invoiceModel.amountRedeemed + "";
		redeemPoints = invoiceModel.pointsRedeemed + "";
		tipAmount = invoiceModel.tipAmount + "";

		Log.e("mInVoiceId--------", mInVoiceId);
		// Log.e("pin--------", pin);
		Log.e("mAmount-------", mAmount);
		Log.e("mMarchantName--------", mMarchantName);
		Log.e("loyalityName---------", loyalityName);
		Log.e("discountAmount----------", discountAmount);
		Log.e("discountType---------", discountType);
		Log.e("numberOfCoupuns", numberOfCoupuns);

		/*
		 * if(settings.getString("invoiceId", "").equals("")){
		 * settings.edit().putString("invoiceId", mInVoiceId).commit(); }
		 */

		/*
		 * Intent i = new Intent(this, QRPin1.class); startActivityForResult(i,
		 * 1);
		 */

		DialogPIN dialog = new DialogPIN(PayByQRProperties.getSDKContext());
		dialog.show();
		// showOTPRequiredDialog();
	}

	@Override
	public boolean callbackTransactionStatus(int code, String description) {
		Log.e("Simobi", "callbackTransactionStatus " + code + " " + description);
		/*
		 * if(code==0){ payByQRSDK.closeSDK(); }
		 */
		return false;
	}

	@Override
	public void callbackSDKClosed() {
		Log.d("Simobi", "callbackSDKClosed");
		finish();
	}

	@Override
	public void callbackLostConnection() {
		setResult(RESULT_CANCELED);
	}

	@Override
	public boolean callbackUnknowError() {
		return false;
	}

	@Override
	public void callbackAuthenticationError() {
		setResult(RESULT_CANCELED);
	}

	/*******************************************************************************
	 * Flashiz Bill Payment Inquiry
	 *******************************************************************************/

	// BillPay inquiry
	@SuppressLint("HandlerLeak")
	private void billPayInquiry(String invoiceID, String pin, String amount, String merchantName, String loyalityName,
			String discountAmount, String discountType, String numberofCoupuns, String redeemAmount,
			String redeemPoints, String tipAmount) {
		Log.d(LOG_TAG, "pin : " + pin);
		// registerReceiver(broadcastReceiver, new
		// IntentFilter("com.mfino.bsim"));
		valueContainer = new ValueContainer();
		valueContainer.setContext(QRPayment2.this);
		valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
		valueContainer.setTransactionName(Constants.TRANSACTION_QR_BILLPAYMENT_INQUIRY);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePin(mpin);
		valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
		valueContainer.setAmount(amount);
		valueContainer.setPaymentMode(Constants.TRANSACTION_QR_PAYMENT);
		try {
			merchantName = URLEncoder.encode(merchantName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		Log.d("TEST", merchantName);
		valueContainer.setMerchantName(merchantName.replace(" ", "+"));
		valueContainer.setBillerCode(QRBillerCode);
		valueContainer.setBillNo(invoiceID);
		valueContainer.setUserApiKey(settings.getString("userApiKey", "NONE"));
		valueContainer.setChannelId("7");
		// New Parameters.
		// valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));

		try {
			loyalityName = URLEncoder.encode(loyalityName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));
		valueContainer.setDiscountAmount(discountAmount);
		valueContainer.setDiscountType(discountType);
		valueContainer.setNumberofCoupuns(numberofCoupuns);
		valueContainer.setRedeemAmount(redeemAmount);
		valueContainer.setRedeemPoints(redeemPoints);
		valueContainer.setTipAmount(tipAmount);
		Log.e("====================", "==================" + valueContainer.toString());
		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, QRPayment2.this);
		/*
		 * dialog = ProgressDialog.show(QRPayment2.this,
		 * "  Banksinarmas                ",
		 * getResources().getString(R.string.eng_loading), true);
		 */

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (responseXml != null) {
					/** Parse the response. */
					XMLParser obj = new XMLParser();
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer.getMsgCode());
					} catch (Exception e) {
						msgCode = 0;
						// e.printStackTrace();
					}

					// dialog.dismiss();
					if (!((msgCode == 72) || (msgCode == 2109) || (msgCode == 713))) {
						Log.e("msg_code", msgCode + "--11--------");
						if (msgCode == 631) {
							Log.e("msg_code", msgCode + "--22--------");
							/*
							 * alertbox = new
							 * AlertDialog.Builder(QRPayment2.this);
							 * alertbox.setMessage(responseContainer.getMsg());
							 * alertbox.setNeutralButton("OK", new
							 * DialogInterface.OnClickListener() { public void
							 * onClick( DialogInterface arg0, int arg1) {
							 * 
							 * Intent intent = new Intent( getBaseContext(),
							 * LoginScreen.class);
							 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							 * startActivity(intent); finish(); } });
							 * alertbox.show();
							 */
							payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
									responseContainer.getMsg(), true);
						} else if (msgCode == 29) {
							// dialog.dismiss();
							Log.e("msg_code", msgCode + "--33--------");

							/*
							 * alertbox = new
							 * AlertDialog.Builder(QRPayment2.this);
							 * alertbox.setMessage(responseContainer.getMsg());
							 * alertbox.setNeutralButton("OK", new
							 * DialogInterface.OnClickListener() { public void
							 * onClick( DialogInterface arg0, int arg1) {
							 * 
							 *//**
								 * Intent intent = new Intent( QRPayment.this,
								 * HomeScreen.class); intent.setFlags
								 * (Intent.FLAG_ACTIVITY_CLEAR_TOP);
								 * startActivity(intent);
								 *//*
								 * 
								 * // closeSDK(); Intent intent = new Intent(
								 * QRPayment2.this, QRPayment2.class);
								 * intent.setFlags(Intent.
								 * FLAG_ACTIVITY_CLEAR_TOP);
								 * startActivity(intent); openMainMenu();
								 * finish(); } }); alertbox.show();
								 */
							payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
									responseContainer.getMsg(), true);
						} else {
							if (msgCode == 2000) {
								if (responseContainer.getMsg().equals("ERROR: Invalid Data")) {
									if (selectedLanguage.equalsIgnoreCase("ENG")) {
										payByQRSDK.notifyTransaction(
												com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
												getResources().getString(R.string.eng_incorrectotp), true);
									} else {
										payByQRSDK.notifyTransaction(
												com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
												getResources().getString(R.string.bahasa_incorrectotp), true);
									}
								} else {
									payByQRSDK.notifyTransaction(
											com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
											responseContainer.getMsg(), true);
								}
							} else {
								payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
										responseContainer.getMsg(), true);
							}
						}

					} else {
						try {
							// System.out.println("Testing>>mfa>>"+responseContainer.getMfaMode());
							if (responseContainer.getMfaMode().equalsIgnoreCase("OTP")) {
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
							parentTxnId = responseContainer.getEncryptedParentTxnId();
							txnId = responseContainer.getEncryptedTransferId();
							Log.d(LOG_TAG, "OTP check!");
							showOTPRequiredDialog();
							/**
							 * try { parentTxnId =
							 * responseContainer.getEncryptedParentTxnId();
							 * txnId =
							 * responseContainer.getEncryptedTransferId(); SctlI
							 * = responseContainer.getSctl(); Long
							 * startTimeInMillis = new
							 * java.util.Date().getTime(); while (true) {
							 * Thread.sleep(2000); final Uri SMS_INBOX =
							 * Uri.parse("content://sms/inbox"); Cursor c =
							 * getContentResolver().query(SMS_INBOX, null, null,
							 * null, "DATE desc"); c.moveToFirst(); for (int i =
							 * 0; i < 10; i++) { String body =
							 * c.getString(c.getColumnIndexOrThrow("body")).
							 * toString().trim(); String number =
							 * c.getString(c.getColumnIndexOrThrow("address")).
							 * toString();
							 * 
							 * if (body.contains("Kode Simobi Anda") &&
							 * body.contains(responseContainer.getSctl())) {
							 * 
							 * otpValue = body.substring(new String(
							 * "Kode Simobi Anda ").length(), body.indexOf(
							 * "(no ref")); sctl =
							 * body.substring(body.indexOf(":") + 1,
							 * body.indexOf(")")); // dialog.dismiss();
							 * 
							 * break;
							 * 
							 * } else if (body.contains("Your Simobi Code is")
							 * && body.contains(responseContainer.getSctl())) {
							 * 
							 * otpValue = body.substring(new String(
							 * "Your Simobi Code is ").length(),
							 * body.indexOf("(ref")); sctl = body.substring(
							 * body.indexOf("(ref no: ") + new String(
							 * "(ref no: ").length(), body.indexOf(")")); break;
							 * } else { c.moveToNext(); } } c.close();
							 * 
							 * if (!(otpValue.equals(""))) {
							 * 
							 * break; } else { Log.e("=============",
							 * "=============nagendra" + (new
							 * java.util.Date().getTime() - startTimeInMillis >=
							 * Constants.MFA_CONNECTION_TIMEOUT)); if (new
							 * java.util.Date().getTime() - startTimeInMillis >=
							 * Constants.MFA_CONNECTION_TIMEOUT) { break; } }
							 * 
							 * }
							 * 
							 * if (otpValue.equals("")) { // dialog.dismiss();
							 * System.out.println("Testing>>OTP>>null");
							 * 
							 * if (selectedLanguage.equalsIgnoreCase("ENG")) {
							 * // dialog.dismiss(); //
							 * displayDialog(getResources().getString(R.string.
							 * eng_transactionFail));
							 * payByQRSDK.notifyTransaction(
							 * com.dimo.PayByQR.data.Constant.
							 * ERROR_CODE_PAYMENT_FAILED,
							 * getResources().getString(R.string.
							 * eng_transactionFail), true); } else { //
							 * dialog.dismiss(); //
							 * displayDialog(getResources().getString(R.string.
							 * bahasa_transactionFail));
							 * payByQRSDK.notifyTransaction(
							 * com.dimo.PayByQR.data.Constant.
							 * ERROR_CODE_PAYMENT_FAILED,
							 * getResources().getString(R.string.
							 * bahasa_transactionFail), true); } } else { //
							 * dialog.dismiss(); billPayConfirmation(); } }
							 * catch (Exception e) { if (!(otpValue.equals("")))
							 * { billPayConfirmation(); } else {
							 * 
							 * } }
							 **/
						} else {
							/**
							 * Log.d(LOG_TAG, "no OTP pass!"); if
							 * (otp.equals("")) { if
							 * (selectedLanguage.equalsIgnoreCase("ENG")) { //
							 * dialog.dismiss(); //
							 * displayDialog(getResources().getString(R.string.
							 * eng_transactionFail));
							 * payByQRSDK.notifyTransaction(
							 * com.dimo.PayByQR.data.Constant.
							 * ERROR_CODE_PAYMENT_FAILED,
							 * getResources().getString(R.string.
							 * eng_transactionFail), true); } else { //
							 * dialog.dismiss(); //
							 * displayDialog(getResources().getString(R.string.
							 * bahasa_transactionFail));
							 * payByQRSDK.notifyTransaction(
							 * com.dimo.PayByQR.data.Constant.
							 * ERROR_CODE_PAYMENT_FAILED,
							 * getResources().getString(R.string.
							 * bahasa_transactionFail), true); } } otp =
							 * otpValue; parentTxnId =
							 * responseContainer.getEncryptedParentTxnId();
							 * txnId =
							 * responseContainer.getEncryptedTransferId();
							 * billPayConfirmation();
							 **/
						}
					}

				} else {
					payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
							getResources().getString(R.string.bahasa_serverNotRespond), true);
					/*
					 * dialog.dismiss();
					 * displayDialog(getResources().getString(R.string.
					 * bahasa_serverNotRespond));
					 */
				}

			}
		};

		final Thread checkUpdate = new Thread() {
			public void run() {
				try {
					responseXml = webServiceHttp.getResponseSSLCertificatation();
					Log.e("=========", "====Nagendra Palepu=========" + responseXml);
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

	@SuppressLint("HandlerLeak")
	private void billPayConfirmation() {
		/** Set Parameters for Service calling . */
		valueContainer = new ValueContainer();
		valueContainer.setContext(QRPayment2.this);
		valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
		valueContainer.setTransactionName(Constants.TRANSACTION_QR_PAYMENT);
		valueContainer.setSourcePin(mpin);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
		valueContainer.setConfirmed("true");
		valueContainer.setAmount(mAmount);
		valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
		valueContainer.setPaymentMode(Constants.TRANSACTION_QR_PAYMENT);
		// valueContainer.setMerchantName(mMarchantName.replace(" ", "+"));
		try {
			mMarchantName = URLEncoder.encode(mMarchantName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
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
		// valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));

		try {
			loyalityName = URLEncoder.encode(loyalityName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		valueContainer.setLoyalityName(loyalityName.replace(" ", "+"));

		valueContainer.setDiscountAmount(discountAmount);
		valueContainer.setDiscountType(discountType);
		valueContainer.setNumberofCoupuns(numberOfCoupuns);
		valueContainer.setRedeemAmount(redeemAmount);
		valueContainer.setRedeemPoints(redeemPoints);
		valueContainer.setTipAmount(tipAmount);

		try {
			if (mfa.equalsIgnoreCase("OTP")) {
				valueContainer.setOTP(edt.getText().toString());
				valueContainer.setMfaMode(mfa);
			}
		} catch (Exception e1) {

		}

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, QRPayment2.this);

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				Log.e("===confirmation Response====", "=-======" + responseXml);
				if (responseXml != null) {
					otp = "";
					otpValue = "";
					XMLParser obj = new XMLParser();
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
						msgCode = Integer.parseInt(responseContainer.getMsgCode());
					} catch (Exception e) {
						e.printStackTrace();
						msgCode = 0;
					}
					// dialogCon.dismiss();

					if (!((Integer.parseInt(responseContainer.getMsgCode()) == 2111)
							|| (Integer.parseInt(responseContainer.getMsgCode()) == 715))) {
						// displayDialog(responseContainer.getMsg());
						if (msgCode == 2000) {
							if (responseContainer.getMsg().equals("ERROR: Invalid Data")) {
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									payByQRSDK.notifyTransaction(
											com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
											getResources().getString(R.string.eng_incorrectotp), true);
								} else {
									payByQRSDK.notifyTransaction(
											com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
											getResources().getString(R.string.bahasa_incorrectotp), true);
								}
							} else {
								payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
										responseContainer.getMsg(), true);
							}
						} else {
							payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
									responseContainer.getMsg(), true);
						}
					} else {
						// Hand over control to Flashiz
						// dialogCon.dismiss();
						payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.STATUS_CODE_PAYMENT_SUCCESS,
								getString(com.dimo.PayByQR.R.string.text_payment_success), true);
					}

				} else {
					/*
					 * dialogCon.dismiss();
					 * displayDialog(getResources().getString(
					 * R.string.bahasa_serverNotRespond));
					 */
					payByQRSDK.notifyTransaction(com.dimo.PayByQR.data.Constant.ERROR_CODE_PAYMENT_FAILED,
							getResources().getString(R.string.bahasa_serverNotRespond), true);
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

	public void openMainMenu() {
		Intent inent = new Intent(QRPayment2.this, HomeScreen.class);
		inent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(inent);
		finish();
	}

	private class DialogPIN extends Dialog {
		private Button btn_ok, btn_cancel;
		SharedPreferences languageSettings;
		String selectedLanguage;
		SharedPreferences settings;
		private EditText pinValue;

		public DialogPIN(Context context) {
			super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
			setContentView(R.layout.enter_pin);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.enter_pin);

			languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
			selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
			settings = getSharedPreferences("LOGIN_PREFERECES", Context.MODE_PRIVATE);

			// Header code...
			View headerContainer = findViewById(R.id.header);
			TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
			ImageButton back = (ImageButton) findViewById(R.id.back);
			ImageButton home = (ImageButton) findViewById(R.id.home_button);

			back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getContext(), HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
			home.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getContext(), HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});

			/*
			 * back.setVisibility(View.GONE); home.setVisibility(View.GONE);
			 */
			screeTitle.setText("QR Payment");

			userApiKey = settings.getString("userApiKey", "NONE");
			pinValue = (EditText) findViewById(R.id.ed_pinValue);
			btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
			btn_cancel = (Button) findViewById(R.id.btn_EnterPin_cancel1);

			if (selectedLanguage.equalsIgnoreCase("ENG")) {

			} else {

				btn_ok.setText(getResources().getString(R.string.bahasa_submit));
				btn_cancel.setText(getResources().getString(R.string.bahasa_cancel));

			}

			btn_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// finish();
					Intent i = new Intent(getContext(), HomeScreen.class);
					startActivity(i);
					finish();
				}
			});

			btn_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(pinValue.getWindowToken(), 0);

					if (pinValue.getText().length() < 1) {
						pinValue.setError(" Masukkan PIN Anda  ");
					} else {
						/*
						 * Intent returnIntent = new Intent();
						 * returnIntent.putExtra("PIN",
						 * pinValue.getText().toString()); setResult(2,
						 * returnIntent); finish();
						 */

						// RHIO test
						mpin = pinValue.getText().toString();
						Log.e("----------", "=-------" + mpin);
						// showOTPRequiredDialog();
						billPayInquiry(mInVoiceId, mpin, mAmount, mMarchantName, loyalityName, discountAmount,
								discountType, numberOfCoupuns, redeemAmount, redeemPoints, tipAmount);
						dismiss();

					}

				}
			});
		}
	}

	@Override
	public void callbackShowDialog(Context context, final int code, String description, LoyaltyModel loyaltyModel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
		builder.setTitle("Custom Dialog");
		builder.setMessage(description);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (code == com.dimo.PayByQR.data.Constant.ERROR_CODE_AUTHENTICATION)
					payByQRSDK.closeSDK();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void recivedSms(String message) {
		try {
			// Log.d(LOG_TAG, "isi SMS : " + message);
			if (message.contains("Kode Simobi Anda ")
					|| message.toLowerCase(Locale.getDefault()).contains("kode simobi anda ")) {
				// Log.d(LOG_TAG, "konten sms : indonesia");
				otpValue = message
						.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
						.trim();
				sctl = message.substring(message.indexOf(":") + 1, message.indexOf(")"));
			} else if (message.contains("Your Simobi Code is ")
					|| message.toLowerCase(Locale.getDefault()).contains("your simobi code is")) {
				// Log.d(LOG_TAG, "konten sms : english");
				otpValue = message
						.substring(message.substring(0, message.indexOf("(")).lastIndexOf(" "), message.indexOf("("))
						.trim();
				sctl = message.substring(message.indexOf("(ref no: ") + new String("(ref no: ").length(),
						message.indexOf(")"));
			}
			edt.setText(otpValue);
		} catch (Exception e) {

		}
	}

	public void errorOTP() {
		AlertDialog.Builder builderError = new AlertDialog.Builder(PayByQRProperties.getSDKContext(),
				R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.eng_otpfailed));
			builderError.setMessage(getResources().getString(R.string.eng_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							Intent intent = new Intent(QRPayment2.this, HomeScreen.class);
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
							Intent intent = new Intent(QRPayment2.this, HomeScreen.class);
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

	public void showOTPRequiredDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PayByQRProperties.getSDKContext(),
				R.style.MyAlertDialogStyle);
		LayoutInflater inflater = this.getLayoutInflater();
		final ViewGroup nullParent = null;
		dialogBuilder.setCancelable(false);
		final View dialogView = inflater.inflate(R.layout.otp_dialog, nullParent);
		dialogBuilder.setView(dialogView);

		// EditText OTP
		edt = (EditText) dialogView.findViewById(R.id.otp_value);

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
					if (myTimer != null) {
						myTimer.cancel();
					}
					dialog.dismiss();
					Intent intent = new Intent(QRPayment2.this, HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		} else {
			dialogBuilder.setTitle(getResources().getString(R.string.bahasa_otprequired_title));
			dialogBuilder.setMessage(getResources().getString(R.string.bahasa_otprequired_desc_1) + "" + mobileNumber
					+ " " + getResources().getString(R.string.bahasa_otprequired_desc_2));
			dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if (myTimer != null) {
						myTimer.cancel();
					}
					dialog.dismiss();
					Intent intent = new Intent(QRPayment2.this, HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}
		dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
				} else {
					Log.d(LOG_TAG, "otpValue via onclick OK : " + otpValue);
					billPayConfirmation();
					if (myTimer != null) {
						myTimer.cancel();
					}
				}
			}
		});

		final AlertDialog b = dialogBuilder.create();
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
			public void afterTextChanged(final Editable s) {
				Boolean isAutoSubmit = settings.getBoolean("isAutoSubmit", false);
				if (TextUtils.isEmpty(s)) {
					((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					if ((edt.getText().length() > 3) && (isAutoSubmit == true)) {
						billPayConfirmation();
						if (myTimer != null) {
							myTimer.cancel();
						}
					} else {
						((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					}
				}

			}
		});
	}
}
