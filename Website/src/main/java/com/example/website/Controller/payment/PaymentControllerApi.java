package com.example.website.Controller.payment;

import com.example.website.Config.Config;
import com.example.website.Enity.*;
import com.example.website.Response.CheckOutResponse;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import com.example.website.dto.PaymentRestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentControllerApi {
    private final HoaDonRepo hoaDonRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @PostMapping("/createPayment")
    public ResponseEntity<?> createPayment(@RequestBody CheckOutResponse checkOutResponse, @RequestParam int money) throws UnsupportedEncodingException {
        List<GioHang> gioHangs = checkOutResponse.getGioHangs();
        KhachHang khachHang = checkOutResponse.getKhachHang();
        List<HoaDon> hoaDons = hoaDonRepo.findAll().stream()
                .filter(hoaDon -> hoaDon.getTrangThai().equals("Chờ xác nhận") || hoaDon.getTrangThai().equals("Xác nhận"))
                .toList();
        for(GioHang gioHang : gioHangs){
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.getReferenceById(gioHang.getSanPhamChiTiet().getId());
            int soLuong = 0;
            for (HoaDon hoaDon : hoaDons){
                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon).stream()
                        .filter(hoaDonChiTiet -> hoaDonChiTiet.getSanPhamChiTiet().equals(sanPhamChiTiet))
                        .toList();
                if(!hoaDonChiTiets.isEmpty()){
                    HoaDonChiTiet hoaDonChiTiet = hoaDonChiTiets.get(0);
                    soLuong += hoaDonChiTiet.getSoLuong();
                }
            }
            int soLuongTonKho = sanPhamChiTiet.getSo_luong() - soLuong;
            if(soLuongTonKho < gioHang.getSoLuong()){
                PaymentRestDTO paymentRestDTO = new PaymentRestDTO();
                paymentRestDTO.setStatus("0");
                paymentRestDTO.setMessage("Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ soLuongTonKho +" sản phẩm khả dụng . Vui lòng giảm số lượng để mua");

                return ResponseEntity.ok(paymentRestDTO);
            }
        }
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = money* 100L;
        String bankCode = "NCB";

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_Locale", "vn");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);


        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        PaymentRestDTO paymentRestDTO = new PaymentRestDTO();
        paymentRestDTO.setURL(paymentUrl);
        return ResponseEntity.ok(paymentRestDTO);
    }
}
