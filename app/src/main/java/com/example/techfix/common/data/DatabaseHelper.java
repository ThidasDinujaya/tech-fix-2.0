package com.example.techfix.common.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Handles database creation and management for the entire app
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 7; // Bumped to 7 for Bookings table

    // Table and column names for services
    public static final String TABLE_SERVICES = "services";
    public static final String COL_SERVICE_ID = "id";
    public static final String COL_SERVICE_NAME = "name";
    public static final String COL_SERVICE_DESC = "description";
    public static final String COL_SERVICE_PRICE = "price";
    public static final String COL_SERVICE_WARRANTY = "warranty";
    public static final String COL_SERVICE_IMAGE = "image_url";

    // Table and column names for bookings
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

    // Sets up the database connection using the application context
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the required tables when the database is first initialized
    @Override
    public void onCreate(SQLiteDatabase db) {
        android.util.Log.d("DatabaseHelper", "Creating tables...");
        
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
        
        db.execSQL(createServicesTable);
        db.execSQL(createBookingsTable);
    }

    // Manages database schema changes during app updates
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }
}
