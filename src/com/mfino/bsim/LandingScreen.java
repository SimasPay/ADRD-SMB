package com.mfino.bsim;

import java.util.ArrayList;
import java.util.HashMap;

import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
/** @author himanshu.kumar */
public class LandingScreen extends Activity {
    /** Called when the activity is first created. */
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String,Object>>();
	SharedPreferences languageSettings;
	String selectedLanguage;
	SharedPreferences encrptionKeys;
	private boolean getPublic=true;
	private AlertDialog.Builder alertbox;
	ValueContainer valueContainer;
	public String responseXml = null;
	ProgressDialog dialog;
	int flag;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        
        languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",	Context.MODE_WORLD_READABLE);
        encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES",	Context.MODE_WORLD_READABLE);
        selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
        System.out.println("Testing>>"+selectedLanguage);
     /*   
        RelativeLayout login=(RelativeLayout)findViewById(R.id.login);
        RelativeLayout active=(RelativeLayout)findViewById(R.id.active);
        RelativeLayout contact=(RelativeLayout)findViewById(R.id.contact_us);
        TextView activationText=(TextView)findViewById(R.id.textView2);*/
        LinearLayout mlogin=(LinearLayout)findViewById(R.id.mlogin);
        LinearLayout active=(LinearLayout)findViewById(R.id.active);
        LinearLayout eform=(LinearLayout)findViewById(R.id.eform);
        
        eform.setVisibility(View.GONE);

        RelativeLayout contact=(RelativeLayout)findViewById(R.id.contact_us);
        TextView activationText=(TextView)findViewById(R.id.textView2);
        
        if(selectedLanguage.equalsIgnoreCase("ENG")){
	    	 System.out.println("Testing1>>"+selectedLanguage);
	    	 activationText.setText(getResources().getString(R.string.eng_activation));
	        	
	        	
	        }else{
	        	activationText.setText(getResources().getString(R.string.bahasa_activation));
	        	
	        }
        

	    //Get public key
        if(getPublic==true)
        	getPublick();
        
        mlogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LandingScreen.this,LoginScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
eform.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//flag=1;
				Intent intent=new Intent(LandingScreen.this,WebviewActivity.class);
				//intent.putExtra("flag","1");
				Log.e("check_flag_value", flag+"");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
        
		active.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(LandingScreen.this,ActivationHome.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);;
					}
				});
		contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setContentView(R.layout.contact_us);
			     TextView screeTitle=(TextView)findViewById(R.id.screenTitle);
			     Button back=(Button)findViewById(R.id.back);
			     
			     
			     back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						onCreate(null);
					}
				});
				TextView customerCare=(TextView)findViewById(R.id.bank_sinarmas_care);
				TextView phoneNum=(TextView)findViewById(R.id.phone_num);
				TextView companyWebSite=(TextView)findViewById(R.id.company_website);
				TextView webSite=(TextView)findViewById(R.id.website);
				TextView mailUsAt=(TextView)findViewById(R.id.mail_us_at);
				TextView mail=(TextView)findViewById(R.id.mail);
				
				  if(selectedLanguage.equalsIgnoreCase("ENG")){
						  screeTitle.setText("Contact us");
						  customerCare.setText(getResources().getString(R.string.eng_bankSinarmasCare));
						  phoneNum.setText(" : 500 153"+"\n"+"(021)501 88888");
						  companyWebSite.setText(getResources().getString(R.string.eng_companyWebsite));
						  webSite.setText(" : www.banksinarmas.com");
						  mailUsAt.setText(getResources().getString(R.string.eng_mainUsat));
						  mail.setText(" : care@banksinarmas.com");
					  
				        }else{
				        	
				          screeTitle.setText("Contact kami");
				          customerCare.setText(getResources().getString(R.string.bahasa_bankSinarmasCare));
						  phoneNum.setText(" : 500 153"+"\n"+"  (021)501 88888");
						  companyWebSite.setText(getResources().getString(R.string.bahasa_companyWebsite));
						  webSite.setText(" : www.banksinarmas.com");
						  mailUsAt.setText(getResources().getString(R.string.bahasa_mainUsat));
						  mail.setText(" : care@banksinarmas.com");
	
				}

			}
		});
	}
    
    //Get Public Key
    public  void getPublick(){
 	   
 		/** Set Parameters for service calling. */
 		valueContainer = new ValueContainer();
 		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
 		valueContainer.setTransactionName(Constants.TRANSACTION_GETPUBLICKEY);

 		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, LandingScreen.this);
 		
 		if (selectedLanguage.equalsIgnoreCase("ENG")) {
 			dialog = ProgressDialog.show(LandingScreen.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

 		} else {
 			dialog = ProgressDialog.show(LandingScreen.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
 		}
 		alertbox = new AlertDialog.Builder(this);
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

 					if (! responseContainer.getSuccess().equalsIgnoreCase("true")) {
 						if (selectedLanguage.equalsIgnoreCase("ENG")) {
 							alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
 						} else {
 							alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
 						}
 						alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
 									public void onClick(DialogInterface arg0,int arg1) {

 									}
 								});
 						alertbox.show();

 					} else{
 						System.out.println(responseContainer.getPublicKeyExponet()+"MODULUS:"+responseContainer.getPublicKeyModulus());
 						encrptionKeys.edit().putString("MODULE",responseContainer.getPublicKeyModulus()).commit();
 						encrptionKeys.edit().putString("EXPONENT",responseContainer.getPublicKeyExponet()).commit();

 					} 

 				} else {
 					dialog.dismiss();
 					if (selectedLanguage.equalsIgnoreCase("ENG")) {
 						alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
 					} else {
 						alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
 					}
 					alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
 								public void onClick(DialogInterface arg0,int arg1) {
 									finish();

 								}
 							});
 					alertbox.show();
 				}

 			}
 		};

 		final Thread checkUpdate = new Thread() {
 			/**
 			 * Service call in thread in and getting response as xml
 			 * in string.
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
}
