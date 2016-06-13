package com.dimo.PayByQR.QrStore.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dimo.PayByQR.QrStore.model.CartData;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rhio on 3/14/16.
 */
public class QRStoreDBUtil {
    public static final String QR_STORE_DB = "com.dimo.PayByQR.QRStore.DB";

    public static String getStringPreferenceValue(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(QR_STORE_DB, null);
    }

    public static void saveStringPreferenceValue(Context context, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QR_STORE_DB, value);
        editor.apply();
    }

    public static ArrayList<GoodsData> getAllCarts(Context context){
        ArrayList<GoodsData> carts = new ArrayList<>();
        String jsonString = getStringPreferenceValue(context);
        if(null != jsonString){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                Gson gson = new Gson();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GoodsData goodsData = gson.fromJson(jsonObject.toString(), GoodsData.class);
                    carts.add(goodsData);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return carts;
    }

    public static ArrayList<CartData> getAllMerchantCarts(Context context, boolean withCartList){
        ArrayList<CartData> merchantCarts = new ArrayList<>();
        HashMap<String, CartData> merchantHashMap = new HashMap<>();
        String jsonString = getStringPreferenceValue(context);
        if(null != jsonString){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                Gson gson = new Gson();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GoodsData goodsData = gson.fromJson(jsonObject.toString(), GoodsData.class);

                    CartData cartData = new CartData();
                    cartData.merchantCode = goodsData.merchantCode;
                    cartData.merchantName = goodsData.merchantName;
                    cartData.merchantImage = goodsData.merchantImage;
                    cartData.merchantURL = goodsData.merchantURL;
                    merchantHashMap.put(goodsData.merchantCode, cartData);
                }

                if(withCartList){
                    ArrayList<CartData> merchantCartsTemp = new ArrayList<>(merchantHashMap.values());
                    for(int i=0;i<merchantCartsTemp.size();i++){
                        CartData cartData = getCartsByMerchant(context, merchantCartsTemp.get(i).merchantCode);
                        merchantCarts.add(cartData);
                    }
                }else{
                    merchantCarts.addAll(merchantHashMap.values());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return merchantCarts;
    }

    public static void saveAllCart(Context context, ArrayList<GoodsData> goodsDatas){
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<goodsDatas.size();i++){
            jsonArray.put(goodsDatas.get(i).toJSONObject());
        }

        saveStringPreferenceValue(context, jsonArray.toString());
    }

    public static void addGoodsToCart(Context context, GoodsData goodsData){
        ArrayList<GoodsData> carts = getAllCarts(context);
        int pos = getGoodsPositionInCart(context, goodsData);
        if(pos >= 0){
            carts.set(pos, goodsData);
        }else {
            carts.add(goodsData);
        }

        saveAllCart(context, carts);
    }

    public static GoodsData getGoodsFromCart(Context context, String goodsID, String merchantCode){
        GoodsData goodsData = null;
        ArrayList<GoodsData> carts = getAllCarts(context);
        for (int i=0;i<carts.size();i++){
            if(carts.get(i).id.equals(goodsID) && carts.get(i).merchantCode.equals(merchantCode)){
                goodsData = carts.get(i);
                break;
            }
        }
        return goodsData;
    }

    public static int getGoodsPositionInCart(Context context, GoodsData goodsData){
        int isGoodsInCart = -1;
        ArrayList<GoodsData> carts = getAllCarts(context);
        for (int i=0;i<carts.size();i++){
            if(carts.get(i).id.equals(goodsData.id) && carts.get(i).merchantCode.equals(goodsData.merchantCode)){
                isGoodsInCart = i;
                break;
            }
        }
        return isGoodsInCart;
    }

    public static CartData getCartsByMerchant(Context context, String merchantID){
        CartData cartData = new CartData();
        cartData.merchantCode = merchantID;
        cartData.totalAmount = 0;

        ArrayList<GoodsData> carts = new ArrayList<>();
        String jsonString = getStringPreferenceValue(context);
        if(null != jsonString){
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                Gson gson = new Gson();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GoodsData goodsData = gson.fromJson(jsonObject.toString(), GoodsData.class);
                    if(merchantID.equals(goodsData.merchantCode)) {
                        carts.add(goodsData);
                        cartData.totalAmount += goodsData.qtyInCart * (int) (goodsData.price - goodsData.discountAmount);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        cartData.carts = carts;
        if(carts.size()>0){
            cartData.merchantName = carts.get(0).merchantName;
            cartData.merchantImage = carts.get(0).merchantImage;
            cartData.merchantURL = carts.get(0).merchantURL;
        }

        return cartData;
    }

    public static boolean removeGoodsFromCartsByMerchant(Context context, String goodsID, String merchantID){
        boolean isDeleted = false;

        ArrayList<GoodsData> carts = getAllCarts(context);
        for(int i=0;i<carts.size();i++){
            if(carts.get(i).merchantCode.equals(merchantID) && carts.get(i).id.equals(goodsID)){
                carts.remove(i);
                isDeleted = true;
                break;
            }
        }

        saveAllCart(context, carts);

        return isDeleted;
    }

    public static int removeAllCartsByMerchant(Context context, String merchantID){
        int deletedItem = 0;

        ArrayList<GoodsData> newCart = new ArrayList<>();
        ArrayList<GoodsData> carts = getAllCarts(context);
        for(int i=0;i<carts.size();i++){
            if(!carts.get(i).merchantCode.equals(merchantID)){
                newCart.add(carts.get(i));
            }else{
                deletedItem++;
            }
        }

        saveAllCart(context, newCart);

        return deletedItem;
    }

}
