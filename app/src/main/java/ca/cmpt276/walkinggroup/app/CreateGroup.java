package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class CreateGroup extends AppCompatActivity {
    private WGServerProxy proxy;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        createGroup();

    }

    private void createGroup(){
        CurrentUserData currentUserData = CurrentUserData.getSingletonInstance();
        //proxy = currentUserData.getCurrentProxy();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), currentUserData.getToken());
        currentUser = currentUserData.getCurrentUser();

        Group newGroup = new Group();
        newGroup.setGroupDescription("Raspberry");
        newGroup.setLeader(currentUser);
        //newGroup.setStartLat(49.2);
        //newGroup.setStartLng(-100.2);

        Call<Group> caller = proxy.createGroup(newGroup);
        ProxyBuilder.callProxy(CreateGroup.this, caller, returnedGroup->response(returnedGroup));

    }

    private void response(Group group){
        Toast.makeText(CreateGroup.this, "Server replied with group: " + group.getGroupDescription(), Toast.LENGTH_LONG).show();

    }
}
