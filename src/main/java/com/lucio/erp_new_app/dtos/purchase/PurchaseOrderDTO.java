package com.lucio.erp_new_app.dtos.purchase;

public class PurchaseOrderDTO {
    private String id;
    private String reference;
    private String supplierName;
    private String transactionDate;
    private String scheduleDate;
    private String status;
    private double total;
    private double netTotal;
    private String currency;
    private double percentBilled;
    private double percentReceived;

    public PurchaseOrderDTO() {}


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getNetTotal() { return netTotal; }
    public void setNetTotal(double netTotal) { this.netTotal = netTotal; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getPercentBilled() { return percentBilled; }
    public void setPercentBilled(double percentBilled) { this.percentBilled = percentBilled; }

    public double getPercentReceived() { return percentReceived; }
    public void setPercentReceived(double percentReceived) { this.percentReceived = percentReceived; }

    public boolean isCompleted() {
        return percentBilled >= 100 && percentReceived >= 100;
    }

    public boolean isPartiallyReceived() {
        return percentReceived > 0 && percentReceived < 100;
    }
}