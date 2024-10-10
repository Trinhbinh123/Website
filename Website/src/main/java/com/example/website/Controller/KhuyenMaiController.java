package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KhuyenMaiController {

    @GetMapping("/admin/khuyenmai")
    public String getAdmin() {
        return "src/khuyenmai/KhuyenMai";
    }
}
