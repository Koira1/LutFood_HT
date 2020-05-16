package com.example.lutfood_ht;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Class to handle firebase data retrieval => Relatively complicated because of the async nature of data retrieval
 */

public class HandleFirebase {

    ArrayList<String[]> result;
    Map<String, Object> data = new HashMap<>();
    CollectionReference dataRef;
    FirebaseFirestore db;
    Restaurant restaurant;
    ArrayList<String[]> firebaseMenu;
    ArrayList<String[]> firebaseReviews;
    ArrayList<String> restaurantNames;
    ArrayList<Restaurant> restaurants;
    ArrayList<Review> reviews;
    int i = 0;
    String collection;


    HandleFirebase(){
        reviews = new ArrayList<>();
        result = new ArrayList<>();
        firebaseMenu = new ArrayList<>();
        firebaseReviews = new ArrayList<>();
        restaurantNames = new ArrayList<>();
        restaurants = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    /*
    Extract restaurant data
     */
    Restaurant extractData(Map<String, Object> data, Restaurant restaurant) {
        String category;
        String food;
        ArrayList<String> ingredients;
        float price;
        String restaurantName;
        String version;


        if(data.get("category") != null) {
            category = data.get("category").toString();
        } else {
            category = "null";
        }

        if(data.get("food") != null){
            food = data.get("food").toString();
        } else {
            food = "null";
        }

        if(data.get("ingredients") != null){
            ingredients = (ArrayList) data.get("ingredients");
        } else {
            ingredients = new ArrayList<>();
        }

        if(data.get("price") != null){
            price = Float.valueOf(data.get("price").toString());
        } else {
            price = 0;
        }

        if(data.get("restaurant") != null) {
            restaurantName = data.get("restaurant").toString();
        } else {
            restaurantName = "null";
        }

        if(data.get("version") != null){
            version = data.get("version").toString();
        } else {
            version = "null";
        }

        restaurant.setMenuItems(category, food, ingredients, price, restaurantName, version);
        return restaurant;
    }


    public void getRestaurantsNames(final FirestoreCallback firestoreCallback) {
        final HandleFirebase handleFirebase_readData = new HandleFirebase();
        dataRef = db.collection("RestaurantNames");
        dataRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("TASK RESULT", task.getResult().toString());
                                String collectionName = document.getData().get("restaurantName").toString();
                                /*
                                Restaurant name is also a collection name for that specific restaurant -> use it as the parameter to read data from database
                                 */
                                restaurantNames.add(collectionName);
                                Log.d("RESTAURANT NAME", collectionName);


                            }
                            Log.d("STARTING", "startReadData()");
                            firestoreCallback.onCallback(restaurantNames);

                        }

                    }
                });
    }



    public void readDataHandler(final ArrayList<String> restaurantNames, final FirestoreCallback firestoreCallback1){
        if(i < restaurantNames.size()){
            Log.d("I = ", Integer.toString(i));
            FirestoreCallback firestoreCallback = new FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<String> restNames) {
                    Log.d("ReadData finished!", "Indeed");
                    i = i + 1;
                    readDataHandler(restaurantNames, firestoreCallback1);
                }

                @Override
                public void fetchDataComplete(ArrayList<Restaurant> restaurants) {
                    firestoreCallback1.fetchDataComplete(restaurants);
                }
            };
            Log.d("Starting readData", "begin..");
            readData(firestoreCallback, restaurantNames.get(i));

        }
    }

    public Review parseReview(Map<String, Object> data){
        String feedback = "";
        String food = "";
        float rating = 0;
        String restaurantName = "";
        String reviewer = "";

        if(data.get("feedback") != null){
            feedback = data.get("feedback").toString();
        }
       if (data.get("food") != null){
           food = data.get("food").toString();
       }
        if(data.get("rating") != null){
            rating = Float.valueOf(data.get("rating").toString());
        }
        if(data.get("restaurant") != null){
            restaurantName = data.get("restaurant").toString();
        }
        if(data.get("reviewer") != null){
            reviewer = data.get("reviewer").toString();
        }

        Review review = new Review(feedback, food, rating, restaurantName, reviewer);
        return review;
    }

    public void readReviews(final FireStoreCallback_reviews firestoreCallback, String collection){
        CollectionReference collRef = db.collection(collection);
        collRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                data = document.getData();
                                Review review = parseReview(data);
                                reviews.add(review);
                            }
                            firestoreCallback.onCallback(reviews);
                        }
                    }
                });
    }

    /*
    Collect data from the database
     */
    public void readData(final FirestoreCallback firestoreCallback, final String collection){
        Log.d("!!!", "Reading new collection");

        dataRef = db.collection(collection);
        dataRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Restaurant restaurant = new Restaurant(collection);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                data = document.getData();
                                extractData(data, restaurant);
                            }
                            restaurants.add(restaurant);
                            Log.d("RESTAURANT SIZE", Integer.toString(restaurants.size()));
                            firestoreCallback.onCallback(restaurantNames);
                            if(i == restaurantNames.size()){
                                Log.d("FETCHDATA", "COMPLETE");
                                firestoreCallback.fetchDataComplete(restaurants);
                            }

                        } else {
                            Log.d("TAG", "Error getting documents ", task.getException());

                        }
                    }
                });
    }

    public interface FireStoreCallback_reviews {
        void onCallback(ArrayList<Review> reviews);
    }


    /*
    Create interface to wait for the task to finish -> This is used for getting restaurants
     */
    public interface FirestoreCallback {
        void onCallback(ArrayList<String> restNames);
        void fetchDataComplete(ArrayList<Restaurant> restaurants);
    }




}





