package com.lucio.erp_new_app.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.lucio.erp_new_app.dtos.purchase.PurchaseOrderDTO;
import com.lucio.erp_new_app.dtos.supplier.QuotationItemDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierQuotationDTO;

import java.time.LocalDate;
import java.util.*;

public class FournisseurUtils {

    public static SupplierDTO mapToSupplierDTO(JsonNode node) {
        SupplierDTO dto = new SupplierDTO();
        dto.setName(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setSupplierGroup(node.path("supplier_group").asText());
        dto.setSupplierType(node.path("supplier_type").asText());
        dto.setCountry(node.path("country").asText());
        dto.setDefaultCurrency(node.path("default_currency").asText());
        dto.setStatus(determineSupplierStatus(node));
        return dto;
    }

    public static SupplierQuotationDTO mapToSupplierQuotationDTO(JsonNode node) {
        SupplierQuotationDTO dto = new SupplierQuotationDTO();
        dto.setId(node.path("name").asText());
        dto.setReference(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setCompany(node.path("company").asText());
        dto.setTransactionDate(LocalDate.parse(node.path("transaction_date").asText()));
        dto.setValidTill(LocalDate.parse(node.path("valid_till").asText()));
        dto.setStatus(node.path("status").asText());
        dto.setTotal(node.path("grand_total").asDouble());
        dto.setNetTotal(node.path("net_total").asDouble());
        dto.setCurrency(node.path("currency").asText());
        dto.setContactPerson(node.path("contact_display").asText(null));
        dto.setContactEmail(node.path("contact_email").asText(null));
        dto.setContactMobile(node.path("contact_mobile").asText(null));

        if (node.has("items")) {
            List<QuotationItemDTO> items = new ArrayList<>();
            for (JsonNode itemNode : node.path("items")) {
                QuotationItemDTO item = new QuotationItemDTO();
                item.setName(itemNode.path("name").asText());
                item.setItemCode(itemNode.path("item_code").asText());
                item.setItemName(itemNode.path("item_name").asText());
                item.setQuantity(itemNode.path("qty").asDouble());
                item.setUom(itemNode.path("uom").asText());
                item.setRate(itemNode.path("rate").asDouble());
                item.setAmount(itemNode.path("amount").asDouble());
                item.setWarehouse(itemNode.path("warehouse").asText());
                items.add(item);
            }
            dto.setQuotationItemDTOs(items);
        }

        return dto;
    }

    public static PurchaseOrderDTO mapToPurchaseOrderDTO(JsonNode node) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(node.path("name").asText());
        dto.setReference(node.path("name").asText());
        dto.setSupplierName(node.path("supplier_name").asText());
        dto.setTransactionDate(node.path("transaction_date").asText());
        dto.setScheduleDate(node.path("schedule_date").asText());
        dto.setStatus(node.path("status").asText());
        dto.setTotal(node.path("total").asDouble());
        dto.setNetTotal(node.path("net_total").asDouble());
        dto.setCurrency(node.path("currency").asText());
        dto.setPercentBilled(node.path("per_billed").asDouble());
        dto.setPercentReceived(node.path("per_received").asDouble());
        return dto;
    }

    public static String determineSupplierStatus(JsonNode node) {
        if (node.path("disabled").asInt() == 1) return "Désactivé";
        if (node.path("is_frozen").asInt() == 1) return "Gelé";
        if (node.path("on_hold").asInt() == 1) return "En attente";
        return "Actif";
    }

    public static void handleException(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}
