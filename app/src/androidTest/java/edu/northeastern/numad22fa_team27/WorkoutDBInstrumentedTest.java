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
    private static AttributeGenerator gen;
    private CountDownLatch latch = new CountDownLatch(1);

    static class AttributeGenerator {
        private static final String TAG = "AttributeGenerator";
        private static Map<WorkoutCategory, List<String>> workoutDiagramLinks;
        private Random random = new java.util.Random();
        private static int namesGenerated = 0;

        public int loadAttributes(String jsonPayload) {
            JSONArray data;
            workoutDiagramLinks = new HashMap<>();
            try {
                data = new JSONArray(jsonPayload);
            } catch (Exception e) {
                System.err.println("Could not load array");
                return 0;
            }

            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject entry = data.getJSONObject(i);
                    String link = entry.getString("Link");

                    // Insert the link with every topic
                    JSONArray topics = entry.getJSONArray("Topics");
                    for (int j = 0; j < topics.length(); j++) {
                        WorkoutCategory currTopic = WorkoutCategory.valueOf(topics.getString(j));
                        if (!workoutDiagramLinks.containsKey(currTopic)) {
                            workoutDiagramLinks.put(currTopic, new ArrayList<>());
                        }
                        workoutDiagramLinks.get(currTopic).add(link);
                    }
                } catch (JSONException e) {
                    System.err.println("Could not load link entry");
                    return 1;
                }
            }
            return 2;
        }


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

        private String topicalDiagrams(WorkoutCategory w) {
            if (workoutDiagramLinks.containsKey(w)) {
                List<String> data = workoutDiagramLinks.get(w);

                // Get a random diagram
                return data.get(random.nextInt(data.size() - 1));
            }

            // We have no links
            return null;
        }

        public List<MediaParagraph> genDescription(WorkoutCategory w) {
            List<MediaParagraph> description = new ArrayList<>();
            for (int i = Math.max(2, random.nextInt(10)); i > 0; i--) {
                description.add(new MediaParagraph(lipsum(random.nextInt(40)), topicalDiagrams(w)));
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

                // Pick a workout category that isn't in our existing list
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
        mDatabase = FirebaseFirestore.getInstance();
    }

    @AfterClass
    public static void tearDown() {
        mDatabase.collection(WORKOUT_TABLE).document(currWorkout.getWorkoutID()).set(null);
    }

    public WorkoutDBInstrumentedTest(String id, String name, List<MediaParagraph> text, List<WorkoutCategory> categories, float difficulty) {
        currWorkout = new Workout(id, name, text, categories, difficulty);
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

        Collection args = new ArrayList<Object[]>();
        gen = new AttributeGenerator();
        assertEquals(gen.loadAttributes(json), 2);

        for (int i = 0; i < 100; i++) {
            for (WorkoutCategory category : WorkoutCategory.values()) {
                args.add(new Object[]{ UUID.randomUUID().toString(), gen.genName(category), gen.genDescription(category), gen.genCategoryList(category), gen.genDifficulty() });
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