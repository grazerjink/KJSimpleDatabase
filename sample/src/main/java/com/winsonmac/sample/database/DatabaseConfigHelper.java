package com.winsonmac.sample.database;

import android.database.sqlite.SQLiteDatabase;

import com.winsonmac.kjsimpledatabase.KJDatabaseManager;
import com.winsonmac.kjsimpledatabase.KJEntityManager;
import com.winsonmac.kjsimpledatabase.KJStudentDAO;
import com.winsonmac.sample.model.Student;

public class DatabaseConfigHelper implements KJDatabaseManager.ConfigHelper {

    @Override
    public String getDatabaseName() {
        return "School.db";
    }

    @Override
    public int getDatabaseVersion() {
        return 1;
    }

    @Override
    public void create(SQLiteDatabase db) {
        KJEntityManager.createTableCourse(db);
        KJEntityManager.createTableStudent(db);
    }

    @Override
    public void seed(SQLiteDatabase db) {
        Student st = new Student();
        st.setStudentName("Sample student");
        st.setStudentGender(1);
        st.setCourseId(1);
        boolean success = KJStudentDAO.save(st);
    }

    @Override
    public void migrate(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
