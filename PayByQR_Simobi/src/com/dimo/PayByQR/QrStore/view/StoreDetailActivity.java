package com.dimo.PayByQR.QrStore.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.R;

import com.dimo.PayByQR.activity.FailedActivity;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.view.DIMOButton;

/**
 * Created by dimo on 11/25/15.
 */
public class StoreDetailActivity extends AppCompatActivity {
    private ImageView btnBack, imageCart;
    private TextView txtTitle, txtNamaBarang, txtOriginalAmount, txtPaidAmount, txtDiscountAmount, txtDescription;  //, txtQuantity
    //private DIMOButton btnAddtoCart;
    //private ImageButton btn_minus, btn_plus;
    private RelativeLayout discountAmountBlock;
    private LinearLayout discountBlock;
    //private String QRStoreURL;
    //private int qtyToAdd = 0, maxQtyToAdd = 0;
    private ImageLoader imgLoader;
    private GoodsData goodsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        //QRStoreURL = getIntent().getStringExtra(Constant.INTENT_EXTRA_INVOICE_ID);

        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        //btnAddtoCart = (DIMOButton) findViewById(R.id.activity_storeDetail_btn_addtocart);
        imageCart = (ImageView) findViewById(R.id.imagecart);
        //btn_minus = (ImageButton) findViewById(R.id.activity_storeDetail_btn_minus);
        //btn_plus = (ImageButton) findViewById(R.id.activity_storeDetail_btn_plus);
        //txtQuantity = (TextView) findViewById(R.id.qrstore_detail_quantity_item);
        txtNamaBarang = (TextView) findViewById(R.id.store_name_goods);
        discountAmountBlock = (RelativeLayout) findViewById(R.id.qrstore_detail_disc_price_block);
        txtOriginalAmount = (TextView) findViewById(R.id.qrstore_detail_disc_price);
        txtPaidAmount = (TextView) findViewById(R.id.qrstore_detail_price);
        discountBlock = (LinearLayout) findViewById(R.id.qrstore_detail_discount_block);
        txtDiscountAmount = (TextView) findViewById(R.id.qrstore_detail_discount_amount);
        txtDescription = (TextView) findViewById(R.id.qrstore_detail_description);

        imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.setIsScale(false);

        /*btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemQtyChange(true, false);
            }
        });
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemQtyChange(false, false);
            }
        });*/

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*btnAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsData.qtyInCart += qtyToAdd;
                QRStoreDBUtil.addGoodsToCart(StoreDetailActivity.this, goodsData);

                Intent intent = new Intent(StoreDetailActivity.this, StoreMenuActivity.class);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, goodsData.merchantCode);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD, goodsData.merchantName);
                startActivityForResult(intent, 0);
            }
        });*/


        //if(getIntent().hasExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID) && getIntent().hasExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_GOODSID)){
            goodsData = QRStoreDBUtil.getGoodsFromCart(this, getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_GOODSID),
                    getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID));
            goodsData.printLogData();
            loadView();
        /*}else {
            new GetGoodsDetail(QRStoreURL).execute();
        }*/
    }

    /*public void onItemQtyChange(boolean isAdd, final boolean isFromScan) {
        if (isAdd) {
            *//*1. stock = 0; maxQuantity = any (0-n)
            -> Maaf, stok barang habis
            2. stock = m; maxQuantity = n; stock < maxQuantity
            eg: stock = 5; maxQuantity = 10
            -> Stok yang tersedia untuk barang ini adalah 5
            3. stock = m; maxQuantity = n; stock > maxQuantity
            eg: stock = 10; maxQuantity = 7
            -> Jumlah maksimal yang bisa Anda beli untuk barang ini adalah 7*//*

            if(goodsData.maxQuantity == 0){
                maxQtyToAdd = goodsData.stock - goodsData.qtyInCart;
                if (qtyToAdd < maxQtyToAdd) {
                    qtyToAdd++;
                } else {
                    String errorMsg = getString(R.string.error_max_stock, goodsData.stock);
                    if(goodsData.qtyInCart > 0)
                        errorMsg = errorMsg + getString(R.string.error_max_goods_in_cart, goodsData.qtyInCart);

                    DIMOUtils.showAlertDialog(StoreDetailActivity.this, null, errorMsg, getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if(isFromScan) finish();
                                }
                            }, null, null);
                }
            }else {
                maxQtyToAdd = Math.min(goodsData.stock, goodsData.maxQuantity) - goodsData.qtyInCart;
                if (qtyToAdd < maxQtyToAdd) {
                    qtyToAdd++;
                } else {
                    String errorMsg = "";
                    if(goodsData.stock < goodsData.maxQuantity){
                        errorMsg = getString(R.string.error_max_stock, goodsData.stock);
                    }else{
                        errorMsg = getString(R.string.error_max_qty, goodsData.maxQuantity);
                    }

                    if(goodsData.qtyInCart > 0)
                        errorMsg = errorMsg + getString(R.string.error_max_goods_in_cart, goodsData.qtyInCart);

                    DIMOUtils.showAlertDialog(StoreDetailActivity.this, null, errorMsg, getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if(isFromScan) finish();
                                }
                            }, null, null);
                }
            }
        } else {
            if (qtyToAdd > 1)
                qtyToAdd--;
        }

        txtQuantity.setText(String.valueOf(qtyToAdd));
        txtOriginalAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString((int) (qtyToAdd * goodsData.price))));
        txtPaidAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(qtyToAdd * (int) (goodsData.price - goodsData.discountAmount))));
    }*/

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
            closeSDK(false);
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

    private void loadView() {
        txtTitle.setText("Detil Barang");
        txtNamaBarang.setText(goodsData.goodsName);
        txtOriginalAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(""+(int) goodsData.price));
        txtPaidAmount.setText(getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString((int) goodsData.price - (int)goodsData.discountAmount)));
        txtDescription.setText(goodsData.description);

        float disc = ((float)goodsData.discountAmount/(float)goodsData.price) * 100;
        if(disc > 0) {
            txtDiscountAmount.setText((int) disc + "%");
            discountBlock.setVisibility(View.VISIBLE);
        }else{
            discountBlock.setVisibility(View.GONE);
        }

        imageCart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageCart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    imageCart.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                ViewGroup.LayoutParams layout = imageCart.getLayoutParams();
                layout.height = layout.width;
                imageCart.setLayoutParams(layout);
            }
        });
        imgLoader.DisplayImage(goodsData.image_url, R.drawable.loyalty_list_no_image, imageCart);

        if(goodsData.discountAmount > 0) {
            discountAmountBlock.setVisibility(View.VISIBLE);
        }else
            discountAmountBlock.setVisibility(View.GONE);

        //handle no stock
        /*if(goodsData.stock <= 0){
            DIMOUtils.showAlertDialog(StoreDetailActivity.this, null, getString(R.string.error_no_stock), getString(R.string.alertdialog_posBtn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }, null, null);
        } else {
            onItemQtyChange(true, true);
        }*/
    }

    private class GetGoodsDetail extends AsyncTask<Void, Void, String> {
        String URL;
        ProgressDialog progressDialog;

        public GetGoodsDetail(String URL){
            this.URL = URL;
            progressDialog = new ProgressDialog(StoreDetailActivity.this);
            progressDialog.setMessage(getString(R.string.progressdialog_message_get_store_item_detail));
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getCartDetail(URL);
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != progressDialog) progressDialog.dismiss();
            try {
                goodsData = DIMOService.parseJSONGoodsDetail(StoreDetailActivity.this, s);

                GoodsData goodsInCart = QRStoreDBUtil.getGoodsFromCart(StoreDetailActivity.this, goodsData.id, goodsData.merchantCode);
                if(null != goodsInCart){
                    goodsData.qtyInCart = goodsInCart.qtyInCart;
                    goodsData.merchantURL = getMerchantURL(URL);
                }else{
                    goodsData.qtyInCart = 0;
                    goodsData.merchantURL = getMerchantURL(URL);
                }
                goodsData.printLogData();

                loadView();
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    closeSDK(false, true, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail());
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else if (e.getErrorCode() == Constant.ERROR_CODE_INVALID_QR) {
                        goToFailedScreen(getString(R.string.error_invalid_qr_title), e.getErrorMessage(), Constant.REQUEST_CODE_ERROR_INVALID_QR);
                    } else if (e.getErrorCode() == Constant.ERROR_CODE_INVALID_GOODS) {
                        DIMOUtils.showAlertDialog(StoreDetailActivity.this, null, getString(R.string.error_item_not_valid),
                                getString(R.string.alertdialog_posBtn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                }, null, null);
                    } else {
                        goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                    }
                }
            }
        }

        private void goToFailedScreen(String title, String errorDetail, int requestCode){
            Intent intentFailed = new Intent(StoreDetailActivity.this, FailedActivity.class);
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
            intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
            startActivityForResult(intentFailed, requestCode);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        }

        private void goToNoConnectionScreen(){
            Intent intentFailed = new Intent(StoreDetailActivity.this, NoConnectionActivity.class);
            startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
        }

        private String getMerchantURL(String goodsURL){
            return goodsURL.substring(0, goodsURL.indexOf("qrstore")) + "qrstore/api/v2/";
        }
    }
}
