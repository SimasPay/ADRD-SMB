package com.mfino.bsim.services;


public class Constants {
	
	public static String SOURCE_MDN_NAME = "";
	public static String SOURCE_MDN_PIN = "1234";
	//public static String DESTINATION_MDN_NAME = "";
	public static String SOURCE_PAYMENT_MODE = "";
    public static String LOG_TAG = "SIMOBI";
    public static int SPLASH_DISPLAY_LENGHT = 3000;
	public static int MFA_CONNECTION_TIMEOUT =60000 ;
	public static int CONNECTION_TIMEOUT =120000 ;
    public static final String ZEROES_STRING = "000";
    public static final String UTF_8 = "UTF-8";
    public static final String US_ASCII = "US-ASCII";
    public static final int PBE_ITERATION_COUNT = 20;
    public static final byte[] ZEROES_STRING_ENCODED = null;
    public static final String EMPTY_STRING = "";
    // services
    public static final String SERVICE_ACCOUNT = "Account";
    public static final String SERVICE_WALLET = "Wallet";
    public static final String SERVICE_BANK = "Bank";
    public static final String SERVICE_AGENT = "AgentServices";
    public static final String SERVICE_SHOPPING = "Shopping";
    public static final String SERVICE_BUY = "Buy";
    public static final String SERVICE_BILLPAYMENT="Payment";
    public static final String SERVICE_MIGRATE_TOKENSIMPLUS = "GenerateMigrateToken";
    
    public static String TRANSACTION_CASHOUT_AT_ATM_INQUIRY= "CashOutAtATMInquiry";
    public static String TRANSACTION_CASHOUT_AT_ATM= "CashOutAtATM";
    // transactions
    
 // QR Payment transactions
 	public static final String TRANSACTION_QR_BILLPAYMENT_INQUIRY = "QRPaymentInquiry";
 	public static final String TRANSACTION_QR_BILLPAYMENT = "QRPayment";
 	public static final String TRANSACTION_QR_PAYMENT = "QRPayment";
 	public static final String TRANSACTION_USER_APIKEY = "GetUserAPIKey";

 	public static final String TRANSACTION_FLASHIZ_INQUIRY = "FlashizInquiry";
 	public static final String TRANSACTION_FLASHIZ = "Flashiz";
 	public static int MFA_QRPAYMENT_CONNECTION_TIMEOUT =30000 ;
    
    public static final String TRANSACTION_GETPUBLICKEY = "GetPublicKey";
    public static final String TRANSACTION_PURCHASE_INQUIRY = "PurchaseInquiry";
    public static final String TRANSACTION_PURCHASE = "Purchase";
    public static final String TRANSACTION_ACTIVATION = "Activation";
    public static final String TRANSACTION_RESEND_OTP = "ResendOtp";
    public static final String TRANSACTION_REACTIVATION = "Reactivation";
    public static final String TRANSACTION_RESET_PIN = "ResetPinByOTP";
    public static final String TRANSACTION_REGISTRATION_MEDIUM = "GetRegistrationMedium";
    public static final String TRANSACTION_TRANSACTIONSTATUS = "TransactionStatus";
    public static final String TRANSACTION_CHANGEPIN = "ChangePIN";
    public static final String TRANSACTION_CHANGEPIN_CONFIRM = "ChangePinConfirm";
    public static final String TRANSACTION_ACTIVATION_CONFIRM = "ActivationConfirm";
    public static final String TRANSACTION_REACTIVATION_CONFIRM = "ReactivationConfirm";
    public static final String TRANSACTION_RESETPIN = "ResetPIN";
    public static final String TRANSACTION_AGENTACTIVATION = "AgentActivation";
    public static final String TRANSACTION_CHECKBALANCE = "CheckBalance";
    public static final String TRANSACTION_HISTORY = "History";
    public static final String TRANSACTION_TRANSFER = "Transfer";
    public static final String TRANSACTION_CASHOUT = "CashOut";
    public static final String TRANSACTION_SUBSCRIBERREGISTRATION = "SubscriberRegistration";
    public static final String TRANSACTION_CASHIN = "CashIn";
    public static final String TRANSACTION_INTERBANK_TRANSFER_INQUIRY = "InterBankTransferInquiry";
    public static final String TRANSACTION_Uangku_INQUIRY = "TransferToUangkuInquiry";
    public static final String TRANSACTION_Uangku_CONFIRM = "TransferToUangku";


    public static final String TRANSACTION_INTERBANK_TRANSFER = "InterBankTransfer";
    //webapi specific transactions
    public static final String TRANSACTION_LOGIN = "Login";
    public static final String TRANSACTION_LOGOUT = "Logout";
    public static final String TRANSACTION_TRANSFER_INQUIRY = "TransferInquiry";
    public static final String TRANSACTION_CASHIN_INQUIRY = "CashInInquiry";
    public static final String TRANSACTION_CASHOUT_INQUIRY = "CashOutInquiry";
    //Buy Section transations
    public static final String TRANSACTION_AIRTIME_PURCHASE_INQUIRY = "AirtimePurchaseInquiry";
    public static final String TRANSACTION_AIRTIME_PURCHASE = "AirtimePurchase";
    
    //Bill payment transactions
    
    public static final String TRANSACTION_BILLPAYMENT_INQUIRY = "BillPayInquiry";
    public static final String TRANSACTION_BILLPAYMENT = "BillPay";
    public static final String TRANSACTION_BILL_INQUIRY = "BillInquiry";
    
	// Parameters for QRBill payement
	public static final String PARAMETER_USER_API_KEY = "userAPIKey";
	public static final String PARAMETER_MERCHANT_NAME = "merchantData";
	public static final String PARAMETER_LOYALITYNAME = "loyalityName";
	public static final String PARAMETER_NUMBEROFCOUPUNS = "numberOfCoupons";
	public static final String PARAMETER_DISCOUNTED_AMOUNT = "discountAmount";
	public static final String PARAMETER_DISCOUNTED_TYPE = "discountType";
	public static final String PARAMETER_REDEEMED_AMOUNT = "amountRedeemed";
	public static final String PARAMETER_REDEEMED_POINT = "pointsRedeemed";
	public static final String PARAMETER_TIP_AMOUNT = "tippingAmount";
    
    //parameters
    public static final String PARAMETER_SERVICE_NAME = "service";
    public static final String PARAMETER_TRANSACTIONNAME = "txnName";
    public static final String PARAMETER_CARD_PAN = "cardPan";
    public static final String PARAMETER_SOURCE_MDN = "sourceMDN";
    public static final String PARAMETER_NEW_PIN = "newPIN";
    public static final String PARAMETER_CONFIRM_NEW_PIN = "newPIN";
    public static final String PARAMETER_OTP = "otp";
    public static final String PARAMETER_NON_FINANCIAL_ENQUIRY = "mfaTransaction";
    
    public static final String PARAMETER_MFA_OTP = "mfaOtp";
    public static final String PARAMETER_CHANNEL_ID = "channelID";
    public static final String PARAMETER_CONFIRM_PIN = "confirmPIN";
    public static final String PARAMETER_SOURCE_PIN = "sourcePIN";
    public static final String PARAMETER_TRANSACTIONID = "transactionID";
    public static final String PARAMETER_DEST_MDN = "destMDN";
    public static final String PARAMETER_AMOUNT = "amount";
    public static final String PARAMETER_SECRET_ANSWER = "secretAnswer";
    public static final String PARAMETER_CONTACT_NO = "contactNumber";
    public static final String PARAMETER_EMAIL = "email";
    public static final String PARAMETER_LANG = "language";
    public static final String PARAMETER_NOTIFICATION_METHOD = "notificationMethod";
    public static final String PARAMETER_PARTNER_CODE = "partnerCode";
    public static final String PARAMETER_AGENT_CODE = "agentCode";
    public static final String PARAMETER_BUCKET_TYPE = "bucketType";
    public static final String PARAMETER_SRC_POCKET_CODE = "sourcePocketCode";
    public static final String PARAMETER_CARDPAN_SUFFIX = "cardPANSuffix";
    public static final String PARAMETER_SRC_MESSAGE = "sourceMessage";
    public static final String PARAMETER_DEST_POCKET_CODE = "destPocketCode";
    public static final String PARAMETER_TRANSFER_ID = "transferID";
    public static final String PARAMETER_CONFIRMED = "confirmed";
    public static final String PARAMETER_PARENTTXN_ID = "parentTxnID";
    public static final String PARAMETER_ISDEFAULT = "isDefault";
    public static final String PARAMETER_BANK_ID = "bankID";
    public static final String PARAMETER_BILLER_NAME = "billerName";
    public static final String PARAMETER_CUSTOMER_ID = "customerID";
    public static final String PARAMETER_BILL_DETAILS = "billDetails";
    public static final String PARAMETER_DATA = "data";
    public static final String PARAMETER_MFS_BILLER_CODE = "mfsBillerCode";
    public static final String PARAMETER_INVOICE_NO = "invoiceNo";
    public static final String PARAMETER_SUB_MDN = "subMDN";
    public static final String PARAMETER_SUB_FIRSTNAME = "subFirstName";
    public static final String PARAMETER_SUB_LASTNAME = "subLastName";
    public static final String PARAMETER_ACCOUNT_TYPE = "accountType";
    public static final String PARAMETER_APPLICATION_ID = "appId";
    public static final String PARAMETER_DOB = "dob";
    public static final String PARAMETER_SALT = "salt";
    public static final String PARAMETER_AUTHENTICATION_STRING = "authenticationString";
    public static final String PARAMETER_ACTIVATION_NEWPIN = "activationNewPin";
    public static final String PARAMETER_ACTIVATION_CONFIRMPIN = "activationConfirmPin";
    public static final String PARAMETER_COMPANY_ID="companyID";
    
    public static final String PARAMETER_APPTYPE = "apptype";
    public static final String PARAMETER_APPVERSION = "appversion";
    public static final String PARAMETER_APPOS = "appos";
    
    public static final String PARAMETER_DEST_BANK_CODE = "destBankCode";
    public static final String PARAMETER_DEST_ACCOUNT_NO = "destAccountNo";
    
   // Parameters for Bill payement  
    public static final String PARAMETER_BILLER_CODE = "billerCode";
    public static final String PARAMETER_BILL_NO="billNo";
    public static final String PARAMETER_PAYMENT_MODE="paymentMode";
    	
    //rajkumer added for DestBankAccount
    public static final String PARAMETER_DEST_BankAccount ="destBankAccount";
    
    public static final String MESSAGE_MOBILE_TRANSFER = "Mobile Transfer";
    public static final String DUMMY_BANK_ID = "Not Yet";
    public static final String CONSTANT_VALUE_ZERO = "0";
    public static final String CONSTANT_VALUE_TRUE = "true";
    public static final String SERVICE_PROVIDER_NAME = "serviceProviderName";
    public static final String CONSTANT_VALUE_FALSE = "false";
    public static final String CONSTANT_CHANNEL_ID = "7";
    public static final String CONSTANT_EMONEY = "E Money";
    public static final String CONSTANT_BANK = "Bank";
    public static final String CONSTANT_AIRTIME = "Airtime";
    public static final int COLOUR_GRAY = 0xB5B5B5;
    public static final int COLOUR_SLATEGRAY = 0x9FB6CD;
    public static final String XML_MESSAGE = "message";
    public static final String XML_ADITIONAL_INFO = "AdditionalInfo";
    public static final String XML_PUBLIC_MODULUS = "PublicKeyModulus";
    public static final String XML_PUBLIC_EXPONENT = "PublicKeyExponent";
    public static final String XML_SUCCESS= "Success";
    public static final String XML_DEST_CUST_NAME = "destinationName";
    public static final String XML_DEST_MDN = "destinationMDN";
    public static final String XML_DEST_BANK= "destinationBank";
    public static final String XML_ACCOUNT_NUMBER = "destinationAccountNumber";
    public static final String XML_AMOUNT_TRANSFER= "debitamt";
    public static final String XML_TRANSACTION_TIME = "transactionTime";
    public static final String XML_REFID = "refID";
    public static final String XML_TRANSFERID = "transferID";
    public static final String XML_SCTL = "sctlID";
    public static final String XML_MFAMODE = "mfaMode";
    public static final String XML_PARENT_TXNID = "parentTxnID";
    public static final String XML_INPUT = "input";
    public static final String XML_NAME = "name";
    public static final String XML_VALUE = "value";
    public static final String XML_AMOUNT = "amount";
    public static final String XML_BILL_DETAILS = "billDetails";
    public static final String XML_KEY = "key";
    public static final String XML_SALT = "salt";
    public static final String XML_AUTHENTICATION = "authentication";
    public static final String XML_TRANSACTION_CHARGES = "charges";
    public static final String XML_DEBIT_AMOUNT = "debitamt";
    public static final String XML_CREDIT_AMOUNT = "creditamt";
    public static final String XML_MIGRATE_TOKEN = "migrateToken";
    public static final String XML_APPUPDATEURL="url";
    public static final String XML_REGISTRATION_MEDIUM="RegistrationMedium";
    public static final String XML_RESET_PIN_REQUEST="ResetPinRequested";
    public static final String XML_STATUS="Status";
    public static final String XML_IS_ALREADY_ACTIVATED="IsAlreadyActivated";
    public static final String XML_PROMO_IMAGE_URL="promoImageURL";
    public static final String XML_USER_API_KEY = "userAPIKey";
    public static final String XML_INVOICE_NO = "invoiceNo";
    public static final String XML_SIMOBIPLUS_UPGRADE = "simobiPlusUpgrade";
    //NOTIFICATIONS
    public static final String NOTIFICATIONCODE_WRONGPINSPECIFIED = "29";
    public static final String NOTIFICATIONCODE_TRANSFERINQUIRY_SUCCESS = "72";
    public static final String FEATURE_TRANSFER_SELF = "Transfer to Self";
    public static final String FEATURE_TRANSFER_OTHERS = "Transfer to Others";
    public static final String FEATURE_TRANSFER_EAZYTOBANK = "eaZyMoney To Bank";
    public static final String FEATURE_TRANSFER_BANKTOEAZY = "Bank to eaZyMoney";
    public static final String FEATURE_TRANSFER = "Transfer";
    public static final String FEATURE_BUY = "Buy";
    public static final String FEATURE_PAY = "Pay";
    public static final String FEATURE_MYACCOUNT = "Account";
    public static final String FEATURE_TO_SELF = "To Self";
    public static final String FEATURE_TO_FRIENDS = "To Friends";
    public static final String LABEL_BANK = "Bank";
    public static final String LABEL_EMONEY = "eaZyMoney";
    public static final String LABEL_BANK_TO_EMONEY = "Bank to eaZyMoney";
    public static final String LABEL_EMONEY_TO_BANK = "eaZyMoney to Bank";
    public static final String POCKET_CODE_EMONEY = "1";
    public static final String POCKET_CODE_BANK = "2";
    public static final String DISCLOSURE_LEGAL = "Legal Disclosure for Mobile Money Service " + "\n" + "I agree to the terms of this service";
    
}
