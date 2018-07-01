package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;

public class Join_Group extends AppCompatActivity {

    private Group group = Group.getGroupSingletonInstance();
    int grpId = group.getGroupId();
    String grpDesc = group.getGroupDescription();
    String leaderName = group.getLeader().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join__group);

        populateGroupID();
        populateGroupDesc();
        populateGroupLeader();
        populateGroupMembersListView();
        setupJoinGroupButton();
        setupCancelJoinButton();
    }

    private void populateGroupID() {
        TextView txtGrpId = findViewById(R.id.txt_join_grp_grpid_val);
        txtGrpId.setText("" + grpId);
    }

    private void populateGroupDesc() {
        TextView txtGrpDesc = findViewById(R.id.txt_join_grp_grpdesc_val);
        txtGrpDesc.setText(grpDesc);
    }

    private void populateGroupLeader(){
        TextView txtGrpLeader = findViewById(R.id.txt_join_group_grp_leader_val);
        txtGrpLeader.setText(leaderName);
    }

    private void populateGroupMembersListView() {
        String[] members = group.getGroupMembersNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_member, members);
        ListView membersList = findViewById(R.id.join_grp_members_listview);
        membersList.setAdapter(adapter);

    }

    private void setupJoinGroupButton() {
        Button btn = findViewById(R.id.btn_join_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Join_Group.this, "You have joined group " + grpId + "!", Toast.LENGTH_SHORT).show();

                // Implement server side codes here

                finish();
            }
        });
    }

    private void setupCancelJoinButton() {
        Button btn = findViewById(R.id.btn_join_group_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
