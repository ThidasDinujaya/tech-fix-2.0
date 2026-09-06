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
            String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
            
            if (cleanCardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                errorMessage.setValue("Please fill in all card details");
                return;
            }

            if (cleanCardNumber.length() < 13 || cleanCardNumber.length() > 19) {
                errorMessage.setValue("Invalid card number length");
                return;
            }

            if (!isValidLuhn(cleanCardNumber)) {
                errorMessage.setValue("Invalid card number (Luhn check failed)");
                return;
            }

            if (!expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                errorMessage.setValue("Invalid expiry date format (MM/YY)");
                return;
            }

            if (cvv.length() < 3 || cvv.length() > 4) {
                errorMessage.setValue("Invalid CVV");
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

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
