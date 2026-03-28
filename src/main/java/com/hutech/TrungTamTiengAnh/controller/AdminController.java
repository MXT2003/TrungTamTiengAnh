package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Payment;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.PaymentRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        LocalDate today = LocalDate.now();

        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", userRepository.countByRole("TEACHER"));
        model.addAttribute("tongThu", paymentRepository.sumPaid());
        model.addAttribute("tongNo", paymentRepository.sumUnpaid());

        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();
        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();

        for (Object[] obj : thongKeThang) {
            thangList.add((Integer) obj[0]);
            soLuongList.add((Long) obj[1]);
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();
        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();

        for (Object[] obj : thongKeTrangThai) {
            trangThaiList.add((String) obj[0]);
            soLuongTrangThai.add((Long) obj[1]);
        }

        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        Map<YearMonth, Double> revenueByMonthMap = new LinkedHashMap<>();
        for (Object[] row : paymentRepository.revenueByMonth(today.getYear())) {
            Integer month = (Integer) row[0];
            Number amount = (Number) row[1];
            revenueByMonthMap.put(YearMonth.of(today.getYear(), month), amount.doubleValue());
        }

        int previousYear = today.minusMonths(5).getYear();
        if (previousYear != today.getYear()) {
            for (Object[] row : paymentRepository.revenueByMonth(previousYear)) {
                Integer month = (Integer) row[0];
                Number amount = (Number) row[1];
                revenueByMonthMap.put(YearMonth.of(previousYear, month), amount.doubleValue());
            }
        }

        List<String> revenueMonthLabels = new ArrayList<>();
        List<Double> revenueLast6Months = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(today.minusMonths(i));
            revenueMonthLabels.add("Th " + month.getMonthValue());
            revenueLast6Months.add(revenueByMonthMap.getOrDefault(month, 0.0));
        }

        model.addAttribute("revenueMonthLabels", revenueMonthLabels);
        model.addAttribute("revenueLast6Months", revenueLast6Months);
        model.addAttribute("revenueLast6MonthHeights", buildBarHeights(revenueLast6Months));

        return "admin/home";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        int currentYear = LocalDate.now().getYear();
        LocalDate today = LocalDate.now();
        List<Payment> payments = paymentRepository.findAll();

        double tongThu = paymentRepository.sumPaid();
        double tongNo = paymentRepository.sumUnpaid();

        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", userRepository.countByRole("TEACHER"));
        model.addAttribute("tongThu", tongThu);
        model.addAttribute("tongNo", tongNo);
        model.addAttribute("currentYear", currentYear);

        List<Integer> thangList = new ArrayList<>();
        List<Double> doanhThuTheoThang = new ArrayList<>();
        List<Double> congNoTheoThang = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            thangList.add(month);
            doanhThuTheoThang.add(0.0);
            congNoTheoThang.add(0.0);
        }

        long daNopCount = 0;
        long chuaNopCount = 0;
        long quaHanCount = 0;
        double tongQuaHan = 0;
        Map<String, Double> doanhThuTheoLop = new LinkedHashMap<>();

        for (Payment payment : payments) {
            if ("PAID".equals(payment.getStatus())) {
                daNopCount++;
                if (payment.getPaidDate() != null && payment.getPaidDate().getYear() == currentYear) {
                    int monthIndex = payment.getPaidDate().getMonthValue() - 1;
                    doanhThuTheoThang.set(monthIndex, doanhThuTheoThang.get(monthIndex) + payment.getAmount());
                }
                if (payment.getLopHoc() != null && payment.getLopHoc().getTenLop() != null) {
                    String tenLop = payment.getLopHoc().getTenLop();
                    doanhThuTheoLop.put(tenLop, doanhThuTheoLop.getOrDefault(tenLop, 0.0) + payment.getAmount());
                }
            }

            if ("UNPAID".equals(payment.getStatus())) {
                chuaNopCount++;
                if (payment.getDueDate() != null && payment.getDueDate().getYear() == currentYear) {
                    int monthIndex = payment.getDueDate().getMonthValue() - 1;
                    congNoTheoThang.set(monthIndex, congNoTheoThang.get(monthIndex) + payment.getAmount());
                }
                if (payment.getDueDate() != null && payment.getDueDate().isBefore(today)) {
                    quaHanCount++;
                    tongQuaHan += payment.getAmount();
                }
            }
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("doanhThuTheoThang", doanhThuTheoThang);
        model.addAttribute("congNoTheoThang", congNoTheoThang);
        model.addAttribute("doanhThuChieuCao", buildBarHeights(doanhThuTheoThang, tongThu));
        model.addAttribute("congNoChieuCao", buildBarHeights(congNoTheoThang, tongNo));

        List<String> trangThaiThanhToan = List.of("Đã nộp", "Chưa nộp", "Quá hạn");
        List<Long> soLuongThanhToan = List.of(daNopCount, chuaNopCount, quaHanCount);
        model.addAttribute("trangThaiThanhToan", trangThaiThanhToan);
        model.addAttribute("soLuongThanhToan", soLuongThanhToan);
        model.addAttribute("tongQuaHan", tongQuaHan);

        List<Map<String, Object>> doanhThuTheoLopList = new ArrayList<>();
        doanhThuTheoLop.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(6)
                .forEach(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("tenLop", entry.getKey());
                    row.put("doanhThu", entry.getValue());
                    doanhThuTheoLopList.add(row);
                });
        model.addAttribute("doanhThuTheoLopList", doanhThuTheoLopList);

        return "admin/reports";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("classes", lopHocRepository.findAllByOrderByNgayBatDauAsc());
        return "admin/schedule";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("pendingRegistrations",
                dangKyRepository.findByTrangThaiOrderByNgayDangKyDesc("CHO_DUYET", PageRequest.of(0, 10)));
        model.addAttribute("unpaidPayments",
                paymentRepository.findByStatusOrderByDueDateAsc("UNPAID", PageRequest.of(0, 10)));
        model.addAttribute("today", LocalDate.now());
        return "admin/notifications";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", userRepository.findById(user.getId()).orElse(user));
        return "admin/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "phone", required = false) String phone,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        User current = userRepository.findById(user.getId()).orElse(null);
        if (current == null) {
            return "redirect:/login";
        }
        if (email == null || email.isBlank() || phone == null || phone.isBlank()) {
            model.addAttribute("user", current);
            model.addAttribute("error", "Vui long dien day du thong tin bat buoc.");
            return "admin/profile";
        }
        current.setEmail(email);
        current.setPhone(phone);
        userRepository.save(current);
        session.setAttribute("user", current);
        model.addAttribute("user", current);
        model.addAttribute("success", "Cap nhat thong tin thanh cong.");
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(HttpSession session,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        User current = userRepository.findById(user.getId()).orElse(null);
        if (current == null) {
            return "redirect:/login";
        }

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            model.addAttribute("user", current);
            model.addAttribute("error", "Mat khau moi khong khop.");
            return "admin/profile";
        }

        String result = userService.changePassword(current, currentPassword, newPassword);
        if (!"SUCCESS".equals(result)) {
            String message = switch (result) {
                case "CURRENT_REQUIRED" -> "Vui long nhap mat khau hien tai.";
                case "NEW_INVALID" -> "Mat khau moi toi thieu 6 ky tu.";
                case "CURRENT_WRONG" -> "Mat khau hien tai khong dung.";
                default -> "Khong the doi mat khau.";
            };
            model.addAttribute("user", current);
            model.addAttribute("error", message);
            return "admin/profile";
        }

        session.setAttribute("user", current);
        model.addAttribute("user", current);
        model.addAttribute("success", "Doi mat khau thanh cong.");
        return "admin/profile";
    }

    private List<Integer> buildBarHeights(List<Double> values, double total) {
        List<Integer> heights = new ArrayList<>();
        double safeTotal = total > 0 ? total : 1;
        for (Double value : values) {
            if (value == null || value <= 0) {
                heights.add(20);
            } else {
                heights.add((int) Math.round((value / safeTotal) * 180 + 20));
            }
        }
        return heights;
    }

    private List<Integer> buildBarHeights(List<Double> values) {
        List<Integer> heights = new ArrayList<>();
        double max = 0;
        for (Double value : values) {
            if (value != null && value > max) {
                max = value;
            }
        }

        double safeMax = max > 0 ? max : 1;
        for (Double value : values) {
            if (value == null || value <= 0) {
                heights.add(20);
            } else {
                heights.add((int) Math.round((value / safeMax) * 180 + 20));
            }
        }
        return heights;
    }
}
