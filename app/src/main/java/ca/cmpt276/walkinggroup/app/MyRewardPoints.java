package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.MyBgs;

public class MyRewardPoints extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward_points);

        setupOpenLeaderBoardBtn();
        setupChangeBgBtn();
        setupBgList();
    }

    private void setupBgList() {

        List<MyBgs> bgResIdList = new ArrayList<>();

        bgResIdList.add(new MyBgs(R.drawable.planet1,"Earth",100));
        bgResIdList.add(new MyBgs(R.drawable.planet2,"Mercury",100));
        bgResIdList.add(new MyBgs(R.drawable.planet3,"Venus",100));
        bgResIdList.add(new MyBgs(R.drawable.planet4,"Mars",100));
        bgResIdList.add(new MyBgs(R.drawable.planet5,"Jupiter",100));
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
                price_tv.setText("cost: "+price+" points");

                ImageView bgImg = (ImageView) convertView.findViewById(R.id.bg_img);
                bgImg.setImageResource(resId);

                return convertView;
            }
        };

        bgList.setAdapter(adapter);


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

    private void setupChangeBgBtn(){
        Button btn = findViewById(R.id.change_bg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSingleton.setBackgroundInUse(R.drawable.testimg4);
            }
        });
    }
}
