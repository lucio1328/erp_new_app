package com.lucio.erp_new_app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentReferenceDTO {
    @JsonProperty("reference_doctype")
    private String referenceDoctype = "Purchase Invoice";

    @JsonProperty("reference_name")
    private String referenceName;

    @JsonProperty("allocated_amount")
    private Double allocatedAmount;

    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("total_amount")
    private Double totalAmount;
}
