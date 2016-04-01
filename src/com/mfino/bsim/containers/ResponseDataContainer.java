package com.mfino.bsim.containers;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.CryptoService;

/**
 *
 * @author karthik
 */
public class ResponseDataContainer {

    private EncryptedResponseDataContainer srdContainer;
    //private KeyParameter keyParameter;

    private ResponseDataContainer(EncryptedResponseDataContainer rd) {
        this.srdContainer = rd;
    }

  /*  public static ResponseDataContainer createResponseData(EncryptedResponseDataContainer rd, KeyParameter kp) {
        ResponseDataContainer sac = new ResponseDataContainer(rd);
        sac.setKeyParameter(kp);
        return sac;
    }*/
    private String Amount;
    private String TransferId;
    private String ParentTxnId;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String refId;
    private String TransactionCharges;

    private String decryptString(String str) {
        if (str == null) {
            return null;
        }
        try {/*
            byte[] strBytes = CryptoService.hexToBin(str.toCharArray());
            byte[] strOut = CryptoService.decryptWithAES(keyParameter, strBytes);
            return new String(strOut, Constants.UTF_8);
        */} catch (Exception ex) {
//            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @return the container
     */
    public EncryptedResponseDataContainer getContainer() {
        return srdContainer;
    }

    /**
     * @return the Amount
     */
    public String getAmount() {
        Amount = decryptString(srdContainer.getEncryptedAmount());
        return Amount;
    }

    /**
     * @return the TransferId
     */
    public String getTransferId() {
        TransferId = decryptString(srdContainer.getEncryptedTransferId());
        return TransferId;
    }

    /**
     * @return the ParentTxnId
     */
    public String getParentTxnId() {
        ParentTxnId = decryptString(srdContainer.getEncryptedParentTxnId());
        return ParentTxnId;
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
        TransactionCharges = decryptString(srdContainer.getEncryptedTransactionCharges());
        return Transactioncharges;
    }

    /**
     * @return the creditAmount
     */
    public String getCreditAmount() {
        creditAmount = decryptString(srdContainer.getEncryptedCreditAmount());
        return creditAmount;
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
        debitAmount = decryptString(srdContainer.getEncryptedDebitAmount());
        return debitAmount;
    }

    /**
     * @return the authenticationString
     */
    public String getAuthenticationString() {
        return srdContainer.getAuthenticationString();
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return srdContainer.getSalt();
    }

    /**
     * @return the transactionTime
     */
    public String getTransactionTime() {
        return srdContainer.getTransactionTime();
    }

    /**
     * @return the refId
     */
    public String getRefId() {
        refId = decryptString(srdContainer.getEncryptedRefId());
        return refId;
    }

    /**
     * @return the msgCode
     */
    public String getMsgCode() {
        return srdContainer.getMsgCode();
    }

    /**
     * @return the TransactionCharges
     */
    public String getTransactionCharges() {
        TransactionCharges = decryptString(srdContainer.getEncryptedTransactionCharges());
        return TransactionCharges;
    }

    public String getAESKey() {
        return srdContainer.getEncryptedAESkey();
    }

    /**
     * @return the Msg
     */
    public String getMsg() {
        return srdContainer.getMessagesList().elementAt(0).toString();
    }

    
    public String getErrorMessage()
    {
    	return srdContainer.getErrorMsg();
    }
}