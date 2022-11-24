package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;

public class LoginActivity extends AppCompatActivity {

    private TextView usr_email, usr_pass;
    private Button sign_in_btn;
    // TODO
    // Add progressbar
    private ProgressBar pb;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usr_email = findViewById(R.id.editTextTextEmailAddress);
        usr_pass = findViewById(R.id.editTextTextPassword);
        sign_in_btn = findViewById(R.id.btn_login);
        user_auth = FirebaseAuth.getInstance();

        loginButtonClicked();

        // Specify the part of the test where user can click to register an account
        TextView tv = findViewById(R.id.create_account_login_page);
        String txt = "Don't have an account? Create one here";
        SpannableString ss =new SpannableString(txt);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Util.openActivity(LoginActivity.this, RegisterActivity.class);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        ss.setSpan(cs, 23, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginButtonClicked() {
        usr_email.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user and pass of the user
                String email = usr_email.getText().toString();
                String pass = usr_pass.getText().toString();
                // Check if the user already exists
                user_auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        // Boolean to check if it is a new user or an old one
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        // Check if all fields are filled
                        boolean isNotEmptyField = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass);

                        if (isNotEmptyField) {
                            // If it is a new user show a proper message
                            if (isNewUser) {
                                Toast.makeText(LoginActivity.this, "User does not exist! Please Sign up!", Toast.LENGTH_SHORT).show();
                                Util.openActivity(LoginActivity.this, RegisterActivity.class);
                            }
                            else {
                                // If user exists sign in the user
                                user_auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If login was successful go to app's main page
                                    if (task.isSuccessful()) {
                                        showMainPage();
                                    } else {
                                        // Otherwise show a message that the password is wrong and
                                        // make the password box empty
                                        Toast.makeText(LoginActivity.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
                                        usr_pass.setText("");
                                    }
                                }
                            });}
                        } else {
                            Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void showMainPage() {
        // TODO
        // This should redirect the user to the main page
        Toast.makeText(LoginActivity.this, "Successfully Signed in", Toast.LENGTH_SHORT).show();
        Util.openActivity(LoginActivity.this, ProfileActivity.class);
    }

    // TODO
    // Implement the logic when user presses back button after logging in.
}