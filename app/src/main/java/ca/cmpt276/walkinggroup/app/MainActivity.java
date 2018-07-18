package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private WGServerProxy proxy;
    public static boolean isLogOut = false;
    public static List<Group> groupsList = new ArrayList<>();
    private String name = "default";

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.test_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Edit_User:
                Intent intent=new Intent(MainActivity.this,EditContactInfo.class);
                startActivity(intent);
                break;

            case R.id.log_out:
                isLogOut = true;
                Intent i = new Intent(MainActivity.this,login.class);
                startActivity(i);
                finish();


        }
        return super.onOptionsItemSelected(item);
    }

    /* =======================================================================================
        This Activity is the app's main menu
        ====================================================================================
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(login.getToken(this) == null) {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish();
        }


        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), login.getToken(this));
        userSingleton.setCurrentProxy(proxy);

        setUpName();
//        setUpLogOut();

        setupLogOutBtn();
        getRemoteGroups();


        setupGetMonitorUsersBtn();
        setupAddMonitorBtn();
        setupMapBtn();
        setupGetMonitorByBtn();
        setupCreateGroupButton();
    }

    private void setUpName() {
        String email = login.getEmail(MainActivity.this);
        if(email != null) {
            Call<User> caller = proxy.getUserByEmail(email);
            ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> response(returnedUser));
        }

    }
    private void updateUI() {
        TextView textView = findViewById(R.id.userName);
        textView.setText(getString(R.string.welcome) + " " + name);
    }

    private void response(User user) {
        name = user.getName();
        userSingleton.setCurrentUser(user);

        Toast.makeText(this, getString(R.string.welcome) + " " + name, Toast.LENGTH_LONG).show();
        updateUI();

    }
//    private void setUpLogOut() {
//        TextView logOut = findViewById(R.id.LogOutText);
//        logOut.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        logOut.setTextColor(Color.BLUE);
//
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isLogOut = true;
//                Intent i = new Intent(MainActivity.this,login.class);
//                startActivity(i);
//                finish();
//            }
//        });
//    }

    private void setupLogOutBtn(){

        Button btn = (Button) findViewById(R.id.logOutBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogOut = true;
                Intent i = new Intent(MainActivity.this,login.class);
                startActivity(i);
                finish();
            }
        });

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

    private void getRemoteGroups() {
        if(login.getToken(MainActivity.this) != null) {
            Call<List<Group>> caller = proxy.getGroups();
            ProxyBuilder.callProxy(MainActivity.this, caller, returnedGroups -> returnGroups(returnedGroups));
        }

    }
    private void returnGroups(List<Group> returnedGroups){
        groupsList = returnedGroups;
    }
}
