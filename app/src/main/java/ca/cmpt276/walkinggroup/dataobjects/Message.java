package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message extends IdItemBase {
    private Boolean read;
    private boolean isEmergency;
    private Date timestamp;
    private String text;

    private User fromUser;
    private User toUser;


    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Boolean isRead() {
        return read;
    }

    public void setIsRead(Boolean read) {
        this.read = read;
    }


    @Override
    public String toString() {
        return "Message{" +
                "read=" + read +
                ", isEmergency=" + isEmergency +
                ", timestamp=" + timestamp +
                ", text='" + text + '\'' +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}

