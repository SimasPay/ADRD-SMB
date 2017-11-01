package com.mfino.bsim.services;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.kxml.Attribute;
import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.services.Constants;


public class XMLParser {

    public EncryptedResponseDataContainer parse(String xml) throws Exception {
        EncryptedResponseDataContainer result = new EncryptedResponseDataContainer();
        ByteArrayInputStream inStream = new ByteArrayInputStream(xml.getBytes());
        InputStreamReader reader = new InputStreamReader(inStream);
        XmlParser parser = new XmlParser(reader);
        traverse(parser, result);
        System.out.println("reson1 :"+xml);
        return result;	
    }

    public void traverse(XmlParser parser, EncryptedResponseDataContainer result) throws Exception {
        boolean leave = false;
        do {
            ParseEvent event = parser.read();
            ParseEvent pe;
            switch (event.getType()) {
                case Xml.START_TAG:
                    if (Constants.XML_MESSAGE.equals(event.getName())) {
                        pe = parser.read();
                        result.setMsg(pe.getText());
                        System.out.println("<<<<Message>>>"+result.getMsg());
                        try {
							result.setMsgCode(event.getAttribute("code").getValue());
						} catch (Exception e) {
						}
                    }else if (Constants.XML_ADITIONAL_INFO.equals(event.getName())) {
                        pe = parser.read();
                        result.setAditionalInfo(pe.getText());
                    }else if (Constants.XML_PUBLIC_MODULUS.equals(event.getName())) {
                        pe = parser.read();
                        result.setPublicKeyModulus(pe.getText());
                    }else if (Constants.XML_PUBLIC_EXPONENT.equals(event.getName())) {
                        pe = parser.read();
                        result.setPublicKeyExponet(pe.getText());
                    }else if (Constants.XML_SUCCESS.equals(event.getName())) {
                        pe = parser.read();
                        result.setSuccess(pe.getText());
                    }else if (Constants.XML_DEST_MDN.equals(event.getName())) {
                        pe = parser.read();
                        result.setDestMDN(pe.getText());
                    }else if (Constants.XML_DEST_CUST_NAME.equals(event.getName())) {
                        pe = parser.read();
                        result.setCustName(pe.getText());
                    }else if (Constants.XML_DEST_BANK.equals(event.getName())) {
                        pe = parser.read();
                        result.setDestBank(pe.getText());
                    }else if (Constants.XML_ACCOUNT_NUMBER.equals(event.getName())) {
                        pe = parser.read();
                        result.setAccountNumber(pe.getText());
                    }else if (Constants.XML_AMOUNT_TRANSFER.equals(event.getName())) {
                        pe = parser.read();
                        result.setAmount(pe.getText());
                    } else if (Constants.XML_TRANSACTION_TIME.equals(event.getName())) {
                        pe = parser.read();
                        result.setTransactionTime(pe.getText());
                    } else if (Constants.XML_REFID.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedRefId(pe.getText());
                    } else if (Constants.XML_TRANSFERID.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedTransferId(pe.getText());
                    } else if (Constants.XML_PARENT_TXNID.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedParentTxnId(pe.getText());
                    } else if (Constants.XML_SCTL.equals(event.getName())) {
                        pe = parser.read();
                        result.setSctl(pe.getText());
                    }else if (Constants.XML_MFAMODE.equals(event.getName())) {
                        pe = parser.read();
                        result.setMfaMode(pe.getText());
                    }else if (Constants.XML_INPUT.equals(event.getName())) {
                        Attribute name = event.getAttribute(Constants.XML_NAME);
                        Attribute value = event.getAttribute(Constants.XML_VALUE);

                        if ((null != name) && !("".equals(name.getValue()) && "ParentTransactionID".equals(name.getValue()))) {
                            if (value != null) {
                                result.setEncryptedParentTxnId(value.getValue());
                            }
                        }

                        if ((null != name) && !("".equals(name.getValue()) && "TransferID".equals(name.getValue()))) {
                            if (value != null) {
                                result.setEncryptedTransferId(value.getValue());
                            }
                        }
                    } else if (Constants.XML_AMOUNT.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedAmount(pe.getText());
                    } else if (Constants.XML_KEY.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedAESkey(pe.getText());
                    } else if (Constants.XML_SALT.equals(event.getName())) {
                        pe = parser.read();
                        result.setSalt(pe.getText());
                    } else if (Constants.XML_AUTHENTICATION.equals(event.getName())) {
                        pe = parser.read();
                        result.setAuthenticationString(pe.getText());
                    } else if (Constants.XML_TRANSACTION_CHARGES.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedTransactionCharges(pe.getText());
                    } else if (Constants.XML_DEBIT_AMOUNT.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedDebitAmount(pe.getText());
                    } else if (Constants.XML_CREDIT_AMOUNT.equals(event.getName())) {
                        pe = parser.read();
                        result.setEncryptedCreditAmount(pe.getText());
                    } else if (Constants.XML_MIGRATE_TOKEN.equals(event.getName())) {
                        pe = parser.read();
                        result.setMigrateToken(pe.getText());
                    }else if (Constants.XML_APPUPDATEURL.equals(event.getName())) {
                        pe = parser.read();
                        result.setAppUpdateURL(pe.getText());
                    }else if (Constants.XML_REGISTRATION_MEDIUM.equals(event.getName())) {
                        pe = parser.read();
                        result.setRegistrationMedium(pe.getText());
                    }else if (Constants.XML_RESET_PIN_REQUEST.equals(event.getName())) {
                        pe = parser.read();
                        result.setResetPinRequested(pe.getText());
                    }else if (Constants.XML_IS_ALREADY_ACTIVATED.equals(event.getName())) {
                        pe = parser.read();
                        result.setIsActivated(pe.getText());
                    }else if (Constants.XML_STATUS.equals(event.getName())) {
                        pe = parser.read();
                        result.setStatus(pe.getText());
                    }else if (Constants.XML_USER_API_KEY.equals(event.getName())) {
                        pe = parser.read();
                        result.setUserApiKey(pe.getText());
                    }
                    traverse(parser, result); // recursion call for each <tag></tag>
                    break;

                case Xml.END_TAG:
                    leave = true;
                    break;

                case Xml.END_DOCUMENT:
                    leave = true;
                    break;

                case Xml.TEXT:
                    break;

                case Xml.WHITESPACE:
                    break;

                default:
            }
        } while (!leave);
    }
//	public static void main(String[] args) throws Exception {
//		XMLParser p = new XMLParser();
//		String str = new String("<?xml version='1.0'?><response>" +
//				"<message code='72'>You requested to transfer IDR 120000 to 629876543210 -- XYZ.</message>" +
//				"<transactionTime>28/01/2011 12:22</transactionTime><transferID>1000123</transferID>" +
//				"<parentTxnID>78911231</parentTxnID></response>");
//		ResponseDataContainer res = p.parse(str);
//		System.out.println("*********** = " + res.toString());
//	}
}

