package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
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

        // Thong ke tong quan
        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", userRepository.countByRole("TEACHER"));
        model.addAttribute("tongThu", paymentRepository.sumPaid());
        model.addAttribute("tongNo", paymentRepository.sumUnpaid());

        // Bieu do dang ky theo thang
        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();

        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();

        for (Object[] obj : thongKeThang) {
            thangList.add((Integer) obj[0]);
            soLuongList.add((Long) obj[1]);
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        // Bieu do theo trang thai dang ky
        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();

        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();

        for (Object[] obj : thongKeTrangThai) {
            trangThaiList.add((String) obj[0]);
            soLuongTrangThai.add((Long) obj[1]);
        }

        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        return "admin/home";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", userRepository.countByRole("TEACHER"));
        model.addAttribute("tongThu", paymentRepository.sumPaid());
        model.addAttribute("tongNo", paymentRepository.sumUnpaid());

        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();
        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();
        for (Object[] obj : thongKeThang) {
            thangList.add((Integer) obj[0]);
            soLuongList.add((Long) obj[1]);
        }
        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();
        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();
        for (Object[] obj : thongKeTrangThai) {
            trangThaiList.add((String) obj[0]);
            soLuongTrangThai.add((Long) obj[1]);
        }
        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        return "admin/reports";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("classes", lopHocRepository.findAllByOrderByNgayBatDauAsc());
        return "admin/schedule";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("pendingRegistrations",
                dangKyRepository.findByTrangThaiOrderByNgayDangKyDesc("CHO_DUYET", PageRequest.of(0, 10)));
        model.addAttribute("unpaidPayments",
                paymentRepository.findByStatusOrderByDueDateAsc("UNPAID", PageRequest.of(0, 10)));
        model.addAttribute("today", LocalDate.now());
        return "admin/notifications";
    }
}
