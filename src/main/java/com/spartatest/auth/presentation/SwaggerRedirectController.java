package com.spartatest.auth.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/doc");
    }
}
