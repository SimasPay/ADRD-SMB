package com.dimo.PayByQR.model;

import java.util.ArrayList;

/**
 * Created by dimoproperty6 on 12/2/15.
 */
public class LoyaltyListResponse {
    public static final String LOYALTY_TYPE_DISCOUNT = "PERMANENT_PERCENTAGE_DISCOUNT";
    public static final String LOYALTY_TYPE_POINTS = "POINTS";

    public static final String REWARD_TYPE_PERCENT = "PERCENT";
    public static final String REWARD_TYPE_CASH = "CASH";

    public int loyaltyProgramId;
    public boolean hasLoyaltyCard;
    public String label;
    public ArrayList<Double> logo;
    public long startDate;
    public long endDate;
    public String loyaltyProgramOwnerBrand;
    public String loyaltyProgramOwnerCompanyName;
    public String loyaltyProgramType;
    public String expenseType;
    public int pointPerExpense;
    public int amountPerPoint;
    public int pointAmountForCoupon;
    public String rewardType;
    public int discountAmount;
    public int monthNumberValidity;
    public int permanentPercentageDiscount;
    public int minTransAmountForDiscount;
    public int maxDiscountAmount;
    public int maxRedeem;
    public MembershipInfo[] membership;
    public FidelitizInfo fidelitizInfo;

    public String rawJSON;

    public class FidelitizInfo{
        public int fidelitizId;
        public int loyaltyProgramId;
        public int loyaltyCardId;
        public int balance;
        public InvoiceDetailResponse.CouponList[] coupons;
    }

}

/*
{
    "result": "success",
    "loyaltyProgram":
        [
            {
                "loyaltyProgramId": 192,
                "hasLoyaltyCard": false,
                "label": "program_nat",
                "logo": [logo],
                "startDate": 1445817600000,
                "endDate": 1448841600000,
                "loyaltyProgramType": "POINTS",
                "expenseType": "MONEY",
                "pointPerExpense": 1,
                "amountPerPoint": 100000,
                "pointAmountForCoupon": 5,
                "rewardType": "CASH",
                "discountAmount": 50000,
                "monthNumberValidity": 2,
                "permanentPercentageDiscount": null,
                "minTransAmountForDiscount": null,
                "maxDiscountAmount": null,
                "maxRedeem": null,
                "membership": [
                    {
                        "memberBrandName": "Ace Hardware Puri Mall",
                        "address1": "Jalan Raya Meruya No 2",
                        "city": "Jakarta Barat"
                    },
                    {
                        "memberBrandName": "Ace Hardware Kota Kasablanka",
                        "address1": "Mall Kota Kasablanka",
                        "city": "Jakarta Selatan"
                    }
                    ],
                "fidelitizInfo": {
                    "loyaltyProgramId": 192,
                    "loyaltyCardId": 29120,
                    "balance": 0,
                    "coupons": [
                        {
                            "loyaltyProgramId": 192,
                            "amount": 50000,
                            "loyaltyCardId": 29120,
                            "couponType": "CASH",
                            "fidelitizId": 2961,
                            "couponId": 16045,
                            "validityEndDate": 1448841600000,
                            "currency": "IDR"
                        }
                    ]
                    }
            }
        ]
}
*/