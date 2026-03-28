package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Payment;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final LopHocRepository lopHocRepository;
    private final DangKyRepository dangKyRepository;

    public AdminPaymentController(PaymentRepository paymentRepository,
                                  LopHocRepository lopHocRepository,
                                  DangKyRepository dangKyRepository) {
        this.paymentRepository = paymentRepository;
        this.lopHocRepository = lopHocRepository;
        this.dangKyRepository = dangKyRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        Page<Payment> pageData;
        if (status != null && !status.isBlank()) {
            pageData = paymentRepository.findByStatus(status, PageRequest.of(safePage, safeSize));
        } else {
            pageData = paymentRepository.findAll(PageRequest.of(safePage, safeSize));
        }
        if (pageData.getTotalPages() > 0 && safePage >= pageData.getTotalPages()) {
            safePage = pageData.getTotalPages() - 1;
            if (status != null && !status.isBlank()) {
                pageData = paymentRepository.findByStatus(status, PageRequest.of(safePage, safeSize));
            } else {
                pageData = paymentRepository.findAll(PageRequest.of(safePage, safeSize));
            }
        }
        model.addAttribute("page", pageData);
        model.addAttribute("status", status);
        model.addAttribute("classes", lopHocRepository.findAll());
        model.addAttribute("sumPaid", paymentRepository.sumPaid());
        model.addAttribute("sumUnpaid", paymentRepository.sumUnpaid());
        return "admin/payment/list";
    }

    @PostMapping("/create")
    public String createByClass(@RequestParam("classId") Long classId) {
        LopHoc lopHoc = lopHocRepository.findById(classId).orElse(null);
        if (lopHoc == null) {
            return "redirect:/admin/payments";
        }
        List<String> activeStatuses = List.of("DA_DUYET");
        List<DangKy> list = dangKyRepository.findByLopHocIdAndTrangThaiIn(classId, activeStatuses);
        for (DangKy dk : list) {
            if (dk.getStudent() == null) {
                continue;
            }
            Payment existed = paymentRepository.findByStudentIdAndLopHocId(dk.getStudent().getId(), classId);
            if (existed == null) {
                Payment p = new Payment();
                p.setStudent(dk.getStudent());
                p.setLopHoc(lopHoc);
                p.setAmount(lopHoc.getHocPhi());
                p.setStatus("UNPAID");
                p.setDueDate(LocalDate.now().plusDays(7));
                paymentRepository.save(p);
            }
        }
        return "redirect:/admin/payments";
    }

    @PostMapping("/mark-paid/{id}")
    public String markPaid(@PathVariable Long id) {
        Payment p = paymentRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus("PAID");
            p.setPaidDate(LocalDate.now());
            paymentRepository.save(p);
        }
        return "redirect:/admin/payments";
    }

    @PostMapping("/mark-unpaid/{id}")
    public String markUnpaid(@PathVariable Long id) {
        // Once a payment is confirmed by admin, keep it locked.
        return "redirect:/admin/payments";
    }
}
