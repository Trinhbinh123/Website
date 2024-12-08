package com.example.website.Controller.KhachHang;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.KhachHang;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.KhachHangRepo;
import com.example.website.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/khachHang")
public class DonHangControllerApi {
    private final HoaDonRepo hoaDonRepo;
    private final KhachHangRepo khachHangRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final UserService userService;

    @GetMapping("/checkBillByStatus")
    public List<HoaDon> checkHDByStatus(@RequestParam String status, Authentication authentication){
        KhachHang khachHang = userService.currentKhachHang(authentication);
        List<HoaDon> hoaDons = hoaDonRepo.findByKhachHang(khachHang);
        hoaDons = hoaDons.stream().filter(hoaDon -> hoaDon.getTrangThai().equals(status)).toList();
        return hoaDons;
    }
    @GetMapping("/getQuantityStatusOfBill")
    public List<Integer> soLuongTrangThaiHoaDon(Authentication authentication){
        KhachHang khachHang = userService.currentKhachHang(authentication);
        List<HoaDon> hoaDons = hoaDonRepo.findByKhachHang(khachHang);
        int choXacNhan = 0;
        int daXacNhan = 0;
        int dangGiaoHang = 0;
        int daGiaoHang = 0;
        int donBiHuy = 0;

        for(HoaDon hoaDon : hoaDons){
            if(hoaDon.getTrangThai().equals("Chờ xác nhận")){
                choXacNhan++;
            }else if(hoaDon.getTrangThai().equals("Xác nhận")){
                daXacNhan++;
            }else if(hoaDon.getTrangThai().equals("Ðang giao")){
                dangGiaoHang++;
            }else if(hoaDon.getTrangThai().equals("Ðã giao")){
                daGiaoHang++;
            }else if(hoaDon.getTrangThai().equals("Đơn bị hủy")){
                donBiHuy++;
            }
        }
        List<Integer> integers = new ArrayList<>();
        integers.add(choXacNhan);
        integers.add(daXacNhan);
        integers.add(dangGiaoHang);
        integers.add(daGiaoHang);
        integers.add(donBiHuy);
        return integers;
    }

    @GetMapping("/getBillDetailByBill/{idBill}")
    public List<HoaDonChiTiet> getHDCT(@PathVariable Integer idBill){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        return hoaDonChiTietRepo.findByHoaDon(hoaDon);
    }

    @GetMapping("/getImg/{idBill}")
    public List<String> getImg(@PathVariable Integer idBill){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        List<String> strings = new ArrayList<>();
        for(HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets){
            strings.add(String.valueOf(hoaDonChiTiet.getSanPhamChiTiet().getAnh_spct()));
        }
        return strings;
    }

    @PutMapping("/huydon/{idBill}")
    public void huyDonHang(@PathVariable Integer idBill, @RequestParam String message, Authentication authentication){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        hoaDon.setGhiChu(message + " - Huỷ bởi khách hàng");
        hoaDon.setTrangThai("Đơn bị hủy");
        hoaDonRepo.save(hoaDon);
    }
}
