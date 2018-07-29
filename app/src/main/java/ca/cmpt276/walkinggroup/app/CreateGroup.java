package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permissions;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.proxy.WGServerProxy.PermissionStatus.APPROVED;

public class CreateGroup extends AppCompatActivity {
    private WGServerProxy proxy;  // Todo: get proxy and user from singleton class
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private Group newGroup = Group.getGroupSingletonInstance();




    private List<PermissionRequest> permissionRequestList;






    /* =======================================================================================
        This Activity is the UI for creating a new group
        ====================================================================================
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);



        proxy = userSingleton.getCurrentProxy();










        setupMeetingPlaceButton();
        setupDestinationButton();
        setupCreateGroupButton();

    }

    private void setupMeetingPlaceButton(){
        Button btn = findViewById(R.id.btn_set_meeting_place);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroup.this, CreateGroupMap.class);
                startActivityForResult(intent,666);
            }
        });
    }


    private void setupDestinationButton(){
        Button btn = findViewById(R.id.btn_set_destination);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroup.this, CreateGroupMap.class);
                startActivityForResult(intent,888);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == 666){
                if(resultCode == Activity.RESULT_OK) {
                    double meetingLat = data.getDoubleExtra("latValue", 0);
                    double meetingLng = data.getDoubleExtra("lngValue", 0);

                    newGroup.addLatCoordinate(0,meetingLat);
                    newGroup.addLngCoordinate(0,meetingLng);
                }
            }

            if(requestCode == 888) {
                if (resultCode == Activity.RESULT_OK) {
                    double meetingLat = data.getDoubleExtra("latValue", 0);
                    double meetingLng = data.getDoubleExtra("lngValue", 0);

                    newGroup.addLatCoordinate(1,meetingLat);
                    newGroup.addLngCoordinate(1,meetingLng);
                }
            }

    }

    private void setupCreateGroupButton(){
        Button btn = findViewById(R.id.btn_create_new_group);

        btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewGroup();
//                Toast.makeText(CreateGroup.this, "Created new group:" + newGroup.getGroupDescription(), Toast.LENGTH_SHORT).show();
//                finish();
                }
            });
        }


    private void createNewGroup(){

        EditText ed = (EditText) findViewById(R.id.groupNameEd);
        String groupName = ed.getText().toString();

        if(groupName.isEmpty()){
            Toast.makeText(this, R.string.empt_group_name_toast_msg,Toast.LENGTH_LONG).show();
        }
        else {

            newGroup.setGroupDescription(groupName);
            User currentUser = userSingleton.getCurrentUser();
            //newGroup.setGroupDescription(groupName);
            newGroup.setLeader(currentUser);

            Call<Group> caller = proxy.createGroup(newGroup);
            ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup,newGroup.getLeader()));






           /* android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(CreateGroup.this);

            //create view
            View mview=getLayoutInflater().inflate(R.layout.leader_permission,null);

            builder.setMessage("")
                    .setView(mview)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            User currentUser = userSingleton.getCurrentUser();
                            //newGroup.setGroupDescription(groupName);
                            newGroup.setLeader(currentUser);

                            Call<Group> caller=proxy.updateGroup(newGroup.getId(),newGroup);
                            ProxyBuilder.callProxy(CreateGroup.this,caller,resGrrp11->resp11(resGrrp11));
                            //Call<Group> caller = proxy.createGroup(newGroup);
                            //ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup,newGroup.getLeader()));
                            Call<List<PermissionRequest>> caller1=proxy.getPermissionByGroup(newGroup.getId());
                            ProxyBuilder.callProxy(CreateGroup.this,caller1,rgroup->res(rgroup));












                        }
                    })


                    .setNegativeButton("no",null)
                    .setCancelable(false);


            AlertDialog alert=builder.create();
            alert.show();*/






        }
    }



    private void resp11(Group resGrrp11){
        finish();
    }







    private void response(Group returnedGroup,User leader){

        Long groupID = returnedGroup.getId();
        Toast.makeText(CreateGroup.this, getString(R.string.create_group_success_toast_msg) + groupID, Toast.LENGTH_LONG).show();

        /////////////////////////////////


        android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(CreateGroup.this);

        //create view
        View mview=getLayoutInflater().inflate(R.layout.leader_permission,null);

        builder.setMessage("")
                .setView(mview)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User currentUser = userSingleton.getCurrentUser();
                        //newGroup.setGroupDescription(groupName);
                        //newGroup.setLeader(currentUser);

                        Call<Group> caller=proxy.updateGroup(groupID,returnedGroup);
                        ProxyBuilder.callProxy(CreateGroup.this,caller,resGrrp11->resp11(resGrrp11));
                        //Call<Group> caller = proxy.createGroup(newGroup);
                        //ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup,newGroup.getLeader()));
                        Call<List<PermissionRequest>> caller1=proxy.getPermissionByGroup(groupID);
                        ProxyBuilder.callProxy(CreateGroup.this,caller1,rgroup->res(rgroup));












                    }
                })


                .setNegativeButton("no",null)
                .setCancelable(false);


        AlertDialog alert=builder.create();
        alert.show();









        ////////////////////////////////




        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroups -> returnGroups(returnedGroups));
    }

    private void returnGroups(List<Group> returnedGroups){
        MainActivity.groupsList = returnedGroups;
    }


    private void res(List<PermissionRequest> rgroup){



        PermissionRequest permissionRequest=rgroup.get(0);
        WGServerProxy.PermissionStatus status=APPROVED;


        //Toast.makeText(CreateGroup.this,"Before req",Toast.LENGTH_LONG).show();


        Call<PermissionRequest> approveCaller = proxy.approveOrDenyPermissionRequest(permissionRequest.getId(),status);
        //Toast.makeText(CreateGroup.this,"Middle req",Toast.LENGTH_LONG).show();

        ProxyBuilder.callProxy(CreateGroup.this,approveCaller,returnedPermission->newPerm(returnedPermission));
        Toast.makeText(CreateGroup.this,"Last req",Toast.LENGTH_LONG).show();

    }


    private void newPerm(PermissionRequest returnedPermission){

        finish();
    }

}
