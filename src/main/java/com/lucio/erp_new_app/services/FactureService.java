package com.lucio.erp_new_app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app.config.ErpnextProperties;
import com.lucio.erp_new_app.dtos.FactureClient;
import com.lucio.erp_new_app.dtos.FactureFournisseur;

@Service
public class FactureService {
    @Autowired
    private FournisseurService fournisseurService;

    private final ErpnextProperties erpnextProperties;
    private final ObjectMapper objectMapper;

    private static final String[] PURCHASE_INVOICE_FIELDS = {
        "name", "owner", "creation", "modified", "modified_by",
        "docstatus", "idx", "title", "naming_series", "supplier",
        "supplier_name", "company", "posting_date", "posting_time",
        "due_date", "is_paid", "is_return", "currency", "conversion_rate",
        "total_qty", "total", "net_total", "grand_total", "outstanding_amount",
        "in_words", "update_stock", "set_warehouse", "base_total",
        "base_net_total", "base_grand_total", "base_in_words", "status",
        "per_received", "credit_to", "party_account_currency",
        "against_expense_account", "remarks", "supplier_group", "language"
    };

    private static final String[] SALES_INVOICE_FIELDS = {
        "name", "owner", "creation", "modified", "modified_by",
        "docstatus", "idx", "naming_series", "customer", "customer_name",
        "company", "posting_date", "posting_time", "due_date", /*"is_paid",*/
        "is_return", "currency", "conversion_rate", "total_qty", "total",
        "net_total", "grand_total", "outstanding_amount", "in_words",
        "base_total", "base_net_total", "base_grand_total", "status",
        "debit_to", "party_account_currency", "against_income_account",
        "remarks", "language", "contact_person", "contact_email",
        "contact_mobile", "tax_id", "company_tax_id", "territory",
        "customer_group", "is_pos", "pos_profile"
    };

    public FactureService(ErpnextProperties erpnextProperties, ObjectMapper objectMapper) {
        this.erpnextProperties = erpnextProperties;
        this.objectMapper = objectMapper;
    }

    public List<FactureFournisseur> getAllFactureFournisseur(String sessionCookie) {
        String url = fournisseurService.buildUrl("Purchase Invoice", PURCHASE_INVOICE_FIELDS);
        return fournisseurService.fetchData(url, sessionCookie, this::mapToFactureFournisseur);
    }

    public FactureFournisseur getFactureByName(String name, String sessionCookie) throws Exception {
        String url = String.format("%s/api/resource/Purchase Invoice/%s",
                                erpnextProperties.getUrl(),
                                name);
        try {
            ResponseEntity<String> response = fournisseurService.executeRequest(url, sessionCookie);
            JsonNode node = objectMapper.readTree(response.getBody()).path("data");

            if (node.isMissingNode()) {
                throw new Exception("Facture non trouvée avec le nom: " + name);
            }

            return mapToFactureFournisseur(node);
        }
        catch (Exception e) {
            throw e;
        }
    }

    private FactureFournisseur mapToFactureFournisseur(JsonNode node) {
        FactureFournisseur facture = new FactureFournisseur();

        facture.setName(node.path("name").asText());
        facture.setOwner(node.path("owner").asText());
        facture.setCreation(parseDateTime(node.path("creation").asText()));
        facture.setModified(parseDateTime(node.path("modified").asText()));
        facture.setModifiedBy(node.path("modified_by").asText());

        facture.setDocstatus(node.path("docstatus").asInt());
        facture.setIdx(node.path("idx").asInt());

        facture.setTitle(node.path("title").asText());
        facture.setNamingSeries(node.path("naming_series").asText());
        facture.setSupplier(node.path("supplier").asText());
        facture.setSupplierName(node.path("supplier_name").asText());
        facture.setCompany(node.path("company").asText());

        facture.setPostingDate(parseDate(node.path("posting_date").asText()));
        facture.setPostingTime(node.path("posting_time").asText());
        if (!node.path("due_date").isMissingNode()) {
            facture.setDueDate(parseDate(node.path("due_date").asText()));
        }

        facture.setPaid(node.path("is_paid").asBoolean());
        facture.setReturn(node.path("is_return").asBoolean());

        facture.setCurrency(node.path("currency").asText());
        facture.setConversionRate(node.path("conversion_rate").asDouble());
        facture.setTotalQty(node.path("total_qty").asDouble());
        facture.setTotal(node.path("total").asDouble());
        facture.setNetTotal(node.path("net_total").asDouble());
        facture.setGrandTotal(node.path("grand_total").asDouble());
        facture.setOutstandingAmount(node.path("outstanding_amount").asDouble());

        facture.setInWords(node.path("in_words").asText());
        facture.setUpdateStock(node.path("update_stock").asBoolean());
        facture.setSetWarehouse(node.path("set_warehouse").asText());

        facture.setBaseTotal(node.path("base_total").asDouble());
        facture.setBaseNetTotal(node.path("base_net_total").asDouble());
        facture.setBaseGrandTotal(node.path("base_grand_total").asDouble());
        facture.setBaseInWords(node.path("base_in_words").asText());

        facture.setStatus(node.path("status").asText());
        facture.setPerReceived(node.path("per_received").asDouble());
        facture.setCreditTo(node.path("credit_to").asText());
        facture.setPartyAccountCurrency(node.path("party_account_currency").asText());
        facture.setAgainstExpenseAccount(node.path("against_expense_account").asText());

        facture.setRemarks(node.path("remarks").asText());
        facture.setSupplierGroup(node.path("supplier_group").asText());
        facture.setLanguage(node.path("language").asText());

        if (node.has("items")) {
        List<FactureFournisseur.FactureItem> items = new ArrayList<>();
        for (JsonNode itemNode : node.path("items")) {
            FactureFournisseur.FactureItem item = new FactureFournisseur.FactureItem();
            item.setItemCode(itemNode.path("item_code").asText());
            item.setItemName(itemNode.path("item_name").asText());
            item.setQuantity(itemNode.path("qty").asDouble());
            item.setRate(itemNode.path("rate").asDouble());
            item.setAmount(itemNode.path("amount").asDouble());
            item.setUom(itemNode.path("uom").asText());
            item.setWarehouse(itemNode.path("warehouse").asText());
            items.add(item);
        }
        facture.setItems(items);
    }

        return facture;
    }

    public List<FactureClient> getAllFactureClient(String sessionCookie) {
        String url = fournisseurService.buildUrl("Sales Invoice", SALES_INVOICE_FIELDS);
        return fournisseurService.fetchData(url, sessionCookie, this::mapToFactureClient);
    }

    private FactureClient mapToFactureClient(JsonNode node) {
        FactureClient facture = new FactureClient();

        facture.setName(node.path("name").asText());
        facture.setOwner(node.path("owner").asText());
        facture.setCreation(parseDateTime(node.path("creation").asText()));
        facture.setModified(parseDateTime(node.path("modified").asText()));
        facture.setModified_by(node.path("modified_by").asText());

        facture.setDocstatus(node.path("docstatus").asInt());
        facture.setIdx(node.path("idx").asInt());

        facture.setNaming_series(node.path("naming_series").asText());
        facture.setCustomer(node.path("customer").asText());
        facture.setCustomer_name(node.path("customer_name").asText());
        facture.setTax_id(node.path("tax_id").asText());
        facture.setCompany(node.path("company").asText());
        facture.setCompany_tax_id(node.path("company_tax_id").asText());

        facture.setPosting_date(parseDate(node.path("posting_date").asText()));
        facture.setPosting_time(node.path("posting_time").asText());
        if (!node.path("due_date").isMissingNode()) {
            facture.setDue_date(parseDate(node.path("due_date").asText()));
        }

        // facture.setIs_paid(node.path("is_paid").asInt());
        facture.setIs_return(node.path("is_return").asInt());
        facture.setIs_pos(node.path("is_pos").asInt());
        facture.setPos_profile(node.path("pos_profile").asText());

        facture.setCurrency(node.path("currency").asText());
        facture.setConversion_rate(node.path("conversion_rate").asDouble());
        facture.setTotal_qty(node.path("total_qty").asDouble());
        facture.setTotal(node.path("total").asDouble());
        facture.setNet_total(node.path("net_total").asDouble());
        facture.setGrand_total(node.path("grand_total").asDouble());
        facture.setOutstanding_amount(node.path("outstanding_amount").asDouble());
        facture.setIn_words(node.path("in_words").asText());

        facture.setBase_total(node.path("base_total").asDouble());
        facture.setBase_net_total(node.path("base_net_total").asDouble());
        facture.setBase_grand_total(node.path("base_grand_total").asDouble());

        facture.setStatus(node.path("status").asText());
        facture.setDebit_to(node.path("debit_to").asText());
        facture.setParty_account_currency(node.path("party_account_currency").asText());
        facture.setAgainst_income_account(node.path("against_income_account").asText());

        facture.setContact_person(node.path("contact_person").asText());
        facture.setContact_email(node.path("contact_email").asText());
        facture.setContact_mobile(node.path("contact_mobile").asText());

        facture.setTerritory(node.path("territory").asText());
        facture.setCustomer_group(node.path("customer_group").asText());
        facture.setRemarks(node.path("remarks").asText());
        facture.setLanguage(node.path("language").asText());

        return facture;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr.replace(" ", "T")) : null;
    }

    private LocalDate parseDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr) : null;
    }
}
