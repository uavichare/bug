package com.example.buglibrary.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buglibrary.R;
import com.example.buglibrary.utils.CommonUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class SymbolLayerMapillaryActivity extends AppCompatActivity implements OnMapReadyCallback,
        MapboxMap.OnMapClickListener {
    private static final String SOURCE_ID = "mapbox.poi";
    private static final String MAKI_LAYER_ID = "mapbox.poi.maki";
    private static final String CALLOUT_LAYER_ID = "mapbox.poi.callout";

    private static final String PROPERTY_SELECTED = "selected";

    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_FAVOURITE = "favourite";


    private MapView mapView;
    private MapboxMap mapboxMap;

    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    private HashMap<String, View> viewMap;


    @ActivityStep
    private int currentStep;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STEP_INITIAL, STEP_LOADING, STEP_READY})
    public @interface ActivityStep {
    }

    private static final int STEP_INITIAL = 0;
    private static final int STEP_LOADING = 1;
    private static final int STEP_READY = 2;

    private static final Map<Integer, Double> stepZoomMap = new HashMap<>();

    static {
        stepZoomMap.put(STEP_INITIAL, 11.0);
        stepZoomMap.put(STEP_LOADING, 13.5);
        stepZoomMap.put(STEP_READY, 18.0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.fragment_home);


        // Initialize the map view
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(getString(R.string.mapbox_style_url), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.getUiSettings().setCompassEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                new LoadPoiDataTask(SymbolLayerMapillaryActivity.this).execute();
                LatLng initalPoint = new LatLng(37.801103690609615, -122.42443084716797);
                CameraPosition position = new CameraPosition.Builder().target(initalPoint).
                        zoom(15).build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                mapboxMap.addOnMapClickListener(SymbolLayerMapillaryActivity.this);
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, CALLOUT_LAYER_ID);
        if (!features.isEmpty()) {
            // we received a click event on the callout layer
            Feature feature = features.get(0);
            PointF symbolScreenPoint = mapboxMap.getProjection().toScreenLocation(convertToLatLng(feature));
            handleClickCallout(feature, screenPoint, symbolScreenPoint);
        } else {
            // we didn't find a click event on callout layer, try clicking maki layer
            return handleClickIcon(screenPoint);
        }
        return true;
    }

    public void setupData(final FeatureCollection collection) {
        if (mapboxMap == null) {
            return;
        }
        featureCollection = collection;
        mapboxMap.getStyle(style -> {
            setupSource(style);
            setupMakiLayer(style);
            setupCalloutLayer(style);
        });
    }

    private void setupSource(@NonNull Style loadedMapStyle) {
        source = new GeoJsonSource(SOURCE_ID, featureCollection);
        loadedMapStyle.addSource(source);
    }

    private void refreshSource() {
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Setup a layer with maki icons, eg. restaurant.
     */
    private void setupMakiLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer(MAKI_LAYER_ID, SOURCE_ID)
                .withProperties(
                        /* show maki icon based on the value of poi feature property
                         * https://www.mapbox.com/maki-icons/
                         */
                        iconImage("{poi}-15"),

                        /* allows show all icons */
                        iconAllowOverlap(true),

                        /* when feature is in selected state, grow icon */
                        iconSize(match(Expression.toString(get(PROPERTY_SELECTED)), literal(1.0f),
                                stop("true", 1.5f))))
        );
    }


    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * title of the feature is used as key for the iconImage
     * </p>
     */
    private void setupCalloutLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, SOURCE_ID)
                .withProperties(
                        /* show image with id title based on the value of the title feature property */
                        iconImage("{title}"),

                        /* set anchor of icon to bottom-left */
                        iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT),

                        /* offset icon slightly to match bubble layout */
                        iconOffset(new Float[]{-20.0f, -10.0f})
                )

                /* add a filter to show only when selected feature property is true */
                .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
    }





    /**
     * This method handles click events for callout symbols.
     * <p>
     * It creates a hit rectangle based on the the textView, offsets that rectangle to the location
     * of the symbol on screen and hit tests that with the screen point.
     * </p>
     *
     * @param feature           the feature that was clicked
     * @param screenPoint       the point on screen clicked
     * @param symbolScreenPoint the point of the symbol on screen
     */
    private void handleClickCallout(Feature feature, PointF screenPoint, PointF symbolScreenPoint) {
        View view = viewMap.get(feature.getStringProperty(PROPERTY_TITLE));
        View textContainer = view.findViewById(R.id.button_navigate);

        // create hitbox for textView
        Rect hitRectText = new Rect();
        textContainer.getHitRect(hitRectText);

        // move hitbox to location of symbol
        hitRectText.offset((int) symbolScreenPoint.x, (int) symbolScreenPoint.y);

        // offset vertically to match anchor behaviour
        hitRectText.offset(0, -view.getMeasuredHeight());

        // hit test if clicked point is in textview hitbox
        if (hitRectText.contains((int) screenPoint.x, (int) screenPoint.y)) {
            // user clicked on text
            String callout = feature.getStringProperty("call-out");
            Toast.makeText(this, callout, Toast.LENGTH_LONG).show();
        } else {
            // user clicked on icon
            List<Feature> featureList = featureCollection.features();
            for (int i = 0; i < featureList.size(); i++) {
                if (featureList.get(i).getStringProperty(PROPERTY_TITLE).equals(feature.getStringProperty(PROPERTY_TITLE))) {
//                    toggleFavourite(i);
                }
            }
        }
    }

    /**
     * This method handles click events for maki symbols.
     * <p>
     * When a maki symbol is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MAKI_LAYER_ID);
        if (!features.isEmpty()) {
            String title = features.get(0).getStringProperty(PROPERTY_TITLE);
            List<Feature> featureList = featureCollection.features();
            for (int i = 0; i < featureList.size(); i++) {
                if (featureList.get(i).getStringProperty(PROPERTY_TITLE).equals(title)) {
                    setSelected(i, true);
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Set a feature selected state with the ability to scroll the RecycleViewer to the provided index.
     *
     * @param index      the index of selected feature
     * @param withScroll indicates if the recyclerView position should be updated
     */
    private void setSelected(int index, boolean withScroll) {

        deselectAll(false);

        Feature feature = featureCollection.features().get(index);
        selectFeature(feature);
        refreshSource();

    }

    /**
     * Deselects the state of all the features
     */
    private void deselectAll(boolean hideRecycler) {
        for (Feature feature : featureCollection.features()) {
            feature.properties().addProperty(PROPERTY_SELECTED, false);
        }

    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void selectFeature(Feature feature) {
        feature.properties().addProperty(PROPERTY_SELECTED, true);
    }

    private Feature getSelectedFeature() {
        if (featureCollection != null) {
            for (Feature feature : featureCollection.features()) {
                if (feature.getBooleanProperty(PROPERTY_SELECTED)) {
                    return feature;
                }
            }
        }

        return null;
    }


    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, View> viewMap, HashMap<String, Bitmap> imageMap) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            }
        });
        // need to store reference to views to be able to use them as hitboxes for click events.
        SymbolLayerMapillaryActivity.this.viewMap = viewMap;
    }

    private void setActivityStep(@ActivityStep int activityStep) {
        Feature selectedFeature = getSelectedFeature();
        double zoom = stepZoomMap.get(activityStep);

        currentStep = activityStep;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (currentStep == STEP_LOADING || currentStep == STEP_READY) {
            setActivityStep(STEP_INITIAL);
            deselectAll(true);
            refreshSource();
        } else {
            super.onBackPressed();
        }
    }

    private LatLng convertToLatLng(Feature feature) {
        Point symbolPoint = (Point) feature.geometry();
        return new LatLng(symbolPoint.latitude(), symbolPoint.longitude());
    }

    /**
     * AsyncTask to load data from the assets folder.
     */
    private static class LoadPoiDataTask extends AsyncTask<Void, Void, FeatureCollection> {

        private final WeakReference<SymbolLayerMapillaryActivity> activityRef;

        LoadPoiDataTask(SymbolLayerMapillaryActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected FeatureCollection doInBackground(Void... params) {
            SymbolLayerMapillaryActivity activity = activityRef.get();

            if (activity == null) {
                return null;
            }

            String geoJson = loadGeoJsonFromAsset(activity, "sf_poi.geojson");
            return FeatureCollection.fromJson(geoJson);
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection) {
            super.onPostExecute(featureCollection);
            SymbolLayerMapillaryActivity activity = activityRef.get();
            if (featureCollection == null || activity == null) {
                return;
            }
            activity.setupData(featureCollection);
            new GenerateViewIconTask(activity).execute(featureCollection);
        }

        static String loadGeoJsonFromAsset(Context context, String filename) {
            try {
                // Load GeoJSON file from local asset folder
                InputStream is = context.getAssets().open(filename);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                return new String(buffer, Charset.forName("UTF-8"));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>> {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<SymbolLayerMapillaryActivity> activityRef;


        GenerateViewIconTask(SymbolLayerMapillaryActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params) {
            SymbolLayerMapillaryActivity activity = activityRef.get();
            if (activity != null) {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity);
                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features()) {
                    View view = inflater.inflate(R.layout.layout_poi_info_window, null);

                    String name = feature.getStringProperty(PROPERTY_TITLE);
                    Bitmap bitmap = CommonUtils.Companion.generate(view);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, view);
                }

                return imagesMap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap) {
            super.onPostExecute(bitmapHashMap);
            SymbolLayerMapillaryActivity activity = activityRef.get();
            if (activity != null && bitmapHashMap != null) {

                activity.setImageGenResults(viewMap, bitmapHashMap);

            }
        }
    }

}