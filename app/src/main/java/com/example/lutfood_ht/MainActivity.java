package com.example.lutfood_ht;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/*
Mainactivity is the app menu => Stays always open
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    Reviews reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN", "MAINACTIVITY STARTED");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("LutFood");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Bundle extras;
        extras = getIntent().getExtras();

        reviews = extras.getParcelable("reviews");

        /*
        On start open menu fragment
         */
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("reviews", reviews);
            MenuFragment menuFragment = new MenuFragment();
            menuFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
            navigationView.setCheckedItem(R.id.nav_menu);
        }
        /*
        Every 60 seconds retrieve reviews from the database, at app opening run the first one after 30 seconds
         */
        Timer delay = new Timer();
        TimerTask getReviews = new TimerTask() {
            @Override
            public void run() {
                Log.d("{BEGINNING}", "GETTING REVIEWS FROM FIREBASE");
                String collection1 = "Reviews";
                HandleFirebase handleFirebase1 = new HandleFirebase();
                handleFirebase1.readReviews(new HandleFirebase.FireStoreCallback_reviews() {
                    @Override
                    public void onCallback(ArrayList<Review> fetchedReviews) {
                        reviews = new Reviews(fetchedReviews);
                    }
                }, collection1);
            }
        }; delay.schedule(getReviews, 30000, 60000);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("reviews", reviews);
        switch(item.getItemId()){

            case R.id.nav_menu:
                MenuFragment menuFragment = new MenuFragment();
                menuFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
                break;

            case R.id.nav_review:
                MakeReviewFragment makeReviewFragment = new MakeReviewFragment();
                makeReviewFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, makeReviewFragment).commit();
                break;

            case R.id.nav_reviews:
                ReviewFragment reviewFragment = new ReviewFragment();
                reviewFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, reviewFragment).commit();
                break;

            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState, persistentState);
    }

}





