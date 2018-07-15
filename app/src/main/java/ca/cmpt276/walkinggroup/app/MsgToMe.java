package ca.cmpt276.walkinggroup.app;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MsgToMe extends AppCompatActivity {

    WGServerProxy proxy = CurrentUserData.getSingletonInstance().getCurrentProxy();
    User currentUser = CurrentUserData.getSingletonInstance().getCurrentUser();

    List<Message> all_msgs;
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
                Message msg = all_msgs.get(position);

                TextView tv = (TextView) view.findViewById(R.id.msg_item);
                tv.setTypeface(Typeface.DEFAULT);
            }
        });

    }


    private void displayMsgs() {

        Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
        ProxyBuilder.callProxy(this,caller, returnedMsgs -> responseAllMsg(returnedMsgs));
    }

    private void responseAllMsg(List<Message> returnedMsgs) {

        all_msgs = returnedMsgs;
        ArrayList<String> ALL_msgs = new ArrayList<String>();
        for(int i =0; i < returnedMsgs.size();i++){


            Message THIS_MSG = returnedMsgs.get(i);
            String text = THIS_MSG.getText();

            ALL_msgs.add(text);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.users_list,ALL_msgs);
        ListView msg_list = (ListView) findViewById(R.id.msgListView);

        msg_list.setAdapter(adapter);
    }

}
