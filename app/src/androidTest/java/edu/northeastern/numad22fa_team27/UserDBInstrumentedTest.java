package edu.northeastern.numad22fa_team27;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.test_utilities.FakeUserGenerator;
import edu.northeastern.numad22fa_team27.workout.test_utilities.FakeWorkoutGenerator;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDBInstrumentedTest {
    private static FirebaseFirestore mDatabase;
    private static FirebaseAuth user_auth;
    private static User currUser;
    private static String password;
    private static FakeUserGenerator gen;
    private CountDownLatch latch = new CountDownLatch(1);

    @BeforeClass
    public static void setUp() {
        mDatabase = FirebaseFirestore.getInstance();
        user_auth = FirebaseAuth.getInstance();
    }

    public UserDBInstrumentedTest(User u, String pass) {
        currUser = u;
        password = pass;
    }

    @Parameterized.Parameters
    public static Collection validUsers() {
        Collection args = new ArrayList<Object[]>();
        gen = new FakeUserGenerator();

        for (int i = 0; i < 5; i++) {
            User currUser = gen.genNewUser();
            gen.doRandomWorkouts(currUser, 3);
            args.add(new Object[]{currUser, "C0rrfecthorsebatterystaple" + i});
        }
        return args;
    }

    @Test
    public void testAUserToDAO() {
        String userId = currUser.getUserID();

        UserDAO dao = new UserDAO(currUser);
        User recreatedUser = new User(dao, userId);

        assertEquals(currUser, recreatedUser);
    }

    @Test
    public void testBUserToDB() {
        AtomicBoolean creationSuccess = new AtomicBoolean(false);
        AtomicBoolean storeSuccess = new AtomicBoolean(false);
        UserDAO toStore = new UserDAO(currUser);

        user_auth.createUserWithEmailAndPassword(toStore.username, password)
                .addOnSuccessListener(task -> {
                    creationSuccess.set(true);
                    mDatabase.collection(Constants.USERS)
                            .document(user_auth.getCurrentUser().getUid())
                            .set(toStore).addOnCompleteListener(task1 -> {
                                storeSuccess.set(task1.isSuccessful());
                                latch.countDown();
                            });
                }).addOnFailureListener(task -> {
                    creationSuccess.set(false);
                    latch.countDown();
                });

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(creationSuccess.get());
        assertTrue(storeSuccess.get());
    }
}