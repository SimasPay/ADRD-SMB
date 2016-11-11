package com.mfino.bsim.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.util.Log;

import com.mfino.bsim.R;
import com.mfino.bsim.containers.ValueContainer;

public class WebServiceHttp extends Activity {

	private ValueContainer edContainer;
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	Context context;
	String otpCode;
	Context ctx;
	String httpString,httpsString;
	AssetManager assetManager;
	
	
	//Prduction
	//public static String webAPIUrlFiles="http://simobi.banksinarmas.com/webapi/dynamic";
	//public static String webAPIUrl ="https://simobi.banksinarmas.com/webapi/sdynamic";
	
	//Devserver
	//public static String webAPIUrlFiles="http://175.101.5.75:8080/webapi/dynamic";
	//public static String webAPIUrl ="https://175.101.5.75:8443/webapi/sdynamic";
	
	//Local
	//public static String webAPIUrlFiles="http://175.101.5.70:8080/webapi/dynamic";
	//public static String webAPIUrl ="https://175.101.5.70:8443/webapi/sdynamic";

	/*	//india
		public static String webAPIUrlFiles;
		public static String webAPIUrl;*/
	
	//UAT
	public static String webAPIUrlFiles="http://dev.simobi.banksinarmas.com/webapi/dynamic";
	public static String webAPIUrl ="https://dev.simobi.banksinarmas.com/webapi/sdynamic";
	
	
		//Development Server
		/*
		public static String webAPIUrlFiles="http://staging.dimo.co.id:8082/webapi/dynamic";//Public
		public static String webAPIUrl ="https://staging.dimo.co.id:8445/webapi/sdynamic";//
*/		
		//Development Server
		
	/*	public static String webAPIUrlFiles="http://175.101.5.69:8080/webapi/dynamic";//Public
		public static String webAPIUrl ="https://175.101.5.69:8443/webapi/sdynamic";//
*/				

	public WebServiceHttp(ValueContainer edContainer, Context context) {
		this.edContainer = edContainer;
		this.context = context;
		/*assetManager = context.getAssets();
		UrlParse();
		webAPIUrlFiles=httpString;
		webAPIUrl=httpsString;*/
		System.out.println("Testing>>>webservices");
	}

	@SuppressWarnings("static-access")
	public String getUrl() {
		
		
		String requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID
				+ "=" + Constants.CONSTANT_CHANNEL_ID + "&"
				+ Constants.PARAMETER_SERVICE_NAME + "="
				+ edContainer.getServiceName() + "&"
				+ Constants.PARAMETER_SOURCE_MDN + "="
				+ edContainer.getSourceMdn() + "&"
				+ Constants.PARAMETER_SOURCE_PIN + "="
				+ edContainer.getSourcePin() + "&"
				+ Constants.PARAMETER_TRANSACTIONNAME + "="
				+ edContainer.getTransactionName();

		if (Constants.TRANSACTION_ACTIVATION.equals(edContainer
				.getTransactionName())) {
			System.out.println("Testing>>>>mfa Otp>>service>>"+edContainer.getMfaMode() );
			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn()

					+ "&" + Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_OTP + "=" + edContainer.getOTP().trim()
					+ "&" + Constants.PARAMETER_ACTIVATION_CONFIRMPIN + "="
					+ edContainer.getActivationConfirmPin() + "&"
					+ Constants.PARAMETER_ACTIVATION_NEWPIN + "="
					+ edContainer.getActivationNewPin()+ "&";
			
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getMfaOTP().trim()+ "&"
							+ Constants.PARAMETER_PARENTTXN_ID + "="
							+ edContainer.getSctl().trim() + "&"
							+ Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="
							+ "Confirm";
				} else {
					requestUrl += Constants.PARAMETER_NON_FINANCIAL_ENQUIRY
							+ "=" + "Inquiry";
				}
			} catch (Exception e) {
				requestUrl += Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="+ "Inquiry";
			}

			System.out.println(" Inside the code....................");

		} else if (Constants.TRANSACTION_RESET_PIN.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_OTP + "=" + edContainer.getOTP().trim()
					+ "&" + Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_ACTIVATION_CONFIRMPIN + "="
					+ edContainer.getActivationConfirmPin() + "&"
					+ Constants.PARAMETER_ACTIVATION_NEWPIN + "="
					+ edContainer.getActivationNewPin();

			System.out.println(" Inside the code....................");

		} else if (Constants.TRANSACTION_REACTIVATION.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_SOURCE_PIN + "="
					+ edContainer.getBankPin() + "&"
					+ Constants.PARAMETER_CARD_PAN + "="
					+ edContainer.getCardPan() + "&"
					+ Constants.PARAMETER_ACTIVATION_NEWPIN + "="
					+ edContainer.getActivationNewPin() + "&"
					+ Constants.PARAMETER_ACTIVATION_CONFIRMPIN + "="
					+ edContainer.getActivationConfirmPin()+ "&";
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getMfaOTP().trim()+"&"
							+Constants.PARAMETER_PARENTTXN_ID + "="
							+ edContainer.getSctl().trim()+"&"
							+ Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="+ "Confirm";
				}else{
					requestUrl +=Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="+ "Inquiry";
				}
			} catch (Exception e) {
				requestUrl +=Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="
						+ "Inquiry";
			}

			System.out.println(" Inside the code....................");

		} else if (Constants.TRANSACTION_RESEND_OTP.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName();
			System.out.println(" Inside the code....................");

		} else if (Constants.TRANSACTION_REGISTRATION_MEDIUM.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName();
			System.out.println(" Inside the code....................");

		} else if (Constants.TRANSACTION_LOGIN.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_AUTHENTICATION_STRING + "="
					+ edContainer.getSourcePin()+ "&"
					+ Constants.PARAMETER_APPOS + "=" + edContainer.getAppOS()
					+ "&" + Constants.PARAMETER_APPVERSION + "="
					+ edContainer.getAppVersion() + "&"
					+ Constants.PARAMETER_APPTYPE + "="
					+ edContainer.getAppType();

		} else if (Constants.TRANSACTION_TRANSFER_INQUIRY.equals(edContainer
				.getTransactionName())) {
			// System.out.println("url :"+edContainer.getTransferType());
			if (edContainer.getTransferType().equals("toBankSinarmas")) {
				System.out.println("raaaaaaa");
				requestUrl += "&" + Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode() + "&"
						+ Constants.PARAMETER_AMOUNT + "="
						+ edContainer.getAmount() + "&"
						+ Constants.PARAMETER_DEST_POCKET_CODE + "="
						+ edContainer.getDestinationPocketCode() + "&"
						+ Constants.PARAMETER_DEST_BankAccount + "="
						+ edContainer.getDestinationBankAccount();
				/*
				 * +"&" + Constants.PARAMETER_SUB_FIRSTNAME + "=" +
				 * edContainer.getFirstName() + "&" +
				 * Constants.PARAMETER_SUB_LASTNAME + "=" +
				 * edContainer.getLastName();
				 */
				/*
				 * if (edContainer.getUnregistered() == true) { requestUrl =
				 * requestUrl + "&"
				 */
				// }
			} else {
				// System.out.println("raaaaaaa1111111111111111");
				requestUrl += "&" + Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode() + "&"
						+ Constants.PARAMETER_AMOUNT + "="
						+ edContainer.getAmount() + "&"
						+ Constants.PARAMETER_DEST_POCKET_CODE + "="
						+ edContainer.getDestinationPocketCode() + "&"
						+ Constants.PARAMETER_DEST_MDN + "="
						+ edContainer.getDestinationMdn();
				/*
				 * if (edContainer.getUnregistered() == true) { requestUrl =
				 * requestUrl + "&" + Constants.PARAMETER_SUB_FIRSTNAME + "=" +
				 * edContainer.getFirstName() + "&" +
				 * Constants.PARAMETER_SUB_LASTNAME + "=" +
				 * edContainer.getLastName(); }TRANSACTION_Uangku_INQUIRY
				 */
			}

		} else if (Constants.TRANSACTION_TRANSFER.equals(edContainer
				.getTransactionName())) {
			if (edContainer.getTransferType().equals("toBankSinarmas")) {
				// System.out.println("tranferrrr111111111");

				requestUrl += "&" + Constants.PARAMETER_TRANSFER_ID + "="
						+ edContainer.getTransferId() + "&"
						+ Constants.PARAMETER_DEST_BankAccount + "="
						+ edContainer.getDestinationBankAccount() + "&"
						+ Constants.PARAMETER_CONFIRMED + "="
						+ edContainer.getConfirmed() + "&"
						+ Constants.PARAMETER_PARENTTXN_ID + "="
						+ edContainer.getParentTxnId() + "&"
						+ Constants.PARAMETER_DEST_POCKET_CODE + "="
						+ edContainer.getDestinationPocketCode() + "&"
						+ Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode();
				try {
					if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
						requestUrl +="&"+Constants.PARAMETER_MFA_OTP + "="
								+ edContainer.getOTP().trim();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// System.out.println("tranferrrr2222222");
				requestUrl += "&" + Constants.PARAMETER_TRANSFER_ID + "="
						+ edContainer.getTransferId() + "&"
						+ Constants.PARAMETER_DEST_MDN + "="
						+ edContainer.getDestinationMdn() + "&"
						+ Constants.PARAMETER_CONFIRMED + "="
						+ edContainer.getConfirmed() + "&"
						+ Constants.PARAMETER_PARENTTXN_ID + "="
						+ edContainer.getParentTxnId() + "&"
						+ Constants.PARAMETER_DEST_POCKET_CODE + "="
						+ edContainer.getDestinationPocketCode() + "&"
						+ Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode();
				try {
					if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
						requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
								+ edContainer.getOTP().trim();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}	
		
		//transferID=195319&sourceMDN=6281295927618&confirmed=true&parentTxnID=2110821&
		//institutionID=simobi&channelID=7&service=Bank&mfaOtp=xxxx&authenticationKey=
			//	&sourcePocketCode=2&txnName=TransferToUangku
		
		
		 else if (Constants.TRANSACTION_Uangku_CONFIRM.equals(edContainer
					.getTransactionName())) {
				if (edContainer.getTransferType().equals("toUnagku")) {
					// System.out.println("tranferrrr111111111");

					requestUrl += "&" + Constants.PARAMETER_TRANSFER_ID + "="
							+ edContainer.getTransferId() + "&"
							
							+ Constants.PARAMETER_CONFIRMED + "="
							+ edContainer.getConfirmed() + "&"
							+ Constants.PARAMETER_PARENTTXN_ID + "="
							+ edContainer.getParentTxnId() + "&"
							//+ "institutionID=simobi&"
							+"channelID=7&"
						+"&authenticationKey=&"
						+"bankID=&"
							
							+ Constants.PARAMETER_SRC_POCKET_CODE + "="
							+ edContainer.getSourcePocketCode();
					try {
						if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
							requestUrl +="&"+Constants.PARAMETER_MFA_OTP + "="
									+ edContainer.getOTP().trim();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// System.out.println("tranferrrr2222222");
					requestUrl += "&" + Constants.PARAMETER_TRANSFER_ID + "="
							+ edContainer.getTransferId() + "&"
							+ Constants.PARAMETER_DEST_MDN + "="
							+ edContainer.getDestinationMdn() + "&"
							+ Constants.PARAMETER_CONFIRMED + "="
							+ edContainer.getConfirmed() + "&"
							+ Constants.PARAMETER_PARENTTXN_ID + "="
							+ edContainer.getParentTxnId() + "&"
							+ Constants.PARAMETER_DEST_POCKET_CODE + "="
							+ edContainer.getDestinationPocketCode() + "&"
							+ Constants.PARAMETER_SRC_POCKET_CODE + "="
							+ edContainer.getSourcePocketCode();
					try {
						if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
							requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
									+ edContainer.getOTP().trim();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}	
			
		
		
		
		else if (Constants.TRANSACTION_Uangku_INQUIRY.equals(edContainer
				.getTransactionName())) {
			//Log.e("url:::::::::", edContainer.getTransferType());

			if (edContainer.getTransferType().equals("toUnagku")) {
				// System.out.println("tranferrrr111111111");
				Log.e("Transfer_to_uangku", "testing");

				requestUrl += "&" + Constants.PARAMETER_AMOUNT + "="
						+ edContainer.getAmount() + "&"
						+ Constants.PARAMETER_DEST_ACCOUNT_NO + "="
						+ edContainer.getDestinationBankAccount() + "&"						
						//+ "institutionID=simobi&"
						+"&authenticationKey=&"
						+"bankID=&"
						+ Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode();
				try {
					if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
						requestUrl +="&"+Constants.PARAMETER_MFA_OTP + "="
								+ edContainer.getOTP().trim();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.e("test_url>>>>>>>>>>>>",requestUrl);
				//System.out.println("Testing>>>url@@@@@@@@@@@@@@@"+requestUrl);

			} else {
				//Log.e("elseeeeeeee","callinggggggggggg");

				// System.out.println("tranferrrr2222222");
				requestUrl += "&" + Constants.PARAMETER_TRANSFER_ID + "="
						+ edContainer.getTransferId() + "&"
						+ Constants.PARAMETER_DEST_MDN + "="
						+ edContainer.getDestinationMdn() + "&"
						+ Constants.PARAMETER_CONFIRMED + "="
						+ edContainer.getConfirmed() + "&"
						+ Constants.PARAMETER_PARENTTXN_ID + "="
						+ edContainer.getParentTxnId() + "&"
						+ Constants.PARAMETER_DEST_POCKET_CODE + "="
						+ edContainer.getDestinationPocketCode() + "&"
						+ Constants.PARAMETER_SRC_POCKET_CODE + "="
						+ edContainer.getSourcePocketCode();
				try {
					if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
						requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
								+ edContainer.getOTP().trim();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		else if (Constants.TRANSACTION_CHECKBALANCE.equals(edContainer
				.getTransactionName())) {

			requestUrl += "&" + Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode();

		} else if (Constants.TRANSACTION_HISTORY.equals(edContainer
				.getTransactionName())) {

			requestUrl += "&" + Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode();
		} else if (Constants.TRANSACTION_PURCHASE_INQUIRY.equals(edContainer
				.getTransactionName())) {

			requestUrl += "&" + Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_PARTNER_CODE + "="
					+ edContainer.getPartnerCode();
		}

		else if (Constants.TRANSACTION_PURCHASE.equals(edContainer
				.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_PARTNER_CODE
					+ "=" + edContainer.getPartnerCode() + "&"
					+ Constants.PARAMETER_PARENTTXN_ID + "="
					+ edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed();
		}

		else if (Constants.TRANSACTION_CHANGEPIN.equals(edContainer
				.getTransactionName())) {
			System.out.println("Testing>>>change pin");
			
			requestUrl = requestUrl + "&" + Constants.PARAMETER_NEW_PIN + "="
					+ edContainer.getNewPin() + "&"
					+ Constants.PARAMETER_CONFIRM_PIN + "="
					+ edContainer.getConfirmPin()+"&";
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP().trim()+"&"
							+Constants.PARAMETER_PARENTTXN_ID + "="
							+ edContainer.getSctl()+"&"
							+ Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="+ "Confirm";
					
				}else{
					requestUrl +=Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="+ "Inquiry";
				}
			} catch (Exception e) {
				requestUrl +=Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="
						+ "Inquiry";
			}
			System.out.println("Testing>>>url"+requestUrl);
		}
		
	/*	else if (Constants.TRANSACTION_CHANGEPIN_CONFIRM.equals(edContainer.getTransactionName())) {
			System.out.println("Testing>>>Confirm");
			requestUrl = requestUrl + "&" + Constants.PARAMETER_NEW_PIN + "="
					+ edContainer.getNewPin() + "&"
					+ Constants.PARAMETER_NON_FINANCIAL_ENQUIRY + "="
					+ "Confirm" + "&" 
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ "ChangePIN"+ "&"
					+ Constants.PARAMETER_CONFIRM_PIN + "="
					+ edContainer.getConfirmPin();
			System.out.println("Testing>>>url"+requestUrl);
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP();
				}
			} catch (Exception e) {
				
			}
			System.out.println("Testing>>>url"+requestUrl);
		}*/
		

		else if (Constants.TRANSACTION_CASHOUT_INQUIRY.equals(edContainer
				.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_AGENT_CODE + "="
					+ edContainer.getAgentCode() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode();
		}

		else if (Constants.TRANSACTION_CASHOUT.equals(edContainer
				.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_AGENT_CODE
					+ "=" + edContainer.getAgentCode() + "&"
					+ Constants.PARAMETER_PARENTTXN_ID + "="
					+ edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed();
		}else if (Constants.TRANSACTION_BILL_INQUIRY.equals(edContainer
				.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE
					+ "=" + edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_BILLER_CODE + "="
					+ edContainer.getBillerCode() + "&"
					+ Constants.PARAMETER_BILL_NO + "="
					+ edContainer.getBillNo();
		}else if (Constants.TRANSACTION_BILLPAYMENT_INQUIRY.equals(edContainer
				.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE
					+ "=" + edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_BILLER_CODE + "="
					+ edContainer.getBillerCode() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_BILL_NO + "="
					+ edContainer.getBillNo();
		} else if (Constants.TRANSACTION_BILLPAYMENT.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_SRC_POCKET_CODE
					+ "=" + edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_PARENTTXN_ID + "="
					+ edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed() + "&"
					+ Constants.PARAMETER_BILLER_CODE + "="
					+ edContainer.getBillerCode() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_BILL_NO + "="
					+ edContainer.getBillNo();
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP().trim();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (Constants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY
				.equals(edContainer.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_SRC_POCKET_CODE
					+ "=" + edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_DEST_MDN + "="
					+ edContainer.getDestinationMdn() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_COMPANY_ID + "="
					+ edContainer.getCompanyId();
		}

		else if (Constants.TRANSACTION_AIRTIME_PURCHASE.equals(edContainer
				.getTransactionName())) {

			requestUrl = webAPIUrl + "?" + Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_DEST_MDN + "="
					+ edContainer.getDestinationMdn() + "&"
					+ Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_PARENTTXN_ID + "="
					+ edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed() + "&"
					+ Constants.PARAMETER_COMPANY_ID + "="
					+ edContainer.getCompanyId();
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl +="&"+Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP().trim();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (Constants.TRANSACTION_INTERBANK_TRANSFER_INQUIRY
				.equals(edContainer.getTransactionName())) {
			requestUrl = requestUrl + "&" + Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_DEST_ACCOUNT_NO + "="
					+ edContainer.getDestinationBankAccount() + "&"
					+ Constants.PARAMETER_DEST_BANK_CODE + "="
					+ edContainer.getBankCode() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_DEST_POCKET_CODE + "="
					+ edContainer.getDestinationPocketCode();
		} else if (Constants.TRANSACTION_INTERBANK_TRANSFER.equals(edContainer
				.getTransactionName())) {

			requestUrl = requestUrl + "&" + Constants.PARAMETER_PARENTTXN_ID
					+ "=" + edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed() + "&"
					+ Constants.PARAMETER_SRC_POCKET_CODE + "="
					+ edContainer.getSourcePocketCode();
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP().trim();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			}
		}else if (Constants.TRANSACTION_QR_BILLPAYMENT_INQUIRY.equals(edContainer
				.getTransactionName())) {
			
//			paymentMode=QRPayment&channelID=7&billerCode=QRFLASHIZ&amount=15000.0&
//			merchantData=Fachrudin Foodcourt &sourceMDN=628812344188&sourcePIN=xxxxxx&service=Payment
//			&sourcePocketCode=2&txnName=QRPaymentInquiry
			
			
			requestUrl =requestUrl+"&"+ Constants.PARAMETER_SRC_POCKET_CODE+ "=" 
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_CHANNEL_ID+ "=" 
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					
					+ Constants.PARAMETER_BILLER_CODE + "="
					+ edContainer.getBillerCode() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_MERCHANT_NAME + "="
					+ edContainer.getMerchantName().replace(" ", "+")+ "&"
					+ Constants.PARAMETER_USER_API_KEY + "="
					+ edContainer.getUserApiKey() + "&"
					+ Constants.PARAMETER_BILL_NO + "="
					+ edContainer.getBillNo()+ "&"
					+ Constants.PARAMETER_LOYALITYNAME + "="
					+ edContainer.getLoyalityName()+ "&"
					+ Constants.PARAMETER_NUMBEROFCOUPUNS + "="
					+ edContainer.getNumberofCoupuns()+ "&"
					+ Constants.PARAMETER_DISCOUNTED_AMOUNT + "="
					+ edContainer.getDiscountAmount()+ "&"
					+ Constants.PARAMETER_DISCOUNTED_TYPE + "="
					+ edContainer.getDiscountType()+ "&"
					+ Constants.PARAMETER_REDEEMED_AMOUNT + "="
					+ edContainer.getRedeemAmount()+ "&"
					+ Constants.PARAMETER_REDEEMED_POINT + "="
					+ edContainer.getRedeemPoints()+ "&"
					+ Constants.PARAMETER_TIP_AMOUNT + "="
					+ edContainer.getTipAmount();
			
		} else if (Constants.TRANSACTION_QR_BILLPAYMENT.equals(edContainer
				.getTransactionName())) {

			requestUrl = requestUrl+"&"+Constants.PARAMETER_SRC_POCKET_CODE+ "=" 
					+ edContainer.getSourcePocketCode() + "&"
					+ Constants.PARAMETER_CHANNEL_ID + "="
					+ Constants.CONSTANT_CHANNEL_ID + "&"
					+ Constants.PARAMETER_SERVICE_NAME + "="
					+ edContainer.getServiceName() + "&"
					+ Constants.PARAMETER_SOURCE_MDN + "="
					+ edContainer.getSourceMdn() + "&"
					+ Constants.PARAMETER_TRANSACTIONNAME + "="
					+ edContainer.getTransactionName() + "&"
					+ Constants.PARAMETER_PARENTTXN_ID + "="
					+ edContainer.getParentTxnId() + "&"
					+ Constants.PARAMETER_TRANSFER_ID + "="
					+ edContainer.getTransferId() + "&"
					+ Constants.PARAMETER_CONFIRMED + "="
					+ edContainer.getConfirmed() + "&"
					+ Constants.PARAMETER_BILLER_CODE + "="
					+ edContainer.getBillerCode() + "&"
					+ Constants.PARAMETER_PAYMENT_MODE + "="
					+ edContainer.getPaymentMode() + "&"
					+ Constants.PARAMETER_AMOUNT + "="
					+ edContainer.getAmount() + "&"
					+ Constants.PARAMETER_MERCHANT_NAME + "="
					+ edContainer.getMerchantName().replace(" ", "+")+ "&"
					+ Constants.PARAMETER_USER_API_KEY + "="
					+ edContainer.getUserApiKey() + "&"
					+ Constants.PARAMETER_BILL_NO + "="
					+ edContainer.getBillNo()+ "&"
					+ Constants.PARAMETER_LOYALITYNAME + "="
					+ edContainer.getLoyalityName()+ "&"
					+ Constants.PARAMETER_NUMBEROFCOUPUNS + "="
					+ edContainer.getNumberofCoupuns()+ "&"
					+ Constants.PARAMETER_DISCOUNTED_AMOUNT + "="
					+ edContainer.getDiscountAmount()+ "&"
					+ Constants.PARAMETER_DISCOUNTED_TYPE + "="
					+ edContainer.getDiscountType()+ "&"
					+ Constants.PARAMETER_REDEEMED_AMOUNT + "="
					+ edContainer.getRedeemAmount()+ "&"
					+ Constants.PARAMETER_REDEEMED_POINT + "="
					+ edContainer.getRedeemPoints()+ "&"
					+ Constants.PARAMETER_TIP_AMOUNT + "="
					+ edContainer.getTipAmount();
			try {
				if (edContainer.getMfaMode().equalsIgnoreCase("OTP")) {
					requestUrl += "&"+Constants.PARAMETER_MFA_OTP + "="
							+ edContainer.getOTP();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("URL: " + requestUrl);
		//System.out.println("URL: " );

		return requestUrl;
	}

	/**
	 * This method used for service call and handled the HTTPS response with SSL
	 * certification.
	 */

	@SuppressLint({ "ParserError", "ParserError", "TrulyRandom" })
	public String getResponseSSLCertificatation() {

		String contents = null;

		// Load the self-signed server certificate
		char[] passphrase = "DDTCert".toCharArray();
		KeyStore ksTrust = null;

		try {
			ksTrust = KeyStore.getInstance("BKS");
		} catch (KeyStoreException e1) {

			e1.printStackTrace();
		}

		try {
			ksTrust.load(context.getResources().openRawResource(R.raw.ddtcert),
					passphrase);
		} catch (NoSuchAlgorithmException e1) {

			e1.printStackTrace();
		} catch (java.security.cert.CertificateException e1) {

			e1.printStackTrace();
		} catch (NotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		TrustManagerFactory tmf = null;

		try {
			tmf = TrustManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e1) {

			e1.printStackTrace();
		}

		try {
			tmf.init(ksTrust);
		} catch (KeyStoreException e1) {

			e1.printStackTrace();
		}

		// Create a SSLContext with the certificate
		SSLContext sslContext = null;

		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e1) {

			e1.printStackTrace();
		}

		try {
			sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
		} catch (KeyManagementException e1) {

			e1.printStackTrace();
		}

		URL url = null;
		// Create a HTTPS connection

		try {
			// Configure SSL Context
			try {
				sslContext = SSLContext.getInstance("TLS");
			} catch (NoSuchAlgorithmException e) {

				e.printStackTrace();
			}

			X509TrustManager nullTrustManager = new NullTrustManager();
			TrustManager[] nullTrustManagers = { nullTrustManager };
			try {
				sslContext.init(null, nullTrustManagers, new SecureRandom());
			} catch (KeyManagementException e) {

				e.printStackTrace();
			}

			url = new URL(getUrl());

		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		System.setProperty("http.keepAlive", "false");

		HttpsURLConnection conn = null;
		try {
				
			conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(new NullVerifier());
			conn.setSSLSocketFactory(sslContext.getSocketFactory());

			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setChunkedStreamingMode(0);
			conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
			conn.setReadTimeout(Constants.CONNECTION_TIMEOUT);
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setRequestMethod("POST");
			
			conn.setAllowUserInteraction(true);
			int rc = 0;
			
			rc = conn.getResponseCode();
			System.out.println("------------clen--------------------------------"+rc);


			if (rc == 0) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

					}
				}, SPLASH_DISPLAY_LENGHT);
			} else {

					InputStreamReader resultInputStream = new InputStreamReader(conn.getInputStream());
					BufferedReader rd = new BufferedReader(resultInputStream);
					String line;
					StringBuffer sb = new StringBuffer("");
					
					while ((line = rd.readLine()) != null) {
						sb.append(line+ "\n");
						// contents +=rd.readLine();

						Log.i("Nav", "version res:: " + line);
					}
					// contents = line;
					contents = sb.toString();
					System.out.println("-----------Check for content--------------------------------"+contents);
					Log.e("contents______________", contents+"");

					rd.close();
					resultInputStream.close();

				
			}
			System.out.println("------------ resp  --------------------------------"+ rc);
			
			Log.e("response))))))))))))))))", rc+"");

		}catch (SocketTimeoutException e) {
	        System.out.println("Time out " );
	        contents=null;
	    }catch (ConnectException e) {
	        System.out.println("connectionException ");
	        contents=null;
	    }catch (java.net.ProtocolException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
			//System.out.println(e.printStackTrace());
			contents=null;
		} finally {
			conn.disconnect();
		}

		return contents;
	}

	
	// iP code parsing..
		@SuppressWarnings("unused")
		private void UrlParse() {

			InputStream inputStream = null;
			try {
				inputStream = assetManager.open("ipChangeFile.csv");
				String str = "";
				StringBuffer buf = new StringBuffer();
				String cvsSplitBy = ",";

				BufferedReader reader = new BufferedReader(new InputStreamReader(
						inputStream));
				if (inputStream != null) {
					while ((str = reader.readLine()) != null) {
						buf.append(str + "\n");
						String[] parse = str.split(cvsSplitBy);
						httpString = parse[0];
						httpsString = parse[1];
						System.out.println("http= " + httpString+ " , https=" + httpsString + "]");
					}
				}
				inputStream.close();

			} catch (IOException e) {
			} finally {
				if (inputStream != null)
					try {
						inputStream.close();
					} catch (IOException e) {
					}
			}
		}
	

}
