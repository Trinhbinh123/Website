package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThongKeController {

    @GetMapping("/admin/thongke")
    public String getAdmin() {
        return "src/thongke/ThongKe";
    }
}
