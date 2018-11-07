package com.winsonmac.sample.model;


import com.winsonmac.kjsimplegenerator.annotations.Column;
import com.winsonmac.kjsimplegenerator.annotations.Entity;

@Entity
public class Course {

    @Column(primaryKey = true, autoIncrement = true)
    private int id;

    @Column
    private String courseName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
