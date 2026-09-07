package com.example.techfix.features.booking.data;

import java.io.Serializable;

public class Review implements Serializable {
    private int id;
    private int bookingId;
    private float rating;
    private String comment;
    private String imageUri;
    private String reviewDate;

    public Review() {}

    public Review(int id, int bookingId, float rating, String comment, String imageUri, String reviewDate) {
        this.id = id;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
        this.imageUri = imageUri;
        this.reviewDate = reviewDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
}
