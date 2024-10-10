package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KhachHangController {

    @GetMapping("/admin/khachhang")
    public String getAdmin() {
        return "src/khachhang/KhachHang";
    }
}
