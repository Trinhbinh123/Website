package com.example.website.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TinNhanController {
    @GetMapping("/admin/tinnhan")
    public String getAdmin() {
        return "src/tinnhan/TinNhan";
    }
}
