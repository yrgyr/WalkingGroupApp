package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class parentInfoList extends AppCompatActivity {

    private Long id;
    private WGServerProxy proxy;
    private User user;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_info_list);

        proxy = userSingleton.getCurrentProxy();
        extraDataFromIntent();

    }

    private void extraDataFromIntent() {
        Intent intent = getIntent();
        id = intent.getLongExtra("ID",-1);
        if(id != -1) {
            Call<User> caller = proxy.getUserById(id);
            ProxyBuilder.callProxy(parentInfoList.this, caller, returnedUser -> response(returnedUser));
        }
    }
    private void response(User returnedUser) {
        user = returnedUser;
        setUpInfo(user);
    }

    private void setUpInfo(User user) {
        TextView name = findViewById(R.id.nameShowInfo);
        TextView birthday = findViewById(R.id.birthShowInfo);
        TextView address = findViewById(R.id.AddressShowInfo);
        TextView homePhone = findViewById(R.id.homePhoneShowInfo);
        TextView cellPhone = findViewById(R.id.cellPhoneShowInfo);
        TextView email = findViewById(R.id.emailShowInfo);

        name.setText(user.getName());
        birthday.setText(user.getBirthMonth() + " " + user.getBirthYear());
        address.setText(user.getAddress());
        homePhone.setText(user.getHomePhone());
        cellPhone.setText(user.getCellPhone());
        email.setText(user.getEmail());

    }

    public static Intent makeIntent(Context context, User user){
        Intent intent = new Intent(context,parentInfoList.class);
        intent.putExtra("ID",user.getId());
        return intent;
    }
}
