package com.dimo.PayByQR.data;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDKListener;

/**
 * Created by Rhio on 10/27/15.
 */
public class Constant {
    public static final int STATUS_CODE_PAYMENT_SUCCESS = 0;
    public static final int ERROR_CODE_CONNECTION = 11;
    public static final int ERROR_CODE_PAYMENT_FAILED = 12;
    public static final int ERROR_CODE_INVALID_QR = 13;
    public static final int ERROR_CODE_UNKNOWN_ERROR = 14;
    public static final int ERROR_CODE_TIME_OUT = 15;
    public static final int ERROR_CODE_AUTHENTICATION = 16;
    public static final int ERROR_CODE_JSON_EXCEPTION=17;
    public static final int ERROR_CODE_JSON_SHIPPING=18;
    public static final int ERROR_CODE_JSON_CHECKOUT=19;
    public static final int ERROR_CODE_JSON_ADRESS=20;
    public static final int ERROR_CODE_INVALID_GOODS = 21;
    public static final int ERROR_CODE_OUT_OF_STOCK = 22;

    public static final int ACTIVITY_RESULT_CLOSE_SDK = 100;
    public static final int ACTIVITY_RESULT_NO_CONNECTION = 102;
    public static final int PERMISSIONS_REQUEST_CAMERA = 103;
    public static final int ACTIVITY_RESULT_QRSTORE_CART = 104;
    public static final int ACTIVITY_REQUEST_QRSTORE_PRODUCT_DETAIL = 105;
    public static final int ACTIVITY_REQUEST_QRSTORE_MERCHANT_LIST = 106;
    public static final int ACTIVITY_RESULT_QRSTORE_CHECKOUT_ERROR = 107;

    public static final int REQUEST_CODE_ERROR_INVALID_QR = 200;
    public static final int REQUEST_CODE_ERROR_CONNECTION = 201;
    public static final int REQUEST_CODE_ERROR_PAYMENT_FAILED = 202;
    public static final int REQUEST_CODE_ERROR_UNKNOWN = 203;
    public static final int REQUEST_CODE_ERROR_TIME_OUT = 205;
    public static final int REQUEST_CODE_ERROR_AUTHENTICATION = 206;
    public static final int REQUEST_CODE_ERROR_MINIMUM_TRX = 207;
    public static final int REQUEST_CODE_ERROR_JSON = 208;
    public static final int REQUEST_CODE_ERROR_QRSTORE = 209;


    public static final int MESSAGE_END_OK = 300;
    public static final int MESSAGE_END_ERROR = 301;
    public static final int MESSAGE_END_TIME_OUT = 302;

    public static final String ERROR_STRING_INVALID_QR = "ERROR_DURING_GET_INVOICE";
    public static final String ERROR_STRING_INVALID_QR_EXPIRED = "MESSAGE_INVOICE_EXPIRED";
    public static final String ERROR_STRING_INVALID_QR_PAID = "INVOICE_NO_LONGER_VALID";
    public static final String ERROR_STRING_INVALID_QR_NOT_FOUND = "INVOICE_NOT_FOUND";

    public static final String BROADCAST_ACTION_CLOSE_SDK = "com.dimo.PayByQR.closeSDK";
    public static final String BROADCAST_ACTION_NOTIFY_TRX = "com.dimo.PayByQR.notifyTrx";
    public static final String INTENT_EXTRA_MODULE = "com.dimo.PayByQR.intent.extra.module";
    public static final String INTENT_EXTRA_INVOICE_ID = "com.dimo.PayByQR.intent.extra.invoiceID";
    public static final String INTENT_EXTRA_INAPP_URL_CALLBACK = "com.dimo.PayByQR.intent.extra.inAppURLCallback";
    public static final String INTENT_EXTRA_IS_CLOSE_SDK = "com.dimo.PayByQR.intent.extra.isCloseSDK";
    public static final String INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG = "com.dimo.PayByQR.intent.extra.isShowCustomDialog";
    public static final String INTENT_EXTRA_CUSTOM_DIALOG_CODE = "com.dimo.PayByQR.intent.extra.customDialogCode";
    public static final String INTENT_EXTRA_CUSTOM_DIALOG_DESC = "com.dimo.PayByQR.intent.extra.customDialogDesc";
    public static final String INTENT_EXTRA_NOTIFY_CODE = "com.dimo.PayByQR.intent.extra.notifyCode";
    public static final String INTENT_EXTRA_NOTIFY_DESC = "com.dimo.PayByQR.intent.extra.notifyDesc";
    public static final String INTENT_EXTRA_ERROR_HEADER = "com.dimo.PayByQR.intent.extra.errorHeader";
    public static final String INTENT_EXTRA_ERROR_DETAIL = "com.dimo.PayByQR.intent.extra.errorDetail";
    public static final String INTENT_EXTRA_REQUEST_CODE = "com.dimo.PayByQR.intent.extra.requestCode";
    public static final String INTENT_EXTRA_EULA_URL = "com.dimo.PayByQR.intent.extra.EULAUrl";
    public static final String INTENT_EXTRA_MERCHANT_NAME = "com.dimo.PayByQR.intent.extra.merchantName";
    public static final String INTENT_EXTRA_DISCOUNT_TYPE = "com.dimo.PayByQR.intent.extra.discountType";
    public static final String INTENT_EXTRA_LOYALTY_PROGRAM_NAME = "com.dimo.PayByQR.intent.extra.programName";
    public static final String INTENT_EXTRA_ORIGINAL_AMOUNT = "com.dimo.PayByQR.intent.extra.originalAmount";
    public static final String INTENT_EXTRA_PAID_AMOUNT = "com.dimo.PayByQR.intent.extra.paidAmount";
    public static final String INTENT_EXTRA_NUMBER_OF_COUPONS = "com.dimo.PayByQR.intent.extra.numberOfCoupons";
    public static final String INTENT_EXTRA_AMOUNT_OF_DISCOUNT = "com.dimo.PayByQR.intent.extra.amountOfDiscount";
    public static final String INTENT_EXTRA_POINT_REDEEMED = "com.dimo.PayByQR.intent.extra.pointsRedeemed";
    public static final String INTENT_EXTRA_AMOUNT_REDEEMED = "com.dimo.PayByQR.intent.extra.amountRedeemed";
    public static final String INTENT_EXTRA_TIP_SUGGESTION = "com.dimo.PayByQR.intent.extra.tipSuggestion";
    public static final String INTENT_EXTRA_TIP_PROPOSITION_1 = "com.dimo.PayByQR.intent.extra.tipProposition1";
    public static final String INTENT_EXTRA_TIP_PROPOSITION_2 = "com.dimo.PayByQR.intent.extra.tipProposition2";
    public static final String INTENT_EXTRA_IS_SHOW_FIDELITIZ_INFO = "com.dimo.PayByQR.intent.extra.isShowFidelitizInfo";
    public static final String INTENT_EXTRA_FIDELITIZ_TYPE = "com.dimo.PayByQR.intent.extra.fidelitizType";
    public static final String INTENT_EXTRA_FIDELITIZ_POINT_BALANCE = "com.dimo.PayByQR.intent.extra.fidelitizPointBalance";
    public static final String INTENT_EXTRA_FIDELITIZ_COUPON_BALANCE = "com.dimo.PayByQR.intent.extra.fidelitizCouponBalance";
    public static final String INTENT_EXTRA_FIDELITIZ_POINT_FOR_COUPON = "com.dimo.PayByQR.intent.extra.fidelitizPointForCoupon";
    public static final String INTENT_EXTRA_FIDELITIZ_POINT_GENERATED = "com.dimo.PayByQR.intent.extra.fidelitizPointGenerated";
    public static final String INTENT_EXTRA_FIDELITIZ_COUPON_GENERATED = "com.dimo.PayByQR.intent.extra.fidelitizCouponGenerated";
    public static final String INTENT_EXTRA_FIDELITIZ_COUPON_VALUE = "com.dimo.PayByQR.intent.extra.fidelitizCouponValue";
    public static final String INTENT_EXTRA_FIDELITIZ_TITLE = "com.dimo.PayByQR.intent.extra.fidelitizTitle";
    public static final String INTENT_EXTRA_FIDELITIZ_JSON = "com.dimo.PayByQR.intent.extra.fidelitizJSON";
    public static final String INTENT_EXTRA_FIDELITIZ_JSON_SUCCESS = "com.dimo.PayByQR.intent.extra.fidelitizJSONSuccess";
    public static final String INTENT_EXTRA_QRSTORE_CUST_NAME = "com.dimo.PayByQR.intent.extra.qrstore.custName";
    public static final String INTENT_EXTRA_QRSTORE_CUST_EMAIL = "com.dimo.PayByQR.intent.extra.qrstore.custEmail";
    public static final String INTENT_EXTRA_QRSTORE_CUST_PHONE = "com.dimo.PayByQR.intent.extra.qrstore.custPhone";

}
