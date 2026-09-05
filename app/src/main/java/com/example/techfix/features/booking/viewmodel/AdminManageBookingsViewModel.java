package com.example.techfix.features.booking.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.booking.data.BookingStatus;
import java.util.List;

// Manages data loading and filtering logic for the Admin booking dashboard
public class AdminManageBookingsViewModel extends AndroidViewModel {

    private final BookingRepository repository;
    private final MutableLiveData<List<Booking>> bookings = new MutableLiveData<>();

    public AdminManageBookingsViewModel(@NonNull Application application) {
        super(application);
        repository = BookingRepository.getInstance(application);
    }

    // Loads bookings from the database based on the selected filter
    public void fetchBookings(String filter) {
        new Thread(() -> {
            if (filter.equals("All")) {
                bookings.postValue(repository.getAllBookings());
            } else if (filter.equals("Pending")) {
                bookings.postValue(repository.getBookingsByStatus(BookingStatus.PENDING));
            } else if (filter.equals("Completed")) {
                bookings.postValue(repository.getBookingsByStatus(BookingStatus.COMPLETED));
            }
        }).start();
    }

    public void updateBookingStatus(int bookingId, String status, String currentFilter) {
        new Thread(() -> {
            if (repository.updateBookingStatus(bookingId, status)) {
                fetchBookings(currentFilter);
            }
        }).start();
    }

    public void deleteBooking(int bookingId, String currentFilter) {
        new Thread(() -> {
            if (repository.deleteBooking(bookingId)) {
                fetchBookings(currentFilter);
            }
        }).start();
    }

    public LiveData<List<Booking>> getBookings() {
        return bookings;
    }
}
