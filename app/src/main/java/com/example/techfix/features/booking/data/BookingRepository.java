package com.example.techfix.features.booking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.techfix.common.data.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

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
        values.put(DatabaseHelper.COL_BOOKING_BRANCH_NAME, booking.getBranchName());
        values.put(DatabaseHelper.COL_BOOKING_TECH_NAME, booking.getTechnicianName());

        // Returns the ID of the new row or -1 if an error occurred
        return db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
    }

    // Fetches all bookings from the database, ordered by newest first
    public List<Booking> getAllBookings() {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, null, null, null, null, DatabaseHelper.COL_BOOKING_ID + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                bookingList.add(mapCursorToBooking(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    // Fetches bookings filtered by their current status
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, DatabaseHelper.COL_BOOKING_STATUS + "=?",
                new String[]{status}, null, null, DatabaseHelper.COL_BOOKING_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                bookingList.add(mapCursorToBooking(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    // Fetches bookings for a specific user
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, DatabaseHelper.COL_BOOKING_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, DatabaseHelper.COL_BOOKING_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                bookingList.add(mapCursorToBooking(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    // Fetches bookings for a specific user and status
    public List<Booking> getBookingsByUserAndStatus(int userId, String[] statuses) {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        StringBuilder selection = new StringBuilder(DatabaseHelper.COL_BOOKING_USER_ID + "=? AND " + DatabaseHelper.COL_BOOKING_STATUS + " IN (");
        String[] selectionArgs = new String[statuses.length + 1];
        selectionArgs[0] = String.valueOf(userId);
        for (int i = 0; i < statuses.length; i++) {
            selection.append("?");
            if (i < statuses.length - 1) selection.append(",");
            selectionArgs[i + 1] = statuses[i];
        }
        selection.append(")");

        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, selection.toString(),
                selectionArgs, null, null, DatabaseHelper.COL_BOOKING_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                bookingList.add(mapCursorToBooking(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    // Fetches a single booking by its ID
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(bookingId)}, null, null, null);

        Booking booking = null;
        if (cursor.moveToFirst()) {
            booking = mapCursorToBooking(cursor);
        }
        cursor.close();
        return booking;
    }

    // Helper method to map a cursor row to a Booking object
    private Booking mapCursorToBooking(Cursor cursor) {
        return new Booking(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_SERVICE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_DEVICE_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_BRAND)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_MODEL)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_DESC)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_IMAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_STATUS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_BRANCH_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_TECH_NAME))
        );
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, status);
        return db.update(DatabaseHelper.TABLE_BOOKINGS, values, DatabaseHelper.COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)}) > 0;
    }

    public boolean updateBookingAssignment(int bookingId, String status, String techName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, status);
        values.put(DatabaseHelper.COL_BOOKING_TECH_NAME, techName);
        return db.update(DatabaseHelper.TABLE_BOOKINGS, values, DatabaseHelper.COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)}) > 0;
    }

    public boolean deleteBooking(int bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_BOOKINGS, DatabaseHelper.COL_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)}) > 0;
    }
}
