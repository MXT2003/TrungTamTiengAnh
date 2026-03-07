package com.hutech.TrungTamTiengAnh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserForm {

    private Long id;

    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 4, max = 50, message = "Username tu 4 den 50 ky tu")
    private String username;

    private String password;

    @NotBlank(message = "Role khong duoc de trong")
    private String role;

    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
