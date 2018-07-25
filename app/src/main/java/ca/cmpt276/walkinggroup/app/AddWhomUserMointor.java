package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class AddWhomUserMointor extends AppCompatActivity {


    private WGServerProxy proxy;
    private Long loggedInUserID;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    /* =======================================================================================
        This Activity is the UI for adding a new monitor user, either you want to monitor this user
        or let this user monitor you.
        ====================================================================================
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_whom_user_mointor);

        proxy = userSingleton.getCurrentProxy();
        User user = userSingleton.getCurrentUser();
        loggedInUserID = user.getId();


        setupAddMonitorBtn();
        setupAddMonitorByBtn();

    }


    //  ==============================  FIRST FIND THIS INPUT USER =====================================

    private void setupAddMonitorBtn() {

        Button btn = (Button) findViewById(R.id.addMonitorBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText emailInput = (EditText) findViewById(R.id.emailET);
                String email = emailInput.getText().toString();


                //  ---------------FIND INPUT USER BY EMAIL --------------------------
                Call<User> caller = proxy.getUserByEmail(email);

                ProxyBuilder.callProxyForUser(AddWhomUserMointor.this, caller, returnedUser -> responseAddMonitor(returnedUser));


            }
        });
    }
    private void responseAddMonitor(User returnedUserFromEdit){
        Call<List<User>> caller = proxy.addToMonitorsUsers(loggedInUserID,returnedUserFromEdit);
        ProxyBuilder.callProxyForAddMonitor(AddWhomUserMointor.this, caller, returnedUsers -> responseMonitor(returnedUsers,returnedUserFromEdit));
    }

    // =======================ADD MONITOR BY ==============================================================

    private void setupAddMonitorByBtn(){

        Button btn = (Button) findViewById(R.id.addMonitorByBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText emailInput = (EditText) findViewById(R.id.emailET);
                String email = emailInput.getText() + "";

                Call<User> caller = proxy.getUserByEmail(email);
                ProxyBuilder.callProxyForUser(AddWhomUserMointor.this, caller, returnedUser -> responseAddMonitorBy(returnedUser));

            }
        });

    }
    // =============================== AFTER FOUND THE USER, MAKE ADD REQUEST ============================


    private void responseAddMonitorBy(User returnedUserFromEdit){
        Call<List<User>> caller = proxy.addToMonitoredByUsers(loggedInUserID,returnedUserFromEdit);
        ProxyBuilder.callProxyForAddMonitor(AddWhomUserMointor.this, caller, returnedUsers -> responseMonitor(returnedUsers,returnedUserFromEdit));
    }



    // ==================================  AFTER ADD IT SUCCESSFULLY, DISPLAY ON THE SCREEN ====================

    private void responseMonitor(List<User> returnedUsers, User returnedUserFromEdit){
        String USER_NAME= returnedUserFromEdit.getName();
        String USER_EMAIL = returnedUserFromEdit.getEmail();
        Long UserID = returnedUserFromEdit.getId();
        Toast.makeText(this,getString(R.string.returned)+ USER_NAME+ " , "+ USER_EMAIL+" , "+ UserID,Toast.LENGTH_LONG).show();
    }
}
