package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.MapsFunctions;
import ca.cmpt276.walkinggroup.dataobjects.MyItem;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.MainActivity.groupsList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();
    private User currentUser = userSingleton.getCurrentUser();

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // Todo: try chaning these 2 to private
    public double latitude;
    public double longitude;
    private double destLat;
    private double destLng;
    private boolean destSet = false;

    private ClusterManager<MyItem> mClusterManager;
    private LocationManager lm = userSingleton.getLocationManager();
    private LocationListener locationListener = userSingleton.getLocationListener();

    private List<Group> groupsOnServer = new ArrayList<>();
    private Group groupSelected = userSingleton.getGroupSelected();


    private boolean uploadingLocation = userSingleton.getUploadingLocation();
    private final int GPS_UPLOAD_INTERVAL_IN_MILLISEC = 8000;
    private final int GPS_UPLOAD_MIN_DIST_IN_METERS = 0;
    private GpsLocation currentGpsLocation = new GpsLocation();

    // Countdown timer when user reaches school
    // Todo: change these back to assignment parameters
    private final int GPS_COUNTDOWN_INTERVAL_IN_MILLISEC = 30000;
    private final int SCHOOL_RADIUS_IN_METERS = 20;
    private CountDownTimer DestReachedCountDown = userSingleton.getDestReachedCountDown();
    private boolean DestReachedCountDownRunning = userSingleton.isDestReachedCountDownRunning();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkIfDestSet();
        getLocationPermission();
        setupUploadButton();
        setupDashBoardButton();
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

    // Codes for google maps from Coding with Mitch tutorial video series: https://www.youtube.com/watch?v=OknMZUnTyds&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLatLng;

        if (mLocationPermissionsGranted) {
            currentLatLng = getDeviceLocation();
            if (currentLatLng != null) {
                groupsOnServer = groupsList;
                setUpLocalGroupCluster(groupsOnServer);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 888){
            if (resultCode == Activity.RESULT_OK){
                Group walkingGroup = userSingleton.getWalkingGroup();
                destLat = walkingGroup.getDestLat();
                destLng = walkingGroup.getDestLng();

//                destLat = data.getDoubleExtra("destLat", 0);
//                destLng = data.getDoubleExtra("destLng", 0);
                destSet = true;

                Toast.makeText(MapsActivity.this, "Destination lat: " + destLat + ", lng: " + destLng, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initializeMap();
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
                    initializeMap();
                }
            }
        }
    }

    private LatLng getDeviceLocation(){
        try{
            if (mLocationPermissionsGranted){
                // Code obtained from StacksOverflow https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
                //LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                if (lm == null){
                    lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                    userSingleton.setLocationManager(lm);
                }

                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null){
                    // default location in emulator
                    longitude = -122.084;
                    latitude = 37.422;
                }

                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

                LatLng currentLatLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

                return currentLatLng;
            }

        }catch (SecurityException e){
            Log.e(TAG, getString(R.string.getDeviceLocation_exception) + e.getMessage() );

        }

        return null;
    }


    private void setUpLocalGroupCluster(List<Group> groups){
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                Long grpId = myItem.getGrpId();
                getRemoteGroupById(grpId);
            }
        });

        addGroupsToCluster(groups);
    }


    private void addGroupsToCluster(List<Group> groups){
        if (groups != null) {
            if (groups.size() > 0) {
                for (int i = 0; i < groups.size(); i++) {
                    Group group = groups.get(i);
                    Long grpId = group.getId();
                    String grpDesc = group.getGroupDescription();


                    List<Double> latArr = group.getRouteLatArray();
                    List<Double> lngArr = group.getRouteLngArray();


                    // only populate groups with non-empty lat and lng arrays
                    if (latArr.size() > 0 && lngArr.size() > 0) {
                        double lat = group.getStartLat();
                        double lng = group.getStartLng();

                        MyItem newItem = new MyItem(lat, lng, getString(R.string.tag_on_info_window) + grpDesc, getString(R.string.group_info_window_snippet), grpId);
                        mClusterManager.addItem(newItem);
                    }
                }
            } else {
                Toast.makeText(MapsActivity.this, R.string.group_size_zero, Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(MapsActivity.this, R.string.group_list_null, Toast.LENGTH_LONG).show();
        }
    }

    private void setupDashBoardButton(){
        Button btn = findViewById(R.id.btn_open_dashboard);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ParentsDashboard.class);
                startActivity(intent);
            }
        });
    }

    private void setupPanicButton(){

    }

    private void setupUploadButton(){
        Button btn = findViewById(R.id.btn_upload_location);
        setUploadButtonText(btn);
        //btn.setText(R.string.btn_start_uploading);
        //Toast.makeText(MapsActivity.this, "uploadingLocation: " + uploadingLocation, Toast.LENGTH_LONG).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadingLocation) {
                    //btn.setText(R.string.btn_start_uploading);
                    userSingleton.setUploadingLocation(false);
                    uploadingLocation = userSingleton.getUploadingLocation();
                    setUploadButtonText(btn);
                        //Toast.makeText(MapsActivity.this, "Turning off location listener", Toast.LENGTH_SHORT).show();
                    lm.removeUpdates(locationListener);
                    if (DestReachedCountDownRunning){
                        resetDestReachedCountDown();
                    }
                } else {
                    //btn.setText(R.string.btn_stop_uploading);
                    if (!destSet) {
                        Toast.makeText(MapsActivity.this, "Please select a group to walk with first!", Toast.LENGTH_LONG).show();
                    } else {
                        userSingleton.setUploadingLocation(true);
                        uploadingLocation = userSingleton.getUploadingLocation();
                        setUploadButtonText(btn);
                        setupLocationListener();
                    }
                }
            }
        });

    }

    private void setUploadButtonText(Button btn){
        //Toast.makeText(MapsActivity.this, "uploadingLocation: " + uploadingLocation, Toast.LENGTH_LONG).show();
        //Button btn = findViewById(R.id.btn_upload_location);
        if (uploadingLocation){
            btn.setText(R.string.btn_stop_uploading);
            //Toast.makeText(MapsActivity.this, "Set button text to stop", Toast.LENGTH_LONG).show();
        } else {
            btn.setText(R.string.btn_start_uploading);
            //Toast.makeText(MapsActivity.this, "Set button text to start", Toast.LENGTH_LONG).show();
        }
    }

    private void setupLocationListener(){
        try{
            if (mLocationPermissionsGranted){
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();

                        String currentTime = MapsFunctions.getCurrentTimeStamp();
                        Toast.makeText(MapsActivity.this, currentTime + ", lat: " + latitude + " , long: " + longitude, Toast.LENGTH_LONG).show();

                        // Calculate distance to destination and start count down timer if one has not been started
                        double distanceToDest = MapsFunctions.distanceInMBetweenTwoCoordinates(latitude, longitude, destLat, destLng);
                        if (distanceToDest <= SCHOOL_RADIUS_IN_METERS){
                            if (!DestReachedCountDownRunning){
                                startDestReachedCountDown();
                            }
                        } else {
                            if (DestReachedCountDownRunning){
                                resetDestReachedCountDown();
                            }
                        }

                        // Send the coordinates to the server
                        currentGpsLocation.setLat(latitude);
                        currentGpsLocation.setLng(longitude);
                        currentGpsLocation.setTimestamp(currentTime);

                        Long currentUserId = currentUser.getId();
                        uploadCurrentLocation(currentUserId, currentGpsLocation);


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                userSingleton.setLocationListener(locationListener);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPLOAD_INTERVAL_IN_MILLISEC, GPS_UPLOAD_MIN_DIST_IN_METERS, locationListener);
            }
        }catch (SecurityException e){
            Log.e(TAG, getString(R.string.getDeviceLocation_exception) + e.getMessage() );

        }
    }

    private void checkIfDestSet(){
        Group walkingGroup = userSingleton.getWalkingGroup();
        if (walkingGroup == null){
            destSet = false;
            //Toast.makeText(MapsActivity.this, "Dest not set yet", Toast.LENGTH_SHORT).show();
        } else {
            destSet = true;
            destLat = walkingGroup.getDestLat();
            destLng = walkingGroup.getDestLng();
            //Toast.makeText(MapsActivity.this, "Previously stored destLat: " + destLat + " , destLng: " + destLng, Toast.LENGTH_LONG).show();
        }
    }

    private void startDestReachedCountDown(){
        DestReachedCountDown = new CountDownTimer(GPS_COUNTDOWN_INTERVAL_IN_MILLISEC, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished/1000;
                Toast.makeText(MapsActivity.this, seconds + " seconds of uploading left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Toast.makeText(MapsActivity.this, "Uploading location stops", Toast.LENGTH_SHORT).show();
                lm.removeUpdates(locationListener);
                DestReachedCountDownRunning = false;
                userSingleton.setDestReachedCountDownRunning(DestReachedCountDownRunning);
                uploadingLocation = false;
                userSingleton.setUploadingLocation(uploadingLocation);

                // Seemed to work using either method call
                //setUploadButtonText();
                setupUploadButton();

                // Tried the 2 lines of code below won't even change the text of the button
                //Button btn = findViewById(R.id.btn_upload_location);
                //btn.setText("Timer stops");
            }
        }.start();

        userSingleton.setDestReachedCountDown(DestReachedCountDown);
        DestReachedCountDownRunning = true;
        userSingleton.setDestReachedCountDownRunning(DestReachedCountDownRunning);
    }

    private void resetDestReachedCountDown(){
        DestReachedCountDown.cancel();
        DestReachedCountDownRunning = false;
        userSingleton.setDestReachedCountDownRunning(DestReachedCountDownRunning);
    }

    private void getRemoteGroupById(Long id){
        Call<Group> caller = proxy.getGroupById(id);
        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedGroup -> returnedGroupById(returnedGroup));
    }

    private void returnedGroupById(Group group){
        groupSelected = group;
        userSingleton.setGroupSelected(group);
        Intent intent = new Intent(MapsActivity.this, Join_Group.class);
        //startActivity(intent);
        startActivityForResult(intent, 888);
    }

    private void uploadCurrentLocation(Long userId, GpsLocation location){
        Call<GpsLocation> caller = proxy.setLastGpsLocation(userId, location);
        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedGpsLocation -> responseUploadGps(returnedGpsLocation));
    }

    private void responseUploadGps(GpsLocation location){
        String receivedTime = location.getTimestamp();
        double lat = location.getLat();
        double lng = location.getLng();
        //Toast.makeText(MapsActivity.this, "lat: " + lat + " lng: " + lng + " received at: " + receivedTime, Toast.LENGTH_LONG).show();
    }
}
