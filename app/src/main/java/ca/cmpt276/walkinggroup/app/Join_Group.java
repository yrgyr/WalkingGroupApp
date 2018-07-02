package ca.cmpt276.walkinggroup.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;

public class Join_Group extends AppCompatActivity {

    private Group group = Group.getGroupSingletonInstance();
    int grpId = group.getGroupId();
    String grpDesc = group.getGroupDescription();
    String leaderName = group.getLeader().getName();
    String[] members = group.getGroupMembersNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join__group);

        populateGroupID();
        populateGroupDesc();
        populateGroupLeader();
        populateGroupMembersListView();
        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.join_group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_join_group:
                Toast.makeText(Join_Group.this, "You have joined group " + grpDesc + "!", Toast.LENGTH_SHORT).show();
                // Todo: server codes to join group

                finish();
                break;
            case R.id.menu_add_user:
                // Todo: launch add user activity
                break;
            case R.id.menu_remove_user:
                // Todo: launch remove user activity
                List<Integer> membersToRemove = new ArrayList<>();
                showRemoveMembersDialog(membersToRemove);
                break;
            case R.id.menu_leave_group:
                boolean isInGroup = true;
                // Todo: server codes to check if I'm currently in this group

                if (isInGroup){
                    // Todo: server codes to remove myself from this group
                    Toast.makeText(this, "You have left group " + grpDesc, Toast.LENGTH_SHORT).show();
                    isInGroup = false;
                    break;
                } else{
                    Toast.makeText(this, "You're not currently in group " + grpDesc, Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.menu_go_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_member, members);
        ListView membersList = findViewById(R.id.join_grp_members_listview);
        membersList.setAdapter(adapter);

    }

    private void setupActionBar(){
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
    }

    private void showRemoveMembersDialog(List<Integer> membersToRemove){
        boolean[] checkedMembers = new boolean[members.length];

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Join_Group.this);
        alertBuilder.setTitle("Select group members to remove:");
        alertBuilder.setMultiChoiceItems(members, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                //String userNameAtPosition = members[position];
                if(isChecked){
                    if(!membersToRemove.contains(position)){
                        membersToRemove.add(position);
                    }
                } else if(membersToRemove.contains(position)){
                    membersToRemove.remove(position);
                }
            }
        });

        alertBuilder.setCancelable(true);
        //boolean cancelAlertDialog = false;

        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (membersToRemove.size()> 0) {
                    List<User> updatedMembers = group.getGroupMembers();

                    for (int i = 0; i < updatedMembers.size(); i++){
                        Log.e("MyApp", "Index " + i + ", user: " + updatedMembers.get(i).getName() + "\n");
                    }

                    for (int i = 0; i < membersToRemove.size(); i++) {
                        int position = membersToRemove.get(i);
                        //Toast.makeText(Join_Group.this, "Selected member in position: " + position, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Join_Group.this, "Member name in list: " + updatedMembers.get(position).getName(), Toast.LENGTH_LONG).show();
                        updatedMembers.remove(position);
                    }
                    group.setGroupMembers(updatedMembers);
                    members = group.getGroupMembersNames();
                    populateGroupMembersListView();
                } else {
                    Toast.makeText(Join_Group.this, "No members selected", Toast.LENGTH_LONG).show();
                }

            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog aDialog = alertBuilder.create();
        aDialog.show();

    }

}
