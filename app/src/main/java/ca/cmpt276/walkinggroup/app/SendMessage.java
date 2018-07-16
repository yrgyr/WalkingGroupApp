package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SendMessage extends AppCompatActivity {

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private User currentUser = userSingleton.getCurrentUser();
    private Message message;
    private Long groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long id = new Long( (long)411 );
                Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
                ProxyBuilder.callProxy(SendMessage.this,caller,messageReturn -> responseGetMessage(messageReturn));

            }
        });

        setUpSendButton();

        extraDataFromIntent();
    }

    private void responseGetMessage (List<Message> messageReturn) {
        TextView textView = findViewById(R.id.messageEdit);
        String text = messageReturn.get(1).getText();
        textView.setText(text);
    }
    private void extraDataFromIntent() {
        Intent intent = getIntent();
        groupId = intent.getLongExtra("ID",-1);
    }

    private void setUpSendButton() {
        Button btn = findViewById(R.id.sendBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = new Message();
                TextView textView = findViewById(R.id.messageEdit);
                String text = textView.getText().toString();

                message.setIsRead(false);
                message.setEmergency(false);
                message.setFromUser(currentUser);
                message.setText(""+text);

                Call<List<Message>> caller = proxy.newMessageToGroup(groupId,message);
                ProxyBuilder.callProxy(SendMessage.this,caller,returnedMsg -> response(returnedMsg));
            }
        });
    }
    private void response(List<Message> returnedMsg) {
        Toast.makeText(this, ""+returnedMsg.get(0).getId(), Toast.LENGTH_LONG).show();
//        finish();
        // ================================================



    }
    public static Intent makeIntent(Context context, Long groupId){
        Intent intent = new Intent(context,SendMessage.class);
        intent.putExtra("ID",groupId);
        return intent;
    }
}
