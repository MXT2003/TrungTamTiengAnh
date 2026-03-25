package com.hutech.TrungTamTiengAnh.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class StudentForm {

    private Long id;
    private Long userId;

    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 4, max = 50, message = "Username tu 4 den 50 ky tu")
    private String username;

    @Size(min = 6, max = 100, message = "Mat khau toi thieu 6 ky tu")
    private String password;

    private boolean active = true;

    @NotBlank(message = "Ho ten khong duoc de trong")
    @Size(max = 100, message = "Ho ten toi da 100 ky tu")
    private String fullName;

    @NotBlank(message = "So dien thoai khong duoc de trong")
    @Size(max = 20, message = "So dien thoai toi da 20 ky tu")
    private String phone;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    @Size(max = 120, message = "Email toi da 120 ky tu")
    private String email;

    @NotNull(message = "Ngay sinh khong duoc de trong")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Trinh do khong duoc de trong")
    @Size(max = 50, message = "Trinh do toi da 50 ky tu")
    private String level;

    @NotBlank(message = "Dia chi khong duoc de trong")
    @Size(max = 255, message = "Dia chi toi da 255 ky tu")
    private String address;

    @Size(max = 255, message = "Ghi chu toi da 255 ky tu")
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

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
