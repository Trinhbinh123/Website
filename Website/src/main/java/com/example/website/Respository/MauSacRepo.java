package com.example.website.Respository;

import com.example.website.Enity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MauSacRepo extends JpaRepository<MauSac,Integer> {
//    List<MauSac> findByTrangThai(Integer trangThai);
}
