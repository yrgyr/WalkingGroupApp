package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        getAllUsersOnServer();

    }

    private void getAllUsersOnServer(){
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoard.this, caller, allUsers -> responseGetAllUsers(allUsers));
    }

    private void responseGetAllUsers(List<User> usersList){
        allUsersOnServer = usersList;
        topUsers = new TopUsers(allUsersOnServer);
        top100Users = topUsers.getTop100Users();

        // Todo: code for testing sorting user by points function; remove later
        String rewardsAscend = "";
        for (int i = 0; i < top100Users.size(); i++){
            User user = top100Users.get(i);
            String reward = user.getName() + ": " + user.getTotalPointsEarned() + ",";
            rewardsAscend += reward;
        }
        Log.e("Rewards:", rewardsAscend);

    }
}
