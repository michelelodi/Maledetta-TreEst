package it.unimi.maledettatreest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.Station;

public class MapsFragment extends Fragment{

    private final static String TAG = MainActivity.TAG_BASE + "MapsFragment";
    private final static int DEFAULT_ZOOM = 15;
    private final static String MESSAGE = "Allow us to show your position on map. However, this isn't required, so it's up to you";

    private CommunicationController cc;
    private GoogleMap map;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LinesModel lm;
    private ArrayList<Station> stations;
    private final ArrayList<LatLng> polyPoints = new ArrayList<>();
    private final ArrayList<Circle> mapCircles = new ArrayList<>();
    private Marker activeMarker = null;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        cc = CommunicationController.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        toggleMyLocationEnabled();
                        enableMyLocation();
                    }
                });

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;

                if(lm.getLineByName(lm.getSelectedDir().getLname()) != null){
                    if(lm.getLineByName(lm.getSelectedDir().getLname()).getStations() != null){
                        stations = lm.getLineByName(lm.getSelectedDir().getLname()).getStations();
                        setUpMap();
                    } else
                        cc.getStations(lm.getSelectedDir().getDid(), this::handleGetStationsResponse,
                                error -> cc.handleVolleyError(error, requireContext(), TAG));
                }else
                    cc.getLines(this::handleGetLinesResponse,
                            error -> cc.handleVolleyError(error, requireContext(), TAG));
            });
        }
    }

    private void handleGetLinesResponse(JSONObject response) {
        try {
            for(int i = 0; i < response.getJSONArray("lines").length(); i++)
                lm.addLine(new Line(((JSONObject) response.getJSONArray("lines").get(i))));
            cc.getStations(lm.getSelectedDir().getDid(), this::handleGetStationsResponse,
                    error -> cc.handleVolleyError(error, requireContext(), TAG));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void handleGetStationsResponse(JSONObject response) {
        Line l = lm.getLineByName(lm.getSelectedDir().getLname());
        try {
            l.setStations(response.getJSONArray("stations"));
            lm.setLine(l);
            stations = l.getStations();
            setUpMap();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void enableMyLocation() {

        CancellationToken cancellationToken = new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return this;
            }
            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        };

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED)
                new AlertDialog.Builder(requireContext()).setMessage(MESSAGE)
                        .setTitle("Location Permissions")
                        .setPositiveButton("Allow", (dialog, id) ->
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                        .setNegativeButton("Deny",(dialog,id)->{}).create().show();
        else{
            map.setOnMyLocationButtonClickListener(() -> {
                Task<Location> locationTask = fusedLocationProviderClient.
                        getCurrentLocation(LocationRequest.PRIORITY_NO_POWER, cancellationToken);
                locationTask.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Location userLocation = task.getResult();
                        if (userLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(userLocation.getLatitude(),
                                            userLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Toast.makeText(requireContext(), "Unable to locate user",
                                Toast.LENGTH_LONG).show();
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }).addOnCanceledListener(requireActivity(), () -> Toast.makeText(requireContext(),
                        "Unable to locate user", Toast.LENGTH_LONG).show());
                return false;
            });
        }
    }

    private void setUpMap(){

        map.setOnCircleClickListener(circle -> {
            if(activeMarker != null){
                activeMarker.remove();
                activeMarker = null;
            }

            for(int i = 0; i < mapCircles.size(); i++)
                if(mapCircles.get(i).equals(circle))
                    activeMarker = map.addMarker(new MarkerOptions()
                                                    .position(mapCircles.get(i).getCenter())
                                                    .title(stations.get(i).getSname()));
            if (activeMarker != null) activeMarker.showInfoWindow();

        });

        map.setOnMapClickListener(latLng -> {
            if(activeMarker != null && !latLng.equals(activeMarker.getPosition())){
                activeMarker.remove();
                activeMarker = null;
            }
        });

        for(Station s : stations)
            polyPoints.add(new LatLng(Float.parseFloat(s.getLat()), Float.parseFloat(s.getLon())));

        enableMyLocation();

        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMaxZoomPreference(20);
        map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        map.moveCamera(CameraUpdateFactory.newLatLng(polyPoints.get((polyPoints.size()-1)/2)));

        AtomicReference<Polyline> mapPolyline = new AtomicReference<>();
        if(checkFullPolyVisibility()) mapPolyline.set(drawPolyline(polyPoints));
        else{
            int actualZoom = DEFAULT_ZOOM;
            while(!checkFullPolyVisibility()){
                map.moveCamera(CameraUpdateFactory.zoomTo(--actualZoom));
                map.moveCamera(CameraUpdateFactory.newLatLng(polyPoints.get((polyPoints.size()-1)/2)));
            }
        }

        map.setOnCameraMoveStartedListener(i -> {
            map.getUiSettings().setZoomControlsEnabled(false);
            mapPolyline.get().remove();
            for(Circle c : mapCircles) c.remove();
            mapCircles.clear();
            if(activeMarker != null){
                activeMarker.remove();
                activeMarker = null;
            }
            if(map.isMyLocationEnabled())toggleMyLocationEnabled();
        });

        map.setOnCameraIdleListener(() -> {
            map.getUiSettings().setZoomControlsEnabled(true);
            if(checkPolyVisibility()) mapPolyline.set(drawPolyline(polyPoints));
            if(!map.isMyLocationEnabled())toggleMyLocationEnabled();
        });
    }

    private boolean checkFullPolyVisibility(){
        for(LatLng point : polyPoints)
            if(!map.getProjection().getVisibleRegion().latLngBounds.contains(point))
               return false;
        return true;
    }

    private boolean checkPolyVisibility(){
        for(LatLng point : polyPoints)
            if(map.getProjection().getVisibleRegion().latLngBounds.contains(point))
                return true;
        return false;
    }

    private Polyline drawPolyline(ArrayList<LatLng> polyLinePoints){
        for(LatLng point : polyLinePoints)
            mapCircles.add(map.addCircle(new CircleOptions().center(point)
                    .radius(Math.abs(21 - map.getCameraPosition().zoom)/2)
                    .strokeColor(Color.rgb(0, 127, 255))
                    .fillColor(Color.rgb(0, 127, 255))
                    .zIndex(1).clickable(true)));

        return map.addPolyline(new PolylineOptions()
                .addAll(polyLinePoints)
                .color(Color.rgb(0,127,255))
                .width(5));
    }

    private void toggleMyLocationEnabled(){
        if(ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(!map.isMyLocationEnabled());
    }
}