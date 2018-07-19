package ca.cmpt276.walkinggroup.dataobjects;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapsFunctionsTest {

    @Test
    public void distanceInMBetweenTwoCoordinates() {
        double lat1 = -122.084;
        double lat2 = -122;
        double lng1 = 37.422;
        double lng2 = 37.4;

        double dist = MapsFunctions.distanceInMBetweenTwoCoordinates(lat1, lng1, lat2, lng2);
        assertEquals(9430, dist, 1);
    }

    @Test
    public void distanceInMBetweenTwoIdenticalCoordinates(){
        double lat = -122.084;
        double lng = 37.422;

        double dist = MapsFunctions.distanceInMBetweenTwoCoordinates(lat, lng, lat, lng);
        assertEquals(0, dist, 1);
    }
}