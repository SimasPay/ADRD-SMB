package com.dimo.PayByQR.QrStore.constans;

/**
 * Created by dimo on 11/30/15.
 */
public class QrStoreDefine {

    public static  final String QRSTORE_STRING= "qrstore";

    public static final String INTENT_EXTRA_QRSTORE_CART_KONFIRMASI_CHECKOUT = "com.dimo.PayByQR.intent.extra.checkout";
    public static final String INTENT_EXTRA_QRSTORE_CART_MERCHANTID = "com.dimo.PayByQR.intent.extra.merchantid";
    public static final String INTENT_EXTRA_QRSTORE_CART_TRANSID = "com.dimo.PayByQR.intent.extra.transid";

    public static final String INTENT_EXTRA_QRSTORE_CART_MERCHANTHEAD = "com.dimo.PayByQR.intent.extra.merchantHeader";
    public static final String INTENT_EXTRA_QRSTORE_CART_KONFIRMASI = "com.dimo.PayByQR.intent.extra.konfirmasi";
    public static final String INTENT_EXTRA_QRSTORE_CART_SHIPPING = "com.dimo.PayByQR.intent.extra.shipping";
    public static final String INTENT_EXTRA_QRSTORE_CART_STORES = "com.dimo.PayByQR.intent.extra.storeNameToId";
    public static final String LAZIES_PADD="!~!";

    public static final String SHARED_PREF_TRANS_ID = "com.dimo.PayByQR.shared.pref.transID";

    public static final int MAXIMUM_CHAR_ONLINE=30;

    public  static final String RESPOND_STORE_JSON_OK ="success";
    public  static final String RESPOND_STORE_JSON_KO ="NOK";

    public static final int STORE_PICK=0;
    public static final int CITY_PICK=1;
    public static final int SELECT_PICK=2;

    public static final int CART_ON_BASKET=0;
    public static final int CART_ON_SHIPPING=1;
    public static final int CART_ON_CHECKOUT=2;
    public static final int CART_ON_CONFIRM=3;
    public static final int CART_ON_PICKUP=4;

    public static final int PASS_SHIPPING=0;
    public static final int PASS_CHEKCOUT=1;
    public static final int PASS_PAID=2;
}
