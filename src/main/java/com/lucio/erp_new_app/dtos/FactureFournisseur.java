package com.lucio.erp_new_app.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class FactureFournisseur {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;

    private int docstatus;
    private int idx;

    private String title;
    private String namingSeries;
    private String supplier;
    private String supplierName;
    private String company;

    private LocalDate postingDate;
    private String postingTime;
    private LocalDate dueDate;

    private boolean isPaid;
    private boolean isReturn;

    private String currency;
    private double conversionRate;
    private double totalQty;
    private double total;

    private double netTotal;
    private double grandTotal;
    private double outstandingAmount;

    private String inWords;
    private boolean updateStock;
    private String setWarehouse;

    private double baseTotal;
    private double baseNetTotal;
    private double baseGrandTotal;
    private String baseInWords;

    private String status;
    private double perReceived;
    private String creditTo;
    private String partyAccountCurrency;
    private String againstExpenseAccount;

    private String remarks;
    private String supplierGroup;
    private String language;

    private List<FactureItem> items;

    @Data
    public static class FactureItem {
        private String itemCode;
        private String itemName;
        private double quantity;
        private double rate;
        private double amount;
        private String uom;
        private String warehouse;
    }

    private List<PaymentSchedule> paymentSchedule;

    @Data
    public static class PaymentSchedule {
        private LocalDate dueDate;
        private double paymentAmount;
        private double outstanding;
        private double paidAmount;
    }
}
