package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonHangController {

    @GetMapping("/admin/donhang")
    public String getAdmin() {
        return "src/donhang/DonHang";
    }
}
