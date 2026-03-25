package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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

    @Query("""
        SELECT MONTH(p.paidDate), COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'PAID'
          AND p.paidDate IS NOT NULL
          AND YEAR(p.paidDate) = :year
        GROUP BY MONTH(p.paidDate)
        ORDER BY MONTH(p.paidDate)
    """)
    List<Object[]> revenueByMonth(@Param("year") int year);

    @Query("""
        SELECT MONTH(p.dueDate), COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'UNPAID'
          AND p.dueDate IS NOT NULL
          AND YEAR(p.dueDate) = :year
        GROUP BY MONTH(p.dueDate)
        ORDER BY MONTH(p.dueDate)
    """)
    List<Object[]> unpaidByMonth(@Param("year") int year);

    @Query("""
        SELECT p.lopHoc.tenLop, COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = 'PAID'
          AND p.lopHoc IS NOT NULL
        GROUP BY p.lopHoc.id, p.lopHoc.tenLop
        ORDER BY COALESCE(SUM(p.amount), 0) DESC
    """)
    List<Object[]> revenueByClass();
}
