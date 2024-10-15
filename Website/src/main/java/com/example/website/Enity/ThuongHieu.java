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
@Table(name = "thuonghieu")
public class ThuongHieu {
    @Column(name="id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="tenthuonghieu")
    String tenThuongHieu;
    @Column(name="mota")
    String moTa;

}
