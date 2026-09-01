package com.example.techfix.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database
    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    public static final String TABLE_USERS = "users";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_PASSWORD = "password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // =========================================
    // CREATE TABLE
    // =========================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUserTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                        COL_PHONE + " TEXT NOT NULL, " +
                        COL_PASSWORD + " TEXT NOT NULL" +
                        ")";

        db.execSQL(createUserTable);
    }


    // =========================================
    // DATABASE UPGRADE
    // =========================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }


    // =========================================
    // INSERT / REGISTER USER
    // =========================================

    public boolean insertUser(
            String name,
            String email,
            String phone,
            String password
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
        values.put(COL_PASSWORD, password);

        long result = db.insert(
                TABLE_USERS,
                null,
                values
        );

        return result != -1;
    }


    // =========================================
    // CHECK DUPLICATE EMAIL
    // =========================================

    public boolean checkEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }


    // =========================================
    // LOGIN CHECK
    // =========================================

    public boolean checkUser(
            String email,
            String password
    ) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?" +
                        " AND " + COL_PASSWORD + " = ?",
                new String[]{
                        email,
                        password
                }
        );

        boolean validUser = cursor.getCount() > 0;

        cursor.close();

        return validUser;
    }


    // =========================================
    // GET USER DETAILS FOR PROFILE
    // =========================================

    public Cursor getUserByEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );
    }


    // =========================================
    // UPDATE PROFILE
    // =========================================

    public boolean updateUserProfile(
            String email,
            String name,
            String phone
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);

        int result = db.update(
                TABLE_USERS,
                values,
                COL_EMAIL + " = ?",
                new String[]{email}
        );

        return result > 0;
    }


    // =========================================
    // GET USER NAME
    // =========================================

    public String getUserName(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_NAME +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );

        String name = "Customer";

        if (cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(COL_NAME);

            if (index >= 0) {
                name = cursor.getString(index);
            }
        }

        cursor.close();

        return name;
    }


    // =========================================
    // DELETE ACCOUNT - OPTIONAL
    // =========================================

    public boolean deleteUser(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_USERS,
                COL_EMAIL + " = ?",
                new String[]{email}
        );

        return result > 0;
    }
}