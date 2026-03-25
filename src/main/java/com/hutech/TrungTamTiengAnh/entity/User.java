package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 4, max = 50, message = "Username tu 4 den 50 ky tu")
    private String username;

    @NotBlank(message = "Mat khau khong duoc de trong")
    @Size(min = 6, max = 100, message = "Mat khau toi thieu 6 ky tu")
    private String password;

    @Size(max = 120, message = "Email toi da 120 ky tu")
    private String email;

    @Size(max = 20, message = "So dien thoai toi da 20 ky tu")
    private String phone;

    private String role;

    @Builder.Default
    private boolean active = true;
}
