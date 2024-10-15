package com.example.website.Respository;

import com.example.website.Enity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface SanPhamRepo extends JpaRepository<SanPham,Integer> {
}
