package com.example.website.Respository;

import com.example.website.Enity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,Integer> {
    List<SanPhamChiTiet> findBySanPham(SanPham sanPham);
    List<SanPhamChiTiet> findBySanPhamAndMauSacAndSize(SanPham sanPham, MauSac mauSac, Size size);
    SanPhamChiTiet findByKhuyenMaiChiTiet(KhuyenMaiChiTiet khuyenMaiChiTiet);
}
