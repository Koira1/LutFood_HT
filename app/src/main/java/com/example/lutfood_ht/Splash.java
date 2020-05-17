package com.example.lutfood_ht;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Display splash screen on app open, splash stays open until data is retrieved from database
 */

public class Splash extends Activity {

    /*
    Set the variables
     */
    ArrayList<Restaurant> restaurants;
    ArrayList<String> restaurantNames;
    Reviews reviews;
    Intent mainIntent;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splashscreen);
        restaurants = new ArrayList<>();

        /*
        Declare intent to open mainactivity
         */
        mainIntent = new Intent(Splash.this, MainActivity.class);

        /*
        Declare shared prefs
         */
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*
        Get the restaurants and their menu items, if the items do not exist yet in the memory => There are so many items, that this arraylist restaurants cannot be passed as intent
         */
        if(mPrefs.contains("restaurants")){
            Log.d("Menu", "Exists");
            getReviews();
        } else {
            Log.d("Message", "Could not locate restaurants, retrieving from database...");
            final HandleFirebase handleFirebase = new HandleFirebase();
            handleFirebase.getRestaurantsNames(new HandleFirebase.FirestoreCallback() {
                @Override
                public void onCallback(final ArrayList<String> restNames) {
                    restaurantNames = restNames;
                    handleFirebase.readDataHandler(restaurantNames, new HandleFirebase.FirestoreCallback() {
                        @Override
                        public void onCallback(ArrayList<String> restNames) {

                        }

                        @Override
                        public void fetchDataComplete(ArrayList<Restaurant> fetched_restaurants) {
                        /*
                        Store restaurant object into shared prefs
                         */
                            Log.d("RESTAURANT", fetched_restaurants.get(0).name);
                            restaurants = fetched_restaurants;
                            makePreferences(restaurants);
                            getReviews();
                        }
                    });
                }

                @Override
                public void fetchDataComplete(ArrayList<Restaurant> restaurants) {

                }
            });
        }

        }



    /*
    Create TinyDB
     */
    void makePreferences(ArrayList<Restaurant> restaurants){
        TinyDB tinyDB = new TinyDB(this);
        ArrayList<Object> restaurantObjects = new ArrayList<>();
        for (Restaurant restaurant : restaurants){
            restaurantObjects.add((Object) restaurant);
        }
        tinyDB.putListObject("restaurants", restaurantObjects);
    }

/*
Get the reviews same way as menu items but they are passed as an intent/bundle to the activities that need them
 */
    void getReviews(){
        String collection1 = "Reviews";
        HandleFirebase handleFirebase = new HandleFirebase();
        handleFirebase.readReviews(new HandleFirebase.FireStoreCallback_reviews() {
            @Override
            public void onCallback(ArrayList<Review> fetchedReviews) {
                Reviews reviews = new Reviews(fetchedReviews);
                mainIntent.putExtra("reviews", reviews);
                startActivity();
            }
        }, collection1);

    }


    void startActivity(){
        /*
        After everything is finished start drawing menu
         */
        Splash.this.startActivity(mainIntent);
        Splash.this.finish();
    }



}


