package com.example.lutfood_ht;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Login fragment from settings page
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    public static final String TAG = "Error";

    Reviews reviews;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    SignInButton signInButton;
    FirebaseAuth firebaseAuth;
    EditText login_email;
    EditText login_password;
    Button login_signIn;
    Button forgotPassword;
    Bundle bundle;
    View v;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        bundle = this.getArguments();
        reviews = bundle.getParcelable("reviews");

        signInButton = v.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        login_email = v.findViewById(R.id.login_email);
        login_password = v.findViewById(R.id.login_password);
        login_signIn = v.findViewById(R.id.login_signIn);
        forgotPassword = v.findViewById(R.id.forgot);

        forgotPassword.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        login_signIn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("931566743482-0dbg10knaqh7n84sg2drg7ctj23t7vkn.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getContext());
        updateUI(account);

    }

    /*
    Check if provided email and password are correct
     */
    boolean checkFields(){
        boolean valid = true;
        Boolean emailOk = Patterns.EMAIL_ADDRESS.matcher(login_email.getText().toString()).matches();
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(login_password.getText().toString());

        if(!emailOk){
            Toast.makeText(getContext(), "Sähköposti ei ole oikeaa muotoa", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!matcher.matches()){
            Toast.makeText(getContext(), "Salasana liian heikko, salasanan tulee olla 9 merkkiä pitkä, salasanan tulee käyttää yhtä isoa kirjainta, yhtä numeroa ja yhtä erikoismerkkiä [@#$%^&+=!]", Toast.LENGTH_LONG).show();
            return false;
        }

        return valid;
    }

    void signInEmailPass(){
        Bundle bundle = new Bundle();
        bundle.putString("email", login_email.getText().toString());
        bundle.putString("password", login_password.getText().toString());
        bundle.putBoolean("isRegister", false);
        bundle.putParcelable("reviews", reviews);
        EmailPasswordActivity EPA = new EmailPasswordActivity();
        EPA.setArguments(bundle);
        Log.d("Bundle", bundle.toString());
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, EPA).commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.login_signIn:
                if(checkFields()){
                    signInEmailPass();
                }
                break;

            case R.id.forgot:
                Bundle bundle = new Bundle();
                bundle.putParcelable("reviews", reviews);
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                forgotPasswordFragment.setArguments(bundle);
                this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, forgotPasswordFragment).commit();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(getActivity(), "Signed in succesfully as " + user.getEmail(), Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("reviews", reviews);
                    SettingsFragment settingsFragment = new SettingsFragment();
                    settingsFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                } else {
                    Log.d("SIGN  IN FAILED", task.getException().toString());
                    Toast.makeText(getActivity(), "Sign in failed " + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void updateUI(GoogleSignInAccount account){


    }


}
