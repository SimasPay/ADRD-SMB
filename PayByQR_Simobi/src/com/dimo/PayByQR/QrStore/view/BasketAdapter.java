package com.dimo.PayByQR.QrStore.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.view.DIMOTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by san on 1/17/16.
 */
public class BasketAdapter extends BaseAdapter {


    private Context context;
    private List data = new ArrayList<Object>();
    private static LayoutInflater inflater = null;

    public BasketAdapter(Context context, List dataItem) {

        this.data = dataItem;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if(null == convertView)
            vi = inflater.inflate(R.layout.act_keranjang_each, null);

            DIMOTextView txQuantity = (DIMOTextView) vi.findViewById(R.id.act_item_quantity);
            DIMOTextView txName = (DIMOTextView) vi.findViewById(R.id.act_goods_name);
            DIMOTextView txTotal = (DIMOTextView) vi.findViewById(R.id.act_item_total);

        Cart temp = (Cart) data.get(position);

        txQuantity.setText(String.valueOf(temp.getDetailQuantity()));
        txName.setText(  QrStoreUtil.setLimitMultiRow(temp.getGoodsName(), QrStoreDefine.MAXIMUM_CHAR_ONLINE));


        txTotal.setText(QrStoreUtil.displayMenu(temp.getDetailQuantity(), temp.getPrice()-temp.getDiscountAmount(), false));

            return vi;
        }

}


