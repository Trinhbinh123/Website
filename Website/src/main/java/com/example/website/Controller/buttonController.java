package com.example.website.Controller;

import com.example.website.Enity.KhuyenMai;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class buttonController {

    @GetMapping("/admin/button")
    public String getAdmin() {
        return "src/pages/ui-features/buttons.html";
    }
}
