package com.dimo.PayByQR.QrStore.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;
import com.dimo.PayByQR.view.DIMOTextView;

/**
 * Created by dimo on 11/25/15.
 */
public class StoreMenuActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle, btnReset;
    private DIMOTextView txtotbel;
    private DIMOButton btncheckout, btnaddgoods, btmulaiBelanja;
    private RelativeLayout layoutEmptyCart, layoutCart;
    private String MerchantCode;
    private ListView listView;
    private CartItemAdapter adapter;

    private CartData cartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);

        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnReset = (TextView) findViewById(R.id.header_bar_action_done);
        btnReset.setText(getString(R.string.tx_store_action_reset));
        btnReset.setVisibility(View.VISIBLE);
        btncheckout = (DIMOButton)findViewById(R.id.activity_store_menu_btn_checkout);
        btnaddgoods = (DIMOButton)findViewById(R.id.activity_store_menu_btn_add_more);
        btmulaiBelanja = (DIMOButton) findViewById(R.id.activity_store_mulai);
        txtotbel = (DIMOTextView)findViewById(R.id.activity_store_menu_total_paid);
        listView = (ListView) findViewById(R.id.listview);
        layoutCart = (RelativeLayout) findViewById(R.id.activity_store_menu_layout_cart);
        layoutEmptyCart = (RelativeLayout) findViewById(R.id.activity_store_menu_layout_empty_cart);

        MerchantCode = getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID);
        txtTitle.setText(getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnaddgoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBackStack(true);
            }
        });
        btmulaiBelanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBackStack(true);
            }
        });
        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreMenuActivity.this, StoreCheckout.class);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, cartData.merchantCode);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, cartData.merchantName);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_SHIPPING, "0");
                startActivityForResult(intent, 0);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int removed = QRStoreDBUtil.removeAllCartsByMerchant(StoreMenuActivity.this, MerchantCode);
                if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "Removed Goods: " + removed);

                gotoEmpty();
            }
        });

        initActivity();
    }

    private void gotoEmpty() {
        layoutCart.setVisibility(View.GONE);
        layoutEmptyCart.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.GONE);
    }

    private void initActivity() {
        cartData = QRStoreDBUtil.getCartsByMerchant(StoreMenuActivity.this, MerchantCode);
        cartData.printLogData();

        if ((cartData.carts.size()==0) || (cartData.carts == null))
            gotoEmpty();

        if(null == adapter) {
            adapter = new CartItemAdapter(this, cartData.carts, onItemDeleted);
            listView.setAdapter(adapter);
        }else{
            adapter.setCartArrayList(cartData.carts);
        }

        txtotbel.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(cartData.totalAmount)));
    }

    @SuppressLint("HandlerLeak")
    private Handler onItemDeleted = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.MESSAGE_END_OK) {
                initActivity();
            }
        }
    };

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
        super.onResume();
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

    @Override
    public void onBackPressed() {
        if(layoutEmptyCart.getVisibility() == View.VISIBLE)
            doBackStack(true);
        else
            doBackStack(false);
    }

    private void doBackStack(boolean isCloseToScan){
        Intent intent = new Intent();
        //if(layoutEmptyCart.getVisibility() == View.VISIBLE) {
            intent.putExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, isCloseToScan);
        //}
        setResult(Constant.ACTIVITY_RESULT_QRSTORE_CART, intent);
        finish();
    }

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK(true);
        }
    };

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