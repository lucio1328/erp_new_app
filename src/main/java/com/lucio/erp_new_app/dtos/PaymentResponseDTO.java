package com.lucio.erp_new_app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    @JsonProperty("name")
    private String name;
}

