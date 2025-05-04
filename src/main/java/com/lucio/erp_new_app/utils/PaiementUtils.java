package com.lucio.erp_new_app.utils;

import com.lucio.erp_new_app.config.ErpnextProperties;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

public class PaiementUtils {

    public static String getSessionId() {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            throw new RuntimeException("Session non authentifiée");
        }
        return sid;
    }

    public static HttpHeaders buildHeaders(String sid, boolean isJsonContent, ErpnextProperties props) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (isJsonContent) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        headers.set("Authorization", "token " + props.getKey() + ":" + props.getSecret());
        headers.set("Cookie", "sid=" + sid);
        return headers;
    }

    public static <T> void checkResponse(ResponseEntity<T> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erreur ERPNext - Code HTTP: " + response.getStatusCode() + " - Message: " + response.getBody());
        }
    }

    public static RuntimeException handleException(String contexte, Exception e) {
        if (e instanceof HttpClientErrorException httpEx) {
            return new RuntimeException("Erreur côté client (4xx) lors du " + contexte + ": " + httpEx.getStatusCode() + " - " + httpEx.getMessage(), httpEx);
        } else if (e instanceof HttpServerErrorException httpEx) {
            return new RuntimeException("Erreur côté serveur ERPNext (5xx) lors du " + contexte + ": " + httpEx.getStatusCode() + " - " + httpEx.getMessage(), httpEx);
        } else if (e instanceof ResourceAccessException) {
            return new RuntimeException("Erreur d’accès au serveur ERPNext lors du " + contexte + ": " + e.getMessage(), e);
        } else if (e instanceof RestClientException) {
            return new RuntimeException("Erreur lors de l’appel ERPNext pour " + contexte + ": " + e.getMessage(), e);
        }
        return new RuntimeException("Erreur inattendue lors du " + contexte + ": " + e.getMessage(), e);
    }
}
