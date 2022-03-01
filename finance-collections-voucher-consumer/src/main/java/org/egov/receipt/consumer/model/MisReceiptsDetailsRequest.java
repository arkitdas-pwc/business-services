package org.egov.receipt.consumer.model;

import java.util.Set;

import javax.validation.constraints.NotNull;



import com.fasterxml.jackson.annotation.JsonProperty;

public class MisReceiptsDetailsRequest {
	
	@JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
	
	@JsonProperty("misReceiptsPOJO")
	private MisReceiptsPOJO misReceiptsPOJO;

	public MisReceiptsPOJO getMisReceiptsPOJO() {
		return misReceiptsPOJO;
	}

	public void setMisReceiptsPOJO(MisReceiptsPOJO misReceiptsPOJO) {
		this.misReceiptsPOJO = misReceiptsPOJO;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	

	
	
}
