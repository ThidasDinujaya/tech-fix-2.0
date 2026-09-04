package com.example.techfix.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.SparePart;
import com.example.techfix.features.branches.data.Technician;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 11;

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

    public static final String TABLE_BRANCHES = "branches";
    public static final String TABLE_TECHNICIANS = "technicians";
    public static final String TABLE_SPARE_PARTS = "spare_parts";

    public static final String COL_BRANCH_ADDRESS = "address";
    public static final String COL_BRANCH_HOURS = "hours";
    public static final String COL_BRANCH_LATITUDE = "latitude";
    public static final String COL_BRANCH_LONGITUDE = "longitude";

    public static final String COL_TECHNICIAN_ROLE = "role";
    public static final String COL_TECHNICIAN_BRANCH = "branch_name";
    public static final String COL_TECHNICIAN_STATUS = "status";

    public static final String COL_SPARE_PART_STOCK = "stock";
    public static final String COL_SPARE_PART_PRICE = "price";

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
        createBranchManagementTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            createBranchManagementTables(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them if a downgrade is requested
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECHNICIANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPARE_PARTS);
        onCreate(db);
    }

    private void createBranchManagementTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BRANCHES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_BRANCH_ADDRESS + " TEXT NOT NULL, "
                + COL_PHONE + " TEXT NOT NULL, "
                + COL_BRANCH_HOURS + " TEXT NOT NULL, "
                + COL_BRANCH_LATITUDE + " REAL NOT NULL, "
                + COL_BRANCH_LONGITUDE + " REAL NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TECHNICIANS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_TECHNICIAN_ROLE + " TEXT NOT NULL, "
                + COL_TECHNICIAN_BRANCH + " TEXT NOT NULL, "
                + COL_TECHNICIAN_STATUS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SPARE_PARTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_SPARE_PART_STOCK + " INTEGER NOT NULL, "
                + COL_SPARE_PART_PRICE + " REAL NOT NULL)");
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

    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_BRANCHES, null,
                null, null, null, null, COL_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                branches.add(new Branch(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BRANCH_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BRANCH_LONGITUDE))));
            }
        }
        return branches;
    }

    public List<Technician> getAllTechnicians() {
        List<Technician> technicians = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_TECHNICIANS, null,
                null, null, null, null, COL_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                technicians.add(new Technician(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_BRANCH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_STATUS))));
            }
        }
        return technicians;
    }

    public boolean addTechnician(String name, String role, String branch, String status) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_TECHNICIAN_ROLE, role);
        values.put(COL_TECHNICIAN_BRANCH, branch);
        values.put(COL_TECHNICIAN_STATUS, status);
        return getWritableDatabase().insert(TABLE_TECHNICIANS, null, values) != -1;
    }

    public List<SparePart> getAllSpareParts() {
        List<SparePart> spareParts = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_SPARE_PARTS, null,
                null, null, null, null, COL_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                spareParts.add(new SparePart(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SPARE_PART_STOCK)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SPARE_PART_PRICE))));
            }
        }
        return spareParts;
    }
}
