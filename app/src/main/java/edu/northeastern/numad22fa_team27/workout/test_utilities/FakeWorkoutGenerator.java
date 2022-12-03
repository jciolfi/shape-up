package edu.northeastern.numad22fa_team27.workout.test_utilities;

import com.google.common.base.CaseFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class FakeWorkoutGenerator {
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

    public Workout newRandomWorkout(WorkoutCategory principleCategory) {
        return new Workout(UUID.randomUUID().toString(), genName(principleCategory), genDescription(principleCategory), genCategoryList(principleCategory), genDifficulty());
    }

    public Workout newRandomWorkout() {
        WorkoutCategory principleCategory = WorkoutCategory.values()[random.nextInt(WorkoutCategory.values().length - 1)];
        return new Workout(UUID.randomUUID().toString(), genName(principleCategory), genDescription(principleCategory), genCategoryList(principleCategory), genDifficulty());
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
