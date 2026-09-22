package com.example.techfix.common.sync;

import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.Technician;
import com.example.techfix.features.branches.data.SparePart;
import com.example.techfix.features.branches.data.TechAvailability;
import com.example.techfix.features.admin.data.Brand;
import com.example.techfix.features.admin.data.DeviceModel;
import com.example.techfix.features.admin.data.ServiceCategory;
import com.example.techfix.features.admin.data.PartQuality;
import com.example.techfix.features.auth.data.User;
import com.example.techfix.features.booking.data.Review;
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.common.data.DatabaseHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Handles synchronization between local SQLite and centralized Firebase Firestore.
public class FirebaseSyncRepository {
    private static final String TAG = "FirebaseSyncRepository";
    private final FirebaseFirestore firestore;
    private final BookingRepository bookingRepo;
    private final DatabaseHelper dbHelper;
    private final ServiceRepository serviceRepo;
    private final Context context;

    public FirebaseSyncRepository(Context context) {
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.bookingRepo = BookingRepository.getInstance(context);
        this.dbHelper = new DatabaseHelper(context);
        this.serviceRepo = ServiceRepository.getInstance(context);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    // Pushes all local data to Firebase Cloud automatically using matching names.
    public void pushAllDataToFirebase() {
        Log.d(TAG, "Starting full automatic sync to Cloud...");
        
        // Connection Heartbeat
        Map<String, Object> hb = new HashMap<>();
        hb.put("lastSync", Timestamp.now());
        hb.put("status", "Active");
        firestore.collection("system").document("heartbeat").set(hb, SetOptions.merge());

        // 1. users
        List<User> users = dbHelper.getAllUsers();
        for (User u : users) syncUser(u);

        // 2. services
        List<Service> services = serviceRepo.getAllServices();
        for (Service s : services) syncService(s);

        // 3. bookings
        List<Booking> bookings = bookingRepo.getAllBookings();
        for (Booking b : bookings) syncBooking(b);

        // 4. payments
        List<Payment> payments = dbHelper.getAllPayments();
        for (Payment p : payments) syncPayment(p);

        // 5. branches
        List<Branch> branches = dbHelper.getAllBranches();
        for (Branch br : branches) syncBranch(br);

        // 6. technicians
        List<Technician> techs = dbHelper.getAllTechnicians();
        for (Technician t : techs) syncTechnician(t);

        // 7. spare_parts
        List<SparePart> parts = dbHelper.getAllSpareParts();
        for (SparePart sp : parts) syncSparePart(sp);

        // 8. technician_availability
        List<TechAvailability> avail = dbHelper.getAllAvailability();
        for (TechAvailability a : avail) syncAvailability(a);

        // Sync Inventory Metadata
        syncMetadata();
    }

    private void syncMetadata() {
        // 9. brands
        try (Cursor c = dbHelper.getAllBrands()) {
            if (c != null) {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    String cat = c.getString(c.getColumnIndexOrThrow("category"));
                    syncBrand(new Brand(id, name, cat));
                }
            }
        } catch (Exception e) { Log.e(TAG, "Sync Error (brands): " + e.getMessage()); }

        // 10. models
        try (Cursor c = dbHelper.getAllBrands()) {
            if (c != null) {
                while (c.moveToNext()) {
                    int bId = c.getInt(c.getColumnIndexOrThrow("id"));
                    String bName = c.getString(c.getColumnIndexOrThrow("name"));
                    try (Cursor mc = dbHelper.getModelsByBrand(bId)) {
                        if (mc != null) {
                            while (mc.moveToNext()) {
                                int id = mc.getInt(mc.getColumnIndexOrThrow("id"));
                                String name = mc.getString(mc.getColumnIndexOrThrow("name"));
                                syncModel(new DeviceModel(id, bId, name, bName));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) { Log.e(TAG, "Sync Error (models): " + e.getMessage()); }

        // 11. qualities
        try (Cursor c = dbHelper.getAllQualities()) {
            if (c != null) {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    syncQuality(new PartQuality(id, name));
                }
            }
        } catch (Exception e) { Log.e(TAG, "Sync Error (qualities): " + e.getMessage()); }

        // 12. service_categories
        try (Cursor c = dbHelper.getAllServiceCategories()) {
            if (c != null) {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow("id"));
                    String name = c.getString(c.getColumnIndexOrThrow("name"));
                    syncServiceCategory(new ServiceCategory(id, name));
                }
            }
        } catch (Exception e) { Log.e(TAG, "Sync Error (service_categories): " + e.getMessage()); }
    }

    public void syncUser(User u) {
        firestore.collection("users").document(String.valueOf(u.getId())).set(u, SetOptions.merge());
    }

    public void syncService(Service s) {
        firestore.collection("services").document(String.valueOf(s.getId())).set(s, SetOptions.merge());
    }

    public void deleteService(int serviceId) {
        firestore.collection("services").document(String.valueOf(serviceId)).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Service deleted from Firebase: " + serviceId))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting service from Firebase: " + serviceId, e));
    }

    public void syncBooking(Booking b) {
        firestore.collection("bookings").document(String.valueOf(b.getId())).set(b, SetOptions.merge());
    }

    public void syncPayment(Payment p) {
        firestore.collection("payments").document(String.valueOf(p.getId())).set(p, SetOptions.merge());
    }

    public void syncBranch(Branch br) {
        firestore.collection("branches").document(String.valueOf(br.getId())).set(br, SetOptions.merge());
    }

    public void syncTechnician(Technician t) {
        firestore.collection("technicians").document(String.valueOf(t.getId())).set(t, SetOptions.merge());
    }

    public void syncSparePart(SparePart sp) {
        firestore.collection("spare_parts").document(String.valueOf(sp.getId())).set(sp, SetOptions.merge());
    }

    public void deleteSparePart(int partId) {
        firestore.collection("spare_parts").document(String.valueOf(partId)).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Spare part deleted from Firebase: " + partId))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting spare part from Firebase: " + partId, e));
    }

    public void syncAvailability(TechAvailability a) {
        firestore.collection("technician_availability").document(String.valueOf(a.getId())).set(a, SetOptions.merge());
    }

    public void syncBrand(Brand b) {
        firestore.collection("brands").document(String.valueOf(b.getId())).set(b, SetOptions.merge());
    }

    public void syncModel(DeviceModel m) {
        firestore.collection("models").document(String.valueOf(m.getId())).set(m, SetOptions.merge());
    }

    public void syncQuality(PartQuality q) {
        firestore.collection("qualities").document(String.valueOf(q.getId())).set(q, SetOptions.merge());
    }

    public void syncServiceCategory(ServiceCategory sc) {
        firestore.collection("service_categories").document(String.valueOf(sc.getId())).set(sc, SetOptions.merge());
    }

    public void syncReview(Review r) {
        firestore.collection("reviews").document(String.valueOf(r.getId())).set(r, SetOptions.merge());
    }

    // PULL GENERAL DATA (For Customers & Admins)
    public void pullGeneralData(OnSyncCompleteListener listener) {
        firestore.collection("services").get().addOnSuccessListener(services -> {
            List<Service> serviceList = new ArrayList<>();
            for (DocumentSnapshot d : services) {
                Service s = d.toObject(Service.class);
                if (s != null) serviceList.add(s);
            }
            syncServicesLocally(serviceList);
            
            firestore.collection("branches").get().addOnSuccessListener(branches -> {
                for (DocumentSnapshot d : branches) {
                    Branch br = d.toObject(Branch.class);
                    if (br != null) insertBranchLocally(br);
                }
                
                firestore.collection("technicians").get().addOnSuccessListener(techs -> {
                    for (DocumentSnapshot d : techs) {
                        Technician t = d.toObject(Technician.class);
                        if (t != null) insertTechLocally(t);
                    }
                    
                    pullMetadata(listener);
                });
            });
        }).addOnFailureListener(e -> listener.onSyncComplete(false));
    }

    // PULL ALL DATA (For Admin)
    public void pullAllDataFromFirebase(OnSyncCompleteListener listener) {
        Log.d(TAG, "Pulling all data from Firebase...");
        
        // Use a counter or chain them. For simplicity, we'll chain some or use a common success listener if we had many.
        // Chaining: Users -> Services -> Bookings -> Payments -> Branches -> Techs -> Parts -> Avail -> Metadata
        
        firestore.collection("users").get().addOnSuccessListener(users -> {
            for (DocumentSnapshot d : users) {
                User u = d.toObject(User.class);
                if (u != null) dbHelper.insertFullUser(u);
            }
            
            firestore.collection("services").get().addOnSuccessListener(services -> {
                List<Service> serviceList = new ArrayList<>();
                for (DocumentSnapshot d : services) {
                    Service s = d.toObject(Service.class);
                    if (s != null) serviceList.add(s);
                }
                syncServicesLocally(serviceList);
                
                firestore.collection("bookings").get().addOnSuccessListener(bookings -> {
                    for (DocumentSnapshot d : bookings) {
                        Booking b = d.toObject(Booking.class);
                        if (b != null) bookingRepo.addBookingLocally(b);
                    }
                    
                    firestore.collection("payments").get().addOnSuccessListener(payments -> {
                        for (DocumentSnapshot d : payments) {
                            Payment p = d.toObject(Payment.class);
                            if (p != null) insertPaymentLocally(p);
                        }
                        
                        firestore.collection("reviews").get().addOnSuccessListener(reviews -> {
                            for (DocumentSnapshot d : reviews) {
                                Review r = d.toObject(Review.class);
                                if (r != null) insertReviewLocally(r);
                            }
                            
                            // Continue for others...
                            pullBranchesAndTechs(listener);
                        });
                    });
                });
            });
        }).addOnFailureListener(e -> listener.onSyncComplete(false));
    }

    private void pullBranchesAndTechs(OnSyncCompleteListener listener) {
        firestore.collection("branches").get().addOnSuccessListener(branches -> {
            for (DocumentSnapshot d : branches) {
                Branch br = d.toObject(Branch.class);
                if (br != null) insertBranchLocally(br);
            }
            
            firestore.collection("technicians").get().addOnSuccessListener(techs -> {
                for (DocumentSnapshot d : techs) {
                    Technician t = d.toObject(Technician.class);
                    if (t != null) insertTechLocally(t);
                }
                
                firestore.collection("spare_parts").get().addOnSuccessListener(parts -> {
                    List<SparePart> remoteList = new ArrayList<>();
                    for (DocumentSnapshot d : parts) {
                        SparePart sp = d.toObject(SparePart.class);
                        if (sp != null) remoteList.add(sp);
                    }
                    syncSparePartsLocally(remoteList);
                    
                    firestore.collection("technician_availability").get().addOnSuccessListener(avail -> {
                        for (DocumentSnapshot d : avail) {
                            TechAvailability a = d.toObject(TechAvailability.class);
                            if (a != null) insertAvailabilityLocally(a);
                        }
                        pullMetadata(listener);
                    });
                });
            });
        });
    }

    private void pullMetadata(OnSyncCompleteListener listener) {
        firestore.collection("brands").get().addOnSuccessListener(brands -> {
            for (DocumentSnapshot d : brands) {
                Brand b = d.toObject(Brand.class);
                if (b != null) dbHelper.syncBrand(b);
            }
            
            firestore.collection("models").get().addOnSuccessListener(models -> {
                for (DocumentSnapshot d : models) {
                    DeviceModel m = d.toObject(DeviceModel.class);
                    if (m != null) dbHelper.syncModel(m);
                }

                firestore.collection("qualities").get().addOnSuccessListener(qualities -> {
                    for (DocumentSnapshot d : qualities) {
                        PartQuality q = d.toObject(PartQuality.class);
                        if (q != null) dbHelper.syncQuality(q);
                    }

                    firestore.collection("service_categories").get().addOnSuccessListener(cats -> {
                        for (DocumentSnapshot d : cats) {
                            ServiceCategory sc = d.toObject(ServiceCategory.class);
                            if (sc != null) dbHelper.syncServiceCategory(sc);
                        }
                        listener.onSyncComplete(true);
                    });
                });
            });
        });
    }

    private void syncServicesLocally(List<Service> remoteServices) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            List<Integer> remoteIds = new ArrayList<>();
            for (Service s : remoteServices) {
                remoteIds.add(s.getId());
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_SERVICE_ID, s.getId());
                v.put(DatabaseHelper.COL_SERVICE_NAME, s.getName());
                v.put(DatabaseHelper.COL_SERVICE_DESC, s.getDescription());
                v.put(DatabaseHelper.COL_SERVICE_PRICE, s.getPrice());
                v.put(DatabaseHelper.COL_SERVICE_WARRANTY, s.getWarranty());
                v.put(DatabaseHelper.COL_SERVICE_IMAGE, s.getImageUrl());
                v.put(DatabaseHelper.COL_SERVICE_CATEGORY, s.getCategory());
                v.put(DatabaseHelper.COL_SERVICE_BRAND, s.getBrand());
                v.put(DatabaseHelper.COL_SERVICE_MODEL, s.getModel());
                v.put(DatabaseHelper.COL_SERVICE_QUALITY, s.getQuality());
                v.put(DatabaseHelper.COL_SERVICE_PART_ID, s.getSparePartId());
                db.insertWithOnConflict(DatabaseHelper.TABLE_SERVICES, null, v, SQLiteDatabase.CONFLICT_REPLACE);
            }

            if (remoteIds.isEmpty()) {
                db.delete(DatabaseHelper.TABLE_SERVICES, null, null);
            } else {
                StringBuilder where = new StringBuilder(DatabaseHelper.COL_SERVICE_ID + " NOT IN (");
                for (int i = 0; i < remoteIds.size(); i++) {
                    where.append(remoteIds.get(i));
                    if (i < remoteIds.size() - 1) where.append(",");
                }
                where.append(")");
                db.delete(DatabaseHelper.TABLE_SERVICES, where.toString(), null);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error syncing services locally", e);
        } finally {
            db.endTransaction();
        }
    }

    private void insertBranchLocally(Branch br) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_ID, br.getId());
        v.put(DatabaseHelper.COL_NAME, br.getName());
        v.put(DatabaseHelper.COL_BRANCH_ADDRESS, br.getAddress());
        v.put(DatabaseHelper.COL_PHONE, br.getPhone());
        v.put(DatabaseHelper.COL_BRANCH_PHONE2, br.getPhone2());
        v.put(DatabaseHelper.COL_BRANCH_HOURS_MON_FRI, br.getHoursMonFri());
        v.put(DatabaseHelper.COL_BRANCH_HOURS_SAT, br.getHoursSat());
        v.put(DatabaseHelper.COL_BRANCH_HOURS_SUN, br.getHoursSun());
        v.put(DatabaseHelper.COL_BRANCH_MAP_LINK, br.getMapLink());
        v.put(DatabaseHelper.COL_BRANCH_LATITUDE, br.getLatitude());
        v.put(DatabaseHelper.COL_BRANCH_LONGITUDE, br.getLongitude());
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_BRANCHES, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void insertTechLocally(Technician t) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_ID, t.getId());
        v.put(DatabaseHelper.COL_NAME, t.getName());
        v.put(DatabaseHelper.COL_TECHNICIAN_BRANCH, t.getBranchName());
        v.put(DatabaseHelper.COL_TECHNICIAN_STATUS, t.getStatus());
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_TECHNICIANS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void syncSparePartsLocally(List<SparePart> remoteParts) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            List<Integer> remoteIds = new ArrayList<>();
            for (SparePart sp : remoteParts) {
                remoteIds.add(sp.getId());
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_ID, sp.getId());
                v.put(DatabaseHelper.COL_NAME, sp.getName());
                v.put(DatabaseHelper.COL_SPARE_PART_STOCK, sp.getStock());
                v.put(DatabaseHelper.COL_SPARE_PART_PRICE, sp.getPrice());
                v.put(DatabaseHelper.COL_SPARE_PART_BRAND, sp.getBrand());
                v.put(DatabaseHelper.COL_SPARE_PART_MODEL, sp.getModel());
                v.put(DatabaseHelper.COL_SPARE_PART_QUALITY, sp.getQuality());
                v.put(DatabaseHelper.COL_SPARE_PART_BRANCH, sp.getBranchName());
                db.insertWithOnConflict(DatabaseHelper.TABLE_SPARE_PARTS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
            }

            if (remoteIds.isEmpty()) {
                db.delete(DatabaseHelper.TABLE_SPARE_PARTS, null, null);
            } else {
                StringBuilder where = new StringBuilder(DatabaseHelper.COL_ID + " NOT IN (");
                for (int i = 0; i < remoteIds.size(); i++) {
                    where.append(remoteIds.get(i));
                    if (i < remoteIds.size() - 1) where.append(",");
                }
                where.append(")");
                db.delete(DatabaseHelper.TABLE_SPARE_PARTS, where.toString(), null);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error syncing spare parts locally", e);
        } finally {
            db.endTransaction();
        }
    }

    private void insertAvailabilityLocally(TechAvailability a) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_AVAIL_ID, a.getId());
        v.put(DatabaseHelper.COL_AVAIL_TECH_ID, a.getTechnicianId());
        v.put(DatabaseHelper.COL_AVAIL_DATE, a.getAvailableDate());
        v.put(DatabaseHelper.COL_AVAIL_STATUS, a.getIsAvailable());
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_TECH_AVAILABILITY, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Pull specific user data and their related records
    public void pullUserData(String email, OnSyncCompleteListener listener) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            dbHelper.insertFullUser(user);
                            // Also pull their bookings
                            pullUserBookings(user.getId(), listener);
                        } else {
                            listener.onSyncComplete(false);
                        }
                    } else {
                        listener.onSyncComplete(false);
                    }
                })
                .addOnFailureListener(e -> listener.onSyncComplete(false));
    }

    private void pullUserBookings(int userId, OnSyncCompleteListener listener) {
        firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Booking b = doc.toObject(Booking.class);
                        if (b != null) {
                            bookingRepo.addBookingLocally(b);
                            // Pull payment and review for this booking if they exist
                            pullBookingPayment(b.getId());
                            pullBookingReview(b.getId());
                        }
                    }
                    listener.onSyncComplete(true);
                })
                .addOnFailureListener(e -> listener.onSyncComplete(false));
    }

    private void pullBookingPayment(int bookingId) {
        firestore.collection("payments")
                .whereEqualTo("bookingId", bookingId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Payment p = queryDocumentSnapshots.getDocuments().get(0).toObject(Payment.class);
                        if (p != null) {
                            insertPaymentLocally(p);
                        }
                    }
                });
    }

    private void pullBookingReview(int bookingId) {
        firestore.collection("reviews")
                .whereEqualTo("bookingId", bookingId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Review r = queryDocumentSnapshots.getDocuments().get(0).toObject(Review.class);
                        if (r != null) {
                            insertReviewLocally(r);
                        }
                    }
                });
    }

    private void insertReviewLocally(Review r) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_REVIEW_ID, r.getId());
        v.put(DatabaseHelper.COL_REVIEW_BOOKING_ID, r.getBookingId());
        v.put(DatabaseHelper.COL_REVIEW_RATING, r.getRating());
        v.put(DatabaseHelper.COL_REVIEW_COMMENT, r.getComment());
        v.put(DatabaseHelper.COL_REVIEW_IMAGE, r.getImageUri());
        v.put(DatabaseHelper.COL_REVIEW_DATE, r.getReviewDate());
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_REVIEWS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void insertPaymentLocally(Payment p) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_ID_PAYMENT, p.getId());
        v.put(DatabaseHelper.COL_PAYMENT_BOOKING_ID, p.getBookingId());
        v.put(DatabaseHelper.COL_PAYMENT_AMOUNT, p.getAmount());
        v.put(DatabaseHelper.COL_PAYMENT_METHOD, p.getMethod());
        v.put(DatabaseHelper.COL_PAYMENT_CARD_NUM, p.getCardNumber());
        v.put(DatabaseHelper.COL_PAYMENT_EXPIRY, p.getExpiryDate());
        v.put(DatabaseHelper.COL_PAYMENT_CVV, p.getCvv());
        v.put(DatabaseHelper.COL_PAYMENT_DATE, p.getPaymentDate());
        dbHelper.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_PAYMENTS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public interface OnSyncCompleteListener {
        void onSyncComplete(boolean success);
    }
}
