package com.example.lutfood_ht;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Fragment where email can be changed
 */

public class emailChangeFragment extends Fragment implements View.OnClickListener {

    EditText email1;
    EditText email2;
    TextView currentUser;
    Button submit;
    FirebaseUser user;
    Reviews reviews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emailchange, container, false);
        email1 = v.findViewById(R.id.new_email1);
        email2 = v.findViewById(R.id.new_email2);
        submit = v.findViewById(R.id.new_email_send);
        currentUser = v.findViewById(R.id.changeEmailCurrentUser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String text = "Sisäänkirjautuneena " + user.getEmail();
        Bundle bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");
        currentUser.setText(text);
        submit.setOnClickListener(this);
        return v;
    }

    /*
    Check if email is actually email
     */
    boolean checkEmail(){

        if(email1.getText().toString().equals(email2.getText().toString())){
            Boolean emailOk = Patterns.EMAIL_ADDRESS.matcher(email1.getText().toString()).matches();
            if(emailOk == false){
                Toast.makeText(getContext(), "Sähköposti on väärää muotoa!", Toast.LENGTH_LONG).show();
            }
            return emailOk;
        } else {
            Toast.makeText(getContext(), "Kohdat eivät täsmää!", Toast.LENGTH_LONG).show();
            return false;
        }

    }
    /*
    Tell database to update email
     */

    void updateEmail(){
        String newEmail = email1.getText().toString();
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    isSuccess(true, "");
                } else {
                    isSuccess(false, task.getException().toString());
                }
            }
        });
    }
    /*
    If updating email is success
     */
    void isSuccess(boolean issuccess, String message){
        /*
        Load settings fragment
         */
        if(issuccess){
            Toast.makeText(getContext(), "Sähköposti vaihdettu!", Toast.LENGTH_LONG).show();
            SettingsFragment settingsFragment = new SettingsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("reviews", reviews);
            settingsFragment.setArguments(bundle);
            this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        } else {
            Toast.makeText(getContext(), "Sähköpostin vaihto epäonnistui! Error: " + message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_email_send:
                if(checkEmail()){
                    updateEmail();
                }
                break;
        }
    }
}

