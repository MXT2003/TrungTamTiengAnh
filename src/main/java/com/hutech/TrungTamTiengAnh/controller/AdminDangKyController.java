package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/dangky")
public class AdminDangKyController {

    @Autowired
    private DangKyRepository dangKyRepository;

    // Hiển thị danh sách đăng ký
    @GetMapping
    public String list(Model model) {

        List<DangKy> list = dangKyRepository.findAll();
        model.addAttribute("list", list);

        return "admin/dangky/list";
    }

    // Duyệt đăng ký
    @GetMapping("/duyet/{id}")
    public String duyet(@PathVariable Long id) {

        DangKy dk = dangKyRepository.findById(id).orElse(null);

        if (dk != null) {
            dk.setTrangThai("DA_DUYET");
            dk.setGhiChu(null);
            dk.setNgayCapNhat(LocalDate.now());
            dangKyRepository.save(dk);
        }

        return "redirect:/admin/dangky";
    }

    // Tu choi dang ky
    @PostMapping("/tuchoi/{id}")
    public String tuChoi(@PathVariable Long id,
                         @RequestParam(required = false) String ghiChu) {
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk != null) {
            dk.setTrangThai("TU_CHOI");
            dk.setGhiChu(ghiChu);
            dk.setNgayCapNhat(LocalDate.now());
            dangKyRepository.save(dk);
        }
        return "redirect:/admin/dangky";
    }
}
