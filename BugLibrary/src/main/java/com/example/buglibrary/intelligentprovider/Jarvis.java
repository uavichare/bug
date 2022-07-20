package com.example.buglibrary.intelligentprovider;

import android.content.Context;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Jarvis extends Observable {
    private List<Observer> locationListenerObservers;
    public SmartRoute smartWayFinder;
    Context mContext;

    public interface RoutingProvider {
        void onReRoute();

        void currIndex(int index);

        void turnByTurnDetail(JSONObject navDetails);

        void route(JSONArray path);

    }

    public Jarvis() {
        locationListenerObservers = new ArrayList<>();
    }

    public void initSmartRouter(Context mContext, RoutingProvider provider) {
        smartWayFinder = new SmartRoute(mContext, provider);
        this.mContext = mContext;
        addLocationObservers(smartWayFinder);

    }

    public void invalidateSmartRouter() {
        if (smartWayFinder != null) {
            smartWayFinder.closeIA();
        }
    }


    private void addLocationObservers(Observer observer) {
        locationListenerObservers.add(observer);
    }

    public void onLocationUpdate(LatLng latlng, int floor) {
        for (Observer observer : locationListenerObservers) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "locationupdate");
                obj.put("lat", latlng.getLatitude());
                obj.put("lng", latlng.getLongitude());
                obj.put("floor", floor);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            observer.update(this, obj);
        }
    }

    public void setRouteDestination(LatLng latLng, int floor) {
        Log.d("TAG", "setRouteDestination: " + smartWayFinder);
        if (smartWayFinder == null) {
//            Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
            return;
        }
        smartWayFinder.setDestination(latLng, floor);
    }


    public void getDistanceAndProjectionOnUpdate() {
        try {
            smartWayFinder.getDistanceAndProjectionOnUpdate();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }
}