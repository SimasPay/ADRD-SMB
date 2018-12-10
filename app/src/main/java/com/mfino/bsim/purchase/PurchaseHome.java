package com.mfino.bsim.purchase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PurchaseHome extends AppCompatActivity {

	Spinner productCategory, provider, productName;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	ProgressDialog dialog;
	// url to make request
	private static String url;
	// JSON Node names
	private static final String TAG_PROVIDERS = "providers";
	private static final String TAG_PRODUCT_NAME = "productName";
	private static final String TAG_PRODUCT_CODE = "productCode";
	private static final String TAG_PAYMENT_MODE = "paymentMode";
	private static final String TAG_INVOICE_TYPE = "invoiceType";
	private static final String TAG_DENOM = "Denom";
	private static final String TAG_IS_PLNP_REPAID = "isPLNPrepaid";
	File myFile;
	StringBuilder sb = new StringBuilder();
	SharedPreferences purchaseVersion;
	// contacts JSONArray
	String productArray[][][] = null;
	int i = 0, j = 0, k = 0;
	int CategoryLen, providerLen, productLen;
	String sp;
	String selectedCategory, selectedProvider, selectedProductName, selectedProductCode = "Select", selectedPaymentMode,
			selectedInvoiceType, selectedProductDenom;
	List<String> listOfProviders, categoriesList, providersList, productNameList, productCodeList, ProductDenomList,
			paymentModeList, invoiceTypeList;
	List<Product> listOfProducts, readlistOfProducts;
	LinkedHashMap<String, List<String>> providersMapArray = new LinkedHashMap<>();
	LinkedHashMap<String, List<Product>> productsMapArray = new LinkedHashMap<>();
	Button continueButton;
	Context ctx;
	String jSONproductName, jSONproductCode, jSONPaymentMode, jSONInvoiceType;
	String jSONoffLine, jSONDenom;
	private AlertDialog.Builder alertbox;
	SharedPreferences languageSettings;
	String selectedLanguage;
	boolean isPLNPrepaid;
	List<Boolean> isPLNPrepaidList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.purchase_selection);
		System.out.println("Testing>>path>>" + this.getFilesDir());
		myFile = new File(this.getFilesDir() + "/", "purchase.txt");
		// = new File("/sdcard/purchase.txt");
		purchaseVersion = getSharedPreferences("PURCHASE_VERSION", 0);

		alertbox = new AlertDialog.Builder(PurchaseHome.this, R.style.MyAlertDialogStyle);

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = headerContainer.findViewById(R.id.screenTitle);
		screeTitle.setText("PURCHASE");
		ImageButton back = headerContainer.findViewById(R.id.back);
		ImageButton home = headerContainer.findViewById(R.id.home_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PurchaseHome.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		productNameList = new ArrayList<>();
		productCodeList = new ArrayList<>();
		ProductDenomList = new ArrayList<>();
		paymentModeList = new ArrayList<>();
		invoiceTypeList = new ArrayList<>();
		isPLNPrepaidList = new ArrayList<>();
		GetPurchase payment = new GetPurchase();
		payment.execute();
		productCategory = findViewById(R.id.productCategory);
		provider = findViewById(R.id.provider);
		productName = findViewById(R.id.productType);
		continueButton = findViewById(R.id.continue_button);

		TextView category = findViewById(R.id.textView_purchaseCategory);
		TextView provider = findViewById(R.id.textView_purchaseProvider);
		TextView type = findViewById(R.id.textView_purchaseType);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			screeTitle.setText(getResources().getString(R.string.eng_purchase));
			category.setText(getResources().getString(R.string.eng_purchaseCategory));
			provider.setText(getResources().getString(R.string.eng_purchaseProvider));
			type.setText(getResources().getString(R.string.eng_purchaseType));
			continueButton.setText(getResources().getString(R.string.eng_next));

		} else {

			screeTitle.setText(getResources().getString(R.string.bahasa_purchase));
			category.setText(getResources().getString(R.string.bahasa_purchaseCategory));
			provider.setText(getResources().getString(R.string.bahasa_purchaseProvider));
			type.setText(getResources().getString(R.string.bahasa_purchaseType));
			continueButton.setText(getResources().getString(R.string.bahasa_next));
		}

	}

	public void getPayment() {
		try {
			String version = purchaseVersion.getString("VERSION", "-1");
            if (!myFile.exists()) {
                version = "-1";
            }
            System.out.println("Version: " + version);
            JSONParser jParser = new JSONParser();
            url = WebServiceHttp.webAPIUrlFiles
                    + "?category=category.purchase&channelID=7&service=Payment&txnName=GetThirdPartyData&version="
                    + version;
            JSONObject json = new JSONObject(jParser.getJSONFromUrl(url).toString().trim());
            //System.out.println("JSON OBJect" + json);
            System.out.println("URL>>>" + url);
            /*
            Ion.with(this)
                    .load("GET", url)
                    .setHeader("Content-Type", "application/json")
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                            	Log.d(LOG_TAG, "response: "+result);
                                JSONObject json = new JSONObject(result);
                                String message = json.getString("message");
                                System.out.println("Testing>>>>>>>>>>message" + message);
								String verStr = json.getString("version");
								purchaseVersion.edit().putString("VERSION", verStr).apply();
								System.out.println("Testing>>>>>>>>>>verStr" + verStr);
								System.out.println("Testing>>>>>>>>>>Catch");
								// myFile.delete();
								myFile.createNewFile();
								FileOutputStream fOut = new FileOutputStream(myFile);
								OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
								myOutWriter.append(json.toString());
								myOutWriter.close();
								fOut.close();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                e.printStackTrace();
                            } catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
                    });
			*/

			try {
				String message = json.getString("message");
				System.out.println("Testing>>>>>>>>>>message" + message);
				// System.out.println("Testing>>>>>>>>>>verStr"+verStr);
			} catch (Exception e) {
				// Download file and store in local file
				String verStr = json.getString("version");
				purchaseVersion.edit().putString("VERSION", verStr).apply();
				System.out.println("Testing>>>>>>>>>>verStr" + verStr);
				System.out.println("Testing>>>>>>>>>>Catch");
				// myFile.delete();
				myFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(json.toString());
				myOutWriter.close();
				fOut.close();
				e.printStackTrace();
			}

			System.out.println("Testing" + myFile.length());
			System.out.println("Testing" + myFile.getAbsolutePath());
			FileInputStream in = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(in));
			String aDataRow = "";
			while ((aDataRow = myReader.readLine()) != null) {

				sb = sb.append(aDataRow);
				System.out.println("Testing>>File>>" + sb);
			}
			in.close();

			JSONObject obj1 = new JSONObject(sb.toString());
			JSONArray ar1 = obj1.getJSONArray("purchaseData");
			//HashMap<String, JSONArray> m1 = new HashMap<String, JSONArray>();

			//JSONArray providersJSONArray = null;

			for (int i = 0; i < ar1.length(); i++) {
				JSONObject temp = ar1.getJSONObject(i);
				String productCategory = temp.getString("productCategory");
				listOfProviders = new ArrayList<String>();

				JSONArray tempArr = temp.getJSONArray(TAG_PROVIDERS);
				for (int j = 0; j < tempArr.length(); j++) {

					JSONObject providerObject = tempArr.getJSONObject(j);
					String providerName = providerObject.getString("providerName");
					listOfProducts = new ArrayList<>();
					JSONArray prodArr = providerObject.getJSONArray("products");

					for (int k = 0; k < prodArr.length(); k++) {

						JSONObject jsonProd = prodArr.getJSONObject(k);
						jSONproductName = jsonProd.getString(TAG_PRODUCT_NAME);
						jSONproductCode = jsonProd.getString(TAG_PRODUCT_CODE);
						jSONPaymentMode = jsonProd.getString(TAG_PAYMENT_MODE);
						jSONInvoiceType = jsonProd.getString(TAG_INVOICE_TYPE);
						try {
							isPLNPrepaid = Boolean.parseBoolean(jsonProd.getString(TAG_IS_PLNP_REPAID));
						} catch (Exception e) {
							isPLNPrepaid = false;
						}

						try {

							jSONDenom = jsonProd.getString(TAG_DENOM);
							// jSONoffLine =
							// jsonProd.getString(TAG_OFFLINE).toString();

						} catch (Exception e) {
							// jSONoffLine="false";
							jSONDenom = "NONE";
							e.printStackTrace();
						}

						Product p = new Product(jSONproductName, jSONproductCode, jSONDenom, jSONPaymentMode,
								jSONInvoiceType, isPLNPrepaid);
						listOfProducts.add(p);
					}
					productsMapArray.put(productCategory + "-" + providerName, listOfProducts);
					listOfProviders.add(providerName);
				}
				providersMapArray.put(productCategory, listOfProviders);
				// m1.put(productCategory, providersJSONArray);
				// break;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class GetPurchase extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			// Language Option..
			languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
			selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

			dialog = new ProgressDialog(PurchaseHome.this);
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

			categoriesList = new ArrayList<String>();
			List<String> categories = new ArrayList<String>(providersMapArray.keySet());
			categoriesList.add("Select");
			for (int i = 0; i < categories.size(); i++) {
				Object obj = categories.get(i);
				categoriesList.add(categories.get(i));
				System.out.println("Keys:>>>" + obj);
			}

			providersList = new ArrayList<String>();

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseHome.this, R.layout.spinner_row,
					categoriesList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			productCategory.setAdapter(dataAdapter);
			productCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					selectedCategory = categoriesList.get(arg2);
					if (selectedCategory.equalsIgnoreCase("Select")) {
						providersList.clear();
						productNameList.clear();
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseHome.this,
								R.layout.spinner_row, providersList);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						provider.setAdapter(dataAdapter);

						ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(PurchaseHome.this,
								R.layout.spinner_row, productNameList);
						dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						productName.setAdapter(dataAdapter1);

					} else {
						List<String> provid = providersMapArray.get(selectedCategory);
						providersList.clear();
						providersList.add("Select");
						for (int i = 0; i < provid.size(); i++) {
							Object obj = provid.get(i);
							providersList.add(provid.get(i));
							System.out.println("Keys:>>>" + obj);
						}

						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseHome.this,
								R.layout.spinner_row, providersList);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						provider.setAdapter(dataAdapter);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			provider.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					selectedProvider = providersList.get(arg2);
					if (selectedProvider.equalsIgnoreCase("Select")) {
						productNameList.clear();
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseHome.this,
								R.layout.spinner_row, productNameList);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						productName.setAdapter(dataAdapter);

					} else {
						readlistOfProducts = productsMapArray.get(selectedCategory + "-" + selectedProvider);
						clearArralyList();

						for (Product prod : readlistOfProducts) {

							System.out.println("*********");

							productNameList.add(prod.getName());
							productCodeList.add(prod.getCode());
							ProductDenomList.add(prod.getDenom());
							paymentModeList.add(prod.getPaymentMode());
							invoiceTypeList.add(prod.getInvoiceType());
							isPLNPrepaidList.add(prod.isCCPayment());
							System.out.println(prod.getDenom());
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseHome.this,
								R.layout.spinner_row, productNameList);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						productName.setAdapter(dataAdapter);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			productName.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					selectedProductName = productNameList.get(arg2);
					selectedProductCode = productCodeList.get(arg2);
					selectedProductDenom = ProductDenomList.get(arg2);
					selectedPaymentMode = paymentModeList.get(arg2);
					selectedInvoiceType = invoiceTypeList.get(arg2);
					isPLNPrepaid = isPLNPrepaidList.get(arg2);
					System.out.println("******** Name>>>*" + selectedProductName);
					System.out.println("******** Code>>>*" + selectedProductCode);
					System.out.println("******** Denom>>>*" + selectedProductDenom);
					System.out.println("******** mode>>>*" + selectedPaymentMode);
					System.out.println("******** invoice>>>*" + selectedInvoiceType);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			continueButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (selectedProductCode.equalsIgnoreCase("Select") || selectedProvider.equalsIgnoreCase("Select")) {
						alertbox.setMessage(" Please Select Provider or product type ");
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						alertbox.show();

					} else {

						Intent in = new Intent(PurchaseHome.this, PurchaseDetails.class);
						in.putExtra("PRODUCT_CODE", selectedProductCode);
						in.putExtra("PRODUCT_DENOM", selectedProductDenom);
						in.putExtra("SELECTED_CATEGORY", selectedCategory);
						in.putExtra("SELECTED_PAYMENT_MODE", selectedPaymentMode);
						in.putExtra("SELECTED_INVOICETYPE", selectedInvoiceType);
						in.putExtra("IS_PLN_PREPAID", isPLNPrepaid);
						System.out.println("Test>>>" + selectedProductCode + ">>" + selectedProductDenom);
						startActivity(in);

					}
				}
			});
		}
	}

	private void clearArralyList() {
		// TODO Auto-generated method stub
		productNameList.clear();
		productCodeList.clear();
		ProductDenomList.clear();
		paymentModeList.clear();
		invoiceTypeList.clear();
		isPLNPrepaidList.clear();
		productNameList.add("Select");
		productCodeList.add("Select");
		ProductDenomList.add("Select");
		paymentModeList.add("Select");
		invoiceTypeList.add("Select");
		isPLNPrepaidList.add(false);

	}

}
