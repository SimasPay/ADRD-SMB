package com.mfino.bsim;
import java.util.ArrayList;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.handset.security.CryptoService;
/** @author pramod */

public class ActivationDisclosure extends Activity {
    /** Called when the activity is first created. */
	private Button  agreeButton,decline;
	private ValueContainer valueContainer;
	private Bundle bundle;
	private String responseXml;
	private AlertDialog.Builder alertbox;
	private String sctl,otpValue;
	SharedPreferences languageSettings,encrptionKeys;
	String selectedLanguage;
	ProgressDialog dialog;
	String newPin,bankPin,cardPan;
	Context context;
	   DBHelper mydb ;
	   String session="false";
	   SharedPreferences settings;
	   String f_mdn;
	  	 ArrayList<String> array_session = new ArrayList<String>();
	  	String session_value;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_disclosure);
        
        settings = getSharedPreferences("LOGIN_PREFERECES",	Context.MODE_WORLD_READABLE);
        f_mdn = settings.getString("mobile", "");
        context=this;
		   mydb = new DBHelper(this);

        
      //Header code...
	     TextView screeTitle=(TextView)findViewById(R.id.screenTitle);
	     ImageButton back=(ImageButton)findViewById(R.id.back);
	     TextView disclosure=(TextView)findViewById(R.id.terms_conditions);
	     
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	     
        bundle = getIntent().getExtras();
        alertbox = new AlertDialog.Builder(this);
        agreeButton = (Button)findViewById(R.id.agreeButton);
        decline = (Button)findViewById(R.id.decline);
        
        //Public key
        encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES",	Context.MODE_WORLD_READABLE);
        
    	//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			disclosure.setText(Html.fromHtml(getResources().getString(R.string.eng_disclosure)));
			screeTitle.setText(getResources().getString(R.string.eng_activationDisclosure));
			agreeButton.setText(getResources().getString(R.string.eng_agree));
			decline.setText(getResources().getString(R.string.eng_cancel));
			back.setBackgroundResource(R.drawable.back_button);

		} else {
			
			disclosure.setText(Html.fromHtml(getResources().getString(R.string.bahasa_disclosure)));
			screeTitle.setText(getResources().getString(R.string.bahasa_activationDisclosure));
			agreeButton.setText(getResources().getString(R.string.bahasa_agree));
			decline.setText(getResources().getString(R.string.bahasa_cancel));
		}
		decline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(	ActivationDisclosure.this,ActivationHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
        agreeButton.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				
				 /** Set Parameters for Activation Service. */
				

				
				String module=encrptionKeys.getString("MODULE", "NONE");
				String exponent=encrptionKeys.getString("EXPONENT", "NONE");
				String confirmPin=CryptoService.encryptWithPublicKey(module, exponent, bundle.getString("CONFIRM_PIN").getBytes());
				newPin=CryptoService.encryptWithPublicKey(module, exponent, bundle.getString("PIN").getBytes());
				 int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			     if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
			         if ((checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS)
			                 != PackageManager.PERMISSION_GRANTED) && checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS)
			                 != PackageManager.PERMISSION_GRANTED) {

			             requestPermissions(new String[]{Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS},
			                     109);
			         } 
			     }
		        
				if(bundle.getString("ACTIVATION_TYPE").equals("Activation")){
					
		        	System.out.println("Testing>>Activation>>>OTP>>"+bundle.getString("OTP"));
		        	 valueContainer = new ValueContainer();
					 valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					 valueContainer.setSourceMdn(bundle.getString("MDN"));
					 valueContainer.setTransactionName(Constants.TRANSACTION_ACTIVATION);
					 valueContainer.setOTP(bundle.getString("OTP"));
					 //Without RSA
					/* valueContainer.setActivationConfirmPin(bundle.getString("PIN"));
					 valueContainer.setActivationNewPin(bundle.getString("PIN"));*/
					 //RSA
					 valueContainer.setActivationConfirmPin(newPin);
					 valueContainer.setActivationNewPin(newPin);
		        	
		        }else{
		        	
		        	 bankPin=CryptoService.encryptWithPublicKey(module, exponent, bundle.getString("SOURCE_PIN").getBytes());
		        	 cardPan=CryptoService.encryptWithPublicKey(module, exponent, bundle.getString("CARD_PAN").getBytes());
		        	 System.out.println("<<<Testing1>>"+bankPin);
		        	 valueContainer = new ValueContainer();
					 valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
					 valueContainer.setSourceMdn(bundle.getString("MDN"));
					 valueContainer.setTransactionName(Constants.TRANSACTION_REACTIVATION);
					 
					//Without RSA
					/* valueContainer.setActivationConfirmPin(bundle.getString("PIN"));
					 valueContainer.setActivationNewPin(bundle.getString("PIN"));
					 valueContainer.setCardPan(bundle.getString("CARD_PAN"));
					 valueContainer.setBankPin(bundle.getString("SOURCE_PIN"));*/
					 //RSA
					 valueContainer.setActivationConfirmPin(newPin);
					 valueContainer.setActivationNewPin(newPin);
					 valueContainer.setCardPan(cardPan);
					 valueContainer.setBankPin(bankPin);
		        }
				 
				 
				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer,ActivationDisclosure.this);
							
				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					dialog = ProgressDialog.show(ActivationDisclosure.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

				} else {
					dialog = ProgressDialog.show(ActivationDisclosure.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
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
		
										alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0, int arg1) {
		
											}
										});
										alertbox.show();
	
								} else if (responseContainer.getMsgCode().equals("2040")||responseContainer.getMsgCode().equals("2041"))  {
	
									//Constants.SOURCE_MDN_PIN = oldpinValue.getText().toString();
									dialog.dismiss();
	
									try {
										System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
										valueContainer.setMfaMode(responseContainer.getMfaMode());
									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}
									
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										
										/********************************************** 2FA factor code start ****************************************************************/
										
										try {
	
											//final ProgressDialog dialog1 = ProgressDialog.show(ActivationDisclosure.this,"  Banksinarmas               ","Please Wait for SMS....   ",true);
											Long startTimeInMillis = new java.util.Date().getTime();
	
											while (true) {
													
												Thread.sleep(2000);
												System.out.println("Testing>>inside Loop");
												final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
												Cursor c = getContentResolver().query(SMS_INBOX, null,null, null,"DATE desc");

												c.moveToFirst();
												for (int i = 0; i <5; i++) {
													String body = c.getString(c.getColumnIndexOrThrow("body")).toString().trim();
													String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
													
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
		
													if (!(otpValue == null)) {
														System.out.println("Testing>>SCTL");
														break;
													} else {
		
														System.out.println("Testing>>SCTL>>else");
														
														if (new java.util.Date().getTime()- startTimeInMillis >= Constants.MFA_CONNECTION_TIMEOUT) {
															System.out.println("Testing>>TimeOut>>");
															break;
														}
		
													}
	
											}
											System.out.println("Testing>>OTP>>"+ otpValue);
											
											if (otpValue == null) {
												
												//dialog1.dismiss();
												System.out.println("Testing>>OTP>>null");
												alertbox.setMessage("Dear Customer,\n Transaction failed due to sms not received.Please try again. ");
												alertbox.setNeutralButton(
														"OK",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																finish();
															}
														});
												alertbox.show();
											} else {
												//dialog1.dismiss();
												System.out.println("Testing>>OTP>>send success");
												
											    if(bundle.getString("ACTIVATION_TYPE").equals("Activation")){
														
													   Intent intent = new Intent(	ActivationDisclosure.this,ActivationConfirm.class);
														intent.putExtra("MSG", responseContainer.getMsg());
														intent.putExtra("SCREEN", "Activation");
														intent.putExtra("OTP",otpValue);
														intent.putExtra("ACTIVATION_OTP",bundle.getString("OTP"));
														//RSA
														intent.putExtra("PIN",newPin);
														intent.putExtra("CONFIRM_PIN",newPin);
														//Without RSA
														/*intent.putExtra("PIN",bundle.getString("PIN"));
														intent.putExtra("CONFIRM_PIN",bundle.getString("CONFIRM_PIN"));*/
														intent.putExtra("MDN",bundle.getString("MDN"));
														intent.putExtra("ACTIVATION_TYPE",bundle.getString("ACTIVATION_TYPE"));
														intent.putExtra("SCTL",responseContainer.getSctl());
														intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
														intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
														startActivity(intent);
													 
										        	
										        }else{
										        	
														Intent intent = new Intent(	ActivationDisclosure.this,ActivationConfirm.class);
														intent.putExtra("MSG", responseContainer.getMsg());
														intent.putExtra("SCREEN", "Activation");
														intent.putExtra("OTP",otpValue);
														intent.putExtra("MDN",bundle.getString("MDN"));
														
														//With RSA
														intent.putExtra("CARD_PAN",cardPan);
														intent.putExtra("PIN",newPin);
														intent.putExtra("SOURCE_PIN", bankPin);
														
														//Without RSA
														/*intent.putExtra("CARD_PAN", bundle.getString("CARD_PAN"));
														intent.putExtra("PIN",bundle.getString("PIN"));
														intent.putExtra("SOURCE_PIN", bundle.getString("SOURCE_PIN"));*/
														
														intent.putExtra("ACTIVATION_TYPE",bundle.getString("ACTIVATION_TYPE"));
														intent.putExtra("SCTL",sctl);
														intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
														intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
														startActivity(intent);
										        }
												
											}
	
										} catch (Exception e) {
											// dialog1.dismiss();
											System.out.println("Testing>>exception>>>>>"+e);
										}
										
										/********************************************** 2FA factor code end ****************************************************************/
										
									} else {
	
										Intent intent = new Intent(	ActivationDisclosure.this,ActivationConfirmation.class);
										intent.putExtra("MSG", responseContainer.getMsg());
										intent.putExtra("SCREEN", "Activation");
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
	
									}
	
									
									
								}
								else{
									alertbox.setMessage(responseContainer.getMsg());
									alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface arg0, int arg1) {
	
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
								alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0, int arg1) {
	
											}
										});
								alertbox.show();
						}

					}
                   };
                  
                   final Thread checkUpdate = new Thread() {

                	   public void run() {
                		   
                		try{
                			   responseXml= webServiceHttp.getResponseSSLCertificatation();  /** Service call for Activation*/
                  		   }catch (Exception e) {
            				responseXml=null;				
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 109) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            	Log.e("if_permission","*********");
              
            } else {
            	Log.e("elseeeee_permission","*********");

            }
        }
    }

}

