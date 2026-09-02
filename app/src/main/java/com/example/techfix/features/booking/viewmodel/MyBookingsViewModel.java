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

public class MyBookingsViewModel extends AndroidViewModel {
    private final BookingRepository repository;
    private final MutableLiveData<List<Booking>> upcomingBookings = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> completedBookings = new MutableLiveData<>();

    public MyBookingsViewModel(@NonNull Application application) {
        super(application);
        repository = BookingRepository.getInstance(application);
    }

    public void loadBookings(int userId) {
        new Thread(() -> {
            String[] upcomingStatuses = {
                BookingStatus.PENDING,
                BookingStatus.ASSIGNED,
                BookingStatus.REPAIRING,
                BookingStatus.READY
            };
            List<Booking> upcoming = repository.getBookingsByUserAndStatus(userId, upcomingStatuses);
            upcomingBookings.postValue(upcoming);

            String[] completedStatuses = {BookingStatus.COMPLETED};
            List<Booking> completed = repository.getBookingsByUserAndStatus(userId, completedStatuses);
            completedBookings.postValue(completed);
        }).start();
    }

    public LiveData<List<Booking>> getUpcomingBookings() { return upcomingBookings; }
    public LiveData<List<Booking>> getCompletedBookings() { return completedBookings; }
}
