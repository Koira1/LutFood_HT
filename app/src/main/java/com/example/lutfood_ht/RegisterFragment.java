package com.example.lutfood_ht;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    Button submit;
    EditText username;
    EditText password;
    EditText password2;
    EditText email;
    EditText email2;
    Reviews reviews;
    Restaurant rakuuna;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.register_button);
        username = view.findViewById(R.id.register_username);
        password = view.findViewById(R.id.register_password);
        password2 = view.findViewById(R.id.register_password1);
        email = view.findViewById(R.id.register_email);
        email2 = view.findViewById(R.id.register_email1);

        submit.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");
        Log.d("REGISTERFRAGMENT", reviews.toString());



    }

    boolean checkFields(){
        boolean valid = true;

        String _email = email.getText().toString();
        if(TextUtils.isEmpty(_email)){
            Toast.makeText(this.getActivity(), "Email is required!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        String _password = password.getText().toString();
        if(TextUtils.isEmpty(_password)){
            Toast.makeText(this.getActivity(), "Password is required!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }
        String _username = username.getText().toString();
        if(TextUtils.isEmpty(_username)){
            Toast.makeText(this.getActivity(), "Username is required!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        if(_username.length() < 4 && _username.length() > 0){
            Toast.makeText(this.getActivity(), "Username cannot be less than 4 characters long!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        if(_password.length() < 8 && _password.length() > 0) {
            Toast.makeText(this.getActivity(), "Password must be at least 8 characters long!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        String _password2 = password2.getText().toString();
        if(TextUtils.equals(_password, _password2)){

        } else {
            Toast.makeText(this.getActivity(), "Passwords do not match!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        /*String _email2 = email2.getText().toString();
        if(TextUtils.equals(_email, _email2)){
            Toast.makeText(this.getActivity(), "Emails do not match!", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }*/

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register_button:
                if (checkFields()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email.getText().toString());
                    bundle.putString("password", password.getText().toString());
                    bundle.putString("username", username.getText().toString());
                    bundle.putBoolean("isRegister", true);
                    bundle.putParcelable("reviews", reviews);
                    EmailPasswordActivity EPA = new EmailPasswordActivity();
                    EPA.setArguments(bundle);
                    Log.d("Bundle", bundle.toString());
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, EPA).commit();
                } else {

                }
                break;
        }
    }
}
