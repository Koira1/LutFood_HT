package com.example.lutfood_ht;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Main view of the app, displays restaurant menu items, ingredients and avg rating
 */

public class MenuFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ASD";
    FirebaseFirestore db;
    Spinner menu_restaurants;
    Spinner menu_categories;
    ArrayAdapter<String> spinnerArrayAdapter;
    ArrayAdapter<String> categoryArrayAdapter;
    ArrayList<String> restaurants_list;
    TableLayout tl;
    ArrayList<Restaurant> restaurants;
    ArrayList<MenuItem> menu;
    Reviews reviews;
    Bundle bundle;
    SharedPreferences mPrefs;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        Log.d("MENUFRAGMENT", "STARTED");

        tl = v.findViewById(R.id.tablelayout);
        menu = new ArrayList<>();
        restaurants_list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        menu_restaurants = v.findViewById(R.id.menu_restaurants);
        menu_categories = v.findViewById(R.id.menu_categories);
        restaurants = new ArrayList<>();
        mPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        /*
        Retrieve restaurants from TinyDB
         */
        retrieveRestaurants();
        /*
        Get the restaurant names for restaurant name spinner
         */
        for(Restaurant restaurant : restaurants){
            restaurants_list.add(restaurant.name);
        }
        spinnerArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, restaurants_list);
        menu_restaurants.setAdapter(spinnerArrayAdapter);
        menu_restaurants.setOnItemSelectedListener(this);
        populateCategorySpinner();
        bundle = this.getArguments();
        if(bundle != null){
            reviews = bundle.getParcelable("reviews");
        }


        /*
        Create textviews that display menuitems
         */
        getFields();


        return v;
    }

    void populateCategorySpinner(){
        String selectedRestaurant = menu_restaurants.getSelectedItem().toString();
        int index = 0;
        /*
        Using set to prevent duplicates + complexity O(1) when looking for specific element
         */
        Set<String> categories = new HashSet<String>();
        /*
        Find right restaurant
         */
        for (Restaurant restaurant : restaurants){
            if(restaurant.name.equals(selectedRestaurant)){
                break;
            }
            index++;
        }
        Restaurant restaurant = restaurants.get(index);
        for(MenuItem item : restaurant.getMenu()){
            categories.add(item.category);
        }
        String[] categoriesArray = new String[categories.size()];
        categories.toArray(categoriesArray);
        categoryArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesArray);
        menu_categories.setAdapter(categoryArrayAdapter);
        menu_categories.setOnItemSelectedListener(categoryChanged);
    }

    void getFields(){
        String selectedCategory = menu_categories.getSelectedItem().toString();
        String selectedRestaurant = menu_restaurants.getSelectedItem().toString();
        removeTextViews();
        int index = 0;
        /*
        Find right restaurant
         */
        for (Restaurant restaurant : restaurants){
            if(restaurant.name.equals(selectedRestaurant)){
                break;
            }
            index++;
        }
        Restaurant restaurant = restaurants.get(index);

            /*
            Get restaurants menu
             */
            ArrayList<MenuItem> menu = restaurant.getMenu();
            String food = "";
            String restaurantName = "";
            String category = "";
            ArrayList<String> ingredients = new ArrayList<>();
            float price = 0;
            String version = "";
            float rating = 0;

            for (MenuItem item : menu){
                if(selectedCategory.equals(item.category)){
                    food = item.food;
                    restaurantName = item.restaurantName;
                    category = item.category;
                    ingredients = item.ingredients;
                    price = item.price;
                    version = item.version;
                    rating = getRatings(reviews, food, restaurantName);
                    createTextView(food, restaurantName, category, ingredients, price, version, rating);
                }
            }

    }


    public void retrieveRestaurants(){
        /*
        Get restaurants from memory
         */
        TinyDB tinyDB = new TinyDB(this.getContext());
        ArrayList<Object> restaurantObjects = tinyDB.getListObject("restaurants", Restaurant.class);
        for(Object o : restaurantObjects){
            restaurants.add((Restaurant) o);
        }
    }

    /*
    When new restaurant/category is selected remove all current textviews
     */
    void removeTextViews(){
        tl.removeAllViews();
    }

    /*
    Retrieve rating for food item from the database, return the average
     */
    float getRatings(Reviews reviews, String food, String restaurantName){
        float rating = 0;
        int counter = 0;
        if(reviews != null){
            for (Review review : reviews.getReviews()){
                if(review.restaurant.equals(restaurantName)){
                    if(review.food.equals(food)){
                        rating = review.rating + rating;
                        counter++;
                    }
                }
            }
        }

        float avg = rating/counter;
        return avg;
    }

    void createTextView(String food, String restaurant, String category, ArrayList<String> ingredients, float price, String version, float rating){
        /*
        Rating settings
         */
        RatingBar ratingBar = new RatingBar(this.getActivity());
        ratingBar.setRating(rating);
        ratingBar.setEnabled(false);
        ratingBar.setStepSize((float) 0.5);

        /*
        Tablerows to display data
         */
        TableRow tableRow = new TableRow(this.getActivity());
        TableRow tableRow1 = new TableRow(this.getActivity());
        TableRow tableRow2 = new TableRow(this.getActivity());
        TableRow tableRow3 = new TableRow(this.getActivity());
        TableRow tableRow4 = new TableRow(this.getActivity());
        TableRow tableRow5 = new TableRow(this.getActivity());

        TextView foodText = new TextView(this.getActivity());
        TextView restaurantText = new TextView(this.getActivity());
        TextView categoryText = new TextView(this.getActivity());
        TextView ingredientsText = new TextView(this.getActivity());
        TextView priceText = new TextView(this.getActivity());
        TextView versionText = new TextView(this.getActivity());
        /*
        Setting params for textviews
         */
        foodText.setText(food);
        foodText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
        foodText.setMaxLines(1);

        restaurantText.setText("Ravintola: " + restaurant);
        restaurantText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        restaurantText.setMaxLines(1);


        categoryText.setText("Kategoria " + category);
        categoryText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        categoryText.setMaxLines(1);


        String appendedIngredients = ingredients.get(0);
        /*
        Cache size
         */
        int size = ingredients.size();
        for(int i = 1; i < size; i++){
            appendedIngredients = appendedIngredients + ", " + ingredients.get(i);
        }
        ingredientsText.setText(appendedIngredients);
        ingredientsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7);
        ingredientsText.setHorizontallyScrolling(true);
        ingredientsText.setMaxLines(1);


        priceText.setText("Hinta: " + price);
        priceText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        priceText.setMaxLines(1);


        if(version.equals("null")){
            versionText.setText("Koko: ");
            versionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            versionText.setMaxLines(1);

        } else {
            versionText.setText("Koko: " + version);
            versionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            versionText.setMaxLines(1);

        }


        /*
        Layout settings
         */
        tl.setPadding(50, 50, 0,0);

        LinearLayout ll = new LinearLayout(this.getActivity());
        ll.addView(ratingBar);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;
        lp.gravity = Gravity.CENTER;

        View v = new View(this.getActivity());
        v.setLayoutParams(new ViewGroup.LayoutParams(0, 2));
        v.setBackgroundColor(Color.parseColor("#000000"));

        tl.addView(tableRow);
        tl.addView(tableRow1);
        tl.addView(tableRow2);
        tl.addView(tableRow3);
        tl.addView(tableRow4);
        tl.addView(tableRow5);
        tl.addView(ll);
        tl.addView(v);

        tableRow.addView(foodText);
        tableRow1.addView(restaurantText);
        tableRow2.addView(categoryText);
        tableRow3.addView(ingredientsText);
        tableRow4.addView(priceText);
        tableRow5.addView(versionText);
        
        //tableRow1.addView(ll);
        tl.setBackgroundResource(R.drawable.menu_items);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    AdapterView.OnItemSelectedListener categoryChanged = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getFields();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        populateCategorySpinner();
        getFields();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
