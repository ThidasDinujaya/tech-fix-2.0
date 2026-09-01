package com.example.techfix.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Central Database Helper for the TechFix application.
 *
 * Handles:
 * 1. Customer/User registration and login
 * 2. Customer profile
 * 3. Services
 * 4. Service images
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // =========================================================
    // DATABASE
    // =========================================================

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 6;


    // =========================================================
    // USERS TABLE
    // =========================================================

    public static final String TABLE_USERS = "users";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_PASSWORD = "password";


    // =========================================================
    // SERVICES TABLE
    // =========================================================

    public static final String TABLE_SERVICES = "services";

    public static final String COL_SERVICE_ID = "id";
    public static final String COL_SERVICE_NAME = "name";
    public static final String COL_SERVICE_DESC = "description";
    public static final String COL_SERVICE_PRICE = "price";
    public static final String COL_SERVICE_WARRANTY = "warranty";
    public static final String COL_SERVICE_IMAGE = "image_url";


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // =========================================================
    // CREATE DATABASE TABLES
    // =========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("DatabaseHelper", "Creating TechFix database");


        // -----------------------------------------------------
        // USERS TABLE
        // -----------------------------------------------------

        String createUserTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +

                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_NAME + " TEXT NOT NULL, " +

                        COL_EMAIL + " TEXT UNIQUE NOT NULL, " +

                        COL_PHONE + " TEXT NOT NULL, " +

                        COL_PASSWORD + " TEXT NOT NULL" +

                        ")";

        db.execSQL(createUserTable);


        // -----------------------------------------------------
        // SERVICES TABLE
        // -----------------------------------------------------

        String createServicesTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_SERVICES + " (" +

                        COL_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_SERVICE_NAME + " TEXT, " +

                        COL_SERVICE_DESC + " TEXT, " +

                        COL_SERVICE_PRICE + " REAL, " +

                        COL_SERVICE_WARRANTY + " TEXT, " +

                        COL_SERVICE_IMAGE + " TEXT" +

                        ")";

        db.execSQL(createServicesTable);


        Log.d("DatabaseHelper", "Users and Services tables created");
    }


    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        Log.d(
                "DatabaseHelper",
                "Upgrading database from version "
                        + oldVersion
                        + " to "
                        + newVersion
        );

        /*
         * IMPORTANT:
         * Do NOT DROP existing tables here.
         *
         * Using CREATE TABLE IF NOT EXISTS prevents
         * existing customer data from being deleted.
         */

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +

                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_NAME + " TEXT NOT NULL, " +

                        COL_EMAIL + " TEXT UNIQUE NOT NULL, " +

                        COL_PHONE + " TEXT NOT NULL, " +

                        COL_PASSWORD + " TEXT NOT NULL" +

                        ")"
        );


        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_SERVICES + " (" +

                        COL_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_SERVICE_NAME + " TEXT, " +

                        COL_SERVICE_DESC + " TEXT, " +

                        COL_SERVICE_PRICE + " REAL, " +

                        COL_SERVICE_WARRANTY + " TEXT, " +

                        COL_SERVICE_IMAGE + " TEXT" +

                        ")"
        );
    }


    // =========================================================
    // INSERT / REGISTER USER
    // =========================================================

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


    // =========================================================
    // CHECK DUPLICATE EMAIL
    // =========================================================

    public boolean checkEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();

        return exists;
    }


    // =========================================================
    // LOGIN CHECK
    // =========================================================

    public boolean checkUser(
            String email,
            String password
    ) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?" +
                        " AND " + COL_PASSWORD + " = ?",

                new String[]{
                        email,
                        password
                }
        );

        boolean validUser = cursor.moveToFirst();

        cursor.close();

        return validUser;
    }


    // =========================================================
    // GET USER DETAILS FOR PROFILE
    // =========================================================

    public Cursor getUserByEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",

                new String[]{email}
        );
    }


    // =========================================================
    // UPDATE USER PROFILE
    // =========================================================

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


    // =========================================================
    // GET USER NAME
    // =========================================================

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

                String databaseName = cursor.getString(index);

                if (databaseName != null &&
                        !databaseName.trim().isEmpty()) {

                    name = databaseName;
                }
            }
        }

        cursor.close();

        return name;
    }


    // =========================================================
    // DELETE USER ACCOUNT
    // =========================================================

    public boolean deleteUser(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_USERS,
                COL_EMAIL + " = ?",
                new String[]{email}
        );

        return result > 0;
    }


    // =========================================================
    // INSERT SERVICE
    // =========================================================

    public long insertService(
            String name,
            String description,
            double price,
            String warranty,
            String imageUrl
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_SERVICE_NAME, name);
        values.put(COL_SERVICE_DESC, description);
        values.put(COL_SERVICE_PRICE, price);
        values.put(COL_SERVICE_WARRANTY, warranty);
        values.put(COL_SERVICE_IMAGE, imageUrl);

        return db.insert(
                TABLE_SERVICES,
                null,
                values
        );
    }


    // =========================================================
    // GET ALL SERVICES
    // =========================================================

    public Cursor getAllServices() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SERVICES +
                        " ORDER BY " + COL_SERVICE_ID + " ASC",
                null
        );
    }


    // =========================================================
    // GET SERVICE BY ID
    // =========================================================

    public Cursor getServiceById(int serviceId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_SERVICES +
                        " WHERE " + COL_SERVICE_ID + " = ?",

                new String[]{
                        String.valueOf(serviceId)
                }
        );
    }


    // =========================================================
    // UPDATE SERVICE
    // =========================================================

    public boolean updateService(
            int serviceId,
            String name,
            String description,
            double price,
            String warranty,
            String imageUrl
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_SERVICE_NAME, name);
        values.put(COL_SERVICE_DESC, description);
        values.put(COL_SERVICE_PRICE, price);
        values.put(COL_SERVICE_WARRANTY, warranty);
        values.put(COL_SERVICE_IMAGE, imageUrl);

        int result = db.update(
                TABLE_SERVICES,
                values,
                COL_SERVICE_ID + " = ?",
                new String[]{
                        String.valueOf(serviceId)
                }
        );

        return result > 0;
    }


    // =========================================================
    // DELETE SERVICE
    // =========================================================

    public boolean deleteService(int serviceId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_SERVICES,
                COL_SERVICE_ID + " = ?",
                new String[]{
                        String.valueOf(serviceId)
                }
        );

        return result > 0;
    }


    // =========================================================
    // CLOSE DATABASE
    // =========================================================

    @Override
    public synchronized void close() {

        super.close();
    }
}