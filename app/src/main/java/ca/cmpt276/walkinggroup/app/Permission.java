package ca.cmpt276.walkinggroup.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

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
//                @NonNull
//               @Override
//                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                    if (convertView == null) {
//                        convertView = getLayoutInflater().inflate(R.layout.msg_list, parent, false);
//                    }
//                    PermissionRequest permissionRequest = returnedPermission.get(position);
//
//                    WGServerProxy.PermissionStatus status = permissionRequest.getStatus();
//
//                    String statusString;
//                    if(status == WGServerProxy.PermissionStatus.PENDING){
//                        statusString = getString(R.string.pending);
//                    }
//                    else if(status == WGServerProxy.PermissionStatus.APPROVED){
//                        statusString = getString(R.string.Approved);
//                    }
//                    else{
//                        statusString = getString(R.string.denied);
//                    }
//                    TextView statusText = findViewById(R.id.statusText);
//                    statusText.setText(statusString);
//
//                    if(status != WGServerProxy.PermissionStatus.PENDING){
//                        //String
//                    }
//
//                }
            };
        }
    }

}
