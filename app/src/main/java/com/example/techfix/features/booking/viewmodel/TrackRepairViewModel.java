package com.example.techfix.features.booking.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;

public class TrackRepairViewModel extends AndroidViewModel {
    private final BookingRepository repository;
    private final MutableLiveData<Booking> bookingDetails = new MutableLiveData<>();

    public TrackRepairViewModel(@NonNull Application application) {
        super(application);
        repository = BookingRepository.getInstance(application);
    }

    public void loadBooking(int bookingId) {
        new Thread(() -> {
            Booking booking = repository.getBookingById(bookingId);
            bookingDetails.postValue(booking);
        }).start();
    }

    public LiveData<Booking> getBookingDetails() { return bookingDetails; }
}
