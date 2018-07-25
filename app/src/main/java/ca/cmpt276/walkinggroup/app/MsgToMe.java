package ca.cmpt276.walkinggroup.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class MsgToMe extends AppCompatActivity {

    private WGServerProxy proxy = CurrentUserData.getSingletonInstance().getCurrentProxy();
    private User currentUser = CurrentUserData.getSingletonInstance().getCurrentUser();

    private List<Message> all_Msgs;

    private ArrayList<String> messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_to_me);

        displayMsgs();
        setupOnItemClick();




    }

    private void setupOnItemClick() {

        ListView messages = (ListView) findViewById(R.id.msgListView);
        messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.timeSent);
                tv.setTypeface(Typeface.DEFAULT);

                TextView tv2 = (TextView) view.findViewById(R.id.msgContent);
                tv2.setTypeface(Typeface.DEFAULT);

                TextView tv3 = (TextView) view.findViewById(R.id.fromWhom);
                tv3.setTypeface(Typeface.DEFAULT);


                Long msgID = all_Msgs.get(position).getId();
                String content = all_Msgs.get(position).getText();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MsgToMe.this);
                alertBuilder.setTitle(getString(R.string.msg_content_dialog_title));


                alertBuilder.setMessage(content);
                AlertDialog aDialog = alertBuilder.create();
                aDialog.show();



                Call<Message> caller = proxy.markMessageAsRead(msgID,true);
                ProxyBuilder.callProxy(MsgToMe.this,caller, returnedMsg -> responseMarkedMsg(returnedMsg));



            }
        });

    }

    private void responseMarkedMsg(Message returnedMsg){


    }

    private void displayMsgs() {

        Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
        ProxyBuilder.callProxy(this,caller, returnedMsgs -> responseAllMsg(returnedMsgs));
    }

    private void responseAllMsg(List<Message> returnedMsgs) {
        if(!returnedMsgs.isEmpty()) {
            all_Msgs = returnedMsgs;

            Collections.sort(returnedMsgs, new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            Collections.reverse(returnedMsgs);


            ListView msg_list = (ListView) findViewById(R.id.msgListView);
            ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, R.layout.msg_list, returnedMsgs) {


                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//                View convertView = convertView;
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.msg_list, parent, false);
                    }

                    Message currentMsg = returnedMsgs.get(position);

                    User from = currentMsg.getFromUser();

                    String fromName = from.getName();
                    Date timestamp = currentMsg.getTimestamp();
                    String time = timestamp.toString().substring(11, 19);
                    String day = timestamp.toString().substring(4, 10);
                    String displayTime = time + " , " + day;


                    String display_msg = fromName + "\n" + currentMsg.getText() + " \n" + time;

                    TextView fromWhomTv = (TextView) convertView.findViewById(R.id.fromWhom);
                    fromWhomTv.setText(getString(R.string.from_whom_display, fromName));

                    TextView timeTv = (TextView) convertView.findViewById(R.id.timeSent);
                    timeTv.setText(displayTime);

                    TextView contentTv = (TextView) convertView.findViewById(R.id.msgContent);
                    contentTv.setText(currentMsg.getText());



                    if (currentMsg.isRead()) {
                        fromWhomTv.setTypeface(Typeface.DEFAULT);
                        timeTv.setTypeface(Typeface.DEFAULT);
                        contentTv.setTypeface(Typeface.DEFAULT);
                    }
                    if (currentMsg.isEmergency()) {

                        convertView.setBackgroundResource(R.color.lightorange);
                    }
                    return convertView;
                }

                @Override
                public int getViewTypeCount() {

                    return getCount();
                }

                @Override
                public int getItemViewType(int position) {

                    return position;
                }

            };
            msg_list.setAdapter(adapter);
        }
        else{
            Toast.makeText(MsgToMe.this, getString(R.string.Not_Message),Toast.LENGTH_LONG).show();
        }
    }
}
