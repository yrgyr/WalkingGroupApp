package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Store information about the walking groups.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group extends IdItemBase{
    private static Group singletonInstance;
    private int id;
    private String groupDescription;
    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private User leader;
    private List<User> groupMembers;


    public Group() { }

    // Singleton support
    public static Group getGroupSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new Group();
        }
        return singletonInstance;
    }

    // Basic group data getters and setters

    public void setGroupId(int id){ this.id = id; }
    public int getGroupId() { return id; }

    public void setGroupDescription(String groupDescription){this.groupDescription = groupDescription;}
    public String getGroupDescription() { return groupDescription; }

    public double getStartLat() {
        return routeLatArray.get(0);
    }

    public void setStartLat(double lat){
        this.routeLatArray.add(lat);
    }

    public double getStartLng() {
        return routeLngArray.get(0);
    }

    public void setStartLng(double lng){
        this.routeLngArray.add(lng);
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public User getLeader() {
        return leader;
    }

    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }


}
