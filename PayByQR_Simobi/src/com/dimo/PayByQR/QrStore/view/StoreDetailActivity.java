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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.activity.DIMOBaseActivity;
import com.dimo.PayByQR.activity.FailedActivity;
import com.dimo.PayByQR.activity.NoConnectionActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.utils.imagecache.ImageCache;
import com.dimo.PayByQR.utils.imagecache.ImageFetcher;

/**
 * Created by dimo on 11/25/15.
 */
public class StoreDetailActivity extends DIMOBaseActivity {
    private ImageView btnBack, imageCart;
    private TextView txtTitle, txtNamaBarang, txtOriginalAmount, txtPaidAmount, txtDiscountAmount, txtDescription;  //, txtQuantity
    private RelativeLayout discountAmountBlock;
    private LinearLayout discountBlock;
    private GoodsData goodsData;
    private ImageFetcher mImageFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        imageCart = (ImageView) findViewById(R.id.imagecart);
        txtNamaBarang = (TextView) findViewById(R.id.store_name_goods);
        discountAmountBlock = (RelativeLayout) findViewById(R.id.qrstore_detail_disc_price_block);
        txtOriginalAmount = (TextView) findViewById(R.id.qrstore_detail_disc_price);
        txtPaidAmount = (TextView) findViewById(R.id.qrstore_detail_price);
        discountBlock = (LinearLayout) findViewById(R.id.qrstore_detail_discount_block);
        txtDiscountAmount = (TextView) findViewById(R.id.qrstore_detail_discount_amount);
        txtDescription = (TextView) findViewById(R.id.qrstore_detail_description);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(StoreDetailActivity.this, QrStoreDefine.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(StoreDetailActivity.this, imageCart.getWidth());
        mImageFetcher.setLoadingImage(R.drawable.loyalty_list_no_image);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        goodsData = QRStoreDBUtil.getGoodsFromCart(this, getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_GOODSID),
                getIntent().getStringExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID));
        goodsData.printLogData();
        loadView();
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
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
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
        txtTitle.setText(getString(R.string.text_header_title_goods_detail));
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

        mImageFetcher.loadImage(goodsData.image_url, imageCart);
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
                imageCart.setScaleType(ImageView.ScaleType.FIT_CENTER);

                mImageFetcher.setImageSize(imageCart.getWidth());
                mImageFetcher.loadImage(goodsData.image_url, imageCart);
            }
        });

        if(goodsData.discountAmount > 0) {
            discountAmountBlock.setVisibility(View.VISIBLE);
        }else
            discountAmountBlock.setVisibility(View.GONE);

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
