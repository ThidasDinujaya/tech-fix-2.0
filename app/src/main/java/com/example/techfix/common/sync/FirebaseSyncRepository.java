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
     * Pushes all local bookings to Firebase.
     */
    public void pushBookingsToFirebase() {
        List<Booking> localBookings = bookingRepo.getAllBookings();
        for (Booking booking : localBookings) {
            syncBooking(booking);
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
                .addOnFailureListener(e -> Log.e(TAG, "Error syncing booking", e));
    }

    /**
     * Syncs technician data to Firebase.
     */
    public void syncTechnician(Technician technician) {
        firestore.collection("technicians")
                .document(String.valueOf(technician.getId()))
                .set(technician)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Tech synced: " + technician.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error syncing tech", e));
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
                        // Here you would logic to update local SQLite if it's newer
                        Log.d(TAG, "Fetched booking from Firebase: " + booking.getId());
                    }
                });
    }
}
