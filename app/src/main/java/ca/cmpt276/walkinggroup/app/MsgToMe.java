package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_to_me);

        displayMsgs();



    }
    private void displayMsgs() {

        Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
        ProxyBuilder.callProxy(this,caller, allMsgs -> responseAllMsg(allMsgs));
    }

    private void responseAllMsg(List<Message> allMsgs) {

        ArrayList<String> ALL_msgs = new ArrayList<String>();
        for(int i =0; i < allMsgs.size();i++){

            Message THIS_MSG = allMsgs.get(i);
            String text = THIS_MSG.getText();
//            String DISPLAY_THIS_MSG = THIS_MSG + "";

            ALL_msgs.add(text);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.users_list,ALL_msgs);
        ListView msg_list = (ListView) findViewById(R.id.msgListView);
        msg_list.setAdapter(adapter);
    }
}
