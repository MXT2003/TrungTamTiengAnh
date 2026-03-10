package com.hutech.TrungTamTiengAnh;

import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createAndLoginUser() {
        String result = userService.createUser("testuser1", "secret123", "STUDENT", true);
        assertEquals("SUCCESS", result);

        User user = userService.login("testuser1", "secret123");
        assertNotNull(user);
        assertEquals("STUDENT", user.getRole());
    }

    @Test
    void loginUpgradesPlainPassword() {
        User u = new User();
        u.setUsername("plainuser");
        u.setPassword("plainpass");
        u.setRole("STUDENT");
        u.setActive(true);
        userRepository.save(u);

        User logged = userService.login("plainuser", "plainpass");
        assertNotNull(logged);

        User updated = userRepository.findByUsername("plainuser");
        assertNotNull(updated.getPassword());
        assertFalse(updated.getPassword().equals("plainpass"));
    }
}

