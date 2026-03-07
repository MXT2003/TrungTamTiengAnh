package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student;

    @ManyToOne
    private LopHoc lopHoc;

    private String trangThai = "CHO_DUYET";

    private LocalDate ngayDangKy = LocalDate.now();

    private LocalDate ngayCapNhat;
    private String ghiChu;

    public Long getId() { return id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }

    public LocalDate getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDate ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
