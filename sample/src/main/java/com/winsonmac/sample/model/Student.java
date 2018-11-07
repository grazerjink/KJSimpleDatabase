package com.winsonmac.sample.model;


import com.winsonmac.kjsimplegenerator.annotations.Column;
import com.winsonmac.kjsimplegenerator.annotations.Entity;

@Entity
public class Student {

    @Column(primaryKey = true, autoIncrement = true)
    private int id;

    @Column
    private String studentName;

    @Column
    private Integer studentGender;

    @Column
    private int courseId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(int studentGender) {
        this.studentGender = studentGender;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
