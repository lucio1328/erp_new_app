package com.lucio.erp_new_app.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app.dtos.purchase.PurchaseOrderDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierDTO;
import com.lucio.erp_new_app.dtos.supplier.SupplierQuotationDTO;
import com.lucio.erp_new_app.services.FournisseurService;
import com.lucio.erp_new_app.utils.Fonction;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {

    @Autowired
    private FournisseurService fournisseurService;

    @Autowired
    private HttpSession session;

    public static void changerInformation(ModelAndView modelAndView, String view, String title) {
        if (modelAndView != null) {
            modelAndView.addObject("view", view);
            modelAndView.addObject("title", title);
        }
        return;
    }

    public void afficherName(ModelAndView modelAndView) {
        String loggedUser = (String) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            loggedUser = "Utilisateur inconnu";
        }
        modelAndView.addObject("loggedUser", loggedUser);
    }

    @GetMapping("/liste")
    public ModelAndView liste() {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");

        String sessionCookie = (String) session.getAttribute("sid");
        List<SupplierDTO> fournisseurs = fournisseurService.getAllFournisseurs(sessionCookie);

        modelAndView.addObject("fournisseurs", fournisseurs);
        this.afficherName(modelAndView);
        FournisseurController.changerInformation(modelAndView, "pages/fournisseur/liste", "Liste Fournisseur");

        return modelAndView;
    }

    @GetMapping("/{name}/details")
    public ModelAndView details(@PathVariable("name") String name) {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        this.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");
        session.setAttribute("name", name);
        SupplierDTO fournisseur = fournisseurService.getFournisseurByName(name, sessionCookie);

        if (fournisseur == null) {
            modelAndView.addObject("error", "Fournisseur non trouvé");
            return modelAndView;
        }

        modelAndView.addObject("fournisseur", fournisseur);
        modelAndView.addObject("view", "pages/fournisseur/details");
        modelAndView.addObject("template", "pages/fournisseur/tabs/information");
        modelAndView.addObject("title", "Détails Fournisseur");
        modelAndView.addObject("section", "details");

        return modelAndView;
    }

    @GetMapping("/{name}/devis")
    public ModelAndView devis(@PathVariable("name") String name) {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        this.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");
        session.setAttribute("name", name);
        SupplierDTO fournisseur = fournisseurService.getFournisseurByName(name, sessionCookie);
        List<SupplierQuotationDTO> quotations = fournisseurService.getSupplierQuotations(name, sessionCookie);

        modelAndView.addObject("fournisseur", fournisseur);
        modelAndView.addObject("quotations", quotations);
        modelAndView.addObject("view", "pages/fournisseur/details");
        modelAndView.addObject("template", "pages/fournisseur/tabs/demandes_devis");
        modelAndView.addObject("title", "Devis Fournisseur");
        modelAndView.addObject("section", "devis");

        return modelAndView;
    }

    @GetMapping("/{name}/commandes")
    public ModelAndView commandes(@PathVariable("name") String name,
                                @RequestParam(required = false) String reference,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        this.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");
        session.setAttribute("name", name);
        SupplierDTO fournisseur = fournisseurService.getFournisseurByName(name, sessionCookie);
        List<PurchaseOrderDTO> purchaseOrders = fournisseurService.getSupplierPurchaseOrders(name, sessionCookie);

        List<PurchaseOrderDTO> filteredOrders = Fonction.recherchePurchaseOrder(purchaseOrders, reference, status, startDate, endDate);
        if ((reference == null || reference.isEmpty()) && (status == null || status.isEmpty()) && startDate == null && endDate == null) {
            filteredOrders = purchaseOrders;
        }

        if (status != null && !status.isEmpty()) {
            filteredOrders = fournisseurService.getPurchaseOrderByStatuts(purchaseOrders, status, name, sessionCookie);
        }

        modelAndView.addObject("fournisseur", fournisseur);
        modelAndView.addObject("commandes", filteredOrders);
        // modelAndView.addObject("statuts", Fonction.getStatuts(purchaseOrders));
        modelAndView.addObject("view", "pages/fournisseur/details");
        modelAndView.addObject("template", "pages/fournisseur/tabs/commandes");
        modelAndView.addObject("title", "Commandes Fournisseur");
        modelAndView.addObject("section", "commandes");

        return modelAndView;
    }

    @GetMapping("/quotation/edit/{id}")
    public ModelAndView modifier(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        this.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");
        SupplierQuotationDTO supplierQuotationDTOs = fournisseurService.getSupplierQuotationByName(id, sessionCookie);

        modelAndView.addObject("supplierQuotation", supplierQuotationDTOs);
        FournisseurController.changerInformation(modelAndView, "pages/fournisseur/tabs/modification_devis", "Modification prix devis");

        return modelAndView;
    }

}
