package ca.cmpt276.walkinggroup.app;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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

                TextView tv = (TextView) view.findViewById(R.id.msg_item);
                tv.setTypeface(Typeface.DEFAULT);

//                Long msgID = all_Msgs.get(position).getId();


            }
        });

    }
    private void displayMsgs() {

        Call<List<Message>> caller = proxy.getMessages(currentUser.getId());
        ProxyBuilder.callProxy(this,caller, returnedMsgs -> responseAllMsg(returnedMsgs));
    }

    private void responseAllMsg(List<Message> returnedMsgs) {

        all_Msgs = returnedMsgs;

        ListView msg_list = (ListView) findViewById(R.id.msgListView);
        ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this,R.layout.users_list,returnedMsgs){


            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View itemView = convertView;
                if(itemView == null){
                    itemView = getLayoutInflater().inflate(R.layout.users_list,parent,false);
                }

                Message currentMsg = returnedMsgs.get(position);




                Date timestamp = currentMsg.getTimestamp();
                String display_msg = currentMsg.getText() + " \n" + timestamp ;

                TextView tv = (TextView) itemView.findViewById(R.id.msg_item);
                tv.setText(display_msg);

                if(currentMsg.isRead()){
                    tv.setTypeface(Typeface.DEFAULT);
                }
                return itemView;
            }

        };
        msg_list.setAdapter(adapter);


//        for(int i =0; i < returnedMsgs.size();i++){
//            Message THIS_MSG = returnedMsgs.get(i);
//            if(THIS_MSG.isRead() == true){
//                View v = msg_list.getChildAt(i - msg_list.getFirstVisiblePosition());
//                if(v == null)
//                    return;
//                TextView tv = (TextView) v.findViewById(R.id.msg_item);
//                tv.setTypeface(Typeface.DEFAULT);
//
//            }
//
//        }


    }
//    private class Myadapter extends ArrayAdapter<String>{
//
//        public Myadapter(){
//            super(MsgToMe.this,R.layout.users_list,messages);
//
//        }
//

//    }
}
