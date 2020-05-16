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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/*
Fragment where user settings can be modified
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    Button changeEmail;
    Button changePass;
    Button apply;
    TextView user;
    Button login;
    Button logout;
    Button register;
    FirebaseUser firebaseUser;
    Bundle bundle;
    Reviews reviews;
    GoogleSignInAccount acct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        changeEmail = v.findViewById(R.id.changeEmail);
        changePass = v.findViewById(R.id.changePass);
        user = v.findViewById(R.id.settings_user);
        login = v.findViewById(R.id.settings_login);
        logout = v.findViewById(R.id.settings_logout);
        register = v.findViewById(R.id.settings_register);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bundle = this.getArguments();
        if(bundle != null){
            reviews = bundle.getParcelable("reviews");
        }
        changeEmail.setOnClickListener(this);
        changePass.setOnClickListener(this);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        register.setOnClickListener(this);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkUserStatus(firebaseUser) && Patterns.EMAIL_ADDRESS.matcher(firebaseUser.getDisplayName()).matches()){
            /*
            If user is signed in with google make change options invisible
             */
            changeEmail.setVisibility(View.INVISIBLE);
            changePass.setVisibility(View.INVISIBLE);

        }
        else if(checkUserStatus(firebaseUser)){
            changeEmail.setVisibility(View.VISIBLE);
            changePass.setVisibility(View.VISIBLE);

        }  else {
            changeEmail.setVisibility(View.INVISIBLE);
            changePass.setVisibility(View.INVISIBLE);
        }
        updateUI();
    }

    void updateUI(){
        if (checkUserStatus(firebaseUser)){
            logout.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
            register.setVisibility(View.INVISIBLE);
            String displayName = "";
            for (UserInfo profile : firebaseUser.getProviderData()) {
                displayName = profile.getDisplayName();
            }
            displayName = "Olet kirjautuneena " + displayName;
            user.setText(displayName);
        } else {
            logout.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
            user.setText("Et ole kirjautunut sisään");
        }
    }

    public boolean checkUserStatus(FirebaseUser user){
        /*
        Is user signed in
         */

        if (user != null){
            return true;
        } else {
            return false;
        }

    }

    public void userLogout(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("reviews", reviews);
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this.getContext(), "Olet kirjautunut ulos!", Toast.LENGTH_LONG).show();
        user.setText("Et ole kirjautunut sisään");
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();

    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()){
            case R.id.settings_login:
                bundle = new Bundle();
                bundle.putParcelable("reviews", reviews);
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setArguments(bundle);
                this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
                break;
            case R.id.settings_logout:
                userLogout();
                break;
            case R.id.changeEmail:
                emailChangeFragment emailChangeFragment = new emailChangeFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("reviews", reviews);
                emailChangeFragment.setArguments(bundle2);
                this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, emailChangeFragment).commit();
                break;
            case R.id.changePass:
                passwordChangeFragment passwordChangeFragment = new passwordChangeFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable("reviews", reviews);
                passwordChangeFragment.setArguments(bundle1);
                this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, passwordChangeFragment).commit();
                break;
            case R.id.settings_register:
                bundle = new Bundle();
                bundle.putParcelable("reviews", reviews);
                RegisterFragment registerFragment = new RegisterFragment();
                registerFragment.setArguments(bundle);
                this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment).commit();
                break;
        }

    }
}
