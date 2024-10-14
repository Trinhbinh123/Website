package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "binhluan")
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_khachhang")
    private Integer idKhachHang;

    @Column(name = "id_chitietsanpham")
    private Integer idChiTietSanPham;

    @Column(name = "binhluan")
    private String binhLuan;

    @Column(name = "danhgia")
    private Integer danhGia;
}
