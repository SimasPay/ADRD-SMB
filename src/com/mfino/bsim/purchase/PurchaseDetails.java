package com.mfino.bsim.purchase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

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

public class PurchaseDetails extends AppCompatActivity implements IncomingSMS.AutoReadSMSListener {

	private static final int SUCCESS_MSGCODE = 660;
	private Button btn_ok;
	private EditText pinValue, mdn, amount;
	private AlertDialog.Builder alertbox;
	private String responseXml;
	ValueContainer valueContainer;
	private Bundle bundle;
	int msgCode = 0, length;
	ArrayList<String> denoms = new ArrayList<String>();
	String denomArray[];
	Spinner denomSpinner;
	String denomValue;
	TextView amountTextView;
	TextView denom;
	String paymentMode;
	int denomSize;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	ArrayList<String> packageCode = new ArrayList<String>();
	ArrayList<String> packageValue = new ArrayList<String>();
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
		setContentView(R.layout.purchase_details);

		settings2 = getSharedPreferences(LOG_TAG, 0);
		settings2.edit().putString("ActivityName", "PurchaseDetails").commit();
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
		settings.edit().putString("ActivityName", "PurchaseDetails").commit();
		settings.edit().putBoolean("isAutoSubmit", false).commit();
		Log.d(LOG_TAG, "Purchase : PurchaseDetails");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(PurchaseDetails.this, HomeScreen.class));
			}
		});

		bundle = getIntent().getExtras();
		System.out.println("Test>>>" + bundle.getString("PRODUCT_DENOM") + ">>");
		System.out.println("******** invoice>>>*" + bundle.getString("SELECTED_INVOICETYPE"));
		System.out.println("******** paymentMode>>>*" + bundle.getString("SELECTED_PAYMENT_MODE"));
		LinearLayout amountLayout = (LinearLayout) findViewById(R.id.amountLayout);
		RelativeLayout denomLayout = (RelativeLayout) findViewById(R.id.denomLayout);
		mdn = (EditText) findViewById(R.id.ed_mdnValue);

		amount = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		denomSpinner = (Spinner) findViewById(R.id.denom_spinner);
		amountTextView = (TextView) findViewById(R.id.amount_textView);

		denom = (TextView) findViewById(R.id.denom);
		TextView textViewdestMdn = (TextView) findViewById(R.id.textView_purchaseDestMDN);
		// TextView textViewamount = (TextView)
		// findViewById(R.id.amount_textView);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		// InvoiceType parsing
		String inVoiceType = bundle.getString("SELECTED_INVOICETYPE");
		String part[] = null;
		if (inVoiceType != null) {
			part = inVoiceType.split("\\|");
			// System.out.println(part[0]+"Test>>"+part[1]);
		}

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			screeTitle.setText(getResources().getString(R.string.eng_purchase));
			// textViewdestMdn.setText(getResources().getString(R.string.eng_destnatin_mdn));
			textViewdestMdn.setText(part[0]);
			// textViewinVoiceNumber.setText(getResources().getString(R.string.eng_invoiceNumber));
			amountTextView.setText(getResources().getString(R.string.eng_amount));
			denom.setText(getResources().getString(R.string.eng_availableDenoms));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {

			screeTitle.setText(getResources().getString(R.string.bahasa_purchase));
			textViewdestMdn.setText(part[1]);
			// textViewinVoiceNumber.setText(getResources().getString(R.string.bahasa_invoiceNumber));
			denom.setText(getResources().getString(R.string.bahasa_availableDenoms));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}

		// Amount or Denom decision
		paymentMode = bundle.getString("SELECTED_PAYMENT_MODE");

		if (paymentMode.equalsIgnoreCase("FullAmount")) {

			amountLayout.setVisibility(View.VISIBLE);
			denomLayout.setVisibility(View.GONE);

		} else if (paymentMode.equalsIgnoreCase("ZeroAmount")) {
			if (bundle.getBoolean("IS_PLN_PREPAID"))
				amountLayout.setVisibility(View.VISIBLE);
			else
				amountLayout.setVisibility(View.GONE);

			denomLayout.setVisibility(View.GONE);
			denomValue = "0";

		} else if (paymentMode.equalsIgnoreCase("Denom")) {

			String data = bundle.getString("PRODUCT_DENOM");
			denomArray = data.split("\\|");
			for (int i = 0; i < denomArray.length; i++) {
				denoms.add(denomArray[i]);
				System.out.println(denomArray[i] + "Test>>");
			}

			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.VISIBLE);
			amount.setVisibility(View.GONE);
			amountTextView.setVisibility(View.GONE);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseDetails.this, R.layout.spinner_row,
					denomArray);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			denomSpinner.setAdapter(dataAdapter);

			denomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					denomValue = denomArray[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

		} else if (paymentMode.equalsIgnoreCase("PackageType")) {

			String data = bundle.getString("PRODUCT_DENOM");
			denomArray = data.split("\\|");
			/*
			 * for (int i = 0; i < denomArray.length; i++) {
			 * denoms.add(denomArray[i]);
			 * System.out.println(denomArray[i]+"Test>>"); }
			 */

			for (int i = 0; i < denomArray.length; i++) {

				String code1 = denomArray[i].substring(denomArray[i].indexOf("[") + 1, denomArray[i].indexOf("]"));
				String value1 = denomArray[i].substring(denomArray[i].indexOf("]") + 1, denomArray[i].length());
				packageCode.add(code1);
				packageValue.add(value1);

				System.out.println(value1 + "Test>>" + code1);

			}

			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.VISIBLE);
			amount.setVisibility(View.GONE);
			amountTextView.setVisibility(View.GONE);

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseDetails.this, R.layout.spinner_row,
					packageValue);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			denomSpinner.setAdapter(dataAdapter);

			denomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					denomValue = packageCode.get(arg2);
					// denomValue=denomValue.substring(1,
					// denomValue.indexOf("]"));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

		} else {
			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.GONE);

		}

		alertbox = new AlertDialog.Builder(PurchaseDetails.this, R.style.MyAlertDialogStyle);

		btn_ok.setOnClickListener(new View.OnClickListener() {
			@SuppressLint({ "NewApi", "HandlerLeak" })
			@Override
			public void onClick(View arg0) {
				settings2.edit().putString("ActivityName", "PurchaseDetails").commit();
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
						displayDialog(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						displayDialog(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}

				} else if (pinValue.getText().length() < 4) {

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						displayDialog(getResources().getString(R.string.eng_pinLength));
					} else {
						displayDialog(getResources().getString(R.string.bahasa_pinLength));
					}

				} else {

					if (paymentMode.equalsIgnoreCase("FullAmount")) {

						denomValue = amount.getText().toString();

					} else if (paymentMode.equalsIgnoreCase("ZeroAmount")) {
						if (bundle.getBoolean("IS_PLN_PREPAID"))
							denomValue = amount.getText().toString();
						else
							denomValue = "0";

					}

					/** Set Parameters for Service Calling. */
					valueContainer = new ValueContainer();
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

					if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {

						System.out.println("Testing>>>airtime");
						valueContainer.setServiceName(Constants.SERVICE_BUY);
						valueContainer.setTransactionName(Constants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setDestinationMdn(mdn.getText().toString().trim());
						valueContainer.setAmount(denomValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setCompanyId(bundle.getString("PRODUCT_CODE"));

					} else {

						System.out.println("Testing>>>purchase");
						valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
						valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setAmount(denomValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
						valueContainer.setBillNo(mdn.getText().toString());

					}

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, PurchaseDetails.this);

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = new ProgressDialog(PurchaseDetails.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.eng_loading));
						dialog.show();
					} else {
						dialog = new ProgressDialog(PurchaseDetails.this, R.style.MyAlertDialogStyle);
						dialog.setTitle("Bank Sinarmas");
						dialog.setCancelable(false);
						dialog.setMessage(getResources().getString(R.string.bahasa_loading));
						dialog.show();
					}

					final Handler handler = new Handler() {
						public void handleMessage(Message msg) {
							if (responseXml != null) {
								/** Parse the response. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
									System.out.print("Testing>>" + responseContainer.getMfaMode());
								} catch (Exception e) {
									// e.printStackTrace();
								}

								dialog.dismiss();

								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}

								if (!((msgCode == SUCCESS_MSGCODE) || (msgCode == 72) || (msgCode == 713)
										|| (msgCode == 660))) {
									if (responseContainer.getMsg() == null) {

										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											displayDialog(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											displayDialog(getResources().getString(R.string.bahasa_serverNotRespond));
										}
									} else {
										displayDialog(responseContainer.getMsg());
									}

								} else {

									// dialog.dismiss();
									try {
										if (responseContainer.getMfaMode() == null) {
											valueContainer.setMfaMode("NONE");

										} else {
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}

									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										Log.e("MFA MODE..", responseContainer.getMfaMode() + "");
										dialog.dismiss();
										Log.d("Widy-Debug", "Dialog OTP Required show");
										settings.edit().putString("Sctl", responseContainer.getSctl()).commit();
										settings2 = getSharedPreferences(LOG_TAG, 0);
										settings2.edit().putString("ActivityName", "PurchaseDetails").commit();
										showOTPRequiredDialog(pinValue.getText().toString(), denomValue,
												responseContainer.getMfaMode(), mdn.getText().toString(),
												responseContainer.getMsg(), responseContainer.getAditionalInfo(),
												responseContainer.getEncryptedParentTxnId(),
												responseContainer.getEncryptedTransferId());
									} else {
										System.out.println("Testing>>>purchase");
										Intent intent = new Intent(PurchaseDetails.this, BuyConfirm.class);
										intent.putExtra("AMT", denomValue);
										intent.putExtra("DESTMDN", mdn.getText().toString().trim());
										intent.putExtra("COMPID", bundle.getString("PRODUCT_CODE"));
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
										intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
										intent.putExtra("MSG", responseContainer.getMsg());
										intent.putExtra("PRODUCT_CODE", bundle.getString("PRODUCT_CODE"));
										intent.putExtra("BILLERNUM", mdn.getText().toString());

										try {
											intent.putExtra("ADDITIONAL_INFO", responseContainer.getAditionalInfo());
										} catch (Exception e) {

											intent.putExtra("ADDITIONAL_INFO", "null");
										}

										intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
										startActivity(intent);
										PurchaseDetails.this.finish();
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

										Intent intent = new Intent(getBaseContext(), HomeScreen.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										PurchaseDetails.this.finish();
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

	/** Empty field validation. */
	private boolean isRequiredFieldEmpty() {

		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		amount = (EditText) findViewById(R.id.ed_amountValue);

		if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {

			mdn = (EditText) findViewById(R.id.ed_mdnValue);
			// if (!(pinValue.getText().toString().equals(""))&&
			// !(mdn.getText().toString().equals("")))
			if (!(pinValue.getText().toString().equals(""))) {
				System.out.println("Testing>>false");
				return false;
			} else {
				System.out.println("Testing>>true");
				return true;
			}

		} else {
			mdn = (EditText) findViewById(R.id.ed_mdnValue);

			// if (!(pinValue.getText().toString().equals(""))&&
			// !(inVoiceNumber.getText().toString().equals(""))&&
			// !(amount.getText().toString().equals(""))) {
			if (!(pinValue.getText().toString().equals(""))) {
				System.out.println("Testing>>false");
				return false;
			} else {
				System.out.println("Testing>>true");
				return true;
			}
		}

	}

	// Dialog Displaying

	public void displayDialog(String msg) {
		// if(dialog.isShowing()){
		try {
			dialog.dismiss();
			Log.e("haiii", "-----------");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// }

		alertbox = new AlertDialog.Builder(PurchaseDetails.this, R.style.MyAlertDialogStyle);
		alertbox.setMessage(msg);
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					dialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (msgCode == 631) {

					Intent intent = new Intent(getBaseContext(), LoginScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					PurchaseDetails.this.finish();

				} else if (msgCode == 699) {
					amount.setText("");
				} else {

					/*
					 * Intent intent = new
					 * Intent(getBaseContext(),HomeScreen.class);
					 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent);
					 */
				}

				pinValue.setText("");

			}
		});
		alertbox.show();
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
		AlertDialog.Builder builderError = new AlertDialog.Builder(PurchaseDetails.this, R.style.MyAlertDialogStyle);
		builderError.setCancelable(false);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			builderError.setTitle(getResources().getString(R.string.eng_otpfailed));
			builderError.setMessage(getResources().getString(R.string.eng_desc_otpfailed)).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							settings2 = getSharedPreferences(LOG_TAG, 0);
							settings2.edit().putString("ActivityName", "ExitPurchaseDetails").commit();
							isExitActivity = true;
							Intent intent = new Intent(PurchaseDetails.this, HomeScreen.class);
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
							settings2.edit().putString("ActivityName", "ExitPurchaseDetails").commit();
							isExitActivity = true;
							Intent intent = new Intent(PurchaseDetails.this, HomeScreen.class);
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

	public void showOTPRequiredDialog(final String pinValue, final String denomValue, final String mfaMode,
			final String MDNValue, final String msgValue, final String aditionalInfo, final String EncryptedParentTxnId,
			final String EncryptedTransferId) {
		LayoutInflater inflater = getLayoutInflater();
		final ViewGroup nullParent = null;
		View dialoglayout = inflater.inflate(R.layout.new_otp_dialog, nullParent, false);
		dialogBuilder = new AlertDialog.Builder(PurchaseDetails.this, R.style.MyAlertDialogStyle).create();
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
				settings2.edit().putString("ActivityName", "ExitPurchaseDetails").commit();
				if (edt.getText().toString() == null || edt.getText().toString().equals("")) {
					errorOTP();
				} else {
					if (myTimer != null) {
						myTimer.cancel();
					}
					if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {
						System.out.println("Testing>>>airtime");
						Intent intent = new Intent(PurchaseDetails.this, BuyConfirm.class);
						intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
						intent.putExtra("PIN", pinValue);
						intent.putExtra("AMT", denomValue);
						intent.putExtra("DESTMDN", MDNValue.trim());
						intent.putExtra("COMPID", bundle.getString("PRODUCT_CODE"));
						intent.putExtra("MSG", msgValue);
						intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
						intent.putExtra("PTFNID", EncryptedParentTxnId);
						intent.putExtra("TFNID", EncryptedTransferId);
						try {
							intent.putExtra("ADDITIONAL_INFO", aditionalInfo);
						} catch (Exception e) {
							intent.putExtra("ADDITIONAL_INFO", "null");
						}
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("MFA_MODE", mfaMode);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						PurchaseDetails.this.finish();
					} else {
						System.out.println("Testing>>>purchase");
						Intent intent = new Intent(PurchaseDetails.this, BuyConfirm.class);
						intent.putExtra("PIN", pinValue);
						intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
						intent.putExtra("MSG", msgValue);
						intent.putExtra("PRODUCT_CODE", bundle.getString("PRODUCT_CODE"));
						intent.putExtra("BILLERNUM", MDNValue.trim());
						try {
							intent.putExtra("ADDITIONAL_INFO", aditionalInfo);
						} catch (Exception e) {
							intent.putExtra("ADDITIONAL_INFO", "null");
						}
						intent.putExtra("PTFNID", EncryptedParentTxnId);
						intent.putExtra("TFNID", EncryptedTransferId);
						intent.putExtra("OTP", edt.getText().toString());
						intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
						intent.putExtra("MFA_MODE", mfaMode);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						PurchaseDetails.this.finish();
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
					settings2 = getSharedPreferences(LOG_TAG, 0);
					String actName = settings2.getString("ActivityName", "");
					Log.d(LOG_TAG, "ActivityName : " + actName);
					if (actName.equals("PurchaseDetails")) {
						if (myTimer != null) {
							myTimer.cancel();
						}
						if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {
							System.out.println("Testing>>>airtime");
							Intent intent = new Intent(PurchaseDetails.this, BuyConfirm.class);
							intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
							intent.putExtra("PIN", pinValue);
							intent.putExtra("AMT", denomValue);
							intent.putExtra("DESTMDN", MDNValue.trim());
							intent.putExtra("COMPID", bundle.getString("PRODUCT_CODE"));
							intent.putExtra("MSG", msgValue);
							intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
							intent.putExtra("PTFNID", EncryptedParentTxnId);
							intent.putExtra("TFNID", EncryptedTransferId);
							try {
								intent.putExtra("ADDITIONAL_INFO", aditionalInfo);
							} catch (Exception e) {
								intent.putExtra("ADDITIONAL_INFO", "null");
							}
							intent.putExtra("OTP", edt.getText().toString());
							intent.putExtra("MFA_MODE", mfaMode);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							PurchaseDetails.this.finish();
						} else {
							System.out.println("Testing>>>purchase");
							Intent intent = new Intent(PurchaseDetails.this, BuyConfirm.class);
							intent.putExtra("PIN", pinValue);
							intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
							intent.putExtra("MSG", msgValue);
							intent.putExtra("PRODUCT_CODE", bundle.getString("PRODUCT_CODE"));
							intent.putExtra("BILLERNUM", MDNValue.trim());
							try {
								intent.putExtra("ADDITIONAL_INFO", aditionalInfo);
							} catch (Exception e) {
								intent.putExtra("ADDITIONAL_INFO", "null");
							}
							intent.putExtra("PTFNID", EncryptedParentTxnId);
							intent.putExtra("TFNID", EncryptedTransferId);
							intent.putExtra("OTP", edt.getText().toString());
							intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
							intent.putExtra("MFA_MODE", mfaMode);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							PurchaseDetails.this.finish();
						}
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
		settings2.edit().putString("ActivityName", "ExitPurchaseDetails").commit();
		context = this;
		isExitActivity = true;
		if (dialogBuilder != null) {
			dialogBuilder.dismiss();
		}
		if (alertError != null) {
			alertError.dismiss();
		}
	}

}
