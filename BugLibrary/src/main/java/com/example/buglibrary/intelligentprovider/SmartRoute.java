package com.example.buglibrary.intelligentprovider;

import android.content.Context;
import android.widget.Toast;

import com.example.buglibrary.R;
import com.example.buglibrary.utils.GeoCentricCordinates;
import com.example.buglibrary.utils.GeoCentricUtil;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class SmartRoute implements Observer {

    private boolean listenForLocationUpdates = true;
    private Context mContext;
    private LatLng currentLatLng;
    private boolean getProjection;
    private List<LatLng> routeLatLongs;
    private Jarvis.RoutingProvider mRerouteProvider;
    private NavigationManager navigationManager;
    private int floor;


    public SmartRoute(Context context, Jarvis.RoutingProvider mRerouteProvider) {
        this.mRerouteProvider = mRerouteProvider;
        this.mContext = context;
        navigationManager = new NavigationManager(context);
    }


    @Override
    public void update(Observable observable, Object o) {
//        Toast.makeText(mContext, "Ia UPDATED", Toast.LENGTH_LONG).show();
        if (!listenForLocationUpdates) {
            currentLatLng = null;
            return;
        }
        try {
            JSONObject json = new JSONObject(o.toString());
            String type = json.getString("type");

            if (!type.equals("locationupdate")) {
                return;
            }
            double lat = json.getDouble("lat");
            double lng = json.getDouble("lng");
            floor = json.getInt("floor");
            currentLatLng = new LatLng(lat, lng);

            if (getProjection && routeLatLongs != null) {
                calculateDistanceAndProjection(currentLatLng);
            }

        } catch (JSONException e) {
            currentLatLng = null;
            e.printStackTrace();
        }
    }


    void setDestination(LatLng latlng, int destFloor) {
        if (currentLatLng == null) {
            Toast.makeText(mContext, "Could not initialize routing provider.Please ensure the wayfinding is supplied and routing provider is initialized", Toast.LENGTH_LONG).show();
            return;
        }
        JSONArray path = navigationManager.getShortestPath(
                currentLatLng,
                latlng,
                String.valueOf(floor),
                String.valueOf(destFloor));

        ArrayList pathListLatLng = new ArrayList<LatLng>();
        for (int i = 0; i < path.length(); i++) {
            try {
                JSONObject item = path.getJSONObject(i);
                LatLng latLngLeg = new LatLng(item.getDouble("lat"), item.getDouble("lng"));
                pathListLatLng.add(latLngLeg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Collections.reverse(pathListLatLng);
        setRoute(pathListLatLng);
        mRerouteProvider.route(path);
    }

    private void setRoute(List<LatLng> pathLatLongs) {
        routeLatLongs = pathLatLongs;
    }

    void getDistanceAndProjectionOnUpdate() {
        if (currentLatLng == null) {
            Toast.makeText(mContext, "Could not get current location.Please ensure location provider is enabled and listening for location updates", Toast.LENGTH_LONG).show();
            return;
        }
        if (routeLatLongs == null) {
            Toast.makeText(mContext, "No route to get projection.Make sure user is in navigation mode", Toast.LENGTH_LONG).show();
            return;
        }

        getProjection = true;
        calculateTurnByTurn();

    }

    private void calculateTurnByTurn() {
        JSONObject navDetails = new JSONObject();
        if (routeLatLongs == null) {
            Toast.makeText(mContext, "No route to turn by turn.Make sure user is in navigation mode", Toast.LENGTH_LONG).show();
            return;
        }

        //navDetails.add(directionText);
        int previous_index = 0;
        for (int i = 1; i < routeLatLongs.size() - 1; i++) {

            LatLng previous = routeLatLongs.get(previous_index);
            LatLng current = routeLatLongs.get(i);
            LatLng next = routeLatLongs.get(i + 1);

            double angle_between = GeoCentricUtil.angleBetweenLines(previous, current, next);
            if ((170 < angle_between && angle_between < 180) || (180 < angle_between && angle_between < 190)) {
                continue;

            } else {
                double previous_dist = GeoCentricUtil.distanceBetweenLatLngs(previous.getLatitude(), previous.getLongitude(), current.getLatitude(), current.getLongitude());
                try {
                    String text = mContext.getString(R.string.continue_for) + Math.round(previous_dist) + mContext.getString(R.string.meter);
                    JSONArray ifPresent = navDetails.getJSONArray(String.valueOf(previous_index));

                    if (ifPresent == null) {
                        navDetails.put(String.valueOf(previous_index), new JSONArray().put(text));
                    } else {
                        navDetails.put(String.valueOf(previous_index), ifPresent.put(text));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                double turn = GeoCentricUtil.angleBetweenLines(new LatLng(previous.getLatitude(), previous.getLongitude()), new LatLng(current.getLatitude(), current.getLongitude()), new LatLng(next.getLatitude(), next.getLongitude()));
                String text;
                

                if (0 < turn && turn < 180) {
                    //right
                    if (i == (routeLatLongs.size() - 2)) {
                        text = "Your location is to the right ";
                    } else {
                        if (turn >= 120) {
                            text = mContext.getString(R.string.slight_right);
                        } else {
                            text = mContext.getString(R.string.turn_right);
                        }
                    }


                } else {
                    //left
                    if (i == (routeLatLongs.size() - 2)) {
                        text = "Your location is to the left";

                    } else {
                        if (turn <= 240) {
                            text = mContext.getString(R.string.slight_left);
                        } else {
                            text = mContext.getString(R.string.turn_left);

                        }
                    }
                }
                try {
                    navDetails.put(String.valueOf(i), new JSONArray().put(text));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                previous_index = i;
            }

        }
        if (this.mRerouteProvider != null) {
            this.mRerouteProvider.turnByTurnDetail(navDetails);
        }

    }

    private void calculateDistanceAndProjection(LatLng latlng) {
        if (routeLatLongs == null) {
            Toast.makeText(mContext, "No route to get projection.", Toast.LENGTH_LONG).show();
            return;
        }
        if (latlng == null) {
            Toast.makeText(mContext, "No location", Toast.LENGTH_LONG).show();
            return;
        }

        double d = 999999999;
        GeoCentricCordinates p = new GeoCentricCordinates(latlng.getLatitude(), latlng.getLongitude());
        int index = -1;
        for (int i = 0; i < (routeLatLongs.size() - 1); i++) {
            LatLng p1 = routeLatLongs.get(i);
            GeoCentricCordinates l1 = new GeoCentricCordinates(p1.getLatitude(), p1.getLongitude());
            LatLng p2 = routeLatLongs.get(i + 1);
            GeoCentricCordinates l2 = new GeoCentricCordinates(p2.getLatitude(), p2.getLongitude());
            double dp = p.distanceToLineSegMtrs(l1, l2);
            if (dp < d) {
                d = dp;
                index = i;
            }

        }

        if (this.mRerouteProvider != null) {
            this.mRerouteProvider.currIndex(index);
        }

        if (d >= 5) {
            //distance of current location from the route is greater than x meters.From trial and error set the x value
//            Toast.makeText(mContext, "Possible reroute trigger", Toast.LENGTH_LONG).show();
            if (this.mRerouteProvider != null) {
                calculateTurnByTurn();
                this.mRerouteProvider.onReRoute();
            }
        }


    }


    void closeIA() {
        routeLatLongs = null;
        getProjection = false;
//        mWayfinder.close();
//        compositeDisposable.dispose();
    }
}
