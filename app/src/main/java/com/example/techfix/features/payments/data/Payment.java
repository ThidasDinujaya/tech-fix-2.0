package com.example.techfix.features.payments.data;

import java.io.Serializable;

public class Payment implements Serializable {
    private int id;
    private int bookingId;
    private double amount;
    private String method; // Cash, Card, Online Transfer
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String paymentDate;

    public Payment() {} // Required for Firestore

    public Payment(int id, int bookingId, double amount, String method, String cardNumber, String expiryDate, String cvv, String paymentDate) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public String getPaymentDate() { return paymentDate; }

    public void setId(int id) { this.id = id; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
}
