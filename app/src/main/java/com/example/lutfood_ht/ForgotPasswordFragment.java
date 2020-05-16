package com.example.lutfood_ht;

import android.os.Bundle;
import android.util.Patterns;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.zip.Inflater;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    Button sendEmail;
    EditText getEmail;
    Reviews reviews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_forgotpassword, container, false);
        sendEmail = v.findViewById(R.id.return_email);
        getEmail = v.findViewById(R.id.return_email_string);

        Bundle bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");

        sendEmail.setOnClickListener(this);
        return v;
    }

    String getEmailString(){
        String email = getEmail.getText().toString();
        if(checkEmailAddress(email)){
            return email;
        } else {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return "null";
        }

    }

    boolean checkEmailAddress(String email){
        if(email.length() == 0){
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }


    }

    void sendResetEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    success();
                    loadSettingsFragment();
                }
            }
        });
    }

    void loadSettingsFragment(){
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("reviews", reviews);
        settingsFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
    }

    void success(){
        Toast.makeText(getContext(), "Email sent.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_email:
                String email = getEmailString();
                if(email.equals("null")){

                } else {
                    sendResetEmail(email);
                }
                break;
        }
    }
}
