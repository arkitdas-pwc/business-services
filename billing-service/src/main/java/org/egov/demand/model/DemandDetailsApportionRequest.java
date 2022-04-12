package org.egov.demand.model;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandDetailsApportionRequest {

        @JsonProperty("RequestInfo")
        private RequestInfo requestInfo = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("Demands")
        @Valid
        private List<Demand> demands = null;

        @JsonProperty("amountToBeAdjusted")
        private String amountToBeAdjusted = null;
}
