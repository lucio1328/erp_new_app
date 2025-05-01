package com.lucio.erp_new_app.dtos;

public class SupplierDTO {

    private String name;
    private String supplierName;
    private String supplierGroup;
    private String supplierType;
    private String country;
    private String defaultCurrency;

    public SupplierDTO() {
    }
    public SupplierDTO(String name, String supplierName, String supplierGroup, String supplierType, String country, String defaultCurrency) {
        this.name = name;
        this.supplierName = supplierName;
        this.supplierGroup = supplierGroup;
        this.supplierType = supplierType;
        this.country = country;
        this.defaultCurrency = defaultCurrency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierGroup() {
        return supplierGroup;
    }

    public void setSupplierGroup(String supplierGroup) {
        this.supplierGroup = supplierGroup;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
