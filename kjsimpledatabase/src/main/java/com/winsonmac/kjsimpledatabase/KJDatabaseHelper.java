package com.winsonmac.kjsimpledatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class KJDatabaseHelper {

    private final static String TAG = "KJ_DATABASE";
    private final static String DB_DIRECTORY = "databases";
    private final static String DB_INFO_PATH = "-info";

    private int mCurrentVersion = 1;
    private String mName;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private String mDirectoryPath;

    public KJDatabaseHelper(Context context) {
        this.mDirectoryPath = String.format("%s/data/%s/%s/", Environment.getDataDirectory().getPath(), context.getPackageName(), DB_DIRECTORY);
        this.mContext = context;
        initDatabaseDirectory();
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public Context getContext() {
        return mContext;
    }

    private void initDatabaseDirectory() {
        File file = new File(mDirectoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    protected void startDatabaseConnection(String databaseName, int version) {

        this.mCurrentVersion = version;
        this.mName = databaseName;

        if (checkDatabaseExists()) {
            // Database is exist, open it.
            mDatabase = SQLiteDatabase.openDatabase(mDirectoryPath + mName, null, SQLiteDatabase.OPEN_READWRITE);

            if (mCurrentVersion == 1) {
                // If CurrentVersion equal 1, we just need to open and use it
                // Do nothing at here.
            } else if (mCurrentVersion > 1) {
                // Else current version greater than 1,
                // we need to check old version and upgrade version step by step.
                for (int i = getOldVersion(); i < mCurrentVersion; i++) {
                    migrateDatabase(mDatabase, i, i + 1);
                }
            }

        } else {
            // Database is not exist, create and open to use it.
            mDatabase = SQLiteDatabase.openOrCreateDatabase(mDirectoryPath + mName, null);

            if (mCurrentVersion == 1) {
                // if current version equal 1, run the first initiation.
                createDatabase(mDatabase);

            } else if (mCurrentVersion > 1) {
                // else current version is defined greater than 1
                // also run the first initiation to create the original database schema.
                createDatabase(mDatabase);

                // run migration to update database to the latest version schema.
                for (int i = 2; i <= mCurrentVersion; i++) {
                    migrateDatabase(mDatabase, i - 1, i);
                }
            }
        }
        seed(mDatabase);
        saveDatabaseVersion();
    }

    private boolean checkDatabaseExists() {
        File file = new File(mDirectoryPath + mName);
        return file.exists();
    }

    private int getOldVersion() {
        int oldVersion = 1;
        try {
            FileInputStream fileInputStream = new FileInputStream(mDirectoryPath + mName + DB_INFO_PATH);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            oldVersion = Byte.valueOf(inputStream.readByte()).intValue();
            inputStream.close();
            Log.e(TAG, "Read database info successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Database info is empty");
        }
        return oldVersion;
    }

    private void saveDatabaseVersion() {
        try {
            File file = new File(mDirectoryPath + mName + DB_INFO_PATH);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.write(mCurrentVersion);
            outputStream.flush();
            outputStream.close();
            Log.e(TAG, "Write database info successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Write database info failed.");
        }
    }


    protected abstract void seed(SQLiteDatabase db);

    protected abstract void createDatabase(SQLiteDatabase db);

    protected abstract void migrateDatabase(SQLiteDatabase db, int oldVersion, int newVersion);

}
