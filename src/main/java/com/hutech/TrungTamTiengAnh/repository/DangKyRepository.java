package com.hutech.TrungTamTiengAnh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hutech.TrungTamTiengAnh.entity.DangKy;

import java.util.List;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {

    List<DangKy> findByStudentId(Long studentId);

    DangKy findByStudentIdAndLopHocId(Long studentId, Long lopHocId);

    long countByLopHocId(Long lopHocId);

    long countByLopHocIdAndTrangThaiIn(Long lopHocId, List<String> trangThai);

    List<DangKy> findByLopHocIdAndTrangThaiIn(Long lopHocId, List<String> trangThai);

    Page<DangKy> findByTrangThai(String trangThai, Pageable pageable);

    Page<DangKy> findByTrangThaiOrderByNgayDangKyDesc(String trangThai, Pageable pageable);

    long count();

    @Query("""
        SELECT MONTH(d.ngayDangKy), COUNT(d)
        FROM DangKy d
        GROUP BY MONTH(d.ngayDangKy)
        ORDER BY MONTH(d.ngayDangKy)
    """)
    List<Object[]> thongKeTheoThang();

    @Query("""
        SELECT d.trangThai, COUNT(d)
        FROM DangKy d
        GROUP BY d.trangThai
    """)
    List<Object[]> thongKeTheoTrangThai();
}
