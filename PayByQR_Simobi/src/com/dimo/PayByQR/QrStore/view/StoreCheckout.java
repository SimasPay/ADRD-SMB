package com.dimo.PayByQR.QrStore.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.app.NotificationCompatSideChannelService;
//import android.support.v7.app.AppCompatActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.AddressStore;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.RespondJson;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;


import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.FailedActivity;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOEditText;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.ArrayList;
import java.util.HashMap;

import  static com.dimo.PayByQR.QrStore.constans.QrStoreDefine.*;

/**
 * Created by dimo on 1/11/16.
 */
public class StoreCheckout extends AppCompatActivity {
    private DIMOButton btnlanjut;
    private String MerchantCode, MerchantName;
    private CartData cartData;
    private ArrayList<AddressStore> storeList;
    private HashMap<String,String> storeNameToId;
    private TextView txtTitle;
    private SharedPreferences sharedPreferences;

    String []arStoreList;
    String []arStoreListDetail;

    int shippingFee, paidAmount;
    DIMOTextView txTotalBelanja, txShippingFee, txTotalBayar, txtAddressToko;//, txViewAddress;
    AppCompatSpinner spinnerPickupMethod, spinnerCity, spinnerStore;
    RelativeLayout relativeLayoutCity, relativeLayoutAddress, relativeLayoutStore;//, relativeLayoutFooter;
    EditText teNama, teTelepon, teAlamat, teEmail;

    ImageView btnBack;
    private String[] arrayPickupMethod, arrayCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_checkout);

        txTotalBelanja = (DIMOTextView)findViewById(R.id.activity_store_check_total);
        txShippingFee = (DIMOTextView)findViewById(R.id.activity_store_confirm_shipping_fee);
        txTotalBayar = (DIMOTextView)findViewById(R.id.activity_store_confirm_total_paid);
        spinnerPickupMethod = (AppCompatSpinner)findViewById(R.id.activity_store_pickup_method_spinner);
        spinnerCity = (AppCompatSpinner)findViewById(R.id.activity_store_kota_spinner);
        spinnerStore = (AppCompatSpinner)findViewById(R.id.activity_store_toko_spinner);

        txtTitle = (TextView) findViewById(R.id.header_bar_title);

        MerchantCode = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID);
        MerchantName = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD);
        txtTitle.setText(MerchantName);

        btnlanjut = (DIMOButton) findViewById(R.id.activity_store_btn_lanjut);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);

        /* =======  Dynamic layout    ===========  */
        relativeLayoutCity=(RelativeLayout)findViewById(R.id.activity_store_confirm_shipping_city_line);
        relativeLayoutStore=(RelativeLayout)findViewById(R.id.activity_store_confirm_pickup_method_store);
        relativeLayoutAddress=(RelativeLayout)findViewById(R.id. activity_store_confirm_shipping_addr_line);

        /* =============  Detail confirmasi=======*/

        teNama = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_name);
        teEmail = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_email);
        teTelepon = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_phone);
        teAlamat = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_addr);

        //txtAddressLabel = (DIMOTextView) findViewById(R.id.activity_store_confirm_shipping_addr_text);
        //storeDivider = findViewById(R.id.activity_store_confirm_shipping_divider);
        txtAddressToko = (DIMOTextView) findViewById(R.id.activity_store_addr_toko);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnlanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        arrayPickupMethod = getResources().getStringArray(R.array.pickup_method_entries);
        arrayCity = getResources().getStringArray(R.array.city_entries);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(QrStoreDefine.SHARED_PREF_TRANS_ID, QrStoreUtil.getTransId()).apply();

        initActivity();
    }

    private void initActivity() {
        cartData = QRStoreDBUtil.getCartsByMerchant(this, MerchantCode);
        cartData.printLogData();

        teNama.setText("");
        teEmail.setText("");
        teTelepon.setText("");
        teAlamat.setText("");
        txtAddressToko.setText("");
        shippingFee = 0;
        paidAmount = cartData.totalAmount + shippingFee;

        ArrayAdapter<String> pickupMethodAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, arrayPickupMethod);
        pickupMethodAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerPickupMethod.setAdapter(pickupMethodAdapter);
        spinnerPickupMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //pos 0 -> ALAMAT ; pos 1 -> TOKO
                if (position == 0) {
                    relativeLayoutStore.setVisibility(View.GONE);
                    relativeLayoutCity.setVisibility(View.VISIBLE);
                    relativeLayoutAddress.setVisibility(View.VISIBLE);
                    txtAddressToko.setVisibility(View.GONE);
                    teAlamat.setText("");
                    txtAddressToko.setText("");
                    spinnerCity.setSelection(0);
                } else {
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, arrayCity);
        citiesAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerCity.setAdapter(citiesAdapter);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    shippingFee = 0;
                    paidAmount = cartData.totalAmount + shippingFee;
                    upDateTotalAfterShipping();
                }else {
                    getShippingFee();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //spinnerPickupMethod.setSelection(1);    //default: TOKO
        //spinnerCity.setSelection(0);

        txTotalBelanja.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(cartData.totalAmount)));
        txShippingFee.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(shippingFee)));
        txTotalBayar.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(paidAmount)));

        new GetStoreAddress(cartData.merchantURL+"address/"+cartData.merchantCode).execute();
    }

    private String generateStringKonfirm() {
        String buff= shippingFee + LAZIES_PADD+
                arrayPickupMethod[spinnerPickupMethod.getSelectedItemPosition()]+ LAZIES_PADD+
                teNama.getText().toString()+LAZIES_PADD+
                teEmail.getText().toString()+LAZIES_PADD+
                teTelepon.getText().toString()+LAZIES_PADD+
                arrayCity[spinnerCity.getSelectedItemPosition()]+LAZIES_PADD+
                teAlamat.getText().toString() +LAZIES_PADD+
                arStoreList[spinnerStore.getSelectedItemPosition()]+LAZIES_PADD+
                arStoreListDetail[spinnerStore.getSelectedItemPosition()]+LAZIES_PADD;

        return buff;
    }

    private void gotoConfirm() {
        Intent intent = new Intent(StoreCheckout.this, StoreKonfirmasiActivity.class);
        intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, MerchantCode);
        intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, MerchantName);
        intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_KONFIRMASI, generateStringKonfirm());
        intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_STORES, storeNameToId);
        startActivityForResult(intent, 0);
    }

    private void submitForm() {
        String pickupMethode = arrayPickupMethod[spinnerPickupMethod.getSelectedItemPosition()];

        if(teNama.getText().length() == 0) {
            teNama.setError(getString(R.string.tx_store_checout_name_empty));
            requestFocus(teNama);
        }else if (teNama.getText().length() < 3) {
            teNama.setError(getString(R.string.tx_store_checout_name_error));
            requestFocus(teNama);
        }else if(teEmail.getText().length() == 0) {
            teEmail.setError(getString(R.string.tx_store_checout_email_empty));
            requestFocus(teEmail);
        }else if(teTelepon.getText().length() == 0) {
            teTelepon.setError(getString(R.string.tx_store_checout_telepon_empty));
            requestFocus(teTelepon);
        }else if (teTelepon.getText().length() < 7) {
            teTelepon.setError(getString(R.string.tx_store_checout_telepon_error));
            requestFocus(teTelepon);
        }else if (spinnerPickupMethod.getSelectedItemPosition() == 0 && spinnerCity.getSelectedItemPosition() == 0){
            DIMOUtils.showAlertDialog(this, null, getString(R.string.tx_store_checout_kota_error), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else if (spinnerPickupMethod.getSelectedItemPosition() == 1 && spinnerStore.getSelectedItemPosition() == 0){
            DIMOUtils.showAlertDialog(this, null, getString(R.string.tx_store_checout_toko_error), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else if(teAlamat.getText().length() == 0) {
            teAlamat.setError(getString(R.string.tx_store_checout_alamat_empty));
            requestFocus(teAlamat);
        }else if (teAlamat.getText().length() < 8 && pickupMethode.contains("ALAMAT")) {
            teAlamat.setError(getString(R.string.tx_store_checout_alamat_error));
            requestFocus(teAlamat);
        }else if(paidAmount < PayByQRProperties.getMinimumTransaction()) {
            DIMOUtils.showAlertDialog(this, null, getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))), getString(R.string.alertdialog_posBtn_ok), null, null, null);
        }else {
            gotoConfirm();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void getShippingFee() {
        String strJson = QrStoreUtil.getStringJson(cartData.carts, teNama.getText().toString(), "", "", "",
                arrayCity[spinnerCity.getSelectedItemPosition()],teTelepon.getText().toString(), MerchantCode,
                sharedPreferences.getString(QrStoreDefine.SHARED_PREF_TRANS_ID, ""), paidAmount, null, null);
        if(PayByQRProperties.isDebugMode()) Log.d("strJson shippingFee", strJson);

        new GetStoreShipping(cartData.merchantURL, strJson).execute();
    }

    private void upDateTotalAfterShipping() {
        txShippingFee.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(shippingFee)));
        txTotalBayar.setText(getString(R.string.text_detail_currency)+" "+DIMOUtils.formatAmount(Integer.toString(paidAmount)));
    }

    private class GetStoreShipping extends AsyncTask<Void, Void, String> {
        String serverURL;
        String paramsheader;
        ProgressDialog progressDialog;

        public GetStoreShipping (String serverURL, String param) {
            this.serverURL = serverURL;
            this.paramsheader = param;

            progressDialog = new ProgressDialog(StoreCheckout.this);
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
                shippingFee = DIMOService.parseQrShippingFee(StoreCheckout.this, s);
                paidAmount = cartData.totalAmount + shippingFee;
                if(PayByQRProperties.isDebugMode()) Log.d("shippingFee", ""+shippingFee);
                upDateTotalAfterShipping();
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        DIMOUtils.showAlertDialog(StoreCheckout.this, null, e.getErrorDetail(), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                    }
                }
            }
        }
    }

    private class GetStoreAddress extends AsyncTask<Void, Void, String> {
        String serverURL;
        ProgressDialog progressDialog;

        public GetStoreAddress (String serverURL) {
            this.serverURL = serverURL;

            progressDialog = new ProgressDialog(StoreCheckout.this);
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
            return DIMOService.getQrAddress(serverURL);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                storeList = DIMOService.parseQrAddress(StoreCheckout.this, s);
                if (storeList != null) {
                    arStoreList = new String[storeList.size() + 1];
                    arStoreListDetail = new String[storeList.size() + 1];

                    arStoreList[0] = getString(R.string.pilih_toko);
                    arStoreListDetail[0] = " ";

                    storeNameToId = new HashMap<>();
                    for (int i = 0; i < storeList.size(); i++) {
                        storeNameToId.put(storeList.get(i).getName(), storeList.get(i).getId());
                        arStoreList[i + 1] = storeList.get(i).getName();
                        arStoreListDetail[i + 1] = storeList.get(i).getAddress();
                    }

                    StoreSpinnerAdapter storeAdapter = new StoreSpinnerAdapter(StoreCheckout.this, R.layout.item_store_spinner, arStoreList, arStoreListDetail);
                    spinnerStore.setAdapter(storeAdapter);
                    spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (position == 0) {
                                        txtAddressToko.setVisibility(View.GONE);
                                    } else {
                                        txtAddressToko.setVisibility(View.GONE);
                                        teAlamat.setText(arStoreListDetail[position]);
                                        txtAddressToko.setText(arStoreListDetail[position]);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                    //set default pickup as TOKO
                    spinnerPickupMethod.setSelection(1);
                }
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        //goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                        DIMOUtils.showAlertDialog(StoreCheckout.this, null, e.getErrorMessage(), getString(R.string.alertdialog_posBtn_ok), null, null, null);
                    }
                }
            }
        }
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(StoreCheckout.this, FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(StoreCheckout.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
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
}
