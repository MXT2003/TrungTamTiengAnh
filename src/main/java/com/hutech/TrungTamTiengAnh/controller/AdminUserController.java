package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.dto.UserForm;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminUserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> pageData;
        if (q != null && !q.isBlank()) {
            pageData = userRepository.findByUsernameContainingIgnoreCase(q, PageRequest.of(page, size));
        } else {
            pageData = userRepository.findAll(PageRequest.of(page, size));
        }
        model.addAttribute("page", pageData);
        model.addAttribute("q", q);
        return "admin/user/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "admin/user/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        form.setActive(user.isActive());
        model.addAttribute("userForm", form);
        return "admin/user/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("userForm") UserForm form,
                       org.springframework.validation.BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/user/form";
        }

        if (form.getId() == null && (form.getPassword() == null || form.getPassword().isBlank())) {
            bindingResult.reject("password", "Mat khau khong duoc de trong");
            return "admin/user/form";
        }

        String result;
        if (form.getId() == null) {
            result = userService.createUser(form.getUsername(), form.getPassword(), form.getRole(), form.isActive());
        } else {
            result = userService.updateUser(form.getId(), form.getUsername(), form.getPassword(), form.getRole(), form.isActive());
        }

        if (!"SUCCESS".equals(result)) {
            model.addAttribute("error", result);
            return "admin/user/form";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }
}
