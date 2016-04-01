package com.dimo.PayByQR;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.dimo.PayByQR.activity.SplashActivity;
import com.dimo.PayByQR.model.InvoiceModel;

/**
 * Created by Rhio on 10/27/15.
 */
public interface PayByQRSDKListener {
    public Fragment callbackShowEULA();
    public void callbackEULAStateChanged(boolean state);
    public void callbackGenerateUserAPIKey(UserAPIKeyListener generateUserAPIKeyHandler);
    public boolean callbackInvalidQRCode();
    public void callbackUserHasCancelTransaction();
    public void callbackPayInvoice(InvoiceModel invoiceModel);
    public boolean callbackTransactionStatus(int code, String description);
    public void callbackShowDialog(Context context, int code, String description);
    public void callbackSDKClosed();
    public void callbackLostConnection();
    public boolean callbackUnknowError();
    public void callbackAuthenticationError();
}
