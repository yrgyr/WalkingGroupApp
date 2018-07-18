package ca.cmpt276.walkinggroup.dataobjects;


import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Simple User class to store the data the server expects and returns.
 * (Incomplete: Needs support for monitoring and groups).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends IdItemBase {
    // ---------------------------------------
    //    Fields
    // ---------------------------------------
    private String name;
    private String email;
    private Integer birthYear;
    private Integer birthMonth;
    private String address;
    private String cellPhone;
    private String homePhone;
    private String grade;
    private String teacherName;
    private String emergencyContactInfo;
    private String password;

    // Monitoring
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<User> monitoredByUsers;
    private List<User> monitorsUsers;

    // Group Membership / Leading
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<Group> memberOfGroups;
    private List<Group> leadsGroups;

    // GPS Location
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private GpsLocation lastGpsLocation;

    // Messages
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<Message> messages;

    // Gamification Support
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private Integer currentPoints;
    private Integer totalPointsEarned;
    // rewards will be serialized to be the customJson
    private EarnedRewards rewards;


    // Permissions
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<PermissionRequest> pendingPermissionRequests;


    // ----------------------------------------------
    // Setters / Getters
    // ----------------------------------------------
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

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getEmergencyContactInfo() {
        return emergencyContactInfo;
    }

    public void setEmergencyContactInfo(String emergencyContactInfo) {
        this.emergencyContactInfo = emergencyContactInfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public List<Group> getMemberOfGroups() {
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

    public GpsLocation getLastGpsLocation() {
        return lastGpsLocation;
    }

    public void setLastGpsLocation(GpsLocation lastGpsLocation) {
        this.lastGpsLocation = lastGpsLocation;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getTotalPointsEarned() {
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



    public List<PermissionRequest> getPendingPermissionRequests() {
        return pendingPermissionRequests;
    }

    public void setPendingPermissionRequests(List<PermissionRequest> pendingPermissionRequests) {
        this.pendingPermissionRequests = pendingPermissionRequests;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthYear=" + birthYear +
                ", birthMonth=" + birthMonth +
                ", address='" + address + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", grade='" + grade + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", emergencyContactInfo='" + emergencyContactInfo + '\'' +
                ", password='" + password + '\'' +
                ", monitoredByUsers=" + monitoredByUsers +
                ", monitorsUsers=" + monitorsUsers +
                ", memberOfGroups=" + memberOfGroups +
                ", leadsGroups=" + leadsGroups +
                ", lastGpsLocation=" + lastGpsLocation +
                ", messages=" + messages +
                ", currentPoints=" + currentPoints +
                ", totalPointsEarned=" + totalPointsEarned +
                ", rewards=" + rewards +
                ", pendingPermissionRequests=" + pendingPermissionRequests +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}
