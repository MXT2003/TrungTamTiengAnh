package com.hutech.TrungTamTiengAnh;

import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrungTamTiengAnhApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrungTamTiengAnhApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository repo) {
        return args -> {
            if (repo.findByUsername("admin") == null) {
                repo.save(
                        User.builder()
                                .username("admin")
                                .password("admin123")
                                .role("ADMIN")
                                .build()
                );
                System.out.println("Admin account created!");
            }

            if (repo.findByUsername("VanA") == null) {
                repo.save(
                        User.builder()
                                .username("VanA")
                                .password("111111")
                                .role("TEACHER")
                                .build()
                );
                System.out.println("Teacher account VanA created!");
            }
        };
    }
}
