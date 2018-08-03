package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.MapsFunctions;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ParentsDashboard extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "ParentsDashboard";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private LocationManager lm;
    private final int UPDATE_UI_FREQUENCY_IN_MILLISECS = 60000;
    private final int MARKER_INACTIVE_TIME_IN_SEC = 300;
    private final int SECONDS_IN_HOUR = 3600;

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private User currentUser = userSingleton.getCurrentUser();
    private List<User> monitorsUsers = currentUser.getMonitorsUsers();
    private List<User> leaders = new ArrayList<>();  // leaders of groups in which my children are in

    // Map marker colours
    private final float ACTIVE_CHILDREN_MARKER_COLOUR = BitmapDescriptorFactory.HUE_GREEN;
    private final float ACTIVE_LEADERS_MARKER_COLOUR = BitmapDescriptorFactory.HUE_CYAN;
    private final float INACTIVE_CHILDREN_MARKER_COLOUR = BitmapDescriptorFactory.HUE_RED;
    private final float INACTIVE_LEADERS_MARKER_COLOUR = BitmapDescriptorFactory.HUE_ORANGE;

    private int unreadCount = 0;
    private int unreadNotEmergent = 0;
    private int unreadEmergent = 0;
    private Runnable myRun;



    // Code for handler: https://guides.codepath.com/android/Repeating-Periodic-Tasks
    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            mMap.clear();
            populateLocationMarkers();

            Call<List<Message>> caller = proxy.getUnreadMessages(currentUser.getId(), false);
            ProxyBuilder.callProxy(ParentsDashboard.this, caller, messageReturn -> responseGetMessage(messageReturn));


            handler.postDelayed(this, UPDATE_UI_FREQUENCY_IN_MILLISECS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_dashboard);
        getLocationPermission();
        populateLocationMarkers();
        getUnReadMessageList();
        setUpMail();

        setUpRefresh();


        // start the routine to update location markers every 30 seconds
        handler.post(runnableCode);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("App", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onMapReady");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            lm = MapsFunctions.getDeviceLocation(mMap, mLocationPermissionsGranted, DEFAULT_ZOOM, this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void initMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            } else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void populateLocationMarkers(){
        // Populate children's markers
        if (monitorsUsers.size() > 0) {
            for (int i = 0; i < monitorsUsers.size(); i++) {
                User user = monitorsUsers.get(i);
                long userId = user.getId();
                String userName = user.getName();

                Call<GpsLocation> caller = proxy.getLastGpsLocation(userId);
                ProxyBuilder.callProxy(ParentsDashboard.this, caller, returnedGpsLocation -> addReturnedLocationMarker(returnedGpsLocation, userName, true));

                // get the group leaders of all the groups in which this user is a member of and add user to leaders list
                List<Group> groups = user.getMemberOfGroups();

                addGroupLeadersMarkers(groups);
            }


        }

    }

    private void addReturnedLocationMarker(GpsLocation gpsLocation, String userName, boolean isChildren) {
        //Log.e("Marker username: ", userName);

        boolean GpsLocationNotEmpty = gpsLocation.isGpsLocationNotEmpty();

        if (GpsLocationNotEmpty) {


            double lat = gpsLocation.getLat();
            double lng = gpsLocation.getLng();
            String time = gpsLocation.getTimestamp();


            //convert the timestamp to datetime.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date d1 = null;

            try {
                d1 = format.parse(time);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            long gpsTime = d1.getTime();


            long sysTime = System.currentTimeMillis();
            //restTime in seconds
            long resTime = (sysTime - gpsTime) / 1000;



            if (resTime <= 60) {
                String infoWindow = userName + "- " + getString(R.string.marker_last_updated) + " " + resTime + " " + getString(R.string.marker_secs_ago);
                if (isChildren) {
                    MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, ACTIVE_CHILDREN_MARKER_COLOUR);
                } else {
                    MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, ACTIVE_LEADERS_MARKER_COLOUR);
                }

            } else {
                if (resTime > MARKER_INACTIVE_TIME_IN_SEC) {
                    if (resTime < SECONDS_IN_HOUR) {
                        String infoWindow = userName + "- " + getString(R.string.marker_went_offline) + " " + resTime / 60 + " " + getString(R.string.marker_mins_ago);
                        if (isChildren) {
                            MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, INACTIVE_CHILDREN_MARKER_COLOUR);
                        } else {
                            MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, INACTIVE_LEADERS_MARKER_COLOUR);
                        }
                    } else {
                        String infoWindow = userName + "- " + getString(R.string.marker_offline_more_than_1_hr);
                        if (isChildren) {
                            MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, INACTIVE_CHILDREN_MARKER_COLOUR);
                        } else {
                            MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, INACTIVE_LEADERS_MARKER_COLOUR);
                        }
                    }
                } else {
                    String infoWindow = userName + "- " + getString(R.string.marker_last_updated) + " " + resTime / 60 + " " + getString(R.string.marker_mins_ago);
                    if (isChildren) {
                        MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, ACTIVE_CHILDREN_MARKER_COLOUR);
                    } else {
                        MapsFunctions.addMarkerToMap(mMap, lat, lng, infoWindow, true, ACTIVE_LEADERS_MARKER_COLOUR);
                    }
                }

            }
        }
    }


    private void addGroupLeadersMarkers(List<Group> groups){
        for (int i = 0; i < groups.size(); i++){
            Group group = groups.get(i);
            long groupId = group.getId();
            getFullGroupDetails(groupId);
        }
    }

    private void getFullGroupDetails(long groupId){
        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(ParentsDashboard.this, caller, returnedGroup -> responseReturnGroup(returnedGroup));
    }

    private void responseReturnGroup(Group group){
        User leader = group.getLeader();
        long leaderId = leader.getId();
        String leaderName = leader.getName();

        Call<GpsLocation> caller = proxy.getLastGpsLocation(leaderId);
        ProxyBuilder.callProxy(ParentsDashboard.this, caller, returnedGpsLocation -> addReturnedLocationMarker(returnedGpsLocation, leaderName, false));
    }
    private void getUnReadMessageList() {

        Call<List<Message>> caller = proxy.getUnreadMessages(currentUser.getId(),false);
        ProxyBuilder.callProxy(ParentsDashboard.this,caller,messageReturn -> responseGetMessage(messageReturn));

    }
    private void responseGetMessage (List<Message> messageReturn) {
        unreadNotEmergent = messageReturn.size();

        Call<List<Message>> caller = proxy.getUnreadMessages(currentUser.getId(), true);
        ProxyBuilder.callProxy(ParentsDashboard.this, caller, messageReturnEm -> responseGetMessageForEmergent(messageReturnEm));

    }

    private void responseGetMessageForEmergent(List<Message> messageReturnEm) {
        unreadEmergent = messageReturnEm.size();
        unreadCount = unreadEmergent + unreadNotEmergent;
        Button button = findViewById(R.id.showUnreadText);
        button.setText(getString(R.string.unreadCountForParent, unreadCount));
    }

    private void setUpMail() {
        Button button = findViewById(R.id.showUnreadText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentsDashboard.this,MsgToMe.class);
                startActivity(intent);
            }
        });
    }
    private void setUpRefresh() {
        Button button = findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                populateLocationMarkers();
                Call<List<Message>> caller = proxy.getUnreadMessages(currentUser.getId(), false);
                ProxyBuilder.callProxy(ParentsDashboard.this, caller, messageReturn -> responseGetMessage(messageReturn));
            }
        });
    }



}
