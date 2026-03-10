package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/home")
    public String home(Model model) {

        // ===== Thống kê tổng quan =====
        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", userRepository.countByRole("TEACHER"));
        model.addAttribute("tongThu", paymentRepository.sumPaid());
        model.addAttribute("tongNo", paymentRepository.sumUnpaid());

        // ===== Biểu đồ đăng ký theo tháng =====
        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();

        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();

        for (Object[] obj : thongKeThang) {
            thangList.add((Integer) obj[0]);     // Tháng
            soLuongList.add((Long) obj[1]);     // Số lượng
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        // ===== Biểu đồ theo trạng thái đăng ký =====
        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();

        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();

        for (Object[] obj : thongKeTrangThai) {
            trangThaiList.add((String) obj[0]);       // Trạng thái
            soLuongTrangThai.add((Long) obj[1]);      // Số lượng
        }

        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        return "admin/home";
    }
}
