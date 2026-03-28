package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Course;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.repository.CourseRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/lophoc")
public class LopHocController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        Page<LopHoc> pageData;
        if (q != null && !q.isBlank()) {
            pageData = lopHocRepository.findByTenLopContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
        } else {
            pageData = lopHocRepository.findAll(PageRequest.of(safePage, safeSize));
        }
        if (pageData.getTotalPages() > 0 && safePage >= pageData.getTotalPages()) {
            safePage = pageData.getTotalPages() - 1;
            if (q != null && !q.isBlank()) {
                pageData = lopHocRepository.findByTenLopContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
            } else {
                pageData = lopHocRepository.findAll(PageRequest.of(safePage, safeSize));
            }
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "admin/lophoc/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("lophoc", new LopHoc());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "admin/lophoc/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("lophoc") LopHoc lopHoc,
                       org.springframework.validation.BindingResult bindingResult,
                       @RequestParam(value = "courseId", required = false) Long courseId,
                       @RequestParam(value = "teacherId", required = false) Long teacherId,
                       @RequestParam(value = "thu", required = false) java.util.List<String> thuList,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", courseRepository.findAll());
            model.addAttribute("teachers", teacherRepository.findAll());
            return "admin/lophoc/form";
        }

        boolean hasThu = thuList != null && !thuList.isEmpty();
        boolean hasStart = lopHoc.getGioBatDau() != null;
        boolean hasEnd = lopHoc.getGioKetThuc() != null;

        if ((hasThu || hasStart || hasEnd) && !(hasThu && hasStart && hasEnd)) {
            bindingResult.reject("schedule", "Vui long nhap day du thu, gio bat dau va gio ket thuc.");
            model.addAttribute("courses", courseRepository.findAll());
            model.addAttribute("teachers", teacherRepository.findAll());
            return "admin/lophoc/form";
        }

        if (hasThu && hasStart && hasEnd && !lopHoc.getGioBatDau().isBefore(lopHoc.getGioKetThuc())) {
            bindingResult.reject("schedule", "Gio bat dau phai nho hon gio ket thuc.");
            model.addAttribute("courses", courseRepository.findAll());
            model.addAttribute("teachers", teacherRepository.findAll());
            return "admin/lophoc/form";
        }

        if (hasThu) {
            lopHoc.setThu(String.join(",", thuList));
        } else {
            lopHoc.setThu(null);
        }

        if (hasThu && lopHoc.getGioBatDau() != null && lopHoc.getGioKetThuc() != null) {
            String lichHoc = String.join(", ", thuList) + " " + lopHoc.getGioBatDau() + "-" + lopHoc.getGioKetThuc();
            if (lopHoc.getNgayBatDau() != null && lopHoc.getNgayKetThuc() != null) {
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lichHoc = lopHoc.getNgayBatDau().format(fmt) + " - " + lopHoc.getNgayKetThuc().format(fmt)
                        + " | " + String.join(", ", thuList)
                        + " | " + lopHoc.getGioBatDau() + "-" + lopHoc.getGioKetThuc();
            }
            lopHoc.setLichHoc(lichHoc);
        }

        if (courseId != null) {
            Course course = courseRepository.findById(courseId).orElse(null);
            lopHoc.setCourse(course);
            if (course != null && (lopHoc.getHocPhi() <= 0)) {
                lopHoc.setHocPhi(course.getFee());
            }
        } else {
            lopHoc.setCourse(null);
        }

        if (teacherId != null) {
            Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
            lopHoc.setTeacher(teacher);
            if (teacher != null && teacher.getFullName() != null && !teacher.getFullName().isBlank()) {
                lopHoc.setGiaoVien(teacher.getFullName());
            }
        } else {
            lopHoc.setTeacher(null);
        }

        lopHocRepository.save(lopHoc);
        return "redirect:/admin/lophoc";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        model.addAttribute("lophoc", lopHoc);
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "admin/lophoc/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        lopHocRepository.deleteById(id);
        return "redirect:/admin/lophoc";
    }
}
