package com.example.techfix.features.payments.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.features.payments.data.PaymentRepository;

public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository repository;
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PaymentViewModel( Application application) {
        super(application);
        repository = PaymentRepository.getInstance(application);
    }

    public void processPayment(int bookingId, double amount, String method, String cardNumber, String expiryDate, String cvv) {
        if ("Card".equalsIgnoreCase(method)) {
            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                errorMessage.setValue("Please fill in all card details");
                return;
            }
        }

        String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        Payment payment = new Payment(0, bookingId, amount, method, cardNumber, expiryDate, cvv, currentDate);

        new Thread(() -> {
            long result = repository.insertPayment(payment);
            if (result != -1) {
                paymentSuccess.postValue(true);
            } else {
                errorMessage.postValue("Failed to process payment");
            }
        }).start();
    }

    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
