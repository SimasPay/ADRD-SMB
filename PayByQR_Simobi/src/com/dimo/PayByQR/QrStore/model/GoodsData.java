package com.dimo.PayByQR.QrStore.model;

import android.util.Log;

import com.dimo.PayByQR.PayByQRProperties;

import org.json.JSONObject;

/**
 * Created by Rhio on 3/14/16.
 */
public class GoodsData {
    public String id;
    public double price;
    public double discountAmount;
    public String goodsName;
    public String description;
    public int maxQuantity;
    public int qtyInCart;
    public int weight;
    public int stock;
    public String image_url;

    public String merchantImage;
    public String merchantName;
    public String merchantCode;
    public String merchantURL;

    public void printLogData(){
        if(PayByQRProperties.isDebugMode()) {
            Log.d("Goods Data", "id: " + id +
                                "\ngoodsName: " + goodsName +
                                "\nprice: " + price +
                                "\ndiscountAmount: " + discountAmount +
                                "\ndescription: " + description +
                                "\nqtyInCart: " + qtyInCart +
                                "\nmaxQuantity: " + maxQuantity +
                                "\nweight: " + weight +
                                "\nstock: " + stock +
                                "\nimage_url: " + image_url +
                                "\nmerchantImage: " + merchantImage +
                                "\nmerchantName: " + merchantName +
                                "\nmerchantURL: " + merchantURL +
                                "\nmerchantCode: " + merchantCode);
        }
    }

    public JSONObject toJSONObject(){
        JSONObject content = new JSONObject();
        try {
            content.put("id", id);
            content.put("goodsName", goodsName);
            content.put("price", price);
            content.put("discountAmount", discountAmount);
            content.put("description", description);
            content.put("qtyInCart", qtyInCart);
            content.put("maxQuantity", maxQuantity);
            content.put("weight", weight);
            content.put("stock", stock);
            content.put("image_url", image_url);

            content.put("merchantCode", merchantCode);
            content.put("merchantName", merchantName);
            content.put("merchantImage", merchantImage);
            content.put("merchantURL", merchantURL);
        }catch (Exception e){
            e.printStackTrace();
        }

        return content;
    }

    public String toJSONString(){
        return toJSONObject().toString();
    }
}