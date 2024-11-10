package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@Table(name = "mau_sac")
@AllArgsConstructor
@NoArgsConstructor
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_mau_sac")
    private String ma_mausac;

    @Column(name = "ten_mau_sac")
    private String tenmausac;

    @Column(name = "trang_thai")
    private Integer trangthai;
}