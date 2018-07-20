package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MonitorUsersList extends AppCompatActivity {


    private WGServerProxy proxy;
    private Long userID;
    private List<User> usersList;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();


    /* =======================================================================================
       ============== This Activity displays all users that  the logged in user
       =============== is monitoring                                               =========
        ====================================================================================
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_users_list);


        proxy = userSingleton.getCurrentProxy();
        User user = userSingleton.getCurrentUser();
        userID = user.getId();


        setupListView();
        listViewOnclick();
        longClick();
        setUpRefresh();

    }




    // ====================== POPULATE ListVIEW =============================================

    private void setupListView(){

        Call<List<User>> caller = proxy.getMonitorsUsers(userID);
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

            String DISPLAY_THIS_USER = "Name: " + name + " , ID: "+ ID + " \nemail: " + email;
            ALL_USERS.add(DISPLAY_THIS_USER);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.users_list,ALL_USERS);
        ListView users_list = (ListView) findViewById(R.id.monitorUsersList);
        users_list.setAdapter(adapter);

    }

    // ================================= DELETE  MONITOR USER  BY CLICK ON ITEM ===================================

    private void listViewOnclick() {

        ListView lv = (ListView) findViewById(R.id.monitorUsersList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = EditContactInfo.makeIntent(MonitorUsersList.this,usersList.get(position).getId());
                startActivity(intent);



            }
        });

    }




    private void longClick() {

        ListView lv = (ListView) findViewById(R.id.monitorUsersList);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {





                android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(MonitorUsersList.this);

                //create view
                View mview=getLayoutInflater().inflate(R.layout.delete_dialog,null);

                builder.setMessage(R.string.alert_dialog_frag)
                        .setView(mview)
                        .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                User litterUser = usersList.get(position);

                                Long deleteID = litterUser.getId();



                                Call<Void> caller = proxy.removeFromMonitorsUsers(userID,deleteID);
                                ProxyBuilder.callProxy(MonitorUsersList.this, caller, nothing -> responseVoid(nothing));

                            }
                        })


                        .setNegativeButton("cancel",null)
                        .setCancelable(false);


                AlertDialog alert=builder.create();
                alert.show();


                return true;




            }

        });



    }




    private void setUpRefresh() {
        Button button = findViewById(R.id.btnRefresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupListView();
            }
        });
    }

    private void responseVoid(Void nothing){
        Toast.makeText(this,"Server replied to delete request.",Toast.LENGTH_LONG).show();
        Call<List<User>> caller2 = proxy.getMonitorsUsers(userID);
        ProxyBuilder.callProxy(MonitorUsersList.this, caller2, returnedUsers -> response(returnedUsers));
    }

}
