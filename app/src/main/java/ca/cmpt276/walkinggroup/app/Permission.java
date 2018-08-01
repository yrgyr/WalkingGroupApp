package ca.cmpt276.walkinggroup.app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
        itemClick();
        setUpRefreshBtn();
    }




    private void displayPermission() {
        Call<List<PermissionRequest>> caller = proxy.getAllPermissionByUserId(currentUser.getId());
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

                    PermissionRequest permissionRequest = returnedPermission.get(position);

                    WGServerProxy.PermissionStatus status = permissionRequest.getStatus();
                    String message = permissionRequest.getMessage();


                    if(status == WGServerProxy.PermissionStatus.PENDING){
                        statusText.setText(getString(R.string.pending));
                        statusText.setTextColor(Color.RED);
                        convertView.setBackgroundResource(R.color.lightorange);

                    }
                    else if (status == WGServerProxy.PermissionStatus.APPROVED){
                        statusText.setText(getString(R.string.Approved));
                    }
                    else{
                        statusText.setText(getString(R.string.denied));
                    }

                    permissionContent.setText(message);

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
            permissionList.setAdapter(adapter);
        }
        else{
            Toast.makeText(Permission.this, getString(R.string.noPermission),Toast.LENGTH_LONG).show();
        }


    }
    private void itemClick() {
        ListView permissionList = findViewById(R.id.permissionListView);
        permissionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PermissionRequest permissionRequest = permissionRequestList.get(position);

                PermissionRequest.Authorizor currentAuthorizor = new PermissionRequest.Authorizor();

                Set<PermissionRequest.Authorizor> authorizors = permissionRequest.getAuthorizors();

                ArrayList<String> listOfUsersApprovedOrDeny = new ArrayList<String>();


                String textOfUser = "";
                WGServerProxy.PermissionStatus status = WGServerProxy.PermissionStatus.PENDING;

                for (PermissionRequest.Authorizor authorizor : authorizors) {
                    if(authorizor.getStatus() != WGServerProxy.PermissionStatus.PENDING){
                        User user = authorizor.getWhoApprovedOrDenied();
                        String name = user.getName();

                        if(authorizor.getStatus() == WGServerProxy.PermissionStatus.APPROVED){
                            textOfUser += name + getString(R.string.approveThisReq) + "\n";
                        }
                        else{
                            textOfUser += name + getString(R.string.denyThisREQ) + "\n";
                        }
                        listOfUsersApprovedOrDeny.add(textOfUser);
                    }

                    if(authorizor.getUsers().contains(currentUser)){
                        currentAuthorizor = authorizor;
                        status = currentAuthorizor.getStatus();
                    }
                }


                if(permissionRequest.getStatus() == WGServerProxy.PermissionStatus.PENDING && status == WGServerProxy.PermissionStatus.PENDING) {

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Permission.this);
                    View mview=getLayoutInflater().inflate(R.layout.delete_dialog,null);

                    TextView text = mview.findViewById(R.id.deleteDialog);
                    String messageString = permissionRequest.getMessage();
                    text.setText(messageString);

                    builder.setMessage(getString(R.string.makeDecision))
                            .setView(mview)
                            .setPositiveButton(getString(R.string.Approved), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Long id = permissionRequest.getId();

                                    Call<PermissionRequest> caller = proxy.approveOrDenyPermissionRequest(id,WGServerProxy.PermissionStatus.APPROVED);
                                    ProxyBuilder.callProxy(Permission.this,caller,returnNothing->responseNothing(returnNothing,WGServerProxy.PermissionStatus.APPROVED));

                                }
                            })


                            .setNegativeButton(getString(R.string.denied), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Long id = permissionRequest.getId();

                                    Call<PermissionRequest> caller = proxy.approveOrDenyPermissionRequest(id,WGServerProxy.PermissionStatus.DENIED);
                                    ProxyBuilder.callProxy(Permission.this,caller,returnNothing->responseNothing(returnNothing,WGServerProxy.PermissionStatus.DENIED));
                                }
                            })
                            .setCancelable(true);


                    AlertDialog alert=builder.create();
                    alert.show();
                }
                else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Permission.this);
                    View mview=getLayoutInflater().inflate(R.layout.delete_dialog,null);
                    TextView text = mview.findViewById(R.id.deleteDialog);

                    text.setText(textOfUser);


                    builder.setMessage(getString(R.string.makeDecision))
                            .setView(mview)
                            .setCancelable(true);

                    AlertDialog alert=builder.create();
                    alert.show();
                }
            }
        });
    }

    private void responseNothing(PermissionRequest returnNothing, WGServerProxy.PermissionStatus status) {
        String text;
        if(status == WGServerProxy.PermissionStatus.APPROVED){
            text = getString(R.string.Approved);

        }
        else{
            text = getString(R.string.denied);
        }
        Toast.makeText(Permission.this,text,Toast.LENGTH_LONG).show();
        displayPermission();
    }
    private void setUpRefreshBtn() {
        Button btn = findViewById(R.id.refreshBtnForPermission);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPermission();
            }
        });

    }

}
