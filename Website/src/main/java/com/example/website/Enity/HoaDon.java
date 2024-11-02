package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "don_hang")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "ma_don_hang")
    private String maDonHang;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "email")
    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "ngay_dat_hang")
    private Date ngayDatHang;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "tong_tien")
    private int tongTien;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "hinh_thuc")
    private String hinhThuc;
}
