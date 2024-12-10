package com.example.website.Enity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Builder
@Table(name = "chi_tiet_san_pham")
public class SanPhamChiTiet {
    @Column(name="id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    SanPham sanPham;
    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    MauSac mauSac;
    @ManyToOne
    @JoinColumn(name = "id_size")
    Size size;
    @Column(name="ma_chi_tiet_san_pham")
    String ma_SPCT;
    @Column(name="so_luong")
    Integer so_luong;
    @Column(name="gia_nhap")
    Double gia_nhap;
    @Column(name="anh_spct")
    String anh_spct;
    @Column(name="gia_ban")
    Double gia_ban;
    @Column(name = "ngay_nhap")
    private LocalDateTime ngay_nhap;
    @Column(name="trang_thai")
    Boolean trang_thai;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "id_san_pham_khuyen_mai")
    private KhuyenMaiChiTiet khuyenMaiChiTiet;

}
