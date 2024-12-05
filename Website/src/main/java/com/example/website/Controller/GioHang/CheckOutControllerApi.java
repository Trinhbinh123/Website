package com.example.website.Controller.GioHang;

import com.example.website.Enity.GioHang;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.GioHangRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckOutControllerApi {
    private final GioHangRepo gioHangRepo;

    @PostMapping("/checkSoLuongSP")
    public String checkSoLuong(@RequestBody List<Integer> integers){
        System.out.println(integers);
        List<GioHang> gioHangs = new ArrayList<>();
        for (Integer i : integers) {
            gioHangs.add(gioHangRepo.getReferenceById(i));
        }
        for(GioHang gioHang : gioHangs){
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(sanPhamChiTiet.getSo_luong() < gioHang.getSoLuong()){
                return "Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ sanPhamChiTiet.getSo_luong() +" . Vui lòng giảm số lượng để mua";
            }
        }
        return null;
    }
}
