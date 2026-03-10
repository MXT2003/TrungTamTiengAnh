package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByStudentIdAndLopHocId(Long studentId, Long lopHocId);

    Page<Payment> findByStatus(String status, Pageable pageable);

    Page<Payment> findByStudentId(Long studentId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    double sumPaid();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'UNPAID'")
    double sumUnpaid();
}
