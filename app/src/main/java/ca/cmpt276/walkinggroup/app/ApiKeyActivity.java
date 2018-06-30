package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import retrofit2.Call;


/**
 * Demonstrates how to work with the server.
 * - User enters a team name.
 * - App sends request to server via the Proxy
 * - Callback from the Proxy (to response() ) handles what the server told us.
 */
public class ApiKeyActivity extends AppCompatActivity {
    private static final String TAG = "ApiKeyAct";

    // Used to access the server.
    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_key);

        // Build proxy (don't yet have API key)
        // Also don't have a token (don't need to log-in for getting the API key), but most
        // of the time your proxy will need to have the token to access protected content
        // on the server.
        String currentApiKey = null;
        proxy = ProxyBuilder.getProxy(currentApiKey);

        setupLookUpButton();
    }

    // When clicked, send message to server.
    private void setupLookUpButton() {
        Button btn = findViewById(R.id.btnLookUpApiKey);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get group name
                EditText groupET = findViewById(R.id.editGroupName);
                String group = groupET.getText().toString();

                // Make call
                Call<String> caller = proxy.getApiKey(group);
                ProxyBuilder.callProxy(ApiKeyActivity.this, caller, returnedKey -> response(returnedKey));

                // NOTE: Above call to proxy could equally well have been the following
                // (uses a method reference vs a lambda function)
                // ProxyBuilder.callProxy(ApiKeyActivity.this, caller, ApiKeyActivity.this::response);

            }
        });
    }

    // Handle the reply from the server.
    private void response(String message) {
        Log.w(TAG, "Server replied with API Key: " + message);

        // Put response on UI
        EditText apiDisplay = findViewById(R.id.editApiKey);
        apiDisplay.setText(message);
    }


}
