package com.lucio.erp_new_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.lucio.erp_new_app.dtos.LoginForm;
import com.lucio.erp_new_app.services.AuthService;

public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginForm loginForm, Model model) {
        boolean success = authService.loginToERPNext(loginForm.getUsername(), loginForm.getPassword());
        if (success) {
            return "redirect:/dashboard";
        }
        else {
            model.addAttribute("error", "Identifiants invalides");
            return "login";
        }
    }
}
