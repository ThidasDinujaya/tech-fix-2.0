package com.example.techfix.features.payments.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Booking;

public class PaymentRepository {
    private static PaymentRepository instance;
    private final DatabaseHelper dbHelper;

    private PaymentRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized PaymentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PaymentRepository(context);
        }
        return instance;
    }

    public long insertPayment(Payment payment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_PAYMENT_BOOKING_ID, payment.getBookingId());
        values.put(DatabaseHelper.COL_PAYMENT_AMOUNT, payment.getAmount());
        values.put(DatabaseHelper.COL_PAYMENT_METHOD, payment.getMethod());
        values.put(DatabaseHelper.COL_PAYMENT_CARD_NUM, payment.getCardNumber());
        values.put(DatabaseHelper.COL_PAYMENT_EXPIRY, payment.getExpiryDate());
        values.put(DatabaseHelper.COL_PAYMENT_CVV, payment.getCvv());
        values.put(DatabaseHelper.COL_PAYMENT_DATE, payment.getPaymentDate());

        return db.insert(DatabaseHelper.TABLE_PAYMENTS, null, values);
    }

    public boolean insertBookingWithPayment(Booking booking, Payment payment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Insert Booking
            ContentValues bValues = new ContentValues();
            bValues.put(DatabaseHelper.COL_BOOKING_SERVICE_ID, booking.getServiceId());
            bValues.put(DatabaseHelper.COL_BOOKING_DEVICE_TYPE, booking.getDeviceType());
            bValues.put(DatabaseHelper.COL_BOOKING_BRAND, booking.getBrand());
            bValues.put(DatabaseHelper.COL_BOOKING_MODEL, booking.getModel());
            bValues.put(DatabaseHelper.COL_BOOKING_DESC, booking.getDescription());
            bValues.put(DatabaseHelper.COL_BOOKING_DATE, booking.getAppointmentDate());
            bValues.put(DatabaseHelper.COL_BOOKING_IMAGE, booking.getImagePath());
            bValues.put(DatabaseHelper.COL_BOOKING_STATUS, booking.getStatus());
            bValues.put(DatabaseHelper.COL_BOOKING_USER_ID, booking.getUserId());
            bValues.put(DatabaseHelper.COL_BOOKING_BRANCH_NAME, booking.getBranchName());
            bValues.put(DatabaseHelper.COL_BOOKING_TECH_NAME, booking.getTechnicianName());

            long bookingId = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, bValues);
            if (bookingId == -1) return false;
            booking.setId((int) bookingId);

            // 2. Insert Payment
            ContentValues pValues = new ContentValues();
            pValues.put(DatabaseHelper.COL_PAYMENT_BOOKING_ID, (int) bookingId);
            pValues.put(DatabaseHelper.COL_PAYMENT_AMOUNT, payment.getAmount());
            pValues.put(DatabaseHelper.COL_PAYMENT_METHOD, payment.getMethod());
            pValues.put(DatabaseHelper.COL_PAYMENT_CARD_NUM, payment.getCardNumber());
            pValues.put(DatabaseHelper.COL_PAYMENT_EXPIRY, payment.getExpiryDate());
            pValues.put(DatabaseHelper.COL_PAYMENT_CVV, payment.getCvv());
            pValues.put(DatabaseHelper.COL_PAYMENT_DATE, payment.getPaymentDate());

            long paymentId = db.insert(DatabaseHelper.TABLE_PAYMENTS, null, pValues);
            if (paymentId == -1) return false;
            payment.setId((int) paymentId);
            payment.setBookingId((int) bookingId);

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public Payment getPaymentByBookingId(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_PAYMENTS, null, 
                DatabaseHelper.COL_PAYMENT_BOOKING_ID + " = ?", 
                new String[]{String.valueOf(bookingId)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return new Payment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_PAYMENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_BOOKING_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_METHOD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_CARD_NUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_EXPIRY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_CVV)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PAYMENT_DATE))
                );
            }
        }
        return null;
    }
}
