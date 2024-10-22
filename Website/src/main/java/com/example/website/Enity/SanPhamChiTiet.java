package com.example.website.Enity;

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
    @JoinColumn(name = "id_chat_lieu")
    ChatLieu chatLieu;
    @ManyToOne
    @JoinColumn(name = "id_size")
    Size size;
    @ManyToOne
    @JoinColumn(name = "id_loai_de")
    LoaiDe loaiDe;
    @Column(name="ma_chi_tiet_san_pham")
    String ma_SPCT;
    @Column(name="so_luong")
    Integer so_luong;
    @Column(name="gia_nhap")
    Double gia_nhap;
    @Column(name="gia_ban")
    Double gia_ban;
    @Column(name = "ngay_nhap")
    private LocalDateTime ngay_nhap;
    @Column(name="trang_thai")
    Boolean trang_thai;

}