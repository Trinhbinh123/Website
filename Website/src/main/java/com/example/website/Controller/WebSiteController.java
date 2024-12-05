package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSiteController {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;
    private final GioHangRepo gioHangRepo;
    private final ChatLieuRepo  chatLieuRepo;
    private final LoaiDeRepo  loaiDeRepo;

    @GetMapping("/website")
    public String getAdmin(Model model) {
        org.springframework.data.domain.Pageable top10 = org.springframework.data.domain.PageRequest.of(0, 12);
        List<SanPham> sanPhams = sanPhamRepo.findTop10ByOrderByCreatedAtDesc(top10).getContent();
        model.addAttribute("sanPhams", sanPhams);
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        Integer soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        model.addAttribute("soLuongGioHang", soLuong);
        return "src/website/WebSite";
    }


    @GetMapping("/detail/{idSanPham}")
    public String details(Model model, @PathVariable Integer idSanPham
                          ) {
        List<ChatLieu> chatLieu = chatLieuRepo.findAll();
        model.addAttribute("chatlieu",chatLieu);
        List<LoaiDe> loaiDe = loaiDeRepo.findAll();
        model.addAttribute("loaide",loaiDe);
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        model.addAttribute("khachHang", khachHang);
        SanPham sanPham = sanPhamRepo.getReferenceById(idSanPham);
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepo.findBySanPham(sanPham);
        List<MauSac> mauSacs = new ArrayList<>();
        List<Size> sizes = new ArrayList<>();
        if (sanPhamChiTiets != null && !sanPhamChiTiets.isEmpty()) {
            for (SanPhamChiTiet spc : sanPhamChiTiets) {
                mauSacs.add(spc.getMauSac());
                sizes.add(spc.getSize());
            }
        }
        Double giaMacDinh = sanPham.getGiaban();
        Set<MauSac> listMauSac = new HashSet<>(mauSacs);
        Set<Size> listSize = new HashSet<>(sizes);
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("sanPhamChiTiets", sanPhamChiTiets);
        model.addAttribute("mauSacs", new ArrayList<>(listMauSac));
        model.addAttribute("sizes", new ArrayList<>(listSize));
        model.addAttribute("giaMacDinh", giaMacDinh);
        return "src/website/detail";
    }
    @GetMapping("/get-price")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPrice(
            @RequestParam Integer idSanPham,
            @RequestParam Integer sizeId) {
        // Lấy sản phẩm dựa vào ID
        SanPham sanPham = sanPhamRepo.findById(idSanPham)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        // Lấy thông tin sản phẩm chi tiết dựa vào sản phẩm và kích thước
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findBySanPhamAndSize_Id(sanPham, sizeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin sản phẩm cho kích thước này"));

        // Chuẩn bị phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("gia_ban", sanPhamChiTiet.getGia_ban()); // Giá của sản phẩm
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shop")
    public String shop() {
        return "src/website/shop"; // Trả về trang about.html
    }

//    @PostMapping("/checkout")
//    public String checkout(Model model, @RequestParam List<Integer> integers) {
//        List<GioHang> gioHangs = new ArrayList<>();
//        for (Integer i : integers) {
//            gioHangs.add(gioHangRepo.getReferenceById(i));
//        }
//        model.addAttribute("productDetail", gioHangs);
//        model.addAttribute("list", integers);
//
//        int tongTien = 0;
//        for (GioHang gioHang : gioHangs) {
//            tongTien += gioHang.getTongTien();
//        }
//        model.addAttribute("tongTien", tongTien);
//
//
//        KhachHang khachHang = khachHangRepo.getReferenceById(1);
//        model.addAttribute("KH", khachHang);
//        return "src/website/checkout"; // Trả về trang about.html
//    }
//
//    @PostMapping("/checkout/success")
//    public String checkoutSuccess(
//            Model model,
//            @RequestParam List<Integer> integers,
//            KhachHang khachHang,
//            @RequestParam String payment
//    ) {
//        KhachHang currentKhachHang = khachHangRepo.getReferenceById(khachHang.getId());
//
//        String[] provinceDetails = getProvinceDetails(khachHang.getThanhPho());
//        String[] districtDetails = getDistrictDetails(khachHang.getHuyen());
//        String[] wardDetails = getWardDetails(khachHang.getXa());
//        String provinceName = provinceDetails[1];
//        String districtName = districtDetails[1];
//        String wardName = wardDetails[1];
//
//
//        HoaDon hoaDon = new HoaDon();
//        hoaDon.setKhachHang(khachHang);
//        hoaDon.setDiaChi(provinceName + ", " + districtName + ", " + wardName + ", " + khachHang.getDiaChi());
//        hoaDon.setMaDonHang(UUID.randomUUID().toString().replace("-", "").substring(10));
//        hoaDon.setTenKhachHang(khachHang.getHoTen());
//        hoaDon.setEmail(khachHang.getEmail());
//        hoaDon.setSoDienThoai(khachHang.getSoDienThoai());z
//        hoaDon.setNgayDatHang(new Date());
//        hoaDon.setTrangThai("Chờ xác nhận");
//        if (payment.equals("1")) {
//            hoaDon.setHinhThuc("Thanh toán khi nhận hàng");
//        } else {
//            hoaDon.setHinhThuc("Thanh toán bằng VN Pay");
//        }
//        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);
//
//        int tongTien = 0;
//
//        for (Integer i : integers) {
//            GioHang gioHang = gioHangRepo.getReferenceById(i);
//
//            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
//            hoaDonChiTiet.setHoaDon(saveHoaDon);
//            hoaDonChiTiet.setSanPhamChiTiet(gioHang.getSanPhamChiTiet());
//            hoaDonChiTiet.setSoLuong(gioHang.getSoLuong());
//            hoaDonChiTiet.setDonGia(gioHang.getTongTien());
//            tongTien += gioHang.getTongTien();
//            hoaDonChiTietRepo.save(hoaDonChiTiet);
//        }
//
//        saveHoaDon.setTongTien(tongTien);
//        hoaDonRepo.save(saveHoaDon);
//
//        return "src/website/WebSite"; // Trả về trang about.html
//    }

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
    @PostMapping("/cart")
    public ResponseEntity<String> addToCart(@RequestBody GioHang item) {
        gioHangRepo.save(item); // Lưu vào CSDL
        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng.");
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