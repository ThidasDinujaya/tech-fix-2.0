package com.example.techfix.common.sync;

import android.content.Context;
import android.util.Log;

import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.branches.data.Technician;
import com.example.techfix.common.data.DatabaseHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

/**
 * Handles synchronization between local SQLite and centralized Firebase Firestore.
 */
public class FirebaseSyncRepository {
    private static final String TAG = "FirebaseSyncRepository";
    private final FirebaseFirestore firestore;
    private final BookingRepository bookingRepo;
    private final DatabaseHelper dbHelper;

    public FirebaseSyncRepository(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.bookingRepo = BookingRepository.getInstance(context);
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Pushes all local bookings and technicians to Firebase.
     */
    public void pushAllDataToFirebase() {
        // Push Bookings
        List<Booking> localBookings = bookingRepo.getAllBookings();
        for (Booking booking : localBookings) {
            syncBooking(booking);
        }

        // Push Technicians
        List<Technician> localTechs = dbHelper.getAllTechnicians();
        for (Technician tech : localTechs) {
            syncTechnician(tech);
        }
    }

    /**
     * Syncs a single booking to Firebase.
     */
    public void syncBooking(Booking booking) {
        firestore.collection("bookings")
                .document(String.valueOf(booking.getId()))
                .set(booking)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking synced: " + booking.getId()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing booking " + booking.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                });
    }

    /**
     * Syncs technician data to Firebase.
     */
    public void syncTechnician(Technician technician) {
        firestore.collection("technicians")
                .document(String.valueOf(technician.getId()))
                .set(technician)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Tech synced: " + technician.getName()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing tech " + technician.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                });
    }

    /**
     * Pulls latest bookings from Firebase.
     */
    public void pullBookings() {
        firestore.collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        Log.d(TAG, "Fetched booking from Firebase: " + booking.getId());
                    }
                });
    }
}
