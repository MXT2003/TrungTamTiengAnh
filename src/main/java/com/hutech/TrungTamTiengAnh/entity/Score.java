package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LopHoc lopHoc;

    @ManyToOne
    private User student;

    private Double score;

    private String comment;

    private LocalDate updatedDate = LocalDate.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }
}

