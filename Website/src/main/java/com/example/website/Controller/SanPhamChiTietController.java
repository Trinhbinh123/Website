package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SanPhamChiTietController {
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepo mauSacRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final SizeRepo sizeRepo;
    private final LoaiDeRepo loaiDeRepo;

    @GetMapping("/admin/san-pham-chi-tiet")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("spct",sanPhamChiTietRepo.findAll());
        return "src/san-pham-chi-tiet/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/san-pham-chi-tiet/add")
    public String getAddPage(Model model) {
        model.addAttribute("listSP", sanPhamRepo.findAll());
        model.addAttribute("listMS", mauSacRepo.findAll());
        model.addAttribute("listSize", sizeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());
        model.addAttribute("listLD", loaiDeRepo.findAll());
        return "src/san-pham-chi-tiet/AddSPCT"; // Template thêm sản phẩm
    }

    @PostMapping("/san-pham-chi-tiet/delete")
    public String delete(@RequestParam Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepo.getReferenceById(id);
        if (spct != null) {
            spct.setTrang_thai(false); // Đặt trạng thái thành Inactive
            sanPhamChiTietRepo.save(spct); // Lưu thay đổi
        }
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/san-pham-chi-tiet/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("spct", sanPhamChiTietRepo.getReferenceById(id));
        model.addAttribute("listSP", sanPhamRepo.findAll());
        model.addAttribute("listMS", mauSacRepo.findAll());
        model.addAttribute("listSize", sizeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());
        model.addAttribute("listLD", loaiDeRepo.findAll());
        return "src/san-pham-chi-tiet/UpdateSPCT"; // Template cập nhật sản phẩm
    }

    @PostMapping("/san-pham-chi-tiet/updateData")
    public String update(@ModelAttribute SanPhamChiTiet spct) {
        spct.setTrang_thai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamChiTietRepo.save(spct); // Lưu cập nhật
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/san-pham-chi-tiet/save")
    public String save(@ModelAttribute SanPhamChiTiet spct) {
        spct.setTrang_thai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamChiTietRepo.save(spct); // Lưu sản phẩm mới
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi lưu
    }
}
