package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.MyItem;
import ca.cmpt276.walkinggroup.dataobjects.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionsGranted = false;
    private boolean getDeviceLocationSuccess = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ClusterManager<MyItem> mClusterManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();

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
        mMap = googleMap;
        LatLng currentLatLng;

        if (mLocationPermissionsGranted) {
            currentLatLng = getDeviceLocation();
            if (currentLatLng != null) {
                //setUpClusterer(70, 100);
                // Test map with locally created groups
                List<Group> groups = createLocalTestGroups(currentLatLng);
                setUpLocalGroupCluster(groups);

            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void initializeMap(){
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
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                LatLng currentLatLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

                Toast.makeText(this, "longitude: " + longitude + ", latitude: " + latitude, Toast.LENGTH_LONG).show();
                return currentLatLng;
            }

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );

        }

        return null;
    }

    // Map clusters
    // Documentation: https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
//    private void setUpClusterer(double lat, double lng) {
//        // Initialize the manager with the context and the map.
//        // (Activity extends context, so we can pass 'this' in the constructor.)
//        mClusterManager = new ClusterManager<MyItem>(this, mMap);
//
//        // Point the map's listeners at the listeners implemented by the cluster
//        // manager.
//        mMap.setOnCameraIdleListener(mClusterManager);
//        mMap.setOnInfoWindowClickListener(mClusterManager);
//
//        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
//            @Override
//            public void onClusterItemInfoWindowClick(MyItem myItem) {
//                Intent intent = new Intent(MapsActivity.this, Join_Group.class);
//                startActivity(intent);
//            }
//        });
//
//        // Add cluster items (markers) to the cluster manager.
//        addItems(lat, lng);
//    }

    private void setUpLocalGroupCluster(List<Group> groups){
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                int grpId = myItem.getGrpId();
                Group group = getLocalGroupById(groups, grpId);
                Group groupToLaunch = Group.getGroupSingletonInstance();
                groupToLaunch.setToGroup2Params(group);
                Intent intent = new Intent(MapsActivity.this, Join_Group.class);
                startActivity(intent);
            }
        });

        addGroupsToCluster(groups);
    }

//    private void addItems(double lat, double lng) {
//
//        // Set some lat/lng coordinates to start with.
////        double lat = 51.5145160;
////        double lng = -0.1270060;
//
//        // Add ten cluster items in close proximity, for purposes of this example.
//        for (int i = 0; i < 10; i++) {
//            double offset = i / 200d;
//            lat = lat + offset;
//            lng = lng + offset;
//            MyItem offsetItem = new MyItem(lat, lng, "Group " + i, "Click to join group", 0);
//            mClusterManager.addItem(offsetItem);
//        }
//    }

    private void addGroupsToCluster(List<Group> groups){
        if (groups.size() > 0){
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                int grpId = group.getGroupId();
                String grpDesc = group.getGroupDescription();
                double lat = group.getStartLat();
                double lng = group.getStartLng();

                MyItem newItem = new MyItem(lat, lng, "Group " + grpId + "- " + grpDesc, "Click here to view group", grpId);
                mClusterManager.addItem(newItem);
            }
        }
    }

    // Todo: remove later; for testing locally created groups only- setup 2 groups consisting of 5 users each locally
    private List<Group> createLocalTestGroups(LatLng latlng){
        List<Group> groups = new ArrayList<>();
        double lat = latlng.latitude;
        double lng = latlng.longitude;

        for (int i = 0; i < 2; i++){
            Group group = new Group();
            group.setGroupId(i);
            group.setGroupDescription("Local group " + i);

            List<User> members = new ArrayList<>();

            for (int j = 0; j < 5; j++){
                User member = new User();
                member.setName("Group " + i + " user " + j);
                member.setId((long)j);
                members.add(member);

                if (j == 0){
                    group.setLeader(member);
                }
            }
            group.setGroupMembers(members);

            double offset = i / 200d;
            lat = lat + offset;
            lng = lng + offset;
            group.setStartLat(lat);
            group.setStartLng(lng);

            groups.add(group);

        }

        return groups;
    }

    // Todo: remove later; for testing locally created groups only; server has getGroupById method
    private Group getLocalGroupById(List<Group> groups, int grpId){
        if (groups.size() > 0){
            for(int i = 0; i < groups.size(); i++){
                Group group = groups.get(i);
                if (group.getGroupId() == grpId){
                    return group;
                }
            }
            return null;
        } else {
            return null;
        }
    }

}