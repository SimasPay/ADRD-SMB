package com.mfino.bsim.transfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class ToBankSinarmas extends Activity {

	private Button btn_ok;
	private EditText pinValue,creditNoValue,amountValue;
	private AlertDialog.Builder alertbox;
	private String billerAmount;
	private String responseXml;
	ValueContainer valueContainer ;
	String bankAccount;
	String otpValue,sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	/** Called when the activity is first created. */	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_bank_sinarmas);
		context=this;
		//Header code...
		 View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	    // screeTitle.setText("TO BANKSINARMAS");
	     Button back=(Button)headerContainer.findViewById(R.id.back);
	     Button home=(Button)headerContainer.findViewById(R.id.home_button);
	     
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});
	     
	     
	     home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(ToBankSinarmas.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		creditNoValue = (EditText) findViewById(R.id.ed_creditACValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);	
		TextView destAcountTxt=(TextView)findViewById(R.id.fundTransfer_toSinarmas_creditAC);
		TextView amountTxt=(TextView)findViewById(R.id.fundTransfer_toSinarmas_amount);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
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
		
		alertbox = new AlertDialog.Builder(this);

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (isRequiredFieldEmpty()) {
					
					boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
					if(!networkCheck){
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond), context);
						}else{
							ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond), context);
						}
										
					}else if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

								}
							});
					alertbox.show();
				} else if (pinValue.getText().length() < 4) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,int arg1) {

								}
							});
					alertbox.show();
				} else {

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
						dialog = ProgressDialog.show(ToBankSinarmas.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

					} else {
						dialog = ProgressDialog.show(ToBankSinarmas.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {
								/** Parse response xml. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
									Log.e("___responseContainer_code__msgCode___", responseContainer+"");
								} catch (Exception e) {

									e.printStackTrace();
								}

								//dialog.dismiss();

								int msgCode = 0;
								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
									Log.e("___msg_code__msgCode___", msgCode+"");
								} catch (Exception e) {
									msgCode = 0;
								}
								System.out.println("Testing>>message code>>>"+msgCode);
								if (!(msgCode == 72)) {
									Log.e("msg_code__if", msgCode+"");
									System.out.println("Testing>>not result>>>"+msgCode);
									if (responseContainer.getMsg() == null) {
										Log.e("___responseContainer.getMsg()e___", responseContainer.getMsg()+"");
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
										}
									} else {
										Log.e("________________responseContainer.getMsg()=========___", responseContainer.getMsg()+"");

										alertbox.setMessage(responseContainer.getMsg());
									}

									alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {

												public void onClick(DialogInterface arg0,int arg1) {
													dialog.dismiss();
													Intent intent = new Intent(getBaseContext(),HomeScreen.class);
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
										System.out.println("Testing>>MFAMODE>>>"+responseContainer
												.getMfaMode());
										Log.e("___1---------.getMfaMode()___", responseContainer.getMfaMode()+"");

										if (responseContainer.getMfaMode() == null) {
											Log.e("___222---------.getMfaMode()___", responseContainer.getMfaMode()+"");

											valueContainer.setMfaMode("NONE");
										} else {
											Log.e("___33333333---------.getMfaMode()___", responseContainer.getMfaMode()+"");

											valueContainer.setMfaMode(responseContainer
															.getMfaMode());
										}

									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
										Log.e("___444444---------.getMfaMode()___", responseContainer.getMfaMode()+"");

									}
								if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
											System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
											Log.e("MFA MODE..", responseContainer.getMfaMode()+"");

									try {
										
										 //dialog.dismiss();
										 //final ProgressDialog dialog1 = ProgressDialog.show(ToBankSinarmas.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
								 		Long startTimeInMillis = new java.util.Date().getTime();
								 		
										while(true){
											
											Thread.sleep(2000);
											System.out.println("Testing>>inside Loop");
											final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
											Cursor c = getContentResolver().query(SMS_INBOX, null,null, null,"DATE desc");

											c.moveToFirst();
											for (int i = 0; i < 10; i++) {
												String body = c.getString(c.getColumnIndexOrThrow("body")).toString().trim();
												Log.e("bodyyyyyyyyyy..", body+"");
												String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
												Log.e("number,,,,,,number..", number+"");
												if(body.contains("Kode Simobi Anda")&&body.contains(responseContainer.getSctl())){
													
													otpValue=body.substring(new String("Kode Simobi Anda ").length(), body.indexOf("(no ref"));
													sctl=body.substring(body.indexOf(":")+1, body.indexOf(")"));
													break;
													
												}else if(body.contains("Your Simobi Code is")&&body.contains(responseContainer.getSctl())){
													
													otpValue=body.substring(new String("Your Simobi Code is ").length(), body.indexOf("(ref"));
													sctl=body.substring(body.indexOf("(ref no: ")+new String("(ref no: ").length(), body.indexOf(")"));
													break;
												}else{
													c.moveToNext();
												}
												
											}
											c.close();
											
											if(!(otpValue==null)){
												System.out.println("Testing>>SCTL");
												break;
											}else{
												
												System.out.println("Testing>>SCTL>>else");
												if(new java.util.Date().getTime() - startTimeInMillis>=Constants.MFA_CONNECTION_TIMEOUT){
													System.out.println("Testing>>TimeOut>>");
													break;
												}
													
												
											}
											
										}
										System.out.println("Testing>>OTP>>"+ otpValue);
										if(otpValue==null){
											//dialog1.dismiss();
											System.out.println("Testing>>OTP>>null"); 
											if (selectedLanguage.equalsIgnoreCase("ENG")) {
												alertbox.setMessage(getResources().getString(R.string.eng_transactionFail));
											} else {
												alertbox.setMessage(getResources().getString(R.string.bahasa_transactionFail));
											}
											 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								          public void onClick(DialogInterface arg0, int arg1) {
								        	  dialog.dismiss();
								        	  
								          	}
											 });
											 alertbox.show();
										}else{
											dialog.dismiss();
											//dialog1.dismiss();
											Intent intent = new Intent(ToBankSinarmas.this,ConfirmAddReceiver.class);
			           						System.out.print("Testing>>name>>"+responseContainer.getCustName());
			           						System.out.print("Testing>numbaer>>>"+responseContainer.getDestMDN());
			           						System.out.print("Testing>>ac>>"+responseContainer.getAccountNumber());
			       							intent.putExtra("PIN", pinValue.getText().toString());
			       							intent.putExtra("MSG", responseContainer.getMsg());
			       							intent.putExtra("CUST_NAME", responseContainer.getCustName());
			       							//intent.putExtra("DEST_NUMBER", responseContainer.getDestMDN());
			       							intent.putExtra("DEST_BANK", responseContainer.getDestBank());
			       							intent.putExtra("DEST_ACCOUNT_NUM", responseContainer.getAccountNumber());
			       							intent.putExtra("AMOUNT", responseContainer.getAmount());
			       							intent.putExtra("DEST", bankAccount);
			       							intent.putExtra("AMT", billerAmount);
			       							intent.putExtra("OTP", otpValue);
			       							intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
			       							intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
			       							intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
			       							intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
			
			       							startActivity(intent);
											}

										} catch (Exception e) {
											System.out.println("Testing>>exception>>");
										}
									}else{
										
										System.out.println("Testing>>OTP else>>>");
									/*	Intent intent = new Intent(ToBankSinarmas.this,ConfirmAddReceiver.class);
		           						System.out.print("Testing>>name>>"+responseContainer.getCustName());
		           						System.out.print("Testing>numbaer>>>"+responseContainer.getDestMDN());
		           						System.out.print("Testing>>ac>>"+responseContainer.getAccountNumber());
		       							intent.putExtra("PIN", pinValue.getText().toString());
		       							intent.putExtra("MSG", responseContainer.getMsg());
		       							intent.putExtra("CUST_NAME", responseContainer.getCustName());
		       							//intent.putExtra("DEST_NUMBER", responseContainer.getDestMDN());
		       							intent.putExtra("DEST_BANK", responseContainer.getDestBank());
		       							intent.putExtra("DEST_ACCOUNT_NUM", responseContainer.getAccountNumber());
		       							intent.putExtra("AMOUNT", responseContainer.getAmount());
		       							intent.putExtra("DEST", bankAccount);
		       							intent.putExtra("AMT", billerAmount);
		       							intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
		       							intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
		       							intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());*/
		       							
		       							Intent intent = new Intent(ToBankSinarmas.this,ConfirmAddReceiver.class);
		           						System.out.print("Testing>>name>>"+responseContainer.getCustName());
		           						System.out.print("Testing>numbaer>>>"+responseContainer.getDestMDN());
		           						System.out.print("Testing>>ac>>"+responseContainer.getAccountNumber());
		       							intent.putExtra("PIN", pinValue.getText().toString());
		       							intent.putExtra("MSG", responseContainer.getMsg());
		       							intent.putExtra("CUST_NAME", responseContainer.getCustName());
		       							//intent.putExtra("DEST_NUMBER", responseContainer.getDestMDN());
		       							intent.putExtra("DEST_BANK", responseContainer.getDestBank());
		       							intent.putExtra("DEST_ACCOUNT_NUM", responseContainer.getAccountNumber());
		       							intent.putExtra("AMOUNT", responseContainer.getAmount());
		       							intent.putExtra("DEST", bankAccount);
		       							intent.putExtra("AMT", billerAmount);
		       							//intent.putExtra("OTP", otpValue);
		       							intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
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
								alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0,int arg1) {
												dialog.dismiss();
												Intent intent = new Intent(getBaseContext(),HomeScreen.class);
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
		
		if(!(pinValue.getText().toString().equals(""))&&!(creditNoValue.getText().toString().equals(""))&&!(amountValue.getText().toString().equals("")))
		{
			return false;
		}
		else{
			return true;
		}	
	}

}
