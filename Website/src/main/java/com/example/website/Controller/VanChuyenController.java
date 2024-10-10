package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VanChuyenController {

    @GetMapping("/admin/vanchuyen")
    public String getAdmin() {
        return "src/vanchuyen/VanChuyen";
    }
}
