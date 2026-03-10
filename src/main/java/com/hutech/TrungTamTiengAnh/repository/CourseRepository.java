package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

