package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "my_activity";

    private WGServerProxy proxy;
    public static String userToken;

    public static String userEmail = "james@sfu.ca";
    private String userPassword = "lin090628";



    public static Long userId;

    private List<User> usersList;

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

//        getUserInfo();
        setupGetMonitorUsersBtn();
        setupLogInBtn();
//        setupNewUserButton();
        setupAddMonitorBtn();
        setupMapBtn();
        setupGetMonitorByBtn();
        setupCreateGroupButton();







    }

    private void setupGetMonitorByBtn() {

        Button btn = (Button) findViewById(R.id.getMonitorByBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MonitorByUsersList.class);
                startActivity(intent);

            }
        });
    }

    // ==================== GET USER ID =============================



    private void setupMapBtn() {

        Button btn = (Button) findViewById(R.id.mapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void setupCreateGroupButton(){
        Button btn = findViewById(R.id.btn_create_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateGroup.class);
                startActivity(intent);
            }
        });
    }


    //============================ ADD MONITOR USER ===================================

    private void setupAddMonitorBtn() {

        Button btn = (Button) findViewById(R.id.addUsers);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddWhomUserMointor.class);
                startActivity(intent);
            }
        });
    }


    // ==========================================GET MONITOR USERS ========================================================
    private void setupGetMonitorUsersBtn() {

        Button btn = (Button) findViewById(R.id.getUsersBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,MonitorUsersList.class);
                startActivity(intent);

            }
        });
    }
//


    //=================================== LOG IN ===============================================
    private void setupLogInBtn(){
        Button btn = (Button) findViewById(R.id.logInBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User();


                user.setEmail(userEmail);
                user.setPassword(userPassword);

                userSingleton.setCurrentUser(user);

                // Register for token received:
                ProxyBuilder.setOnTokenReceiveCallback( token -> onReceiveToken(token));

                // Make call
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(MainActivity.this, caller, returnedNothing -> response(returnedNothing));



                TextView txt = (TextView) findViewById(R.id.userInfo);
                txt.setText(getString(R.string.loginuserText,user.getEmail()));
            }
        });
    }
    private void response(Void returnedNothing) {
        notifyUserViaLogAndToast("Server replied to login request (no content was expected).");
    }

    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        Toast.makeText(this, token, Toast.LENGTH_LONG).show();


        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userSingleton.setToken(token);

        Call<User> getUserCaller = proxy.getUserByEmail(userEmail);
        ProxyBuilder.callProxy(MainActivity.this, getUserCaller, returnedLogInUser -> responseLoginUser(returnedLogInUser));


    }

    private void responseLoginUser(User returnedLoginUser){

        // set logged in users id to singleton instance
        userId = returnedLoginUser.getId();
        userSingleton.setID(userId);


    }

    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
