package com.example.buglibrary.utils;

import com.mapbox.mapboxsdk.geometry.LatLng;

import static java.lang.StrictMath.abs;


public class GeoCentricUtil {
    public static final double EARTH_RADIUS = 6378137.0;

    public static GeoCentricCordinates bdccGeoGetIntersection(GeoCentricCordinates geo1, GeoCentricCordinates geo2, GeoCentricCordinates geo3, GeoCentricCordinates geo4) {
        GeoCentricCordinates geoCross1 = geo1.crossNormalize(geo2);
        GeoCentricCordinates geoCross2 = geo3.crossNormalize(geo4);
        return geoCross1.crossNormalize(geoCross2);
    }

    //from Radians to Meters
    public static double bdccGeoRadiansToMeters(double rad) {
        return rad * EARTH_RADIUS; // WGS84 Equatorial Radius in Meters
    }

    //from Meters to Radians
    public static double bdccGeoMetersToRadians(double m) {
        return m / EARTH_RADIUS; // WGS84 Equatorial Radius in Meters
    }

    public static double distanceBetweenLatLngs(double lat1, double lon1, double lat2, double lon2) {
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS * c; // Distance in metres
        return d;
    }

    private static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    private static double rad2deg(double rad) {
        return rad * (180 / Math.PI);
    }

    public static double angleBetweenLines(LatLng latlongA, LatLng latlongB, LatLng latlongC) {
        double headingBA = calculateBearing(latlongB, latlongA);
        double headingBC = calculateBearing(latlongB, latlongC);

        return angleBetweenHeadings(headingBA, headingBC);
    }

    private static double angleBetweenHeadings(double headingBA, double headingBC) {
        double angle = ((headingBA - headingBC) + 360) % 360;


        return angle;


    }

    public static double calculateBearing(LatLng latlong1, LatLng latlong2) {
        double lat1 = Math.toRadians(latlong1.getLatitude());
        double lng1 = Math.toRadians(latlong1.getLongitude());

        double lat2 = Math.toRadians(latlong2.getLatitude());
        double lng2 = Math.toRadians(latlong2.getLongitude());

        double dLong = lng2 - lng1;
        double dPhi = Math.log(Math.tan(lat2 / 2.0 + Math.PI / 4.0) / Math.tan(lat1 / 2.0 + Math.PI / 4.0));
        if (abs(dLong) > Math.PI) {
            if (dLong > 0.0) {
                dLong = -(2.0 * Math.PI - dLong);
            } else {
                dLong = (2.0 * Math.PI + dLong);
            }
        }
        double degrees = Math.toDegrees(Math.atan2(dLong, dPhi));
//        double bearing = (degrees + 360.0) % 360.0;

        return degrees;
    }


}
