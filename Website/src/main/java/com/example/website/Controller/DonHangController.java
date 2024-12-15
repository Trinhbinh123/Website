package com.example.website.Controller;


import com.example.website.Enity.DonHang;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Enity.HoaDon;
import com.example.website.Respository.SanPhamChiTietRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
@Controller
@RequiredArgsConstructor
public class DonHangController {
    private final HoaDonRepo hoaDonRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("/admin/donhang")
    public String getAdmin(@RequestParam(defaultValue = "") String trangThai, Model model) {
        List<HoaDon> danhSachHoaDon = trangThai.isEmpty()
                ? hoaDonRepo.findAllOrderByNgayDatHangDesc()
                : hoaDonRepo.findByTrangThaiOrderByNgayDatHangDesc(trangThai);

        model.addAttribute("danhSachHoaDon", danhSachHoaDon);
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("soDonChuaXacNhan", hoaDonRepo.countByTrangThai("Chờ xác nhận"));
        model.addAttribute("soDonXacNhan", hoaDonRepo.countByTrangThai("Xác nhận"));
        model.addAttribute("soDonDangGiao", hoaDonRepo.countByTrangThai("Đang giao"));
        model.addAttribute("soDonDaGiao", hoaDonRepo.countByTrangThai("Đã giao"));
        model.addAttribute("soDonDoiTra", hoaDonRepo.countByTrangThai("Đổi trả"));
        model.addAttribute("soDonBiHuy", hoaDonRepo.countByTrangThai("Đơn bị hủy"));

        return "donhang/DonHang";
    }

    @GetMapping("/donhang/detail")
    public String getDonHangDetail(@RequestParam Integer id, Model model) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isEmpty()) {
            return "redirect:/admin/donhang?error=not_found";
        }
        HoaDon hoaDon = optionalHoaDon.get();
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        model.addAttribute("dh", hoaDon);
        model.addAttribute("chiTietList", chiTietList);
        return "donhang/DonHangDetail";
    }

    @PostMapping("/donhang/updateData")
    public String updateTrangThaiDonHang(@RequestParam Integer id, @RequestParam String trangThai) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isEmpty()) {
            return "redirect:/admin/donhang?error=not_found";
        }
        HoaDon hoaDon = optionalHoaDon.get();
        List<String> validTrangThai = List.of("Chờ xác nhận", "Xác nhận", "Đang giao", "Đã giao", "Đổi trả", "Đơn bị hủy");

        if (!validTrangThai.contains(trangThai)) {
            return "redirect:/admin/donhang?error=invalid_status";
        }

        switch (hoaDon.getTrangThai()) {
            case "Chờ xác nhận" -> {
                if ("Xác nhận".equals(trangThai) || "Đơn bị hủy".equals(trangThai)) {
                    hoaDon.setTrangThai(trangThai);
                }
            }
            case "Xác nhận" -> {
                if ("Đang giao".equals(trangThai)) {
                    boolean check = hoaDonChiTietRepo.findByHoaDon(hoaDon).stream()
                            .anyMatch(hoaDonChiTiet -> hoaDonChiTiet.getSanPhamChiTiet().getSo_luong() < hoaDonChiTiet.getSoLuong());
                    if (check) {
                        return "redirect:/admin/donhang?error=quantity_insufficient";
                    }
                    hoaDonChiTietRepo.findByHoaDon(hoaDon).forEach(hoaDonChiTiet -> {
                        SanPhamChiTiet spChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
                        spChiTiet.setSo_luong(spChiTiet.getSo_luong() - hoaDonChiTiet.getSoLuong());
                        sanPhamChiTietRepo.save(spChiTiet);
                    });
                }
                hoaDon.setTrangThai(trangThai);
            }
            case "Đang giao" -> {
                if ("Đã giao".equals(trangThai)) {
                    hoaDon.setTrangThai(trangThai);
                }
            }
            case "Đã giao" -> {
                if ("Đổi trả".equals(trangThai)) {
                    hoaDon.setTrangThai(trangThai);
                }
            }
            case "Đơn bị hủy" -> {
                if ("Chờ xác nhận".equals(trangThai)) {
                    hoaDon.setTrangThai(trangThai);
                }
            }
        }
        hoaDonRepo.save(hoaDon);
        return "redirect:/admin/donhang";
    }

    @PostMapping("/donhang/cancelOrder")
    public String cancelOrder(@RequestParam Integer id) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (optionalHoaDon.isEmpty()) {
            return "redirect:/admin/donhang?error=not_found";
        }
        HoaDon hoaDon = optionalHoaDon.get();
        if ("Chờ xác nhận".equals(hoaDon.getTrangThai()) || "Xác nhận".equals(hoaDon.getTrangThai())) {
            hoaDon.setTrangThai("Đơn bị hủy");
            hoaDonRepo.save(hoaDon);
        }
        return "redirect:/admin/donhang";
    }
}
