package ca.cmpt276.walkinggroup.proxy;

import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

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

    @POST("/users/{id}")
    Call<User> editUser(@Body User user, @Path("id") long id);




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
    Call<List<User>> getMonitoredByUsers(@Path("id") Long childId);

    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> addToMonitoredByUsers(@Path("id") Long childId, @Body User parent);

    @DELETE("/users/{id}/monitoredByUsers/{parentId}")
    Call<Void> removeFromMonitoredByUsers(@Path("id") Long childId, @Path("parentId") Long parentId);


    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitorsUsers(@Path("id") Long parentId);

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addToMonitorsUsers(@Path("id") Long parentId, @Body User child);

    @DELETE("/users/{id}/monitorsUsers/{childId}")
    Call<Void> removeFromMonitorsUsers(@Path("id") Long parentId, @Path("childId") Long childId);


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
    @POST("/messages/togroup/{groupId}")
    Call<List<Message>> newMessageToGroup(@Path("groupId") Long groupId, @Body Message message);

    @POST("/messages/toparentsof/{userId}")
    Call<List<Message>> newMessageToParentsOf(@Path("userId") Long userId, @Body Message message);

    @GET("/messages")
    Call<List<Message>> getMessages();
    @GET("/messages")
    Call<List<Message>> getMessages(@Query("touser") Long toUserId);
    @GET("/messages")
    Call<List<Message>> getMessages(@Query("touser") Long toUserId, @Query("is-emergency") Boolean isEmergency);
    @GET("/messages?status=unread")
    Call<List<Message>> getUnreadMessages(
            @Query("touser") Long toUserId,
            @Query("is-emergency") Boolean isEmergency);    // null for not filtering
    @GET("/messages?status=read")
    Call<List<Message>> getReadMessages(
            @Query("touser") Long toUserId,
            @Query("is-emergency") Boolean isEmergency);    // null for not filtering

    @GET("/messages/{id}")
    Call<Message> getOneMessage(@Path("id") Long id);

    // if markAsRead is false, then marks it as unread.
    @POST("/messages/{id}/mark-read-or-unread")
    Call<Message> markMessageAsRead(@Path("id") Long id, @Body Boolean markAsRead);

    @DELETE("/messages/{id}")
    Call<Void> deleteMessage(@Path("id") Long id);



    // -----------------------------
    // Permissions
    // -----------------------------
    // TODO: Add query options
    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions();

    @GET("/permissions/{id}")
    Call<PermissionRequest> getPermissionById(@Path("id") Long permissionId);

    @POST("/permissions/{id}")
    Call<PermissionRequest> approveOrDenyPermissionRequest(
            @Path("id") long permissionId,
            @Body PermissionStatus status
    );




    @GET("/permissions")
    Call<List<PermissionRequest>> getAllPermissionByUserId(@Query("userId") Long userId);

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissionByGroup(@Query("groupId") Long groupId);






//
//    // -- Internal --
//    @GET("/permissions/actions")
//    Call<List<ActionInfo>> getPermissionActionInfo();
//    //  -- Internal --
//    @POST("/permissions/{id}/magicOverrideDoNotUse")
//    Call<PermissionRequest> magicOverridePermissionById(
//            @Path("id") long permissionId,
//            @Body PermissionRequest request
//    );


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
