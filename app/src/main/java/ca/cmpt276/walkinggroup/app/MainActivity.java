package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private WGServerProxy proxy;
    public static boolean isLogOut = false;
    public static List<Group> groupsList = new ArrayList<>();
    public static List<Message> messageList = new ArrayList<>();
    private User currentUser;
    public static int unreadCount = 0;
    private Runnable myRun;
    private String name = "default";

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();


    private int backgroundID = userSingleton.getBackgroundInUse();



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
                Intent intent=EditContactInfo.makeIntent(MainActivity.this, currentUser.getId());
                startActivity(intent);
                break;

            case R.id.log_out:
                isLogOut = true;
                Intent i = new Intent(MainActivity.this,login.class);
                startActivity(i);
                userSingleton.setBackgroundInUse(-1);
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


        setupParentDashboardBtn();
        getRemoteGroups();


        setupGetMonitorUsersBtn();
        setupAddMonitorBtn();
        setupMapBtn();
        setupGetMonitorByBtn();
        setupCreateGroupButton();

        setupPanicBtn();
        setupViewRewardsBtn();
        setUpPermissionListBtn();


    }

    @Override
    public void onResume(){
        super.onResume();
        backgroundID = userSingleton.getBackgroundInUse();
        ImageView mailbox = (ImageView) findViewById(R.id.mailImage);;
        if (backgroundID != -1) {
            mailbox.setImageResource(backgroundID);
        } else {
            mailbox.setImageResource(R.drawable.mail);
        }

    }

    private void setupImageBtn() {

        ImageView myImg = (ImageView) findViewById(R.id.mailImage);
        Integer imgResId = currentUser.getRewards().getMessageLogoInUse();

        if (imgResId != null){
            myImg.setImageResource(imgResId);
        }


        myImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MsgToMe.class);
                startActivity(intent);
            }
        });
    }


    private void setUpName() {
        String email = login.getEmail(MainActivity.this);
        if(email != null) {
            Call<User> caller = proxy.getUserByEmail(email);
            ProxyBuilder.callProxy(MainActivity.this, caller, returnedUser -> response(returnedUser));
        }

    }

    private void response(User user) {
        name = user.getName();
        userSingleton.setCurrentUser(user);
        currentUser = user;
        setupImageBtn();
        getUnReadMessageList();
        Toast.makeText(this, getString(R.string.welcome) + " " + name, Toast.LENGTH_LONG).show();

    }

    private void setupParentDashboardBtn(){

        Button btn = (Button) findViewById(R.id.parentDashBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ParentsDashboard.class);
                startActivity(i);
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


    private void getUnReadMessageList() {
        if(login.getToken(MainActivity.this) != null) {
            Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
            ProxyBuilder.callProxy(MainActivity.this,caller,messageReturn -> responseGetMessage(messageReturn));
        }
    }


    private void responseGetMessage (List<Message> messageReturn) {

        unreadCount = messageReturn.size();
        TextView textView = findViewById(R.id.userName);
        textView.setText(getString(R.string.welcome) + " " + name );
    }


    private void setupPanicBtn(){

        Button btn = (Button) findViewById(R.id.panicBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SendMessage.class);
                intent.putExtra("case2",666);
                startActivity(intent);
            }
        });
    }

    private void setUpPermissionListBtn() {
        Button btn = findViewById(R.id.permissionBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Permission.class);
                startActivity(intent);
            }
        });
    }


    private void setupViewRewardsBtn(){
        Button btn = findViewById(R.id.btn_view_rewards);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyRewardPoints.class);
                startActivity(intent);
            }
        });
    }

}
