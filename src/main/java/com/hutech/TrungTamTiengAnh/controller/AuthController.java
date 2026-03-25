package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.dto.RegisterForm;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.entity.StudentProfile;
import com.hutech.TrungTamTiengAnh.repository.StudentProfileRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final StudentProfileRepository studentProfileRepository;

    // Constructor Injection
    public AuthController(UserService userService, StudentProfileRepository studentProfileRepository) {
        this.userService = userService;
        this.studentProfileRepository = studentProfileRepository;
    }

    // ===================== LOGIN =====================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userService.login(username, password);

        if (user != null) {
            session.setAttribute("user", user);

            // Phan quyen
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/home";
            }
            if ("TEACHER".equals(user.getRole())) {
                return "redirect:/teacher/home";
            }
            return "redirect:/student/home";
        }

        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
        return "login";
    }

    // ===================== REGISTER =====================

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           org.springframework.validation.BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(form.getPassword());

        // Mac dinh tai khoan dang ky la STUDENT
        user.setRole("STUDENT");

        String result = userService.register(user);

        if ("SUCCESS".equals(result)) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            profile.setFullName(form.getFullName());
            profile.setPhone(form.getPhone());
            profile.setEmail(form.getEmail());
            profile.setDateOfBirth(form.getDateOfBirth());
            studentProfileRepository.save(profile);
            return "redirect:/login";
        }

        model.addAttribute("error", result);
        return "register";
    }

    // ===================== LOGOUT =====================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}



