package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.MyBgs;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MyRewardPoints extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private User currentUser = CurrentUserData.getSingletonInstance().getCurrentUser();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private List<MyBgs> bgResIdList = new ArrayList<>();
    private List<Integer> purchasedLogos = currentUser.getRewards().getPurchasedMessageLogos();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward_points);

        setupOpenLeaderBoardBtn();
        setupBgList();
        setupOnItemClick();
        currentUser.setCurrentPoints(500);  // todo: delete later
        setupTextView();


    }

    private void setupBgList() {

        bgResIdList.add(new MyBgs(R.drawable.planet1,"Earth",100));
        bgResIdList.add(new MyBgs(R.drawable.planet2,"Mercury",100));
        bgResIdList.add(new MyBgs(R.drawable.planet3,"Venus",100));
        bgResIdList.add(new MyBgs(R.drawable.planet4,"Mars",200));
        bgResIdList.add(new MyBgs(R.drawable.planet5,"Jupiter",300));
        bgResIdList.add(new MyBgs(R.drawable.planet6,"Saturn",100));
        bgResIdList.add(new MyBgs(R.drawable.planet7,"Neptune",100));


        ListView bgList = (ListView) findViewById(R.id.bgList);

        ArrayAdapter<MyBgs> adapter = new ArrayAdapter<MyBgs>(this, R.layout.background_list, bgResIdList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.background_list, parent, false);
                }
                MyBgs currentBg = bgResIdList.get(position);

                String name = currentBg.getName();
                int price = currentBg.getPrice();
                int resId = currentBg.getBgResId();


                TextView tv = (TextView) convertView.findViewById(R.id.bg_name);
                tv.setText(name);

                TextView price_tv = (TextView) convertView.findViewById(R.id.bg_price);
                if (!purchasedLogos.contains(resId)){
                    price_tv.setText("cost: "+price+" points");
                } else {
                    price_tv.setText(R.string.text_already_purchased);
                }

                ImageView bgImg = (ImageView) convertView.findViewById(R.id.bg_img);
                bgImg.setImageResource(resId);

                return convertView;
            }
        };

        bgList.setAdapter(adapter);
    }

    private void setupOnItemClick(){

        ListView lv = (ListView) findViewById(R.id.bgList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                MyBgs currentBg = bgResIdList.get(position);
                int imgResId = currentBg.getBgResId();
                int price = currentBg.getPrice();

                if (!purchasedLogos.contains(imgResId)) {

                    int user_points = currentUser.getCurrentPoints();

                    if (user_points >= price) {
                        int points = user_points - price;
                        currentUser.setCurrentPoints(points);
                        currentUser.getRewards().setMessageLogoInUse(imgResId);
                        userSingleton.setBackgroundInUse(imgResId);
                        purchasedLogos.add(imgResId);
                        currentUser.getRewards().setPurchasedMessageLogos(purchasedLogos);

                        TextView tv = (TextView) view.findViewById(R.id.bg_price);
                        tv.setText(R.string.text_already_purchased);
                        updateUserRewards();
                    } else {
                        Toast.makeText(MyRewardPoints.this, R.string.toast_not_enough_points, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MyRewardPoints.this, R.string.text_already_purchased, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupTextView() {
        int currentPointspoints = currentUser.getCurrentPoints();
        TextView cp = (TextView) findViewById(R.id.currentPoints);
        cp.setText("you have: " + currentPointspoints + " points remaining");

    }

    private void setupOpenLeaderBoardBtn(){
        Button btn = findViewById(R.id.btn_open_leaderboard);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyRewardPoints.this, LeaderBoard.class);
                startActivity(intent);
            }
        });
    }

    private void updateUserRewards(){
        Call<User> caller = proxy.editUser(currentUser, currentUser.getId());
        ProxyBuilder.callProxy(MyRewardPoints.this, caller, returnedUser -> responseUpdateUser(returnedUser));
    }

    private void responseUpdateUser(User user){
        Toast.makeText(MyRewardPoints.this, R.string.toast_purchase_successful, Toast.LENGTH_LONG).show();
        setupTextView();
    }
}
