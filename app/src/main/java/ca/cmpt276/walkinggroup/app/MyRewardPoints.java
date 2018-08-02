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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;

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

        List<Integer> bgResIdList = new ArrayList<Integer>();

        bgResIdList.add(R.drawable.testimg2);
        bgResIdList.add(R.drawable.testimg3);
        bgResIdList.add(R.drawable.testimg4);
        bgResIdList.add(R.drawable.testimg5);
        bgResIdList.add(R.drawable.testimg6);
        bgResIdList.add(R.drawable.testimg7);



        ListView bgList = (ListView) findViewById(R.id.bgList);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.background_list, bgResIdList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.background_list, parent, false);
                }


                int thisId = bgResIdList.get(position);


                TextView tv = (TextView) convertView.findViewById(R.id.bgName);
                tv.setText(thisId + "hello");

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
