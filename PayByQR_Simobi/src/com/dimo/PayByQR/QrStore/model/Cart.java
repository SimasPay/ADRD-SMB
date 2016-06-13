package com.dimo.PayByQR.QrStore.model;

import android.graphics.Bitmap;

/**
 * Created by dimo on 11/25/15.
 */
public class Cart {

    private  int detailQuantity;

    private String id;
    private String invoiceId;
    private String goodsName;
    private Integer price;
    private Integer discountAmount;
    private int     stock;
    private int maxQuantity;
    private String merchantCode;
    private String merchantName;
    private int    weight;
    private Bitmap picture;
    private String detailDescription;
    private String imageUrl;
    private String imageUrlmerchant;
    private String trandId;
    private int status;
    private String invoiceIdPE;
    private boolean isPaid;// this for history & goods pickup


    public int getDetailQuantity() {
        return detailQuantity;
    }

    public void setDetailQuantity(int detailQuantity) {
        this.detailQuantity = detailQuantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getWeigth() {
        return weight;
    }

    public void setWeigth(int weigth) {
        this.weight = weigth;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public void stringClass ()
    {
        System.out.print( "==========CART DEBUG "+"\n" +
                "invoiceId:" +invoiceId +"\n" +
       "detailQuantity:" +detailQuantity +"\n" +
        "id:"+ getId() +"\n" +
       "goodsName:"+goodsName+"\n" +
        "price:"+ price+"\n" +
        "discountAmount:"+ discountAmount+"\n" +
        "stock:"+ stock+"\n" +
        "maxQuantity:"+ maxQuantity +"\n" +
        "merchantCode:"+ merchantCode+"\n" +
        "merchantName:"+ merchantName+"\n" +
       " weight:"+ weight+"\n" +
        " urlimage:"+ imageUrl+"\n" +
         "merchantImage" +              imageUrlmerchant+"\n"+
        "detailDescription:"+ detailDescription
        );
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrlmerchant() {
        return imageUrlmerchant;
    }

    public void setImageUrlmerchant(String imageUrlmerchant) {
        this.imageUrlmerchant = imageUrlmerchant;
    }

    public String getTrandId() {
        return trandId;
    }

    public void setTrandId(String trandId) {
        this.trandId = trandId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInvoiceIdPE() {
        return invoiceIdPE;
    }

    public void setInvoiceIdPE(String invoiceIdPE) {
        this.invoiceIdPE = invoiceIdPE;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
}
