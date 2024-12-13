package com.example.website.Controller.GioHang;

import com.example.website.Enity.*;
import com.example.website.Response.CheckOutResponse;
import com.example.website.Respository.GioHangRepo;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.KhachHangRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.website.Controller.GioHang.GioHangControllerApi.getGia_ban;
import static com.example.website.Controller.WebControllerMap.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckOutControllerApi {
    private final GioHangRepo gioHangRepo;
    private final KhachHangRepo khachHangRepo;
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @PostMapping("/checkSoLuongSP")
    public String checkSoLuong(@RequestBody List<Integer> integers){
        List<GioHang> gioHangs = new ArrayList<>();
        for (Integer i : integers) {
            if(gioHangRepo.existsById(i)){
                gioHangs.add(gioHangRepo.getReferenceById(i));
            }else {
                return "Một số sản phẩm trong giỏ hàng đã được thay đổi. Vui lòng kiểm tra lại";
            }
        }
        for(GioHang gioHang : gioHangs){
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(sanPhamChiTiet.getSo_luong() < gioHang.getSoLuong()){
                return "Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ sanPhamChiTiet.getSo_luong() +" . Vui lòng giảm số lượng để mua";
            }
        }
        return null;
    }

    @PostMapping("/getItem")
    public List<GioHang> getItem(@RequestBody List<GioHang> gioHangs){
        List<GioHang> gioHangList = new ArrayList<>();
        for(GioHang gioHang : gioHangs){
            GioHang existingGioHang = gioHangRepo.getReferenceById(gioHang.getId());
            GioHang gh = new GioHang();
            gh.setId(existingGioHang.getId());
            gh.setSoLuong(gioHang.getSoLuong());
            gh.setSanPhamChiTiet(existingGioHang.getSanPhamChiTiet());
            getGia_ban(gh.getSanPhamChiTiet(), gh);
            if(gh.getSanPhamChiTiet().getKhuyenMaiChiTiet() != null && !gh.getSanPhamChiTiet().getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                gh.getSanPhamChiTiet().setKhuyenMaiChiTiet(null);
            }
            gioHangList.add(gh);
        }
        return gioHangList;
    }

    @PostMapping("/successCheckout")
    public String success(
            @RequestBody CheckOutResponse checkOutResponse,
            @RequestParam String payment
    ){
        List<GioHang> gioHangs = checkOutResponse.getGioHangs();
        KhachHang khachHang = checkOutResponse.getKhachHang();
        for(GioHang gioHang : gioHangs){
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(sanPhamChiTiet.getSo_luong() < gioHang.getSoLuong()){
                return "Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ sanPhamChiTiet.getSo_luong() +" . Vui lòng giảm số lượng để mua";
            }
        }
        String[] provinceDetails = getProvinceDetails(khachHang.getThanhPho());
        String[] districtDetails = getDistrictDetails(khachHang.getHuyen());
        String[] wardDetails = getWardDetails(khachHang.getXa());
        String provinceName = provinceDetails[1];
        String districtName = districtDetails[1];
        String wardName = wardDetails[1];


        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setDiaChi(provinceName + ", " + districtName + ", " + wardName + ", " + khachHang.getDiaChi());
        hoaDon.setMaDonHang("#" + UUID.randomUUID().toString().replace("-", "").substring(10).toUpperCase(Locale.ROOT));
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

        for (GioHang gioHang : gioHangs) {
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
        return null;
    }
}
