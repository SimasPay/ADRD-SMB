package com.dimo.PayByQR.QrStore.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dimo.PayByQR.R;

import java.util.ArrayList;

/**
 * Created by Rhio on 2/10/16.
 */
public class StoreSpinnerAdapter extends ArrayAdapter<String>{
    Context mContext;
    int viewResourceID;
    String[] datas, details;
    LayoutInflater inflater;

    public StoreSpinnerAdapter(Context mContext, int viewResourceID, String[] datas, String[] details){
        super(mContext, viewResourceID, datas);

        this.mContext = mContext;
        this.viewResourceID = viewResourceID;
        this.datas = datas;
        this.details = details;

        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, boolean isDropDownView){
        View row = inflater.inflate(viewResourceID, parent, false);

        TextView txtTitle = (TextView) row.findViewById(R.id.item_store_spinner_title);
        TextView txtAddr = (TextView) row.findViewById(R.id.item_store_spinner_address);

        txtTitle.setText(datas[position]);
        txtAddr.setText(details[position]);

        if(isDropDownView){
            txtAddr.setVisibility(View.GONE);
        }else{
            if(position == 0) txtAddr.setVisibility(View.GONE);
            else txtAddr.setVisibility(View.VISIBLE);
        }

        return row;
    }
}
