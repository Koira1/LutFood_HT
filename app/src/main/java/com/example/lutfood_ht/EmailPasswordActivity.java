package com.example.lutfood_ht;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.auth.UserProfileChangeRequest;

/*
Logic for email and password authentication, this fragment is opened from registerfragment when submit is clicked
 */
public class EmailPasswordActivity extends Fragment implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    String email;
    String password;
    String username;
    Boolean isRegister;
    Bundle bundle;
    Intent intent;
    Reviews reviews;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");
        Log.d("EMAILPASSWORDACTIVITY", reviews.toString());
        mAuth = FirebaseAuth.getInstance();
        email = bundle.getString("email");
        password = bundle.getString("password");
        isRegister = bundle.getBoolean("isRegister");
        username = bundle.getString("username");
        return super.onCreateView(inflater, container, savedInstanceState);


    }


    @Override
    public void onStart() {
        super.onStart();
        /*
        From the registerfragment isRegister is passed as true for the logic to know whether task is to sign in or register
         */
        if(isRegister){
            signNewUser(email, password);
        } else {
            signExistingUser(email, password);
        }
    }

    void signNewUser(String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                            Log.d("Task not succesful", "asd");
                            RegisterFragment registerFragment = new RegisterFragment();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                bundle.putString("error", e.toString());
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                                Log.d("Error", e.toString());

                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                bundle.putString("error", e.toString());
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                                Log.d("Error", e.toString());

                            } catch(FirebaseAuthUserCollisionException e) {
                                bundle.putString("error", e.toString());
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                                Log.d("Error", e.toString());

                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                                bundle.putString("error", e.toString());
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                            }

                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("reviews", reviews);
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "SUCCESS");
                                } else {
                                    Log.d("TAG", "fail");
                                }
                            }
                        });
                        MenuFragment menuFragment = new MenuFragment();
                        Log.d("User", user.toString());
                        menuFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
                    }



            }
        });
    }

    void signExistingUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("reviews", reviews);
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("User", user.toString());
                    MenuFragment menuFragment = new MenuFragment();
                    menuFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
                    //updateUI(user);
                } else {
                    Log.d("fail", "Failed...");
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

    }


}
