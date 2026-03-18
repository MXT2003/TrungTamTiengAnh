package com.hutech.TrungTamTiengAnh.service;

import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(User user) {

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "SUCCESS";
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null;
        }
        if (!user.isActive()) {
            return null;
        }
        String role = user.getRole();
        if (role != null) {
            role = role.trim().toUpperCase();
        }
        if (role == null || (!role.equals("ADMIN") && !role.equals("STUDENT") && !role.equals("TEACHER"))) {
            return null;
        }
        if (!role.equals(user.getRole())) {
            user.setRole(role);
            userRepository.save(user);
        }

        String stored = user.getPassword();

        if (stored != null && isBcryptHash(stored)) {
            return passwordEncoder.matches(password, stored) ? user : null;
        }

        if (stored != null && stored.equals(password)) {
            // Upgrade legacy plaintext password to bcrypt
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return user;
        }

        return null;
    }

    public String createUser(String username, String password, String role, boolean active) {
        if (userRepository.findByUsername(username) != null) {
            return "Username already exists!";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(active);
        userRepository.save(user);
        return "SUCCESS";
    }

    public String updateUser(Long id, String username, String password, String role, boolean active) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "User not found!";
        }

        User existed = userRepository.findByUsername(username);
        if (existed != null && !existed.getId().equals(id)) {
            return "Username already exists!";
        }

        user.setUsername(username);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setRole(role);
        user.setActive(active);
        userRepository.save(user);
        return "SUCCESS";
    }

    public String changePassword(User user, String currentPassword, String newPassword) {
        if (user == null) {
            return "USER_NOT_FOUND";
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            return "CURRENT_REQUIRED";
        }
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            return "NEW_INVALID";
        }

        String stored = user.getPassword();
        boolean matched = false;
        if (stored != null && isBcryptHash(stored)) {
            matched = passwordEncoder.matches(currentPassword, stored);
        } else if (stored != null) {
            matched = stored.equals(currentPassword);
        }

        if (!matched) {
            return "CURRENT_WRONG";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "SUCCESS";
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
