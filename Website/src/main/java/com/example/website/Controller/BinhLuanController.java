package com.example.website.Controller;

import com.example.website.Enity.BinhLuan;
import com.example.website.Respository.BinhLuanRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BinhLuanController {
    @Autowired
    private BinhLuanRepo binhLuanRepo;
    private BinhLuan binhLuan;

    @GetMapping("/admin/binhluan")
    public String HienThi(Model model) {
        model.addAttribute("binhluan", binhLuan);
        model.addAttribute("bl", binhLuanRepo.findAll());
        return "src/binhluan/BinhLuan";
    }
}
