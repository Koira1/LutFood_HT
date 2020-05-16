package com.example.lutfood_ht;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MakeReviewFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    FirebaseUser user;
    FirebaseFirestore db;
    Button submit;
    Spinner restaurant_makereview;
    Spinner food_makereview;
    EditText freeWord;
    RatingBar ratingBar;
    ArrayList<Restaurant> restaurants;
    ArrayList<String> restaurantNames;
    ArrayList<String> foods;
    ArrayAdapter<String> restaurant_adapter;
    ArrayAdapter<String> food_adapter;
    Reviews reviews;
    Restaurant rakuuna;
    Review review;
    CheckBox anonymous;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        Initiating variables
         */
        View v = inflater.inflate(R.layout.fragment_makereview, container, false);

        db = FirebaseFirestore.getInstance();

        restaurants = new ArrayList<>();
        restaurantNames = new ArrayList<>();
        foods = new ArrayList<>();

        submit = v.findViewById(R.id.submit_review);
        submit.setOnClickListener(this);

        anonymous = v.findViewById(R.id.make_review_anonymous);
        restaurant_makereview = v.findViewById(R.id.ravintola);
        food_makereview = v.findViewById(R.id.ruoka);
        freeWord = v.findViewById(R.id.freeWord);
        ratingBar = v.findViewById(R.id.rating);

        restaurant_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, restaurantNames);
        food_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, foods);
        populateSpinners();
        return v;

    }

    public boolean checkUserStatus(){
        /*
        Is user signed in
         */
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            return true;
        } else {
            return false;
        }

    }

    private void retrieveRestaurants(){
        TinyDB tinyDB = new TinyDB(this.getContext());
        ArrayList<Object> objects = tinyDB.getListObject("restaurants", Restaurant.class);
        for(Object obj : objects){
            restaurants.add((Restaurant) obj);
        }
    }

    public void populateSpinners(){
         /*
        Add restaurant names
         */
        retrieveRestaurants();
        for(Restaurant restaurant : restaurants){
            restaurantNames.add(restaurant.name);
        }


        /*
        Populate the spinners
       */
        restaurant_makereview.setAdapter(restaurant_adapter);
        restaurant_makereview.setOnItemSelectedListener(this);
        String restaurantName = restaurant_makereview.getSelectedItem().toString();
        getMenuItemsForRestaurant(restaurantName);
    }

    void getMenuItemsForRestaurant(String restaurantName){
        foods = new ArrayList<>();
        Log.d("getmenuitems", "true");
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
            foods.add(food);
        }
        food_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, foods);
        food_makereview.setAdapter(food_adapter);
    }

    boolean checkFields(){
        if(restaurant_makereview.getSelectedItem().toString().equals("Ravintola")){
            Toast.makeText(this.getContext(), "Ole hyvä ja valitse ravintola", Toast.LENGTH_LONG).show();
            return false;
        }
        if(food_makereview.getSelectedItem().toString().equals("Ruoka")){
            Toast.makeText(this.getContext(), "Ole hyvä ja valitse ruoka", Toast.LENGTH_LONG).show();
            return false;
        }
        if(freeWord.getText().toString() == null){
            freeWord.setText("");
        }
        if(Float.toString(ratingBar.getRating()) == null){
            Toast.makeText(this.getContext(), "Ole hyvä ja anna arvosana", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public String getDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String reviewer = user.getDisplayName();
        return reviewer;
    }

    public void submitReview(){

        String restaurant;
        String food;
        String feedback;
        float rating;
        String reviewer;
        Boolean checkForm;


        /*
        Get the components for submitting review
         */
        if(checkForm = checkFields()) {
            restaurant = restaurant_makereview.getSelectedItem().toString();
            food = food_makereview.getSelectedItem().toString();
            feedback = freeWord.getText().toString();
            rating = ratingBar.getRating();
            reviewer = getDisplayName();
            if(anonymous.isChecked()){
                reviewer = "Anonyymi";
            }
            review = new Review(feedback, food, rating, restaurant, reviewer);
        }

        if(checkForm){
            /*
        Generate unique id for review from date
         */
            Date review_date = new Date();
            long id = review_date.getTime();

            db.collection("Reviews").document("Review#" + id)
                    .set(review)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Data written succesfully");
                            startMenuFragment();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "ERROR WRITING DOCUMENT: ", e);
                }
            });
        }




    }

    void startMenuFragment(){
        Toast.makeText(getContext(), "Review submitted succesfully!", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putParcelable("reviews", reviews);
        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.submit_review:
                /*
                Check if logged in
                 */
                if(checkUserStatus()){
                    submitReview();
                } else {
                    Toast.makeText(this.getActivity(), "Please log in first before writing a review.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = restaurant_makereview.getSelectedItem().toString();
        getMenuItemsForRestaurant(name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
