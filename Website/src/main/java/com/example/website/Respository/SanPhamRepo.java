package com.example.website.Respository;

import com.example.website.Enity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface SanPhamRepo extends JpaRepository<SanPham,Integer> {
    @Query("SELECT p FROM SanPham p ORDER BY p.id DESC")
    Page<SanPham> findTop10ByOrderByCreatedAtDesc(Pageable pageable);
}
