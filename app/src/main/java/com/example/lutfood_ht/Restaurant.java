package com.example.lutfood_ht;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import org.testng.annotations.Test;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Restaurant implements Parcelable {
    String name;
    ArrayList<MenuItem> restaurantMenu;


    Restaurant(String name){
        this.name = name;
        restaurantMenu = new ArrayList<>();
        //setMenuItems();
    }

    ArrayList<MenuItem> getMenu(){
        return restaurantMenu;
    }

    public void setMenuItems(String category, String food, ArrayList<String> ingredients, float price, String restaurantName, String version){
        MenuItem menuItem = new MenuItem(category, food, ingredients, price, restaurantName, version);
        this.restaurantMenu.add(menuItem);
    }



    protected Restaurant(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            restaurantMenu = new ArrayList<MenuItem>();
            in.readList(restaurantMenu, MenuItem.class.getClassLoader());
        } else {
            restaurantMenu = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (restaurantMenu == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(restaurantMenu);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
