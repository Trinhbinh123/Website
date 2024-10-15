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
@Table(name = "chatlieu")
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ma_chatlieu")
    private String ma_chatlieu;

    @Column(name="tenchatlieu")
    private String tenchatlieu;

    @Column(name="trangthai")
    private Integer trangthai;
}
