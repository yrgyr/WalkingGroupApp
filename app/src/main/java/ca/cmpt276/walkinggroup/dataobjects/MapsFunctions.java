package ca.cmpt276.walkinggroup.dataobjects;

// Used for maps related functions

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import ca.cmpt276.walkinggroup.app.CreateGroupMap;

public class MapsFunctions {

    public static LocationManager getDeviceLocation(GoogleMap mMap, boolean mLocationPermissionsGranted, float DEFAULT_ZOOM, Context context){
        LocationManager lm = null;
        try{
            if (mLocationPermissionsGranted){
                // Code obtained from StacksOverflow https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude;
                double latitude;
                if (location == null){
                    longitude = -122.91988329999998;
                    latitude = 49.2780937;
                } else {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }

                LatLng currentLatLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
            }

        }catch (SecurityException e){
            Log.e("Maps: ", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }

        return lm;
    }

    public static void addMarkerToMap(GoogleMap mMap, double lat, double lng, String infoWindowMsg, boolean showInfoWindow, float markerColour){
        LatLng latLng = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(infoWindowMsg).icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
        if (showInfoWindow) {
            marker.showInfoWindow();
        }
    }

    // code obtained from https://stackoverflow.com/questions/365826/calculate-distance-between-2-gps-coordinates
    private static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    // code obtained from https://stackoverflow.com/questions/365826/calculate-distance-between-2-gps-coordinates
    public static double distanceInMBetweenTwoCoordinates(double lat1, double lng1, double lat2, double lng2) {
        double earthRadiusKm = 6371;

        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lng2 - lng1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInKm = earthRadiusKm * c;
        return distanceInKm * 1000;

    }

    // From: https://stackoverflow.com/questions/8077530/android-get-current-timestamp
    public static String getCurrentTimeStamp(){
        String currentDateTime = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentDateTime = dateFormat.format(new Date()); // Find todays date

        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateTime;
    }

}
