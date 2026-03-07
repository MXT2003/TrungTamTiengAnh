package com.hutech.TrungTamTiengAnh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Trang chủ
    @GetMapping("/")
    public String index() {
        return "index";
    }
}