package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class register extends AppCompatActivity {
    private WGServerProxy proxy;


    /* =======================================================================================
        This Activity is the UI for registering a new user
        ====================================================================================
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
        setSignUp();
    }

    private void setSignUp() {
        Button btn = findViewById(R.id.signUpButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();

                EditText email = findViewById(R.id.regEmailEdit);
                String emailAddress = email.getText().toString();


                EditText nameEdit = findViewById(R.id.regNameEdit);
                String name = nameEdit.getText().toString();


                EditText passwordEdit = findViewById(R.id.regPassEdit);
                String password = passwordEdit.getText().toString();

                if(!(emailAddress.contains("@")) || !(password.length() > 0) || !(name.length() > 0)){
                    if(!emailAddress.contains("@")) {
                        Toast.makeText(register.this, R.string.invalidEmail, Toast.LENGTH_LONG).show();
                    }
                    else if (!(password.length() > 0)){
                        Toast.makeText(register.this, R.string.invalidPassword, Toast.LENGTH_LONG).show();
                    }
                    else if (!(name.length() > 0)){
                        Toast.makeText(register.this, R.string.invalidName, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    user.setEmail("" + emailAddress);
                    user.setName("" + name);
                    user.setPassword("" + password);
                    user.setCurrentPoints(100);
                    user.setTotalPointsEarned(2500);
                    user.setRewards(new EarnedRewards());


                    Call<User> caller = proxy.createUser(user);
                    ProxyBuilder.callProxyForRegister(register.this, caller, returnedUser -> response(returnedUser));
                }
            }
        });

    }
    private void response(User user) {
        Toast.makeText(this, R.string.registerSucceed, Toast.LENGTH_LONG).show();
        finish();
    }

}
