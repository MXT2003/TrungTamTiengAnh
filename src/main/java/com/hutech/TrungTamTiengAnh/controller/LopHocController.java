package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/lophoc")
public class LopHocController {

    @Autowired
    private LopHocRepository lopHocRepository;

    // 📌 Hiển thị danh sách
    @GetMapping
    public String list(Model model) {
        List<LopHoc> list = lopHocRepository.findAll();
        model.addAttribute("list", list);
        return "admin/lophoc/list";
    }

    // 📌 Form thêm
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("lophoc", new LopHoc());
        return "admin/lophoc/form";
    }

    // 📌 Lưu (Thêm + Sửa)
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("lophoc") LopHoc lopHoc,
                       org.springframework.validation.BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/lophoc/form";
        }

        boolean hasThu = lopHoc.getThu() != null && !lopHoc.getThu().isBlank();
        boolean hasStart = lopHoc.getGioBatDau() != null;
        boolean hasEnd = lopHoc.getGioKetThuc() != null;

        if ((hasThu || hasStart || hasEnd) && !(hasThu && hasStart && hasEnd)) {
            bindingResult.reject("schedule", "Vui long nhap day du thu, gio bat dau va gio ket thuc.");
            return "admin/lophoc/form";
        }

        if (hasThu && hasStart && hasEnd && !lopHoc.getGioBatDau().isBefore(lopHoc.getGioKetThuc())) {
            bindingResult.reject("schedule", "Gio bat dau phai nho hon gio ket thuc.");
            return "admin/lophoc/form";
        }

        if (lopHoc.getThu() != null && !lopHoc.getThu().isBlank()
                && lopHoc.getGioBatDau() != null && lopHoc.getGioKetThuc() != null) {
            lopHoc.setLichHoc(lopHoc.getThu() + " " + lopHoc.getGioBatDau() + "-" + lopHoc.getGioKetThuc());
        }

        lopHocRepository.save(lopHoc);
        return "redirect:/admin/lophoc";
    }

    // 📌 Form sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        model.addAttribute("lophoc", lopHoc);
        return "admin/lophoc/form";
    }

    // 📌 Xoá
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        lopHocRepository.deleteById(id);
        return "redirect:/admin/lophoc";
    }
}


