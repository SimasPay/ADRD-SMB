package com.dimo.PayByQR.QrStore.model;

/**
 * Created by dimo on 1/19/16.
 */
public class Merchant {

    private  String merchantName;
    private String merchantCode;
    private String urlImageMerhcant;
    private int listitem;


    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getUrlImageMerhcant() {
        return urlImageMerhcant;
    }

    public void setUrlImageMerhcant(String urlImageMerhcant) {
        this.urlImageMerhcant = urlImageMerhcant;
    }

    public int getListitem() {
        return listitem;
    }

    public void setListitem(int listitem) {
        this.listitem = listitem;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
