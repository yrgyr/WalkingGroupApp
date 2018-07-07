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


    String[] members = groupSelected.getGroupMembersNames();  // Todo: replace with groupMembers

    //long[] membersIds = group.getGroupMembersIds();
    private List<User> monitorsUsers = new ArrayList<>(); // Todo: get the array of monitors users by calling getMonitorsUsers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join__group);

        Log.e("grpID in join group:", "" + grpDesc);


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
                // Todo: call addUserToGroup with current user's ID
                addUserToGroup(grpId, currentUser);
                finish();
                break;
            case R.id.menu_add_user:
                getRemoteMonitorsUsers(currentUser.getId());
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
                // Todo: implement error message to show up when I'm not in the group
                //boolean isInGroup = checkIfIAmInGroup();

//                if (isInGroup){
//                    removeUserFromGroup(grpId, currentUser.getId());
//                    Toast.makeText(this, "You have left group " + grpDesc, Toast.LENGTH_SHORT).show();
//                    break;
//                } else{
//                    Toast.makeText(this, "You're not currently in group " + grpDesc, Toast.LENGTH_SHORT).show();
//                    break;
//                }
                Long currentUserId = currentUser.getId();
                removeUserFromGroup(grpId, currentUserId);
                Toast.makeText(Join_Group.this, "You have left this group!", Toast.LENGTH_SHORT).show();
                finish();
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
        // Todo: for testing
        //List<User> membersTest = groupSelected.getMemberUsers();
//        List<User> membersTest = groupMembers;
//        User testUser = membersTest.get(0);
//        Log.e("Join_Group", "First member ID: " + testUser.getId());
//        Log.e("Join_Group", "First member name: " + testUser.getName());
        members = groupSelected.getGroupMembersNames();
        //Log.e("Join_Group", "Group size: " + groupSelected.getGroupSize());
        //Log.e("Join_Group", "UserId: " + )
        //Log.e("Join_Group", "members[0]: " + members[0]);

//        if (members == null){
//            members = new String[0];
//        }

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
                    List<User> originalMembers = groupSelected.getMemberUsers();
                    for (int i = 0; i < membersToRemove.size(); i++){
                        int j = membersToRemove.get(i);
                        User user = originalMembers.get(j);
                        Long userId = user.getId();
                        //deleteLocalGroupMembersById(userId);  // Todo: replace with removeFromMonitorsUsers server call
                        removeUserFromGroup(grpId, userId);
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
        //createLostMonitorsUser(); // todo: delete this later
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

        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (membersToAdd.size()> 0) {

                    for (int i = 0; i < membersToAdd.size(); i++) {
                        Integer j = membersToAdd.get(i);
                        User user = monitorsUsers.get(j);
                        //addLocalUserToGroup(user);  //
                        //Toast.makeText(Join_Group.this, "You're adding user: " + user.getName(), Toast.LENGTH_LONG).show();
                        addUserToGroup(grpId, user);
                    }
                    populateGroupMembersListView();
                }

                finish();

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

    // Todo: delete this testing method later
    private void addLocalUserToGroup(User user){
        List<User> updatedMembers = groupSelected.getMemberUsers();
        updatedMembers.add(user);
        groupSelected.setMemberUsers(updatedMembers);
    }

    // Todo: delete this later
    private void deleteLocalGroupMembersById(long userId){
        List<User> originalMembers = groupSelected.getMemberUsers();
        List<User> updatedMembers = new ArrayList<>();

        for (int i = 0; i < originalMembers.size(); i++){
            User user = originalMembers.get(i);
            if (user.getId() != userId){
                updatedMembers.add(user);
            }
        }

        groupSelected.setMemberUsers(updatedMembers);
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

    private void getRemoteMonitorsUsers(Long userId){
        Call<List<User>> caller = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedUsers -> returnedMonitorsUser(returnedUsers));
    }

    private void returnedMonitorsUser(List<User> users){
        monitorsUsers = users;
        showAddMembersDialogue();

    }

    private void addUserToGroup(Long groupId, User user){
        Call<List<User>> caller = proxy.addGroupMember(groupId, user);
        ProxyBuilder.callProxyForAddUserToGroup(Join_Group.this, caller, returnedUsers -> returnedMembers(returnedUsers));
    }

    private void getRemoteGroupMembers(Long groupId){
        Call<List<User>> caller = proxy.getGroupMembers(groupId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedMembers -> returnGroupMembers(returnedMembers));
    }

    private void returnGroupMembers(List<User> returnedMembers){
        groupMembers = returnedMembers;
        //List<User> = groupMembers.
    }

    private void removeUserFromGroup(Long groupId, Long userId){
        Call<Void> caller = proxy.removeGroupMember(groupId, userId);
        ProxyBuilder.callProxy(Join_Group.this, caller, returnedNothing -> response(returnedNothing));

    }

    private void returnedMembers(List<User> users){
        groupMembers = users;
        Log.e("Join_Group", "User list length after adding: " + groupMembers.size());
    }

    private void response(Void returnedNothing){

    }
}
