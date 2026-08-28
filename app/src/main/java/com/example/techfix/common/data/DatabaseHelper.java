package com.example.techfix.common.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Handles database creation and management for the entire app
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 6; // Bumped to 6 for highly relevant repair images

    // Table and column names for services
    public static final String TABLE_SERVICES = "services";
    public static final String COL_SERVICE_ID = "id";
    public static final String COL_SERVICE_NAME = "name";
    public static final String COL_SERVICE_DESC = "description";
    public static final String COL_SERVICE_PRICE = "price";
    public static final String COL_SERVICE_WARRANTY = "warranty";
    public static final String COL_SERVICE_IMAGE = "image_url";

    // Sets up the database connection using the application context
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the required tables when the database is first initialized
    @Override
    public void onCreate(SQLiteDatabase db) {
        android.util.Log.d("DatabaseHelper", "Creating table: " + TABLE_SERVICES);
        String createServicesTable = "CREATE TABLE " + TABLE_SERVICES + " (" +
                COL_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SERVICE_NAME + " TEXT, " +
                COL_SERVICE_DESC + " TEXT, " +
                COL_SERVICE_PRICE + " REAL, " +
                COL_SERVICE_WARRANTY + " TEXT, " +
                COL_SERVICE_IMAGE + " TEXT)"; // Changed to TEXT for URL
        
        db.execSQL(createServicesTable);
    }

    // Manages database schema changes during app updates
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        onCreate(db);
    }
}
