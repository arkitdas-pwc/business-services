package org.egov.receipt.consumer.model;

import java.math.BigDecimal;

public class MisReceiptsPOJO {
	
	private String payments_id;
	private BigDecimal total_amt_paid;
	private String receipt_number;
	private Long receipt_date;
	private String paid_by;
	private String payer_address;
	private String narration;
	private String payment_status;
	private String bank_name;
	private String bank_branch;
	private String subdivison;
	private String servicename;
	private String collectedbyname;
	private String gstno;
	private String payment_mode;
	private String chequeddno;
	private Long chequedddate;
	
	
	public String getPayments_id() {
		return payments_id;
	}
	public void setPayments_id(String payments_id) {
		this.payments_id = payments_id;
	}
	
	public String getReceipt_number() {
		return receipt_number;
	}
	public void setReceipt_number(String receipt_number) {
		this.receipt_number = receipt_number;
	}
	
	
	
	
	public Long getReceipt_date() {
		return receipt_date;
	}
	public void setReceipt_date(Long receipt_date) {
		this.receipt_date = receipt_date;
	}
	public String getPaid_by() {
		return paid_by;
	}
	public void setPaid_by(String paid_by) {
		this.paid_by = paid_by;
	}
	public String getPayer_address() {
		return payer_address;
	}
	public void setPayer_address(String payer_address) {
		this.payer_address = payer_address;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getPayment_status() {
		return payment_status;
	}
	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_branch() {
		return bank_branch;
	}
	public void setBank_branch(String bank_branch) {
		this.bank_branch = bank_branch;
	}
	public String getSubdivison() {
		return subdivison;
	}
	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}
	public String getServicename() {
		return servicename;
	}
	public void setServicename(String servicename) {
		this.servicename = servicename;
	}
	public String getCollectedbyname() {
		return collectedbyname;
	}
	public void setCollectedbyname(String collectedbyname) {
		this.collectedbyname = collectedbyname;
	}
	public String getGstno() {
		return gstno;
	}
	public void setGstno(String gstno) {
		this.gstno = gstno;
	}
	public String getPayment_mode() {
		return payment_mode;
	}
	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}
	public BigDecimal getTotal_amt_paid() {
		return total_amt_paid;
	}
	public void setTotal_amt_paid(BigDecimal total_amt_paid) {
		this.total_amt_paid = total_amt_paid;
	}
	
	public String getChequeddno() {
		return chequeddno;
	}
	public void setChequeddno(String chequeddno) {
		this.chequeddno = chequeddno;
	}
	public Long getChequedddate() {
		return chequedddate;
	}
	public void setChequedddate(Long chequedddate) {
		this.chequedddate = chequedddate;
	}
	@Override
	public String toString() {
		return "MisReceiptsPOJO [payments_id=" + payments_id + ", total_amt_paid=" + total_amt_paid
				+ ", receipt_number=" + receipt_number + ", receipt_date=" + receipt_date + ", paid_by=" + paid_by
				+ ", payer_address=" + payer_address + ", narration=" + narration + ", payment_status=" + payment_status
				+ ", bank_name=" + bank_name + ", bank_branch=" + bank_branch + ", subdivison=" + subdivison
				+ ", servicename=" + servicename + ", collectedbyname=" + collectedbyname + ", gstno=" + gstno
				+ ", payment_mode=" + payment_mode + ", chequeddno=" + chequeddno + ", chequedddate=" + chequedddate
				+ "]";
	}
	  
	 
	
	
}
