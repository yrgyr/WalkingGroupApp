package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.login.email;

public class ParentInfo extends AppCompatActivity {

    private User user;
    private Long id;
    private WGServerProxy proxy;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_info);
        proxy = userSingleton.getCurrentProxy();

        extraDataFromIntent();
        setCheckParentInfoClick();
    }


    private void extraDataFromIntent() {
        Intent intent = getIntent();
        id = intent.getLongExtra("ID",-1);
        if(id != -1) {
            Call<User> caller = proxy.getUserById(id);
            ProxyBuilder.callProxy(ParentInfo.this, caller, returnedUser -> response(returnedUser));
        }
    }
    private void response(User returnedUser) {
        user = returnedUser;

        Call<List<User>> caller = proxy.getMonitoredByUsers(id);
        ProxyBuilder.callProxy(this, caller, returnedUsers -> response(returnedUsers));
    }
    private void response(List<User> returnedUsers) {

        ArrayList<String> ALL_USERS = new ArrayList<String>();
        for(int i =0; i < returnedUsers.size();i++){

            usersList = returnedUsers;
            User THIS_USER = returnedUsers.get(i);
            String email = THIS_USER.getEmail();
            Long ID = THIS_USER.getId();
            String name = THIS_USER.getName();

            String DISPLAY_THIS_USER = getString(R.string.name) + ": " + name;
            ALL_USERS.add(DISPLAY_THIS_USER);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.users_list,ALL_USERS);
        ListView users_list = (ListView) findViewById(R.id.parentList);
        users_list.setAdapter(adapter);

    }
    private void setCheckParentInfoClick() {
        ListView listView = findViewById(R.id.parentList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = parentInfoList.makeIntent(ParentInfo.this, usersList.get(position));
                startActivity(intent);
            }
        });


    }

    public static Intent makeIntent(Context context, User user){
        Intent intent = new Intent(context,ParentInfo.class);
        intent.putExtra("ID",user.getId());
        return intent;
    }
}
