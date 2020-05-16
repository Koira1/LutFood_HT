package com.example.lutfood_ht;

import android.os.Bundle;
import android.util.Log;
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

public class passwordChangeFragment extends Fragment implements View.OnClickListener {

    EditText password1;
    EditText password2;
    TextView currentUser;
    Button submit;
    FirebaseUser user;
    Reviews reviews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passwordchange, container, false);
        password1 = v.findViewById(R.id.new_password1);
        password2 = v.findViewById(R.id.new_password2);
        submit = v.findViewById(R.id.new_password_send);
        currentUser = v.findViewById(R.id.changePasswordCurrentUser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");
        String text = "Sisäänkirjautuneena " + user.getDisplayName();
        currentUser.setText(text);
        submit.setOnClickListener(this);
        return v;
    }

    boolean checkPassword(){

        if(password1.getText().toString().equals(password2.getText().toString())){
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password1.getText().toString());
            if(matcher.matches() == false){
                Toast.makeText(getContext(), "Salasana liian heikko", Toast.LENGTH_LONG).show();
            }
            return matcher.matches();
        } else {
            Toast.makeText(getContext(), "Kohdat eivät täsmää!", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    void updatePassword(){
        String newPassword = password1.getText().toString();
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    void isSuccess(boolean issuccess, String message){
        if(issuccess){
            Toast.makeText(getContext(), "Salasana vaihdettu!", Toast.LENGTH_LONG).show();
            SettingsFragment settingsFragment = new SettingsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("reviews", reviews);
            settingsFragment.setArguments(bundle);
            this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        } else {
            Toast.makeText(getContext(), "Salasanan vaihto epäonnistui, Error: " + message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_password_send:
                if(checkPassword()){
                    updatePassword();
                }
                break;
        }
    }
}
