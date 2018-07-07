package ca.cmpt276.walkinggroup.dataobjects;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GroupTest2 {

    private Group TestGroup = new Group();


    @Test
    public void getGroupSingletonInstance() {
        Group singleton = Group.getGroupSingletonInstance();
        singleton.setGroupDescription("singleton");
        Group group2 = new Group();
        group2.setGroupDescription("another group");  // test if another regular group object can be instantiated
        assertEquals("singleton", singleton.getGroupDescription());
        assertEquals("another group", group2.getGroupDescription());
    }

    @Test
    public void setGroupId() {
        TestGroup.setGroupId(1);
        assertEquals(1, TestGroup.getGroupId());
    }

    @Test
    public void setGroupDescription() {
        TestGroup.setGroupDescription("Test group 1");
        assertEquals("Test group 1", TestGroup.getGroupDescription());
    }

    @Test
    public void setStartLat() {
        TestGroup.setStartLat(50.13);
        assertEquals(50.13, TestGroup.getStartLat(),0.0001);
    }

    @Test
    public void setStartLng() {
        TestGroup.setStartLng(-30.12);
        assertEquals(-30.12, TestGroup.getStartLng(),0.0001);
    }

    @Test
    public void setLeader() {
        User leader = new User();
        leader.setName("group leader");
        TestGroup.setLeader(leader);
        User returnedLeader = TestGroup.getLeader();
        assertEquals("group leader", returnedLeader.getName());
    }

    @Test
    public void setGroupMembers() {
        List<User> testUsers = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            User user = new User();
            user.setName("User " + i);
            testUsers.add(user);
        }

        TestGroup.setMemberUsers(testUsers);
        List<User> returnedUsers = TestGroup.getMemberUsers();

        for (int i = 0; i < 2; i++){
            User user = returnedUsers.get(i);
            assertEquals("User " + i, user.getName());
        }
    }

}