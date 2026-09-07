package com.example.techfix.common.sync;

import android.content.Context;
import android.util.Log;
import android.database.Cursor;
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
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.common.data.DatabaseHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles synchronization between local SQLite and centralized Firebase Firestore.
 */
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

    /**
     * Pushes all local data to Firebase Cloud automatically using matching names.
     */
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
}
