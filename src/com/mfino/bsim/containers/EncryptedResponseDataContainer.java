package com.mfino.bsim.containers;

import java.util.Vector;


public class EncryptedResponseDataContainer {

    private String errorMsg;
    private Vector messagesList;
    private String msg;
    private String transactionTime;
    private String encryptedRefId;
    private String encryptedTransferId;
    private String encryptedParentTxnId;
    private String msgCode;
    private String responseData;
    private String encryptedAmount;
    private String encryptedAeskey;
    private String AuthenticationString;
    private String Salt;
    private String encryptedTransactionCharges;
    private String encryptedDebitAmount;
    private String encryptedCreditAmount;
    private String appUpdateURL;
    private String registrationMedium;
    private String aditionalInfo;
	private String custName;
    private String accountNumber;
    private String amount;
    private String destBank;
    private String destMDN;
    
    private String resetPinRequested;
    private String status;
	private String sctl;
	private String mfaMode;
	private String isActivated;
	private String publicKeyModulus;
	private String publicKeyExponet;
	private String success;
	private String userApiKey;
	
	public String getUserApiKey() {
		return userApiKey;
	}

	public void setUserApiKey(String userApiKey) {
		this.userApiKey = userApiKey;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getPublicKeyModulus() {
		return publicKeyModulus;
	}

	public void setPublicKeyModulus(String publicKeyModulus) {
		this.publicKeyModulus = publicKeyModulus;
	}

	public String getPublicKeyExponet() {
		return publicKeyExponet;
	}

	public void setPublicKeyExponet(String publicKeyExponet) {
		this.publicKeyExponet = publicKeyExponet;
	}
   
    public String getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(String isActivated) {
		this.isActivated = isActivated;
	}

	public String getMfaMode() {
		return mfaMode;
	}

	public void setMfaMode(String mfaMode) {
		this.mfaMode = mfaMode;
	}

	public String getSctl() {
		return sctl;
	}

	public void setSctl(String sctl) {
		this.sctl = sctl;
	}

    public String getResetPinRequested() {
		return resetPinRequested;
	}

	public void setResetPinRequested(String resetPinRequested) {
		this.resetPinRequested = resetPinRequested;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


    public String getDestMDN() {
		return destMDN;
	}

	public void setDestMDN(String destMDN) {
		this.destMDN = destMDN;
	}

	public String getDestBank() {
		return destBank;
	}

	public void setDestBank(String destBank) {
		this.destBank = destBank;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
		 System.out.println("Testing>>CUST NAME>>"+custName);
	}


	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
		 System.out.println("Testing>>CUST NAME>>"+accountNumber);
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
    public String getAditionalInfo() {
		return aditionalInfo;
	}

	public void setAditionalInfo(String aditionalInfo) {
		this.aditionalInfo = aditionalInfo;
	}

	public String getRegistrationMedium() {
		return registrationMedium;
	}

	public void setRegistrationMedium(String registrationMedium) {
		this.registrationMedium = registrationMedium;
	}

	public String getAppUpdateURL() {
        return appUpdateURL;
    }

    public void setAppUpdateURL(String appUpdateURL) {
        this.appUpdateURL = appUpdateURL;
    }

    public void setMsg(String msg) {
        if (this.messagesList == null) {
            this.messagesList = new Vector();
        }
        this.messagesList.addElement(msg);
      /*  if(msg.contains("NGN")){
        	msg=msg.replace("NGN", "$");
    	}else{
    		msg=msg;
    	}*/
        this.msg =msg;
    }
    
    public String getMsg() {
        return msg;
    }


    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    /**
     * @return the encryptedMessagesList
     */
    public Vector getMessagesList() {
        return messagesList;
    }

    /**
     * @return the encryptedRefId
     */
    public String getEncryptedRefId() {
        return encryptedRefId;
    }

    /**
     * @param encryptedRefId the encryptedRefId to set
     */
    public void setEncryptedRefId(String encryptedRefId) {
        this.encryptedRefId = encryptedRefId;
    }

    /**
     * @return the encryptedTransferId
     */
    public String getEncryptedTransferId() {
        return encryptedTransferId;
    }

    /**
     * @param encryptedTransferId the encryptedTransferId to set
     */
    public void setEncryptedTransferId(String encryptedTransferId) {
        this.encryptedTransferId = encryptedTransferId;
    }

    /**
     * @return the encryptedParentTxnId
     */
    public String getEncryptedParentTxnId() {
        return encryptedParentTxnId;
    }

    /**
     * @param encryptedParentTxnId the encryptedParentTxnId to set
     */
    public void setEncryptedParentTxnId(String encryptedParentTxnId) {
        this.encryptedParentTxnId = encryptedParentTxnId;
    }

    /**
     * @return the encryptedAmount
     */
    public String getEncryptedAmount() {
        return encryptedAmount;
    }

    /**
     * @param encryptedAmount the encryptedAmount to set
     */
    public void setEncryptedAmount(String encryptedAmount) {
        this.encryptedAmount = encryptedAmount;
    }

    /**
     * @return the encryptedAeskey
     */
    public String getEncryptedAESkey() {
        return encryptedAeskey;
    }

    /**
     * @param encryptedAeskey the encryptedAeskey to set
     */
    public void setEncryptedAESkey(String encryptedAeskey) {
        this.encryptedAeskey = encryptedAeskey;
    }

    /**
     * @return the encryptedAuthenticationString
     */
    public String getAuthenticationString() {
        return AuthenticationString;
    }

    /**
     * @param encryptedAuthenticationString the encryptedAuthenticationString to set
     */
    public void setAuthenticationString(String AuthenticationString) {
        this.AuthenticationString = AuthenticationString;
    }

    /**
     * @return the encryptedSalt
     */
    public String getSalt() {
        return Salt;
    }

    /**
     * @param encryptedSalt the encryptedSalt to set
     */
    public void setSalt(String Salt) {
        this.Salt = Salt;
    }

    /**
     * @return the encryptedTransactionCharges
     */
    public String getEncryptedTransactionCharges() {
        return encryptedTransactionCharges;
    }

    /**
     * @param encryptedTransactionCharges the encryptedTransactionCharges to set
     */
    public void setEncryptedTransactionCharges(String encryptedTransactionCharges) {
        this.encryptedTransactionCharges = encryptedTransactionCharges;
    }

    /**
     * @return the encryptedDebitAmount
     */
    public String getEncryptedDebitAmount() {
        return encryptedDebitAmount;
    }

    /**
     * @param encryptedDebitAmount the encryptedDebitAmount to set
     */
    public void setEncryptedDebitAmount(String encryptedDebitAmount) {
        this.encryptedDebitAmount = encryptedDebitAmount;
    }

    /**
     * @return the encryptedCreditAmount
     */
    public String getEncryptedCreditAmount() {
        return encryptedCreditAmount;
    }

    /**
     * @param encryptedCreditAmount the encryptedCreditAmount to set
     */
    public void setEncryptedCreditAmount(String encryptedCreditAmount) {
        this.encryptedCreditAmount = encryptedCreditAmount;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

