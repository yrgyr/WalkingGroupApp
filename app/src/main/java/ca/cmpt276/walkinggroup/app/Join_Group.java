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

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.MapsActivity.groupSelected;

public class Join_Group extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private User currentUser = userSingleton.getCurrentUser();
    private List<User> groupMembers = groupSelected.getMemberUsers();

    //private Group group = groupSelected;
    Long grpId = groupSelected.getId();
    String grpDesc = groupSelected.getGroupDescription();
    String leaderName = groupSelected.getLeader().getName();

    Long currentUserId = currentUser.getId();
    String[] members = groupSelected.getGroupMembersNames();

    //long[] membersIds = group.getGroupMembersIds();
    private List<User> monitorsUsers = new ArrayList<>();


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
                //Toast.makeText(Join_Group.this, "You have joined group " + grpDesc + "!", Toast.LENGTH_SHORT).show();
                addUserToGroup(grpId, currentUser);
                finish();
                break;
            case R.id.menu_add_user:
                addRemotesUsers(currentUser.getId());
                break;
            case R.id.menu_remove_user:
                removeRemoteUsers(currentUser.getId());
                break;
            case R.id.menu_leave_group:
//                Long currentUserId = currentUser.getId();
                User leader = groupSelected.getLeader();
                Long leaderId = leader.getId();

                if (currentUserId == leaderId){
                    Toast.makeText(Join_Group.this, "You're the leader of this group; can't leave this group", Toast.LENGTH_LONG).show();
                } else {
                    leaveGroup(grpId, currentUserId);
                    finish();
                }
                break;
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
        members = groupSelected.getGroupMembersNames();

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
            List<User> groupMembers = groupSelected.getMemberUsers();
            // get String[] of monitorsUsers who are in group members

            if (groupMembers.size() > 0) {
                List<String> monitorsInMembersList = new ArrayList<>();
                for (int i = 0; i < monitorsUsers.size(); i++) {
                    User user = monitorsUsers.get(i);
                    Long monitorUserId = user.getId();

                    // Check if the monitors user is one of the group members
                    for (int j = 0; j < groupMembers.size(); j++) {
                        Long groupUserId = groupMembers.get(j).getId();
                        if (monitorUserId == groupUserId) {
                            String userName = groupMembers.get(j).getName();
                            monitorsInMembersList.add(userName);
                        }
                    }

                }
                membersList = monitorsInMembersList.toArray(new String[monitorsInMembersList.size()]);
            } else {
                membersList = new String[0];
            }

        }

        boolean[] checkedMembers = new boolean[membersList.length];
        List<Integer> membersToRemove = new ArrayList<>();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Join_Group.this);
        alertBuilder.setTitle(R.string.remove_dialogue_text);
        alertBuilder.setMultiChoiceItems(membersList, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if(isChecked){
                    if(!membersToRemove.contains(Integer.valueOf(position))){
                        membersToRemove.add(Integer.valueOf(position));
                    }
                } else if(membersToRemove.contains(Integer.valueOf(position))){
                    membersToRemove.remove(Integer.valueOf(position));
                }
            }
        });

        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(R.string.dialogue_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (membersToRemove.size()> 0) {
                    List<User> originalMembers = groupSelected.getMemberUsers();
                    for (int i = 0; i < membersToRemove.size(); i++){
                        int j = membersToRemove.get(i);
                        User user = originalMembers.get(j);
                        Long userId = user.getId();
                        removeUserFromGroup(grpId, userId);
                    }
                    populateGroupMembersListView();
                }
                finish();

            }
        });

        alertBuilder.setNegativeButton(R.string.dialogue_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog aDialog = alertBuilder.create();
        aDialog.show();

    }

    private void showAddMembersDialogue(){
        String[] monitorsUsersArr = new String[monitorsUsers.size()];
        boolean[] checkedMembers = new boolean[monitorsUsers.size()];
        List<Integer> membersToAdd = new ArrayList<>();

        for (int i = 0; i < monitorsUsers.size(); i++){
            monitorsUsersArr[i] = monitorsUsers.get(i).getName();
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Join_Group.this);
        alertBuilder.setTitle(R.string.alert_add_members);
        alertBuilder.setMultiChoiceItems(monitorsUsersArr, checkedMembers, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if(isChecked){
                    if(!membersToAdd.contains(Integer.valueOf(position))){
                        membersToAdd.add(Integer.valueOf(position));
                        //Toast.makeText(Join_Group.this, "You have selected pos: " + position, Toast.LENGTH_LONG).show();
                    }
                } else if(membersToAdd.contains(Integer.valueOf(position))){
                    membersToAdd.remove(Integer.valueOf(position));
                }
            }
        });

        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(R.string.dialogue_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (membersToAdd.size()> 0) {

                    for (int i = 0; i < membersToAdd.size(); i++) {
                        Integer j = membersToAdd.get(i);
                        User user = monitorsUsers.get(j);
                        addUserToGroup(grpId, user);
                    }
                    populateGroupMembersListView();
                }


            }
        });

        alertBuilder.setNegativeButton(R.string.dialogue_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog aDialog = alertBuilder.create();
        aDialog.show();
    }

    private boolean checkIfUserIsLeader(){
        if (currentUser.getId() == groupSelected.getGroupId()){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfIAmInGroup() {
        getRemoteGroupMembers(grpId);

        for (int i = 0; i < groupMembers.size(); i++){
            User user = groupMembers.get(i);
            if (currentUser.getId() == user.getId()){
                return true;
            }
        }

        return false;

    }


    private void getRemoteMonitorsUsers(Long userId){
        Call<List<User>> caller = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedUsers -> returnedMonitorsUser(returnedUsers));
    }

    private void returnedMonitorsUser(List<User> users){
        monitorsUsers = users;
    }

    private void addRemotesUsers(Long userId){
        Call<List<User>> caller = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedUsers -> responseAddRemoteUser(returnedUsers));
    }

    private void responseAddRemoteUser(List<User> users){
        monitorsUsers = users;
        showAddMembersDialogue();
    }


    private void removeRemoteUsers(Long userId){
        Long leaderId = groupSelected.getLeader().getId();
        if (userId != leaderId){
            Call<List<User>> caller = proxy.getMonitorsUsers(userId);
            ProxyBuilder.callProxy(Join_Group.this, caller, returnedUsers -> responseRemoveRemoteUsersNonLeader(returnedUsers));
        } else {
            showRemoveMembersDialog(true);
        }
    }

    private void responseRemoveRemoteUsersNonLeader(List<User> users){
        monitorsUsers = users;
        showRemoveMembersDialog(false);
    }

    private void addUserToGroup(Long groupId, User user){
        Call<List<User>> caller = proxy.addGroupMember(groupId, user);
        ProxyBuilder.callProxyForAddUserToGroup(Join_Group.this, caller, returnedUsers -> responseAddUsers(returnedUsers));
    }

    private void getRemoteGroupMembers(Long groupId){
        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedMembers -> returnGroupMembers(returnedMembers));
    }

    private void returnGroupMembers(List<User> returnedMembers){
        groupMembers = returnedMembers;
    }

    private void removeUserFromGroup(Long groupId, Long userId){
        Call<Void> caller = proxy.removeGroupMember(groupId, userId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedNothing -> responseRemoveUsers(returnedNothing));

    }


    private void responseAddUsers(List<User> users){
        groupMembers = users;
        Toast.makeText(Join_Group.this, R.string.Toast_user_added, Toast.LENGTH_LONG).show();
        finish();
    }

    private void responseRemoveUsers(Void returnedNothing){
        Toast.makeText(Join_Group.this, R.string.Toast_user_removed, Toast.LENGTH_LONG).show();
        finish();
    }

    private void leaveGroup(Long groupId, Long userId){
        Call<Void> caller = proxy.removeGroupMember(groupId, userId);
        ProxyBuilder.callProxyForLeavingGroup(Join_Group.this, caller, returnedNothing -> responseLeaveGroup(returnedNothing));

    }

    private void responseLeaveGroup(Void returnedNothing){
        Toast.makeText(Join_Group.this, R.string.Toast_left_group, Toast.LENGTH_LONG).show();
        finish();
    }

}
