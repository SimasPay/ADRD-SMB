package com.mfino.bsim.transfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.JSONParser;
import com.mfino.bsim.services.WebServiceHttp;

public class OtherBankList extends Activity {

	// url to make request
	private static String url;
	// JSON Node names
	private static final String TAG_BANKDATA = "bankData";
	private static final String TAG_NAME = "name";
	private static final String TAG_CODE = "code";
	ProgressDialog dialog;
	ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	SharedPreferences languageSettings;

	// contacts JSONArray
	JSONArray banksListJSON = null;
	ListView lv;
	String sp;
	SharedPreferences bankListVersion;
	File myFile;
	StringBuilder sb = new StringBuilder();
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bankslist);
		context = this;

		myFile = new File(this.getFilesDir() + "/", "banklist.txt");
		bankListVersion = getSharedPreferences("LOGIN_PREFERECES", 0);

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(OtherBankList.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_bankList));
			/*
			 * home.setBackgroundResource(R.drawable.home_icon1);
			 * back.setBackgroundResource(R.drawable.back_button);
			 */
		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_bankList));
			/*
			 * home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			 * back.setBackgroundResource(R.drawable.bahasa_back_button);
			 */
		}

		lv = (ListView) findViewById(R.id.bankList);
		boolean networkCheck = ConfigurationUtil.isConnectingToInternet(context);
		if (!networkCheck) {
			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond),
						context);
			} else {
				ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond),
						context);
			}

		} else {
			GetBankLists getBankList = new GetBankLists();
			getBankList.execute();
		}

	}

	public void bankList() {

		try {
			String version = bankListVersion.getString("VERSION", "-1");
			System.out.println("Verion" + version);
			JSONParser jParser = new JSONParser();
			url = WebServiceHttp.webAPIUrlFiles
					+ "?category=category.bankCodes&channelID=7&service=Payment&txnName=GetThirdPartyData&version="
					+ version;
			JSONObject json = jParser.getJSONFromUrl(url);
			System.out.println("JSON OBJect" + json);
			System.out.println("URL>>>" + url);
			try {
				String message = json.getString("message");
			} catch (Exception e) {

				String verStr = json.getString("version");
				bankListVersion.edit().putString("VERSION", verStr).commit();
				myFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(json.toString());
				myOutWriter.close();
				fOut.close();
				e.printStackTrace();
			}

			FileInputStream in = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(in));
			String aDataRow = "";
			while ((aDataRow = myReader.readLine()) != null) {
				sb = sb.append(aDataRow);
			}
			in.close();

			JSONObject json1 = new JSONObject(sb.toString());
			banksListJSON = json1.getJSONArray(TAG_BANKDATA);
			System.out.println("Length" + banksListJSON.length());
			for (int i = 0; i < banksListJSON.length(); i++) {
				JSONObject c = banksListJSON.getJSONObject(i);
				String name = c.getString(TAG_NAME);
				String code = c.getString(TAG_CODE);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TAG_NAME, name);
				map.put(TAG_CODE, code);
				contactList.add(map);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class GetBankLists extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(OtherBankList.this);
			dialog.setCancelable(false);
			dialog.setMessage("Loading ...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			bankList();
			dialog.cancel();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dialog.cancel();
			ListAdapter adapter = new SimpleAdapter(OtherBankList.this, contactList, R.layout.list_item,
					new String[] { TAG_NAME, TAG_CODE }, new int[] { R.id.name, R.id.email });

			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
					String code = ((TextView) view.findViewById(R.id.email)).getText().toString();
					Intent in = new Intent(getApplicationContext(), ToOtherBankDetails.class);
					in.putExtra(TAG_NAME, name);
					in.putExtra(TAG_CODE, code);
					startActivity(in);

				}
			});

		}
	}

}