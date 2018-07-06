package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class CreateGroup extends AppCompatActivity {
    private WGServerProxy proxy;  // Todo: get proxy and user from singleton class
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);



        proxy = userSingleton.getCurrentProxy();


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
//                Toast.makeText(CreateGroup.this, "Created new group:" + newGroup.getGroupDescription(), Toast.LENGTH_SHORT).show();
//                finish();
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

        EditText ed = (EditText) findViewById(R.id.groupNameEd);
        String groupName = ed.getText()+"";

        User currentUser = userSingleton.getCurrentUser();
//        Group newGroup = Group.getGroupSingletonInstance();
        Group newGroup = new Group();

        newGroup.setGroupDescription(groupName);
        newGroup.setLeader(currentUser);

        double[] a1 = new double[2];
        double[] a2 = new double[2];

        a1[0] = 121.11;
        a1[1] = 122.22;

        a2[0] = 111.55;
        a2[1] = 111.66;
        newGroup.setRouteLatArray(a1);
        newGroup.setRouteLngArray(a2);


//        List<Double> lats = newGroup.getRouteLatArray();
//        List<Double> lngs = newGroup.getRouteLngArray();
//
//        double lat1 = lats.get(0);
//        double lat2 = lats.get(1);
//
//        double lng1 = lngs.get(0);
//        double lng2 = lngs.get(1);
//
//
//
//        TextView tv = (TextView) findViewById(R.id.myTextView);
//        tv.setText(lat1+" , " + lng1 + "second: " + lat2+ "," + lng2);


        Call<Group> caller = proxy.createGroup(newGroup);
        ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup));
    }


    private void response(Group returnedGroup){

        Long groupID = returnedGroup.getId();
//        User leader = group.getLeader();
//        String email = leader.getEmail();
        Toast.makeText(CreateGroup.this, "Server replied with group: " + groupID, Toast.LENGTH_LONG).show();



//        TextView tv = (TextView) findViewById(R.id.myTextView);
//        tv.setText(email);




    }
}
