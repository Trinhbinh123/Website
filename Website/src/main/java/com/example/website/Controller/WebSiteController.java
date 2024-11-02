package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.util.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSiteController {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;
    private final GioHangRepo gioHangRepo;
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("/website")
    public String getAdmin(Model model) {
        model.addAttribute("sanPhams", sanPhamRepo.findAll());
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        Integer soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        model.addAttribute("soLuongGioHang", soLuong);
        return "src/website/WebSite";
    }


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

    @GetMapping("/shop")
    public String shop() {
        return "src/website/shop"; // Trả về trang about.html
    }
    @PostMapping("/checkout")
    public String checkout(Model model,@RequestParam List<Integer> integers) {
        List<GioHang> gioHangs = new ArrayList<>();
        for(Integer i : integers){
            gioHangs.add(gioHangRepo.getReferenceById(i));
        }
        model.addAttribute("productDetail", gioHangs);
        model.addAttribute("list", integers);

        int tongTien = 0;
        for(GioHang gioHang : gioHangs){
            tongTien += gioHang.getTongTien();
        }
        model.addAttribute("tongTien", tongTien);


        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        model.addAttribute("KH", khachHang);
        return "src/website/checkout"; // Trả về trang about.html
    }

    @PostMapping("/checkout/success")
    public String checkoutSuccess(
            Model model,
            @RequestParam List<Integer> integers,
            KhachHang khachHang,
            @RequestParam String payment
    ) {
        KhachHang currentKhachHang = khachHangRepo.getReferenceById(khachHang.getId());

        String[] provinceDetails = getProvinceDetails(khachHang.getThanhPho());
        String[] districtDetails = getDistrictDetails(khachHang.getHuyen());
        String[] wardDetails = getWardDetails(khachHang.getXa());
        String provinceName = provinceDetails[1];
        String districtName = districtDetails[1];
        String wardName = wardDetails[1];


        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setDiaChi(provinceName + ", " + districtName + ", " + wardName + ", " + khachHang.getDiaChi());
        hoaDon.setMaDonHang(UUID.randomUUID().toString().replace("-", "").substring(10));
        hoaDon.setTenKhachHang(khachHang.getHoTen());
        hoaDon.setEmail(khachHang.getEmail());
        hoaDon.setSoDienThoai(khachHang.getSoDienThoai());
        hoaDon.setNgayDatHang(new Date());
        hoaDon.setTrangThai("Chờ xác nhận");
        if(payment.equals("1")){
            hoaDon.setHinhThuc("Thanh toán khi nhận hàng");
        }else {
            hoaDon.setHinhThuc("Thanh toán bằng VN Pay");
        }
        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);

        int tongTien = 0;

        for(Integer i : integers){
            GioHang gioHang = gioHangRepo.getReferenceById(i);

            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(saveHoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(gioHang.getSanPhamChiTiet());
            hoaDonChiTiet.setSoLuong(gioHang.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHang.getTongTien());
            tongTien += gioHang.getTongTien();
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }

        saveHoaDon.setTongTien(tongTien);
        hoaDonRepo.save(saveHoaDon);

        return "src/website/WebSite"; // Trả về trang about.html
    }

    private String[] getProvinceDetails(String provinceCode) {
        String url = "https://provinces.open-api.vn/api/p/" + provinceCode;
        return getDetailsFromApi(url);
    }

    private String[] getDistrictDetails(String districtCode) {
        String url = "https://provinces.open-api.vn/api/d/" + districtCode;
        return getDetailsFromApi(url);
    }

    private String[] getWardDetails(String wardCode) {
        String url = "https://provinces.open-api.vn/api/w/" + wardCode;
        return getDetailsFromApi(url);
    }
    private String[] getDetailsFromApi(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = response.getBody();


            Integer id = (Integer) data.get("code");
            String idString = id != null ? String.valueOf(id) : "Unknown";

            String name = (String) data.get("name");

            return new String[]{idString, name};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"Unknown", "Unknown"};
        }
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
