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
    private static final int DATABASE_VERSION = 21;

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
    
    public static final String TABLE_BRANDS = "brands";
    public static final String TABLE_MODELS = "models";
    public static final String TABLE_QUALITIES = "qualities";

    public static final String COL_BRANCH_ADDRESS = "address";
    public static final String COL_BRANCH_HOURS = "hours";
    public static final String COL_BRANCH_PHONE2 = "phone2";
    public static final String COL_BRANCH_HOURS_MON_FRI = "hours_mon_fri";
    public static final String COL_BRANCH_HOURS_SAT = "hours_sat";
    public static final String COL_BRANCH_HOURS_SUN = "hours_sun";
    public static final String COL_BRANCH_MAP_LINK = "map_link";

    public static final String COL_TECHNICIAN_ROLE = "role";
    public static final String COL_TECHNICIAN_BRANCH = "branch_name";
    public static final String COL_TECHNICIAN_STATUS = "status";

    public static final String COL_SPARE_PART_STOCK = "stock";
    public static final String COL_SPARE_PART_PRICE = "price";
    
    public static final String COL_BRAND_CATEGORY = "category";
    public static final String COL_MODEL_BRAND_ID = "brand_id";
    public static final String COL_QUALITY_MULTIPLIER = "multiplier";

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
                COL_SERVICE_IMAGE + " TEXT, " +
                COL_SERVICE_CATEGORY + " TEXT)";

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
        createDeviceManagementTables(db);
        populateInitialData(db);
    }

    private void populateInitialData(SQLiteDatabase db) {
        // Populate Branches
        insertBranch(db, "Colombo Main", "123 Galle Road, Colombo 03", "0112345678", "0112345679", 
                     "08:00 AM - 06:00 PM", "09:00 AM - 04:00 PM", "Closed", "https://maps.google.com/?q=6.9271,79.8612");
        insertBranch(db, "Kandy Branch", "45 Dalada Veediya, Kandy", "0812345678", "", 
                     "08:30 AM - 05:30 PM", "08:30 AM - 01:00 PM", "Closed", "https://maps.google.com/?q=7.2906,80.6337");
        insertBranch(db, "Galle Fort", "12 Church Street, Galle", "0912345678", "", 
                     "09:00 AM - 05:00 PM", "09:00 AM - 12:00 PM", "Closed", "https://maps.google.com/?q=6.0367,80.2170");

        // Populate Technicians
        insertTechnician(db, "Kasun Perera", "Senior Technician", "Colombo Main", "Available");
        insertTechnician(db, "Amara Silva", "Mobile Expert", "Kandy Branch", "Busy");

        // Populate Spare Parts
        insertSparePart(db, "iPhone 13 Screen", 50, 15000.0);
        insertSparePart(db, "Samsung S22 Battery", 30, 8500.0);
        
        // Populate Brands & Models
        long appleId = insertBrand(db, "Apple", "Phone");
        insertModel(db, appleId, "iPhone 15 Pro");
        insertModel(db, appleId, "iPhone 14");

        long samsungId = insertBrand(db, "Samsung", "Phone");
        insertModel(db, samsungId, "Galaxy S23 Ultra");
        insertModel(db, samsungId, "Galaxy S22");

        // Populate Qualities
        insertQuality(db, "Original", 1.5);
        insertQuality(db, "Grade A", 1.2);
        insertQuality(db, "Grade B", 1.0);
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

    private void insertQuality(SQLiteDatabase db, String name, double multiplier) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_QUALITY_MULTIPLIER, multiplier);
        db.insert(TABLE_QUALITIES, null, v);
    }

    private void insertBranch(SQLiteDatabase db, String name, String addr, String phone, String phone2,
                              String hMonFri, String hSat, String hSun, String mapLink) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, addr);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
        v.put(COL_BRANCH_MAP_LINK, mapLink);
        v.put(COL_BRANCH_HOURS, "Mon-Fri: " + hMonFri); // summary
        db.insert(TABLE_BRANCHES, null, v);
    }

    private void insertTechnician(SQLiteDatabase db, String name, String role, String branch, String status) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_TECHNICIAN_ROLE, role);
        v.put(COL_TECHNICIAN_BRANCH, branch);
        v.put(COL_TECHNICIAN_STATUS, status);
        db.insert(TABLE_TECHNICIANS, null, v);
    }

    private void insertSparePart(SQLiteDatabase db, String name, int stock, double price) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_SPARE_PART_STOCK, stock);
        v.put(COL_SPARE_PART_PRICE, price);
        db.insert(TABLE_SPARE_PARTS, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void createBranchManagementTables(SQLiteDatabase db) {
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
                + COL_BRANCH_MAP_LINK + " TEXT)");

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
    
    private void createDeviceManagementTables(SQLiteDatabase db) {
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
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUALITY_MULTIPLIER + " REAL NOT NULL)");
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
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_PHONE2)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_MON_FRI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_SAT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_HOURS_SUN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BRANCH_MAP_LINK))));
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

    public boolean updateTechnician(int id, String name, String role, String branch, String status) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_TECHNICIAN_ROLE, role);
        values.put(COL_TECHNICIAN_BRANCH, branch);
        values.put(COL_TECHNICIAN_STATUS, status);
        return getWritableDatabase().update(TABLE_TECHNICIANS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteTechnician(int id) {
        return getWritableDatabase().delete(TABLE_TECHNICIANS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
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

    public boolean addSparePart(String name, int stock, double price) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SPARE_PART_STOCK, stock);
        values.put(COL_SPARE_PART_PRICE, price);
        return getWritableDatabase().insert(TABLE_SPARE_PARTS, null, values) != -1;
    }

    public boolean updateSparePart(int id, String name, int stock, double price) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SPARE_PART_STOCK, stock);
        values.put(COL_SPARE_PART_PRICE, price);
        return getWritableDatabase().update(TABLE_SPARE_PARTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteSparePart(int id) {
        return getWritableDatabase().delete(TABLE_SPARE_PARTS, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean addBranch(String name, String address, String phone, String phone2, 
                             String hMonFri, String hSat, String hSun, String mapLink) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, address);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
        v.put(COL_BRANCH_MAP_LINK, mapLink);
        v.put(COL_BRANCH_HOURS, "Mon-Fri: " + hMonFri);
        return getWritableDatabase().insert(TABLE_BRANCHES, null, v) != -1;
    }

    public boolean updateBranch(int id, String name, String address, String phone, String phone2,
                                String hMonFri, String hSat, String hSun, String mapLink) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_BRANCH_ADDRESS, address);
        v.put(COL_PHONE, phone);
        v.put(COL_BRANCH_PHONE2, phone2);
        v.put(COL_BRANCH_HOURS_MON_FRI, hMonFri);
        v.put(COL_BRANCH_HOURS_SAT, hSat);
        v.put(COL_BRANCH_HOURS_SUN, hSun);
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

    public boolean addQuality(String name, double multiplier) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUALITY_MULTIPLIER, multiplier);
        return getWritableDatabase().insert(TABLE_QUALITIES, null, values) != -1;
    }

    public boolean updateQuality(int id, String name, double multiplier) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUALITY_MULTIPLIER, multiplier);
        return getWritableDatabase().update(TABLE_QUALITIES, values, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteQuality(int id) {
        return getWritableDatabase().delete(TABLE_QUALITIES, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllQualities() {
        return getReadableDatabase().query(TABLE_QUALITIES, null, null, null, null, null, COL_NAME + " ASC");
    }
}
