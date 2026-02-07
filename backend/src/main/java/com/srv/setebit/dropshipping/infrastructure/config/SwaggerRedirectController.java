package com.srv.setebit.dropshipping.infrastructure.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redireciona /swagger-ui e /swagger-ui/ para /swagger-ui/index.html (evita loop com springdoc).
 */
@Controller
public class SwaggerRedirectController {

    @GetMapping({"/swagger-ui", "/swagger-ui/"})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
