package com.example.website.Controller;

import com.example.website.Enity.DonHang;
import com.example.website.Enity.HoaDon;
import com.example.website.Enity.ThongKe;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.HoaDonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ThongKeController {
    private final donhangService DonHangRepo;

    @GetMapping("/admin/thongke")
    public String getAdmin() {
        return "src/thongke/ThongKe";
    }

    @GetMapping("/thong-ke-theo-thang")
    public Map<String, Double> getThongKeTheoThang(@RequestParam String month) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date startDate = sdf.parse(month + "-01");
        Date endDate = sdf.parse(month + "-31");

        List<HoaDon> donHangs = DonHangRepo.getDonHangsByMonth(startDate, endDate);

        return DonHangRepo.getThongKeTheoThang(donHangs);
    }
}

