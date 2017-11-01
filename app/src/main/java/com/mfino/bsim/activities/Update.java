
package com.mfino.bsim.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import com.mfino.bsim.R;
import com.mfino.bsim.containers.ValueContainer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Update extends Activity {
	
	Button update,cancel;
	TextView tv;
	ValueContainer valueContainer;
	private Bundle bundle;
	private AlertDialog.Builder alertbox;
	private ProgressDialog progressDialog;
	private boolean error=false;
	SharedPreferences languageSettings;
	String selectedLanguage;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.update);
		
		//Header code...
		bundle = getIntent().getExtras();
		
		alertbox = new AlertDialog.Builder(Update.this);
		update=(Button)findViewById(R.id.Update);
		cancel=(Button)findViewById(R.id.Cancel);
		tv=(TextView)findViewById(R.id.update_msg);
		error=false;
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			update.setText(getResources().getString(R.string.eng_confirm));
			cancel.setText(getResources().getString(R.string.eng_cancel));
			tv.setText("New Version of app is available, do you want to upgrade");
			alertbox.setMessage(getResources().getString(R.string.eng_updateText));

		} else {
			
			update.setText(getResources().getString(R.string.bahasa_confirm));
			cancel.setText(getResources().getString(R.string.bahasa_cancel));
			tv.setText("New Version of app is available, do you want to upgrade");
			alertbox.setMessage(getResources().getString(R.string.bahasa_updateText));

		}
		//System.out.println("hiiiii"+valueContainer.getAppUpdateURL());
		
       
           update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				//   String url=bundle.getString("msg"); //bundle.getString("msg");
				//if(!url.startsWith("http://") && !url.startsWith("https://"))
					//url = "http://" + url;
				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					progressDialog = ProgressDialog.show(Update.this,"BankSinarmas             ", getResources().getString(R.string.eng_loading));

				} else {
					progressDialog = ProgressDialog.show(Update.this,"BankSinarmas             ", getResources().getString(R.string.bahasa_loading));
				}

				new Thread() {

					public void run() {

					try{

						Update(bundle.getString("msg"));  
					} catch (Exception e) {
						
						progressDialog.dismiss();
						error=true;
					    
					Log.e("tag", e.getMessage());

					}
			
			    }				
			  }.start();
			
    			  if(error)
	    		  {
				   if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_updatedDownload));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_updatedDownload));
					}
				    alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface arg0, int arg1) {
	
		        	    Intent intent = new Intent(Update.this, HomeScreen.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						error=false;
		              } 
		            });
		             alertbox.show();
		    	  }			
			}		
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
		@Override
			public void onClick(View arg0) {
			
				Intent intent = new Intent(Update.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	
		 public void Update(String apkurl){
		        try {
		        	
		        	System.out.println("Testing>>APKURL"+apkurl);
		              URL url = new URL(apkurl);
		              HttpURLConnection c = (HttpURLConnection) url.openConnection();
		              c.setRequestMethod("GET");
		              c.setDoOutput(true);
		              c.connect();

		              String PATH = Environment.getExternalStorageDirectory() + "/download/";
		              File file = new File(PATH);
		              file.mkdirs();
		              File outputFile = new File(file, "app.apk");
		              FileOutputStream fos = new FileOutputStream(outputFile);

		              InputStream is = c.getInputStream();

		              byte[] buffer = new byte[1024];
		              int len1 = 0;
		              while ((len1 = is.read(buffer)) != -1) {
		                  fos.write(buffer, 0, len1);
		              }
		              fos.close();
		              is.close();
		              
		              Intent intent = new Intent(Intent.ACTION_VIEW);
		              intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");   
		              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		              progressDialog.dismiss();
		              startActivity(intent);
		          } catch (IOException e) {
		        	  progressDialog.dismiss();
		              Toast.makeText(getApplicationContext(), "Update error!"+e, Toast.LENGTH_LONG).show();
		          }
	
	}

}


