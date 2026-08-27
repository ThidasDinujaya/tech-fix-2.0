package com.example.management_new.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.management_new.model.Branch;
import com.example.management_new.model.SparePart;
import com.example.management_new.model.Technician;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFix.db";
    // Upgraded version to 2 to automatically recreate the DB with 5 branches
    private static final int DATABASE_VERSION = 2;

    // Table Names
    public static final String TABLE_BRANCHES = "branches";
    public static final String TABLE_TECHNICIANS = "technicians";
    public static final String TABLE_SPARE_PARTS = "spare_parts";

    // Common Columns
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    // Branch Columns
    public static final String KEY_BRANCH_ADDRESS = "address";
    public static final String KEY_BRANCH_PHONE = "phone";
    public static final String KEY_BRANCH_HOURS = "hours";
    public static final String KEY_BRANCH_LAT = "latitude";
    public static final String KEY_BRANCH_LNG = "longitude";

    // Technician Columns
    public static final String KEY_TECH_ROLE = "role";
    public static final String KEY_TECH_BRANCH = "branch_name";
    public static final String KEY_TECH_STATUS = "status";

    // Spare Part Columns
    public static final String KEY_PART_STOCK = "stock";
    public static final String KEY_PART_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BRANCHES_TABLE = "CREATE TABLE " + TABLE_BRANCHES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_BRANCH_ADDRESS + " TEXT,"
                + KEY_BRANCH_PHONE + " TEXT,"
                + KEY_BRANCH_HOURS + " TEXT,"
                + KEY_BRANCH_LAT + " REAL,"
                + KEY_BRANCH_LNG + " REAL" + ")";

        String CREATE_TECHNICIANS_TABLE = "CREATE TABLE " + TABLE_TECHNICIANS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_TECH_ROLE + " TEXT,"
                + KEY_TECH_BRANCH + " TEXT,"
                + KEY_TECH_STATUS + " TEXT" + ")";

        String CREATE_SPARE_PARTS_TABLE = "CREATE TABLE " + TABLE_SPARE_PARTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PART_STOCK + " INTEGER,"
                + KEY_PART_PRICE + " REAL" + ")";

        db.execSQL(CREATE_BRANCHES_TABLE);
        db.execSQL(CREATE_TECHNICIANS_TABLE);
        db.execSQL(CREATE_SPARE_PARTS_TABLE);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECHNICIANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPARE_PARTS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed Initial 5 Branches
        db.execSQL("INSERT INTO " + TABLE_BRANCHES + " (name, address, phone, hours, latitude, longitude) VALUES " +
                "('Colombo Branch', '123, Galle Road, Colombo 03', '011 234 5678', 'Open: 9:00 AM - 7:00 PM', 6.9271, 79.8612)," +
                "('Galle Branch', '45, Main Street, Galle', '091 234 5678', 'Open: 9:00 AM - 6:00 PM', 6.0535, 80.2210)," +
                "('Kandy Branch', '78, Dalada Veediya, Kandy', '081 234 5678', 'Open: 9:00 AM - 6:30 PM', 7.2906, 80.6337)," +
                "('Negombo Branch', '12, Main Street, Negombo', '031 234 5678', 'Open: 9:00 AM - 6:00 PM', 7.2008, 79.8737)," +
                "('Kurunegala Branch', '56, Colombo Road, Kurunegala', '037 234 5678', 'Open: 9:00 AM - 6:00 PM', 7.4863, 80.3647)");

        // Seed Initial Technicians for Branches
        db.execSQL("INSERT INTO " + TABLE_TECHNICIANS + " (name, role, branch_name, status) VALUES " +
                "('John Perera', 'Mobile Repair Expert', 'Colombo Branch', 'Available')," +
                "('Saman Kumara', 'Laptop Repair Expert', 'Galle Branch', 'Busy')," +
                "('Kasun Fernando', 'Hardware Specialist', 'Kandy Branch', 'Available')," +
                "('Nimal De Silva', 'Software Specialist', 'Negombo Branch', 'Available')," +
                "('Ruwan Jayasinghe', 'Screen Specialist', 'Kurunegala Branch', 'Available')");

        // Seed Initial Spare Parts
        db.execSQL("INSERT INTO " + TABLE_SPARE_PARTS + " (name, stock, price) VALUES " +
                "('Screen (Samsung A52)', 15, 2500.00)," +
                "('Battery (Redmi Note 10)', 20, 2000.00)," +
                "('Charging Port (Type-C)', 30, 1200.00)," +
                "('Camera Module', 10, 1800.00)");
    }

    // --- BRANCH OPERATIONS ---
    public List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BRANCHES, null);

        if (cursor.moveToFirst()) {
            do {
                Branch branch = new Branch(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRANCH_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRANCH_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_BRANCH_HOURS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_BRANCH_LAT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_BRANCH_LNG))
                );
                list.add(branch);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- TECHNICIAN OPERATIONS ---
    public List<Technician> getAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TECHNICIANS, null);

        if (cursor.moveToFirst()) {
            do {
                Technician tech = new Technician(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TECH_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TECH_BRANCH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TECH_STATUS))
                );
                list.add(tech);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean addTechnician(String name, String role, String branch, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TECH_ROLE, role);
        values.put(KEY_TECH_BRANCH, branch);
        values.put(KEY_TECH_STATUS, status);

        long result = db.insert(TABLE_TECHNICIANS, null, values);
        return result != -1;
    }

    // --- SPARE PART OPERATIONS ---
    public List<SparePart> getAllSpareParts() {
        List<SparePart> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SPARE_PARTS, null);

        if (cursor.moveToFirst()) {
            do {
                SparePart part = new SparePart(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PART_STOCK)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PART_PRICE))
                );
                list.add(part);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}