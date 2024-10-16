package com.example.website.Controller;

import com.example.website.Enity.SanPham;
import com.example.website.Respository.SanPhamRepo;
import com.example.website.Enity.ThuongHieu;
import com.example.website.Respository.ThuongHieuRepo; // Giả sử bạn có repo cho Thương Hiệu
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamRepo sanPhamRepo;
    private final ThuongHieuRepo thuongHieuRepo; // Thêm repo cho Thương Hiệu

    @GetMapping("/admin/san-pham")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<SanPham> sanPhamPage = sanPhamRepo.findAll(pageable);

        model.addAttribute("sp", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        return "src/san-pham/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/san-pham/add")
    public String getAddPage(Model model) {
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        return "src/san-pham/AddSanPham"; // Template thêm sản phẩm
    }

    @PostMapping("/san-pham/delete")
    public String delete(@RequestParam Integer id) {
        SanPham sanPham = sanPhamRepo.getReferenceById(id);
        if (sanPham != null) {
            sanPham.setTrangthai(false); // Đặt trạng thái thành Inactive
            sanPhamRepo.save(sanPham); // Lưu thay đổi
        }
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/san-pham/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("SP", sanPhamRepo.getReferenceById(id));
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        return "src/san-pham/UpdateSanPham"; // Template cập nhật sản phẩm
    }

    @PostMapping("/san-pham/updateData")
    public String update(@ModelAttribute SanPham sanPham) {
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamRepo.save(sanPham); // Lưu cập nhật
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/san-pham/save")
    public String save(@ModelAttribute SanPham sanPham) {
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamRepo.save(sanPham); // Lưu sản phẩm mới
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi lưu
    }
}
