package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionRequest extends IdItemBase {
    private String action;
    private WGServerProxy.PermissionStatus status;
    private User userA;
    private User userB;
    private Group groupG;
    private User requestingUser;
    private Set<Authorizor> authorizors;
    private String message;



    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WGServerProxy.PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(WGServerProxy.PermissionStatus status) {
        this.status = status;
    }


    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    @JsonIgnore
    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }


    public Group getGroupG() {
        return groupG;
    }

    public void setGroupG(Group groupG) {
        this.groupG = groupG;
    }

    @JsonIgnore
    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Set<Authorizor> getAuthorizors() {
        return authorizors;
    }


    public void setAuthorizors(Set<Authorizor> authorizors) {
        this.authorizors = authorizors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Authorizor {
        private Long id;
        private Set<User> users;
        private WGServerProxy.PermissionStatus status;
        private User whoApprovedOrDenied;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }


        public Set<User> getUsers() {
            return users;
        }

        public void setUsers(Set<User> users) {
            this.users = users;
        }

        public WGServerProxy.PermissionStatus getStatus() {
            return status;
        }

        public void setStatus(WGServerProxy.PermissionStatus status) {
            this.status = status;
        }


        public User getWhoApprovedOrDenied() {
            return whoApprovedOrDenied;
        }

        public void setWhoApprovedOrDenied(User whoApprovedOrDenied) {
            this.whoApprovedOrDenied = whoApprovedOrDenied;
        }
    }


    @Override
    public String toString() {
        return "PermissionRequest{" +
                "action='" + action + '\'' +
                ", status=" + status +
                ", userA=" + userA +
                ", userB=" + userB +
                ", groupG=" + groupG +
                ", requestingUser=" + requestingUser +
                ", authorizors=" + authorizors +
                ", message='" + message + '\'' +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}