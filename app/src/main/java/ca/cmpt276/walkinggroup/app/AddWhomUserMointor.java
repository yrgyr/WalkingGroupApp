package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class AddWhomUserMointor extends AppCompatActivity {


    private WGServerProxy proxy;
    private String TAG = "addMonitor";

    private String loggedInUserEmail = MainActivity.getUserEmail();
    private Long loggedInUserID;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_whom_user_mointor);


        setupProxy();

        setupOkBtn();
        Call<User> caller = proxy.getUserByEmail(loggedInUserEmail);
        ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, userInfo -> responseUserInfo(userInfo));






    }

    private void setupProxy() {
        token = MainActivity.getUserToken();
        Toast.makeText(this,token,Toast.LENGTH_LONG).show();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
    }


    private void responseUserInfo(User userInfo){
        loggedInUserID = userInfo.getId();
    }

    private void setupOkBtn() {

        Button btn = (Button) findViewById(R.id.okBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText emailInput = (EditText) findViewById(R.id.emailET);

                String email = emailInput.getText() + "";

                Toast.makeText(AddWhomUserMointor.this,email,Toast.LENGTH_LONG).show();

                Call<User> caller = proxy.getUserByEmail(email);

                ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }


    private void response(User returnedUser){

        String USER_NAME= returnedUser.getName();
        String USER_EMAIL = returnedUser.getEmail();
        Long UserID = returnedUser.getId();


        Call<List<User>> caller = proxy.addToMonitorsUsers(loggedInUserID,returnedUser);
        ProxyBuilder.callProxy(AddWhomUserMointor.this, caller, returnedUsers -> responseMonitor(returnedUsers));




        Toast.makeText(this,USER_NAME+ " , "+ USER_EMAIL+" , "+ UserID,Toast.LENGTH_LONG).show();
    }

    private void responseMonitor(List<User> returnedUsers){
//        Log.w(TAG, "All Users:");
//        for (User user : returnedUsers) {
//            Log.w(TAG, "    User: " + user.toString());
//            Toast.makeText(this,user.toString(),Toast.LENGTH_LONG).show();
//        }

        // ====================== display list of monitoring users ===================================

        ArrayList<String> returned_users = new ArrayList<String>();
        for(int i =0; i < returnedUsers.size();i++){

            User THIS_USER = returnedUsers.get(i);
            String email = THIS_USER.getEmail();
            Long ID = THIS_USER.getId();

            String DISPLAY_THIS_USER = "ID: "+ ID + " , email: " + email;

            returned_users.add(DISPLAY_THIS_USER);


        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddWhomUserMointor.this,R.layout.users_list,returned_users);
        ListView users_list = (ListView) findViewById(R.id.MonitorsUsersListView);
        users_list.setAdapter(adapter);
    }
}
