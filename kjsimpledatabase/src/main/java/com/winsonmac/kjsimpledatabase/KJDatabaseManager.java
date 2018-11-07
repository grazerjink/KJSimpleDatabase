package com.winsonmac.kjsimpledatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KJDatabaseManager extends KJDatabaseHelper {

    public interface ConfigHelper {

        String getDatabaseName();

        int getDatabaseVersion();

        void create(SQLiteDatabase db);

        void seed(SQLiteDatabase db);

        void migrate(SQLiteDatabase db, int oldVersion, int newVersion);
    }

    private static KJDatabaseManager instance = null;

    public static KJDatabaseManager init(Context context) {
        if (instance == null) {
            synchronized (KJDatabaseManager.class) {
                if (null == instance) {
                    instance = new KJDatabaseManager(context);
                }
            }
        }
        return instance;
    }

    public static KJDatabaseManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("You must init manager first in application, then try again...");
        } else {
            return instance;
        }
    }

    private ConfigHelper helper;

    private KJDatabaseManager(@NotNull Context context) {
        super(context);
    }

    public void setConfigHelper(ConfigHelper helper) {
        this.helper = helper;
        startDatabaseConnection(helper.getDatabaseName(), helper.getDatabaseVersion());
    }

    @Override
    protected void createDatabase(SQLiteDatabase db) {
        if (helper != null) helper.create(db);
    }

    @Override
    protected void migrateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (helper != null) helper.migrate(db, oldVersion, newVersion);
    }

    @Override
    protected void seed(SQLiteDatabase db) {
        if (helper != null) helper.seed(db);
    }

    public boolean isExist(Object id, String key, String tableName) {
        Cursor cursor = getDatabase()
                .query(tableName,
                        null,
                        "? = ?",
                        new String[]{key, id.toString()},
                        null,
                        null,
                        null);
        boolean isExist = cursor.moveToFirst();
        cursor.close();
        return isExist;
    }

    public long insertSingle(ContentValues values, String tableName) {
        return getDatabase().insert(tableName, null, values);
    }

    public int updateSingle(ContentValues values, Object id, String key, String tableName) {
        return getDatabase().update(tableName, values, "? = ? ", new String[]{id.toString(), key});
    }

    public void insertAll(List<ContentValues> values, String tableName) {
        try {
            getDatabase().beginTransaction();
            for (ContentValues record : values) {
                getDatabase().insert(tableName, null, record);
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
    }
}
