package com.example.techfix.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 8;

    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_PASSWORD = "password";

    public static final String TABLE_SERVICES = "services";
    public static final String COL_SERVICE_ID = "id";
    public static final String COL_SERVICE_NAME = "name";
    public static final String COL_SERVICE_DESC = "description";
    public static final String COL_SERVICE_PRICE = "price";
    public static final String COL_SERVICE_WARRANTY = "warranty";
    public static final String COL_SERVICE_IMAGE = "image_url";

    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_SERVICE_ID = "service_id";
    public static final String COL_BOOKING_DEVICE_TYPE = "device_type";
    public static final String COL_BOOKING_BRAND = "brand";
    public static final String COL_BOOKING_MODEL = "model";
    public static final String COL_BOOKING_DESC = "description";
    public static final String COL_BOOKING_DATE = "appointment_date";
    public static final String COL_BOOKING_IMAGE = "image_path";
    public static final String COL_BOOKING_STATUS = "status";
    public static final String COL_BOOKING_USER_ID = "user_id";

    public static final String TABLE_PAYMENTS = "payments";
    public static final String COL_PAYMENT_ID = "id";
    public static final String COL_PAYMENT_BOOKING_ID = "booking_id";
    public static final String COL_PAYMENT_AMOUNT = "amount";
    public static final String COL_PAYMENT_METHOD = "method";
    public static final String COL_PAYMENT_CARD_NUM = "card_number";
    public static final String COL_PAYMENT_EXPIRY = "expiry_date";
    public static final String COL_PAYMENT_CVV = "cvv";
    public static final String COL_PAYMENT_DATE = "payment_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PHONE + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL)";

        String createServicesTable = "CREATE TABLE " + TABLE_SERVICES + " (" +
                COL_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SERVICE_NAME + " TEXT, " +
                COL_SERVICE_DESC + " TEXT, " +
                COL_SERVICE_PRICE + " REAL, " +
                COL_SERVICE_WARRANTY + " TEXT, " +
                COL_SERVICE_IMAGE + " TEXT)";

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_SERVICE_ID + " INTEGER, " +
                COL_BOOKING_DEVICE_TYPE + " TEXT, " +
                COL_BOOKING_BRAND + " TEXT, " +
                COL_BOOKING_MODEL + " TEXT, " +
                COL_BOOKING_DESC + " TEXT, " +
                COL_BOOKING_DATE + " TEXT, " +
                COL_BOOKING_IMAGE + " TEXT, " +
                COL_BOOKING_STATUS + " TEXT, " +
                COL_BOOKING_USER_ID + " INTEGER)";

        String createPaymentsTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                COL_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PAYMENT_BOOKING_ID + " INTEGER, " +
                COL_PAYMENT_AMOUNT + " REAL, " +
                COL_PAYMENT_METHOD + " TEXT, " +
                COL_PAYMENT_CARD_NUM + " TEXT, " +
                COL_PAYMENT_EXPIRY + " TEXT, " +
                COL_PAYMENT_CVV + " TEXT, " +
                COL_PAYMENT_DATE + " TEXT)";

        db.execSQL(createUserTable);
        db.execSQL(createServicesTable);
        db.execSQL(createBookingsTable);
        db.execSQL(createPaymentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        onCreate(db);
    }

    public boolean insertUser(String name, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?", new String[]{email, password});
        boolean validUser = cursor.getCount() > 0;
        cursor.close();
        return validUser;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        String name = "Customer";
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COL_NAME);
            if (index >= 0) name = cursor.getString(index);
        }
        cursor.close();
        return name;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
    }

    public boolean updateUserProfile(String email, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        return db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email}) > 0;
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        int userId = -1;
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COL_ID);
            if (index >= 0) userId = cursor.getInt(index);
        }
        cursor.close();
        return userId;
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COL_EMAIL + " = ?", new String[]{email}) > 0;
    }
}
