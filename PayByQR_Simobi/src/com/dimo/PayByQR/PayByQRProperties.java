package com.dimo.PayByQR;

import android.content.Context;

/**
 * Created by Rhio on 10/27/15.
 */
public class PayByQRProperties {
    private static boolean EULAState;
    private static String UserAPIKey;
    private static boolean isDebugMode = true;  // TODO: 1/14/16 Don't forget to set this value to false on release
    private static boolean isPolling;
    private static boolean isUsingCustomDialog;
    private static boolean isUsingLoyalty;
    private static boolean isUsingTip;
    private static PayByQRSDK.ServerURL serverURL;
    private static String serverURLString;
    private static Context SDKContext;
    private static LoginState loginState;
    private static int minimumTransaction;

    public enum LoginState {
        NOT_AUTHENTICATE,
        NOT_AUTHENTICATE_NO_CONNECTION,
        GETTING_USER_KEY,
        LOGGING_IN,
        LOGGED_IN
    }

    public static boolean getEULAState() {
        return PayByQRProperties.EULAState;
    }

    public static void setEULAState(boolean EULAState) {
        PayByQRProperties.EULAState = EULAState;
        PayByQRSDK.getListener().callbackEULAStateChanged(getEULAState());
    }

    public static String getUserAPIKey() {
        return UserAPIKey;
    }

    public static void setUserAPIKey(String userAPIKey) {
        UserAPIKey = userAPIKey;
    }

    public static boolean isDebugMode() {
        return isDebugMode;
    }

    public static boolean isPolling() {
        return isPolling;
    }

    public static void setIsPolling(boolean isPolling) {
        PayByQRProperties.isPolling = isPolling;
    }

    public static boolean isUsingCustomDialog() {
        return isUsingCustomDialog;
    }

    public static void setIsUsingCustomDialog(boolean isUsingCustomDialog) {
        PayByQRProperties.isUsingCustomDialog = isUsingCustomDialog;
    }

    public static PayByQRSDK.ServerURL getServerURL() {
        return serverURL;
    }

    public static void setServerURL(PayByQRSDK.ServerURL serverURL) {
        PayByQRProperties.serverURL = serverURL;
    }

    public static boolean isUsingLoyalty() {
        return isUsingLoyalty;
    }

    public static void setIsUsingLoyalty(boolean isUsingLoyalty) {
        PayByQRProperties.isUsingLoyalty = isUsingLoyalty;
    }

    public static boolean isUsingTip() {
        return isUsingTip;
    }

    public static void setIsUsingTip(boolean isUsingTip) {
        PayByQRProperties.isUsingTip = isUsingTip;
    }

    public static String getServerURLString() {
        return serverURLString;
    }

    public static void setServerURLString(String serverURLString) {
        PayByQRProperties.serverURLString = serverURLString;
    }

    public static Context getSDKContext() {
        return SDKContext;
    }

    public static void setSDKContext(Context SDKContext) {
        PayByQRProperties.SDKContext = SDKContext;
    }

    public static LoginState getLoginState() {
        return loginState;
    }

    public static void setLoginState(LoginState loginState) {
        PayByQRProperties.loginState = loginState;
    }

    public static int getMinimumTransaction() {
        return minimumTransaction;
    }

    public static void setMinimumTransaction(int minimumTransaction) {
        PayByQRProperties.minimumTransaction = minimumTransaction;
    }

}
