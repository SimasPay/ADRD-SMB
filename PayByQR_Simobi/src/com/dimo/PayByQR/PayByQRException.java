package com.dimo.PayByQR;

/**
 * Created by Rhio on 11/2/15.
 */
public class PayByQRException extends Exception{
    private String errorMessage, errorDetail;
    private int errorCode;

    public PayByQRException(){
        this(0, "", "");
    }

    public PayByQRException(int errorCode, String errorMessage, String errorDetail){
        super();
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
        setErrorDetail(errorDetail);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}
