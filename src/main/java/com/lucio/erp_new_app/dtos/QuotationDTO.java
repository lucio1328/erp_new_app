package com.lucio.erp_new_app.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

public class QuotationDTO {
    private String id;
    private String reference;
    private String customerName;
    private LocalDate transactionDate;
    private LocalDate validTill;
    private String status;
    private double total;
    private double netTotal;
    private String currency;
    private String contactPerson;
    private String contactEmail;
    private String contactMobile;
    private String customerGroup;
    private String territory;

    public QuotationDTO(JsonNode node) {
        this.id = node.path("name").asText();
        this.reference = node.path("name").asText();
        this.customerName = node.path("customer_name").asText();
        this.transactionDate = LocalDate.parse(node.path("transaction_date").asText());
        this.validTill = LocalDate.parse(node.path("valid_till").asText());
        this.status = node.path("status").asText();
        this.total = node.path("total").asDouble();
        this.netTotal = node.path("net_total").asDouble();
        this.currency = node.path("currency").asText();
        this.contactPerson = node.path("contact_display").asText();
        this.contactEmail = node.path("contact_email").asText();
        this.contactMobile = node.path("contact_mobile").asText();
        this.customerGroup = node.path("customer_group").asText();
        this.territory = node.path("territory").asText();
    }

    public String getId() { return id; }
    public String getReference() { return reference; }
    public String getCustomerName() { return customerName; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDate getValidTill() { return validTill; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public double getNetTotal() { return netTotal; }
    public String getCurrency() { return currency; }
    public String getContactPerson() { return contactPerson; }
    public String getContactEmail() { return contactEmail; }
    public String getContactMobile() { return contactMobile; }
    public String getCustomerGroup() { return customerGroup; }
    public String getTerritory() { return territory; }


    public boolean isCancelled() {
        return "Cancelled".equals(status);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(validTill);
    }
}