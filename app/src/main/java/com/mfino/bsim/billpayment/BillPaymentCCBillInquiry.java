package com.mfino.bsim.billpayment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfino.bsim.R;
import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.activities.LoginScreen;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

import static com.mfino.bsim.services.Constants.LOG_TAG;

public class BillPaymentCCBillInquiry extends AppCompatActivity {
    ValueContainer valueContainer;
    int msgCode = 0;
    SharedPreferences languageSettings;
    String selectedLanguage;
    ProgressDialog dialog;
    String amount;
    EditText edAmount;
    String otpValue, sctl;
    private String responseXml;
    private Bundle bundle;
    private AlertDialog.Builder alertbox;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_inquiry);

        //Language Option..
        languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
        selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

        bundle = getIntent().getExtras();
        TextView tvConfirmInfo = findViewById(R.id.tv_Confirm_info);
        TextView aditionalInfo = findViewById(R.id.aditional_info);
        Button btn_confirm = findViewById(R.id.confirmButton);
        Button btn_cancel = findViewById(R.id.cancelButton);
        LinearLayout ccPaymentLayout = findViewById(R.id.isCreditCardLayout);
        final TextView otherAmountText = findViewById(R.id.tv_otherAmount);
        alertbox = new AlertDialog.Builder(BillPaymentCCBillInquiry.this, R.style.MyAlertDialogStyle);
        amount = bundle.getString("AMOUNT");
        if (bundle.getBoolean("IS_CCPAYMENT")) {
            ccPaymentLayout.setVisibility(View.VISIBLE);
            edAmount = findViewById(R.id.et_amount);
            edAmount.setText(amount);
        }

        tvConfirmInfo.setText(bundle.getString("MSG"));
        if (selectedLanguage.equalsIgnoreCase("ENG")) {
            otherAmountText.setText(getResources().getString(R.string.eng_otherAmount));
            btn_confirm.setText(getResources().getString(R.string.eng_submit));
        } else {
            otherAmountText.setText(getResources().getString(R.string.bahasa_otherAmount));
            btn_confirm.setText(getResources().getString(R.string.bahasa_submit));
        }

        try {
            if (bundle.getString("ADITIONAL_INFO").length() <= 0 || bundle.getString("ADITIONAL_INFO").equalsIgnoreCase("null")) {
                aditionalInfo.setVisibility(View.GONE);
            } else {
                aditionalInfo.setVisibility(View.VISIBLE);
                String adInfo = bundle.getString("ADITIONAL_INFO");
                StringBuilder sb = new StringBuilder();
                String delimiter = "\\|";
                String temp[] = adInfo.split(delimiter);
                for (String aTemp : temp) sb.append(aTemp).append("\n");
                aditionalInfo.setText(sb.toString());
            }
        } catch (Exception e) {

            aditionalInfo.setVisibility(View.GONE);
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null == edAmount.getText().toString()) {
                    if (selectedLanguage.equalsIgnoreCase("ENG"))
                        edAmount.setError(getResources().getString(
                                R.string.eng_otherAmount));
                    else
                        edAmount.setError(getResources().getString(
                                R.string.bahasa_otherAmount));
                } else {
                    amount = edAmount.getText().toString();
                    valueContainer = new ValueContainer();
                    valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
                    valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT_INQUIRY);
                    valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
                    valueContainer.setSourcePin(bundle.getString("PIN"));
                    valueContainer.setSourcePocketCode(getResources()
                            .getString(R.string.source_packet_code));
                    valueContainer.setAmount(amount);
                    valueContainer.setPaymentMode(bundle
                            .getString("SELECTED_PAYMENT_MODE"));
                    valueContainer.setBillerCode(bundle
                            .getString("PRODUCT_CODE"));
                    valueContainer.setBillNo(bundle.getString("BILLERNUM"));
                    try {
                        if (bundle.getString("MFA_MODE")
                                .equalsIgnoreCase("OTP")) {
                            valueContainer.setOTP(bundle.getString("OTP"));
                            valueContainer.setMfaMode(bundle
                                    .getString("MFA_MODE"));
                        }
                    } catch (Exception e1) {
                        Log.e(LOG_TAG, "print:" + e1.toString());
                    }

                    final WebServiceHttp webServiceHttp = new WebServiceHttp(
                            valueContainer, BillPaymentCCBillInquiry.this);

                    if (selectedLanguage.equalsIgnoreCase("ENG")) {
                        dialog = ProgressDialog.show(
                                BillPaymentCCBillInquiry.this,
                                "  Bank Sinarmas               ", getResources()
                                        .getString(R.string.eng_loading), true);

                    } else {
                        dialog = ProgressDialog.show(
                                BillPaymentCCBillInquiry.this,
                                "  Bank Sinarmas               ", getResources()
                                        .getString(R.string.bahasa_loading),
                                true);
                    }

                    @SuppressLint("HandlerLeak") final Handler handler = new Handler() {

                        public void handleMessage(Message msg) {
                            if (responseXml != null) {
                                /* Parse the response. */
                                XMLParser obj = new XMLParser();
                                EncryptedResponseDataContainer responseContainer = null;
                                try {
                                    responseContainer = obj.parse(responseXml);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();

                                try {
                                    if (responseContainer != null) {
                                        msgCode = Integer
                                                .parseInt(responseContainer.getMsgCode());
                                    }
                                } catch (Exception e) {
                                    msgCode = 0;
                                }
                                if (responseContainer != null) {
                                    System.out.println("messsage :"
                                            + responseContainer.getMsgCode());
                                }
                                if (!((msgCode == 660) || (msgCode == 72) || (msgCode == 713))) {
                                    if (responseContainer != null) {
                                        if (responseContainer.getMsg() == null) {

                                            if (selectedLanguage
                                                    .equalsIgnoreCase("ENG")) {
                                                displayDialog(getResources()
                                                        .getString(
                                                                R.string.eng_serverNotRespond));
                                            } else {
                                                displayDialog(getResources()
                                                        .getString(
                                                                R.string.bahasa_serverNotRespond));
                                            }
                                        } else {
                                            displayDialog(responseContainer
                                                    .getMsg());
                                            System.out
                                                    .println("Testing>>Buyenter>>rsponse"
                                                            + responseContainer
                                                            .getMsg());
                                        }
                                    }

                                } else {
                                    dialog.dismiss();
                                    try {
                                        if (responseContainer != null) {
                                            valueContainer
                                                    .setMfaMode(responseContainer.getMfaMode());
                                        }
                                    } catch (Exception e1) {
                                        valueContainer.setMfaMode("NONE");
                                    }
                                    if (valueContainer.getMfaMode()
                                            .equalsIgnoreCase("OTP")) {
                                        try {

                                            // final ProgressDialog dialog1 =
                                            // ProgressDialog.show(BillPaymentCCBillInquiry.this,
                                            // "  Banksinarmas               ",
                                            // "Please Wait for SMS....   ",
                                            // true);
                                            Long startTimeInMillis = new java.util.Date()
                                                    .getTime();

                                            while (true) {

                                                Thread.sleep(2000);
                                                System.out.println("Testing>>inside Loop");
                                                final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
                                                Cursor c = getContentResolver().query(SMS_INBOX, null, null, null, "DATE desc");

                                                c.moveToFirst();
                                                for (int i = 0; i < 10; i++) {
                                                    String body = c.getString(c.getColumnIndexOrThrow("body"));
                                                    //String number = c.getString(c.getColumnIndexOrThrow("address"));

                                                    if (body.contains("Kode Simobi Anda")) {

                                                        otpValue = body.substring("Kode Simobi Anda ".length(), body.indexOf("(ref"));
                                                        sctl = body.substring(body.indexOf(":") + 1, body.indexOf(")"));
                                                        break;

                                                    } else if (responseContainer != null) {
                                                        if (body.contains("Your Simobi Code is") && body.contains(responseContainer.getSctl())) {
                                                            otpValue = body.substring("Your Simobi Code is ".length(), body.indexOf("(ref"));
                                                            sctl = body.substring(body.indexOf("(ref no: ") + "(ref no: ".length(), body.indexOf(")"));
                                                            break;
                                                        } else {
                                                            c.moveToNext();
                                                        }
                                                    }
                                                }
                                                c.close();

                                                if (!(otpValue == null)) {
                                                    System.out
                                                            .println("Testing>>SCTL");
                                                    break;
                                                } else {

                                                    if (new java.util.Date()
                                                            .getTime()
                                                            - startTimeInMillis >= Constants.MFA_CONNECTION_TIMEOUT) {
                                                        break;
                                                    }
                                                }

                                            }
                                            System.out.println("Testing>>OTP>>"
                                                    + otpValue);
                                            if (otpValue == null) {
                                                // dialog1.dismiss();
                                                System.out
                                                        .println("Testing>>OTP>>null");

                                                if (selectedLanguage
                                                        .equalsIgnoreCase("ENG")) {

                                                    displayDialog(getResources()
                                                            .getString(
                                                                    R.string.eng_transactionFail));

                                                } else {
                                                    displayDialog(getResources()
                                                            .getString(
                                                                    R.string.bahasa_transactionFail));
                                                }

                                            } else {

                                                // dialog1.dismiss();
                                                Intent intent = new Intent(
                                                        BillPaymentCCBillInquiry.this,
                                                        BillPaymentConfirm.class);
                                                intent.putExtra("PIN",
                                                        bundle.getString("PIN"));
                                                intent.putExtra(
                                                        "SELECTED_CATEGORY",
                                                        bundle.getString("SELECTED_CATEGORY"));
                                                if (responseContainer != null) {
                                                    intent.putExtra("MSG",
                                                            responseContainer.getMsg());
                                                }
                                                // intent.putExtra("SELECTED_OFFLINE",
                                                // bundle.getString("SELECTED_OFFLINE"));
                                                if (responseContainer != null) {
                                                    System.out.println("Testing>>>Admitional INfo>>>>"
                                                            + responseContainer.getAditionalInfo());
                                                }
                                                try {

                                                    if (responseContainer != null) {
                                                        intent.putExtra(
                                                                "ADITIONAL_INFO",
                                                                responseContainer
                                                                        .getAditionalInfo());
                                                    }
                                                    intent.putExtra(
                                                            "IS_CCPAYMENT",
                                                            bundle.getBoolean("IS_CCPAYMENT"));
                                                } catch (Exception e) {

                                                    intent.putExtra(
                                                            "ADITIONAL_INFO",
                                                            "null");
                                                }
                                                intent.putExtra(
                                                        "PRODUCT_CODE",
                                                        bundle.getString("PRODUCT_CODE"));
                                                intent.putExtra(
                                                        "BILLERNUM",
                                                        bundle.getString("BILLERNUM"));
                                                if (responseContainer != null) {
                                                    intent.putExtra(
                                                            "PTFNID",
                                                            responseContainer
                                                                    .getEncryptedParentTxnId());
                                                }
                                                if (responseContainer != null) {
                                                    intent.putExtra(
                                                            "TFNID",
                                                            responseContainer
                                                                    .getEncryptedTransferId());
                                                }
                                                intent.putExtra("OTP", otpValue);
                                                intent.putExtra(
                                                        "SELECTED_PAYMENT_MODE",
                                                        bundle.getString("SELECTED_PAYMENT_MODE"));
                                                if (responseContainer != null) {
                                                    intent.putExtra("MFA_MODE",
                                                            responseContainer
                                                                    .getMfaMode());
                                                }
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        } catch (Exception e) {
                                            System.out
                                                    .println("Testing>>exception>>");
                                        }
                                    } else {
                                        dialog.dismiss();
                                        Intent intent = new Intent(
                                                BillPaymentCCBillInquiry.this,
                                                BillPaymentConfirm.class);
                                        intent.putExtra("PIN",
                                                bundle.getString("PIN"));
                                        intent.putExtra(
                                                "SELECTED_CATEGORY",
                                                bundle.getString("SELECTED_CATEGORY"));
                                        if (responseContainer != null) {
                                            intent.putExtra("MSG",
                                                    responseContainer.getMsg());
                                        }

                                        try {
                                            if (responseContainer != null) {
                                                intent.putExtra("ADITIONAL_INFO",
                                                        responseContainer
                                                                .getAditionalInfo());
                                            }
                                            intent.putExtra(
                                                    "IS_CCPAYMENT",
                                                    bundle.getBoolean("IS_CCPAYMENT"));
                                        } catch (Exception e) {
                                            intent.putExtra("ADITIONAL_INFO",
                                                    "null");
                                        }
                                        intent.putExtra("PRODUCT_CODE", bundle
                                                .getString("PRODUCT_CODE"));
                                        intent.putExtra("BILLERNUM",
                                                bundle.getString("BILLERNUM"));
                                        intent.putExtra(
                                                "SELECTED_PAYMENT_MODE",
                                                bundle.getString("SELECTED_PAYMENT_MODE"));
                                        if (responseContainer != null) {
                                            intent.putExtra(
                                                    "PTFNID",
                                                    responseContainer
                                                            .getEncryptedParentTxnId());
                                        }
                                        if (responseContainer != null) {
                                            intent.putExtra(
                                                    "TFNID",
                                                    responseContainer
                                                            .getEncryptedTransferId());
                                        }
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                }

                            } else {

                                dialog.dismiss();
                                if (selectedLanguage.equalsIgnoreCase("ENG")) {
                                    alertbox.setMessage(getResources()
                                            .getString(
                                                    R.string.eng_appTimeout));
                                } else {
                                    alertbox.setMessage(getResources()
                                            .getString(
                                                    R.string.bahasa_appTimeout));
                                }
                                alertbox.setNeutralButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface arg0,
                                                    int arg1) {

                                                Intent intent = new Intent(
                                                        getBaseContext(),
                                                        HomeScreen.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                            }
                                        });
                                alertbox.show();
                            }

                        }
                    };

                    final Thread checkUpdate = new Thread() {
                        /** Service call in this thread. */
                        public void run() {
                            try {
                                responseXml = webServiceHttp
                                        .getResponseSSLCertificatation();
                            } catch (Exception e) {
                                responseXml = null;
                            }
                            handler.sendEmptyMessage(0);
                        }
                    };
                    checkUpdate.start();
                }
            }
        });

        /* Cancel button event handling. */
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(BillPaymentCCBillInquiry.this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    // Dialog Displaying
    public void displayDialog(String msg) {
        alertbox = new AlertDialog.Builder(BillPaymentCCBillInquiry.this, R.style.MyAlertDialogStyle);
        alertbox.setMessage(msg);
        alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (msgCode == 631) {
                    Intent intent = new Intent(getBaseContext(), LoginScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        alertbox.show();
    }

}
