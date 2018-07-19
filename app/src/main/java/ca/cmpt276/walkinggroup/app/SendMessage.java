package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

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

        int case1 = getIntent().getIntExtra("case1",0);
        int case2 = getIntent().getIntExtra("case2",0);
        int case3 = getIntent().getIntExtra("case3",0);



        if (case1 == 888){
            setUpSendMsgToGroupBtn();
        }

        else if(case2 == 666){
            EmergencyToParent();
        }
        else if(case3 == 555){

            nonEmergencyMsgToParent();
        }

        extraDataFromIntent();


    }

    private void nonEmergencyMsgToParent() {
        Button btn = (Button) findViewById(R.id.sendBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = new Message();
                EditText textView = (EditText) findViewById(R.id.messageEdit);
                String text = textView.getText().toString();

                message.setIsRead(false);
                message.setEmergency(false);
                message.setFromUser(currentUser);
                message.setText(text);
                Call<List<Message>> caller = proxy.newMessageToParentsOf(currentUser.getId(),message);
                ProxyBuilder.callProxy(SendMessage.this,caller,returnedMsg -> childMsgResponse(returnedMsg));

            }
        });
    }


    private void EmergencyToParent() {
        EditText editText = (EditText) findViewById(R.id.messageEdit);
        editText.setText(getString(R.string.defalut_emergency_msg_));

        Button btn = (Button) findViewById(R.id.sendBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = new Message();

                EditText editText = (EditText) findViewById(R.id.messageEdit);

                String text = editText.getText().toString();
                message.setIsRead(false);
                message.setEmergency(true);
                message.setFromUser(currentUser);
                message.setText(text);
                Call<List<Message>> caller = proxy.newMessageToParentsOf(currentUser.getId(),message);
                ProxyBuilder.callProxy(SendMessage.this,caller,returnedMsg -> childMsgResponse(returnedMsg));

            }
        });

    }
    private void childMsgResponse(List<Message> returnedMsg){

        if(!returnedMsg.isEmpty()){
            Toast.makeText(this, getString(R.string.msg_sent_successful_text,returnedMsg.get(0).getId()), Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(this, getString(R.string.fail_to_send_msg_text), Toast.LENGTH_LONG).show();

        }

    }

    private void extraDataFromIntent() {
        Intent intent = getIntent();
        groupId = intent.getLongExtra("ID",-1);
    }

    private void setUpSendMsgToGroupBtn() {

        TableRow tr = (TableRow) findViewById(R.id.my_table_row);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(getString(R.string.checkbox_text));
        checkBox.setTextSize(22);
        checkBox.setTypeface(Typeface.DEFAULT_BOLD);

        tr.setPadding(250,0,50,0);

        tr.addView(checkBox);

        Button btn = findViewById(R.id.sendBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = new Message();
//                TextView textView = findViewById(R.id.messageEdit);
                EditText textView = (EditText) findViewById(R.id.messageEdit);
                String text = textView.getText().toString();

                if(checkBox.isChecked()){
                    message.setEmergency(true);

                }
                else{
                    message.setEmergency(false);

                }
                message.setIsRead(false);
                message.setFromUser(currentUser);
                message.setText(""+text);

                Call<List<Message>> caller = proxy.newMessageToGroup(groupId,message);
                ProxyBuilder.callProxy(SendMessage.this,caller,returnedMsg -> response(returnedMsg));
            }
        });
    }
    private void response(List<Message> returnedMsg) {
        Toast.makeText(this, ""+returnedMsg.get(0).getId(), Toast.LENGTH_LONG).show();


    }
    public static Intent makeIntent(Context context, Long groupId){
        Intent intent = new Intent(context,SendMessage.class);
        intent.putExtra("ID",groupId);
        return intent;
    }
}
