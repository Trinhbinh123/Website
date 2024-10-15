package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Builder
@Table(name = "sanpham")
public class SanPham {
    @Column(name="id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="ma_sanpham")
    String ma_sanpham;
    @Column(name="tensanpham")
    String tensanpham;
    @Column(name="trangthai")
    Boolean trangthai;
    @ManyToOne
    @JoinColumn(name = "id_thuonghieu")
    ThuongHieu thuongHieu;
    Double giaban;
    String url_anh;
}
