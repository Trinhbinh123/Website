package com.example.website.Respository;

import com.example.website.Enity.SanPham;
import com.example.website.Response.SanPhamOfficeResponse;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SanPhamRepo extends JpaRepository<SanPham,Integer> , SanPhamRepoCustom{
    @Query("SELECT p FROM SanPham p ORDER BY p.id DESC")
    Page<SanPham> findTop10ByOrderByCreatedAtDesc(Pageable pageable);
}
