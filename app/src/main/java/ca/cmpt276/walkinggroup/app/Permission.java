package ca.cmpt276.walkinggroup.app;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class Permission extends AppCompatActivity {

    private WGServerProxy proxy = CurrentUserData.getSingletonInstance().getCurrentProxy();
    private User currentUser = CurrentUserData.getSingletonInstance().getCurrentUser();

    private List<PermissionRequest> permissionRequestList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        displayPermission();
    }

    private void displayPermission() {
        Call<List<PermissionRequest>> caller = proxy.getPermissions();
        ProxyBuilder.callProxy(this,caller, returnedPermission -> responseReturnListPermission(returnedPermission));
    }

    private void responseReturnListPermission(List<PermissionRequest> returnedPermission) {
        if(!returnedPermission.isEmpty()){
            permissionRequestList = returnedPermission;


            ListView permissionList = findViewById(R.id.permissionListView);
            ArrayAdapter<PermissionRequest> adapter= new ArrayAdapter<PermissionRequest>(this, R.layout.permission_list,returnedPermission){


                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.permission_list, parent, false);
                    }

                    TextView statusText = convertView.findViewById(R.id.statusText);
                    TextView permissionContent = convertView.findViewById(R.id.permissionContent);
                    TextView userDeny = convertView.findViewById(R.id.userApproveOrDeny);


                    PermissionRequest permissionRequest = returnedPermission.get(position);

                    WGServerProxy.PermissionStatus status = permissionRequest.getStatus();
                    String message = permissionRequest.getMessage();
                    //TODO: list who have approved this request or deny.

//                    Set<PermissionRequest.Authorizor> authorizors;
//                    authorizors = permissionRequest.getAuthorizors();
//                    permissionRequest.getAuthorizors().size();

                    if(status == WGServerProxy.PermissionStatus.PENDING){
                        statusText.setText(getString(R.string.pending));
                    }
                    else if (status == WGServerProxy.PermissionStatus.APPROVED){
                        statusText.setText(getString(R.string.Approved));
                    }
                    else{
                        statusText.setText(getString(R.string.denied));
                    }

                    permissionContent.setText(message);


//                    if(authorizors.size()==0){
//                        userDeny.setText("zero");
//                    }
//                    else if (authorizors.size()==1){
//                        userDeny.setText("one");
//                    }
//                    else{
//                        userDeny.setText("more than 2");
//                    }
                    //userDeny.setText("" + authorizors.size());
                    //Iterator<PermissionRequest.Authorizor> iterator = authorizors.iterator();
//                    while(iterator.hasNext()) {
//                        PermissionRequest.Authorizor authorizor = iterator.next();
//                        if(authorizor.getStatus() == WGServerProxy.PermissionStatus.PENDING){
//                            userDeny.setText("pending !");
//                        }
//                    }





                    return convertView;
                }

            };
            permissionList.setAdapter(adapter);
        }
        else{
            //TODO
            Toast.makeText(Permission.this,"empty",Toast.LENGTH_LONG).show();;
        }


    }

}
