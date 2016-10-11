package com.mfino.bsim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.flashiz.QRPayment2;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.utils.ImageSliderAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/** @author himanshu.kumar */
@SuppressWarnings("deprecation")
public class LandingScreen extends AppCompatActivity {
	/** Called when the activity is first created. */
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	SharedPreferences languageSettings;
	String selectedLanguage;
	SharedPreferences encrptionKeys;
	private boolean getPublic = true;
	private AlertDialog.Builder alertbox;
	ValueContainer valueContainer;
	public String responseXml = null;
	ProgressDialog dialog;
	private static ViewPager mPager;
	private static int currentPage = 0;
    private static int NUM_PAGES = 0;
	private ArrayList<String> IMAGES = new ArrayList<String>();
	//private ArrayList<String> ImagesArray = new ArrayList<String>();
	int flag;
	private static final String[] requiredPermissions = new String[] { Manifest.permission.RECEIVE_SMS,
			Manifest.permission.READ_SMS
			/* ETC.. */
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing_screen);

		// Check Permission
		if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
			requestPermissions(new String[] { Manifest.permission.READ_SMS }, 1);
			Log.d("Simobi", "permission requested");
		} else {
			Log.d("Simobi", "permission granted");
		}

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		System.out.println("Testing>>" + selectedLanguage);

		// Image Slider
		new ImageSliderTask().execute();

		/*
		 * RelativeLayout login=(RelativeLayout)findViewById(R.id.login);
		 * RelativeLayout active=(RelativeLayout)findViewById(R.id.active);
		 * RelativeLayout contact=(RelativeLayout)findViewById(R.id.contact_us);
		 * TextView activationText=(TextView)findViewById(R.id.textView2);
		 */
		LinearLayout mlogin = (LinearLayout) findViewById(R.id.mlogin);
		LinearLayout active = (LinearLayout) findViewById(R.id.active);
		//LinearLayout eform = (LinearLayout) findViewById(R.id.eform);

		//eform.setVisibility(View.GONE);

		TextView contact = (TextView) findViewById(R.id.contact);
		contact.setPaintFlags(contact.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
		TextView activationText = (TextView) findViewById(R.id.textView2);
		TextView toc = (TextView) findViewById(R.id.termsandconditions);
		toc.setPaintFlags(toc.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
		//toc.setVisibility(View.GONE);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			System.out.println("Testing1>>" + selectedLanguage);
			activationText.setText(getResources().getString(R.string.eng_activation));

		} else {
			activationText.setText(getResources().getString(R.string.bahasa_activation));

		}

		// Get public key
		if (getPublic == true)
			getPublick();

		toc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LandingScreen.this, TermsAndConditions.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 1);
			}
		});
		
		contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LandingScreen.this, ContactUs.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 1);
			}
		});

		mlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LandingScreen.this, LoginScreen.class);
				if (getIntent().hasExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID)) {
					intent.putExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID,
							getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID));
					intent.putExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK,
							getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK));
				}
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 1);
			}
		});
		
		/**
		eform.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// flag=1;
				Intent intent = new Intent(LandingScreen.this, WebviewActivity.class);
				// intent.putExtra("flag","1");
				Log.e("check_flag_value", flag + "");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		**/
		
		active.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LandingScreen.this, ActivationHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	// Get Public Key
	@SuppressLint("HandlerLeak")
	public void getPublick() {

		/** Set Parameters for service calling. */
		valueContainer = new ValueContainer();
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setTransactionName(Constants.TRANSACTION_GETPUBLICKEY);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, LandingScreen.this);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			dialog = new ProgressDialog(LandingScreen.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.eng_loading));
			dialog.show();
		} else {
			dialog = new ProgressDialog(LandingScreen.this, R.style.MyAlertDialogStyle);
			dialog.setTitle("Bank Sinarmas");
			dialog.setCancelable(false);
			dialog.setMessage(getResources().getString(R.string.bahasa_loading));
			dialog.show();
		}
		alertbox = new AlertDialog.Builder(LandingScreen.this, R.style.MyAlertDialogStyle);
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
				

					if (!responseContainer.getSuccess().equalsIgnoreCase("true")) {
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
						}
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						alertbox.show();

					} else {
						System.out.println(responseContainer.getPublicKeyExponet() + "MODULUS:"
								+ responseContainer.getPublicKeyModulus());
						encrptionKeys.edit().putString("MODULE", responseContainer.getPublicKeyModulus()).commit();
						encrptionKeys.edit().putString("EXPONENT", responseContainer.getPublicKeyExponet()).commit();
					}

				} else {
					dialog.dismiss();
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
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
					responseXml = webServiceHttp.getResponseSSLCertificatation();
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();
	}
	
	public class ImageSliderTask extends AsyncTask<Void, Integer, String> {
		StringBuilder total = new StringBuilder();
		String line;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

		@Override
        protected String doInBackground(Void... params) {
        	try {
        		HttpClient httpclient = new DefaultHttpClient();
    			HttpGet httppost = new HttpGet("http://banksinarmas.com/id/slidersimobi.php");
				HttpResponse response = httpclient.execute(httppost);
    			HttpEntity ht = response.getEntity();
				BufferedHttpEntity buf = new BufferedHttpEntity(ht);
    			InputStream is = buf.getContent();
    			BufferedReader r = new BufferedReader(new InputStreamReader(is));
    			while ((line = r.readLine()) != null) {
    				total.append(line + "\n");
    			}
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	//Log.d("Simobi-Widy-test", "Stringtotal : " + total.toString());
            return total.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] parts = total.toString().split("\\r?\\n");
    		for (int i = 0; i < parts.length; i++){
    			if(!parts[i].trim().isEmpty()){
    				Log.d("Simobi", "string added : " + parts[i].toString());
    				IMAGES.add(parts[i].trim().toString());
    			}
    		}
    		init();
        }
    }

	private void init() {
		mPager = (ViewPager) findViewById(R.id.pager);
		if(IMAGES == null){
			//Log.d("SIMOBI", "Images null!");
		}
		mPager.setAdapter(new ImageSliderAdapter(LandingScreen.this, IMAGES));

		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

		indicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;

		// Set circle indicator radius
		indicator.setRadius(5 * density);

		NUM_PAGES = IMAGES.size();

		// Auto start of viewpager
		final Handler handler = new Handler();
		final Runnable Update = new Runnable() {
			public void run() {
				if (currentPage == NUM_PAGES) {
					currentPage = 0;
				}
				mPager.setCurrentItem(currentPage++, true);
			}
		};
		Timer swipeTimer = new Timer();
		swipeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(Update);
			}
		}, 30000, 30000);

		// Pager listener over indicator
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentPage = position;

			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});
	}

	@SuppressLint("NewApi")
	public boolean hasPermissions(@NonNull String... permissions) {
		for (String permission : permissions)
			if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
				return false;
		return true;
	}

}
