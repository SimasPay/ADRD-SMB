package com.dimo.PayByQR.QrStore.utility;

import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.model.GoodsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.List;


import static com.dimo.PayByQR.utils.DIMOUtils.formatAmount;

/**
 * Created by dimo on 12/17/15.
 */
public class QrStoreUtil {

    public static Bitmap decodeFromString(String s) {
        try {
            byte[] decodedByte = android.util.Base64.decode(s.getBytes(), 0);
            InputStream f = new ByteArrayInputStream(decodedByte);
            if (PayByQRProperties.isDebugMode())
                Log.e("LOg IMAGE","1"+ decodedByte.length + "");

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            Bitmap m = BitmapFactory.decodeStream(f, null, o);

            if (PayByQRProperties.isDebugMode())
            Log.e("LOg IMAGE", "1A" + m);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            if (PayByQRProperties.isDebugMode()) Log.e("LOg IMAGE", "1B"+ o2.inSampleSize);
                    Bitmap m2=BitmapFactory.decodeStream(f, null, o2);
            if (PayByQRProperties.isDebugMode()) Log.e("LOg IMAGE", "1C"+ m2);
            return m2;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //  return bitmap;
    }

    public static String getStringPayment(String invoiceId, String merchantCode, String transId, String status) {
        try {
            JSONObject ji = new JSONObject();
            ji.put("invoiceId", invoiceId);
            ji.put("merchantCode", merchantCode);
            ji.put("transId", transId);
            ji.put("status", status);
            return ji.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStringJson(ArrayList<GoodsData> cartList, String customerName,
                                       String email, String address,
                                       String zipCode, String city,
                                       String PhoneNum, String MerchantCode,
                                       String transId, long totalAmount,
                                       String pickupMethodID, String pickupContentID) {
        try {
            JSONObject ji = new JSONObject();
            ji.put("merchantCode", MerchantCode);
            if (totalAmount>0)
               ji.put("totalAmount", totalAmount);

            ji.put("transId", transId);
            if(PayByQRProperties.isDebugMode()) Log.d("ji put transID ", transId);

            JSONObject je = new JSONObject();
            je.put("name", customerName);
            je.put("email", email);
            je.put("address", address);
            je.put("zipCode",zipCode);
            je.put("city",city);
            je.put("phoneNum",PhoneNum);
            ji.put("customerDetail",je);

            JSONArray ja = new JSONArray();
            for (int i = 0; i < cartList.size(); i++) {
                GoodsData goodsData = cartList.get(i);
                JSONObject jo = new JSONObject();
                jo.put("sku", goodsData.id);
                jo.put("quantity", goodsData.qtyInCart);
                ja.put(jo);
            }
            ji.put("items", ja);

            // OLD
            /*if (pickupMethod != null) {
                JSONObject pickup = new JSONObject();
                pickup.put("method", pickupMethod);
                pickup.put("storeId", pickupStoreId);
                ji.put("pickup",pickup);
            }*/
            JSONObject jsonPickup = new JSONObject();
            jsonPickup.put("methodId", pickupMethodID);
            jsonPickup.put("id", pickupContentID);
            ji.put("pickup", jsonPickup);

            return ji.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTransId() {
        long longtime = System.currentTimeMillis();
        String strtime = String.valueOf(longtime);
        byte bytes[] = strtime.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        long checksumValue = checksum.getValue();
        return String.valueOf(checksumValue);
    }

    public static String displayMenu(int detail, long price, boolean isdetail) {
        String ret = "";
        if (isdetail) {
            ret = String.valueOf(detail)+" x " +"Rp. "+ formatAmount(String.valueOf(price));
        } else {
            long sum = detail*price;
            ret = "Rp. "+ formatAmount(String.valueOf(sum));
        }
        return ret;
    }

    public static String displayMenu(int detail, int price, boolean isdetail) {
        String ret = "";
        if (isdetail) {
            ret = String.valueOf(detail)+" x " +"Rp. "+ formatAmount(String.valueOf(price));
        } else {
            long sum = detail*price;
            ret = "Rp. "+ formatAmount(String.valueOf(sum));
        }
        return ret;
    }

    public static String getUrlMerchant(String urlori) {
        String[] splits = urlori.split("/");
        String datainvoice = splits[splits.length-1];
        String second= urlori.replace(datainvoice,"");
        return second.replace("item/","");
    }

    public static boolean isNullContent(String input) {
        if (input==null)
            return false;
        if( (input.length()==0) || input.isEmpty() || (input==null) || (input.equals("null")))
            return true;

        return false;
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static boolean isValidInput(String input, int lenMIn, int lenmax) {
        if (input.length()>lenMIn && input.length()<=lenmax )
             return true;
        return false;

    }

    public static  String setLimitMultiRow(String input  , int limitmax) {
        StringBuilder a = new StringBuilder();
        String []at= input.split(" ");
        int j=1;
        for (int i =0; i < at.length ; i++) {
            a.append(at[i]);
            a.append(" ");
            if (a.toString().length() >=(limitmax*j)) {
                a.append("\n");
                j++;
            }
        }
        return a.toString();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean isWifiAvailable (Context context) {
        boolean br = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }

    public static boolean isMobileAvailable (Context context) {
        boolean br= false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_MOBILE));

        return br;
    }

    public static  boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                if (activeNetwork.isConnected())
                    haveConnectedWifi = true;

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                if (activeNetwork.isConnected())
                    haveConnectedMobile = true;
            }
        } else {
            // not connected to the internet
            return false;
        }

        return haveConnectedWifi || haveConnectedMobile;
        //  return (isMobileAvailable(context)||isWifiAvailable(context));
    }
}
