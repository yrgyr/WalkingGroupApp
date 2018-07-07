package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class login extends AppCompatActivity {

    public static final String APP_PREFERENCE = "App_preference";
    public static final String TOKEN = "TOKEN";
    public static final String EMAIL = "EMAIL";
    private WGServerProxy proxy;
    private static final String TAG = "register";

    public static String tokenOfLoggingUser;
    public static String email;

    /* =======================================================================================
        This Activity is the UI for Logging in to the app
        ====================================================================================
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(MainActivity.isLogOut == true){
            MainActivity.isLogOut = false;
            saveToken(null);
            saveEmail(null);
        }

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
        setUpRegister();
        setUpLogin();

    }


    private void setUpRegister() {
        Button btn = findViewById(R.id.registerButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (login.this, register.class);
                startActivity(i);
            }
        });

    }
    private void setUpLogin() {
        Button btn = findViewById(R.id.loginButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                EditText emailText = findViewById(R.id.loginEmailEdit);
                email = emailText.getText().toString();

                EditText passwordText = findViewById(R.id.loginTextEdit);
                String password = passwordText.getText().toString();


                user.setEmail(email);
                user.setPassword(password);


                ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxyForLogin(login.this, caller, returnedNothing -> response(returnedNothing));
            }
        });

    }

    private void onReceiveToken(String token) {
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        tokenOfLoggingUser = token;
        saveToken(tokenOfLoggingUser);
        saveEmail(email);
    }



    private void response(Void returnedNothing) {
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void saveToken(String tokenOfLoggingUser) {
        SharedPreferences preferences = login.this.getSharedPreferences(APP_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN,tokenOfLoggingUser);
        editor.apply();
    }
    private void saveEmail(String email) {

        SharedPreferences preferences = login.this.getSharedPreferences(APP_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL,email);
        editor.apply();
    }

    public static String getToken(Context context){

        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(TOKEN,null);

    }
    public static String getEmail(Context context){

        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE);
        return preferences.getString(EMAIL,null);

    }

}
