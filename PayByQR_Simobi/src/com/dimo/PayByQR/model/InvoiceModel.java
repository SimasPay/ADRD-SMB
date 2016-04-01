package com.dimo.PayByQR.model;

/**
 * Created by Rhio on 10/27/15.
 */
public class InvoiceModel {
    public String invoiceID;
    public int originalAmount;
    public int paidAmount;
    public int amountOfDiscount;
    public int numberOfCoupons;
    public String merchantName;
    public String discountType;
    public String loyaltyProgramName;
    public int tipAmount;
    public int pointsRedeemed;
    public int amountRedeemed;

    /**
     *  paidAmount -> originalAmount - amountOfDiscount
     *
     *  amountOfDiscount ->
     *      - normal : 0
     *      - permanent percentage : amount - correctedAmount...
     *      - voucher :
     *              - Cash : numberOfCoupons * discountAmount
     *              - Percentage : amount * min(numberOfCoupons, 1) * discountAmount/100
     *
     *  numberOfCoupons ->
     *  -- numberof coup++;
     *      - maxUsingCoupons : roundUp(amount/discountAmount)
     *      - maxRedeem : min (couponsCount, maxUsingCoupons)
     *      - numberOfCoupons : min(maxRedeem, numberOfCoupons)
     *
     */
}
