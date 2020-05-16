package com.example.lutfood_ht;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Review implements Parcelable {
    public String feedback;
    public String food;
    public float rating;
    public String restaurant;
    public String reviewer;

    public Review(String feedback, String food, float rating, String restaurant, String reviewer){
        this.feedback = feedback;
        this.food = food;
        this.rating = rating;
        this.restaurant = restaurant;
        this.reviewer = reviewer;
    }

    protected Review(Parcel in) {
        feedback = in.readString();
        food = in.readString();
        rating = in.readFloat();
        restaurant = in.readString();
        reviewer = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(feedback);
        dest.writeString(food);
        dest.writeFloat(rating);
        dest.writeString(restaurant);
        dest.writeString(reviewer);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
