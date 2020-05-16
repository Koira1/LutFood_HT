package com.example.lutfood_ht;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*
This fragment shows reviews
 */

public class ReviewFragment extends Fragment {

    private static final String TAG = "TAG TAG";
    Reviews reviews;
    Restaurant rakuuna;
    ArrayList<Restaurant> restaurants;
    ArrayList<String> restaurantNames;
    ArrayList<String> restaurantFoods;
    Spinner reviews_restaurant_spinner;
    Spinner reviews_foods_spinner;
    SpinnerAdapter restaurant_adapter;
    SpinnerAdapter foods_adapter;
    TableLayout tl;
    Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        Populate data
         */
        restaurants = new ArrayList<>();
        restaurantNames = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        reviews_foods_spinner = v.findViewById(R.id.reviews_foods);
        reviews_restaurant_spinner = v.findViewById(R.id.reviews_restaurant);
        reviews_restaurant_spinner.setOnItemSelectedListener(restaurantChanged);
        reviews_foods_spinner.setOnItemSelectedListener(foodChanged);
        tl = v.findViewById(R.id.tl);

        getRestaurants();
        populateSpinners();

        bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");

        return v;
    }

    /*
    Get restaurants from save location
     */
    void getRestaurants(){
        TinyDB tinyDB = new TinyDB(this.getContext());
        ArrayList<Object> objects = tinyDB.getListObject("restaurants", Restaurant.class);
        for(Object obj : objects) {
            restaurants.add((Restaurant) obj);
        }
    }
    /*
    Get foods for specific restaurant
     */
    private void getRestaurantFoods(String restaurantName){
        restaurantFoods = new ArrayList<>();
        int index = 0;
        while (index < restaurants.size()){
            if(restaurants.get(index).name.equals(restaurantName)){
                break;
            }
            index++;
        }
        Restaurant restaurant = restaurants.get(index);
        ArrayList<MenuItem> menu = restaurant.getMenu();
        String food = "";
        String category = "";
        for (MenuItem item : menu){
            food = item.food;
            category = item.category;
            restaurantFoods.add(food);
        }
        foods_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, restaurantFoods);
        reviews_foods_spinner.setAdapter(foods_adapter);
    }

    public void populateSpinners(){
        for(Restaurant restaurant : restaurants) {
            restaurantNames.add(restaurant.name);
        }
        restaurant_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, restaurantNames);
        reviews_restaurant_spinner.setAdapter(restaurant_adapter);
        getRestaurantFoods(reviews_restaurant_spinner.getSelectedItem().toString());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    public void removeTextView(){
        tl.removeAllViews();
    }

    public void addTextView(Review review){
        TextView textView = new TextView(this.getActivity());
        String placeHolder = "Ravintola: " + review.restaurant + "\n" + "Ruoka: " + review.food + "\n" + "Arvosana: " + review.rating +"/5" + "\n" + "Vapaa sana: " + review.feedback + "\n" + "Arvostelija: " + review.reviewer;
        textView.setText(placeHolder);
        textView.setTextSize(20);
        textView.setBackgroundResource(R.drawable.menu_items);
        textView.setPadding(25,25,0,25);
        tl.addView(textView);
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    AdapterView.OnItemSelectedListener foodChanged = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            removeTextView();
            for(Review review : reviews.getReviews()) {
                if(review.restaurant.equals(reviews_restaurant_spinner.getSelectedItem().toString()) && review.food.equals(reviews_foods_spinner.getSelectedItem().toString())){
                    addTextView(review);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    AdapterView.OnItemSelectedListener restaurantChanged = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                removeTextView();
                getRestaurantFoods(reviews_restaurant_spinner.getSelectedItem().toString());
                for(Review review : reviews.getReviews()) {
                    if(review.restaurant.equals(reviews_restaurant_spinner.getSelectedItem().toString()) && review.food.equals(reviews_foods_spinner.getSelectedItem().toString())){
                        addTextView(review);
                    }
                }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
