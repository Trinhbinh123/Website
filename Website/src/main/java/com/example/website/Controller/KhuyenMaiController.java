package com.example.website.Controller;

import com.example.website.Enity.KhuyenMai;
import com.example.website.Response.KhuyenMailResponse;
import com.example.website.Respository.KhuyenMaiRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class KhuyenMaiController {
    @Autowired
    private final KhuyenMaiRepo khuyenMaiRepo;

    @GetMapping("/admin/khuyenmai")
    public String getAdmin(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<KhuyenMailResponse> khuyenMailResponses = new ArrayList<>();
        List<KhuyenMai> khuyenMais = khuyenMaiRepo.findAll();
        for(KhuyenMai khuyenMai : khuyenMais){
            KhuyenMailResponse khuyenMailResponse = new KhuyenMailResponse();
            BeanUtils.copyProperties(khuyenMai,khuyenMailResponse);
            if (khuyenMai.getNgayTao() != null) {
                khuyenMailResponse.setNgayTao(khuyenMai.getNgayTao().format(formatter));
            } else {
                khuyenMailResponse.setNgayTao("Ngày tạo không xác định");
            }
            khuyenMailResponse.setNgayBatDau(khuyenMai.getNgayBatDau().format(formatter));
            khuyenMailResponse.setNgayKetThuc(khuyenMai.getNgayKetThuc().format(formatter));
            if(khuyenMai.getNgaySua() != null){
                khuyenMailResponse.setNgaySua(khuyenMai.getNgaySua().format(formatter));
            }
            khuyenMailResponses.add(khuyenMailResponse);
        }
        model.addAttribute("khuyenMaiList", khuyenMailResponses);
        return "src/khuyenmai/KhuyenMai";
    }
    @GetMapping("/khuyenmai/addkhuyenmai")
    public String addKhuyenMai(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "src/khuyenmai/AddKhuyenMai";
    }
    @PostMapping("/khuyenmai/addkhuyenmai/save")
    public String saveKM(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai){
        System.out.println(khuyenMai);
        khuyenMai.setNgayTao(LocalDateTime.now());
        khuyenMai.setTinhTrang("Sắp diễn ra");
        khuyenMai.setTrangThai(true);
        khuyenMaiRepo.save(khuyenMai);
        return "redirect:/admin/khuyenmai";
    }

    @GetMapping("/khuyenmai/delete/{id}")
    public String deleteKM(@PathVariable Integer id){
        KhuyenMai khuyenMai = khuyenMaiRepo.getReferenceById(id);
        khuyenMai.setTrangThai(!khuyenMai.isTrangThai());
        khuyenMaiRepo.save(khuyenMai);
        return "redirect:/admin/khuyenmai";
    }
    @GetMapping("/khuyenmai/updatePage")
    public String updatePage(Model model, @RequestParam Integer id){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        KhuyenMai khuyenMai = khuyenMaiRepo.getReferenceById(id);
        KhuyenMailResponse khuyenMailResponse = new KhuyenMailResponse();
        BeanUtils.copyProperties(khuyenMai, khuyenMailResponse);
        khuyenMailResponse.setNgayBatDau(khuyenMai.getNgayBatDau().format(formatter));
        khuyenMailResponse.setNgayKetThuc(khuyenMai.getNgayKetThuc().format(formatter));
        model.addAttribute("KM", khuyenMailResponse);
        return "src/khuyenmai/UpdateKhuyenMai";
    }
    @PostMapping("/khuyenmai/update")
    public String updateKM(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai){
        KhuyenMai newKM = khuyenMaiRepo.getReferenceById(khuyenMai.getId());
        newKM.setNgaySua(LocalDateTime.now());
        newKM.setTenKhuyenMai(khuyenMai.getTenKhuyenMai());
        newKM.setGiaTriGiam(khuyenMai.getGiaTriGiam());
        newKM.setNgayKetThuc(khuyenMai.getNgayKetThuc());
        newKM.setNgayBatDau(khuyenMai.getNgayBatDau());
        newKM.setLoaiKhuyenMai(khuyenMai.getLoaiKhuyenMai());
        khuyenMaiRepo.save(newKM);
        return "redirect:/admin/khuyenmai";
    }
}
