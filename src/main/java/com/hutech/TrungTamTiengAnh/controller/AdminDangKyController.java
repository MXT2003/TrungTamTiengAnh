package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/dangky")
public class AdminDangKyController {

    @Autowired
    private DangKyRepository dangKyRepository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<DangKy> pageData;
        if (status != null && !status.isBlank()) {
            pageData = dangKyRepository.findByTrangThai(status, PageRequest.of(page, size));
        } else {
            pageData = dangKyRepository.findAll(PageRequest.of(page, size));
        }
        model.addAttribute("page", pageData);
        model.addAttribute("status", status);

        return "admin/dangky/list";
    }

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

