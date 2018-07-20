package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private EditText updateContactNumber;
    private EditText updateHomePhone;
    private EditText updateAddress;
    private EditText updateBirthYear;
    private EditText updateBirthMonth;
    private EditText updateTeacher;
    private EditText updateGrade;
    private EditText updateEmergency;




    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);

        proxy = userSingleton.getCurrentProxy();


        extraDataFromIntent();

        setupValidateButton();



    }
    private void extraDataFromIntent() {
        Intent intent = getIntent();
        Long userId = intent.getLongExtra("ID",-1);
        if(userId != -1){
            Call<User> caller = proxy.getUserById(userId);
            ProxyBuilder.callProxy(EditContactInfo.this, caller, returnedUser -> response(returnedUser));
        }

    }
    private void response(User returnUser) {
        user = returnUser;
        setUpdatedInfo();
    }

    //Get the new info from the fields

    private void setUpdatedInfo() {

        updateName=findViewById(R.id.updateName);
        updateName.setText(user.getName());


        updateEmail=findViewById(R.id.updateEmail);
        updateEmail.setText(user.getEmail());

        updateAddress=findViewById(R.id.updateAddress);
        updateAddress.setText(user.getAddress());

        updateContactNumber=findViewById(R.id.updatePhone);
        updateContactNumber.setText(user.getCellPhone());

        updateHomePhone=findViewById(R.id.updateHomePhone);
        updateHomePhone.setText(user.getHomePhone());

        updateGrade=findViewById(R.id.updateGrade);
        updateGrade.setText(user.getGrade());

        updateTeacher=findViewById(R.id.updateTeacherName);
        updateTeacher.setText(user.getTeacherName());

        updateEmergency=findViewById(R.id.updateEmergencyContact);
        updateEmergency.setText(user.getEmergencyContactInfo());

        updateBirthYear=findViewById(R.id.updateYear);

        updateBirthYear.setText(""+user.getBirthYear());


        updateBirthMonth=findViewById(R.id.updateMonth);
        updateBirthMonth.setText(""+user.getBirthMonth());



    }

    //Set the new info in the User class

    private void setNewInfo(){
        String newName=updateName.getText().toString();
        user.setName(newName);

        String newEmail=updateEmail.getText().toString();
        user.setEmail(newEmail);

        String newAddress=updateAddress.getText().toString();
        user.setAddress(newAddress);

        String newCellPhone=updateContactNumber.getText().toString();
        user.setCellPhone(newCellPhone);

        String newHomePhone=updateHomePhone.getText().toString();
        user.setHomePhone(newHomePhone);

        String newGrade=updateGrade.getText().toString();
        user.setGrade(newGrade);

        String newTeacher=updateTeacher.getText().toString();
        user.setTeacherName(newTeacher);

        String newEmergency=updateEmergency.getText().toString();
        user.setEmergencyContactInfo(newEmergency);

        String month=updateBirthMonth.getText().toString();
        try {
            int newMonth = Integer.parseInt(month);
            user.setBirthMonth(newMonth);
        }
        catch (NumberFormatException e){}


        String year=updateBirthYear.getText().toString();
        try{
            int newYear=Integer.parseInt(year);
            user.setBirthYear(newYear);
        }
        catch (NumberFormatException e){}







    }

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
    public static Intent makeIntent(Context context, Long UserId){
        Intent intent = new Intent(context,EditContactInfo.class);
        intent.putExtra("ID",UserId);
        return intent;
    }
}
