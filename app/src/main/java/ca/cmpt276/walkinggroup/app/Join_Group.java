package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Path;

import static ca.cmpt276.walkinggroup.proxy.WGServerProxy.PermissionStatus.APPROVED;


public class Join_Group extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private User currentUser = userSingleton.getCurrentUser();
    private Group groupSelected = userSingleton.getGroupSelected();
    private List<User> groupMembers = groupSelected.getMemberUsers();

    private List<Long> validUser = new ArrayList<Long>();
    private boolean isValid = false;

    Long grpId = groupSelected.getId();
    String grpDesc = groupSelected.getGroupDescription();
    String leaderName = groupSelected.getLeader().getName();;
    Long leaderId = groupSelected.getLeader().getId();;

    Long currentUserId = currentUser.getId();
    String[] members = groupSelected.getGroupMembersNames();
    long[] membersId = groupSelected.getGroupMembersIds();
    private List<User> monitorsUsers = new ArrayList<>();

    private boolean IAmInThisGroup = false;

    private boolean uploadingLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join__group);


        checkIfIAmInGroup();


        setUpValidUserCanCheckInfo();

        populateGroupID();
        populateGroupDesc();
        populateGroupLeader();

        populateGroupMembersListView();
        setupActionBar();

    }


    private void setUpValidUserCanCheckInfo() {
        User leader = groupSelected.getLeader();
        Long leaderId = leader.getId();

        TableRow btns = (TableRow) findViewById(R.id.btnsTableRow);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                1.0f);
        params.setMargins(15,10,15,10);


        if(currentUser.getId().equals(leaderId)){
            isValid = true;


            // ================ first button ===========================
            Button sendMsgToWholeGroupBtn = new Button(this);
            sendMsgToWholeGroupBtn.setText(getString(R.string.leader_btn_text));
            sendMsgToWholeGroupBtn.setTypeface(Typeface.DEFAULT_BOLD);
            sendMsgToWholeGroupBtn.setBackgroundResource(R.drawable.button_style);

            sendMsgToWholeGroupBtn.setTextSize(15);
            sendMsgToWholeGroupBtn.setLayoutParams(params);
            sendMsgToWholeGroupBtn.setPadding(0,0,0,0);

            sendMsgToWholeGroupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = SendMessage.makeIntent(Join_Group.this, grpId);
                    intent.putExtra("case1",888);
                    startActivity(intent);
                }
            });

            btns.addView(sendMsgToWholeGroupBtn);

        }

        for(int i = 0; i < groupMembers.size(); i++)
        {
            User memberUser = groupMembers.get(i);
            Long memberId = memberUser.getId();
            validUser.add(memberId);
            if(currentUser.getId().equals(memberId)){
                isValid = true;
            }
            Call<List<User>> caller = proxy.getMonitoredByUsers(memberId);

            ProxyBuilder.callProxy(this, caller, returnedUsers -> response(returnedUsers));


        }

        if(validUser.contains(currentUserId)){

//            // ===================== second button ================================
//
            Button NonEmergencyBtn = new Button(this);
            NonEmergencyBtn.setText(getString(R.string.non_emergency_btn_text));
            NonEmergencyBtn.setTypeface(Typeface.DEFAULT_BOLD);


            NonEmergencyBtn.setLayoutParams(params);
            NonEmergencyBtn.setPadding(0,0,0,0);
            NonEmergencyBtn.setBackgroundResource(R.drawable.button_style);


            NonEmergencyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = SendMessage.makeIntent(Join_Group.this,grpId);
                    intent.putExtra("case3",555);
                    startActivity(intent);
                }
            });
            btns.addView(NonEmergencyBtn);

        }

    }

    private void response(List<User> returnedUsers) {
        for(int i =0; i < returnedUsers.size();i++){
            User memberUser = returnedUsers.get(i);
            Long memberId = memberUser.getId();
            if(currentUser.getId().equals(memberId)){
                isValid = true;
            }
        }


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
                User leader = groupSelected.getLeader();
                Long leaderId = leader.getId();

                if (currentUserId == leaderId){
                    Toast.makeText(Join_Group.this, R.string.remove_leader_error_toast, Toast.LENGTH_LONG).show();
                } else {
                    leaveGroup(grpId, currentUserId);
                    finish();
                }
                break;
            case R.id.menu_go_back:
                finish();
                break;
            case R.id.menu_start_walking:
                if (!IAmInThisGroup){
                    Toast.makeText(Join_Group.this, "Please join this group first!", Toast.LENGTH_LONG).show();
                } else {
                    userSingleton.setWalkingGroup(groupSelected);
//                    double destLat = groupSelected.getDestLat();
//                    double destLng = groupSelected.getDestLng();

                    Intent intent = new Intent();
//                    intent.putExtra("destLat",destLat);
//                    intent.putExtra("destLng",destLng);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

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


        membersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isValid == true) {
                    Intent intent = ParentInfo.makeIntent(Join_Group.this, groupMembers.get(position));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Join_Group.this, getString(R.string.cantAccess),Toast.LENGTH_LONG).show();
                }
            }
        });




    }

    private void setupActionBar(){
        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
    }

    // Alert dialogue box: https://www.youtube.com/watch?v=wfADRuyul04
    private void showRemoveMembersDialog(boolean isLeader){
        String[] membersList;
        if (isLeader){
            membersList = groupSelected.getGroupMembersNames();
        } else {
            List<User> groupMembers = groupSelected.getMemberUsers();

            String test = "";
            if (groupMembers.size() > 0) {
                List<String> monitorsInMembersList = new ArrayList<>();
                for (int i = 0; i < monitorsUsers.size(); i++) {

                    User user = monitorsUsers.get(i);
                    Long monitorUserId = user.getId();

                    // Check if the monitors user is one of the group members
                    for (int j = 0; j < groupMembers.size(); j++) {
                        User testUser = groupMembers.get(j);
                        Long groupUserId = testUser.getId();
                        if (monitorUserId.equals(groupUserId)) {
                            String userName = testUser.getName();
                            monitorsInMembersList.add(userName);

                        }
                    }

                }
                membersList = monitorsInMembersList.toArray(new String[monitorsInMembersList.size() ]);

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

    // Alert dialogue box: https://www.youtube.com/watch?v=wfADRuyul04
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

    private void checkIfIAmInGroup(){
        //Toast.makeText(Join_Group.this, "currentUserId: " + currentUserId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(Join_Group.this, "LeaderId: " + leaderId, Toast.LENGTH_SHORT).show();
        if (currentUserId.equals(leaderId)){
            IAmInThisGroup = true;
        } else {
            for (int i = 0; i < membersId.length; i++){
                if (currentUserId == membersId[i]){
                    IAmInThisGroup = true;
                    break;
                }
            }
        }
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
