package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSiteController {

    @GetMapping("/website")
    public String getAdmin() {
        return "src/website/WebSite";
    }
}
