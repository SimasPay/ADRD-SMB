package com.mfino.bsim.services;

public class Product {

	String name;
	String code;
	String denom;
	String paymentMode;
	String invoiceType;
	boolean isCCPayment;
	
	public boolean isCCPayment() {
		return isCCPayment;
	}

	public void setCCPayment(boolean isCCPayment) {
		this.isCCPayment = isCCPayment;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}


	public Product(String name,String code,String denom,String paymentMode,String invoiceType,boolean isCCPayment) {
		this.name = name;
		this.code = code;
		this.denom = denom;

		this.paymentMode=paymentMode;
		this.invoiceType=invoiceType;
		this.isCCPayment=isCCPayment;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDenom() {
		return this.denom;
	}
	public void setDenom(String denom) {
		this.denom = denom;
	}

	@Override
	public String toString() {
		return this.name + "-" +this.code +"-"+this.denom;
	}


}
