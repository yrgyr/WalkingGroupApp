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

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "my_activity";

    private WGServerProxy proxy;
    public static String userToken;

    public static String userEmail = "Mike62679@test.com";
    private String userPassword = "12345";



    public static Long userId;

    private List<User> usersList;

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


        getAllUsersBtn();

        setupGetMonitorByBtn();




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
    public static Long getUserId() {
        return userId;
    }



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


    //===============================================================

    private void setupAddMonitorBtn() {

        Button btn = (Button) findViewById(R.id.addUsers);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddWhomUserMointor.class);
                startActivityForResult(intent,42);
            }
        });
    }

    public static String getUserEmail() {
        return userEmail;
    }
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
    }
    // ==========================================GET MONITOR USERS ========================================================
    private void setupGetMonitorUsersBtn() {




        Button btn = (Button) findViewById(R.id.getUsersBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,MonitorUsersList.class);
                startActivity(intent);

//                Call<List<User>> caller = proxy.getMonitorsUsers(userId);
//                ProxyBuilder.callProxy(MainActivity.this, caller, returnedUsers -> response(returnedUsers));
            }
        });
    }

    private void response(List<User> returnedUsers) {

//        notifyUserViaLogAndToast("Got list of " + returnedUsers.size() + " users! See logcat.");
//        Log.w(TAG, "All Users:");
//        for (User user : returnedUsers) {
//            Log.w(TAG, "    User: " + user.toString());
//        }

        ArrayList<String> ALL_USERS = new ArrayList<String>();
        for(int i =0; i < returnedUsers.size();i++){

            usersList = returnedUsers;
            User THIS_USER = returnedUsers.get(i);
            String email = THIS_USER.getEmail();
            Long ID = THIS_USER.getId();
            String name = THIS_USER.getName();

            String DISPLAY_THIS_USER = "Name: " + name + " , ID: "+ ID + " , email: " + email;
            ALL_USERS.add(DISPLAY_THIS_USER);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.users_list,ALL_USERS);
        ListView users_list = (ListView) findViewById(R.id.usersList);
        users_list.setAdapter(adapter);

    }



    // ================================= GET ALL USERS ==========================================

    private void getAllUsersBtn(){

        Button btn = (Button) findViewById(R.id.allUsersBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<User>> caller = proxy.getUsers();
                ProxyBuilder.callProxy(MainActivity.this, caller, returnedUsers -> response(returnedUsers));
            }
        });
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



                TextView txt = (TextView) findViewById(R.id.userInfo);
                txt.setText("user "+ user.getEmail()+" has already logged In ");
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
        userToken = token;

        Call<User> getUserCaller = proxy.getUserByEmail(userEmail);
        ProxyBuilder.callProxy(MainActivity.this, getUserCaller, returnedLogInUser -> responseLoginUser(returnedLogInUser));


    }

    private void responseLoginUser(User returnedLoginUser){
        // logged in user Info
        userId = returnedLoginUser.getId();

    }

    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static String getUserToken(){
        return userToken;
    }

//    private void saveToken(String token){
//
//        SharedPreferences tokenShare = this.getSharedPreferences("tokenShare",MODE_PRIVATE);
//        SharedPreferences.Editor editor = tokenShare.edit();
//
//        editor.putString("user token",token);
//        editor.apply();
//
//    }
//
//    public static String getToken(Context context){
//
//
//        SharedPreferences share = context.getSharedPreferences("user token",MODE_PRIVATE);
//
//        String token
//
//                share.getString()
//
//
//    }
}
