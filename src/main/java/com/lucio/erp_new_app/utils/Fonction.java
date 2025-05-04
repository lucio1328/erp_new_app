package com.lucio.erp_new_app.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.lucio.erp_new_app.dtos.PurchaseOrderDTO;

public class Fonction {
    // getStatuts commandes
    public static List<String> getStatuts(List<PurchaseOrderDTO> all) {
        return all.stream()
                .map(PurchaseOrderDTO::getStatus)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    // recherche multi-criteres commandes
    public static List<PurchaseOrderDTO> recherchePurchaseOrder(List<PurchaseOrderDTO> purchaseOrders,
                                                             String reference,
                                                             String status,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {

        return purchaseOrders.stream()
            .filter(po -> reference == null || matchesReference(po, reference))
            .filter(po -> status == null || matchesStatus(po, status))
            .filter(po -> startDate == null || isAfterOrEqual(po, startDate))
            .filter(po -> endDate == null || isBeforeOrEqual(po, endDate))
            .toList();
    }

    private static boolean isAfterOrEqual(PurchaseOrderDTO po, LocalDate date) {
        LocalDate transactionDate = safeParseTransactionDate(po);
        return transactionDate != null && !transactionDate.isBefore(date);
    }

    private static boolean isBeforeOrEqual(PurchaseOrderDTO po, LocalDate date) {
        LocalDate transactionDate = safeParseTransactionDate(po);
        return transactionDate != null && !transactionDate.isAfter(date);
    }

    private static LocalDate safeParseTransactionDate(PurchaseOrderDTO po) {
        try {
            String raw = po.getTransactionDate();
            if (raw == null) return null;
            String cleaned = raw.trim();

            if (cleaned.contains("T")) {
                return LocalDateTime.parse(cleaned, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            } else {
                return LocalDate.parse(cleaned, DateTimeFormatter.ISO_DATE);
            }
        } catch (Exception e) {
            System.out.println("Erreur parsing date: " + po.getTransactionDate());
            return null;
        }
    }

    private static boolean matchesReference(PurchaseOrderDTO po, String reference) {
        return po.getReference() != null &&
            po.getReference().toLowerCase().contains(reference.toLowerCase());
    }

    private static boolean matchesStatus(PurchaseOrderDTO po, String status) {
        return po.getStatus() != null &&
            po.getStatus().equalsIgnoreCase(status);
    }

}
