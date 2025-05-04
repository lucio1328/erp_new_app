package com.lucio.erp_new_app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PaymentDTO {
    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("posting_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate postingDate;

    @JsonProperty("company")
    private String company;

    @JsonProperty("received_amount")
    private Double receivedAmount;

    @JsonProperty("source_exchange_rate")
    private Double sourceExchangeRate = 1.0;

    @JsonProperty("target_exchange_rate")
    private Double targetExchangeRate = 1.0;

    @JsonProperty("paid_amount")
    private Double paidAmount;

    @JsonProperty("reference_no")
    private String referenceNo;

    @JsonProperty("reference_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate referenceDate;

    @JsonProperty("invoice_type")
    private String invoiceType = "Purchase Invoice";

    @JsonProperty("invoice_name")
    private String invoiceName;

    @JsonProperty("allocated_amount")
    private Double allocatedAmount;

    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;

    @JsonProperty("party_type")
    private String partyType = "Supplier";

    @JsonProperty("party")
    private String party;

    @JsonProperty("mode_of_payment")
    private String modeOfPayment;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("paid_from")
    private String paidFrom;

    @JsonProperty("paid_from_account_currency")
    private String paidFromAccountCurrency;

    @JsonProperty("paid_to")
    private String paidTo;

    @JsonProperty("paid_to_account_currency")
    private String paidToAccountCurrency = "EURO";

    @JsonProperty("is_partial_payment")
    private Boolean isPartialPayment = false;

    @JsonProperty("difference_amount")
    private Double differenceAmount;

    @JsonProperty("docstatus")
    private Integer docstatus = 0;

    @JsonProperty("ignore_accounting_period")
    private Boolean ignoreAccountingPeriod = true;

    @JsonProperty("accounting_period")
    private String accountingPeriod;

    @JsonProperty("references")
    private List<PaymentReferenceDTO> references;

    // Méthodes utilitaires
    public boolean isFullPayment() {
        return allocatedAmount != null
               && outstandingAmount != null
               && allocatedAmount.compareTo(outstandingAmount) == 0;
    }

    public void prepareForPartialPayment() {
        if (allocatedAmount == null || outstandingAmount == null) {
            throw new IllegalStateException("allocatedAmount and outstandingAmount must be set");
        }

        this.isPartialPayment = !isFullPayment();
        this.paidAmount = allocatedAmount;
        this.receivedAmount = allocatedAmount;
        this.differenceAmount = 0.0;

        if (this.references != null && !this.references.isEmpty()) {
            this.references.forEach(ref -> {
                ref.setAllocatedAmount(allocatedAmount);
                ref.setOutstandingAmount(outstandingAmount);
            });
        }
    }


    public static void afficherPaymentDTO(PaymentDTO payment) {
        System.out.println("=== PaymentDTO ===");
        System.out.println("Payment Type: " + payment.getPaymentType());
        System.out.println("Posting Date: " + payment.getPostingDate());
        System.out.println("Company: " + payment.getCompany());
        System.out.println("Party Type: " + payment.getPartyType());
        System.out.println("Party: " + payment.getParty());
        System.out.println("Mode of Payment: " + payment.getModeOfPayment());
        System.out.println("Currency: " + payment.getCurrency());
        System.out.println("Paid From: " + payment.getPaidFrom());
        System.out.println("Paid From Account Currency: " + payment.getPaidFromAccountCurrency());
        System.out.println("Paid To: " + payment.getPaidTo());
        System.out.println("Paid To Account Currency: " + payment.getPaidToAccountCurrency());
        System.out.println("Paid Amount: " + payment.getPaidAmount());
        System.out.println("Received Amount: " + payment.getReceivedAmount());
        System.out.println("Allocated Amount: " + payment.getAllocatedAmount());
        System.out.println("Outstanding Amount: " + payment.getOutstandingAmount());
        System.out.println("Difference Amount: " + payment.getDifferenceAmount());
        System.out.println("Is Partial Payment: " + payment.getIsPartialPayment());
        System.out.println("Invoice Type: " + payment.getInvoiceType());
        System.out.println("Invoice Name: " + payment.getInvoiceName());
        System.out.println("Docstatus: " + payment.getDocstatus());

        if (payment.getReferences() != null && !payment.getReferences().isEmpty()) {
            System.out.println("--- References ---");
            for (PaymentReferenceDTO ref : payment.getReferences()) {
                System.out.println("  Reference Doctype: " + ref.getReferenceDoctype());
                System.out.println("  Reference Name: " + ref.getReferenceName());
                System.out.println("  Allocated Amount: " + ref.getAllocatedAmount());
                System.out.println("  Outstanding Amount: " + ref.getOutstandingAmount());
                System.out.println("  Total Amount: " + ref.getTotalAmount());
                System.out.println("  Due Date: " + ref.getDueDate());
                System.out.println("------------------");
            }
        } else {
            System.out.println("No references.");
        }
    }

}