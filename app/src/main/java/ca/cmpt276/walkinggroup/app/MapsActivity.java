package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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

import ca.cmpt276.walkinggroup.dataobjects.CurrentUserData;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.MyItem;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.MainActivity.groupsList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private CurrentUserData userSingleton = CurrentUserData.getSingletonInstance();
    private WGServerProxy proxy = userSingleton.getCurrentProxy();

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    public double latitude;
    public double longitude;

    private ClusterManager<MyItem> mClusterManager;

    private List<Group> groupsOnServer = new ArrayList<>();
    public static Group groupSelected;



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
                LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null){
                    // default location in emulator
                    longitude = -122.084;
                    latitude = 37.422;
                }

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

                LatLng currentLatLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

                return currentLatLng;
            }

        }catch (SecurityException e){
            Log.e(TAG, getString(R.string.getDeviceLocation_exception) + e.getMessage() );

        }

        return null;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Toast.makeText(MapsActivity.this, "Current lat: " + latitude + " , long: " + longitude, Toast.LENGTH_LONG).show();

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


    private void getRemoteGroupById(Long id){
        Call<Group> caller = proxy.getGroupById(id);
        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedGroup -> returnedGroupById(returnedGroup));
    }

    private void returnedGroupById(Group group){
        groupSelected = group;
        Intent intent = new Intent(MapsActivity.this, Join_Group.class);
        startActivity(intent);

    }

}
