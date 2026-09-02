package com.example.techfix.features.payments.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.techfix.common.data.DatabaseHelper;

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
}
