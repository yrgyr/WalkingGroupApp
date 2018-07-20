package ca.cmpt276.walkinggroup.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class MessageFragment extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout,null);

        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent=new Intent(getActivity(),EditContactInfo.class);
                        startActivity(intent);


                }



            }
        };



        return new AlertDialog.Builder(getActivity())
                .setTitle("ALERT!!")
                .setView(v)
                .setPositiveButton("Edit",listener)
                .setNegativeButton("cancel",listener)
                .create();


    }
}
