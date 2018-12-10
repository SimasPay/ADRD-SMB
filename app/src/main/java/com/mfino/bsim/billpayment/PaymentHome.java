package com.mfino.bsim.billpayment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mfino.bsim.R;
import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.services.JSONParser;
import com.mfino.bsim.services.Product;
import com.mfino.bsim.services.WebServiceHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.mfino.bsim.services.Constants.LOG_TAG;

public class PaymentHome extends AppCompatActivity {

	Spinner productCategory, provider_sp, productName;
	LinearLayout spin2, spin3;
	//ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	ProgressDialog dialog;
	// JSON Node names
	private static final String TAG_PAYMENT_DATA = "paymentData";
	private static final String TAG_PRODUCT_CATEGORY = "productCategory";
	private static final String TAG_PROVIDERS = "providers";
	private static final String TAG_PROVIDER_NAME = "providerName";
	private static final String TAG_PRODUCT_NAME = "productName";
	private static final String TAG_PRODUCT_CODE = "productCode";
	private static final String TAG_PAYMENT_MODE = "paymentMode";
	private static final String TAG_INVOICE_TYPE = "invoiceType";
	private static final String TAG_IS_CCPAYMENT = "isCCPayment";
	private static final String TAG_PRODUCTS = "products";
	// private static final String TAG_OFFLINE = "offline";
	// private static final String TAG_SUCCESS = "success";
	// private static final String TAG_TOTAL_COUNT = "totalCount";
	// contacts JSONArray
	//String productArray[][][] = null;
	//int i = 0, j = 0, k = 0;
	//int CategoryLen, providerLen, productLen;
	String selectedCategory, selectedProvider, productCode = "Select", selectedPaymentMode, selectedInvoiceType,
			selectedProductDenom;
	List<String> listOfProviders, categoriesList, providersList, ProductDenomList, productNameList, productCodeList,
			paymentModeList, inVoiceModeList;
	List<Boolean> ccPayment;
	List<Product> listOfProducts, readlistOfProducts;
	LinkedHashMap<String, List<String>> providersMapArray = new LinkedHashMap<>();
	LinkedHashMap<String, List<Product>> productsMapArray = new LinkedHashMap<>();
	Button continueButton;
	File myFile;
	String jSONproductName, jSONproductCode, jSONPaymentMode, jSONInvoiceType;
	boolean isCCPayment;
	//String jSONoffLine;
	StringBuilder sb = new StringBuilder();

	SharedPreferences paymentVersion;
	private AlertDialog.Builder alertbox;
	SharedPreferences languageSettings;
	String selectedLanguage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		productNameList = new ArrayList<>();
		productCodeList = new ArrayList<>();
		paymentModeList = new ArrayList<>();
		inVoiceModeList = new ArrayList<>();
		ProductDenomList = new ArrayList<>();
		ccPayment = new ArrayList<>();
		setContentView(R.layout.payment_selection);
		System.out.println("Testing>>path>>" + this.getFilesDir());
		myFile = new File(this.getFilesDir() + "/", "payment.txt");
		paymentVersion = getSharedPreferences("PAYMENT_VERSION", 0);

		alertbox = new AlertDialog.Builder(PaymentHome.this, R.style.MyAlertDialogStyle);

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = headerContainer.findViewById(R.id.screenTitle);
		screeTitle.setText(getResources().getText(R.string.eng_payment));
		ImageButton back = headerContainer.findViewById(R.id.back);
		ImageButton home = headerContainer.findViewById(R.id.home_button);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PaymentHome.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		selectedCategory = "Select";
		GetPayment payment = new GetPayment();
		payment.execute();

		productCategory = findViewById(R.id.productCategory);
		provider_sp = findViewById(R.id.provider);
		spin2 = findViewById(R.id.lt_spin2);
		spin3 = findViewById(R.id.lt_spin3);
		productName = findViewById(R.id.productType);
		continueButton = findViewById(R.id.continue_button);
		TextView category = findViewById(R.id.textView_paymentCategory);
		TextView provider = findViewById(R.id.textView_paymentProvider);
		TextView type = findViewById(R.id.textView_paymentType);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_payment));
			category.setText(getResources().getString(R.string.eng_paymentCategory));
			provider.setText(getResources().getString(R.string.eng_paymentProvider));
			type.setText(getResources().getString(R.string.eng_paymentType));
			continueButton.setText(getResources().getString(R.string.eng_next));
		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_payment));
			category.setText(getResources().getString(R.string.bahasa_paymentCategory));
			provider.setText(getResources().getString(R.string.bahasa_paymentProvider));
			type.setText(getResources().getString(R.string.bahasa_paymentType));
			continueButton.setText(getResources().getString(R.string.bahasa_next));
		}

		productCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedCategory = categoriesList.get(arg2);
				if (selectedCategory.equalsIgnoreCase("Select")) {
					Log.e(LOG_TAG, "SelectedCategory: " + selectedCategory);
				} else {
					List<String> provid = providersMapArray.get(selectedCategory);
					providersList.clear();
					providersList.add("Select");
					//Object obj = provid.get(i);
					providersList.addAll(provid);
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							providersList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					provider_sp.setAdapter(dataAdapter);
				}
				if (selectedCategory.equalsIgnoreCase("Select")) {
					providersList.clear();
					productNameList.clear();
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							providersList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					provider_sp.setAdapter(dataAdapter);

					ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							productNameList);
					dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					productName.setAdapter(dataAdapter1);
					provider_sp.setEnabled(false);
					provider_sp.setFocusable(false);
					provider_sp.setFocusableInTouchMode(false);
					provider_sp.setClickable(false);
					productName.setEnabled(false);
					productName.setFocusable(false);
					productName.setFocusableInTouchMode(false);
					productName.setClickable(false);

				} else {

					List<String> provid = providersMapArray.get(selectedCategory);
					providersList.clear();
					providersList.add("Select");
					//Object obj = provid.get(i);
					providersList.addAll(provid);
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							providersList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					provider_sp.setAdapter(dataAdapter);
					provider_sp.setEnabled(true);
					provider_sp.setFocusable(true);
					provider_sp.setFocusableInTouchMode(true);
					provider_sp.setClickable(true);
					productName.setEnabled(true);
					productName.setFocusable(true);
					productName.setFocusableInTouchMode(true);
					productName.setClickable(true);

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		provider_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("================", "---------Ramya---------" + selectedCategory);
				selectedProvider = providersList.get(arg2);
				if (selectedProvider.equalsIgnoreCase("Select")) {
					productName.setEnabled(false);
					productName.setFocusable(false);
					productName.setFocusableInTouchMode(false);
					productName.setClickable(false);
					productNameList.clear();
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							productNameList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				} else {
					// provider.setEnabled(true);
					// provider.setFocusable(true);
					readlistOfProducts = productsMapArray.get(selectedCategory + "-" + selectedProvider);
					clearArralyList();
					for (Product prod : readlistOfProducts) {
						System.out.println("******** NAME>>>*" + prod.getName());
						productNameList.add(prod.getName());
						productCodeList.add(prod.getCode());
						paymentModeList.add(prod.getPaymentMode());
						inVoiceModeList.add(prod.getInvoiceType());
						ProductDenomList.add(prod.getDenom());
						ccPayment.add(prod.isCCPayment());
						// productOffLineList.add(prod.getOffline());
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
							productNameList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					productName.setAdapter(dataAdapter);
					productName.setEnabled(true);
					productName.setFocusable(true);
					productName.setFocusableInTouchMode(true);
					productName.setClickable(true);

				}
				// }

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		productName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("sizeee", providersList.size() + "-----" + arg2);
				Log.e("testtttttttt", productNameList.toString());
				Log.e("providersList", providersList.toString());
				// providersList
				selectedProvider = productNameList.get(arg2);
				/*
				 * if (selectedProvider.equalsIgnoreCase("Select")) {
				 * 
				 * 
				 * 
				 * }else{
				 */

				productCode = productCodeList.get(arg2);
				selectedPaymentMode = paymentModeList.get(arg2);
				selectedInvoiceType = inVoiceModeList.get(arg2);
				selectedProductDenom = ProductDenomList.get(arg2);
				isCCPayment = ccPayment.get(arg2);
				System.out.println("******** Testing IsCCPayment>>>*" + isCCPayment);
				System.out.println("******** Name>>>*" + productNameList.get(arg2));
				System.out.println("******** Code>>>*" + productCode);
				System.out.println(
						selectedProductDenom + "******** Mode>>>*" + selectedPaymentMode + selectedInvoiceType);
				// }

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		continueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productCode.equalsIgnoreCase("Select") || selectedProvider.equalsIgnoreCase("Select")) {
					alertbox.setMessage(" Please Select Provider or product type  ");
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
					alertbox.show();

				} else {
					Intent in = new Intent(PaymentHome.this, PaymentDetails.class);
					in.putExtra("PRODUCT_CODE", productCode);
					in.putExtra("SELECTED_CATEGORY", selectedCategory);
					in.putExtra("SELECTED_PAYMENT_MODE", selectedPaymentMode);
					in.putExtra("SELECTED_INVOICETYPE", selectedInvoiceType);
					in.putExtra("PRODUCT_DENOM", selectedProductDenom);
					in.putExtra("IS_CCPAYMENT", isCCPayment);
					in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
				}

			}
		});

	}

	public void getPayment() {
		try {
			String version = paymentVersion.getString("VERSION", "-1");
			// version = "-1";
			if (!myFile.exists()) {
				version = "-1";
			}
			System.out.println("Version: " + version);
			JSONParser jParser = new JSONParser();
			// private AlertDialog.Builder alertbox;
			// url to make request
			//String url2 = "https://www.banksinarmas.com/webapi/dynamic?category=category.payments&channelID=7&service=Payment&txnName=GetThirdPartyData&version=1";
			String url = WebServiceHttp.webAPIUrlFiles + "?category=category.payments&channelID=7&service=Payment&txnName=GetThirdPartyData&version=" + 1+"&addedby=widy";
			JSONObject json = jParser.getJSONFromUrl(url);
			System.out.println("JSON OBJect" + json);
			Log.e("jsonobject_________", json + "");
			Log.e("URL_________", url + "");
			System.out.println("URL>>>" + url);

			try {
				@SuppressWarnings("unused")
				String message = json.getString("message");
			} catch (Exception e) {
				String verStr = json.getString("version");
				paymentVersion.edit().putString("VERSION", verStr).apply();
				boolean success = myFile.createNewFile();
				if(success){
					Log.d(LOG_TAG, "file created");
				}else{
					Log.d(LOG_TAG, "file create failed");
				}
				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(json.toString());
				myOutWriter.close();
				fOut.close();
				e.printStackTrace();
			}

			FileInputStream in = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(in));
			String aDataRow;
			while ((aDataRow = myReader.readLine()) != null) {

				sb = sb.append(aDataRow);
			}
			in.close();
			String jsonString = sb.toString();
			JSONObject obj1 = new JSONObject(jsonString);
			JSONArray ar1 = obj1.getJSONArray(TAG_PAYMENT_DATA);
			//HashMap<String, JSONArray> m1 = new HashMap<String, JSONArray>();

			//JSONArray providersJSONArray = null;

			for (int i = 0; i < ar1.length(); i++) {
				JSONObject temp = ar1.getJSONObject(i);
				String productCategory = temp.getString(TAG_PRODUCT_CATEGORY);
				System.out.println("Testing>>>" + productCategory);
				listOfProviders = new ArrayList<>();

				JSONArray tempArr = temp.getJSONArray(TAG_PROVIDERS);
				for (int j = 0; j < tempArr.length(); j++) {

					JSONObject providerObject = tempArr.getJSONObject(j);
					String providerName = providerObject.getString(TAG_PROVIDER_NAME);
					listOfProducts = new ArrayList<>();
					JSONArray prodArr = providerObject.getJSONArray(TAG_PRODUCTS);

					for (int k = 0; k < prodArr.length(); k++) {

						JSONObject jsonProd = prodArr.getJSONObject(k);
						jSONproductName = jsonProd.getString(TAG_PRODUCT_NAME);
						jSONproductCode = jsonProd.getString(TAG_PRODUCT_CODE);
						jSONPaymentMode = jsonProd.getString(TAG_PAYMENT_MODE);
						jSONInvoiceType = jsonProd.getString(TAG_INVOICE_TYPE);
						jSONInvoiceType = jsonProd.getString(TAG_INVOICE_TYPE);
						try {
							isCCPayment = Boolean.parseBoolean(jsonProd.getString(TAG_IS_CCPAYMENT));
						} catch (Exception e) {
							isCCPayment = false;
						}
						System.out.println("Testing>>>Is CCPayment1>>" + isCCPayment);
						Product p = new Product(jSONproductName, jSONproductCode, null, jSONPaymentMode,
								jSONInvoiceType, isCCPayment);

						listOfProducts.add(p);
					}

					productsMapArray.put(productCategory + "-" + providerName, listOfProducts);
					listOfProviders.add(providerName);
				}
				System.out.println("Test>>>>>categories>>" + productCategory);
				Log.e("productCategory", productCategory);
				Log.e("listOfProviders", listOfProviders + "");
				providersMapArray.put(productCategory, listOfProviders);
			}

			String selProductCat = "Public";

			providersList = providersMapArray.get(selProductCat);

			String selProviderName = "PLN";
			readlistOfProducts = productsMapArray.get(selProductCat + "-" + selProviderName);
			System.out.println(selProductCat + "-" + selProviderName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Testing>> JSONException>>>>>>>>>>>>");
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("StaticFieldLeak")
	private class GetPayment extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			// Language Option..
			languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
			selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

			dialog = new ProgressDialog(PaymentHome.this);
			dialog.setCancelable(false);
			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				dialog.setMessage(getResources().getString(R.string.eng_loading));
			} else {
				dialog.setMessage(getResources().getString(R.string.bahasa_loading));
			}
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			getPayment();
			dialog.cancel();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.cancel();

			categoriesList = new ArrayList<>();
			//Vector<String> categories = new Vector<String>(providersMapArray.keySet());
			categoriesList.add("Select");
			for (String key : providersMapArray.keySet()) {
				// String key = entry.getKey();
				categoriesList.add(key);
				System.out.println("Keys:>>>" + key);
				// String value = entry.getValue();
				// do stuff

			}
			setDataToSpinners();
			/*
			 * for (int i = 0; i < categories.size(); i++) {
			 * 
			 * Object obj = categories.get(i);
			 * categoriesList.add(categories.get(i));
			 * System.out.println("Keys:>>>" + obj); }
			 */
			// -------------------------RAMYA----------------------//

			/*
			 * providersList = new ArrayList<String>(); ArrayAdapter<String>
			 * dataAdapter = new ArrayAdapter<String>(PaymentHome.this,
			 * R.layout.spinner_row, categoriesList);
			 * dataAdapter.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item);
			 * productCategory.setAdapter(dataAdapter);
			 * 
			 * productCategory.setOnItemSelectedListener(new
			 * OnItemSelectedListener() {
			 * 
			 * @Override public void onItemSelected(AdapterView<?> arg0, View
			 * arg1, int arg2, long arg3) { selectedCategory =
			 * categoriesList.get(arg2); if
			 * (selectedCategory.equalsIgnoreCase("Select")) {
			 * providersList.clear(); productNameList.clear();
			 * ArrayAdapter<String> dataAdapter = new
			 * ArrayAdapter<String>(PaymentHome.this,
			 * R.layout.spinner_row,providersList);
			 * dataAdapter.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item); provider.setAdapter(dataAdapter);
			 * ArrayAdapter<String> dataAdapter1 = new
			 * ArrayAdapter<String>(PaymentHome.this,
			 * R.layout.spinner_row,productNameList);
			 * dataAdapter1.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item);
			 * productName.setAdapter(dataAdapter1); } else {
			 * 
			 * List<String> provid = providersMapArray.get(selectedCategory);
			 * providersList.clear(); providersList.add("Select"); for (int i =
			 * 0; i < provid.size(); i++) { Object obj = provid.get(i);
			 * providersList.add(provid.get(i)); } ArrayAdapter<String>
			 * dataAdapter = new ArrayAdapter<String>(PaymentHome.this,
			 * R.layout.spinner_row,providersList);
			 * dataAdapter.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item); provider.setAdapter(dataAdapter);
			 * } }
			 * 
			 * @Override public void onNothingSelected(AdapterView<?> arg0) { }
			 * }); provider.setOnItemSelectedListener(new
			 * OnItemSelectedListener() {
			 * 
			 * @Override public void onItemSelected(AdapterView<?> arg0, View
			 * arg1, int arg2, long arg3) { Log.e("================",
			 * "---------Ramya---------"+selectedCategory);
			 * if(selectedCategory.equalsIgnoreCase("Select")){
			 * alertbox.setMessage(" Please Select Provider or product type  ");
			 * alertbox.setNeutralButton("OK", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { } }); alertbox.show();
			 * } else { selectedProvider = providersList.get(arg2); if
			 * (selectedProvider.equalsIgnoreCase("Select")) { //
			 * provider.setEnabled(false); // provider.setFocusable(false);
			 * productNameList.clear(); ArrayAdapter<String> dataAdapter = new
			 * ArrayAdapter<String>( PaymentHome.this, R.layout.spinner_row,
			 * productNameList); dataAdapter
			 * .setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item);
			 * productName.setAdapter(dataAdapter); } else { //
			 * provider.setEnabled(true); // provider.setFocusable(true);
			 * readlistOfProducts = productsMapArray .get(selectedCategory + "-"
			 * + selectedProvider); clearArralyList(); for (Product prod :
			 * readlistOfProducts) { System.out.println("******** NAME>>>*" +
			 * prod.getName()); productNameList.add(prod.getName());
			 * productCodeList.add(prod.getCode());
			 * paymentModeList.add(prod.getPaymentMode());
			 * inVoiceModeList.add(prod.getInvoiceType());
			 * ProductDenomList.add(prod.getDenom());
			 * ccPayment.add(prod.isCCPayment()); //
			 * productOffLineList.add(prod.getOffline()); } ArrayAdapter<String>
			 * dataAdapter = new ArrayAdapter<String>( PaymentHome.this,
			 * R.layout.spinner_row, productNameList); dataAdapter
			 * .setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item);
			 * productName.setAdapter(dataAdapter); } } }
			 * 
			 * @Override public void onNothingSelected(AdapterView<?> arg0) { }
			 * }); productName.setOnItemSelectedListener(new
			 * OnItemSelectedListener() {
			 * 
			 * @Override public void onItemSelected(AdapterView<?> arg0, View
			 * arg1, int arg2, long arg3) { productCode =
			 * productCodeList.get(arg2); selectedPaymentMode =
			 * paymentModeList.get(arg2); selectedInvoiceType =
			 * inVoiceModeList.get(arg2);
			 * selectedProductDenom=ProductDenomList.get(arg2);
			 * isCCPayment=ccPayment.get(arg2); System.out.println(
			 * "******** Testing IsCCPayment>>>*"+ isCCPayment);
			 * System.out.println("******** Name>>>*"+
			 * productNameList.get(arg2)); System.out.println(
			 * "******** Code>>>*" + productCode);
			 * System.out.println(selectedProductDenom+"******** Mode>>>*" +
			 * selectedPaymentMode+selectedInvoiceType); }
			 * 
			 * @Override public void onNothingSelected(AdapterView<?> arg0) { }
			 * }); continueButton.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View arg0) { if
			 * (productCode.equalsIgnoreCase("Select")||
			 * selectedProvider.equalsIgnoreCase("Select")) {
			 * alertbox.setMessage(" Please Select Provider or product type  ");
			 * alertbox.setNeutralButton("OK", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { } }); alertbox.show();
			 * } else { Intent in = new Intent(PaymentHome.this,
			 * PaymentDetails.class); in.putExtra("PRODUCT_CODE", productCode);
			 * in.putExtra("SELECTED_CATEGORY", selectedCategory);
			 * in.putExtra("SELECTED_PAYMENT_MODE", selectedPaymentMode);
			 * in.putExtra("SELECTED_INVOICETYPE", selectedInvoiceType);
			 * in.putExtra("PRODUCT_DENOM", selectedProductDenom);
			 * in.putExtra("IS_CCPAYMENT", isCCPayment);
			 * in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(in); }
			 * } });
			 * 
			 * --------------------------------RAMYA----------------------------
			 * ---------------------------------
			 * 
			 */
		}
	}

	public void setDataToSpinners() {
		providersList = new ArrayList<>();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PaymentHome.this, R.layout.spinner_row,
				categoriesList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		productCategory.setAdapter(dataAdapter);
	}

	private void clearArralyList() {
		productNameList.clear();
		productCodeList.clear();
		paymentModeList.clear();
		inVoiceModeList.clear();
		ProductDenomList.clear();
		ccPayment.clear();
		productNameList.add("Select");
		productCodeList.add("Select");
		paymentModeList.add("Select");
		inVoiceModeList.add("Select");
		ProductDenomList.add("Select");
		ccPayment.add(false);
	}

}