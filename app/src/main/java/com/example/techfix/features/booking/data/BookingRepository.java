package com.example.techfix.features.booking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.techfix.common.data.DatabaseHelper;

// Handles database operations specifically for Repair Bookings
public class BookingRepository {
    private static BookingRepository instance;
    private final DatabaseHelper dbHelper;

    // Initializes the repository with the database helper
    private BookingRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    // Provides a shared instance of the repository across the app
    public static synchronized BookingRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BookingRepository(context);
        }
        return instance;
    }

    // Saves a new booking request into the SQLite database
    public long insertBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(DatabaseHelper.COL_BOOKING_SERVICE_ID, booking.getServiceId());
        values.put(DatabaseHelper.COL_BOOKING_DEVICE_TYPE, booking.getDeviceType());
        values.put(DatabaseHelper.COL_BOOKING_BRAND, booking.getBrand());
        values.put(DatabaseHelper.COL_BOOKING_MODEL, booking.getModel());
        values.put(DatabaseHelper.COL_BOOKING_DESC, booking.getDescription());
        values.put(DatabaseHelper.COL_BOOKING_DATE, booking.getAppointmentDate());
        values.put(DatabaseHelper.COL_BOOKING_IMAGE, booking.getImagePath());
        values.put(DatabaseHelper.COL_BOOKING_STATUS, booking.getStatus());
        values.put(DatabaseHelper.COL_BOOKING_USER_ID, booking.getUserId());

        // Returns the ID of the new row or -1 if an error occurred
        return db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
    }
}
