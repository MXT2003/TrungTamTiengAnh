package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Course;
import com.hutech.TrungTamTiengAnh.repository.CourseRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/course")
public class AdminCourseController {

    private final CourseRepository courseRepository;

    public AdminCourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Course> pageData;
        if (q != null && !q.isBlank()) {
            pageData = courseRepository.findByNameContainingIgnoreCase(q, PageRequest.of(page, size));
        } else {
            pageData = courseRepository.findAll(PageRequest.of(page, size));
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "admin/course/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("course", new Course());
        return "admin/course/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElse(null);
        model.addAttribute("course", course);
        return "admin/course/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("course") Course course,
                       org.springframework.validation.BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/course/form";
        }
        courseRepository.save(course);
        return "redirect:/admin/course";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/course";
    }
}

