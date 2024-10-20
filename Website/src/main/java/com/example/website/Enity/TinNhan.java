package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tin_nhan")
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_admin")
    private Integer idAdmin;

    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "ngaygui")
    private Date ngayGui;
}
