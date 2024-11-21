package com.example.website.Controller;

import com.example.website.Enity.SanPham;
import com.example.website.Respository.ChatLieuRepo;
import com.example.website.Respository.LoaiDeRepo;
import com.example.website.Respository.SanPhamRepo;
import com.example.website.Respository.ThuongHieuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamRepo sanPhamRepo;
    private final ThuongHieuRepo thuongHieuRepo; // Thêm repo cho Thương Hiệu
    private final LoaiDeRepo loaiDeRepo;
    private final ChatLieuRepo chatLieuRepo;

    @GetMapping("/admin/san-pham")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("sp",sanPhamRepo.findAll());
        return "src/san-pham/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/san-pham/add")
    public String getAddPage(Model model) {
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        model.addAttribute("listLD", loaiDeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());

        // Tạo mã sản phẩm mới (mã SP tự động tăng)
        String maSanPhamMoi = "SP" + String.format("%03d", sanPhamRepo.count() + 1);  // Giả sử mã sản phẩm tăng dần từ 001
        model.addAttribute("maSanPhamMoi", maSanPhamMoi);
        return "src/san-pham/AddSanPham"; // Template thêm sản phẩm
    }

    @PostMapping("/san-pham/delete")
    public String delete(@RequestParam Integer id) {
        SanPham sanPham = sanPhamRepo.getReferenceById(id);
        if (sanPham != null) {
            sanPham.setTrangthai(false); // Đặt trạng thái thành Inactive
            sanPhamRepo.save(sanPham); // Lưu thay đổi
        }
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/san-pham/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        SanPham sanPham = sanPhamRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        // Truyền đối tượng sản phẩm vào model
        model.addAttribute("SP", sanPham);
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        model.addAttribute("listLD", loaiDeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());
        return "src/san-pham/UpdateSanPham"; // Template cập nhật sản phẩm
    }

    @PostMapping("/san-pham/updateData")
    public String update(@ModelAttribute SanPham sanPham) {
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamRepo.save(sanPham); // Lưu cập nhật
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/san-pham/save")
    public String save(@ModelAttribute SanPham sanPham, Model model) {
        // Kiểm tra nếu tên sản phẩm trống hoặc chỉ chứa khoảng trắng
        if (sanPham.getTensanpham().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Tên sản phẩm không thể để trống hoặc chỉ chứa khoảng trắng.");
            return "src/san-pham/AddSanPham"; // Trả lại trang thêm sản phẩm với thông báo lỗi
        }
        if (sanPham.getMa_sanpham() == null || sanPham.getMa_sanpham().isEmpty()) {
            sanPham.setMa_sanpham(generateProductCode());
        }
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active
        sanPhamRepo.save(sanPham);
        return "redirect:/admin/san-pham";
    }
    public String generateProductCode() {
        // Lấy danh sách mã sản phẩm
        List<String> productCodes = sanPhamRepo.findAll()
                .stream()
                .map(SanPham::getMa_sanpham)
                .collect(Collectors.toList());

        // Tìm mã sản phẩm cao nhất
        int maxNumber = productCodes.stream()
                .filter(code -> code.startsWith("SP")) // Lọc mã bắt đầu bằng "SP"
                .map(code -> Integer.parseInt(code.substring(2))) // Lấy phần số sau "SP"
                .max(Comparator.naturalOrder())
                .orElse(0); // Nếu không có mã nào, mặc định là 0

        // Sinh mã mới
        int newNumber = maxNumber + 1;
        if (newNumber > 999) {
            throw new RuntimeException("Số lượng mã sản phẩm đã đạt giới hạn");
        }

        // Trả về mã sản phẩm mới (dạng SP001, SP002,...)
        return String.format("SP%03d", newNumber);
    }
}
