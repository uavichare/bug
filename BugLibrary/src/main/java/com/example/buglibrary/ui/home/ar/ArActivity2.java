// This file is originally based on the ARCore example code, which is licensed under Apache 2.0
// as specified below. To view the modifications by IndoorAtlas, see diff to the source:
// https://github.com/google-ar/arcore-android-sdk/blob/v1.18.1/samples/hello_ar_java/app/src/main/java/com/google/ar/core/examples/java/helloar/HelloArActivity.java
/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.buglibrary.ui.home.ar;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buglibrary.R;
import com.example.buglibrary.helper.BackgroundRenderer;
import com.example.buglibrary.helper.CameraPermissionHelper;
import com.example.buglibrary.helper.ObjectRenderer;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.indooratlas.android.sdk.IAARObject;
import com.indooratlas.android.sdk.IAARSession;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.IAWayfindingRequest;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IAVenue;

import com.example.buglibrary.utils.LocationUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ArActivity2 extends AppCompatActivity implements GLSurfaceView.Renderer, IALocationListener, IARegion.Listener {
    private static final String TAG = ArActivity2.class.getSimpleName() + "yy";

    private GLSurfaceView surfaceView;
    private boolean installRequested, viewportChanged;
    private int viewportWidth, viewportHeight;

    private Session session;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer destinationObject = new ObjectRenderer();
    private final ObjectRenderer arrowObject = new ObjectRenderer();

    // IndoorAtlas objects
    private IALocationManager iaLocationManager;
    private IAARSession iaArSession;
    private Snackbar snackbar;
    private IALocation iaLocation = null;

    // Temporary matrices allocated here to reduce number of allocations for each frame.
    private final float[]
            viewMtx = new float[16],
            projMtx = new float[16],
            invViewMtx = new float[16],
            sensorPoseMtx = new float[16],
            modelMtx = new float[16];

    static class ArMarkerWrapper {
        Anchor anchor;
        ObjectRenderer renderer;
        LatLng latLng;
        boolean glInitialized;
    }

    private List<ArMarkerWrapper> arObjects = new ArrayList<>();
    private ArrayList<LatLng> latlngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        surfaceView = findViewById(R.id.surfaceview);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        surfaceView.setWillNotDraw(false);

        installRequested = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // NOTE: even though this is the simples standalone method for starting IndoorAtlas positioning,
        // it is typically not ideal to create the location manager only for the AR Activity, since this
        // way, the positioning is restarted when the AR Activity is opened, which slows down first fix.
        iaLocationManager = IALocationManager.create(this);
        // Instead, it would be preferable to create the location manager elsewhere and share it with
        // this activity, for example: iaLocationManager = MyApplication.getIaLocationManager()
        // then the positioning is not interrupted by switches between 2D and AR navigation

        // ---------- start IndoorAtlas positioning and AR session
        iaArSession = iaLocationManager.requestArUpdates();

        iaLocationManager.requestLocationUpdates(IALocationRequest.create(), this);
        iaLocationManager.registerRegionListener(this);

        // start IndoorAtlas AR wayfinding, assuming the destination is sent to this Activity
        // via an Android Intent, which is typically the most convenient way to receive that info
        // in a new Activity
        IAWayfindingRequest wayfindingRequest = getIntent().getParcelableExtra("iaArWayfindingTarget");
        latlngs = getIntent().getParcelableArrayListExtra("lat_lngs");

        iaArSession.startWayfinding(wayfindingRequest);

        // ---------- ARCore resume & initialization
        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                // Create the session.
                session = new Session(/* context= */ this);
                Config config = session.getConfig();
                session.configure(config);
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            Log.e(TAG, "Camera not available. Try restarting the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // ---------- stop IndoorAtlas positioning and AR wayfinding
        iaArSession.stopWayfinding();
        iaArSession.destroy();
        iaLocationManager.unregisterRegionListener(this);
        iaLocationManager.removeLocationUpdates(this);
        // NOTE: if the location manager instance was shared (as recommended), then the following
        // line should be removed
        iaLocationManager.destroy();

        // ---------- ARCore teardown
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // This should be shown to the user as, e.g., a Toast message
            Log.e(TAG, "Camera permission is needed to run this application");
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            backgroundRenderer.createOnGlThread(this);

            destinationObject.createOnGlThread(this, "models/arrow.obj", "models/andy.png");
            arrowObject.createOnGlThread(this, "models/arrow_purple.obj", "models/finish.png");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewportChanged = true;
        viewportWidth = width;
        viewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // ---------- standard ARCore & OpenGL things
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        if (viewportChanged) {
            // Note: this is done with DisplayRotationHelper in ARCore examples
            // This is a simplified version that assumes locked orientation
            session.setDisplayGeometry(Surface.ROTATION_0, viewportWidth, viewportHeight);
            viewportChanged = false;
        }

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();



            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3D objects, show tracking failure reason instead.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                // could show tracking state failure reason here (e.g., Snackbar message)
                return;
            }

            // Get projection matrix.
            camera.getProjectionMatrix(projMtx, 0, 0.1f, 100.0f);

            // Get camera matrix (world-to-camera) and draw.
            camera.getViewMatrix(viewMtx, 0);

            // light estimate reading skipped, could be done here

            // ---------- IndoorAtlas AR SDK inputs
            // camera-to-world matrix
            camera.getPose().toMatrix(invViewMtx, 0);
            // IMU-to-world matrix (usually equal to the camera-to-world matrix in portrait mode)
            frame.getAndroidSensorPose().toMatrix(sensorPoseMtx, 0);

//            iaArSession.setCameraToWorldMatrix(invViewMtx);
//            iaArSession.setPoseMatrix(sensorPoseMtx);

            // Tracked horizontal planes
           /* for (Plane plane : session.getAllTrackables(Plane.class)) {
                if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING) {

                    iaArSession.addArPlane(
                            plane.getCenterPose().getTranslation(),
                            plane.getExtentX(),
                            plane.getExtentZ());
                }
            }*/


            // Check if IndoorAtlas AR is ready
          /*  if (!iaArSession.converged()) {
                if (snackbar == null) {
                    snackbar = Snackbar.make(surfaceView, "Walk 20 meters to any direction so we can orient you.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                //
                // Recommendation: show a message to the user in this case, e.g., a Snackbar with
                // "Walk 20 meters to any direction so we can orient you."
                //

                // returning here is not strictly necessary but recommended as the locations and
                // orientations of the objects may be too much off
                return;
            }*/

            // ---------- Rendering
            // All of the IndoorAtlas AR components below are optional. For example, if you do not
            // wish to render the turn-by-turn arrows, just remove the relevant code block below.

            // Render AR turn-by-turn wayfinding arrows
            for (IAARObject wp : iaArSession.getWayfindingTurnArrows()) {
                float scaleFactor = 0.5f;
//                if (wp.updateModelMatrix(modelMtx)) {
                arrowObject.updateModelMatrix(modelMtx, scaleFactor);
                arrowObject.draw(viewMtx, projMtx);
//                }
            }

           /* // draw wayfinding destination
            if (iaArSession.getWayfindingTarget().updateModelMatrix(modelMtx)) {
                float scaleFactor = 1;
                destinationObject.updateModelMatrix(modelMtx, scaleFactor);
                destinationObject.draw(viewMtx, projMtx);
            }*/

            // draw wayfinding compass arrow
//            if (iaArSession.getWayfindingCompassArrow().updateModelMatrix(modelMtx)) {


//            }

            if (camera.getTrackingState() == TrackingState.TRACKING) {
                Log.d(TAG, "onDrawFrame: Added");
                float scaleFactor = 0.3f;
//                Matrix.multiplyMM(modelMtx, 0, projMtx, 0, viewMtx, 0);
                arrowObject.updateModelMatrix(modelMtx, scaleFactor);
                arrowObject.draw(viewMtx, projMtx);
            }
            addAnchers(frame, latlngs);
        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    // ---------- IndoorAtlas listeners

    @Override
    public void onEnterRegion(IARegion region) {
        Log.d(TAG, "ENTER region " + region.getName());
        IAFloorPlan floorPlan = region.getFloorPlan();
        if (floorPlan != null) {
            Log.i(TAG, "ENTER floor plan %s" + floorPlan.getName());
            // this signifies that the IndoorAtlas SDK has detected the floor and
            // estimates the position to be indoors in the mapped area
        }

        // ---------- (AR) POIs

        // This is an example how to use the IndoorAtlas POIs and their custom JSON payload
        // as the POI database. The AR 3D (or 2D) assets are expected to exist locally, though
        IAVenue venue = region.getVenue();
        if (venue != null) {
            // This signifies that we are close to an IndoorAtlas-fingerprinted venue
            Log.i(TAG, "ENTER venue %s" + venue.getName());
        }
    }

    @Override
    public void onExitRegion(IARegion region) {
        Log.d(TAG, "EXIT region " + region.getName());
    }

    @Override
    public void onLocationChanged(IALocation location) {
        iaLocation = location;
//        Log.d(TAG, "onLocationChanged: " + location.toLocation());
        // we're not using the blue dot directly in this example so the location can be ignored
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "IndoorAtlas status " + status);
    }

    private boolean anchorsNeedRefresh = true;

    private void addAnchers(Frame frame, List<LatLng> latlngs) {
        if (anchorsNeedRefresh) {
            anchorsNeedRefresh = false;

            for (int i = 0; i < latlngs.size(); i++) {
                try {

                    int markerDistance = (int) Math.round(
                            LocationUtils.Companion.getDistanceFromLatLonInMeters(
                                    latlngs.get(i).getLatitude(), latlngs.get(i).getLongitude(),
                                    iaLocation.getLatitude(),
                                    iaLocation.getLongitude())
                    );

                    float markerBearing =/* deviceOrientation.currentDegree + */(float) LocationUtils.Companion.bearing(
                            iaLocation.getLatitude(),
                            iaLocation.getLongitude(),
                            latlngs.get(i).getLatitude(),
                            latlngs.get(i).getLongitude());

                    // Bearing adjustment can be set if you are trying to
                    // correct the heading of north - setBearingAdjustment(10)
                    markerBearing = markerBearing % 360;

                    double rotation = Math.floor(markerBearing);

                    // When pointing device upwards (camera towards sky)
                    // the compass bearing can flip.
                    // In experiments this seems to happen at pitch~=-25
//                    if (deviceOrientation.pitch > -25)
//                        rotation = rotation * Math.PI / 180;

                    int renderDistance = markerDistance;

                    // Limit the distance of the Anchor within the scene.
                    // Prevents rendering issues.
//                    if (renderDistance > distanceLimit)
//                        renderDistance = distanceLimit;

                    // Adjustment to add markers on horizon, instead of just directly in front of camera
//                    double heightAdjustment = Math.round(renderDistance * (Math.tan(Math.toRadians(deviceOrientation.pitch))));

                    // Raise distant markers for better illusion of distance
                    // Hacky - but it works as a temporary measure
//                    int cappedRealDistance = markerDistance > 500 ? 500 : markerDistance;
//                    if (renderDistance != markerDistance)
//                        heightAdjustment += 0.01F * (cappedRealDistance - renderDistance);

                    float x = 0;
                    float z = -renderDistance;

                    float zRotated = (float) (z * Math.cos(rotation) - x * Math.sin(rotation));
                    float xRotated = (float) -(z * Math.sin(rotation) + x * Math.cos(rotation));

                    // Current camera height
                    float y = frame.getCamera().getDisplayOrientedPose().ty();

                    // Don't immediately assign newly created anchor in-case of exceptions
                    Anchor newAnchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(xRotated, y, zRotated)));

                    ArMarkerWrapper markerWrapper = new ArMarkerWrapper();
                    markerWrapper.anchor = newAnchor;

                    markerWrapper.renderer.createOnGlThread(this, "models/arrow.obj", "models/andy.png");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }
}