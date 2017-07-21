package com.mfino.bsim.account;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.Confirmation_History;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

@SuppressLint({ "ParserError", "ParserError" })

public class AccountSelection extends Activity {
	String amount = "";
	String accountNumber = "";
	String transactionTime = "";
	private ImageView mimage1, mimage2;
	private ImageView mimage3, language;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	private AlertDialog.Builder alertbox;
	ValueContainer valueContainer;
	public String responseXml = null;
	SharedPreferences languageSettings, settings;
	String selectedLanguage;
	ProgressDialog dialog;
	String pin;
	Context context;
	ArrayList<String> array = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		setContentView(R.layout.common_listitem_account);
		context = this;
		alertbox = new AlertDialog.Builder(AccountSelection.this, R.style.MyAlertDialogStyle);
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		// languageSettings.edit().clear();
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		// Pin
		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		String mobileNumber = settings.getString("mobile", "");
		pin = settings.getString("pin", "");

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		screeTitle.setText("ACCOUNT");
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AccountSelection.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountSelection.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		mimage1 = (ImageView) findViewById(R.id.imageView1);
		mimage2 = (ImageView) findViewById(R.id.imageView2);
		mimage3 = (ImageView) findViewById(R.id.imageView3);
		language = (ImageView) findViewById(R.id.language_change);
		TextView balance = (TextView) findViewById(R.id.textView1);
		TextView history = (TextView) findViewById(R.id.textView2);
		TextView changePin = (TextView) findViewById(R.id.textView3);
		TextView language_change = (TextView) findViewById(R.id.lang);

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_myaccont));
			balance.setText(getResources().getString(R.string.eng_checkBalance));
			history.setText(getResources().getString(R.string.eng_history));
			changePin.setText(getResources().getString(R.string.eng_changePin));
			language_change.setText(getResources().getString(R.string.eng_languageSelection));
			/*
			 * mimage1.setImageResource(R.drawable.change_button);
			 * mimage2.setImageResource(R.drawable.check_button);
			 * mimage3.setImageResource(R.drawable.history_button);
			 * home.setBackgroundResource(R.drawable.home_icon1);
			 * back.setBackgroundResource(R.drawable.back_button);
			 */

		} else {

			screeTitle.setText(getResources().getString(R.string.bahasa_myaccont));
			balance.setText(getResources().getString(R.string.bahasa_checkBalance));
			history.setText(getResources().getString(R.string.bahasa_history));
			changePin.setText(getResources().getString(R.string.bahasa_changePin));
			language_change.setText(getResources().getString(R.string.bahasa_languageSelection));
			/*
			 * mimage1.setImageResource(R.drawable.bahasa_change_button);
			 * mimage2.setImageResource(R.drawable.bahasa_check_button);
			 * mimage3.setImageResource(R.drawable.bahasa_history_button);
			 * home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			 * back.setBackgroundResource(R.drawable.bahasa_back_button);
			 */

		}
		mimage1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(AccountSelection.this, ChangePin.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		mimage2.setOnClickListener(new View.OnClickListener() {
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

				} else {
					checkBalance();
				}
				// checkBalance();
			}
		});

		mimage3.setOnClickListener(new View.OnClickListener() {
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

				} else {
					viewTransactions();
				}
			}
		});
		language.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				languageSelection();
			}
		});
	}

	// Check Balance start
	public void checkBalance() {

		/** Set Parameters for service calling. */
		valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_BANK);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePin(pin);
		valueContainer.setTransactionName(Constants.TRANSACTION_CHECKBALANCE);
		valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, AccountSelection.this);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialog = new ProgressDialog(AccountSelection.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.eng_loading));
			dialog.show();
		} else {
			dialog = new ProgressDialog(AccountSelection.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.bahasa_loading));
			dialog.show();
		}

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {

					XMLParser obj = new XMLParser();
					/** Parsing of response. */
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
							public void onClick(DialogInterface arg0, int arg1) {

								Intent intent = new Intent(getBaseContext(), HomeScreen.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}
						});
						alertbox.show();

					} else if (Integer.parseInt(responseContainer.getMsgCode()) == 6
							|| Integer.parseInt(responseContainer.getMsgCode()) == 11
							|| Integer.parseInt(responseContainer.getMsgCode()) == 89
							|| Integer.parseInt(responseContainer.getMsgCode()) == 631) {
						Intent intent = new Intent(AccountSelection.this, ConfirmationScreen.class);
						intent.putExtra("MSG", responseContainer.getMsg());
						intent.putExtra("ADITIONAL_INFO", "");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					} else {
						try {
							String contentsXML = responseXml;
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							DocumentBuilder db;
							db = dbf.newDocumentBuilder();
							ByteArrayInputStream bis = new ByteArrayInputStream(contentsXML.getBytes());
							Document doc = db.parse(bis);
							String rootNode = doc.getDocumentElement().getNodeName();
							NodeList bookslist = doc.getElementsByTagName(rootNode);
							if (bookslist != null) {
								if(contentsXML.contains("<transactionTime>")){
									transactionTime = (String) ((Element) bookslist.item(0)).getElementsByTagName("transactionTime").item(0)
											.getChildNodes().item(0).getNodeValue();
								}
								if(contentsXML.contains("<amount>")){
									amount = (String) ((Element) bookslist.item(0)).getElementsByTagName("amount").item(0)
											.getChildNodes().item(0).getNodeValue();
								}
								if(contentsXML.contains("<accountNumber>")){
									accountNumber = (String) ((Element) bookslist.item(0))
											.getElementsByTagName("accountNumber").item(0).getChildNodes().item(0)
											.getNodeValue();
								}
								System.out.println("transactionTime " + transactionTime);
								System.out.println("amount " + amount);
								System.out.println("accountNumber " + accountNumber);
							} else {
								amount = "";
								accountNumber = "";
								transactionTime = "";
							}
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						Intent intent = new Intent(AccountSelection.this, ConfirmationScreen.class);
						Log.d("Simobi", "responseContainer : " + responseContainer + ", "
								+ responseContainer.getAccountNumber());
						intent.putExtra("MSG", responseContainer.getMsg());
						intent.putExtra("AccountNumber", accountNumber);
						intent.putExtra("TransactionTime", transactionTime);
						intent.putExtra("Amount", amount);
						intent.putExtra("ADITIONAL_INFO", "");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}

					/*
					 * } else {
					 * 
					 * alertbox.setMessage(responseContainer.getMsg());
					 * alertbox.setNeutralButton("OK", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface arg0,int arg1) {
					 * 
					 * Intent intent = new
					 * Intent(getBaseContext(),HomeScreen.class);
					 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent); pinValue.setText("");
					 * 
					 * } }); alertbox.show();
					 * 
					 * }
					 * 
					 * pinValue.setText("");
					 */

				} else {
					dialog.dismiss();
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

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
	
	public boolean isElementExists(String content, String tagName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		NodeList nList = null;
		try {
			db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
		    Document doc = db.parse(bis);
			doc.getDocumentElement().normalize();
			nList = doc.getElementsByTagName(tagName);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return nList.getLength() == 0 ? true : false;
	}

	// Check Balance end

	// View Transactions
	@SuppressLint("HandlerLeak")
	public void viewTransactions() {

		/** Set Parameters for service calling. */
		valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_BANK);
		valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
		valueContainer.setSourcePin(pin);
		valueContainer.setTransactionName(Constants.TRANSACTION_HISTORY);
		valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, AccountSelection.this);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialog = new ProgressDialog(AccountSelection.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.eng_loading));
			dialog.show();
		} else {
			dialog = new ProgressDialog(AccountSelection.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.bahasa_loading));
			dialog.show();
		}

		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {

					XMLParser obj = new XMLParser();
					/** Parsing of response. */
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
							public void onClick(DialogInterface arg0, int arg1) {

								Intent intent = new Intent(getBaseContext(), HomeScreen.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}
						});
						alertbox.show();

					}else if(responseContainer.getMsgCode()=="631" || Integer.parseInt(responseContainer.getMsgCode()) == 631 || responseContainer.getMsg().toLowerCase(Locale.getDefault()).trim().equals("please login again")){
						alertbox.setMessage(responseContainer.getMsg());
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent = new Intent(getBaseContext(), LoginScreen.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						});
						alertbox.show();
					}else {

						System.out.println("Testing>History>>" + responseContainer.getMsg());
						Intent intent = new Intent(AccountSelection.this, Confirmation_History.class);
						intent.putExtra("MSG", responseContainer.getMsg());
						intent.putExtra("Content", responseXml);
						intent.putExtra("MsgCode", responseContainer.getMsgCode());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}

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
	// View Transactions end

	// Language Selection

	public void languageSelection() {
		setContentView(R.layout.language_selection);
		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AccountSelection.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountSelection.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		TextView chooseLanguage = (TextView) findViewById(R.id.tv_choose_language);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_languageSelection));
			chooseLanguage.setText(getResources().getString(R.string.eng_chooseLanguage));

		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_languageSelection));
			chooseLanguage.setText(getResources().getString(R.string.bahasa_chooseLanguage));
		}

		Spinner selection = (Spinner) findViewById(R.id.spinner_language);
		Button submit = (Button) findViewById(R.id.submit);
		array.clear();
		array.add("INDONESIA");
		array.add("ENGLISH");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AccountSelection.this, R.layout.spinner_row, array);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selection.setAdapter(dataAdapter);
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			selection.setSelection(1);

		} else {
			selection.setSelection(0);
		}

		selection.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (array.get(arg2).equalsIgnoreCase("ENGLISH")) {
					languageSettings.edit().putString("LANGUAGE", "ENG").commit();
				} else {
					languageSettings.edit().putString("LANGUAGE", "BAHASA").commit();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onCreate(null);
			}
		});
	}
}
