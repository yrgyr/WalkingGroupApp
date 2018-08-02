package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;

public class MyRewardPoints extends AppCompatActivity {
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward_points);

        setupOpenLeaderBoardBtn();
        setupChangeBgBtn();
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
                userSingleton.setBackgroundInUse(R.drawable.testimg3);
            }
        });
    }
}
