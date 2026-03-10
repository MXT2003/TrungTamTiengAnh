package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByUserId(Long userId);
    Page<Teacher> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
