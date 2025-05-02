package com.lucio.erp_new_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app.dtos.FactureClient;
import com.lucio.erp_new_app.dtos.FactureFournisseur;
import com.lucio.erp_new_app.services.FactureService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/factures")
public class FactureController {
    @Autowired
    private HttpSession session;

    @Autowired
    private FactureService factureService;

    @Autowired
    private FournisseurController fournisseurController;

    @GetMapping("/fournisseur")
    public ModelAndView listeFactureFournisseur() {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        fournisseurController.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");

        List<FactureFournisseur> factureFournisseurs = factureService.getAllFactureFournisseur(sessionCookie);
        modelAndView.addObject("factureFournisseurs", factureFournisseurs);

        FournisseurController.changerInformation(modelAndView, "pages/facture/facture_fournisseur", "Liste des factures fournisseurs");

        return modelAndView;
    }

    @GetMapping("/client")
    public ModelAndView listeFactureClient() {
        ModelAndView modelAndView = new ModelAndView("pages/layout/modele");
        fournisseurController.afficherName(modelAndView);

        String sessionCookie = (String) session.getAttribute("sid");

        List<FactureClient> factureClients = factureService.getAllFactureClient(sessionCookie);
        System.out.println(factureClients.size());
        modelAndView.addObject("factureClients", factureClients);

        FournisseurController.changerInformation(modelAndView, "pages/facture/facture_client", "Liste des factures clients");

        return modelAndView;
    }
}
