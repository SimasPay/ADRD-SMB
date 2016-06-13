package com.dimo.PayByQR.model;

/**
 * Created by Rhio on 11/9/15.
 */
public class InvoiceDetailResponse {

    public String merchantApikey;
    public String receiver;
    public String invoiceId;
    public String status;
    public boolean hasLoyaltyCard;
    public double permanentPercentageDiscount;
    public boolean tipEnabled;
    public TipDetail tip;
    public String currency;
    public int amount;
    public CouponList[] couponList;
    public int correctedInvoiceAmountWithPercentage;
    public int fidelitizId;
    public CurrentLoyaltyProgram currentLoyaltyProgram;
    public String comment;

    public class CurrentLoyaltyProgram {
        public String result;
        public LoyaltyProgramModel loyaltyProgram;
    }

    public class TipDetail {
        public int suggestedAmount;
        public int firstProposition;
        public int secondProposition;
    }

    public class CouponList {
        public int loyaltyProgramId;
        public int amount;
        public int loyaltyCardId;
        public String couponType;
        public int fidelitizId;
        public int couponId;
        public long validityEndDate;
        public String currency;
    }
}
