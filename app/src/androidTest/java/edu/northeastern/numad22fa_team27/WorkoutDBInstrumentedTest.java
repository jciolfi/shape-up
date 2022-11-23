package edu.northeastern.numad22fa_team27;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.google.common.base.CaseFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.sticker_messenger.FirebaseUserDaoConverter;
import edu.northeastern.numad22fa_team27.workout.converters.WorkoutSnapshotConverter;
import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkoutDBInstrumentedTest {
    private static final String WORKOUT_TABLE = "workouts";
    private static DatabaseReference mDatabase;
    private static Workout currWorkout;
    private CountDownLatch latch = new CountDownLatch(1);

    static class AttributeGenerator {
        private Random random = new java.util.Random();
        private static int namesGenerated = 0;

        private String lipsum(int numWords) {
            String[] lexicon = new String[] {
                    "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit"
            };
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numWords; i++) {
                sb.append(lexicon[random.nextInt(lexicon.length - 1)]);
                sb.append(' ');
            }
            return sb.toString();
        }

        public List<MediaParagraph> genDescription() {
            List<MediaParagraph> description = new ArrayList<>();
            for (int i = random.nextInt(10); i >0; i--) {
                description.add(new MediaParagraph(lipsum(random.nextInt(20)), "LINK HERE"));
            }
            return description;
        }

        public String genName(WorkoutCategory w) {
            namesGenerated++;
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, w.name()) + "Workout" + Integer.toString(namesGenerated);
        }

        /**
         * Generate a short list of categories that include the provided category
         * @param w Workout that is guaranteed to be included in the results
         * @return 1-2 random workout categories in addition to the inputted workout type
         */
        public List<WorkoutCategory> genCategoryList(WorkoutCategory w) {
            List<WorkoutCategory> categories = new ArrayList<>();
            categories.add(w);
            for (int i = 0; i < 2; i++) {

                // Allow us to quit early
                if (random.nextBoolean()) {
                    break;
                }

                // Pick a workout category that isn't in our exisitng list
                int sz = random.nextInt(WorkoutCategory.values().length - 1);
                while(categories.contains(WorkoutCategory.values()[sz])) {
                    sz = random.nextInt(WorkoutCategory.values().length - 1);
                }
                categories.add(WorkoutCategory.values()[sz]);
            }

            return categories;
        }

        public float genDifficulty() {
            return Math.round(random.nextFloat() * 20) / 4f;
        }
    }

    @BeforeClass
    public static void setUp() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @AfterClass
    public static void tearDown() {
        mDatabase.child(WORKOUT_TABLE).child(currWorkout.getWorkoutID().toString()).setValue(null);
    }

    public WorkoutDBInstrumentedTest(UUID id, String name, List<MediaParagraph> text, List<WorkoutCategory> categories, float difficulty) {
        currWorkout = new Workout(id, name, text, categories, difficulty);
    }

    @Parameterized.Parameters
    public static Collection validWorkouts() {
        AttributeGenerator gen = new AttributeGenerator();
        Collection args = new ArrayList<Object[]>();

        for (WorkoutCategory category : WorkoutCategory.values()) {
            args.add(new Object[]{ UUID.randomUUID(), gen.genName(category), gen.genDescription(), gen.genCategoryList(category), gen.genDifficulty() });
        }
        return args;
    }

    @Test
    public void testAWorkoutToDB() {
        AtomicBoolean success = new AtomicBoolean(false);

        // Context of the app under test.
        mDatabase.child(WORKOUT_TABLE).child(currWorkout.getWorkoutID().toString()).setValue(currWorkout)
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
        mDatabase.child(WORKOUT_TABLE).child(currWorkout.getWorkoutID().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        assertTrue(snapshot.exists());

                        Workout retrievedWorkout = new WorkoutSnapshotConverter().unpack(snapshot);

                        assertNotNull(retrievedWorkout);

                        assertEquals(retrievedWorkout.getDifficulty(), currWorkout.getDifficulty(), 0);
                        assertTrue(retrievedWorkout.getWorkoutName().equals(currWorkout.getWorkoutName()));
                        assertEquals(retrievedWorkout.getWorkoutID().hashCode(), currWorkout.getWorkoutID().hashCode());
                        assertArrayEquals(retrievedWorkout.getWorkoutDescription().toArray(), currWorkout.getWorkoutDescription().toArray());
                        assertArrayEquals(retrievedWorkout.getCategoriesPresent().toArray(), currWorkout.getCategoriesPresent().toArray());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}