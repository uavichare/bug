package com.example.buglibrary.utils;

public class GeoCentricCordinates {

    private double x;
    private double y;
    private double z;

    public GeoCentricCordinates(double lat, double lng) {
        double theta = (lng * Math.PI / 180.0);
        double rlat = bdccGeoGeocentricLatitude(lat * Math.PI / 180.0);
        double c = Math.cos(rlat);
        this.x = c * Math.cos(theta);
        this.y = c * Math.sin(theta);
        this.z = Math.sin(rlat);
    }

    private double bdccGeoGeocentricLatitude(double geographiclat) {
        double flattening = 1.0 / 298.257223563;//WGS84
        double f = (1.0 - flattening) * (1.0 - flattening);
        return Math.atan((Math.tan(geographiclat) * f));
    }

    private double bdccGeoGeographicLatitude(double geocentriclat) {
        double flattening = 1.0 / 298.257223563;//WGS84
        double f = (1.0 - flattening) * (1.0 - flattening);
        return Math.atan(Math.tan(geocentriclat) / f);
    }

    private double getLatitudeRadians() {
        return this.bdccGeoGeographicLatitude(Math.atan2(this.z,
                Math.sqrt((this.x * this.x) + (this.y * this.y))));
    }

    private double getLongitudeRadians() {
        return (Math.atan2(this.y, this.x));
    }

    private double getLatitude() {
        return this.getLatitudeRadians() * 180.0 / Math.PI;
    }

    private double getLongitude() {
        return this.getLongitudeRadians() * 180.0 / Math.PI;
    }

    private double dot(GeoCentricCordinates b) {
        return ((this.x * b.x) + (this.y * b.y) + (this.z * b.z));
    }

    private double crossLength(GeoCentricCordinates b) {
        double x = (this.y * b.z) - (this.z * b.y);
        double y = (this.z * b.x) - (this.x * b.z);
        double z = (this.x * b.y) - (this.y * b.x);
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    private GeoCentricCordinates scale(double s) {
        GeoCentricCordinates r = new GeoCentricCordinates(0, 0);
        r.x = this.x * s;
        r.y = this.y * s;
        r.z = this.z * s;
        return r;
    }

    public GeoCentricCordinates crossNormalize(GeoCentricCordinates b) {
        double x = (this.y * b.z) - (this.z * b.y);
        double y = (this.z * b.x) - (this.x * b.z);
        double z = (this.x * b.y) - (this.y * b.x);
        double L = Math.sqrt((x * x) + (y * y) + (z * z));
        GeoCentricCordinates r = new GeoCentricCordinates(0, 0);
        r.x = x / L;
        r.y = y / L;
        r.z = z / L;
        return r;
    }

    private GeoCentricCordinates antipode() {
        return this.scale(-1.0);
    }

    private double distance(GeoCentricCordinates v2) {
        return Math.atan2(v2.crossLength(this), v2.dot(this));
    }

    public double distanceToLineSegMtrs(GeoCentricCordinates geo1, GeoCentricCordinates geo2) {

        //point on unit sphere above origin and normal to plane of geo1,geo2
        //could be either side of the plane
        GeoCentricCordinates p2 = geo1.crossNormalize(geo2);

        // intersection of GC normal to geo1/geo2 passing through p with GC geo1/geo2
        GeoCentricCordinates ip = GeoCentricUtil.bdccGeoGetIntersection(geo1, geo2, this, p2);

        //need to check that ip or its antipode is between p1 and p2
        double d = geo1.distance(geo2);
        double d1p = geo1.distance(ip);
        double d2p = geo2.distance(ip);
        //window.status = d + ", " + d1p + ", " + d2p;
        if ((d >= d1p) && (d >= d2p))
            return GeoCentricUtil.bdccGeoRadiansToMeters(this.distance(ip));
        else {
            ip = ip.antipode();
            d1p = geo1.distance(ip);
            d2p = geo2.distance(ip);
        }
        if ((d >= d1p) && (d >= d2p))
            return GeoCentricUtil.bdccGeoRadiansToMeters(this.distance(ip));
        else
            return GeoCentricUtil.bdccGeoRadiansToMeters(Math.min(geo1.distance(this), geo2.distance(this)));
    }


}
