package ca.cmpt276.walkinggroup.dataobjects;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private Long grpId;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        // added the 2 lines below in otherwise will cause compiler error
        mTitle = null;
        mSnippet = null;

    }

    public MyItem(double lat, double lng, String title, String snippet, Long grpId) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        this.grpId = grpId;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public Long getGrpId(){ return grpId;}
}