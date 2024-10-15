package com.example.website.Enity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Entity
@Builder
@Table(name = "mausac")
@AllArgsConstructor
@NoArgsConstructor
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Mã màu sắc không được để trống")
    @Size(max = 20, message = "Mã màu sắc không được vượt quá 20 ký tự")
    @Column(name = "ma_mausac")
    private String ma_mausac;

    @NotBlank(message = "Tên màu sắc không được để trống")
    @Size(max = 50, message = "Tên màu sắc không được vượt quá 50 ký tự")
    @Column(name = "tenmausac")
    private String tenmausac;

    @Column(name = "trangthai")
    private Integer trangthai;
}