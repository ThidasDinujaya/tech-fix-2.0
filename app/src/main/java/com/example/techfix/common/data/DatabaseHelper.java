package com.example.techfix.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.techfix.features.auth.data.User;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.SparePart;
import com.example.techfix.features.branches.data.TechAvailability;
import com.example.techfix.features.branches.data.Technician;
import com.example.techfix.features.payments.data.Payment;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_db";
    private static final int DATABASE_VERSION = 34;

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
    public static final String COL_SERVICE_CATEGORY = "category";
    public static final String COL_SERVICE_BRAND = "brand";
    public static final String COL_SERVICE_MODEL = "model";
    public static final String COL_SERVICE_QUALITY = "quality";
    public static final String COL_SERVICE_PART_ID = "spare_part_id";

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
    public static final String COL_BOOKING_BRANCH_NAME = "branch_name";
    public static final String COL_BOOKING_TECH_NAME = "technician_name";

    public static final String TABLE_PAYMENTS = "payments";
    public static final String COL_ID_PAYMENT = "id";
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
    
    public static final String TABLE_BRANDS = "brands";
    public static final String TABLE_MODELS = "models";
    public static final String TABLE_QUALITIES = "qualities";
    public static final String TABLE_SERVICE_CATEGORIES = "service_categories";

    public static final String TABLE_TECH_AVAILABILITY = "technician_availability";
    public static final String COL_AVAIL_ID = "id";
    public static final String COL_AVAIL_TECH_ID = "technician_id";
    public static final String COL_AVAIL_DATE = "available_date"; 
    public static final String COL_AVAIL_STATUS = "is_available"; 

    public static final String COL_BRANCH_ADDRESS = "address";
    public static final String COL_BRANCH_HOURS = "hours";
    public static final String COL_BRANCH_PHONE2 = "phone2";
    public static final String COL_BRANCH_HOURS_MON_FRI = "hours_mon_fri";
    public static final String COL_BRANCH_HOURS_SAT = "hours_sat";
    public static final String COL_BRANCH_HOURS_SUN = "hours_sun";
    public static final String COL_BRANCH_MAP_LINK = "map_link";
    public static final String COL_BRANCH_LATITUDE = "latitude";
    public static final String COL_BRANCH_LONGITUDE = "longitude";

    public static final String COL_TECHNICIAN_BRANCH = "branch_name";
    public static final String COL_TECHNICIAN_STATUS = "status";

    public static final String COL_SPARE_PART_STOCK = "stock";
    public static final String COL_SPARE_PART_PRICE = "price";
    public static final String COL_SPARE_PART_BRAND = "brand";
    public static final String COL_SPARE_PART_MODEL = "model";
    public static final String COL_SPARE_PART_QUALITY = "quality";
    
    public static final String COL_BRAND_CATEGORY = "category";
    public static final String COL_MODEL_BRAND_ID = "brand_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PHONE + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_SERVICES + " (" +
                COL_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SERVICE_NAME + " TEXT, " +
                COL_SERVICE_DESC + " TEXT, " +
                COL_SERVICE_PRICE + " REAL, " +
                COL_SERVICE_WARRANTY + " TEXT, " +
                COL_SERVICE_IMAGE + " TEXT, " +
                COL_SERVICE_CATEGORY + " TEXT, " +
                COL_SERVICE_BRAND + " TEXT, " +
                COL_SERVICE_MODEL + " TEXT, " +
                COL_SERVICE_QUALITY + " TEXT, " +
                COL_SERVICE_PART_ID + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_SERVICE_ID + " INTEGER, " +
                COL_BOOKING_DEVICE_TYPE + " TEXT, " +
                COL_BOOKING_BRAND + " TEXT, " +
                COL_BOOKING_MODEL + " TEXT, " +
                COL_BOOKING_DESC + " TEXT, " +
                COL_BOOKING_DATE + " TEXT, " +
                COL_BOOKING_IMAGE + " TEXT, " +
                COL_BOOKING_STATUS + " TEXT, " +
                COL_BOOKING_USER_ID + " INTEGER, " +
                COL_BOOKING_BRANCH_NAME + " TEXT, " +
                COL_BOOKING_TECH_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PAYMENTS + " (" +
                COL_ID_PAYMENT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PAYMENT_BOOKING_ID + " INTEGER, " +
                COL_PAYMENT_AMOUNT + " REAL, " +
                COL_PAYMENT_METHOD + " TEXT, " +
                COL_PAYMENT_CARD_NUM + " TEXT, " +
                COL_PAYMENT_EXPIRY + " TEXT, " +
                COL_PAYMENT_CVV + " TEXT, " +
                COL_PAYMENT_DATE + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BRANCHES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_BRANCH_ADDRESS + " TEXT NOT NULL, "
                + COL_PHONE + " TEXT NOT NULL, "
                + COL_BRANCH_PHONE2 + " TEXT, "
                + COL_BRANCH_HOURS_MON_FRI + " TEXT, "
                + COL_BRANCH_HOURS_SAT + " TEXT, "
                + COL_BRANCH_HOURS_SUN + " TEXT, "
                + COL_BRANCH_HOURS + " TEXT, "
                + COL_BRANCH_MAP_LINK + " TEXT, "
                + COL_BRANCH_LATITUDE + " REAL, "
                + COL_BRANCH_LONGITUDE + " REAL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TECHNICIANS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_TECHNICIAN_BRANCH + " TEXT NOT NULL, "
                + COL_TECHNICIAN_STATUS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SPARE_PARTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_SPARE_PART_STOCK + " INTEGER NOT NULL, "
                + COL_SPARE_PART_PRICE + " REAL NOT NULL, "
                + COL_SPARE_PART_BRAND + " TEXT, "
                + COL_SPARE_PART_MODEL + " TEXT, "
                + COL_SPARE_PART_QUALITY + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BRANDS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_BRAND_CATEGORY + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MODELS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MODEL_BRAND_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_MODEL_BRAND_ID + ") REFERENCES " + TABLE_BRANDS + "(" + COL_ID + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUALITIES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SERVICE_CATEGORIES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT UNIQUE NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TECH_AVAILABILITY + " (" +
                COL_AVAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AVAIL_TECH_ID + " INTEGER, " +
                COL_AVAIL_DATE + " TEXT, " +
                COL_AVAIL_STATUS + " INTEGER, " +
                "FOREIGN KEY(" + COL_AVAIL_TECH_ID + ") REFERENCES " + TABLE_TECHNICIANS + "(" + COL_ID + ") ON DELETE CASCADE)");

        populateInitialData(db);
    }

    private void populateInitialData(SQLiteDatabase db) {
        insertBranch(db, "Colombo Main", "123 Galle Road, Colombo 03", "0112345678", "0112345679", 
                     "08:00 AM - 06:00 PM", "09:00 AM - 04:00 PM", "Closed", "https://maps.google.com/?q=6.9271,79.8612", 6.9271, 79.8612);
        insertBranch(db, "Kandy Branch", "45 Dalada Veediya, Kandy", "0812345678", "", 
                     "08:30 AM - 05:30 PM", "08:30 AM - 01:00 PM", "Closed", "https://maps.google.com/?q=7.2906,80.6337", 7.2906, 80.6337);

        insertServiceCategory(db, "Phone");
        insertServiceCategory(db, "Laptop");
        insertServiceCategory(db, "Desktop");
        insertServiceCategory(db, "Tablet");

        insertTechnician(db, "Kasun Perera", "Colombo Main", "Available");
        insertTechnician(db, "Amara Silva", "Kandy Branch", "Busy");

        insertSparePart(db, "iPhone 13 Screen", 50, 15000.0, "Apple", "iPhone 13", "Original");
        
        long appleId = insertBrand(db, "Apple", "Phone");
        insertModel(db, appleId, "iPhone 15 Pro");
        insertModel(db, appleId, "iPhone 14");
        insertModel(db, appleId, "iPhone 13");

        insertQuality(db, "Original");
        insertQuality(db, "Grade A");
    }

    private long insertServiceCategory(SQLiteDatabase db, String name) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        return db.insert(TABLE_SERVICE_CATEGORIES, null, v);
    }

    private long insertBrand(SQLiteDatabase db, String name, String category) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRAND_CATEGORY, category);
        return db.insert(TABLE_BRANDS, null, v);
    }

    private void insertModel(SQLiteDatabase db, long brandId, String name) {
        ContentValues v = new ContentValues();
        v.put(COL_MODEL_BRAND_ID, brandId);
        v.put(COL_NAME, name);
        db.insert(TABLE_MODELS, null, v);
    }

    private void insertQuality(SQLiteDatabase db, String name) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        db.insert(TABLE_QUALITIES, null, v);
    }

    private void insertBranch(SQLiteDatabase db, String name, String addr, String phone, String phone2,
                              String hMonFri, String hSat, String hSun, String mapLink, double lat, double lon) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, addr);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
        v.put(COL_BRANCH_MAP_LINK, mapLink);
        v.put(COL_BRANCH_LATITUDE, lat);
        v.put(COL_BRANCH_LONGITUDE, lon);
        v.put(COL_BRANCH_HOURS, "Mon-Fri: " + hMonFri);
        db.insert(TABLE_BRANCHES, null, v);
    }

    private void insertTechnician(SQLiteDatabase db, String name, String branch, String status) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_TECHNICIAN_BRANCH, branch);
        v.put(COL_TECHNICIAN_STATUS, status);
        db.insert(TABLE_TECHNICIANS, null, v);
    }

    private void insertSparePart(SQLiteDatabase db, String name, int stock, double price, String brand, String model, String quality) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_SPARE_PART_STOCK, stock);
        v.put(COL_SPARE_PART_PRICE, price);
        v.put(COL_SPARE_PART_BRAND, brand);
        v.put(COL_SPARE_PART_MODEL, model);
        v.put(COL_SPARE_PART_QUALITY, quality);
        db.insert(TABLE_SPARE_PARTS, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS technician_roles");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECHNICIANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPARE_PARTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUALITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECH_AVAILABILITY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
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
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_PHONE2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_MON_FRI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_SAT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_SUN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_MAP_LINK)),
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
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_BRANCH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_STATUS))));
            }
        }
        return technicians;
    }

    public boolean addTechnician(String name, String branch, String status) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_TECHNICIAN_BRANCH, branch);
        values.put(COL_TECHNICIAN_STATUS, status);
        return getWritableDatabase().insert(TABLE_TECHNICIANS, null, values) != -1;
    }

    public boolean updateTechnician(int id, String name, String branch, String status) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_TECHNICIAN_BRANCH, branch);
        values.put(COL_TECHNICIAN_STATUS, status);
        return getWritableDatabase().update(TABLE_TECHNICIANS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteTechnician(int id) {
        return getWritableDatabase().delete(TABLE_TECHNICIANS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<SparePart> getAllSpareParts() {
        List<SparePart> spareParts = new ArrayList<>();
        String query = "SELECT s.*, b." + COL_BRAND_CATEGORY + " FROM " + TABLE_SPARE_PARTS + " s " +
                "LEFT JOIN " + TABLE_BRANDS + " b ON s." + COL_SPARE_PART_BRAND + " = b." + COL_NAME;
        
        try (Cursor cursor = getReadableDatabase().rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                spareParts.add(new SparePart(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SPARE_PART_STOCK)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_SPARE_PART_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SPARE_PART_BRAND)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SPARE_PART_MODEL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SPARE_PART_QUALITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRAND_CATEGORY))));
            }
        }
        return spareParts;
    }

    public Cursor getSparePartsByModel(String brand, String model) {
        return getReadableDatabase().query(TABLE_SPARE_PARTS, null, 
                COL_SPARE_PART_BRAND + "=? AND " + COL_SPARE_PART_MODEL + "=?", 
                new String[]{brand, model}, null, null, COL_NAME + " ASC");
    }

    public boolean addSparePart(String name, int stock, double price, String brand, String model, String quality) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SPARE_PART_STOCK, stock);
        values.put(COL_SPARE_PART_PRICE, price);
        values.put(COL_SPARE_PART_BRAND, brand);
        values.put(COL_SPARE_PART_MODEL, model);
        values.put(COL_SPARE_PART_QUALITY, quality);
        return getWritableDatabase().insert(TABLE_SPARE_PARTS, null, values) != -1;
    }

    public boolean updateSparePart(int id, String name, int stock, double price, String brand, String model, String quality) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SPARE_PART_STOCK, stock);
        values.put(COL_SPARE_PART_PRICE, price);
        values.put(COL_SPARE_PART_BRAND, brand);
        values.put(COL_SPARE_PART_MODEL, model);
        values.put(COL_SPARE_PART_QUALITY, quality);
        return getWritableDatabase().update(TABLE_SPARE_PARTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteSparePart(int id) {
        return getWritableDatabase().delete(TABLE_SPARE_PARTS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean addBranch(String name, String address, String phone, String phone2,
                             String hMonFri, String hSat, String hSun, String mapLink, double lat, double lon) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, address);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
        v.put(COL_BRANCH_LATITUDE, lat);
        v.put(COL_BRANCH_LONGITUDE, lon);
        v.put(COL_BRANCH_MAP_LINK, mapLink);
        v.put(COL_BRANCH_HOURS, "Mon-Fri: " + hMonFri);
        return getWritableDatabase().insert(TABLE_BRANCHES, null, v) != -1;
    }

    public boolean updateBranch(int id, String name, String address, String phone, String phone2,
                                String hMonFri, String hSat, String hSun, String mapLink, double lat, double lon) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, address);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
        v.put(COL_BRANCH_LATITUDE, lat);
        v.put(COL_BRANCH_LONGITUDE, lon);
        v.put(COL_BRANCH_MAP_LINK, mapLink);
        v.put(COL_BRANCH_HOURS, "Mon-Fri: " + hMonFri);
        return getWritableDatabase().update(TABLE_BRANCHES, v, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteBranch(int id) {
        return getWritableDatabase().delete(TABLE_BRANCHES, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
    
    // ==========================================
    // BRANDS, MODELS, QUALITIES CRUD
    // ==========================================

    public boolean addBrand(String name, String category) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_BRAND_CATEGORY, category);
        return getWritableDatabase().insert(TABLE_BRANDS, null, values) != -1;
    }

    public boolean updateBrand(int id, String name, String category) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_BRAND_CATEGORY, category);
        return getWritableDatabase().update(TABLE_BRANDS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteBrand(int id) {
        return getWritableDatabase().delete(TABLE_BRANDS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getBrandsByCategory(String category) {
        return getReadableDatabase().query(TABLE_BRANDS, null, COL_BRAND_CATEGORY + " = ?", new String[]{category}, null, null, COL_NAME + " ASC");
    }

    public Cursor getAllBrands() {
        return getReadableDatabase().query(TABLE_BRANDS, null, null, null, null, null, COL_NAME + " ASC");
    }

    public boolean addModel(int brandId, String name) {
        ContentValues values = new ContentValues();
        values.put(COL_MODEL_BRAND_ID, brandId);
        values.put(COL_NAME, name);
        return getWritableDatabase().insert(TABLE_MODELS, null, values) != -1;
    }

    public boolean updateModel(int id, int brandId, String name) {
        ContentValues values = new ContentValues();
        values.put(COL_MODEL_BRAND_ID, brandId);
        values.put(COL_NAME, name);
        return getWritableDatabase().update(TABLE_MODELS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteModel(int id) {
        return getWritableDatabase().delete(TABLE_MODELS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getModelsByBrand(int brandId) {
        return getReadableDatabase().query(TABLE_MODELS, null, COL_MODEL_BRAND_ID + " = ?", new String[]{String.valueOf(brandId)}, null, null, COL_NAME + " ASC");
    }

    public boolean addQuality(String name) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        return getWritableDatabase().insert(TABLE_QUALITIES, null, values) != -1;
    }

    public boolean updateQuality(int id, String name) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        return getWritableDatabase().update(TABLE_QUALITIES, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteQuality(int id) {
        return getWritableDatabase().delete(TABLE_QUALITIES, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllQualities() {
        return getReadableDatabase().query(TABLE_QUALITIES, null, null, null, null, null, COL_NAME + " ASC");
    }

    // ==========================================
    // SERVICE CATEGORIES
    // ==========================================

    public Cursor getAllServiceCategories() {
        return getReadableDatabase().query(TABLE_SERVICE_CATEGORIES, null, null, null, null, null, COL_NAME + " ASC");
    }

    public boolean addServiceCategory(String name) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        return getWritableDatabase().insert(TABLE_SERVICE_CATEGORIES, null, v) != -1;
    }

    public boolean updateServiceCategory(int id, String name) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        return getWritableDatabase().update(TABLE_SERVICE_CATEGORIES, v, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteServiceCategory(int id) {
        return getWritableDatabase().delete(TABLE_SERVICE_CATEGORIES, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // ==========================================
    // AVAILABILITY METHODS
    // ==========================================

    public boolean setTechAvailability(int techId, String date, boolean available) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AVAIL_TECH_ID, techId);
        values.put(COL_AVAIL_DATE, date);
        values.put(COL_AVAIL_STATUS, available ? 1 : 0);

        int rows = db.update(TABLE_TECH_AVAILABILITY, values, 
                COL_AVAIL_TECH_ID + "=? AND " + COL_AVAIL_DATE + "=?", 
                new String[]{String.valueOf(techId), date});
        
        if (rows == 0) {
            return db.insert(TABLE_TECH_AVAILABILITY, null, values) != -1;
        }
        return true;
    }

    public boolean isTechAvailable(int techId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TECH_AVAILABILITY, new String[]{COL_AVAIL_STATUS},
                COL_AVAIL_TECH_ID + "=? AND " + COL_AVAIL_DATE + "=?",
                new String[]{String.valueOf(techId), date}, null, null, null);
        
        boolean available = true; 
        if (cursor != null && cursor.moveToFirst()) {
            available = cursor.getInt(0) == 1;
            cursor.close();
        }
        return available;
    }

    public int getAvailableTechCount(String branchName, String date) {
        List<Technician> branchTechs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TECHNICIANS, null, COL_TECHNICIAN_BRANCH + "=?", 
                new String[]{branchName}, null, null, null);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                branchTechs.add(new Technician(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_BRANCH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_STATUS))
                ));
            }
            cursor.close();
        }

        int count = 0;
        for (Technician t : branchTechs) {
            if (isTechAvailable(t.getId(), date)) {
                count++;
            }
        }
        return count;
    }

    public int getBookingCount(String branchName, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS + 
                " WHERE " + COL_BOOKING_BRANCH_NAME + "=? AND " + COL_BOOKING_DATE + "=?",
                new String[]{branchName, date});
        
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public List<Technician> getAvailableTechsForBranch(String branchName, String date) {
        List<Technician> allBranchTechs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TECHNICIANS, null, COL_TECHNICIAN_BRANCH + "=?", 
                new String[]{branchName}, null, null, null);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                allBranchTechs.add(new Technician(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_BRANCH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TECHNICIAN_STATUS))
                ));
            }
            cursor.close();
        }

        List<Technician> availableTechs = new ArrayList<>();
        for (Technician t : allBranchTechs) {
            if (isTechAvailable(t.getId(), date)) {
                availableTechs.add(t);
            }
        }
        return availableTechs;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
                ));
            }
        }
        return users;
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PAYMENTS, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                payments.add(new Payment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_PAYMENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAYMENT_BOOKING_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PAYMENT_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_METHOD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_CARD_NUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_EXPIRY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_CVV)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PAYMENT_DATE))
                ));
            }
        }
        return payments;
    }

    public List<TechAvailability> getAllAvailability() {
        List<TechAvailability> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_TECH_AVAILABILITY, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                list.add(new TechAvailability(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_AVAIL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_AVAIL_TECH_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAIL_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_AVAIL_STATUS))
                ));
            }
        }
        return list;
    }
}
