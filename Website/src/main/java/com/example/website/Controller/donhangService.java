package com.example.website.Controller;

import com.example.website.Enity.DonHang;
import com.example.website.Enity.HoaDon;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class donhangService {
    @Autowired
    private HoaDonRepo donHangRepo ;

    // Phương thức lấy danh sách đơn hàng theo tháng
    public List<HoaDon> getDonHangsByMonth(Date startDate, Date endDate) {
        return donHangRepo.findByNgayDatHangBetween(startDate, endDate);
    }

    // Phương thức tính tổng tiền theo từng tháng
    public Map<String, Double> getThongKeTheoThang(List<HoaDon> donHangs) {
        return donHangs.stream()
                .collect(Collectors.groupingBy(d -> {
                    // Trích xuất năm và tháng từ ngày đặt hàng
                    return new SimpleDateFormat("yyyy-MM").format(d.getNgayDatHang());
                }, Collectors.summingDouble(HoaDon::getTongTien)));
    }
}

