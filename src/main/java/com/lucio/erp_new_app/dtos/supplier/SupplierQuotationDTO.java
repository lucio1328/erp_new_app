package com.lucio.erp_new_app.dtos.supplier;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class SupplierQuotationDTO {
    private String id;
    private String reference;
    private String supplierName;
    private String company;
    private LocalDate transactionDate;
    private LocalDate validTill;
    private String status;
    private double total;
    private double netTotal;
    private String currency;
    private String contactPerson;
    private String contactEmail;
    private String contactMobile;
    private List<QuotationItemDTO> quotationItemDTOs;

    public SupplierQuotationDTO(JsonNode node) {
        this.id = node.path("name").asText();
        this.reference = node.path("name").asText();
        this.supplierName = node.path("supplier_name").asText();
        this.company = node.path("company").asText();
        this.transactionDate = LocalDate.parse(node.path("transaction_date").asText());
        this.validTill = LocalDate.parse(node.path("valid_till").asText());
        this.status = node.path("status").asText();
        this.total = node.path("grand_total").asDouble();
        this.netTotal = node.path("net_total").asDouble();
        this.currency = node.path("currency").asText();
        this.contactPerson = node.path("contact_display").asText(null);
        this.contactEmail = node.path("contact_email").asText(null);
        this.contactMobile = node.path("contact_mobile").asText(null);
    }

    public SupplierQuotationDTO() {}

    public String getId() { return id; }
    public String getReference() { return reference; }
    public String getSupplierName() { return supplierName; }
    public String getCompany() { return company; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDate getValidTill() { return validTill; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public double getNetTotal() { return netTotal; }
    public String getCurrency() { return currency; }
    public String getContactPerson() { return contactPerson; }
    public String getContactEmail() { return contactEmail; }
    public String getContactMobile() { return contactMobile; }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(validTill);
    }

    public List<QuotationItemDTO> getQuotationItemDTOs() {
        return quotationItemDTOs;
    }

    public void setQuotationItemDTOs(List<QuotationItemDTO> quotationItemDTOs) {
        this.quotationItemDTOs = quotationItemDTOs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setValidTill(LocalDate validTill) {
        this.validTill = validTill;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }
}
