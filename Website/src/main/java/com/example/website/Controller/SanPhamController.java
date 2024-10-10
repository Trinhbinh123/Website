package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SanPhamController {

    @GetMapping("/admin/sanpham")
    public String getAdmin() {
        return "src/sanpham/SanPham";
    }
}
