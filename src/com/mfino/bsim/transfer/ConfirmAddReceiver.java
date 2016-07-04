package com.mfino.bsim.transfer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ConfirmAddReceiver extends AppCompatActivity {

	private Button btn_confirm, btn_cancel;
	private TextView customerName, destBank, accountNumber, amount;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_transfer);
		bundle = getIntent().getExtras();

		// Header code...
		/*
		 * View headerContainer = findViewById(R.id.header); TextView screeTitle
		 * = (TextView) headerContainer.findViewById(R.id.screenTitle); Button
		 * back = (Button) headerContainer.findViewById(R.id.back); Button home
		 * = (Button) headerContainer.findViewById(R.id.home_button);
		 * back.setVisibility(View.GONE);
		 * 
		 * back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * if (bundle.getString("TRANSFER_TYPE").equals("toSmartFren")) {
		 * 
		 * Intent intent = new Intent(ConfirmAddReceiver.this,
		 * SmartFrenDetails.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent);
		 * 
		 * } else if
		 * (bundle.getString("TRANSFER_TYPE").equals("toBankSinarmas")) {
		 * 
		 * Intent intent = new
		 * Intent(ConfirmAddReceiver.this,ToBankSinarmas.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent);
		 * 
		 * } else { Intent intent = new
		 * Intent(ConfirmAddReceiver.this,ToOtherBankDetails.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } } }); home.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent=new
		 * Intent(ConfirmAddReceiver.this,HomeScreen.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 */

		// final String confirmMsg = bundle.getString("MSG");
		alertbox = new AlertDialog.Builder(ConfirmAddReceiver.this, R.style.MyAlertDialogStyle);
		btn_confirm = (Button) findViewById(R.id.confirmButton);
		btn_cancel = (Button) findViewById(R.id.cancelButton);
		LinearLayout custNameLayout = (LinearLayout) findViewById(R.id.custNameLayout);
		LinearLayout destNumLayout = (LinearLayout) findViewById(R.id.destNumberLayout);
		LinearLayout accountNumLayout = (LinearLayout) findViewById(R.id.acountNumberLayout);
		LinearLayout amountLayout = (LinearLayout) findViewById(R.id.amountLayout);

		customerName = (TextView) findViewById(R.id.custName);
		destBank = (TextView) findViewById(R.id.destNumber);
		accountNumber = (TextView) findViewById(R.id.accountNumber);
		amount = (TextView) findViewById(R.id.amount);
		TextView txtCustName = (TextView) findViewById(R.id.fundTransfer_toOtherBank_custName);
		TextView txtDestBank = (TextView) findViewById(R.id.fundTransfer_toOtherBank_destBank);
		TextView txtDestNum = (TextView) findViewById(R.id.fundTransfer_toOtherBank_destNum);
		TextView txtAmount = (TextView) findViewById(R.id.fundTransfer_toOtherBank_amount);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			// screeTitle.setText(getResources().getString(R.string.eng_confirm));
			txtCustName.setText(getResources().getString(R.string.eng_customerName));
			txtDestBank.setText(getResources().getString(R.string.eng_destinationBank));
			txtDestNum.setText(getResources().getString(R.string.eng_destinationNumber));
			txtAmount.setText(getResources().getString(R.string.eng_amount));
			btn_confirm.setText(getResources().getString(R.string.eng_confirm));
			btn_cancel.setText(getResources().getString(R.string.eng_cancel));

		} else {

			// screeTitle.setText(getResources().getString(R.string.bahasa_confirm));
			txtCustName.setText(getResources().getString(R.string.bahasa_customerName));
			txtDestBank.setText(getResources().getString(R.string.bahasa_destinationBank));
			txtDestNum.setText(getResources().getString(R.string.bahasa_destinationNumber));
			txtAmount.setText(getResources().getString(R.string.bahasa_amount));
			btn_confirm.setText(getResources().getString(R.string.bahasa_confirm));
			btn_cancel.setText(getResources().getString(R.string.bahasa_cancel));

		}

		System.out.println("MFA MODE" + bundle.getString("MFA_MODE"));
		if (bundle.getString("TRANSFER_TYPE").equals("toSmartFren")) {

			custNameLayout.setVisibility(View.VISIBLE);
			destNumLayout.setVisibility(View.VISIBLE);
			accountNumLayout.setVisibility(View.VISIBLE);
			amountLayout.setVisibility(View.VISIBLE);
			System.out.println("Testing>>DestNum" + bundle.getString("DEST_NUMBER"));

			try {
				if (bundle.getString("CUST_NAME").toString().equalsIgnoreCase("null")
						|| bundle.getString("CUST_NAME").length() <= 0) {

					custNameLayout.setVisibility(View.GONE);
				} else {
					customerName.setText(": " + bundle.getString("CUST_NAME"));
				}
			} catch (NullPointerException e) {
				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_BANK").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_BANK").length() <= 0) {
					destNumLayout.setVisibility(View.GONE);
				} else {
					destBank.setText(": " + bundle.getString("DEST_BANK"));
				}
			} catch (NullPointerException e) {
				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_NUMBER").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_NUMBER").length() <= 0) {
					destNumLayout.setVisibility(View.GONE);
				} else {
					accountNumber.setText(": " + bundle.getString("DEST_NUMBER"));
				}
			} catch (NullPointerException e) {

				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("AMOUNT").toString().equalsIgnoreCase("null")
						|| bundle.getString("AMOUNT").length() <= 0) {

					amountLayout.setVisibility(View.GONE);

				} else {
					amount.setText(": " + bundle.getString("AMOUNT"));
				}
			} catch (NullPointerException e) {

				amountLayout.setVisibility(View.GONE);
			}

		} else if (bundle.getString("TRANSFER_TYPE").equals("toBankSinarmas")) {
			custNameLayout.setVisibility(View.VISIBLE);
			destNumLayout.setVisibility(View.VISIBLE);
			accountNumLayout.setVisibility(View.VISIBLE);
			amountLayout.setVisibility(View.VISIBLE);

			try {
				if (bundle.getString("CUST_NAME").toString().equalsIgnoreCase("null")
						|| bundle.getString("CUST_NAME").length() <= 0) {
					custNameLayout.setVisibility(View.GONE);
				} else {
					customerName.setText(": " + bundle.getString("CUST_NAME"));
				}
			} catch (NullPointerException e) {
				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_BANK").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_BANK").length() <= 0) {
					destNumLayout.setVisibility(View.GONE);
				} else {
					destBank.setText(": " + bundle.getString("DEST_BANK"));
				}
			} catch (NullPointerException e) {
				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_ACCOUNT_NUM").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_ACCOUNT_NUM").length() <= 0) {
					accountNumLayout.setVisibility(View.GONE);
				} else {
					accountNumber.setText(": " + bundle.getString("DEST_ACCOUNT_NUM"));
				}
			} catch (NullPointerException e) {
				accountNumLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("AMOUNT").toString().equalsIgnoreCase("null")
						|| bundle.getString("AMOUNT").length() <= 0) {
					amountLayout.setVisibility(View.GONE);
				} else {
					amount.setText(": " + bundle.getString("AMOUNT"));
				}
			} catch (NullPointerException e) {
				amountLayout.setVisibility(View.GONE);
			}

		} else {
			custNameLayout.setVisibility(View.VISIBLE);
			destNumLayout.setVisibility(View.VISIBLE);
			accountNumLayout.setVisibility(View.VISIBLE);
			amountLayout.setVisibility(View.VISIBLE);

			try {
				if (bundle.getString("CUST_NAME").toString().equalsIgnoreCase("null")
						|| bundle.getString("CUST_NAME").length() <= 0) {

					custNameLayout.setVisibility(View.GONE);

				} else {
					customerName.setText(": " + bundle.getString("CUST_NAME"));
				}
			} catch (NullPointerException e) {

				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_BANK").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_BANK").length() <= 0) {
					destNumLayout.setVisibility(View.GONE);
				} else {
					destBank.setText(": " + bundle.getString("DEST_BANK"));
				}
			} catch (NullPointerException e) {

				custNameLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("DEST_ACCOUNT_NUM").toString().equalsIgnoreCase("null")
						|| bundle.getString("DEST_ACCOUNT_NUM").length() <= 0) {
					accountNumLayout.setVisibility(View.GONE);
				} else {
					accountNumber.setText(": " + bundle.getString("DEST_ACCOUNT_NUM"));
				}
			} catch (NullPointerException e) {

				accountNumLayout.setVisibility(View.GONE);
			}

			try {
				if (bundle.getString("AMOUNT").toString().equalsIgnoreCase("null")
						|| bundle.getString("AMOUNT").length() <= 0) {

					amountLayout.setVisibility(View.GONE);

				} else {
					amount.setText(": " + bundle.getString("AMOUNT"));
				}
			} catch (NullPointerException e) {

				amountLayout.setVisibility(View.GONE);
			}

		}

		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {
				/** Set Parameters for service call */
				// System.out.println("raaaaaaa11111"+bundle.getInt("POSITION")+"
				// aaaa "+bundle.getInt("POSITION2"));

				valueContainer = new ValueContainer();
				valueContainer.setServiceName(Constants.SERVICE_BANK);
				valueContainer.setTransferType(bundle.getString("TRANSFER_TYPE"));
				valueContainer.setParentTxnId(bundle.getString("PTFNID"));
				valueContainer.setTransferId(bundle.getString("TFNID"));
				valueContainer.setConfirmed("true");
				valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
				valueContainer.setTransferType(bundle.getString("TRANSFER_TYPE"));
				System.out.println("Testing>>transferType" + bundle.getString("TRANSFER_TYPE"));

				if (bundle.getString("TRANSFER_TYPE").equals("toSmartFren")) {

					System.out.println("Testing>>toSmartFren");
					valueContainer.setTransactionName(Constants.TRANSACTION_TRANSFER);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setSourcePin(bundle.getString("PIN"));
					valueContainer.setAmount(bundle.getString("AMOUNT"));
					valueContainer.setDestinationMdn(bundle.getString("RCVNUM"));
					valueContainer.setDestinationPocketCode("2");

				} else if (bundle.getString("TRANSFER_TYPE").equals("toBankSinarmas")) {

					System.out.println("Testing>>toBankSinarmas");
					valueContainer.setTransactionName(Constants.TRANSACTION_TRANSFER);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setSourcePin(bundle.getString("PIN"));
					valueContainer.setAmount(bundle.getString("AMOUNT"));
					valueContainer.setDestinationBankAccount(bundle.getString("DEST"));
					valueContainer.setDestinationPocketCode("2");
				} else {
					System.out.println("Testing>>toOtherBank");
					valueContainer.setSourcePin(bundle.getString("PIN"));
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setTransactionName(Constants.TRANSACTION_INTERBANK_TRANSFER);
				}

				try {
					if (bundle.getString("MFA_MODE").equalsIgnoreCase("OTP")) {
						valueContainer.setOTP(bundle.getString("OTP"));
						valueContainer.setMfaMode(bundle.getString("MFA_MODE"));
					}
				} catch (Exception e1) {
				}

				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, ConfirmAddReceiver.this);

				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					dialog = new ProgressDialog(ConfirmAddReceiver.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.eng_loading));
					dialog.show();
					/**
					dialog = ProgressDialog.show(ConfirmAddReceiver.this, "  Bank Sinarmas               ",
							getResources().getString(R.string.eng_loading), true);
							**/

				} else {
					dialog = new ProgressDialog(ConfirmAddReceiver.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.bahasa_loading));
					dialog.show();
					/**
					dialog = ProgressDialog.show(ConfirmAddReceiver.this, "  Bank Sinarmas               ",
							getResources().getString(R.string.bahasa_loading), true);
							**/
				}

				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						if (responseXml != null) {
							XMLParser obj = new XMLParser();
							/** Parse the response */
							EncryptedResponseDataContainer responseContainer = null;
							try {
								responseContainer = obj.parse(responseXml);
								msgCode = Integer.parseInt(responseContainer.getMsgCode());
							} catch (Exception e) {
								msgCode = 0;
								e.printStackTrace();
							}
							System.out.println("Message Code &&" + msgCode);
							if (!(msgCode == 703) && !(msgCode == 81)) {
								if (responseContainer.getMsg() == null) {
									if (selectedLanguage.equalsIgnoreCase("ENG")) {
										alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
									} else {
										alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
									}
								} else {
									if(msgCode == 2000){
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage("You have entered incorrect code. Please try again and ensure that you enter the correct code.");
										} else {
											alertbox.setMessage("Kode yang Anda masukkan salah. Silakan coba lagi dan pastikan Anda memasukkan kode yang benar.");
										}
									}else{
										alertbox.setMessage(responseContainer.getMsg());
									}
									
								}

								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {

										if (msgCode == 631) {
											Intent intent = new Intent(getBaseContext(), LoginScreen.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										} else {
											SharedPreferences settings = ConfirmAddReceiver.this
													.getSharedPreferences("LOGIN_PREFERECES", 0);
											String fragName = settings.getString("ActivityName", "");
											if (fragName.equals("ToBankSinarmas")) {
												Intent intent = new Intent(getBaseContext(), HomeScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											} else if (fragName.equals("ToOtherBankDetails")) {
												Intent intent = new Intent(getBaseContext(), HomeScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											} else {
												Intent intent = new Intent(getBaseContext(), HomeScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											}
										}

									}
								});
								alertbox.show();

							} else {
								System.out.println("hieeeeeeeee" + responseContainer.getMsg());
								Intent intent = new Intent(ConfirmAddReceiver.this, ConfirmationScreen.class);
								intent.putExtra("MSG", responseContainer.getMsg());
								intent.putExtra("ADITIONAL_INFO", "");
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}

						} else {

							dialog.dismiss();
							if (selectedLanguage.equalsIgnoreCase("ENG")) {
								alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
							} else {
								alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
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
							System.out.println("Testing XML ");
							responseXml = webServiceHttp.getResponseSSLCertificatation();
							System.out.println("Testing XML" + responseXml);
						} catch (Exception e) {
							responseXml = null;
							System.out.println("Testing Exception");
						}
						handler.sendEmptyMessage(0);
					}
				};
				checkUpdate.start();

			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(ConfirmAddReceiver.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});
	}

	public void onBackPressed() {
		if (bundle.getString("TRANSFER_TYPE").equals("toBank")) {
			Intent intent = new Intent(ConfirmAddReceiver.this, SmartFrenDetails.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (bundle.getString("TRANSFER_TYPE").equals("toBankSinarmas")) {

			Intent intent = new Intent(ConfirmAddReceiver.this, ToBankSinarmas.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} else {
			Intent intent = new Intent(ConfirmAddReceiver.this, ToOtherBankDetails.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

}
