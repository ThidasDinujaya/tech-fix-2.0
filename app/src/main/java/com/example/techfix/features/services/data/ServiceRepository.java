package com.example.techfix.features.services.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.techfix.common.data.DatabaseHelper;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

// Repository to manage repair service data using SQLite with web images
public class ServiceRepository {
    private static final String TAG = "ServiceRepository";
    private static ServiceRepository instance;
    private DatabaseHelper dbHelper;

    private ServiceRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized ServiceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceRepository(context);
        }
        return instance;
    }

    private void ensureInitialData() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_SERVICES, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count == 0) {
                    loadMockDataIntoDb();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking DB data", e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void loadMockDataIntoDb() {
        Log.d(TAG, "Inserting verified high-reliability professional mock data...");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Highly relevant repair-specific imagery
            addService(db, "Screen Repair", "Expert replacement for cracked or bleeding phone and laptop screens.", 2500.0, "6 Months", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?q=80&w=500");
            addService(db, "Battery Replacement", "Genuine high-capacity battery installation for all device brands.", 2000.0, "3 Months", "https://images.unsplash.com/photo-1600003014755-931ff9f43627?q=80&w=500");
            addService(db, "Water Damage", "Specialized ultrasonic cleaning and motherboard restoration services.", 3500.0, "1 Month", "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?q=80&w=500");
            addService(db, "Charging Port", "Precision repair for loose or damaged USB-C and Lightning ports.", 1800.0, "3 Months", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=500");
            addService(db, "Camera Module", "Front and rear camera lens and sensor replacement.", 2000.0, "6 Months", "https://images.unsplash.com/photo-1516035069341-34939603c7f2?q=80&w=500");
            addService(db, "Software Optimization", "Virus removal, OS flashing, and performance speed-up services.", 1500.0, "N/A", "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=500");
            db.setTransactionSuccessful();
            Log.d(TAG, "Verified mock data loaded successfully.");
        } finally {
            db.endTransaction();
        }
    }

    private void addService(SQLiteDatabase db, String name, String desc, double price, String warranty, String url) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_NAME, name);
        values.put(DatabaseHelper.COL_SERVICE_DESC, desc);
        values.put(DatabaseHelper.COL_SERVICE_PRICE, price);
        values.put(DatabaseHelper.COL_SERVICE_WARRANTY, warranty);
        values.put(DatabaseHelper.COL_SERVICE_IMAGE, url);
        db.insert(DatabaseHelper.TABLE_SERVICES, null, values);
    }

    public List<Service> getAllServices() {
        ensureInitialData();
        List<Service> serviceList = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_SERVICES, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    serviceList.add(new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_DESC)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_WARRANTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_IMAGE))
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return serviceList;
    }
}
