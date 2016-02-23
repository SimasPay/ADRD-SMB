/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.bsim.containers;

/**
 *
 * @author karthik
 */
public class EncryptedUserDataContainer {

    private UserDataContainer container;

    private EncryptedUserDataContainer(UserDataContainer container) {
        this.container = container;
    }

    public static EncryptedUserDataContainer createSecureApplicationDataContainer(UserDataContainer container) {
        EncryptedUserDataContainer sac = new EncryptedUserDataContainer(container);
        return sac;
    }
    private String TransactionID;
    private String Transactioncharges;
    private String creditAmount;
    private String debitAmount;
    private String DateOfBirth;
    private String AccountType;

    /**
     * @return the agentCode
     */
    public String getAgentCode() {
        return this.container.getAgentCode();
    }

    /**
     * @return the container
     */
    public UserDataContainer getContainer() {
        return container;
    }

    /**
     * @return the sourceMdn
     */
    public String getSourceMdn() {
        return container.getSourceMdn();
    }

    /**
     * @return the destinationMdn
     */
    public String getDestinationMdn() {
        return container.getDestinationMdn();
    }
    
    private String encryptString(String str) {
        if (str == null) {
            return null;
        }
        try {
            //return new String(CryptoService.binToHex(CryptoService.encryptWithAES(container.getAESKey(), str.getBytes(Constants.UTF_8))));
        } catch (Exception ex) {
        }
        return null;
    }
    /**
     * @return the sourcePin
     */
    public String getSourcePin() {
        return encryptString(container.getSourcePin());
    }

    /**
     * @return the Amount
     */
    public String getAmount() {
        return encryptString(container.getAmount());
    }

    /**
     * @return the TransferId
     */
    public String getTransferId() {
        return encryptString(container.getTransferId());
    }

    /**
     * @return the ParentTxnId
     */
    public String getParentTxnId() {
        return encryptString(container.getParentTxnId());
    }

    /**
     * @return the SecretAnswer
     */
    public String getSecretAnswer() {
        return container.getSecretAnswer();
    }

    /**
     * @return the NewPin
     */
    public String getNewPin() {
        return encryptString(container.getNewPin());
    }

    /**
     * @return the ConfirmPin
     */
    public String getConfirmPin() {
        return encryptString(container.getConfirmPin());
    }

    /**
     * @return the ConfirmPin
     */
   /* public String getActivationConfirmPin() {
        return encryptWithOTP(container.getActivationConfirmPin());
    }

    public String getActivationNewPin() {
        return encryptWithOTP(container.getActivationNewPin());
    }*/

/*    private String encryptWithOTP(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] strBytes = str.getBytes(Constants.UTF_8);
            String smdn = this.container.getSourceMdn();
            smdn = ConfigurationUtil.normalizeMDN(smdn);
            char[] pwd = CryptoService.generateHash(smdn, this.container.getOTP());
            for(int i=0;i<pwd.length;i++)
            {
            	System.out.print(pwd[i]);
            }
            byte[] salt = {0, 0, 0, 0, 0, 0, 0, 0};
            //return new String(CryptoService.binToHex(CryptoService.encryptWithPBE(strBytes, pwd, salt, 20)));
        } catch (Exception ex) {
        }
        return null;
    }*/

    /**
     * @return the OTP
     */
    /*public String getOTP() {
        return encryptWithOTP(Constants.ZEROES_STRING);
    }*/

    /**
     * @return the TransactionID
     */
    public String getTransactionID() {
        this.TransactionID = encryptString(container.getTransactionID());
        return this.TransactionID;
    }

    /**
     * @return the Transactioncharges
     */
    public String getTransactioncharges() {
        this.Transactioncharges = encryptString(container.getTransactioncharges());
        return this.Transactioncharges;
    }

    /**
     * @return the creditAmount
     */
    public String getCreditAmount() {
        this.creditAmount = encryptString(container.getCreditAmount());
        return this.creditAmount;
    }

    /**
     * @return the debitAmount
     */
    public String getDebitAmount() {
        this.debitAmount = encryptString(container.getDebitAmount());
        return this.debitAmount;
    }

    /**
     * @return the authenticationString
     */
    public String getAuthenticationString() {
        return container.getAuthenticationString();
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return container.getSalt();
    }

    /**
     * @return the confirmed
     */
    public String getConfirmed() {
        return container.getConfirmed();
    }

    /**
     * @return the sourcePocketCode
     */
    public String getSourcePocketCode() {
        return container.getSourcePocketCode();
    }

    /**
     * @return the destinationPocketCode
     */
    public String getDestinationPocketCode() {
        return container.getDestinationPocketCode();
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return container.getServiceName();
    }

    /**
     * @return the keyParameter
     */
    /*public KeyParameter getKeyParameter() {
        //return container.getAESKey();
    }*/

    /**
     * @return the TransactionName
     */
    public String getTransactionName() {
        return container.getTransactionName();
    }

    /**
     * @return the merchantCode
     */
    public String getPartnerCode() {
        return container.getPartnerCode();
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return container.getFirstName();
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return container.getLastName();
    }

    /**
     * @return the applicationID
     */
    public String getApplicationID() {
        return container.getApplicationID();
    }

    /**
     * @return the SubscriberMDN
     */
    public String getSubscriberMDN() {
        return container.getSubscriberMDN();
    }

    /**
     * @return the DateOfBirth
     */
    public String getDateOfBirth() {
        this.DateOfBirth = encryptString(container.getDateOfBirth());
        return this.DateOfBirth;
    }

    /**
     * @return the AccountType
     */
    public String getAccountType() {
        this.AccountType = encryptString(container.getAccountType());
        return this.AccountType;
    }
}
