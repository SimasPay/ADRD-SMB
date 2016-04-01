package com.dimo.PayByQR.QrStore.model;

import android.util.Log;

import com.dimo.PayByQR.PayByQRProperties;

/**
 * Created by dimo on 1/20/16.
 */
public class RespondJson {
     String result ;
     String Description ;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public  void toClassToStr()
    {
        if(PayByQRProperties.isDebugMode()) Log.d("Respond Json", "respond "+ this.result+ "\ndesc "+this.Description );
    }
}
