package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Payment;
import com.hutech.TrungTamTiengAnh.entity.StudentProfile;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.PaymentRepository;
import com.hutech.TrungTamTiengAnh.repository.StudentProfileRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.time.LocalDate;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentProfileRepository studentProfileRepository;
    // ==============================
    // 📌 Trang Home Student
    // ==============================
    @GetMapping("/home")
    public String home(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        return "student/home";
    }

    // ==============================
    // 📌 Xem danh sách lớp
    // ==============================
    @GetMapping("/classes")
    public String viewClasses(Model model,
                              @RequestParam(value = "error", required = false) String error) {

        List<LopHoc> list = lopHocRepository.findAll();
        model.addAttribute("list", list);

        List<String> activeStatuses = List.of("CHO_DUYET", "DA_DUYET");
        Map<Long, Long> soLuongMap = new HashMap<>();
        Map<Long, Long> conChoMap = new HashMap<>();

        for (LopHoc lh : list) {
            long count = dangKyRepository.countByLopHocIdAndTrangThaiIn(lh.getId(), activeStatuses);
            soLuongMap.put(lh.getId(), count);
            long conCho = Math.max(0, lh.getSiSo() - count);
            conChoMap.put(lh.getId(), conCho);
        }

        model.addAttribute("soLuongMap", soLuongMap);
        model.addAttribute("conChoMap", conChoMap);

        if (error != null) {
            String message = switch (error) {
                case "CLOSED" -> "Lop hien dang dong dang ky.";
                case "FULL" -> "Lop da du si so.";
                case "CONFLICT" -> "Ban bi trung lich voi lop da dang ky.";
                default -> "Khong the dang ky lop.";
            };
            model.addAttribute("error", message);
        }

        return "student/classes";
    }

    // ==============================
    // 📌 Đăng ký lớp
    // ==============================
    @GetMapping("/register/{id}")
    public String register(@PathVariable Long id,
                           HttpSession session) {

        User user = (User) session.getAttribute("user");

        // Neu chua dang nhap
        if (user == null) {
            return "redirect:/login";
        }

        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);

        // Neu lop khong ton tai
        if (lopHoc == null) {
            return "redirect:/student/classes";
        }

        if (!lopHoc.isMoDangKy()) {
            return "redirect:/student/classes?error=CLOSED";
        }

        List<String> activeStatuses = List.of("CHO_DUYET", "DA_DUYET");
        long count = dangKyRepository.countByLopHocIdAndTrangThaiIn(id, activeStatuses);
        if (count >= lopHoc.getSiSo()) {
            return "redirect:/student/classes?error=FULL";
        }

        // Kiem tra trung lich
        List<DangKy> existedList = dangKyRepository.findByStudentId(user.getId());
        for (DangKy dk : existedList) {
            if (dk.getLopHoc() != null
                    && activeStatuses.contains(dk.getTrangThai())
                    && isTrungLich(dk.getLopHoc(), lopHoc)) {
                return "redirect:/student/classes?error=CONFLICT";
            }
        }

        // Kiem tra da dang ky chua
        DangKy existed = dangKyRepository.findByStudentIdAndLopHocId(user.getId(), id);

        if (existed != null && activeStatuses.contains(existed.getTrangThai())) {
            return "redirect:/student/my-classes";
        }

        // Tao dang ky moi
        DangKy dk = new DangKy();
        dk.setStudent(user);
        dk.setLopHoc(lopHoc);

        dangKyRepository.save(dk);

        return "redirect:/student/my-classes";
    }

    // ==============================
    // 📌 Xem lớp đã đăng ký
    // ==============================
    @GetMapping("/my-classes")
    public String myClasses(Model model,
                            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<DangKy> list = dangKyRepository
                .findByStudentId(user.getId());

        model.addAttribute("list", list);

        return "student/my-classes";
    }

    @GetMapping("/payments")
    public String payments(Model model,
                           HttpSession session,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Page<Payment> pageData = paymentRepository.findByStudentId(user.getId(), PageRequest.of(page, size));
        model.addAttribute("page", pageData);
        return "student/payments";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId());
        if (profile == null) {
            profile = new StudentProfile();
            profile.setUser(user);
            profile.setFullName(user.getUsername());
        }
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "address", required = false) String address,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId());
        if (profile == null) {
            profile = new StudentProfile();
            profile.setUser(user);
            profile.setFullName(user.getUsername());
        }

        if (phone == null || phone.isBlank() || email == null || email.isBlank() || address == null || address.isBlank()) {
            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            model.addAttribute("error", "Vui long dien day du thong tin bat buoc.");
            return "student/profile";
        }

        profile.setPhone(phone);
        profile.setEmail(email);
        profile.setAddress(address);
        studentProfileRepository.save(profile);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("success", "Cap nhat thong tin thanh cong.");
        return "student/profile";
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

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("profile", studentProfileRepository.findByUserId(user.getId()));
            model.addAttribute("error", "Mật khẩu mới không khớp.");
            return "student/profile";
        }

        String result = userService.changePassword(user, currentPassword, newPassword);
        if (!"SUCCESS".equals(result)) {
            model.addAttribute("user", user);
            model.addAttribute("profile", studentProfileRepository.findByUserId(user.getId()));
            String message = switch (result) {
                case "CURRENT_REQUIRED" -> "Vui lòng nhập mật khẩu hiện tại.";
                case "NEW_INVALID" -> "Mật khẩu mới tối thiểu 6 ký tự.";
                case "CURRENT_WRONG" -> "Mật khẩu hiện tại không đúng.";
                default -> "Không thể đổi mật khẩu.";
            };
            model.addAttribute("error", message);
            return "student/profile";
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", studentProfileRepository.findByUserId(user.getId()));
        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return "student/profile";
    }

    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk != null && dk.getStudent() != null && dk.getStudent().getId().equals(user.getId())
                && "CHO_DUYET".equals(dk.getTrangThai())) {
            dk.setTrangThai("HUY");
            dk.setGhiChu("Hoc vien huy");
            dk.setNgayCapNhat(LocalDate.now());
            dangKyRepository.save(dk);
        }
        return "redirect:/student/my-classes";
    }

    private boolean isTrungLich(LopHoc a, LopHoc b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.getThu() != null && b.getThu() != null
                && a.getGioBatDau() != null && a.getGioKetThuc() != null
                && b.getGioBatDau() != null && b.getGioKetThuc() != null) {
            if (!a.getThu().equalsIgnoreCase(b.getThu())) {
                return false;
            }

            LocalTime startA = a.getGioBatDau();
            LocalTime endA = a.getGioKetThuc();
            LocalTime startB = b.getGioBatDau();
            LocalTime endB = b.getGioKetThuc();

            return startA.isBefore(endB) && startB.isBefore(endA);
        }

        if (a.getLichHoc() != null && b.getLichHoc() != null) {
            return a.getLichHoc().equalsIgnoreCase(b.getLichHoc());
        }

        return false;
    }
}









