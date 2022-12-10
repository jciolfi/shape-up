package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Util.requestNoActivityBar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;
import edu.northeastern.numad22fa_team27.workout.utilities.UserUtil;

public class LoginActivity extends AppCompatActivity {

    private TextView usr_email, usr_pass;
    private Button sign_in_btn, new_acc_btn;
    private ProgressBar pb;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNoActivityBar(this);
        setContentView(R.layout.activity_login);

        usr_email = findViewById(R.id.editTextTextEmailAddress);
        usr_pass = findViewById(R.id.editTextTextPassword);
        sign_in_btn = findViewById(R.id.btn_login);
        new_acc_btn = findViewById(R.id.btn_new_account);
        pb = findViewById(R.id.progressbar_login);

        new_acc_btn.setOnClickListener(v -> Util.openActivity(LoginActivity.this, RegisterActivity.class));
        loginButtonClicked();

        user_auth = FirebaseAuth.getInstance();

        new FirestoreService().johntest();
    }

    @Override
    public void onStart() {
        super.onStart();
        Animation slide_in = AnimationUtils.loadAnimation(this, R.anim.slide_from_left_slow);
        findViewById(R.id.welcome_banner).setAnimation(slide_in);
        usr_email.setAnimation(slide_in);
        usr_pass.setAnimation(slide_in);
        sign_in_btn.setAnimation(slide_in);
        new_acc_btn.setAnimation(slide_in);
    }

    private void loginButtonClicked() {
        usr_email.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        sign_in_btn.setOnClickListener(v -> {
            // Indicate we're processing the request
            pb.setVisibility(View.VISIBLE);

            // Get the user and pass of the user
            String email = usr_email.getText().toString();
            String pass = usr_pass.getText().toString();

            // Check if all fields are filled
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.INVISIBLE);
                return;
            }
            if (!isEmailValid(email)) {
                Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.INVISIBLE);
                return;
            }

            // Check if the user already exists
            user_auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                // Boolean to check if it is a new user or an old one
                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                // If it is a new user show a proper message
                if (isNewUser) {
                    Toast.makeText(LoginActivity.this, "User does not exist! Please Sign up!", Toast.LENGTH_SHORT).show();
                    Util.openActivity(LoginActivity.this, RegisterActivity.class);
                }
                else {
                    // If user exists sign in the user
                    user_auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task1 -> {
                        // If login was successful go to app's main page
                        if (task1.isSuccessful()) {
                            pb.setVisibility(View.INVISIBLE);
                            showMainPage();
                        } else {
                            pb.setVisibility(View.INVISIBLE);
                            // Otherwise show a message that the password is wrong and
                            // make the password box empty
                            Toast.makeText(LoginActivity.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
                            usr_pass.setText("");
                        }
                    });
                }
            });
        });
    }

    public void showMainPage() {
        Toast.makeText(LoginActivity.this, "Successfully Signed in", Toast.LENGTH_SHORT).show();

        // Don't let anyone hit "back" to the login
        finish();

        // We know that the auth is set up and the user is valid
        UserUtil.getInstance().startWatchingUserChanges();
        Util.openActivity(LoginActivity.this, ProfileActivity.class);
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

    // TODO
    // Implement the logic when user presses back button after logging in.
}