package com.dimo.PayByQR.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Rhio on 6/1/16.
 */
public class LoyaltyModel implements Parcelable{
    public String loyaltyProgramLabel;
    public int pointsGenerated;
    public int pointsBalance;
    public int couponsGenerated;
    public int couponsBalance;
    public int pointAmountForCoupon;
    public int couponValue;
    public String rewardType;

    public LoyaltyModel(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loyaltyProgramLabel);
        dest.writeInt(pointsGenerated);
        dest.writeInt(pointsBalance);
        dest.writeInt(couponsGenerated);
        dest.writeInt(couponsBalance);
        dest.writeInt(pointAmountForCoupon);
        dest.writeInt(couponValue);
        dest.writeString(rewardType);
    }

    public static final Creator<LoyaltyModel> CREATOR = new Creator<LoyaltyModel>() {
        public LoyaltyModel createFromParcel(Parcel in) {
            return new LoyaltyModel(in);
        }

        public LoyaltyModel[] newArray(int size) {
            return new LoyaltyModel[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private LoyaltyModel(Parcel in) {
        loyaltyProgramLabel = in.readString();
        pointsGenerated = in.readInt();
        pointsBalance = in.readInt();
        couponsGenerated = in.readInt();
        couponsBalance = in.readInt();
        pointAmountForCoupon = in.readInt();
        couponValue = in.readInt();
        rewardType = in.readString();
    }
}
