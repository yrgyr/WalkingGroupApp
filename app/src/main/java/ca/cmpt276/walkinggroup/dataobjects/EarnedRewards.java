package ca.cmpt276.walkinggroup.dataobjects;

import android.graphics.Color;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom class that your group can change the format of in (almost) any way you like
 * to encode the rewards that this user has earned.
 *
 * This class gets serialized/deserialized as part of a User object. Server stores it as
 * a JSON string, so it has no direct knowledge of what it contains.
 * (Rewards may not be used during first project iteration or two)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnedRewards {
    private String title = "Dragon slayer";
    private List<File> possibleBackgroundFiles = new ArrayList<>();
    private Integer selectedBackground = 1;
    private List<Integer> purchasedMessageLogos = new ArrayList<>();
    private Integer MessageLogoInUse = null;

    private Integer titleColor = Color.BLUE;

    // Needed for JSON deserialization
    public EarnedRewards() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<File> getPossibleBackgroundFiles() {
        return possibleBackgroundFiles;
    }

    public void setPossibleBackgroundFiles(List<File> possibleBackgroundFiles) {
        this.possibleBackgroundFiles = possibleBackgroundFiles;
    }

    public int getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(int selectedBackground) {
        this.selectedBackground = selectedBackground;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public List<Integer> getPurchasedMessageLogos() {
        return purchasedMessageLogos;
    }


    public Integer getMessageLogoInUse() {
        return MessageLogoInUse;
    }

    public void setPurchasedMessageLogos(List<Integer> purchasedMessageLogos) {
        this.purchasedMessageLogos = purchasedMessageLogos;
    }


    public void setMessageLogoInUse(Integer messageLogoInUse) {
        MessageLogoInUse = messageLogoInUse;
    }

    @Override
    public String toString() {
        return "EarnedRewards{" +
                "title='" + title + '\'' +
                ", possibleBackgroundFiles=" + possibleBackgroundFiles +
                ", selectedBackground=" + selectedBackground +
                ", titleColor=" + titleColor +
                '}';
    }

}