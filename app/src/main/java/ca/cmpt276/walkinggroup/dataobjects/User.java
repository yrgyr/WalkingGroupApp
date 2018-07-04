package ca.cmpt276.walkinggroup.dataobjects;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User class to store the data the server expects and returns.
 * (Incomplete: Needs support for monitoring and groups).
 */

// All model classes coming from server must have this next line.
// It ensures deserialization does not fail if server sends you some fields you are not expecting.
// This is needed for the server to be able to change without breaking your app!
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends IdItemBase{
    // NOTE: id, hasFullData, and href in IdItemBase base class.

    // Data fields for the user.
    // -------------------------------------------------------------------------------------------
    // NOTE: Make numbers Long/Integer, not long/int because only the former will
    //       deserialize if the value is null from the server.
    private String name;
    private String email;
    private String password;

    private Integer currentPoints;
    private Integer totalPointsEarned;
    private EarnedRewards rewards;

    private List<User> monitoredByUsers = new ArrayList<>();// <-- TO BE IMPLEMENTED
    private List<User> monitorsUsers = new ArrayList<>();   // <-- TO BE IMPLEMENTED

    private List<Group> memberOfGroups = new ArrayList<>(); // <-- TO BE IMPLEMENTED
    private List<Group> leadsGroups = new ArrayList<>();    // <-- TO BE IMPLEMENTED

    // Basic User Data
    // -------------------------------------------------------------------------------------------
    public String getName() {

        return name;
    }
    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {

        return email;
    }
    public void setEmail(String email) {

        this.email = email;
    }

    // Note: Password never returned by the server; only used to send password to server.
    public String getPassword() {

        return password;
    }
    public void setPassword(String password) {

        this.password = password;
    }

    // Monitoring
    // -------------------------------------------------------------------------------------------
    public List<User> getMonitoredByUsers() {

        return monitoredByUsers;
    }
    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {

        return monitorsUsers;
    }
    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }

    // Groups
    // -------------------------------------------------------------------------------------------
    public List<Group> getMemberOfGroups()
    {
        return memberOfGroups;
    }
    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public List<Group> getLeadsGroups() {

        return leadsGroups;
    }
    public void setLeadsGroups(List<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }


    // Rewards (custom JSON data)
    // -------------------------------------------------------------------------------------------
    public Integer getCurrentPoints() {

        return currentPoints;
    }
    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getTotalPointsEarned()
    {
        return totalPointsEarned;
    }
    public void setTotalPointsEarned(Integer totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }


    // Setter will be called when deserializing User's JSON object; we'll automatically
    // expand it into the custom object.
    public void setCustomJson(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            rewards = null;
            Log.w("USER", "De-serializing string is null for User's custom Json rewards; ignoring.");
        } else {
            Log.w("USER", "De-serializing string: " + jsonString);
            try {
                rewards = new ObjectMapper().readValue(
                        jsonString,
                        EarnedRewards.class);
                Log.w("USER", "De-serialized embedded rewards object: " + rewards);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Having a getter will make this function be called to set the value of the
    // customJson field of the JSON data being sent to server.
    public String getCustomJson() {
        // Convert custom object to a JSON string:
        String customAsJson = null;
        try {
            customAsJson = new ObjectMapper().writeValueAsString(rewards);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return customAsJson;
    }

    public EarnedRewards getRewards() {
        return rewards;
    }
    public void setRewards(EarnedRewards rewards) {
        this.rewards = rewards;
    }

    // Utility Functions
    // -------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", currentPoints=" + currentPoints +
                ", totalPointsEarned=" + totalPointsEarned +
                ", monitoredByUsers=" + monitoredByUsers +
                ", monitorsUsers=" + monitorsUsers +
                ", memberOfGroups=" + memberOfGroups +
                ", leadsGroups=" + leadsGroups +
                ", hasFullData=" + hasFullData()+
                ", href='" + getHref() + '\'' +
                '}';
    }
}
