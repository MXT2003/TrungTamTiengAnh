package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Score findByLopHocIdAndStudentId(Long lopHocId, Long studentId);
}

