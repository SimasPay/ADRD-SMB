package com.dimo.PayByQR.QrStore.model;

import android.util.Log;

import com.dimo.PayByQR.PayByQRProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rhio on 3/14/16.
 */
public class CartData implements Serializable{
    public String merchantImage;
    public String merchantName;
    public String merchantCode;
    public String merchantURL;
    public ArrayList<GoodsData> carts;
    public int totalAmount;

    public void printLogData(){
        if(PayByQRProperties.isDebugMode()) {
            String log = "merchantImage: " + merchantImage +
                        "\nmerchantName: " + merchantName +
                        "\nmerchantURL: " + merchantURL +
                        "\nmerchantCode: " + merchantCode +
                        "\ntotalAmount: " + totalAmount;
            Log.d("CART Datas", log);
            if(carts.size() > 0){
                for (int i = 0; i<carts.size();i++){
                    Log.d("CART Datas", "\nCARTS "+i+": ");
                    carts.get(i).printLogData();
                }
            }
        }
    }

    public JSONObject toJSONObject(){
        JSONObject content = new JSONObject();
        try {
            content.put("merchantCode", merchantCode);
            content.put("merchantName", merchantName);
            content.put("merchantImage", merchantImage);
            content.put("merchantURL", merchantURL);

            JSONArray jsonArrayCart = new JSONArray();
            for (int i = 0; i < carts.size(); i++) {
                jsonArrayCart.put(carts.get(i).toJSONObject());
            }
            content.put("carts", jsonArrayCart);

            content.put("totalAmount", totalAmount);
        }catch (Exception e){
            e.printStackTrace();
        }

        return content;
    }

    public String toJSONString(){
        return toJSONObject().toString();
    }
}
