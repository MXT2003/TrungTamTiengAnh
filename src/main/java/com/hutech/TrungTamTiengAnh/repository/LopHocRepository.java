package com.hutech.TrungTamTiengAnh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LopHocRepository extends JpaRepository<LopHoc, Long> {
    Page<LopHoc> findByTenLopContainingIgnoreCase(String tenLop, Pageable pageable);

    java.util.List<LopHoc> findByCourseIdOrderByNgayBatDauAsc(Long courseId);

    java.util.List<LopHoc> findAllByOrderByNgayBatDauAsc();
}
