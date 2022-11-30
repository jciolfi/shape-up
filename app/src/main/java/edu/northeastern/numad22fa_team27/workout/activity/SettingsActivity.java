package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import javax.annotation.Nullable;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;

public class SettingsActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button saveBtn;
    private Button cancelBtn;
    private EditText emailChange, passChange;
    private ProgressBar pb;
    private Uri imageUri;
    private Boolean picSelected = false;
    private Boolean profilePicUpdated = false;
    private Boolean credsUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageView = findViewById(R.id.profilePic);
        saveBtn = findViewById(R.id.saveButton);
        pb = findViewById(R.id.save_progressbar);
        cancelBtn = findViewById(R.id.cancelButton);
        emailChange = findViewById(R.id.editTextEmail);
        passChange = findViewById(R.id.editTextPass);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openActivity(SettingsActivity.this, ProfileActivity.class);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
                picSelected = true;
            }
        });

    }

    private void updateCreds() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (user != null) {

            String username = emailChange.getText().toString();

            if (!username.isEmpty()) {
                if (!username.equals(user.getEmail())) {
                    user.updateEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                credsUpdated = true;
                                documentReference.update("username", username);
                            }
                            else {
                                Toast.makeText(SettingsActivity.this, "Couldn't update the email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    credsUpdated = true;
                } else {
                    credsUpdated = false;
                    Toast.makeText(this, "New username cannot be same as the previous one!", Toast.LENGTH_SHORT).show();
                }
            } else {
                credsUpdated = true;
            }
        }


    }

    private void changeProfilePic() {
        if (picSelected) {
            FirebaseStorage.getInstance()
                    .getReference("images/" + FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            pb.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()) {
                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            updateProfilePic(task.getResult().toString());
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(SettingsActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            profilePicUpdated = true;
        }
        else {
            profilePicUpdated = true;
        }
    }

    private void saveChanges() {
        pb.setVisibility(View.VISIBLE);
        changeProfilePic();
        updateCreds();
        if (profilePicUpdated && credsUpdated) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(SettingsActivity.this, "Changes Successfully Saved!", Toast.LENGTH_SHORT).show();
            Util.openActivity(SettingsActivity.this, ProfileActivity.class);
        }

        else {
            if (profilePicUpdated) {
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(SettingsActivity.this, "Error in updating the username", Toast.LENGTH_SHORT).show();
            }
            else {
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(SettingsActivity.this, "Error in updating the profile picture", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void updateProfilePic(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("profilePic", url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            getImage();
        }
    }

    private void getImage() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Upload not successful! Try again!", Toast.LENGTH_SHORT).show();
        }
        imageView.setImageBitmap(bitmap);
    }


}