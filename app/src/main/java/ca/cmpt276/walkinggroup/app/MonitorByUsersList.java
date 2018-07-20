package ca.cmpt276.walkinggroup.app;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MonitorByUsersList extends AppCompatActivity {


    private WGServerProxy proxy;
    private Long userID;
    private List<User> usersList;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();



    /* =======================================================================================
           ============== This Activity displays all users who monitor the logged in user =========
            ====================================================================================
        */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_by_users_list);

        proxy = userSingleton.getCurrentProxy();
        User user = userSingleton.getCurrentUser();
        userID = user.getId();
        setupListView();
        //listViewOnclick();
        longClick();






    }

    private void setupListView(){

        Call<List<User>> caller = proxy.getMonitoredByUsers(userID);
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

            String DISPLAY_THIS_USER = "Name: " + name + " , ID: "+ ID + " , email: " + email;
            ALL_USERS.add(DISPLAY_THIS_USER);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.users_list,ALL_USERS);
        ListView users_list = (ListView) findViewById(R.id.monitorByUsersList);
        users_list.setAdapter(adapter);

    }



    private void longClick() {

        ListView lv = (ListView) findViewById(R.id.monitorByUsersList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {





                android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(MonitorByUsersList.this);

                //create view
                View mview=getLayoutInflater().inflate(R.layout.delete_dialog,null);

                builder.setMessage(R.string.alert_dialog_frag)
                        .setView(mview)
                        .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                User litterUser = usersList.get(position);

                                Long deleteID = litterUser.getId();



                                Call<Void> caller = proxy.removeFromMonitoredByUsers(userID,deleteID);
                                ProxyBuilder.callProxy(MonitorByUsersList.this, caller, nothing -> responseVoid(nothing));

                            }
                        })


                        .setNegativeButton("cancel",null)
                        .setCancelable(false);


                AlertDialog alert=builder.create();
                alert.show();







            }

        });



    }



    private void responseVoid(Void nothing){
        Toast.makeText(this,"Server replied to delete request.",Toast.LENGTH_LONG).show();
        Call<List<User>> caller2 = proxy.getMonitoredByUsers(userID);
        ProxyBuilder.callProxy(MonitorByUsersList.this, caller2, returnedUsers -> response(returnedUsers));
    }
}
