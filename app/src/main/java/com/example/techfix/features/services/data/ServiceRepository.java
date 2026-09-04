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
        Log.d(TAG, "Inserting categorized professional mock data...");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // PHONE SERVICES
            addService(db, "iPhone Screen Repair", "Expert replacement for cracked or bleeding iPhone screens.", 25000.0, "6 Months", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?q=80&w=500", "Phone");
            addService(db, "Phone Battery Replacement", "Genuine high-capacity battery installation for all smartphones.", 8500.0, "3 Months", "https://images.unsplash.com/photo-1600003014755-931ff9f43627?q=80&w=500", "Phone");
            
            // LAPTOP SERVICES
            addService(db, "MacBook Logic Board Repair", "Specialized ultrasonic cleaning and motherboard restoration.", 45000.0, "3 Months", "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?q=80&w=500", "Laptop");
            addService(db, "Laptop Screen Replacement", "LED/LCD screen replacement for all major laptop brands.", 15000.0, "6 Months", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=500", "Laptop");

            // DESKTOP SERVICES
            addService(db, "PC Power Supply Fix", "Replacement of burnt or malfunctioning power supply units.", 12000.0, "1 Year", "https://images.unsplash.com/photo-1562408590-e32931084e23?q=80&w=500", "Desktop");
            addService(db, "GPU Cleaning & Thermal Paste", "Professional GPU maintenance to prevent overheating.", 5000.0, "N/A", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=500", "Desktop");

            // TABLET SERVICES
            addService(db, "iPad Digitizer Repair", "Touch screen glass replacement for iPads and Android tablets.", 18000.0, "6 Months", "https://images.unsplash.com/photo-1516035069341-34939603c7f2?q=80&w=500", "Tablet");
            addService(db, "Tablet Charging Port Fix", "Precision repair for loose or damaged charging ports.", 7500.0, "3 Months", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=500", "Tablet");

            db.setTransactionSuccessful();
            Log.d(TAG, "Categorized mock data loaded successfully.");
        } finally {
            db.endTransaction();
        }
    }

    private void addService(SQLiteDatabase db, String name, String desc, double price, String warranty, String url, String category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SERVICE_NAME, name);
        values.put(DatabaseHelper.COL_SERVICE_DESC, desc);
        values.put(DatabaseHelper.COL_SERVICE_PRICE, price);
        values.put(DatabaseHelper.COL_SERVICE_WARRANTY, warranty);
        values.put(DatabaseHelper.COL_SERVICE_IMAGE, url);
        values.put(DatabaseHelper.COL_SERVICE_CATEGORY, category);
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
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SERVICE_CATEGORY))
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
}
