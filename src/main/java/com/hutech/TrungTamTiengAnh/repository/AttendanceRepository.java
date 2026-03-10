package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByLopHocIdAndNgayHoc(Long lopHocId, LocalDate ngayHoc);
    Attendance findByLopHocIdAndNgayHocAndStudentId(Long lopHocId, LocalDate ngayHoc, Long studentId);
}

