package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import com.example.website.Service.UserService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Controller
@RequiredArgsConstructor
public class WebSiteController {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;
    private final GioHangRepo gioHangRepo;
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final LoaiDeRepo loaiDeRepo;
    private final UserService userService;

    @GetMapping("/website")
    public String getAdmin(Model model, Authentication authentication) {
        int soLuong = 0;
        KhachHang khachHang = new KhachHang();
        if (authentication != null) {
            khachHang = userService.currentKhachHang(authentication);
            soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        }
        model.addAttribute("user", khachHang);
        model.addAttribute("soLuongGioHang", soLuong);
        org.springframework.data.domain.Pageable top10 = org.springframework.data.domain.PageRequest.of(0, 12);
        List<SanPham> sanPhams = sanPhamRepo.findTop10ByOrderByCreatedAtDesc(top10).getContent();
        model.addAttribute("sanPhams", sanPhams);

        return "src/website/WebSite";
    }


    @GetMapping("/detail/{idSanPham}")
    public String details(Model model, @PathVariable Integer idSanPham
    ) {
        List<ChatLieu> chatLieu = chatLieuRepo.findAll();
        model.addAttribute("chatlieu", chatLieu);
        List<LoaiDe> loaiDe = loaiDeRepo.findAll();
        model.addAttribute("loaide", loaiDe);
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
    public String shop(Authentication authentication, Model model) {
        int soLuong = 0;
        KhachHang khachHang = new KhachHang();
        if (authentication != null) {
            khachHang = userService.currentKhachHang(authentication);
            soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        }
        model.addAttribute("user", khachHang);
        model.addAttribute("soLuongGioHang", soLuong);
        return "src/website/shop"; // Trả về trang about.html
    }

    @PostMapping("/checkout") //3
    public String checkout(Model model, @RequestParam List<Integer> integers, Authentication authentication) {
        List<GioHang> gioHangs = new ArrayList<>();
        for (Integer i : integers) {
            gioHangs.add(gioHangRepo.getReferenceById(i));
        }
        model.addAttribute("productDetail", gioHangs);
        model.addAttribute("list", integers);

        int tongTien = 0;
        for (GioHang gioHang : gioHangs) {
            tongTien += gioHang.getTongTien();
        }
        model.addAttribute("tongTien", tongTien);

        if (authentication != null) {
            KhachHang khachHang = userService.currentKhachHang(authentication);
            model.addAttribute("KH", khachHang);
        } else {
            return "src/Login/login";
        }
        return "src/website/checkout"; // Trả về trang about.html
    }

    @PostMapping("/checkout/success") //1
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
        if (payment.equals("1")) {
            hoaDon.setHinhThuc("Thanh toán khi nhận hàng");
        } else {
            hoaDon.setHinhThuc("Thanh toán bằng VN Pay");
        }
        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);

        int tongTien = 0;

        for (Integer i : integers) {
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

        return "redirect:/website"; // Trả về trang about.html
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

    @GetMapping("/cart") //2
    public String cart(Model model, Authentication authentication) {
        if (authentication == null) {
            return "src/Login/login";
        }
        int soLuong = 0;
        KhachHang khachHang = new KhachHang();
        khachHang = userService.currentKhachHang(authentication);
        soLuong = gioHangRepo.findByKhachHang(khachHang).size();
        model.addAttribute("user", khachHang);
        model.addAttribute("soLuongGioHang", soLuong);
        return "src/website/cart"; // Trả về trang about.html
    }

    @GetMapping("/contact")
    public String contact() {
        return "src/website/contact"; // Trả về trang about.html
    }


//    @GetMapping("/generate-qr")
//    @ResponseBody
//    public ResponseEntity<byte[]> generateQRCode(@RequestParam("paymentInfo") String paymentInfo) {
//        try {
//            int width = 250;
//            int height = 250;
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(paymentInfo, BarcodeFormat.QR_CODE, width, height);
//
//            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
//                }
//            }
//
//            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "PNG", pngOutputStream);
//            byte[] qrCodeImage = pngOutputStream.toByteArray();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr.png")
//                    .contentType(MediaType.IMAGE_PNG)
//                    .body(qrCodeImage);
//        } catch (WriterException | IOException e) {
//            return ResponseEntity.badRequest().build();
//        }
//
//    }
    @GetMapping("/quenMk/{email}")
    public String quenMk(@PathVariable String email, Model model){
        if(email != null){
            KhachHang khachHang = khachHangRepo.findByEmail(email);
            model.addAttribute("id", khachHang.getId());
        }
        return "/src/Login/quenMatKhau";
    }
    @GetMapping("/quenMk/changePassword/{id}/{newPassword}")
    public String changePass(
            @PathVariable Integer id,
            @PathVariable String newPassword
    ){
        System.out.println(newPassword);
        System.out.println(id);
        KhachHang khachHang = khachHangRepo.getReferenceById(id);
        khachHang.setMatKhau(newPassword);
        khachHangRepo.save(khachHang);
        return "redirect:/login";
    }
}


