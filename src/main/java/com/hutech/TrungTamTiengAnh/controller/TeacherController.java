package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.*;
import com.hutech.TrungTamTiengAnh.repository.AttendanceRepository;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.ScoreRepository;
import com.hutech.TrungTamTiengAnh.repository.StudentProfileRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final LopHocRepository lopHocRepository;
    private final DangKyRepository dangKyRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final StudentProfileRepository studentProfileRepository;

    public TeacherController(TeacherRepository teacherRepository,
                             LopHocRepository lopHocRepository,
                             DangKyRepository dangKyRepository,
                             AttendanceRepository attendanceRepository,
                             ScoreRepository scoreRepository,
                             UserRepository userRepository,
                             UserService userService,
                             StudentProfileRepository studentProfileRepository) {
        this.teacherRepository = teacherRepository;
        this.lopHocRepository = lopHocRepository;
        this.dangKyRepository = dangKyRepository;
        this.attendanceRepository = attendanceRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.studentProfileRepository = studentProfileRepository;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Teacher teacher = teacherRepository.findByUserId(user.getId());
        if (teacher == null) {
            model.addAttribute("classCount", 0);
            model.addAttribute("studentCount", 0);
            return "teacher/home";
        }
        List<LopHoc> classes = lopHocRepository.findAll().stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacher.getId()))
                .toList();
        model.addAttribute("classCount", classes.size());
        long students = 0;
        List<String> active = List.of("DA_DUYET");
        for (LopHoc c : classes) {
            students += dangKyRepository.countByLopHocIdAndTrangThaiIn(c.getId(), active);
        }
        model.addAttribute("studentCount", students);
        return "teacher/home";
    }

    @GetMapping("/classes")
    public String classes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Teacher teacher = teacherRepository.findByUserId(user.getId());
        if (teacher == null) {
            model.addAttribute("classes", List.of());
            return "teacher/classes";
        }
        List<LopHoc> classes = lopHocRepository.findAll().stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacher.getId()))
                .toList();
        model.addAttribute("classes", classes);
        return "teacher/classes";
    }

    @GetMapping("/class/{id}/students")
    public String students(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/teacher/classes";
        }
        List<String> activeStatuses = List.of("DA_DUYET");
        List<DangKy> list = dangKyRepository.findByLopHocIdAndTrangThaiIn(id, activeStatuses);
        java.util.Map<Long, StudentProfile> profileMap = new java.util.HashMap<>();
        for (DangKy dk : list) {
            if (dk.getStudent() != null) {
                profileMap.put(dk.getStudent().getId(),
                        studentProfileRepository.findByUserId(dk.getStudent().getId()));
            }
        }
        model.addAttribute("profileMap", profileMap);
        model.addAttribute("lopHoc", lopHoc);
        model.addAttribute("list", list);
        return "teacher/students";
    }

    @GetMapping("/class/{id}/attendance")
    public String attendance(@PathVariable Long id,
                             @RequestParam(value = "date", required = false) String date,
                             HttpSession session,
                             Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/teacher/classes";
        }
        LocalDate ngay = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        List<String> activeStatuses = List.of("DA_DUYET");
        List<DangKy> list = dangKyRepository.findByLopHocIdAndTrangThaiIn(id, activeStatuses);
        model.addAttribute("lopHoc", lopHoc);
        model.addAttribute("ngay", ngay);
        model.addAttribute("list", list);
        return "teacher/attendance";
    }

    @PostMapping("/class/{id}/attendance")
    public String saveAttendance(@PathVariable Long id,
                                 @RequestParam("date") String date,
                                 @RequestParam("studentId") List<Long> studentIds,
                                 @RequestParam("status") List<String> statuses,
                                 @RequestParam("note") List<String> notes) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/teacher/classes";
        }
        LocalDate ngay = LocalDate.parse(date);
        for (int i = 0; i < studentIds.size(); i++) {
            Long studentId = studentIds.get(i);
            String status = statuses.size() > i ? statuses.get(i) : "PRESENT";
            String note = notes.size() > i ? notes.get(i) : null;
            Attendance existed = attendanceRepository.findByLopHocIdAndNgayHocAndStudentId(id, ngay, studentId);
            if (existed == null) {
                Attendance a = new Attendance();
                a.setLopHoc(lopHoc);
                User student = userRepository.findById(studentId).orElse(null);
                if (student == null) {
                    continue;
                }
                a.setStudent(student);
                a.setNgayHoc(ngay);
                a.setStatus(status);
                a.setNote(note);
                attendanceRepository.save(a);
            } else {
                existed.setStatus(status);
                existed.setNote(note);
                attendanceRepository.save(existed);
            }
        }
        return "redirect:/teacher/class/" + id + "/attendance?date=" + date;
    }

    @GetMapping("/class/{id}/scores")
    public String scores(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/teacher/classes";
        }
        List<String> activeStatuses = List.of("DA_DUYET");
        List<DangKy> list = dangKyRepository.findByLopHocIdAndTrangThaiIn(id, activeStatuses);
        model.addAttribute("lopHoc", lopHoc);
        model.addAttribute("list", list);
        return "teacher/scores";
    }

    @PostMapping("/class/{id}/scores")
    public String saveScores(@PathVariable Long id,
                             @RequestParam("studentId") List<Long> studentIds,
                             @RequestParam("score") List<String> scores,
                             @RequestParam("comment") List<String> comments) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) {
            return "redirect:/teacher/classes";
        }
        for (int i = 0; i < studentIds.size(); i++) {
            Long studentId = studentIds.get(i);
            String scoreStr = scores.size() > i ? scores.get(i) : null;
            String comment = comments.size() > i ? comments.get(i) : null;
            Double scoreVal = null;
            if (scoreStr != null && !scoreStr.isBlank()) {
                try {
                    scoreVal = Double.parseDouble(scoreStr);
                } catch (NumberFormatException ignored) {
                }
            }
            Score existed = scoreRepository.findByLopHocIdAndStudentId(id, studentId);
            if (existed == null) {
                Score s = new Score();
                s.setLopHoc(lopHoc);
                User student = userRepository.findById(studentId).orElse(null);
                if (student == null) {
                    continue;
                }
                s.setStudent(student);
                s.setScore(scoreVal);
                s.setComment(comment);
                scoreRepository.save(s);
            } else {
                existed.setScore(scoreVal);
                existed.setComment(comment);
                scoreRepository.save(existed);
            }
        }
        return "redirect:/teacher/class/" + id + "/scores";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Teacher teacher = teacherRepository.findByUserId(user.getId());
        if (teacher == null) {
            teacher = new Teacher();
            teacher.setUser(user);
            teacher.setFullName(user.getUsername());
        }
        model.addAttribute("user", user);
        model.addAttribute("teacher", teacher);
        return "teacher/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "email", required = false) String email,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Teacher teacher = teacherRepository.findByUserId(user.getId());
        if (teacher == null) {
            teacher = new Teacher();
            teacher.setUser(user);
            teacher.setFullName(user.getUsername());
        }
        if (phone == null || phone.isBlank() || email == null || email.isBlank()) {
            model.addAttribute("user", user);
            model.addAttribute("teacher", teacher);
            model.addAttribute("error", "Vui long dien day du thong tin bat buoc.");
            return "teacher/profile";
        }
        teacher.setPhone(phone);
        teacher.setEmail(email);
        teacherRepository.save(teacher);

        model.addAttribute("user", user);
        model.addAttribute("teacher", teacher);
        model.addAttribute("success", "Cap nhat thong tin thanh cong.");
        return "teacher/profile";
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

        Teacher teacher = teacherRepository.findByUserId(user.getId());
        if (teacher == null) {
            teacher = new Teacher();
            teacher.setUser(user);
            teacher.setFullName(user.getUsername());
        }

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("teacher", teacher);
            model.addAttribute("error", "Mat khau moi khong khop.");
            return "teacher/profile";
        }

        String result = userService.changePassword(user, currentPassword, newPassword);
        if (!"SUCCESS".equals(result)) {
            model.addAttribute("user", user);
            model.addAttribute("teacher", teacher);
            String message = switch (result) {
                case "CURRENT_REQUIRED" -> "Vui long nhap mat khau hien tai.";
                case "NEW_INVALID" -> "Mat khau moi toi thieu 6 ky tu.";
                case "CURRENT_WRONG" -> "Mat khau hien tai khong dung.";
                default -> "Khong the doi mat khau.";
            };
            model.addAttribute("error", message);
            return "teacher/profile";
        }

        session.setAttribute("user", user);
        model.addAttribute("user", user);
        model.addAttribute("teacher", teacher);
        model.addAttribute("success", "Doi mat khau thanh cong.");
        return "teacher/profile";
    }
}
