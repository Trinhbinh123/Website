package com.example.website.Controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Response.SanPhamOfficeResponse;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.SanPhamRepo;
import com.example.website.Sdi.createHoaDonOfficeSdi;
import com.example.website.Sdi.hdct;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HoaDonController {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamRepo sanPhamRepo;

    // @GetMapping("/admin/hoadon")
    // public String getAdmin() {
    //     return "src/hoadon/HoaDon";
    // }

    @GetMapping("/admin/hoadon")
    public String getAdmin(Model model) {
        List<SanPhamOfficeResponse> sanPhamOffices = sanPhamRepo.findSanPhamOffice();
        List<HoaDon> danhSachHoaDon = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");

        // Truyền dữ liệu đếm vào model
        model.addAttribute("danhSachHoaDon", danhSachHoaDon);
        model.addAttribute("sanPhamOffices", sanPhamOffices);


        return "src/hoadon/HoaDon";
    }

    @GetMapping("/hoadon/fetch")
    public ResponseEntity<List<HoaDon>> fetchHoaDon() {
        List<HoaDon> danhSachHoaDon = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");
        return ResponseEntity.ok(danhSachHoaDon);
    }

    @GetMapping("/hoadon/detail")
    public String getDonHangDetail(@RequestParam int idDonHang, Model model) {
        HoaDon hoaDon = hoaDonRepo.findDetailSaleOffice(idDonHang);
        List<HoaDonChiTiet> chitiet = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        // List<HoaDon> dh = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");

        model.addAttribute("dh", hoaDon);
        model.addAttribute("chitiet", chitiet);
        return "src/hoadon/HoaDonDetail";
    }

    @PostMapping("/hoadon/add")
    public ResponseEntity<?> addHoaDon(@RequestBody createHoaDonOfficeSdi input) {
        try{
            String hinhthucthanhtoan = input.getHinhthucthanhtoan();
            int tongtien = input.getTongtien();
            List<hdct> hdcts = input.getHdct();
            HoaDon hoaDon = new HoaDon();
            hoaDon.setTenKhachHang("khách mua tại cửa hàng");
            hoaDon.setNgayDatHang(new Date());
            hoaDon.setTrangThai("Đã giao");
            hoaDon.setHinhThuc(hinhthucthanhtoan);
            hoaDon.setTongTien(tongtien);
            hoaDon.setMaDonHang(UUID.randomUUID().toString().replace("-", "").substring(10));
    
            hoaDon = hoaDonRepo.save(hoaDon);
            if (hoaDon != null && hdcts.size() > 0) {
                for (hdct _hdct : hdcts) {
                    HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                    hoaDonChiTiet.setHoaDon(hoaDon);
                    hoaDonChiTiet.setSoLuong(_hdct.getSoluong());
                    hoaDonChiTiet.setDonGia(_hdct.getDongia());
                    SanPhamChiTiet spct = new SanPhamChiTiet();
                    spct.setId(_hdct.getIdchitietsanpham());
                    hoaDonChiTiet.setSanPhamChiTiet(spct);
    
                    hoaDonChiTietRepo.save(hoaDonChiTiet);
                }
                
            }
            return ResponseEntity.ok().build();
        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
        
    }
}
