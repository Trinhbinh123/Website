package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSiteController {

    @GetMapping("/website")
    public String getAdmin() {
        return "src/website/WebSite";
    }
    @GetMapping("/detail")
    public String detail() {
        return "src/website/detail"; // Trả về trang home.html
    }
    @GetMapping("/shop")
    public String shop() {
        return "src/website/shop"; // Trả về trang about.html
    }
    @GetMapping("/checkout")
    public String checkout() {
        return "src/website/checkout"; // Trả về trang about.html
    }
    @GetMapping("/cart")
    public String cart() {
        return "src/website/cart"; // Trả về trang about.html
    }
    @GetMapping("/contact")
    public String contact() {
        return "src/website/contact"; // Trả về trang about.html
    }
}
