package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<User> memberUsers = new ArrayList<>();

    public Group() { }

    // Singleton support
    @JsonIgnore
    public static Group getGroupSingletonInstance(){
        if (singletonInstance == null){
            singletonInstance = new Group();
        }
        return singletonInstance;
    }

    // Basic group data getters and setters

    public void setGroupId(int id){
        this.id = id;
    }
    public int getGroupId() {
        return id;
    }



    public void setGroupDescription(String groupDescription){
        this.groupDescription = groupDescription;
    }
    public String getGroupDescription() {
        return groupDescription; }
    @JsonIgnore
    public double getStartLat() {

        return routeLatArray.get(0);
    }

    @JsonIgnore
    public void setStartLat(double lat){

        this.routeLatArray.add(lat);
    }
    @JsonIgnore
    public double getStartLng() {

        return routeLngArray.get(0);
    }
    @JsonIgnore
    public void setStartLng(double lng){
        this.routeLngArray.add(lng);
    }

    @JsonIgnore
    public double getDestLat(){
        return routeLatArray.get(1);
    }

    @JsonIgnore
    public double getDestLng(){
        return routeLngArray.get(1);
    }

    public void addLatCoordinate(int index,double lat){

        if (routeLatArray.size() < 2){
            routeLatArray.add(lat);

        }
        else{
            routeLatArray.set(index,lat);
        }

    }

    public void addLngCoordinate(int index,double lng){

        if (routeLngArray.size() < 2){
            routeLngArray.add(lng);
        }
        else{
            routeLngArray.set(index,lng);
        }
    }

    public void setLeader(User leader) {

        this.leader = leader;
    }

    public User getLeader() {

        return leader;
    }
    //@JsonIgnore
    public List<User> getMemberUsers()
    {
        return memberUsers;
    }

    public void setMemberUsers(List<User> memberUsers)
    {
        this.memberUsers = memberUsers;
    }

    //@JsonIgnore
    public List<Double> getRouteLatArray() {

        return routeLatArray;
    }
    //@JsonIgnore
    public List<Double> getRouteLngArray() {

        return routeLngArray;
    }

    public void setRouteLatArray(List<Double> routeLatArray)
    {
        this.routeLatArray = routeLatArray;
    }

    public void setRouteLngArray(List<Double> routeLngArray) {

        this.routeLngArray = routeLngArray;
    }
    @JsonIgnore
    public int getGroupSize(){
        return memberUsers.size();}
    @JsonIgnore
    public String[] getGroupMembersNames(){

        String[] names;
        if (memberUsers.size() > 0) {
            names = new String[getGroupSize()];

            for (int i = 0; i < getGroupSize(); i++) {
                User member = memberUsers.get(i);
                names[i] = member.getName();
            }
        } else {
            names = new String[0];
        }
        return names;
    }

    @JsonIgnore
    public List<User> removeGroupMember(Long userId){
        for (int i = 0; i < memberUsers.size(); i++){
            if (userId == memberUsers.get(i).getId()){
                memberUsers.remove(i);
                break;
            }
        }

        return memberUsers;
    }

    @JsonIgnore
    public long[] getGroupMembersIds(){
        long[] Ids = new long[getGroupSize()];

        for (int i = 0; i < getGroupSize(); i++){
            User member = memberUsers.get(i);
            Ids[i] = member.getId();
        }

        return Ids;
    }
    @JsonIgnore
    public void setToGroup2Params(Group group2){
//        this.id = group2.getGroupId();
        this.groupDescription = group2.getGroupDescription();
        this.routeLatArray = group2.getRouteLatArray();
        this.routeLngArray = group2.getRouteLngArray();
        this.leader = group2.getLeader();
        this.memberUsers = group2.getMemberUsers();
    }


    @Override
    public String toString() {
        return "Group{" +
                "id=:" + getId() +
                ", groupDescription : '" + groupDescription+ '\'' +
                ", routeLatArray : '" + routeLatArray + '\'' +
                ", routeLngArray : '" + routeLngArray + '\'' +
                ", leader : {" +
                "id : " + leader +

                '}';
    }

}
