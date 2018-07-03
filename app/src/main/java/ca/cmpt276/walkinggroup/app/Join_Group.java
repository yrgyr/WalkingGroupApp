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
    //long[] membersIds = group.getGroupMembersIds();
    List<User> monitorsUsers = new ArrayList<>(); // Todo: get the array of monitors users by calling getMonitorsUsers


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
                showAddMembersDialogue();
                break;
            case R.id.menu_remove_user:
                boolean isLeader = checkIfUserIsLeader();
                if(members.length>0) {
                    showRemoveMembersDialog(isLeader);
                } else {
                    Toast.makeText(Join_Group.this, "This group is currently empty!", Toast.LENGTH_SHORT).show();
                }
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
        members = group.getGroupMembersNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group_member, members);
        ListView membersList = findViewById(R.id.join_grp_members_listview);
        membersList.setAdapter(adapter);

    }

    private void setupActionBar(){
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
    }

    private void showRemoveMembersDialog(boolean isLeader){
        String[] membersList;
        if (isLeader){
            membersList = members;
        } else {
            membersList = (String[]) monitorsUsers.toArray();  // need to convert to array format for setMultiChoiceItems
        }

        boolean[] checkedMembers = new boolean[membersList.length];
        List<Integer> membersToRemove = new ArrayList<>();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Join_Group.this);
        alertBuilder.setTitle("Select group members to remove:");
        alertBuilder.setMultiChoiceItems(membersList, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
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
                    List<User> originalMembers = group.getGroupMembers();
                    for (int i = 0; i < membersToRemove.size(); i++){
                        int j = membersToRemove.get(i);
                        User user = originalMembers.get(j);
                        long userId = user.getId();
                        deleteLocalGroupMembersById(userId);  // Todo: replace with removeFromMonitorsUsers server call
                    }
                    populateGroupMembersListView();
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

    private void showAddMembersDialogue(){
        createLostMonitorsUser(); // todo: delete this later
        String[] monitorsUsersArr = new String[monitorsUsers.size()];
        boolean[] checkedMembers = new boolean[monitorsUsers.size()];
        List<Integer> membersToAdd = new ArrayList<>();

        // Todo: add check to see if any monitorsUsers is already in group?
        // Create monitorsUsersArr (names of monitorsUsers) from monitorsUsers List
        for (int i = 0; i < monitorsUsers.size(); i++){
            monitorsUsersArr[i] = monitorsUsers.get(i).getName();
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Join_Group.this);
        alertBuilder.setTitle("Select group members to add:");
        alertBuilder.setMultiChoiceItems(monitorsUsersArr, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if(isChecked){
                    if(!membersToAdd.contains(position)){
                        membersToAdd.add(position);
                    }
                } else if(membersToAdd.contains(position)){
                    membersToAdd.remove(position);
                }
            }
        });

        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (membersToAdd.size()> 0) {

                    for (int i = 0; i < membersToAdd.size(); i++) {
                        User user = monitorsUsers.get(i);
                        addLocalUserToGroup(user);  // Todo: replace with addGroupMember call to server
                    }
                    populateGroupMembersListView();
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

    private boolean checkIfUserIsLeader(){
        // Todo: check with server if the current user is the group leader
        return true;
    }

    // Todo: delete this testing method later
    private void addLocalUserToGroup(User user){
        List<User> updatedMembers = group.getGroupMembers();
        updatedMembers.add(user);
        group.setGroupMembers(updatedMembers);
    }

    // Todo: delete this later
    private void deleteLocalGroupMembersById(long userId){
        List<User> originalMembers = group.getGroupMembers();
        List<User> updatedMembers = new ArrayList<>();

        for (int i = 0; i < originalMembers.size(); i++){
            User user = originalMembers.get(i);
            if (user.getId() != userId){
                updatedMembers.add(user);
            }
        }

        group.setGroupMembers(updatedMembers);
    }

    // Todo: delete this later
    private void createLostMonitorsUser(){
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setName("Monitor " + i);
            monitorsUsers.add(user);
        }
    }

    private long[] createMonitorsUserIds(){
        long[] Ids = new long[monitorsUsers.size()];
        for(int i = 0; i < monitorsUsers.size(); i++){
            User user = monitorsUsers.get(i);
            Ids[i] = user.getId();
        }

        return Ids;
    }


}
