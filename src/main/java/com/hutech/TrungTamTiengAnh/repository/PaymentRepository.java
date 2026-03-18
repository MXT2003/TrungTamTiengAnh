package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByStudentIdAndLopHocId(Long studentId, Long lopHocId);

    Page<Payment> findByStatus(String status, Pageable pageable);

    Page<Payment> findByStudentId(Long studentId, Pageable pageable);

    Page<Payment> findByStatusOrderByDueDateAsc(String status, Pageable pageable);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    double sumPaid();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'UNPAID'")
    double sumUnpaid();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PENDING'")
    double sumPending();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'UNPAID' AND p.dueDate < :today")
    double sumOverdue(@Param("today") LocalDate today);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'UNPAID' AND p.dueDate < :today")
    long countOverdue(@Param("today") LocalDate today);
}
