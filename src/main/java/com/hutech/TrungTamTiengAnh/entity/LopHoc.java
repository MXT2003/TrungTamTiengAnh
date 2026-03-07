package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

@Entity
public class LopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 100, message = "Tên lớp tối đa 100 ký tự")
    private String tenLop;

    @NotBlank(message = "Giáo viên không được để trống")
    @Size(max = 100, message = "Tên giáo viên tối đa 100 ký tự")
    private String giaoVien;

    @NotBlank(message = "Lịch học không được để trống")
    @Size(max = 100, message = "Lịch học tối đa 100 ký tự")
    private String lichHoc;

    @Min(value = 1, message = "Sĩ số tối đa phải >= 1")
    private int siSo;

    @PositiveOrZero(message = "Học phí phải >= 0")
    private double hocPhi;

    private boolean moDangKy = true;

    private String thu;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    @ManyToOne
    private Teacher teacher;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getGiaoVien() { return giaoVien; }
    public void setGiaoVien(String giaoVien) { this.giaoVien = giaoVien; }

    public String getLichHoc() { return lichHoc; }
    public void setLichHoc(String lichHoc) { this.lichHoc = lichHoc; }

    public int getSiSo() { return siSo; }
    public void setSiSo(int siSo) { this.siSo = siSo; }

    public double getHocPhi() { return hocPhi; }
    public void setHocPhi(double hocPhi) { this.hocPhi = hocPhi; }

    public boolean isMoDangKy() { return moDangKy; }
    public void setMoDangKy(boolean moDangKy) { this.moDangKy = moDangKy; }

    public String getThu() { return thu; }
    public void setThu(String thu) { this.thu = thu; }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}



