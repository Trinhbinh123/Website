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
@Table(name = "loaide")
public class LoaiDe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ma_loaide")
    private String ma_loaide;

    @Column(name="tenloaide")
    private String tenloaide;

    @Column(name="trangthai")
    private Integer trangthai;
}
