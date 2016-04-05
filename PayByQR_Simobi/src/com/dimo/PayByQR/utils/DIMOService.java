package com.dimo.PayByQR.utils;

import android.content.Context;
import android.util.Log;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.AddressStore;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.model.PickupMethodData;
import com.dimo.PayByQR.QrStore.model.RespondJson;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceDetailResponse;
import com.dimo.PayByQR.model.InvoiceStatusResponse;
import com.dimo.PayByQR.model.LoginResponse;
import com.dimo.PayByQR.model.LoyaltyListResponse;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//import static com.dimo.PayByQR.QrStore.utility.QrStoreUtil.decodeBase64;
//import static com.dimo.PayByQR.QrStore.utility.QrStoreUtil.getImageFromString;


/**
 * Created by Rhio on 10/28/15.
 */
public class DIMOService {
    private final static String TAG = "DIMOService";
    public final static String METHOD_GET = "GET";
    public final static String METHOD_POST = "POST";

    public static void init(){
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    private static String getBaseUrl(Context ctx){
        String baseURL = ctx.getString(R.string.server_live);
        if(null != PayByQRProperties.getServerURLString() && PayByQRProperties.getServerURLString().length()>0){
            baseURL = PayByQRProperties.getServerURLString();
            if(PayByQRProperties.isDebugMode()) System.out.println("ServerURLString found: " + baseURL);
        }else {
            PayByQRSDK.ServerURL enumURL = PayByQRProperties.getServerURL();
            if (enumURL == PayByQRSDK.ServerURL.SERVER_URL_LIVE)
                baseURL = ctx.getString(R.string.server_live);
            else if (enumURL == PayByQRSDK.ServerURL.SERVER_URL_DEV)
                baseURL = ctx.getString(R.string.server_dev);
            else if (enumURL == PayByQRSDK.ServerURL.SERVER_URL_UAT)
                baseURL = ctx.getString(R.string.server_uat);
        }
        return baseURL;
    }

    public static String doLogin(Context ctx){
        String loginResponse = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userkey", PayByQRProperties.getUserAPIKey());
            params.put("pin", ctx.getString(R.string.default_pin));

            String url = getBaseUrl(ctx)+ctx.getString(R.string.url_path_login);
            loginResponse = getResponse(url, getQuery(params), METHOD_POST);
            if(PayByQRProperties.isDebugMode()) System.out.println("\ndoLogin response: " + loginResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return loginResponse;
    }

    public static String getInvoiceStatus(Context ctx, String invoiceID){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "getInvoiceStatus "+ invoiceID);
        String response = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userkey", PayByQRProperties.getUserAPIKey());
            params.put("invoiceId", invoiceID);

            String url = getBaseUrl(ctx)+ctx.getString(R.string.url_path_invoiceStatus);
            response = getResponse(url, getQuery(params), METHOD_POST);
            if(PayByQRProperties.isDebugMode()) System.out.println("\ngetInvoiceStatus response: " + response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getInvoiceDetail(Context ctx, String invoiceID, String amount){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "getInvoiceDetail "+ invoiceID);
        String response = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userkey", PayByQRProperties.getUserAPIKey());
            params.put("invoiceId", invoiceID);
            params.put("version", "2");
            if(null != amount) params.put("amount", amount);

            String url = getBaseUrl(ctx)+ctx.getString(R.string.url_path_invoiceDetail);
            response = getResponse(url, getQuery(params), METHOD_POST);
            if(PayByQRProperties.isDebugMode()) System.out.println("\ngetInvoiceDetail response: " + response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String joinLoyaltyProgram(Context ctx, String loyaltyProgramID){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "joinLoyaltyProgram "+ loyaltyProgramID);
        String response = null;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("loyaltyProgramId", loyaltyProgramID);

            String url = getBaseUrl(ctx)+ctx.getString(R.string.url_path_joinLoyaltyProgram);
            response = getResponse(url, getQuery(params), METHOD_POST);
            if(PayByQRProperties.isDebugMode()) System.out.println("\njoinLoyaltyProgram response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getLoyaltyList(Context ctx){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "getLoyaltyList ");
        String response = null;

        try {
            String url = getBaseUrl(ctx)+ctx.getString(R.string.url_path_getLoyaltyList);
            response = getResponse(url+"?withLogo=true", "", METHOD_POST);
            if(PayByQRProperties.isDebugMode()) System.out.println("\ngetLoyaltyList response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String getResponse(String strURL, String postParams, String method, boolean isQrStore){
        String stringResponse = null;
        try{
            URL url = new URL(strURL);
            HttpURLConnection conn;
            if(strURL.startsWith("https")){
                conn = (HttpsURLConnection) url.openConnection();
            }else{
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            if(PayByQRProperties.isDebugMode())
                System.out.println("\nSending " + method + " request to URL : " + url);
            if (isQrStore)
                conn.addRequestProperty("x-api-key", PayByQRProperties.getUserAPIKey());    //"y1LLATaIgrJDUTks5ffeeqZLYMyqMcpBlOsRzZ9b");

            if(method.equals("POST")){
                conn.setRequestProperty("Content-Length", postParams.length() + "");
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                if(PayByQRProperties.isDebugMode()) System.out.println("Post parameters : " + postParams);
            }


            int responseCode = conn.getResponseCode();
            if(PayByQRProperties.isDebugMode()) System.out.println("Response Code : " + responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                stringResponse = response.toString();
            }else{
                stringResponse = "";
            }
        } catch (SocketTimeoutException ste) {
            stringResponse = null;
            if(PayByQRProperties.isDebugMode()) Log.e(TAG, "STE : " + ste.toString());
            PayByQRSDK.getListener().callbackLostConnection();
        } catch (Exception e) {
            stringResponse = null;
            e.printStackTrace();
            if(PayByQRProperties.isDebugMode()) Log.e(TAG, "conn catch exeption error " + e.toString());
            PayByQRSDK.getListener().callbackLostConnection();
        }
        return stringResponse;
    }

    private static String getResponse(String strURL, String postParams, String method){
        String stringResponse = null;
        try{
            URL url = new URL(strURL);
            HttpURLConnection conn;
            if(strURL.startsWith("https")){
                conn = (HttpsURLConnection) url.openConnection();
            }else{
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(PayByQRProperties.isDebugMode())
                System.out.println("\nSending " + method + " request to URL : " + url);

            if(method.equals("POST")){
                conn.setRequestProperty("Content-Length", postParams.length() + "");
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                if(PayByQRProperties.isDebugMode()) System.out.println("Post parameters : " + postParams);
            }

            if(method.equals("GET")) {
                conn.addRequestProperty("x-api-key", PayByQRProperties.getUserAPIKey());// PayByQRProperties.getUserAPIKey()
            }

            int responseCode = conn.getResponseCode();
            if(PayByQRProperties.isDebugMode()) System.out.println("Response Code : " + responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                stringResponse = response.toString();
            }else{
                stringResponse = "";
            }
        } catch (SocketTimeoutException ste) {
            stringResponse = null;
            if(PayByQRProperties.isDebugMode()) Log.e(TAG, "STE : " + ste.toString());
            PayByQRSDK.getListener().callbackLostConnection();
        } catch (Exception e) {
            stringResponse = null;
            e.printStackTrace();
            if(PayByQRProperties.isDebugMode()) Log.e(TAG, "conn catch exeption error " + e.toString());
            PayByQRSDK.getListener().callbackLostConnection();
        }
        return stringResponse;
    }

    private static String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> pair : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static LoginResponse parseJSONLogin(Context ctx, String response) throws PayByQRException{
        // response NOK: result, errorMessage, errorCode (int), errorDetail (int)
        // response OK: {"result":"success","currency":"IDR","LastAddedUserKey":"87614c1a489fbc794d7ac2a515622566cb4c583f"}
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.result = object.optString("result", null);
                    loginResponse.currency = object.optString("currency", "IDR");
                    loginResponse.LastAddedUserKey = object.optString("LastAddedUserKey", null);
                    return loginResponse;
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_authentication), "");
        }
    }

    public static InvoiceDetailResponse parseJSONInvoiceDetail(Context ctx, String response) throws PayByQRException{
        //OK: {"result":"success","content":{"merchantApikey":"4a09b20eeff0d1aac5327878614d867866caa044","receiver":"David Samuel","invoiceId":"sTn98JaTqYyJ","status":"AVAILABLE_FOR_PAYMENT","hasLoyaltyCard":false,"permanentPercentageDiscount":0,"tipEnabled":false,"currency":"IDR","amount":50000,"couponList":[],"correctedInvoiceAmountWithPercentage":0,"fidelitizId":3075,"currentLoyaltyProgram":{},"comment":"No comment"}}
        //NOK: {"errorMessage":"ERROR_DURING_GET_INVOICE","result":"NOK","errorDetail":"INVOICE_NOT_FOUND","errorCode":3010}
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONObject detail = object.getJSONObject("content");
                    Gson gson = new Gson();
                    InvoiceDetailResponse invoiceDetailResponse = gson.fromJson(detail.toString(), InvoiceDetailResponse.class);

                    /*InvoiceModel invoiceModel = new InvoiceModel();
                    invoiceModel.invoiceID = detail.optString("invoiceId", null);
                    invoiceModel.amount = detail.optDouble("amount", 0);
                    invoiceModel.merchantName = detail.optString("receiver", null);
                    invoiceModel.currentLoyaltyProgram = parseJSONCurrentLoyaltyProgram(detail.getJSONObject("currentLoyaltyProgram").toString());
                    /*invoiceModel.discountedAmount = object.optDouble("");
                    invoiceModel.numberOfCoupons;
                    invoiceModel.discountType;
                    invoiceModel.loyaltyProgramName;
                    invoiceModel.amountOfDiscount;
                    invoiceModel.pointsRedeemed;
                    invoiceModel.amountRedeemed;*/

                    return invoiceDetailResponse;
                } else {
                    int errorCode = object.optInt("errorCode", -1);
                    String errorMessage = object.optString("errorMessage", null);
                    String errorDetail = object.optString("errorDetail", null);
                    if(null != errorMessage){
                        if(errorMessage.equals(Constant.ERROR_STRING_INVALID_QR)){
                            if(errorDetail.equals(Constant.ERROR_STRING_INVALID_QR_EXPIRED))
                                throw new PayByQRException(Constant.ERROR_CODE_INVALID_QR, ctx.getString(R.string.error_invalid_qr_detail_expired), "");
                            else if(errorDetail.equals(Constant.ERROR_STRING_INVALID_QR_NOT_FOUND))
                                throw new PayByQRException(Constant.ERROR_CODE_INVALID_QR, ctx.getString(R.string.error_invalid_qr_detail_notFound), "");
                            else if(errorDetail.equals(Constant.ERROR_STRING_INVALID_QR_PAID))
                                throw new PayByQRException(Constant.ERROR_CODE_INVALID_QR, ctx.getString(R.string.error_invalid_qr_detail_paid), "");
                            else
                                throw new PayByQRException(Constant.ERROR_CODE_INVALID_QR, ctx.getString(R.string.error_invalid_qr_detail_notFound), "");
                        }else
                            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
                    }else
                        throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static LoyaltyProgramModel parseJSONCurrentLoyaltyProgram(String response){
        LoyaltyProgramModel loyaltyProgramModel = null;
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONObject detail = object.getJSONObject("loyaltyProgram");
                    Gson gson = new Gson();
                    loyaltyProgramModel = gson.fromJson(detail.toString(), LoyaltyProgramModel.class);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return loyaltyProgramModel;
    }

    public static InvoiceStatusResponse parseJSONInvoiceStatus(Context ctx, String response) throws PayByQRException{
        // response NOK: result, errorMessage, errorCode (int), errorDetail (int)
        // response OK: {"result": "success","content": {"invoiceId": "7jFDD1cmbIN1","status": "PAID"}
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONObject detail = object.getJSONObject("content");
                    Gson gson = new Gson();
                    InvoiceStatusResponse invoiceStatusResponse = gson.fromJson(detail.toString(), InvoiceStatusResponse.class);
                    invoiceStatusResponse.rawJSON = detail.toString();
                    return invoiceStatusResponse;
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static int parseJSONJoinLoyaltyProgram(Context ctx, String response) throws PayByQRException{
        // response NOK: result, errorMessage, errorCode (int), errorDetail (int)
        // response OK: {"result": "success","loyaltyCardId": 29121}
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    int loyaltyCardId = object.optInt("loyaltyCardId", -1);
                    if(loyaltyCardId >= 0) return loyaltyCardId;
                    else throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_unknown), "");
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static ArrayList<LoyaltyListResponse> parseJSONLoyaltyList(Context ctx, String response) throws PayByQRException{
        // response NOK: result, errorMessage, errorCode (int), errorDetail (int)
        // response OK: {"result": "success","loyaltyProgram": []
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONArray detail = object.getJSONArray("loyaltyPrograms");
                    Gson gson = new Gson();

                    ArrayList<LoyaltyListResponse> loyaltyListResponses = new ArrayList<LoyaltyListResponse>();
                    for(int i=0 ; i< detail.length() ; i++){
                        LoyaltyListResponse loyaltyListResponse = gson.fromJson(detail.get(i).toString(), LoyaltyListResponse.class);
                        loyaltyListResponse.rawJSON = detail.get(i).toString();
                        loyaltyListResponses.add(loyaltyListResponse);
                    }
                    return loyaltyListResponses;
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    /*================== QR STORE  ================*/

    public static String getCartDetail(String URL){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "getCartDetail "+ URL);
        String response = getResponse(URL, null, METHOD_GET, true);
        if(PayByQRProperties.isDebugMode()) System.out.println("\ngetCartDetail response: " + response);

        return response;
    }

    public static String getQrAddress(String URL){
        String response = getResponse(URL, null, METHOD_GET, true);
        if(PayByQRProperties.isDebugMode()) System.out.println("\ngetQrAddress response: " + response);

        return response;
    }

    public static String getPickupMethod(String URL, String merchantCode){
        if(PayByQRProperties.isDebugMode()) Log.d(TAG, "getCartDetail "+ URL + " " + merchantCode);
        String response = getResponse(URL+"pickupMethod/"+merchantCode, null, METHOD_GET, true);
        if(PayByQRProperties.isDebugMode()) System.out.println("\ngetPickupMethod response: " + response);

        return response;
    }

    public static String  getQrGeneric(String URL, String param, int isCheckout){
        String  response = null;
        String url = URL;

        switch (isCheckout) {
            case QrStoreDefine.PASS_CHEKCOUT:
                url = url.concat("checkout");
                break;
            case QrStoreDefine.PASS_SHIPPING:
                url = url.concat("shipping");
                break;
            case QrStoreDefine.PASS_PAID:
                url = url.concat("paid");
                break;
        }

        response = getResponse(url, param, METHOD_POST, true);
        if(PayByQRProperties.isDebugMode()) System.out.println("\ngetShiping_Checkout response: " + response);

        return response;
    }

    public  static ArrayList<AddressStore> parseQrAddress(Context ctx, String response)throws PayByQRException {
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONArray stores = object.getJSONArray("stores");
                    Gson gson = new Gson();
                    ArrayList<AddressStore> addressStores = new ArrayList<>();
                    for(int i=0;i<stores.length();i++) {
                        AddressStore store = gson.fromJson(stores.get(i).toString(), AddressStore.class);
                        addressStores.add(store);
                    }
                    return addressStores;
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static ArrayList<PickupMethodData> parseJSONPickupMethod(Context ctx, String response) throws PayByQRException {
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONArray method = object.getJSONArray("method");
                    Gson gson = new Gson();
                    ArrayList<PickupMethodData> pickupMethodDatas = new ArrayList<>();
                    for(int i=0;i<method.length();i++) {
                        PickupMethodData pickupMethodData = gson.fromJson(method.get(i).toString(), PickupMethodData.class);
                        pickupMethodDatas.add(pickupMethodData);
                    }
                    return pickupMethodDatas;
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static String parseQrPayment(Context ctx, String response)throws PayByQRException {
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                return object.optString("result", "N/A");
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static String parseQrCheckout(Context ctx, String response)throws PayByQRException {
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    return object.optString("invoiceId", "");
                } else {
                    if(object.has("error")){
                        throw new PayByQRException(Constant.ERROR_CODE_OUT_OF_STOCK, object.optString("error", ctx.getString(R.string.error_unknown)),
                                object.optString("errorDetail", ""));
                    }else {
                        throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                                object.optString("errorDetail", ""));
                    }
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public  static int parseQrShippingFee(Context ctx, String response)throws PayByQRException {
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONObject detail = object.getJSONObject("contents");
                    return detail.optInt("shippingFee", 0);
                } else {
                    throw new PayByQRException(object.optInt("errorCode", 0), object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static GoodsData parseJSONGoodsDetail(Context ctx, String response) throws PayByQRException{
        try {
            if(null != response) {
                JSONObject object = new JSONObject(response);
                if (object.has("result") && object.getString("result").equals("success")) {
                    JSONObject detail = object.getJSONObject("contents");
                    Gson gson = new Gson();

                    GoodsData goodsData = gson.fromJson(detail.toString(), GoodsData.class);
                    return goodsData;
                } else {
                    throw new PayByQRException(Constant.ERROR_CODE_INVALID_GOODS, object.optString("errorMessage", ctx.getString(R.string.error_unknown)),
                            object.optString("errorDetail", ""));
                }
            }else{
                throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_UNKNOWN_ERROR, ctx.getString(R.string.error_JSON_parser), "");
        }
    }

    public static Cart parseSingleFeed(Context ctx, String content, String invoice) throws PayByQRException {
        if(PayByQRProperties.isDebugMode()) {
          //  Log.d(TAG, "parseSingleFeed1 " + content);
            Log.d(TAG, "parseSingleFeed2 " + invoice);
            Log.d(TAG, "content " + content);
        }
        try {
            if (null != content) {
                JSONObject object = new JSONObject(content);

                Cart cart = new Cart();
                if (object.has("result") && object.getString("result").equals(QrStoreDefine.RESPOND_STORE_JSON_OK)) {
                    JSONObject detail = new JSONObject (object.getJSONObject("contents").toString());
                    cart.setInvoiceId(invoice);
                    cart.setDetailQuantity(1);
                    cart.setId(detail.getString("id"));
                    cart.setGoodsName(detail.getString("goodsName"));
                    cart.setPrice(detail.getInt("price"));
                    cart.setDiscountAmount(detail.getInt("discountAmount"));
                    cart.setMaxQuantity(detail.getInt("maxQuantity"));
                    cart.setStock(detail.getInt("stock"));
                    if (detail.has("merchantCode"))
                       cart.setMerchantCode(detail.getString("merchantCode"));
                    else
                    {
                        String[] splits = invoice.split("/");
                        String datainvoice = splits[splits.length-1];
                        cart.setMerchantCode(datainvoice.substring(0,10));

                    }
                    cart.setMerchantName(detail.getString("merchantName"));

                    cart.setWeigth(detail.getInt("weight"));
                    cart.setImageUrl(detail.getString("image_url"));
                    cart.setImageUrlmerchant(detail.getString("merchantImage"));


                    cart.setDetailDescription(detail.getString("description"));



                    return cart;
                } else if (object.has("result") && object.getString("result").equals(QrStoreDefine.RESPOND_STORE_JSON_KO)) {
                    JSONObject detail2 = new JSONObject (object.getJSONObject("contents").toString());
                    cart.setDetailDescription("NOK" + detail2.optString("message", ""));
                    return cart;

                }
                //else {
                 //   throw new PayByQRException(Constant.ERROR_CODE_CONNECTION, ctx.getString(R.string.error_connection_message), ctx.getString(R.string.error_connection_detail));
               // }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new PayByQRException(Constant.ERROR_CODE_JSON_EXCEPTION, ctx.getString(R.string.error_JSON_parser), "");
        }

        return null;
    }




}
