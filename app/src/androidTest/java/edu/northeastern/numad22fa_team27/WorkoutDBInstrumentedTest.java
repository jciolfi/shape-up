package edu.northeastern.numad22fa_team27;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.test_utilities.FakeWorkoutGenerator;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkoutDBInstrumentedTest {
    private static final String WORKOUT_TABLE = "workouts";
    private static FirebaseFirestore mDatabase;
    private static Workout currWorkout;
    private static FakeWorkoutGenerator gen;
    private CountDownLatch latch = new CountDownLatch(1);

    @BeforeClass
    public static void setUp() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    @AfterClass
    public static void tearDown() {
        mDatabase.collection(WORKOUT_TABLE).document(currWorkout.getWorkoutID()).set(null);
    }

    public WorkoutDBInstrumentedTest(Workout w) {
        currWorkout = w;
    }

    @Parameterized.Parameters
    public static Collection validWorkouts() {
        URL url = Resources.getResource("workout_links.json");
        String json = null;
        try {
            json = Resources.toString(url, Charsets.UTF_8);
        } catch (Exception ex) {

        }
        assertNotNull(json);
        assertFalse(json.isEmpty());

        Collection args = new ArrayList<Workout>();
        gen = new FakeWorkoutGenerator();
        assertEquals(gen.loadAttributes(json), 2);

        for (int i = 0; i < 100; i++) {
            for (WorkoutCategory category : WorkoutCategory.values()) {
                args.add(gen.newRandomWorkout(category));
            }
        }
        return args;
    }

    @Test
    public void testAWorkoutToDB() {
        AtomicBoolean success = new AtomicBoolean(false);

        // Context of the app under test.
        mDatabase.collection(WORKOUT_TABLE).document(currWorkout.getWorkoutID()).set(currWorkout)
                .addOnSuccessListener(unused -> {
                    success.set(true);
                    latch.countDown();
                }).addOnFailureListener(e -> {
                    success.set(false);
                    latch.countDown();
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(success.get());
    }

    @Test
    public void testBWorkoutFromDB() {
        // Context of the app under test.
        AtomicBoolean success = new AtomicBoolean(false);
        mDatabase.collection(WORKOUT_TABLE).document(currWorkout.getWorkoutID()).get()
                .addOnSuccessListener(document -> {
                        assertTrue(document.exists());

                        Workout retrievedWorkout = document.toObject(Workout.class);

                        assertNotNull(retrievedWorkout);

                        assertEquals(retrievedWorkout.getDifficulty(), currWorkout.getDifficulty(), 0);
                        assertTrue(retrievedWorkout.getWorkoutName().equals(currWorkout.getWorkoutName()));
                        assertEquals(retrievedWorkout.getWorkoutID().hashCode(), currWorkout.getWorkoutID().hashCode());
                        assertArrayEquals(retrievedWorkout.getWorkoutDescription().toArray(), currWorkout.getWorkoutDescription().toArray());
                        assertArrayEquals(retrievedWorkout.getCategoriesPresent().toArray(), currWorkout.getCategoriesPresent().toArray());

                        success.set(true);
                        latch.countDown();
                    }
                ).addOnFailureListener(l -> {
                        success.set(false);
                        latch.countDown();
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(success.get());
    }
}