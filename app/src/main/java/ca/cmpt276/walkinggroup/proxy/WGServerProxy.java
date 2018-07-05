package ca.cmpt276.walkinggroup.proxy;

import android.webkit.PermissionRequest;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * The ProxyBuilder class will handle the apiKey and token being injected as a header to all calls
 * This is a Retrofit interface.
 */
public interface WGServerProxy {
    @GET("getApiKey")
    Call<String> getApiKey(@Query("groupName") String groupName);

    // -----------------------------
    // Users
    // -----------------------------
    @POST("/users/signup")
    Call<User> createUser(@Body User user);

    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    @GET("/users")
    Call<List<User>> getUsers();

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    // -----------------------------
    // GPS Location
    // -----------------------------
    @GET("/users/{id}/lastGpsLocation")
    Call<GpsLocation> getLastGpsLocation(@Path("id") Long userId);

    @POST("/users/{id}/lastGpsLocation")
    Call<GpsLocation> setLastGpsLocation(@Path("id") Long userId, @Body GpsLocation location);


    // -----------------------------
    // User Monitoring
    // -----------------------------
    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getMonitoredByUsers(@Path("id") Long userId);

    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> addToMonitoredByUsers(@Path("id") Long userId, @Body User user);

    @DELETE("/users/{id}/monitoredByUsers/{childId}")
    Call<Void> removeFromMonitoredByUsers(@Path("id") Long userId, @Path("childId") Long childId);


    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitorsUsers(@Path("id") Long userId);

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addToMonitorsUsers(@Path("id") Long userId, @Body User user);

    @DELETE("/users/{id}/monitorsUsers/{childId}")
    Call<Void> removeFromMonitorsUsers(@Path("id") Long userId, @Path("childId") Long childId);


    // -----------------------------
    // Groups
    // -----------------------------
    @GET("/groups")
    Call<List<Group>> getGroups();

    @GET("/groups/{id}")
    Call<Group> getGroupById(@Path("id") Long groupId);

    @POST("/groups")
    Call<Group> createGroup(@Body Group group);

    @POST("/groups/{id}")
    Call<Group> updateGroup(@Path("id") Long groupId, @Body Group group);

    @DELETE("/groups/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupId);

    @GET("/groups/{id}/memberUsers")
    Call<List<User>> getGroupMembers(@Path("id") Long groupId);

    @POST("/groups/{id}/memberUsers")
    Call<List<User>> addGroupMember(@Path("id") Long groupId, @Body User user);

    @DELETE("/groups/{id}/memberUsers/{userId}")
    Call<Void> removeGroupMember(@Path("id") Long groupId, @Path("userId") Long userId);


    // -----------------------------
    // Messages
    // -----------------------------
    // TODO: Implement

    // -----------------------------
    // Permissions
    // -----------------------------
    // TODO: Add query options
    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions();

    @GET("/permissions/{id}")
    Call<PermissionRequest> getPermissionById(@Path("id") long permissionId);

    @POST("/permissions/{id}")
    Call<PermissionRequest> approveOrDenyPermissionRequest(
            @Path("id") long permissionId,
            @Body PermissionStatus status
    );

    // -- Internal --
    @GET("/permissions/actions")
    Call<List<ActionInfo>> getPermissionActionInfo();
    //  -- Internal --
    @POST("/permissions/{id}/magicOverrideDoNotUse")
    Call<PermissionRequest> magicOverridePermissionById(
            @Path("id") long permissionId,
            @Body PermissionRequest request
    );


    // -----------------------------
    // Small data classes required by permissions
    // -----------------------------
    class ActionInfo {
        String value;
        String description;
    }

    enum PermissionStatus {
        PENDING,
        APPROVED,
        DENIED
    }


}
