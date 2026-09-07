package com.example.techfix.features.services.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.techfix.common.data.DatabaseHelper;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

// Repository to manage repair service data using SQLite with device-specific details and parts
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
        Log.d(TAG, "Inserting categorized mock data with specs...");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // PHONE SERVICES
            addService(db, "Device Diagnosis", "Unsure what's wrong? Our experts will diagnose the issue for you.", 1500.0, "N/A", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?q=80&w=500", "Phone", "", "", "", 0);
            addService(db, "iPhone 13 Screen Repair", "Expert replacement for cracked iPhone 13 screens.", 15000.0, "6 Months", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?q=80&w=500", "Phone", "Apple", "iPhone 13", "Original", 1);
            addService(db, "S22 Battery Replacement", "Genuine high-capacity battery installation for Samsung S22.", 8500.0, "3 Months", "https://images.unsplash.com/photo-1600003014755-931ff9f43627?q=80&w=500", "Phone", "Samsung", "Galaxy S22", "Grade A", 2);
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void addService(SQLiteDatabase db, String name, String desc, double price, String warranty, 
                            String url, String category, String brand, String model, String quality, int partId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_NAME, name);
        values.put(DatabaseHelper.COL_SERVICE_DESC, desc);
        values.put(DatabaseHelper.COL_SERVICE_PRICE, price);
        values.put(DatabaseHelper.COL_SERVICE_WARRANTY, warranty);
        values.put(DatabaseHelper.COL_SERVICE_IMAGE, url);
        values.put(DatabaseHelper.COL_SERVICE_CATEGORY, category);
        values.put(DatabaseHelper.COL_SERVICE_BRAND, brand);
        values.put(DatabaseHelper.COL_SERVICE_MODEL, model);
        values.put(DatabaseHelper.COL_SERVICE_QUALITY, quality);
        values.put(DatabaseHelper.COL_SERVICE_PART_ID, partId);
        db.insert(DatabaseHelper.TABLE_SERVICES, null, values);
    }

    public List<Service> getServicesByCategory(String category) {
        ensureInitialData();
        List<Service> serviceList = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selection = null;
            String[] selectionArgs = null;
            
            if (category != null && !category.isEmpty()) {
                selection = DatabaseHelper.COL_SERVICE_CATEGORY + " = ?";
                selectionArgs = new String[]{category};
            }

            cursor = db.query(DatabaseHelper.TABLE_SERVICES, null, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    serviceList.add(new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_DESC)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_WARRANTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_BRAND)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_MODEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_QUALITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_PART_ID))
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return serviceList;
    }

    public List<Service> getAllServices() {
        return getServicesByCategory(null);
    }

    public Service getServiceById(int id) {
        ensureInitialData();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_SERVICES, null, DatabaseHelper.COL_SERVICE_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_DESC)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_WARRANTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_BRAND)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_MODEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_QUALITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_PART_ID))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public boolean addService(String name, String desc, double price, String warranty, String url, 
                              String category, String brand, String model, String quality, int partId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_NAME, name);
        values.put(DatabaseHelper.COL_SERVICE_DESC, desc);
        values.put(DatabaseHelper.COL_SERVICE_PRICE, price);
        values.put(DatabaseHelper.COL_SERVICE_WARRANTY, warranty);
        values.put(DatabaseHelper.COL_SERVICE_IMAGE, url);
        values.put(DatabaseHelper.COL_SERVICE_CATEGORY, category);
        values.put(DatabaseHelper.COL_SERVICE_BRAND, brand);
        values.put(DatabaseHelper.COL_SERVICE_MODEL, model);
        values.put(DatabaseHelper.COL_SERVICE_QUALITY, quality);
        values.put(DatabaseHelper.COL_SERVICE_PART_ID, partId);
        return db.insert(DatabaseHelper.TABLE_SERVICES, null, values) != -1;
    }

    public boolean updateService(int id, String name, String desc, double price, String warranty, 
                                 String url, String category, String brand, String model, String quality, int partId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_NAME, name);
        values.put(DatabaseHelper.COL_SERVICE_DESC, desc);
        values.put(DatabaseHelper.COL_SERVICE_PRICE, price);
        values.put(DatabaseHelper.COL_SERVICE_WARRANTY, warranty);
        values.put(DatabaseHelper.COL_SERVICE_IMAGE, url);
        values.put(DatabaseHelper.COL_SERVICE_CATEGORY, category);
        values.put(DatabaseHelper.COL_SERVICE_BRAND, brand);
        values.put(DatabaseHelper.COL_SERVICE_MODEL, model);
        values.put(DatabaseHelper.COL_SERVICE_QUALITY, quality);
        values.put(DatabaseHelper.COL_SERVICE_PART_ID, partId);
        return db.update(DatabaseHelper.TABLE_SERVICES, values, DatabaseHelper.COL_SERVICE_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteService(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_SERVICES, DatabaseHelper.COL_SERVICE_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
