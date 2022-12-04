package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class RegisterActivity extends AppCompatActivity {

    private TextView usr_email, usr_pass, usr_pass_confirm;
    private Button sign_up_btn;
    private ProgressBar pb;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usr_email = findViewById(R.id.register_EmailAddress);
        usr_pass = findViewById(R.id.register_password);
        usr_pass_confirm = findViewById(R.id.repeat_register_Password);
        sign_up_btn = findViewById(R.id.btn_signup);
        pb = findViewById(R.id.progressbar_register);
        user_auth = FirebaseAuth.getInstance();


        registerButtonClicked();

        // Specify the part of the test where user can click to register an account
        TextView tv = findViewById(R.id.already_have_account);
        String txt = "Already have an account? Sign in here";
        SpannableString ss = new SpannableString(txt);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Util.openActivity(RegisterActivity.this, LoginActivity.class);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        ss.setSpan(cs, 25, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void registerButtonClicked() {
        usr_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        usr_pass_confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        sign_up_btn.setOnClickListener(v -> {
            // Get the entered email, pass, and pass confirmation
            String email = usr_email.getText().toString();
            String pass = usr_pass.getText().toString();
            String pass_confirm = usr_pass_confirm.getText().toString();

            // Checks the empty Fields
            boolean isNotEmptyField = !TextUtils.isEmpty(email)
                                      && !TextUtils.isEmpty(pass)
                                      && !TextUtils.isEmpty(pass_confirm);
            if (isEmailValid(email)) {
                // If the fields are not empty
                if (isNotEmptyField) {
                    // if the pass and pass_confirm matches
                    if (pass.equals(pass_confirm)) {
                        pb.setVisibility(View.VISIBLE);
                        user_auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                User user = new User(email, pass, "");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users")
                                        .document(user_auth.getCurrentUser().getUid())
                                        .set(user);
                                pb.setVisibility(View.INVISIBLE);
                                showMainPage();
                            } else {
                                // If registration not successful that means there is a user with this credentials in our DB
                                Toast.makeText(RegisterActivity.this, "User already exists! Please log in.", Toast.LENGTH_SHORT).show();
                                Util.openActivity(RegisterActivity.this, LoginActivity.class);
                            }
                        });
                    } else {
                        pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "Passwords doesn't match! Please Try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void showMainPage() {
        // This should redirect the user to the login page
        Toast.makeText(RegisterActivity.this, "User successfully created. Please log in!", Toast.LENGTH_SHORT).show();
        Util.openActivity(RegisterActivity.this, LoginActivity.class);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}