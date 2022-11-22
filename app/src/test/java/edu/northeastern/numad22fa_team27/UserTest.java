package edu.northeastern.numad22fa_team27;
import org.junit.Test;


import static org.junit.Assert.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.numad22fa_team27.workout.models.User;

public class UserTest {
    private DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
    private User testUser = new User("Farzad", "1234");

    @Test
    public void User() {

    }
}
