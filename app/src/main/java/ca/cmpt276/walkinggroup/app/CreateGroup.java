package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class CreateGroup extends AppCompatActivity {
    private WGServerProxy proxy;  // Todo: get proxy and user from singleton class
    private User currentUser;
    private Group newGroup = Group.getGroupSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Todo: setup new method to get user input from textview for group description
        setupMeetingPlaceButton();
        setupDestinationButton();
        setupCreateGroupButton();
        setupCancelButton();

    }

    private void setupMeetingPlaceButton(){
        Button btn = findViewById(R.id.btn_set_meeting_place);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroup.this, CreateGroupMap.class);
                startActivity(intent);
            }
        });
    }

    private void setupDestinationButton(){
        Button btn = findViewById(R.id.btn_set_destination);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateGroup.this, CreateGroupMap.class);
                startActivity(intent);
            }
        });

    }

    private void setupCreateGroupButton(){
        Button btn = findViewById(R.id.btn_create_new_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewGroup();
                Toast.makeText(CreateGroup.this, "Created new group:" + newGroup.getGroupDescription(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void setupCancelButton(){
        Button btn = findViewById(R.id.btn_cancel_create_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createNewGroup(){
        Call<Group> caller = proxy.createGroup(newGroup);
        ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup));
    }


    private void response(Group group){
        Toast.makeText(CreateGroup.this, "Server replied with group: " + group.getGroupDescription(), Toast.LENGTH_LONG).show();

    }
}
