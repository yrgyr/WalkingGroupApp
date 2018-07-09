package ca.cmpt276.walkinggroup.dataobjects;

// Used for maps related functions

public class MapsFunctions {

    // code obtained from https://stackoverflow.com/questions/365826/calculate-distance-between-2-gps-coordinates
    private static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    // code obtained from https://stackoverflow.com/questions/365826/calculate-distance-between-2-gps-coordinates
    public static double distanceInMBetweenTwoCoordinates(double lat1, double lng1, double lat2, double lng2) {
        double earthRadiusKm = 6371;

        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lng2 - lng1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInKm = earthRadiusKm * c;
        return distanceInKm * 1000;

    }

}
