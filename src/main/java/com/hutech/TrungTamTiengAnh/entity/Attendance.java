package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LopHoc lopHoc;

    @ManyToOne
    private User student;

    private LocalDate ngayHoc;

    private String status = "PRESENT";

    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LocalDate getNgayHoc() { return ngayHoc; }
    public void setNgayHoc(LocalDate ngayHoc) { this.ngayHoc = ngayHoc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

