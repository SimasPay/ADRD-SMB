package com.mfino.bsim.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String strURL) {

		// Making HTTP request
		/*try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		HttpURLConnection conn;
        try{
			URL url = new URL(strURL);
	        if(strURL.startsWith("https")){
	            conn = (HttpsURLConnection) url.openConnection();
	        }else{
	            conn = (HttpURLConnection) url.openConnection();
	        }
	        conn.setReadTimeout(30000);
	        conn.setConnectTimeout(30000);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setRequestProperty("Accept", "application/json");
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        System.out.println("\nSending POST request to URL : " + url);
	        
	        is = conn.getInputStream();
		} catch (SocketTimeoutException ste) {
			ste.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		Log.e("file", "JSON Data " + jObj);
		// return JSON String
		return jObj;

	}
}
