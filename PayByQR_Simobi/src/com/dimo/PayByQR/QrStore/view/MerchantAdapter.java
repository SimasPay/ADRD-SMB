package com.dimo.PayByQR.QrStore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.utils.imagecache.ImageCache;
import com.dimo.PayByQR.utils.imagecache.ImageFetcher;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.ArrayList;

/**
 * Created by dimo on 1/19/16.
 */
public class MerchantAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartData> data;
    private LayoutInflater inflater;
    private ImageFetcher mImageFetcher;

    public MerchantAdapter(Context context, ArrayList<CartData> dataItem) {
        this.data = dataItem;
        this.context = context;

        inflater = LayoutInflater.from(context);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(context, QrStoreDefine.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(context, 300);
        mImageFetcher.setLoadingImage(R.drawable.loyalty_list_no_image);
        mImageFetcher.addImageCache(((FragmentActivity) context).getSupportFragmentManager(), cacheParams);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(null == convertView)
            vi = inflater.inflate(R.layout.qr_merchant_each, null);

        final ImageView imageView = (ImageView) vi.findViewById(R.id.item_cart_merchant);
        DIMOTextView teMercahntName = (DIMOTextView) vi.findViewById(R.id. item_merchant_name);
        DIMOTextView teMercahntSum = (DIMOTextView) vi.findViewById(R.id.item_merchant_qty);

        final CartData cartData = data.get(position);

        mImageFetcher.loadImage(cartData.merchantImage, imageView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        mImageFetcher.setImageSize(imageView.getWidth());
                        mImageFetcher.loadImage(cartData.merchantImage, imageView);
                    }
                });

        teMercahntName.setText(cartData.merchantName);

        int totalCartSize = 0;
        for(int i=0;i<cartData.carts.size();i++){
            totalCartSize += cartData.carts.get(i).qtyInCart;
        }
        teMercahntSum.setText(context.getString(R.string.tx_store_item_per_merchant, String.valueOf(totalCartSize)));

        return vi;
    }

}
