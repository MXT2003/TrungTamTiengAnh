package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Page<StudentProfile> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    StudentProfile findByUserId(Long userId);
}

