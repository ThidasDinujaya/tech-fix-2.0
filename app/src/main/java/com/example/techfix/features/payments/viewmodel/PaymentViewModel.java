package com.example.techfix.features.payments.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.features.payments.data.PaymentRepository;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.common.sync.FirebaseSyncRepository;

import java.text.DateFormat;
import java.util.Date;

public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository repository;
    private final FirebaseSyncRepository syncRepo;
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        repository = PaymentRepository.getInstance(application);
        syncRepo = new FirebaseSyncRepository(application);
    }

    public void processPayment(int bookingId, double amount, String method, String cardNumber, String expiryDate, String cvv) {
        if ("Card".equalsIgnoreCase(method)) {
            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                errorMessage.setValue("Please fill in all card details");
                return;
            }
        }

        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Payment payment = new Payment(0, bookingId, amount, method, cardNumber, expiryDate, cvv, currentDate);

        new Thread(() -> {
            long result = repository.insertPayment(payment);
            if (result != -1) {
                payment.setId((int) result);
                syncRepo.syncPayment(payment);
                paymentSuccess.postValue(true);
            } else {
                errorMessage.postValue("Failed to process payment");
            }
        }).start();
    }

    public void processBookingPayment(Booking booking, double amount, String method, String cardNumber, String expiryDate, String cvv) {
        if ("Card".equalsIgnoreCase(method)) {
            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                errorMessage.setValue("Please fill in all card details");
                return;
            }
        }

        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Payment payment = new Payment(0, 0, amount, method, cardNumber, expiryDate, cvv, currentDate);

        new Thread(() -> {
            boolean success = repository.insertBookingWithPayment(booking, payment);
            if (success) {
                syncRepo.syncBooking(booking);
                syncRepo.syncPayment(payment);
                paymentSuccess.postValue(true);
            } else {
                errorMessage.postValue("Failed to process payment and booking");
            }
        }).start();
    }

    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
