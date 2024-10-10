package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HoaDonController {

    @GetMapping("/admin/hoadon")
    public String getAdmin() {
        return "src/hoadon/HoaDon";
    }
}
