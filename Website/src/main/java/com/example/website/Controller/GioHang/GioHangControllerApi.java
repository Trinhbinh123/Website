package com.example.website.Controller.GioHang;

import com.example.website.Enity.GioHang;
import com.example.website.Enity.KhachHang;
import com.example.website.Enity.SanPham;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class GioHangControllerApi {
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final GioHangRepo gioHangRepo;
    private final KhachHangRepo khachHangRepo;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepo mauSacRepo;
    private final SizeRepo sizeRepo;

    @PostMapping("/{idSanPham}/{idMauSac}/{idSize}/{soLuong}")
    public String addToCart(
            @PathVariable Integer idSanPham,
            @PathVariable Integer idMauSac,
            @PathVariable Integer idSize,
            @PathVariable Integer soLuong
    ){
        KhachHang khachHang = khachHangRepo.getReferenceById(1); // sau lấy từ authen rồi thế vào
        GioHang gioHang = new GioHang();
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findBySanPhamAndMauSacAndSize(
                sanPhamRepo.getReferenceById(idSanPham),
                mauSacRepo.getReferenceById(idMauSac),
                sizeRepo.getReferenceById(idSize)).get(0);
        sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() - soLuong);

        gioHang.setKhachHang(khachHang);
        boolean check = false;
        List<GioHang> gioHangs = gioHangRepo.findByKhachHang(khachHang);
        for(GioHang gh : gioHangs){
            if(gh.getSanPhamChiTiet().equals(sanPhamChiTiet)){
                gh.setSoLuong(gh.getSoLuong() + soLuong);
                gh.setTongTien((int) (sanPhamChiTiet.getGia_ban() * gh.getSoLuong()));
                gioHangRepo.save(gh);
                check = true;
                break;
            }
        }
        if(check){
            return null;
        }
        gioHang.setSanPhamChiTiet(sanPhamChiTiet);
        gioHang.setSoLuong(soLuong);
        gioHang.setTongTien((int) (sanPhamChiTiet.getGia_ban() * gioHang.getSoLuong()));
        gioHangRepo.save(gioHang);
        return null;
    }

    @GetMapping("/cartPage")
    public List<GioHang> cartPage(){
        KhachHang khachHang = khachHangRepo.getReferenceById(1);
        return gioHangRepo.findByKhachHang(khachHang);
    }

    @PutMapping("/quantity/{idCart}")
    public GioHang setQuantity(@PathVariable Integer idCart, @RequestParam String status){
        GioHang gioHang = gioHangRepo.getReferenceById(idCart);
        SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
        if(status.equals("cong")){
            gioHang.setSoLuong(gioHang.getSoLuong() + 1);
            gioHang.setTongTien((int) (gioHang.getSanPhamChiTiet().getGia_ban() * gioHang.getSoLuong()));
            sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() - 1);
        }else {
            if(gioHang.getSoLuong() <= 1){
                return null;
            }
            gioHang.setSoLuong(gioHang.getSoLuong() - 1);
            gioHang.setTongTien((int) (gioHang.getSanPhamChiTiet().getGia_ban() * gioHang.getSoLuong()));
            sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + 1);
        }
        sanPhamChiTietRepo.save(sanPhamChiTiet);
        return gioHangRepo.save(gioHang);
    }

    @DeleteMapping("/deleteCart/{idCart}")
    public void deleteCart(@PathVariable Integer idCart){
        GioHang gioHang = gioHangRepo.getReferenceById(idCart);
        SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
        sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + gioHang.getSoLuong());
        gioHangRepo.delete(gioHang);
        sanPhamChiTietRepo.save(sanPhamChiTiet);
    }

    @PostMapping("/tongTien")
    public Integer tongTien(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> idGioHang = requestData.get("idGioHang");
        int tongTien = 0;
        for(Integer i : idGioHang){
            GioHang gioHang = gioHangRepo.getReferenceById(i);
            tongTien += gioHang.getTongTien();
        }
        return tongTien;
    }

}
