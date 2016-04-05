package com.dimo.PayByQR.QrStore.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.Merchant;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.QrStore.utility.UtilDb;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dimo on 1/19/16.
 */
public class MerchantAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartData> data;
    private LayoutInflater inflater;
    private ImageLoader imgLoader;

    public MerchantAdapter(Context context, ArrayList<CartData> dataItem) {
        this.data = dataItem;
        this.context = context;

        inflater = LayoutInflater.from(context);
        imgLoader = new ImageLoader(context);
        imgLoader.setIsScale(false);
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

        ImageView imageView = (ImageView) vi.findViewById(R.id.item_cart_merchant);
        DIMOTextView teMercahntName = (DIMOTextView) vi.findViewById(R.id. item_merchant_name);
        DIMOTextView teMercahntSum = (DIMOTextView) vi.findViewById(R.id.item_merchant_qty);

        CartData cartData = data.get(position);

        imgLoader.DisplayImage(cartData.merchantImage, R.drawable.loyalty_list_no_image, imageView);

        teMercahntName.setText(cartData.merchantName);

        int totalCartSize = 0;
        for(int i=0;i<cartData.carts.size();i++){
            totalCartSize += cartData.carts.get(i).qtyInCart;
        }
        teMercahntSum.setText(context.getString(R.string.tx_store_item_per_merchant, String.valueOf(totalCartSize)));

        return vi;
    }

}
