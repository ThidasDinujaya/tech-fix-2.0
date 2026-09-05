package com.example.techfix.features.booking.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.booking.data.BookingStatus;

// Manages the logic and validation for the repair booking form
public class BookRepairViewModel extends AndroidViewModel {

    private final BookingRepository repository;
    private final MutableLiveData<Boolean> bookingStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public BookRepairViewModel(@NonNull Application application) {
        super(application);
        repository = BookingRepository.getInstance(application);
    }

    // Validates the form data and attempts to save it to the database
    public void submitBooking(int serviceId, String type, String brand, String model, String desc, String date, int userId, String imagePaths) {
        if (type.isEmpty() || brand.isEmpty() || model.isEmpty() || desc.isEmpty() || date.isEmpty()) {
            errorMessage.setValue("Please fill in all required fields");
            return;
        }

        if (userId == -1) {
            errorMessage.setValue("Error: User session expired. Please log in again.");
            return;
        }

        // Create a new booking object with initial PENDING status
        Booking newBooking = new Booking(0, serviceId, type, brand, model, desc, date, imagePaths, BookingStatus.PENDING, userId);

        // Run database operation in a background thread to keep UI smooth
        new Thread(() -> {
            long result = repository.insertBooking(newBooking);
            if (result != -1) {
                bookingStatus.postValue(true);
            } else {
                errorMessage.postValue("Database error: Could not save booking");
            }
        }).start();
    }

    public LiveData<Boolean> getBookingStatus() { return bookingStatus; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
