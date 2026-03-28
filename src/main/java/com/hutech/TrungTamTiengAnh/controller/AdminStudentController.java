package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.dto.StudentForm;
import com.hutech.TrungTamTiengAnh.entity.StudentProfile;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.StudentProfileRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public AdminStudentController(StudentProfileRepository studentProfileRepository,
                                  UserRepository userRepository,
                                  UserService userService) {
        this.studentProfileRepository = studentProfileRepository;
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
        Page<StudentProfile> pageData;
        if (q != null && !q.isBlank()) {
            pageData = studentProfileRepository.findByFullNameContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
        } else {
            pageData = studentProfileRepository.findAll(PageRequest.of(safePage, safeSize));
        }
        if (pageData.getTotalPages() > 0 && safePage >= pageData.getTotalPages()) {
            safePage = pageData.getTotalPages() - 1;
            if (q != null && !q.isBlank()) {
                pageData = studentProfileRepository.findByFullNameContainingIgnoreCase(q, PageRequest.of(safePage, safeSize));
            } else {
                pageData = studentProfileRepository.findAll(PageRequest.of(safePage, safeSize));
            }
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "admin/student/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "admin/student/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        StudentProfile profile = studentProfileRepository.findById(id).orElse(null);
        if (profile == null) {
            return "redirect:/admin/students";
        }
        StudentForm form = new StudentForm();
        form.setId(profile.getId());
        if (profile.getUser() != null) {
            form.setUserId(profile.getUser().getId());
            form.setUsername(profile.getUser().getUsername());
            form.setActive(profile.getUser().isActive());
        }
        form.setFullName(profile.getFullName());
        form.setPhone(profile.getPhone());
        form.setEmail(profile.getEmail());
        form.setDateOfBirth(profile.getDateOfBirth());
        form.setLevel(profile.getLevel());
        form.setAddress(profile.getAddress());
        form.setNote(profile.getNote());
        model.addAttribute("studentForm", form);
        return "admin/student/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("studentForm") StudentForm form,
                       org.springframework.validation.BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/student/form";
        }

        if (form.getId() == null && (form.getPassword() == null || form.getPassword().isBlank())) {
            bindingResult.reject("password", "Mat khau khong duoc de trong");
            return "admin/student/form";
        }

        StudentProfile profile;
        if (form.getId() == null) {
            String result = userService.createUser(form.getUsername(), form.getPassword(), "STUDENT", form.isActive());
            if (!"SUCCESS".equals(result)) {
                model.addAttribute("error", result);
                return "admin/student/form";
            }
            User user = userRepository.findByUsername(form.getUsername());
            profile = new StudentProfile();
            profile.setUser(user);
        } else {
            profile = studentProfileRepository.findById(form.getId()).orElse(null);
            if (profile == null || profile.getUser() == null) {
                return "redirect:/admin/students";
            }
            String result = userService.updateUser(profile.getUser().getId(), form.getUsername(), form.getPassword(), "STUDENT", form.isActive());
            if (!"SUCCESS".equals(result)) {
                model.addAttribute("error", result);
                return "admin/student/form";
            }
        }

        profile.setFullName(form.getFullName());
        profile.setPhone(form.getPhone());
        profile.setEmail(form.getEmail());
        profile.setDateOfBirth(form.getDateOfBirth());
        profile.setLevel(form.getLevel());
        profile.setAddress(form.getAddress());
        profile.setNote(form.getNote());

        studentProfileRepository.save(profile);
        return "redirect:/admin/students";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        StudentProfile profile = studentProfileRepository.findById(id).orElse(null);
        if (profile != null && profile.getUser() != null) {
            User user = profile.getUser();
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
        return "redirect:/admin/students";
    }
}
