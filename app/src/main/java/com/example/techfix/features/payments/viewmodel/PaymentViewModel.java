package com.example.techfix.features.payments.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.features.payments.data.PaymentRepository;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import com.example.techfix.common.sync.FirebaseSyncRepository;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class PaymentViewModel extends AndroidViewModel {
    // ViewModel for handling payment processing and booking details

    private final PaymentRepository repository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final FirebaseSyncRepository syncRepo;
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Double> amount = new MutableLiveData<>();
    private final MutableLiveData<String> serviceName = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        repository = PaymentRepository.getInstance(application);
        bookingRepository = BookingRepository.getInstance(application);
        serviceRepository = ServiceRepository.getInstance(application);
        syncRepo = new FirebaseSyncRepository(application);
    }

    public void processPayment(int bookingId, double amount, String method, String cardNumber, String expiryDate, String cvv) {
        if ("Card".equalsIgnoreCase(method)) {
            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                errorMessage.setValue("Please fill in all card details");
                return;
            }
            if (!isValidCardNumber(cardNumber)) {
                errorMessage.setValue("Invalid card number");
                return;
            }
            if (!isValidExpiryDate(expiryDate)) {
                errorMessage.setValue("Invalid expiry date (MM/YY) or card has expired");
                return;
            }
            if (!isValidCvv(cvv)) {
                errorMessage.setValue("Invalid CVV");
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
            if (!isValidCardNumber(cardNumber)) {
                errorMessage.setValue("Invalid card number");
                return;
            }
            if (!isValidExpiryDate(expiryDate)) {
                errorMessage.setValue("Invalid expiry date (MM/YY) or card has expired");
                return;
            }
            if (!isValidCvv(cvv)) {
                errorMessage.setValue("Invalid CVV");
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

    public void loadBookingDetails(int bookingId) {
        new Thread(() -> {
            Booking booking = bookingRepository.getBookingById(bookingId);
            if (booking != null) {
                Service service = serviceRepository.getServiceById(booking.getServiceId());
                if (service != null) {
                    amount.postValue(service.getPrice());
                    serviceName.postValue("Service: " + service.getName());
                } else {
                    errorMessage.postValue("Service details not found");
                }
            } else {
                errorMessage.postValue("Booking not found");
            }
        }).start();
    }

    public LiveData<Double> getAmount() { return amount; }
    public LiveData<String> getServiceName() { return serviceName; }
    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Validation Methods
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;
        String cleanNumber = cardNumber.replaceAll("\\s+", "").replaceAll("-", "");
        if (cleanNumber.length() < 13 || cleanNumber.length() > 19 || !cleanNumber.matches("\\d+")) {
            return false;
        }

        // Luhn Algorithm Check
        int sum = 0;
        boolean alternate = false;
        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cleanNumber.substring(i, i + 1));
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

    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("^(0[1-9]|1[0-2])/?([0-9]{2})$")) {
            return false;
        }
        
        String cleanExpiry = expiryDate.replaceAll("/", "");
        int month = Integer.parseInt(cleanExpiry.substring(0, 2));
        int year = Integer.parseInt(cleanExpiry.substring(2, 4)) + 2000;

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;

        if (year < currentYear) {
            return false;
        }
        if (year == currentYear && month < currentMonth) {
            return false;
        }
        return true;
    }

    private boolean isValidCvv(String cvv) {
        return cvv != null && cvv.matches("^[0-9]{3,4}$");
    }
}
