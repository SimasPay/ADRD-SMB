package com.dimo.PayByQR.model;

import java.util.ArrayList;

/**
 * Created by Rhio on 11/9/15.
 */
public class InvoiceStatusResponse {
    public String invoiceId;
    public String status;
    public FidelitizInfo fidelitizInfo;
    public String rawJSON;

    /*"fidelitizInfo": {
        "loyaltyProgramLabel": "ACE Point Reward",
        "couponValue": 100000,          nilai voucher nya
        "rewardType": "CASH",
        "pointsBalance": 5,             point yg sudah dikumpulkan user
        "couponsBalance": 2,            kupon yg sudah dikumpulkan user
        "pointAmountForCoupon": 40,     jumlah point yg dibutuhkan utk mendapatkan 1 coupon
        "pointsGenerated": 2,           point yg dihasilkan saat trx
        "couponsGenerated": 1           coupon yg dihasilkan saat trx
    */

    public class FidelitizInfo {
        public int loyaltyProgramId;
        public String loyaltyProgramLabel;
        public int couponValue;
        public String rewardType;
        public int pointsBalance;
        public int couponsBalance;
        public int pointAmountForCoupon;
        public int pointsGenerated;
        public int couponsGenerated;
        public ArrayList<Double> logo;
        public long startDate;
        public long endDate;
        public int pointPerExpense;
        public int amountPerPoint;
        public String expenseType;
        public int maxRedeem;
        public boolean isMaxRedeemDaily;
        public MembershipInfo[] membership;
    }
}
