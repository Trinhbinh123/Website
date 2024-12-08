package com.example.website.Respository;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    // Thêm phương thức sắp xếp theo ngày đặt hàng giảm dần
    @Query("SELECT h FROM HoaDon h ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findAllOrderByNgayDatHangDesc();

    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = :trangThai ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findByTrangThaiOrderByNgayDatHangDesc(@Param("trangThai") String trangThai);
    List<HoaDon> findByTrangThai(String trangThai);
    long countByTrangThai(String trangThai);
    List<HoaDon> findByKhachHang(KhachHang khachHang);

    @Query("SELECT h FROM HoaDon h WHERE h.tenKhachHang = :tenKhachHang ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findAllSaleOffice(@Param("tenKhachHang") String tenKhachHang);

    @Query("SELECT h FROM HoaDon h WHERE h.id = :idDonHang")
    HoaDon findDetailSaleOffice(@Param("idDonHang") int idDonHang);

}
