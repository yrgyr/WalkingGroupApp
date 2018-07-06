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
                String email = emailInput.getText() + "";


                //  ---------------FIND INPUT USER BY EMAIL --------------------------
                Call<User> caller = proxy.getUserByEmail(email);
                ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUser -> responseAddMonitor(returnedUser));


            }
        });
    }
    private void responseAddMonitor(User returnedUser){

        String USER_NAME= returnedUser.getName();
        String USER_EMAIL = returnedUser.getEmail();
        Long UserID = returnedUser.getId();
        Toast.makeText(this,"returned: "+ USER_NAME+ " , "+ USER_EMAIL+" , "+ UserID,Toast.LENGTH_LONG).show();


        Call<List<User>> caller = proxy.addToMonitorsUsers(loggedInUserID,returnedUser);
        ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUsers -> responseMonitor(returnedUsers));

        TextView tv = (TextView) findViewById(R.id.addPageTv);
        tv.setText(getString(R.string.responseAddMonitortext));



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
                ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUser -> responseAddMonitorBy(returnedUser));

            }
        });

    }
    // =============================== AFTER FOUND THE USER, MAKE ADD REQUEST ============================


    private void responseAddMonitorBy(User returnedUser){

        String USER_NAME= returnedUser.getName();
        String USER_EMAIL = returnedUser.getEmail();
        Long UserID = returnedUser.getId();
        Toast.makeText(this,"returned: "+ USER_NAME+ " , "+ USER_EMAIL+" , "+ UserID,Toast.LENGTH_LONG).show();


        Call<List<User>> caller = proxy.addToMonitoredByUsers(loggedInUserID,returnedUser);
        ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUsers -> responseMonitor(returnedUsers));

        TextView tv = (TextView) findViewById(R.id.addPageTv);
        tv.setText(getString(R.string.responseAddMonitorByText));



    }



    // ==================================  AFTER ADD IT SUCCESSFULLY, DISPLAY ON THE SCREEN ====================

    private void responseMonitor(List<User> returnedUsers){
        ArrayList<String> returned_users = new ArrayList<String>();
        for(int i =0; i < returnedUsers.size();i++){

            User THIS_USER = returnedUsers.get(i);
            String email = THIS_USER.getEmail();
            Long ID = THIS_USER.getId();
            String DISPLAY_THIS_USER = "ID: "+ ID + " , email: " + email;
            returned_users.add(DISPLAY_THIS_USER);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddWhomUserMointor.this,R.layout.users_list,returned_users);
        ListView users_list = (ListView) findViewById(R.id.addMonitorsUsersList);
        users_list.setAdapter(adapter);
    }
}