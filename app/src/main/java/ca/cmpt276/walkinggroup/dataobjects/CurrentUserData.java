package ca.cmpt276.walkinggroup.dataobjects;

// Singleton class used to pass proxy and current user data between activities

import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class CurrentUserData {
    private static CurrentUserData singletonInstance;
    private WGServerProxy currentProxy;
    private User currentUser;
    private boolean uploadingLocation = false;
    private String token;
    private Long ID;

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

}

