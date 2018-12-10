package com.mfino.bsim.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRSDK;
import com.mfino.bsim.R;
import com.mfino.bsim.account.ChangePin;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.flashiz.QRPayment2;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.handset.security.CryptoService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author pramod
 */
@SuppressLint("NewApi")
public class LoginScreen extends AppCompatActivity {
    private EditText loginId, loginPin;
    private AlertDialog.Builder alertbox;
    SharedPreferences languageSettings, encrptionKeys;
    ValueContainer valueContainer;
    private String responseXml;
    SharedPreferences settings, settings2;
    String selectedLanguage;
    ProgressDialog dialog;
    Context context;
    String userApiKey;
    String mobileNumber;
    DBHelper mydb;
    String mdn_name;
    ArrayList<String> array = new ArrayList<>();
    int msgcode;
    String new_mdn, final_mdn;
    final private int PERMISSION_REQUEST_CODE = 765;
    public static final String LOG_TAG = "SIMOBI";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                Log.d(LOG_TAG, "permission receiveSMS");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                        PERMISSION_REQUEST_CODE);
            }
        }

        mydb = new DBHelper(this);

        context = this;
        settings2 = getSharedPreferences(LOG_TAG, 0);
        settings2.edit().putString("ActivityName", "LoginScreen").apply();
        settings = getSharedPreferences("LOGIN_PREFERECES", 0);
        mobileNumber = settings.getString("mobile", "");
        // String password = settings.getString("pin", "");
        encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES", 0);
        languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
        selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

        /*
      Called when the activity is first created.
     */
        Button loginButton = findViewById(R.id.btn_Login);
        /*
        Button testButton = findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "contoh://deeplinknya")));
            }
        });
        */
        loginId = findViewById(R.id.ed_Login_MobNo);
        loginPin = findViewById(R.id.ed_Login_Pin);
        TextView loginTxt = findViewById(R.id.loginText);
        TextView mdnTxt = findViewById(R.id.mdn_Textview);
        TextView welcomeTxt = findViewById(R.id.lbl_welcome);
        loginId.setText(mobileNumber);

        if (selectedLanguage.equalsIgnoreCase("ENG")) {
            System.out.println("Testing1>>" + selectedLanguage);
            // loginButton.setBackgroundResource(R.drawable.login_button);
            loginTxt.setText(getResources().getString(R.string.eng_login));
            mdnTxt.setText(getResources().getString(R.string.eng_mobileNumber));
            welcomeTxt.setText(getResources().getString(R.string.eng_welcome));
        } else {
            // loginButton.setBackgroundResource(R.drawable.login_button);
            loginTxt.setText(getResources().getString(R.string.bahasa_login));
            mdnTxt.setText(getResources().getString(R.string.bahasa_mobileNumber));
            welcomeTxt.setText(getResources().getString(R.string.bahasa_welcome));
        }

        alertbox = new AlertDialog.Builder(LoginScreen.this, R.style.MyAlertDialogStyle);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View arg0) {

                // SDKLinkFragmentActivity.resetUserSession();

                boolean networkCheck = ConfigurationUtil.isConnectingToInternet(context);
                if (!networkCheck) {
                    if (selectedLanguage.equalsIgnoreCase("ENG")) {
                        ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_noInterent),
                                context);
                    } else {
                        ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_noInternet),
                                context);
                    }

                } else if (isRequiredFieldEmpty()) {
                    if (selectedLanguage.equalsIgnoreCase("ENG")) {
                        if (loginId.getText().toString().equals("")) {
                            alertbox.setMessage(getResources().getString(R.string.eng_emptymdn));
                        } else if (loginPin.getText().toString().equals("")) {
                            alertbox.setMessage(getResources().getString(R.string.eng_emptympin));
                        }
                    } else {
                        if (loginId.getText().toString().equals("")) {
                            alertbox.setMessage(getResources().getString(R.string.bahasa_emptymdn));
                        } else if (loginPin.getText().toString().equals("")) {
                            alertbox.setMessage(getResources().getString(R.string.bahasa_emptympin));
                        }
                    }
                    alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    alertbox.show();
                } else if (loginId.getText().length() < 4 || loginId.getText().length() > 16) {
                    if (selectedLanguage.equalsIgnoreCase("ENG")) {
                        alertbox.setMessage(getResources().getString(R.string.eng_enterValidMobile));
                    } else {
                        alertbox.setMessage(getResources().getString(R.string.bahasa_enterValidMobile));
                    }
                    alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    alertbox.show();
                } else if (loginPin.getText().length() < 6) {
                    if (selectedLanguage.equalsIgnoreCase("ENG")) {
                        alertbox.setMessage(getResources().getString(R.string.eng_LoginpinLength));
                    } else {
                        alertbox.setMessage(getResources().getString(R.string.bahasa_LoginpinLength));
                    }
                    alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    alertbox.show();
                } else {

                    try {
                        // ** Set Parameters for login service. *//*

                        // ** Set Parameters for login service. *//*
                        String module = encrptionKeys.getString("MODULE", "NONE");
                        String exponent = encrptionKeys.getString("EXPONENT", "NONE");
                        System.out.println(module + ">>KEYs>>" + exponent + ">>Login>>"
                                + Arrays.toString(loginPin.getText().toString().getBytes()));
                        String rsaKey = CryptoService.encryptWithPublicKey(module, exponent,
                                loginPin.getText().toString().getBytes());
                        String appVersion = getResources().getString(R.string.app_version);
                        valueContainer = new ValueContainer();
                        valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
                        String mdn = loginId.getText().toString().trim();
                        settings.edit().putString("mobile", mdn).apply();
                        Constants.SOURCE_MDN_NAME = loginId.getText().toString().trim();
                        valueContainer.setSourceMdn(mdn);
                        // settings.edit().putString("SOURCE_MDN_PIN",loginId.getText().toString()).commit();
                        // Without RSA
                        // valueContainer.setSourcePin(loginPin.getText().toString());

                        // RSA
                        valueContainer.setSourcePin(rsaKey);
                        valueContainer.setTransactionName(Constants.TRANSACTION_LOGIN);
                        valueContainer.setAppOS("android");
                        valueContainer.setAppType("subapp");
                        valueContainer.setAppVersion(appVersion);

                        final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, LoginScreen.this);

                        if (selectedLanguage.equalsIgnoreCase("ENG")) {
                            dialog = new ProgressDialog(LoginScreen.this, R.style.MyAlertDialogStyle);
                            dialog.setTitle("Bank Sinarmas");
                            dialog.setCancelable(false);
                            dialog.setMessage(getResources().getString(R.string.eng_loading));
                            dialog.show();
                        } else {
                            dialog = new ProgressDialog(LoginScreen.this, R.style.MyAlertDialogStyle);
                            dialog.setTitle("Bank Sinarmas");
                            dialog.setCancelable(false);
                            dialog.setMessage(getResources().getString(R.string.bahasa_loading));
                            dialog.show();
                        }

                        dialog.setCancelable(true);

                        final Handler handler = new Handler() {

                            public void handleMessage(Message msg) {

                                if (responseXml != null) {
                                    XMLParser obj = new XMLParser();
                                    EncryptedResponseDataContainer responseContainer = null;
                                    try {
                                        responseContainer = obj.parse(responseXml);
                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();

                                    int msgCode = 0;

                                    try {
                                        if (responseContainer != null) {
                                            msgCode = Integer.parseInt(responseContainer.getMsgCode());
                                        }
                                    } catch (Exception e) {
                                        msgCode = 0;
                                    }

                                    Log.d("Simobi", "get MsgCode : " + msgCode);
                                    if (!(msgCode == 630)) {
                                        if (responseContainer != null) {
                                            if (responseContainer.getMsg() == null) {
                                                if (selectedLanguage.equalsIgnoreCase("ENG")) {
                                                    alertbox.setMessage(
                                                            getResources().getString(R.string.eng_serverNotRespond));
                                                } else {
                                                    alertbox.setMessage(
                                                            getResources().getString(R.string.bahasa_serverNotRespond));
                                                }
                                                alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int arg1) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alertbox.show();
                                            } else if (msgCode == 2177) {
                                                if (selectedLanguage.equalsIgnoreCase("ENG")) {
                                                    alertbox.setMessage(getResources().getString(R.string.eng_changepin));
                                                } else {
                                                    alertbox.setMessage(
                                                            getResources().getString(R.string.bahasa_changepin));
                                                }
                                                alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int arg1) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(LoginScreen.this, ChangePin.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        Constants.SOURCE_MDN_NAME = loginId.getText().toString().trim();
                                                        intent.putExtra("mdn", settings.getString("mobile", ""));
                                                        intent.putExtra("REQUIRED", "yes");
                                                        startActivity(intent);
                                                    }
                                                });
                                                alertbox.show();
                                            } else {
                                                alertbox.setMessage(responseContainer.getMsg());
                                                alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int arg1) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alertbox.show();
                                            }
                                        }

                                    } else {
                                        settings.edit().putString("mobile", loginId.getText().toString()).apply();
                                        settings.edit().putString("pin", loginPin.getText().toString()).apply();
                                        String mobi = settings.getString("mobile", "");
                                        System.out.println(
                                                "Testing>>>mobile Number" + mobi + settings.getString("pin", ""));

                                        Constants.SOURCE_MDN_NAME = loginId.getText().toString().trim();
                                        settings.edit().putString("mobile", loginId.getText().toString().trim())
                                                .apply();
                                        valueContainer.setAppUpdateURL(responseContainer.getAppUpdateURL());

                                        System.out.println("hieeeeeeeee" + responseContainer.getAppUpdateURL());
                                        System.out.println("response :" + responseXml);
                                        Log.e("responseeeeee", responseXml);

                                        try {
                                            userApiKey = responseContainer.getUserApiKey();
                                            if (userApiKey.equalsIgnoreCase("NONE")) {
                                                getUserAPIKey();

                                            } else {
                                                settings.edit()
                                                        .putString("userApiKey", responseContainer.getUserApiKey())
                                                        .apply();
                                                Log.e("userApiKey---------", userApiKey);

                                            }
                                        } catch (Exception e) {
                                            Log.d(LOG_TAG, "error: " + e.toString());
                                        }
                                        new_mdn = loginId.getText().toString();

                                        Cursor rs = mydb.getData();
                                        Log.e("countttt", rs.getCount() + "");
                                        if (rs.getCount() != 0) {

                                            while (rs.moveToNext()) {
                                                // array.clear();
                                                mdn_name = rs
                                                        .getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
                                                array.add(mdn_name);

                                            }
                                            Log.e("mdn_name", array + ";;;;;;;;;;;;");

                                            Log.e("check_array_size", array.size() + "");
                                            if (array.size() != 0) {
                                                Log.e("mdn_name", array + ";;;;;;;;;;;;" + final_mdn + "final_mdn");
                                                if (new_mdn.startsWith("0")) {
                                                    Log.e("check_mdn_name+0", new_mdn);

                                                    final_mdn = new_mdn.substring(1);
                                                    final_mdn = "62" + final_mdn;
                                                    Log.e("check_mdn_name", new_mdn + final_mdn);

                                                } else if (new_mdn.startsWith("62")) {
                                                    Log.e("check_mdn_name+62", new_mdn);

                                                    final_mdn = new_mdn;
                                                    Log.e("check_mdn_name", new_mdn + final_mdn);

                                                } else {
                                                    Log.e("check_mdn_name----", new_mdn);

                                                    final_mdn = "62" + new_mdn;
                                                    Log.e("check_mdn_name", new_mdn + final_mdn);

                                                }
                                                Log.e("mdn_name", array + ";;;;;;;;;;;;" + final_mdn + "final_mdn");

                                                if (array.contains(final_mdn)) {
                                                    Log.e("new_mdn", new_mdn);
                                                    Log.e("check_mdn_name",
                                                            mobileNumber + array.toString() + "iffffff");

                                                } else {
                                                    Log.e("check_mdn_name", mobileNumber + "elseeeeeee");

                                                    // SDKLinkFragmentActivity.resetUserSession();
                                                    /*
													 * Log.e("check_mdn_name",
													 * mobileNumber+
													 * "else_outtttttt");
													 * if(new_mdn.startsWith("0"
													 * )){
													 * Log.e("check_mdn_name+0",
													 * new_mdn);
													 *
													 * final_mdn=new_mdn.
													 * substring(1);
													 * final_mdn="62"+final_mdn;
													 * Log.e("check_mdn_name",
													 * new_mdn+final_mdn);
													 *
													 *
													 *
													 * }else
													 * if(new_mdn.startsWith(
													 * "62")){
													 * Log.e("check_mdn_name+62"
													 * ,new_mdn);
													 *
													 * final_mdn=new_mdn;
													 * Log.e("check_mdn_name",
													 * new_mdn+final_mdn);
													 *
													 *
													 * }else{ Log.e(
													 * "check_mdn_name----",
													 * new_mdn);
													 *
													 * final_mdn="62"+new_mdn;
													 * Log.e("check_mdn_name",
													 * new_mdn+final_mdn);
													 *
													 *
													 * }
													 */
                                                    Log.e("check_mdn_name",
                                                            new_mdn + final_mdn);

                                                    mydb.insertMdn(final_mdn);
                                                    mydb.insertfalshiz("false", "");

                                                    // mydb.insertMdn(loginId.getText().toString());
                                                    // mydb.insertfalshiz("false","");

                                                    // mydb.insertfalshiz("false",
                                                    // loginId.getText().toString());

                                                }

                                            }
                                            // mydb.insertMdn(loginId.getText().toString());

                                        } else {
                                            Log.e("no data", "founddddddd");
                                            new_mdn = loginId.getText().toString();
                                            if (new_mdn.startsWith("0")) {
                                                Log.e("check_mdn_name+0", new_mdn);

                                                final_mdn = new_mdn.substring(1);
                                                final_mdn = "62" + final_mdn;
                                                Log.e("check_mdn_name", new_mdn + final_mdn);

                                            } else if (new_mdn.startsWith("62")) {
                                                Log.e("check_mdn_name+62", new_mdn);

                                                final_mdn = new_mdn;
                                                Log.e("check_mdn_name", new_mdn + final_mdn);

                                            } else {
                                                Log.e("check_mdn_name----", new_mdn);

                                                final_mdn = "62" + new_mdn;
                                                Log.e("check_mdn_name", new_mdn + final_mdn);

                                            }
                                            Log.e("check_mdn_name", new_mdn + final_mdn);

                                            mydb.insertMdn(final_mdn);
                                            mydb.insertfalshiz("false", "");
                                            // mydb.insertMdn(loginId.getText().toString());
                                            // mydb.insertfalshiz("false","");
                                            // mydb.insertfalshiz("false",
                                            // loginId.getText().toString());

                                            rs = mydb.getData();
                                            Log.e("countttt", rs.getCount() + "");
                                            if (rs.getCount() != 0) {

                                                while (rs.moveToNext()) {
                                                    // array.clear();
                                                    mdn_name = rs.getString(
                                                            rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
                                                    array.add(mdn_name);

                                                }
                                                Log.e("mdn_name", array + "''''''''''''''''");

                                                Log.e("check_array_size", array.size() + "");
                                                if (array.size() != 0) {
                                                    if (array.contains(mobileNumber)) {
                                                        Log.e("check_mdn_name",
                                                                mobileNumber + array.toString() + "iffffff_iff");
                                                    } else {
                                                        Log.e("check_mdn_name", mobileNumber + "elseeeeeee_eeeee");
                                                        // SDKLinkFragmentActivity.resetUserSession();
                                                        Log.e("check_mdn_name", mobileNumber + "else_outtttttt_tttttt");
                                                        // mydb.insertMdn(loginId.getText().toString());

                                                    }

                                                }

                                            }
                                        }

                                        // }
                                        if (!rs.isClosed()) {
                                            rs.close();
                                        }

                                        if (getIntent().hasExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID)) {
                                            Intent intent = new Intent(LoginScreen.this, QRPayment2.class);
                                            intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_IN_APP);
                                            intent.putExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID,
                                                    getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID));
                                            intent.putExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK,
                                                    getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK));
                                            startActivity(intent);
                                            setResult(RESULT_OK);
                                            finish();
                                        } else {
                                            switch (responseContainer.getSimobiPlusUpgrade()) {
                                                case "0": {
                                                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("upgradeEnable", false);
                                                    intent.putExtra("getUpgradeValue", 0);
                                                    startActivity(intent);
                                                    break;
                                                }
                                                case "1": {
                                                    Intent intent = new Intent(LoginScreen.this, UpgradeToSimobiPlus.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("upgradeEnable", true);
                                                    intent.putExtra("getUpgradeValue", 1);
                                                    startActivity(intent);
                                                    break;
                                                }
                                                case "2": {
                                                    Intent intent = new Intent(LoginScreen.this, UpgradeToSimobiPlus.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("upgradeEnable", true);
                                                    intent.putExtra("getUpgradeValue", 2);
                                                    startActivity(intent);
                                                    break;
                                                }
                                            }
                                        }

										/*
										 * if ("-1".equals(responseContainer.
										 * getAppUpdateURL())) {
										 *
										 * Intent intent = new
										 * Intent(LoginScreen.this,HomeScreen.
										 * class); intent.setFlags(Intent.
										 * FLAG_ACTIVITY_CLEAR_TOP);
										 * startActivity(intent);
										 *
										 * } else { Intent intent = new
										 * Intent(LoginScreen.this,Update.class)
										 * ;
										 * intent.putExtra("msg",valueContainer.
										 * getAppUpdateURL());
										 * intent.setFlags(Intent.
										 * FLAG_ACTIVITY_CLEAR_TOP);
										 * startActivity(intent); }
										 */
                                    }
                                    loginPin.setText("");
                                } else {

                                    dialog.dismiss();
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
                                }
                            }
                        };

                        final Thread checkUpdate = new Thread() {
                            public void run() {
                                try {
                                    responseXml = webServiceHttp.getResponseSSLCertificatation(); // Service
                                    // //
                                    // call
                                } catch (Exception e) {
                                    responseXml = null;
                                }
                                handler.sendEmptyMessage(0);
                            }
                        };
                        checkUpdate.start();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    private boolean isRequiredFieldEmpty() {
        loginId = findViewById(R.id.ed_Login_MobNo);
        loginPin = findViewById(R.id.ed_Login_Pin);
        return !(!(loginId.getText().toString().equals("")) && !(loginPin.getText().toString().equals("")));
    }

    @SuppressLint("HandlerLeak")
    private void getUserAPIKey() {

        valueContainer = new ValueContainer();
        // valueContainer.setContext(QRPayment.this);
        valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
        valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
        valueContainer.setTransactionName(Constants.TRANSACTION_USER_APIKEY);

        final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, LoginScreen.this);

        dialog = ProgressDialog.show(LoginScreen.this, "  Mohon tunggu sebentar        ",
                getResources().getString(R.string.bahasa_loading), true);

        final Handler handler = new Handler() {

            public void handleMessage(Message msg) {

                if (responseXml != null) {

                    XMLParser obj = new XMLParser();
                    /* Parsing of response. */
                    EncryptedResponseDataContainer responseContainer = null;
                    try {
                        responseContainer = obj.parse(responseXml);
                        msgcode = Integer.parseInt(responseContainer.getMsgCode());
                        System.out.println(">>>>>>>>" + responseContainer.getMsg());

                    } catch (Exception e) {

                        msgcode = 0;
                    }

                    dialog.dismiss();
                    if (msgcode == 2103) {
                        if (responseContainer != null) {
                            userApiKey = responseContainer.getUserApiKey();
                            settings.edit().putString("userApiKey", responseContainer.getUserApiKey()).apply();
                        }
                        if (getIntent().hasExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID)) {
                            Intent intent = new Intent(LoginScreen.this, QRPayment2.class);
                            intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_IN_APP);
                            intent.putExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID,
                                    getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID));
                            intent.putExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK,
                                    getIntent().getStringExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK));
                            startActivity(intent);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        // UserAPI Key
                        // userApiKey = settings.getString("userApiKey",
                        // "NONE");
                        Log.e("userApiKey", userApiKey);

                    } else {
                        userApiKey = "NONE";
                        String massage = null;
                        try {
                            if (responseContainer != null) {
                                massage = responseContainer.getMsg();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            massage = getResources().getString(R.string.bahasa_serverNotRespond);
                        }
                        // alertbox = new AlertDialog.Builder(context);
                        alertbox.setMessage(massage);
                        alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        });
                        alertbox.show();
                    }

                } else {
                    dialog.dismiss();
                    // alertbox = new AlertDialog.Builder(context);
                    alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Simobi", "sms read granted");
                } else {
                    Log.d("Simobi", "sms read failed");
                }
            }
        }
    }

}