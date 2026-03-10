package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.*;
import com.hutech.TrungTamTiengAnh.repository.AttendanceRepository;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.ScoreRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
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

    public TeacherController(TeacherRepository teacherRepository,
                             LopHocRepository lopHocRepository,
                             DangKyRepository dangKyRepository,
                             AttendanceRepository attendanceRepository,
                             ScoreRepository scoreRepository,
                             UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.lopHocRepository = lopHocRepository;
        this.dangKyRepository = dangKyRepository;
        this.attendanceRepository = attendanceRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
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
}
