package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.GioHangRepo;
import com.example.website.Respository.KhachHangRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import com.example.website.Respository.SanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebSiteController {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;
    private final GioHangRepo gioHangRepo;

    @GetMapping("/website")
    public String getAdmin(Model model) {
        model.addAttribute("sanPhams", sanPhamRepo.findAll());
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        Integer soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        model.addAttribute("soLuongGioHang", soLuong);
        return "src/website/WebSite";
    }
<<<<<<< HEAD

    @GetMapping("/detail/{idSanPham}")
    public String detail(Model model, @PathVariable Integer idSanPham) {
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        model.addAttribute("khachHang", khachHang);
        SanPham sanPham = sanPhamRepo.getReferenceById(idSanPham);
        model.addAttribute("sanPham", sanPham);
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepo.findBySanPham(sanPham);
        List<MauSac> mauSacs = new ArrayList<>();
        List<Size> sizes = new ArrayList<>();
        for(SanPhamChiTiet sanPhamChiTiet : sanPhamChiTiets){
            mauSacs.add(sanPhamChiTiet.getMauSac());
            sizes.add(sanPhamChiTiet.getSize());
        }
        Set<MauSac> listMauSac = new HashSet<>(mauSacs);
        Set<Size> listSize = new HashSet<>(sizes);
        model.addAttribute("mauSacs", new ArrayList<>(listMauSac));
        model.addAttribute("sizes", new ArrayList<>(listSize));
=======
    @GetMapping("/detail")
    public String detail() {
>>>>>>> f72893b768e8c13f4cc08ebd2ee798da4cfcdf57
        return "src/website/detail"; // Trả về trang home.html
    }
    @GetMapping("/shop")
    public String shop() {
        return "src/website/shop"; // Trả về trang about.html
    }
    @GetMapping("/checkout")
    public String checkout() {
        return "src/website/checkout"; // Trả về trang about.html
    }
    @GetMapping("/cart")
    public String cart(Model model) {
        return "src/website/cart"; // Trả về trang about.html
    }
    @GetMapping("/contact")
    public String contact() {
        return "src/website/contact"; // Trả về trang about.html
    }
}
