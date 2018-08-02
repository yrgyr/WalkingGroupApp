package ca.cmpt276.walkinggroup.app;

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
import ca.cmpt276.walkinggroup.dataobjects.TopUsers;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class LeaderBoard extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private List<User> allUsersOnServer = new ArrayList<>();
    private TopUsers topUsers;
    private List<User> top100Users = new ArrayList<>();
    private List<String> top100NamesAndPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        getAllUsersOnServer();
        setupRefreshButton();

    }

    private void getAllUsersOnServer(){
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoard.this, caller, allUsers -> responseGetAllUsers(allUsers));
    }

    private void responseGetAllUsers(List<User> usersList){
        allUsersOnServer = usersList;
        topUsers = new TopUsers(allUsersOnServer);
        top100Users = topUsers.getTop100Users();

        populateTop100ListView();


    }

    private void populateTop100ListView(){
        if (top100Users != null) {
            if (top100Users.size() > 0) {
                top100NamesAndPoints = new ArrayList<>();
                for (int i = 0; i < top100Users.size(); i++) {
                    User user = top100Users.get(i);
                    String[] origNameSplit = user.getName().split(" ");
                    String newName = "";
                    if (origNameSplit.length >= 2) {
                        newName = origNameSplit[0] + " " + origNameSplit[1].charAt(0);
                    } else {
                        newName = user.getName();
                    }
                    String userEntry = newName + "- " + user.getTotalPointsEarned() + " points";
                    top100NamesAndPoints.add(userEntry);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.users_list, top100NamesAndPoints);
                ListView usersList = findViewById(R.id.listview_top100_users);
                usersList.setAdapter(adapter);
            }
        }
    }

    private void setupRefreshButton(){
        Button btn = findViewById(R.id.btn_referesh_leaderboard);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllUsersOnServer();
            }
        });
    }
}
