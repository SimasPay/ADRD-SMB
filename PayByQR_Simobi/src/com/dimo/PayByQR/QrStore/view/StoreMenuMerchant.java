package com.dimo.PayByQR.QrStore.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.Merchant;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.UtilDb;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by san on 1/18/16.
 */
public class StoreMenuMerchant extends AppCompatActivity {
    private ImageView btnBack;
    private TextView txtTitle;
    private MerchantAdapter merchantAdapter;
    private ListView actListMerchant;
    private ArrayList<CartData> cartDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qrstore_merchant);

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtTitle.setText(getResources().getText(R.string.tx_store_keranjang_bel));
        actListMerchant = (ListView)findViewById(R.id.act_store_listview_mercant);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setFillTheBasket();
    }

    private void setFillTheBasket() {
        cartDatas = QRStoreDBUtil.getAllMerchantCarts(this, true);

        merchantAdapter = new MerchantAdapter(this, cartDatas);
        actListMerchant.setAdapter(merchantAdapter);
        actListMerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CartData cartData = cartDatas.get(position);

                Intent intent = new Intent(StoreMenuMerchant.this, StoreMenuActivity.class);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, cartData.merchantCode);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, cartData.merchantName);
                startActivityForResult(intent, 0);
            }
        });
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

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK(true);
        }
    };

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
        }else if(Constant.ACTIVITY_RESULT_QRSTORE_CART == resultCode){
            if(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, false)){
                closeSDK(false);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(Constant.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
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


