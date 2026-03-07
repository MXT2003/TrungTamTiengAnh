package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/phancong")
public class PhanCongController {

    private final LopHocRepository lopHocRepository;
    private final TeacherRepository teacherRepository;

    public PhanCongController(LopHocRepository lopHocRepository, TeacherRepository teacherRepository) {
        this.lopHocRepository = lopHocRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<LopHoc> classes = lopHocRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("classes", classes);
        model.addAttribute("teachers", teachers);
        return "admin/phancong/list";
    }

    @PostMapping("/{id}")
    public String assign(@PathVariable Long id,
                         @RequestParam(required = false) Long teacherId) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/admin/phancong";
        }
        if (teacherId == null) {
            lopHoc.setTeacher(null);
            lopHocRepository.save(lopHoc);
            return "redirect:/admin/phancong";
        }
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null) {
            lopHoc.setTeacher(teacher);
            if (teacher.getFullName() != null && !teacher.getFullName().isBlank()) {
                lopHoc.setGiaoVien(teacher.getFullName());
            }
            lopHocRepository.save(lopHoc);
        }
        return "redirect:/admin/phancong";
    }
}
