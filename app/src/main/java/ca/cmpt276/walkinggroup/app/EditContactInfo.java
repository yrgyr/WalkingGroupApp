package ca.cmpt276.walkinggroup.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class EditContactInfo extends AppCompatActivity {


    private WGServerProxy proxy;
    private User user;

    private EditText updateName;
    private EditText updateEmail;
    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);

        proxy = userSingleton.getCurrentProxy();

        user = userSingleton.getCurrentUser();





        setUpdatedInfo();
        setupValidateButton();







    }

    //Get the new info from the fields

    private void setUpdatedInfo() {
        updateName=findViewById(R.id.updateName);
        updateName.setText(user.getName());

        updateEmail=findViewById(R.id.updateEmail);
        updateEmail.setText(user.getEmail());

    }

    //Set the new info in the User class

    private void setNewInfo(){
        String newName=updateName.getText().toString();
        user.setName(newName);

        String newEmail=updateEmail.getText().toString();
        user.setEmail(newEmail);



    }
    //D
    private void setupValidateButton() {
        Button doneBtn=(Button) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewInfo();
                //update the info in the server
                Call<User> caller=proxy.editUser(user,user.getId());
                ProxyBuilder.callProxy(EditContactInfo.this,caller,returnedUser->userResponse(returnedUser));
            }
        });
    }

    private void userResponse(User returnedUser) {
        Log.i("Updated Name","Successfull");
        finish();
    }
}
