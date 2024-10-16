package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "binh_luan")
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "id_chi_tiet_san_pham")
    private Integer idChiTietSanPham;

    @Column(name = "binh_luan")
    private String binhLuan;

    @Column(name = "danh_gia")
    private Integer danhGia;
}
