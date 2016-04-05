package com.dimo.PayByQR.QrStore.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.AddressStore;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.PickupMethodData;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;

import java.util.ArrayList;

import static com.dimo.PayByQR.QrStore.constans.QrStoreDefine.LAZIES_PADD;

/**
 * Created by Rhio on 31/03/16.
 */
public class StoreCheckout2 extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle, txtSubtitle;
    private SharedPreferences sharedPreferences;
    private LinearLayout layoutPickupMethodChoices, layoutPickupMethodAddress, layoutPickupMethodStore;
    private TextView txtPickupMethodAddress, txtPickupMethodStore, txtPickupMethodDefault, txtShippingFee, txtStoreAddress;
    private EditText editAddress;
    private AppCompatSpinner spinnerCity, spinnerStore;
    private RelativeLayout layoutShippingFee, layoutStoreAddress;
    private DIMOButton btnLanjut;

    private String MerchantCode, MerchantName, pickupContentID, pickupContentName, selectedPickupMethodID, selectedPickupMethodName;
    private CartData cartData;
    private int shippingFee, paidAmount;
    private ArrayList<PickupMethodData> pickupMethodList;

    public static final String PICKUP_METHODE_STORE = "1";
    public static final String PICKUP_METHODE_ADDR = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_checkout_2);

        MerchantCode = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID);
        MerchantName = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        txtSubtitle = (TextView) findViewById(R.id.header_bar_subtitle);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);

        txtTitle.setText(getString(R.string.text_header_title_checkout_2));
        txtSubtitle.setText(getString(R.string.text_header_subtitle_checkout, "2"));
        txtSubtitle.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutPickupMethodChoices = (LinearLayout) findViewById(R.id.checkout_pickup_method_layout_choices);
        layoutPickupMethodAddress = (LinearLayout) findViewById(R.id.checkout_pickup_method_address);
        layoutPickupMethodStore = (LinearLayout) findViewById(R.id.checkout_pickup_method_store);
        txtPickupMethodAddress = (TextView) findViewById(R.id.checkout_pickup_method_1);
        txtPickupMethodStore = (TextView) findViewById(R.id.checkout_pickup_method_2);
        txtPickupMethodDefault = (TextView) findViewById(R.id.checkout_pickup_method_layout_single);
        txtShippingFee = (TextView) findViewById(R.id.activity_store_confirm_shipping_fee);
        txtStoreAddress = (TextView) findViewById(R.id.checkout_pickup_method_store_address);
        editAddress = (EditText) findViewById(R.id.activity_store_confirm_shipping_addr);
        spinnerCity = (AppCompatSpinner) findViewById(R.id.activity_store_kota_spinner);
        spinnerStore = (AppCompatSpinner) findViewById(R.id.activity_store_toko_spinner);
        layoutShippingFee = (RelativeLayout) findViewById(R.id.checkout_pickup_method_address_shippingfee_layout);
        layoutStoreAddress = (RelativeLayout) findViewById(R.id.checkout_pickup_method_store_address_layout);
        btnLanjut = (DIMOButton) findViewById(R.id.activity_store_btn_lanjut);

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        cartData = QRStoreDBUtil.getCartsByMerchant(this, MerchantCode);
        cartData.printLogData();

        shippingFee = 0;
        paidAmount = cartData.totalAmount + shippingFee;

        txtShippingFee.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(shippingFee)));

        new GetPickupMethodTask(cartData.merchantURL, cartData.merchantCode).execute();
    }

    private void initPickupMethod(){
        // TODO: 4/1/16 init pickup method option
        /*String[] arrayPickupMethod = new String[pickupMethodList.size()];
        for(int i=0;i<pickupMethodList.size();i++){
            arrayPickupMethod[i] = pickupMethodList.get(i).name;
        }
        ArrayAdapter<String> pickupMethodAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, arrayPickupMethod);
        pickupMethodAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerPickupMethod.setAdapter(pickupMethodAdapter);
        spinnerPickupMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //id 1 -> TOKO ; id 2 -> ALAMAT
                if (pickupMethodList.get(position).id.equals(PICKUP_METHODE_STORE)) {
                    relativeLayoutStore.setVisibility(View.VISIBLE);
                    relativeLayoutCity.setVisibility(View.GONE);
                    relativeLayoutAddress.setVisibility(View.GONE);
                    txtAddressToko.setVisibility(View.GONE);
                    teAlamat.setText("");
                    txtAddressToko.setText("");
                    shippingFee = 0;
                    paidAmount = cartData.totalAmount + shippingFee;
                    upDateTotalAfterShipping();
                    if (null != arStoreList && arStoreList.length > 0)
                        spinnerStore.setSelection(0);
                } else {
                    relativeLayoutStore.setVisibility(View.GONE);
                    relativeLayoutCity.setVisibility(View.VISIBLE);
                    relativeLayoutAddress.setVisibility(View.VISIBLE);
                    txtAddressToko.setVisibility(View.GONE);
                    teAlamat.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_ADDR, ""));
                    txtAddressToko.setText("");
                    spinnerCity.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });*/

        if(pickupMethodList.size()==1){
            //default pickup method
            PickupMethodData pickupMethodData = pickupMethodList.get(0);
            txtPickupMethodDefault.setText(pickupMethodData.name);
            txtPickupMethodDefault.setSelected(true);
            txtPickupMethodDefault.setVisibility(View.VISIBLE);
            layoutPickupMethodChoices.setVisibility(View.GONE);

            if(pickupMethodData.id.equals(PICKUP_METHODE_STORE)){
                //Store selected
                initPickupMethodStoreLayout(pickupMethodData);
                layoutPickupMethodStore.setVisibility(View.VISIBLE);
                layoutPickupMethodAddress.setVisibility(View.GONE);
            }else if(pickupMethodData.id.equals(PICKUP_METHODE_ADDR)){
                //Address selected
                initPickupMethodAddressLayout(pickupMethodData);
                layoutPickupMethodStore.setVisibility(View.GONE);
                layoutPickupMethodAddress.setVisibility(View.VISIBLE);
            }

            selectedPickupMethodID = pickupMethodData.id;
            selectedPickupMethodName = pickupMethodData.name;
        } else {
            //choices pickup method
            layoutPickupMethodChoices.setVisibility(View.VISIBLE);
            txtPickupMethodDefault.setVisibility(View.GONE);

            for(int i=0;i<pickupMethodList.size();i++){
                final PickupMethodData pickupMethodData = pickupMethodList.get(i);
                if(pickupMethodData.id.equals(PICKUP_METHODE_STORE)){
                    //init Store Layout (TOKO)
                    txtPickupMethodStore.setText(pickupMethodData.name);
                    txtPickupMethodStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            togglePickupMethod(PICKUP_METHODE_STORE);
                            selectedPickupMethodName = pickupMethodData.name;
                        }
                    });
                    initPickupMethodStoreLayout(pickupMethodData);

                }else if(pickupMethodData.id.equals(PICKUP_METHODE_ADDR)){
                    //init Address Layout (ALAMAT)
                    txtPickupMethodAddress.setText(pickupMethodData.name);
                    txtPickupMethodAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            togglePickupMethod(PICKUP_METHODE_ADDR);
                            selectedPickupMethodName = pickupMethodData.name;
                        }
                    });
                    initPickupMethodAddressLayout(pickupMethodData);

                    //set default selected to Address
                    togglePickupMethod(PICKUP_METHODE_ADDR);
                    selectedPickupMethodName = pickupMethodData.name;
                }
            }
        }
    }

    private void initPickupMethodStoreLayout(PickupMethodData pickupMethodData){
        final AddressStore[] spinnerStoreData = pickupMethodData.object;
        final String[] arStoreList = new String[spinnerStoreData.length + 1];
        final String[] arStoreListDetail = new String[spinnerStoreData.length + 1];

        arStoreList[0] = getString(R.string.pilih_toko);
        arStoreListDetail[0] = " ";

        for (int j = 0; j < spinnerStoreData.length; j++) {
            arStoreList[j + 1] = spinnerStoreData[j].name;
            arStoreListDetail[j + 1] = spinnerStoreData[j].address;
        }

        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, arStoreList);
        storeAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerStore.setAdapter(storeAdapter);
        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (position == 0) {
                            layoutStoreAddress.setVisibility(View.GONE);
                        } else {
                            editAddress.setText(arStoreListDetail[position]);
                            txtStoreAddress.setText(arStoreListDetail[position]);
                            layoutStoreAddress.setVisibility(View.VISIBLE);

                            pickupContentID = spinnerStoreData[position-1].id;
                            pickupContentName = spinnerStoreData[position-1].name;
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initPickupMethodAddressLayout(PickupMethodData pickupMethodData){
        editAddress.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_ADDR, ""));

        final AddressStore[] spinnerCitiesData = pickupMethodData.object;
        String[] arrayCity = new String[spinnerCitiesData.length + 1];
        arrayCity[0] = getString(R.string.pilih_kota);
        for(int j=0;j<spinnerCitiesData.length;j++){
            arrayCity[j + 1] = spinnerCitiesData[j].name;
        }

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, arrayCity);
        citiesAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCity.setAdapter(citiesAdapter);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    shippingFee = 0;
                    paidAmount = cartData.totalAmount + shippingFee;
                    upDateTotalAfterShipping(View.GONE);
                }else {
                    getShippingFee();
                    pickupContentID = spinnerCitiesData[position-1].id;
                    pickupContentName = spinnerCitiesData[position-1].name;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void togglePickupMethod(String toggledPickupMethod) {
        if (selectedPickupMethodID==null || !selectedPickupMethodID.equals(toggledPickupMethod)) {
            if (toggledPickupMethod.equals(PICKUP_METHODE_STORE)) {
                //Store selected
                txtPickupMethodStore.setSelected(true);
                txtPickupMethodAddress.setSelected(false);

                layoutPickupMethodStore.setVisibility(View.VISIBLE);
                layoutPickupMethodAddress.setVisibility(View.GONE);

                editAddress.setText("");
                shippingFee = 0;
                paidAmount = cartData.totalAmount + shippingFee;
            } else if (toggledPickupMethod.equals(PICKUP_METHODE_ADDR)) {
                //Address selected
                txtPickupMethodStore.setSelected(false);
                txtPickupMethodAddress.setSelected(true);

                editAddress.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_ADDR, ""));
                layoutPickupMethodStore.setVisibility(View.GONE);
                layoutPickupMethodAddress.setVisibility(View.VISIBLE);
            }
            selectedPickupMethodID = toggledPickupMethod;
        }
    }

    private void upDateTotalAfterShipping(int visibility) {
        txtShippingFee.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(shippingFee)));
        layoutShippingFee.setVisibility(visibility);
    }

    private void getShippingFee() {
        String strJson = QrStoreUtil.getStringJson(cartData.carts, sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_NAME, ""),
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_EMAIL, ""), "", "",
                selectedPickupMethodID, sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_PHONE, ""),
                MerchantCode, sharedPreferences.getString(QrStoreDefine.SHARED_PREF_TRANS_ID, ""), paidAmount, "", "");
        if(PayByQRProperties.isDebugMode()) Log.d("strJson shippingFee", strJson);

        new GetStoreShipping(cartData.merchantURL, strJson).execute();
    }

    private void submitForm() {
        if(selectedPickupMethodID.equals(PICKUP_METHODE_ADDR) && editAddress.getText().length() == 0) {
            editAddress.setError(getString(R.string.tx_store_checout_alamat_empty));
            requestFocus(editAddress);
        }else if (selectedPickupMethodID.equals(PICKUP_METHODE_ADDR) && editAddress.getText().length() < 8 && selectedPickupMethodID.equals(PICKUP_METHODE_ADDR)) {
            editAddress.setError(getString(R.string.tx_store_checout_alamat_error));
            requestFocus(editAddress);
        }else if (selectedPickupMethodID.equals(PICKUP_METHODE_ADDR) && spinnerCity.getSelectedItemPosition() == 0){
            DIMOUtils.showAlertDialog(this, null, getString(R.string.tx_store_checout_kota_error), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else if (selectedPickupMethodID.equals(PICKUP_METHODE_STORE) && spinnerStore.getSelectedItemPosition() == 0){
            DIMOUtils.showAlertDialog(this, null, getString(R.string.tx_store_checout_toko_error), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else if(paidAmount < PayByQRProperties.getMinimumTransaction()) {
            DIMOUtils.showAlertDialog(this, null, getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else {
            if(selectedPickupMethodID.equals(PICKUP_METHODE_ADDR)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(QrStoreDefine.SHARED_PREF_CUST_ADDR, editAddress.getText().toString());
                editor.apply();
            }

            Intent intent = new Intent(StoreCheckout2.this, StoreKonfirmasiActivity.class);
            intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, MerchantCode);
            intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, MerchantName);
            intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_KONFIRMASI, generateStringKonfirm());
            //intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_STORES, storeNameToId);
            startActivityForResult(intent, 0);
        }
    }

    private String generateStringKonfirm() {
        String buff = shippingFee + LAZIES_PADD+
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_NAME, "")+LAZIES_PADD+
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_EMAIL, "")+LAZIES_PADD+
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_PHONE, "")+LAZIES_PADD+
                editAddress.getText().toString() +LAZIES_PADD+
                selectedPickupMethodID+LAZIES_PADD+
                selectedPickupMethodName+LAZIES_PADD+
                pickupContentID+LAZIES_PADD+
                pickupContentName;

        return buff;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            if (data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, false)){
                closeSDK(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, false),
                        data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, true),
                        data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                        data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC));
            }else{
                closeSDK(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true));
            }
        }else if(Constant.ACTIVITY_RESULT_NO_CONNECTION == resultCode){
            closeSDK(false);
        }else if(Constant.ACTIVITY_RESULT_QRSTORE_CHECKOUT_ERROR == resultCode){
            setResult(Constant.ACTIVITY_RESULT_QRSTORE_CHECKOUT_ERROR);
            finish();
        }
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    private void closeSDK(boolean isCloseSDK, boolean isShowCustomDialog, int code, String desc){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        intent.putExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, isShowCustomDialog);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, code);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC, desc);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(StoreCheckout2.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private class GetPickupMethodTask extends AsyncTask<Void, Void, String> {
        String serverURL, merchantCode;
        ProgressDialog progressDialog;

        public GetPickupMethodTask (String serverURL, String merchantCode) {
            this.serverURL = serverURL;
            this.merchantCode = merchantCode;

            progressDialog = new ProgressDialog(StoreCheckout2.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_login_waiting));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getPickupMethod(serverURL, merchantCode);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                pickupMethodList = DIMOService.parseJSONPickupMethod(StoreCheckout2.this, s);
                if (pickupMethodList != null && pickupMethodList.size() > 0) {
                    initPickupMethod();
                } else {
                    DIMOUtils.showAlertDialog(StoreCheckout2.this, null, getString(R.string.error_unknown), getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }, null, null);
                }
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        DIMOUtils.showAlertDialog(StoreCheckout2.this, null, e.getErrorMessage(), getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }, null, null);
                    }
                }
            }
        }
    }

    private class GetStoreShipping extends AsyncTask<Void, Void, String> {
        String serverURL;
        String paramsheader;
        ProgressDialog progressDialog;

        public GetStoreShipping (String serverURL, String param) {
            this.serverURL = serverURL;
            this.paramsheader = param;

            progressDialog = new ProgressDialog(StoreCheckout2.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_login_waiting));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getQrGeneric(serverURL, paramsheader, QrStoreDefine.PASS_SHIPPING);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                shippingFee = DIMOService.parseQrShippingFee(StoreCheckout2.this, s);
                paidAmount = cartData.totalAmount + shippingFee;
                if(PayByQRProperties.isDebugMode()) Log.d("shippingFee", ""+shippingFee);
                upDateTotalAfterShipping(View.VISIBLE);
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        DIMOUtils.showAlertDialog(StoreCheckout2.this, null, e.getErrorDetail(), getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spinnerCity.setSelection(0);
                            }
                        }, null, null);
                    }
                }
            }
        }
    }
}
