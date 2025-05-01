package com.lucio.erp_new_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/fournisseur")
public class FournisseurController {
    @GetMapping("/accueil")
    public ModelAndView accueil() {
        return new ModelAndView("pages/accueil");
    }
}
