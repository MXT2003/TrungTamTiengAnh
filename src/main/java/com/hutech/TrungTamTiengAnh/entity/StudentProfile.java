package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @NotBlank(message = "Ho ten khong duoc de trong")
    @Size(max = 100, message = "Ho ten toi da 100 ky tu")
    private String fullName;

    @Size(max = 20, message = "So dien thoai toi da 20 ky tu")
    private String phone;

    @Email(message = "Email khong hop le")
    @Size(max = 120, message = "Email toi da 120 ky tu")
    private String email;

    private LocalDate dateOfBirth;

    @Size(max = 50, message = "Trinh do toi da 50 ky tu")
    private String level;

    @Size(max = 255, message = "Dia chi toi da 255 ky tu")
    private String address;

    @Size(max = 255, message = "Ghi chu toi da 255 ky tu")
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

