package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Constants.GROUPS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class GroupDisplay extends AppCompatActivity {
    private TextView groupTitle;
    private TextView adminStatus;
    private TextView memberCount;
    private Switch privacySwitch;
    private Button actionButton;
    private final FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private final FirestoreService firestoreService = new FirestoreService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_display);

        groupTitle = findViewById(R.id.groupTitle);
        adminStatus = findViewById(R.id.adminStatus);
        memberCount = findViewById(R.id.memberCount);
        privacySwitch = findViewById(R.id.privacySwitch);
        actionButton = findViewById(R.id.groupActionButton);

        Intent i = getIntent();
        extractInformation(i);
        setActionButton(i);
    }

    private void extractInformation(Intent intent) {
        groupTitle.setText(intent.getStringExtra("GROUP_NAME"));
    }

    private void setActionButton(Intent intent) {
        String groupID = intent.getStringExtra("GROUP_ID");
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (groupID == null || fbUser == null) {
            actionButton.setVisibility(View.INVISIBLE);
            privacySwitch.setEnabled(false);
            return;
        }

        String currentUserID = fbUser.getUid();

        // set fields that if changed, have an impact (e.g. admin, membership status)
        actionButton.setVisibility(View.VISIBLE);
        firestoreDB.collection(GROUPS)
                .document(groupID)
                .get()
                .addOnSuccessListener(groupSnapshot -> {
                    GroupDAO selectedGroup = groupSnapshot.toObject(GroupDAO.class);
                    if (selectedGroup == null) {
                        return;
                    }
                    boolean isAdmin = selectedGroup.adminID.equals(currentUserID);

                    // set up group members
                    memberCount.setText(String.format("MEMBERS: %s", selectedGroup.members.size()));

                    actionButton.setVisibility(View.INVISIBLE);

                    // set up action button
                    if (selectedGroup.members.contains(currentUserID)) {
                        actionButton.setText("LEAVE");
                        actionButton.setOnClickListener(v -> {
                            if (isAdmin) {
                                Toast.makeText(this, "You can't leave a group if you're the admin!", Toast.LENGTH_SHORT).show();
                            } else {
                                firestoreService.leaveGroup(groupID);
                                onBackPressed();
                            }
                        });
                    } else {
                        actionButton.setText("JOIN");
                        actionButton.setOnClickListener(v -> {
                            if (selectedGroup.acceptingMembers) {
                                firestoreService.tryJoinGroup(groupID);
                                onBackPressed();
                            } else {
                                Toast.makeText(this, "This group is not currently accepting members.", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }

                    // set up privacy switch
                    privacySwitch.setChecked(selectedGroup.acceptingMembers);
                    if (isAdmin) {
                        adminStatus.setVisibility(View.VISIBLE);
                        privacySwitch.setEnabled(true);
                        privacySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                            firestoreService.tryChangeGroupPrivacy(groupID, compoundButton.isChecked());
                        });
                    } else {
                        adminStatus.setVisibility(View.INVISIBLE);
                        privacySwitch.setEnabled(false);
                    }

                    actionButton.setEnabled(true);
                    actionButton.setVisibility(View.VISIBLE);
                });
    }
}
