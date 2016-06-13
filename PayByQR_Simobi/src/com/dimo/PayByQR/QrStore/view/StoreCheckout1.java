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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.AddressStore;
import com.dimo.PayByQR.QrStore.model.PickupMethodData;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.DIMOBaseActivity;
import com.dimo.PayByQR.activity.FailedActivity;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.LoyaltyModel;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOEditText;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.HashMap;

import static com.dimo.PayByQR.QrStore.constans.QrStoreDefine.LAZIES_PADD;

/**
 * Created by Rhio on 31/03/16.
 */
public class StoreCheckout1 extends DIMOBaseActivity {
    private ImageView btnBack;
    private TextView txtTitle, txtSubtitle;
    private String MerchantCode, MerchantName;
    private SharedPreferences sharedPreferences;
    private EditText editName, editPhone, editEmail;
    private DIMOButton btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_checkout_1);

        MerchantCode = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID);
        MerchantName = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(QrStoreDefine.SHARED_PREF_TRANS_ID, QrStoreUtil.getTransId()).apply();

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        txtSubtitle = (TextView) findViewById(R.id.header_bar_subtitle);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);

        txtTitle.setText(getString(R.string.text_header_title_checkout_1));
        txtSubtitle.setText(getString(R.string.text_header_subtitle_checkout, "1"));
        txtSubtitle.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editName = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_name);
        editEmail = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_email);
        editPhone = (DIMOEditText)findViewById(R.id.activity_store_confirm_shipping_phone);
        btnLanjut = (DIMOButton) findViewById(R.id.activity_store_btn_lanjut);

        editName.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_NAME, ""));
        editEmail.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_EMAIL, ""));
        editPhone.setText(sharedPreferences.getString(QrStoreDefine.SHARED_PREF_CUST_PHONE, ""));
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        editName.setBackgroundResource(R.drawable.background_store_edittext);
        editPhone.setBackgroundResource(R.drawable.background_store_edittext);
        editEmail.setBackgroundResource(R.drawable.background_store_edittext);

        if(editName.getText().length() == 0) {
            editName.setBackgroundResource(R.drawable.background_store_edittext_error);
            editName.setError(getString(R.string.tx_store_checout_name_empty));
            requestFocus(editName);
        }else if (editName.getText().length() < 3) {
            editName.setBackgroundResource(R.drawable.background_store_edittext_error);
            editName.setError(getString(R.string.tx_store_checout_name_error));
            requestFocus(editName);
        }else if(editPhone.getText().length() == 0) {
            editPhone.setBackgroundResource(R.drawable.background_store_edittext_error);
            editPhone.setError(getString(R.string.tx_store_checout_telepon_empty));
            requestFocus(editPhone);
        }else if (editPhone.getText().length() < 7) {
            editPhone.setBackgroundResource(R.drawable.background_store_edittext_error);
            editPhone.setError(getString(R.string.tx_store_checout_telepon_error));
            requestFocus(editPhone);
        }else if(editEmail.getText().length() == 0) {
            editEmail.setBackgroundResource(R.drawable.background_store_edittext_error);
            editEmail.setError(getString(R.string.tx_store_checout_email_empty));
            requestFocus(editEmail);
        }else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()).matches()) {
            editEmail.setBackgroundResource(R.drawable.background_store_edittext_error);
            editEmail.setError(getString(R.string.tx_store_checout_email_error));
            requestFocus(editEmail);
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(QrStoreDefine.SHARED_PREF_CUST_NAME, editName.getText().toString());
            editor.putString(QrStoreDefine.SHARED_PREF_CUST_EMAIL, editEmail.getText().toString());
            editor.putString(QrStoreDefine.SHARED_PREF_CUST_PHONE, editPhone.getText().toString());
            editor.apply();

            Intent intent = new Intent(StoreCheckout1.this, StoreCheckout2.class);
            intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, MerchantCode);
            intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, MerchantName);
            startActivityForResult(intent, 0);
        }
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
                        data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC),
                        (LoyaltyModel) data.getParcelableExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY));
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

    private void closeSDK(boolean isCloseSDK, boolean isShowCustomDialog, int code, String desc, LoyaltyModel loyaltyModel){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        intent.putExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, isShowCustomDialog);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, code);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC, desc);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_LOYALTY, loyaltyModel);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        finish();
    }
}
