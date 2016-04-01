package com.dimo.PayByQR.model;

/**
 * Created by Rhio on 11/9/15.
 */
public class LoyaltyProgramModel {
        /*"result": "success",
            "loyaltyProgram": {*/
    public static final String LOYALTY_TYPE_DISCOUNT = "PERMANENT_PERCENTAGE_DISCOUNT";
    public static final String LOYALTY_TYPE_POINTS = "POINTS";

    public static final String REWARD_TYPE_PERCENT = "PERCENT";
    public static final String REWARD_TYPE_CASH = "CASH";

    public long startDate;
    public int minTransAmountForDiscount;
    public String borderColor;
    public String backgroundColor;
    public boolean hasLoyaltyCard;
    public int permanentPercentageDiscount;
    public int affiliationId;
    public int individualId;
    public long endDate;
    public String loyaltyProgramType;
    public int discountAmount;
    public String currency;
    public int amountPerPoint;
    public int monthNumberValidity;
    public int maxRedeem;
    public boolean isPrivate;
    public int pointPerExpense;
    public int fidelitizId;
    public int pointAmountForCoupon;
    public int loyaltyProgramId;
    public String logo;
    public String loyaltyProgramOwnerBrand;
    public String label;
    public String rewardType;
    public String loyaltyProgramOwnerCompanyName;
    public int maxDiscountAmount;
    public String expenseType;
    public int loyaltyProgramOwnerId;
    public String loyaltyProgramOwnerEmail;

}
