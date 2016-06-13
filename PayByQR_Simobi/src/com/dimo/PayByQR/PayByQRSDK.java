package com.dimo.PayByQR;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.dimo.PayByQR.activity.SplashActivity;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOService;

/**
 * Created by Rhio on 10/27/15.
 */
public class PayByQRSDK {
    private static PayByQRSDKListener listener;
    private Context context;

    public static final int MODULE_PAYMENT = 1;
    public static final int MODULE_LOYALTY = 2;
    public static final int MODULE_QRSTORE = 4;
    public static final int MODULE_IN_APP = 3;

    private static int module;

    public enum ServerURL {
        SERVER_URL_DEV,
        SERVER_URL_UAT,
        SERVER_URL_LIVE
    }

    public enum SDKLocale {
        ENGLISH,
        INDONESIAN
    }

    public PayByQRSDK(Context context, PayByQRSDKListener listener){
        this.context = context;
        PayByQRSDK.listener = listener;
        PayByQRProperties.setSDKContext(context);
        PayByQRProperties.setEULAState(PayByQRPreference.readEULAStatePrefs(context));
        //PayByQRProperties.setUserAPIKey(PayByQRPreference.readUserAPIKeyPrefs(context));
        PayByQRProperties.setIsPolling(true);
        PayByQRProperties.setIsUsingCustomDialog(false);
        PayByQRProperties.setIsUsingLoyalty(true);
        PayByQRProperties.setIsUsingTip(true);
        PayByQRProperties.setServerURL(ServerURL.SERVER_URL_UAT);
        PayByQRProperties.setMinimumTransaction(0);
        PayByQRProperties.setSDKLocale(SDKLocale.INDONESIAN);

        DIMOService.init();
    }

    public void startSDK(int module){
        startSDK(module, null, null);
    }

    public void startSDK(int module, String invoiceID, String urlCallback){
        if(listener != null){
            PayByQRSDK.module = module;
            Intent intent = new Intent(context, SplashActivity.class);
            intent.putExtra(Constant.INTENT_EXTRA_MODULE, module);
            if(null!=invoiceID) intent.putExtra(Constant.INTENT_EXTRA_INVOICE_ID, invoiceID);
            if(null!=urlCallback) intent.putExtra(Constant.INTENT_EXTRA_INAPP_URL_CALLBACK, urlCallback);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, R.string.error_SDK_no_listener, Toast.LENGTH_SHORT).show();
        }
    }

    public void closeSDK(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constant.BROADCAST_ACTION_CLOSE_SDK);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }

    public static int getModule(){
        return module;
    }

    public static PayByQRSDKListener getListener(){
        return listener;
    }

    public void setServerURL(ServerURL serverURL){
        PayByQRProperties.setServerURL(serverURL);
    }

    public void setSDKLocale(SDKLocale locale){
        PayByQRProperties.setSDKLocale(locale);
    }

    public boolean getEULAState(){
        return PayByQRPreference.readEULAStatePrefs(context);
    }

    public void setEULAState(boolean eulaState){
        PayByQRPreference.saveEULAStatePref(context, eulaState);
        PayByQRProperties.setEULAState(PayByQRPreference.readEULAStatePrefs(context));
    }

    public String getUserAPIKey(){
        return PayByQRProperties.getUserAPIKey();
    }

    public void setUserAPIKey(String userAPIKey){
        //PayByQRPreference.saveUserAPIKeyPref(context, userAPIKey);
        PayByQRProperties.setUserAPIKey(userAPIKey);
    }

    public void resetUserAPIKey(){
        PayByQRPreference.saveEULAStatePref(context, false);
        //PayByQRPreference.saveUserAPIKeyPref(context, null);

        PayByQRProperties.setEULAState(PayByQRPreference.readEULAStatePrefs(context));
        PayByQRProperties.setUserAPIKey(null);
    }

    public boolean isPolling(){
        return PayByQRProperties.isPolling();
    }

    public void setIsPolling(boolean isPolling){
        PayByQRProperties.setIsPolling(isPolling);
    }

    public boolean isUsingCustomDialog(){
        return PayByQRProperties.isUsingCustomDialog();
    }

    public void setIsUsingCustomDialog(boolean isUsingCustomDialog){
        PayByQRProperties.setIsUsingCustomDialog(isUsingCustomDialog);
    }

    /*public boolean isUsingLoyalty(){
        return PayByQRProperties.isUsingLoyalty();
    }

    public void setIsUsingLoyalty(boolean isUsingLoyalty){
        PayByQRProperties.setIsUsingLoyalty(isUsingLoyalty);
    }

    public boolean isUsingTip(){
        return PayByQRProperties.isUsingTip();
    }

    public void setIsUsingTip(boolean isUsingTip){
        PayByQRProperties.setIsUsingTip(isUsingTip);
    }*/

    public int getMinimumTransaction() {
        return PayByQRProperties.getMinimumTransaction();
    }

    public void setMinimumTransaction(int minimumTransaction) {
        PayByQRProperties.setMinimumTransaction(minimumTransaction);
    }

    public void notifyTransaction(int code, String message, boolean isDefaultLayout){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constant.BROADCAST_ACTION_NOTIFY_TRX);
        broadcastIntent.putExtra(Constant.INTENT_EXTRA_NOTIFY_CODE, code);
        broadcastIntent.putExtra(Constant.INTENT_EXTRA_NOTIFY_DESC, message);
        broadcastIntent.putExtra(Constant.INTENT_EXTRA_NOTIFY_LAYOUT, isDefaultLayout);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }
}
