package com.example.lutfood_ht;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/*
Class that contains all of the reviews
 */

public class Reviews implements Parcelable {
    ArrayList<Review> reviews;

    public Reviews(ArrayList<Review> reviews){
        this.reviews = reviews;
    }

    void setReviews(String feedback, String food, float rating, String restaurant, String reviewer){
        Review review = new Review(feedback, food, rating, restaurant, reviewer);
        reviews.add(review);
    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    protected Reviews(Parcel in) {
        if (in.readByte() == 0x01) {
            reviews = new ArrayList<Review>();
            in.readList(reviews, Review.class.getClassLoader());
        } else {
            reviews = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (reviews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviews);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}
