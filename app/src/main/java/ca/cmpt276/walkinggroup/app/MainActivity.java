package ca.cmpt276.walkinggroup.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "my_activity";
    private WGServerProxy proxy;
    private User currentUser;  // stores current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
        setupGetUsersBtn();
        setupLogInBtn();
        setupNewUserButton();
        setUpCreateGroupBtn();
    }
    private String userEmail = "Mike64140@test.com";
    private String userPassword = "12345";
    private Long userId;

    private void setupNewUserButton() {
        Button btn = findViewById(R.id.newUserBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build new user (with random email to avoid conflicts)
                User user = new User();
                int random = (int) (Math.random() * 100000);
                user.setEmail("Mike"+random+"@test.com");
                user.setName("I am Mike");
                user.setPassword(userPassword);
                user.setCurrentPoints(100);
                user.setTotalPointsEarned(2500);
                user.setRewards(new EarnedRewards());
                // Make call
                Call<User> caller = proxy.createUser(user);

                ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }
    // ------------------------------------------------------------------------------------------
    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        userId = user.getId();
        userEmail = user.getEmail();

        currentUser = user;
    }
    private void setupGetUsersBtn() {

        Button btn = (Button) findViewById(R.id.getUsersBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<List<User>> caller = proxy.getUsers();
                ProxyBuilder.callProxy(MainActivity.this, caller, returnedUsers -> response(returnedUsers));


            }
        });
    }
    private void response(List<User> returnedUsers) {

        notifyUserViaLogAndToast("Got list of " + returnedUsers.size() + " users! See logcat.");
        Log.w(TAG, "All Users:");
        for (User user : returnedUsers) {
            Log.w(TAG, "    User: " + user.toString());
        }
        // ===================================================

        ArrayList<String> ALL_USERS = new ArrayList<String>();
        for(int i =0; i < returnedUsers.size();i++){

            User THIS_USER = returnedUsers.get(i);
            String email = THIS_USER.getEmail();
            Long ID = THIS_USER.getId();

            String DISPLAY_THIS_USER = "ID: "+ ID + " , email: " + email;

            ALL_USERS.add(DISPLAY_THIS_USER);


        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.users_list,ALL_USERS);
        ListView users_list = (ListView) findViewById(R.id.usersList);
        users_list.setAdapter(adapter);

    }
    //=================================== LOG IN ===============================================
    private void setupLogInBtn(){
        Button btn = (Button) findViewById(R.id.logInBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User();
                user.setEmail(userEmail);
                user.setPassword(userPassword);

                // Register for token received:
                ProxyBuilder.setOnTokenReceiveCallback( token -> onReceiveToken(token));

                // Make call
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(MainActivity.this, caller, returnedNothing -> response(returnedNothing));
            }
        });
    }
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        Toast.makeText(this, token, Toast.LENGTH_LONG).show();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        CurrentUserData currentUserData = CurrentUserData.getSingletonInstance();
        currentUserData.setToken(token);
    }
    private void response(Void returnedNothing) {
        notifyUserViaLogAndToast("Server replied to login request (no content was expected).");
    }

    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setUpCreateGroupBtn(){
        Button btn = findViewById(R.id.btn_make_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentUserData userData = CurrentUserData.getSingletonInstance();
                userData.setCurrentProxy(proxy);
                userData.setCurrentUser(currentUser);

                Intent intent = new Intent(MainActivity.this, CreateGroup.class);
                startActivity(intent);
            }
        });
    }
}
