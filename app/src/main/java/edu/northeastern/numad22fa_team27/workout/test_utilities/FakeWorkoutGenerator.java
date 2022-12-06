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
    private String[] workoutCovers;

    public FakeWorkoutGenerator() {
        workoutCovers = new String[]{
                "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
                "https://plus.unsplash.com/premium_photo-1665673312770-90df9f77ddfa?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8N3x8Z3ltfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
                "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1576678927484-cc907957088c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
                "https://images.unsplash.com/photo-1549060279-7e168fcee0c2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://plus.unsplash.com/premium_photo-1661281320733-ec1d612aee68?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1549476464-37392f717541?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
                "https://images.unsplash.com/photo-1593476123561-9516f2097158?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
                "https://images.unsplash.com/photo-1596357395217-80de13130e92?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1742&q=80",
                "https://images.unsplash.com/photo-1604480133435-25b86862d276?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
        };
    }

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

    public String genCoverImage() {
        return this.workoutCovers[random.nextInt(workoutCovers.length - 1)];
    }

    public Workout newRandomWorkout(WorkoutCategory principleCategory) {
        return new Workout(
                UUID.randomUUID().toString(),
                genName(principleCategory),
                genDescription(principleCategory),
                genCategoryList(principleCategory),
                genDifficulty(),
                genCoverImage(),
                String.format("This workout has an epic description %s...", lipsum(6))
        );
    }

    public Workout newRandomWorkout() {
        WorkoutCategory principleCategory = WorkoutCategory.values()[random.nextInt(WorkoutCategory.values().length - 1)];
        return newRandomWorkout(principleCategory);
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
