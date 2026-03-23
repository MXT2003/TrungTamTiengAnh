package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ten khoa hoc khong duoc de trong")
    @Size(max = 100, message = "Ten khoa hoc toi da 100 ky tu")
    private String name;

    @Size(max = 500, message = "Mo ta toi da 500 ky tu")
    @NotBlank(message = "Mo ta khong duoc de trong")
    private String description;

    @Size(max = 50, message = "Cap do toi da 50 ky tu")
    @NotBlank(message = "Cap do khong duoc de trong")
    private String level;

    @Min(value = 1, message = "So tuan hoc phai >= 1")
    private int durationWeeks;

    @Min(value = 1, message = "Hoc phi phai >= 1")
    private double fee;

    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public int getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(int durationWeeks) { this.durationWeeks = durationWeeks; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
