package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.dto.TeacherForm;
import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/teacher")
public class AdminTeacherController {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public AdminTeacherController(TeacherRepository teacherRepository,
                                  UserRepository userRepository,
                                  UserService userService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        Page<Teacher> pageData;
        if (q != null && !q.isBlank()) {
            pageData = teacherRepository.findByFullNameContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
        } else {
            pageData = teacherRepository.findAll(PageRequest.of(safePage, safeSize));
        }
        if (pageData.getTotalPages() > 0 && safePage >= pageData.getTotalPages()) {
            safePage = pageData.getTotalPages() - 1;
            if (q != null && !q.isBlank()) {
                pageData = teacherRepository.findByFullNameContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
            } else {
                pageData = teacherRepository.findAll(PageRequest.of(safePage, safeSize));
            }
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "admin/teacher/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("teacherForm", new TeacherForm());
        return "admin/teacher/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher == null) {
            return "redirect:/admin/teacher";
        }
        TeacherForm form = new TeacherForm();
        form.setId(teacher.getId());
        form.setFullName(teacher.getFullName());
        form.setPhone(teacher.getPhone());
        form.setEmail(teacher.getEmail());
        form.setSpecialization(teacher.getSpecialization());
        if (teacher.getUser() != null) {
            form.setUsername(teacher.getUser().getUsername());
            form.setActive(teacher.getUser().isActive());
        }
        model.addAttribute("teacherForm", form);
        return "admin/teacher/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("teacherForm") TeacherForm form,
                       org.springframework.validation.BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/teacher/form";
        }

        if (form.getId() == null && (form.getPassword() == null || form.getPassword().isBlank())) {
            bindingResult.reject("password", "Mat khau khong duoc de trong");
            return "admin/teacher/form";
        }

        Teacher teacher;
        if (form.getId() == null) {
            String result = userService.createUser(form.getUsername(), form.getPassword(), "TEACHER", form.isActive());
            if (!"SUCCESS".equals(result)) {
                model.addAttribute("error", result);
                return "admin/teacher/form";
            }
            User user = userRepository.findByUsername(form.getUsername());
            teacher = new Teacher();
            teacher.setUser(user);
        } else {
            teacher = teacherRepository.findById(form.getId()).orElse(null);
            if (teacher == null || teacher.getUser() == null) {
                return "redirect:/admin/teacher";
            }
            String result = userService.updateUser(teacher.getUser().getId(), form.getUsername(), form.getPassword(), "TEACHER", form.isActive());
            if (!"SUCCESS".equals(result)) {
                model.addAttribute("error", result);
                return "admin/teacher/form";
            }
        }

        teacher.setFullName(form.getFullName());
        teacher.setPhone(form.getPhone());
        teacher.setEmail(form.getEmail());
        teacher.setSpecialization(form.getSpecialization());
        teacherRepository.save(teacher);
        return "redirect:/admin/teacher";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher != null) {
            if (teacher.getUser() != null) {
                User user = teacher.getUser();
                user.setActive(false);
                userRepository.save(user);
            }
            teacherRepository.delete(teacher);
        }
        return "redirect:/admin/teacher";
    }
}
