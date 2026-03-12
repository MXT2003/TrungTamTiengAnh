package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Course;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.repository.CourseRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final CourseRepository courseRepository;
    private final LopHocRepository lopHocRepository;

    public HomeController(CourseRepository courseRepository, LopHocRepository lopHocRepository) {
        this.courseRepository = courseRepository;
        this.lopHocRepository = lopHocRepository;
    }

    // Trang chu
    @GetMapping("/")
    public String index(Model model) {
        Page<Course> featured = courseRepository.findAll(PageRequest.of(0, 3));
        model.addAttribute("featuredCourses", featured.getContent());
        return "index";
    }

    @GetMapping("/courses")
    public String courses(Model model,
                          @RequestParam(value = "q", required = false) String q,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "9") int size) {
        Page<Course> pageData;
        if (q != null && !q.isBlank()) {
            pageData = courseRepository.findByNameContainingIgnoreCase(q, PageRequest.of(page, size));
        } else {
            pageData = courseRepository.findAll(PageRequest.of(page, size));
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "public/courses";
    }

    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            return "redirect:/courses";
        }
        List<LopHoc> classes = lopHocRepository.findByCourseIdOrderByNgayBatDauAsc(id);
        model.addAttribute("course", course);
        model.addAttribute("classes", classes);
        return "public/course-detail";
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/teachers")
    public String teachers() {
        return "public/teachers";
    }

    @GetMapping("/facilities")
    public String facilities() {
        return "public/facilities";
    }

    @GetMapping("/careers")
    public String careers() {
        return "public/careers";
    }

    @GetMapping("/help")
    public String help() {
        return "public/help";
    }

    @GetMapping("/tuition-policy")
    public String tuitionPolicy() {
        return "public/tuition-policy";
    }

    @GetMapping("/rules")
    public String rules() {
        return "public/rules";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }
}
