package com.dimo.PayByQR.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOUtils;

public class OfflineQRActivity extends AppCompatActivity {
    private PayByQRSDKListener listener;
    private TextView txtTitle;
    private ImageView btnBack;
    private EditText editAmount;
    private ImageButton btnClear;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_qr);

        listener = PayByQRSDK.getListener();

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);

        txtTitle.setText(getString(R.string.text_header_title_input_amount));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editAmount = (EditText) findViewById(R.id.activity_offlineQR_amount_edit);
        btnClear = (ImageButton) findViewById(R.id.activity_offlineQR_btn_clear);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editAmount, InputMethodManager.SHOW_IMPLICIT);

        editAmount.addTextChangedListener(textWatcherOfflineQR);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = 0;
                editAmount.setText("");
            }
        });

        amount = 0;
        editAmount.setText("");

        final String merchantRef = getIntent().getStringExtra(Constant.INTENT_EXTRA_INVOICE_ID);
        editAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(amount>0) {
                        if(amount < PayByQRProperties.getMinimumTransaction()) {
                            DIMOUtils.showAlertDialog(OfflineQRActivity.this, null,
                                    getString(R.string.error_minimum_trx, DIMOUtils.formatAmount(Integer.toString(PayByQRProperties.getMinimumTransaction()))),
                                    getString(R.string.alertdialog_posBtn_ok), null, null, null);
                        }else
                            doCreateInvoice(merchantRef, Integer.toString(amount));
                    }else
                        Toast.makeText(OfflineQRActivity.this, getString(R.string.error_empty_amount), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    TextWatcher textWatcherOfflineQR = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            String userInput = "" + s.toString().replaceAll("["+getString(R.string.text_detail_currency)+",.\\s]", "");
            StringBuilder cashAmountBuilder = new StringBuilder(userInput);
            if(cashAmountBuilder.length() == 0) cashAmountBuilder.append("0");

            //if (checkAmount(Integer.parseInt(cashAmountBuilder.toString()))) {
            amount = Integer.parseInt(cashAmountBuilder.toString());
            editAmount.removeTextChangedListener(this);

            editAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(cashAmountBuilder.toString()));
            Selection.setSelection(editAmount.getText(), editAmount.getText().length());

            editAmount.addTextChangedListener(this);
            //}
        }
    };

    private void doCreateInvoice(String merchantRef, String amount){
        Intent intent = new Intent(OfflineQRActivity.this, InvoiceDetailActivity.class);
        intent.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, merchantRef);
        intent.putExtra(Constant.INTENT_EXTRA_ORIGINAL_AMOUNT, amount);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(closeSDKBroadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION_CLOSE_SDK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeSDKBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        PayByQRProperties.setSDKContext(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editAmount, InputMethodManager.SHOW_IMPLICIT);
        super.onResume();
    }

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK(true);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.out_to_bottom);
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
        }
    }

    private void closeSDK(boolean isCloseSDK){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) listener.callbackSDKClosed();
        finish();
    }

    private void closeSDK(boolean isCloseSDK, boolean isShowCustomDialog, int code, String desc){
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseSDK);
        intent.putExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, isShowCustomDialog);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, code);
        intent.putExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC, desc);
        setResult(Constant.ACTIVITY_RESULT_CLOSE_SDK, intent);
        if(PayByQRSDK.getModule() == PayByQRSDK.MODULE_IN_APP) listener.callbackSDKClosed();
        finish();
    }
}
