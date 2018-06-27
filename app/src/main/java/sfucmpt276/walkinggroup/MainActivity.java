package sfucmpt276.walkinggroup;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // From video tutorial https://www.youtube.com/watch?v=urLA8z6-l3k&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=3&t=0s
    public boolean IsGooglePlayServicesOK(){
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS){
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)){
            // error occurred but is resolvable
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Error: unable to make Google Map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
