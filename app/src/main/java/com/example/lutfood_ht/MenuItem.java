package com.example.lutfood_ht;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/*
This class is one menu item within restaurant's menu
 */

public class MenuItem implements Parcelable {
    String category;
    String food;
    ArrayList<String> ingredients;
    float price;
    String restaurantName;
    String version;

    MenuItem(String category, String food, ArrayList<String> ingredients, float price, String restaurantName, String version){
        this.category = category;
        this.food = food;
        this.ingredients = ingredients;
        this.price = price;
        this.restaurantName = restaurantName;
        this.version = version;
    }


    /*
    All the code beneath is just to make this custom class parcelable between fragments
     */



    protected MenuItem(Parcel in) {
        category = in.readString();
        food = in.readString();
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<String>();
            in.readList(ingredients, String.class.getClassLoader());
        } else {
            ingredients = null;
        }
        price = in.readFloat();
        restaurantName = in.readString();
        version = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(food);
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
        dest.writeFloat(price);
        dest.writeString(restaurantName);
        dest.writeString(version);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };
}
