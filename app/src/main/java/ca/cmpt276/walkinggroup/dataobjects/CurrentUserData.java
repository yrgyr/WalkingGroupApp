package ca.cmpt276.walkinggroup.dataobjects;

// Singleton class used to pass proxy and current user data between activities

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.util.Log;

import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class CurrentUserData {
    private static CurrentUserData singletonInstance;
    private WGServerProxy currentProxy;
    private User currentUser;
    private boolean uploadingLocation = false;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private Group groupSelected;
    private Group walkingGroup;  // the group the user is currently walking with
    private boolean destReachedCountDownRunning = false;
    private CountDownTimer destReachedCountDown;
    private String token;
    private Long ID;
    private int backgroundInUse = -1;

    private CurrentUserData() {}

    public static CurrentUserData getSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new CurrentUserData();
        }
        return singletonInstance;
    }

    public WGServerProxy getCurrentProxy() {

        return currentProxy;
    }

    public void setCurrentProxy(WGServerProxy currentProxy) {
        this.currentProxy = currentProxy;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setUploadingLocation(boolean bool) {this.uploadingLocation = bool;}

    public boolean getUploadingLocation() {return uploadingLocation;}

    public void setLocationManager(LocationManager lm) {this.locationManager = lm;}

    public LocationManager getLocationManager(){return locationManager;}

    public void setLocationListener(LocationListener listener) {this.locationListener = listener;}

    public LocationListener getLocationListener(){return locationListener;}

    public void setGroupSelected(Group group) {this.groupSelected = group;}

    public Group getGroupSelected(){return groupSelected;}

    public void setWalkingGroup(Group group){this.walkingGroup = group;}

    public Group getWalkingGroup(){return walkingGroup;}

    public boolean isDestReachedCountDownRunning() {
        return destReachedCountDownRunning;
    }

    public CountDownTimer getDestReachedCountDown() {
        return destReachedCountDown;
    }

    public void setDestReachedCountDownRunning(boolean isTimerRunning) {
        this.destReachedCountDownRunning = isTimerRunning;
    }

    public void setDestReachedCountDown(CountDownTimer timer) {
        this.destReachedCountDown = timer;
    }

    public void setBackgroundInUse(int resID) {
        this.backgroundInUse = resID;
    }

    public int getBackgroundInUse() {
        return backgroundInUse;
    }
}
