package com.lucio.erp_new_app.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
