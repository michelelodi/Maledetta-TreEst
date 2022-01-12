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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.Line;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.Station;

public class MapsFragment extends Fragment{

    private final static String TAG = MainActivity.TAG_BASE + "MapsFragment";
    private final static int DEFAULT_ZOOM = 15;
    private final static String MESSAGE = "Allow us to show your current position on map. However, this isn't required, so it's up to you";

    private CommunicationController cc;
    private GoogleMap map;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LinesModel lm;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<Station> stations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cc = CommunicationController.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted ->
                        { if (isGranted) enableMyLocation(); });

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                map.getUiSettings().setZoomControlsEnabled(true);
                LatLng unimi = new LatLng(45.476956, 9.231614);
                map.setMaxZoomPreference(20);
                map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
                map.moveCamera(CameraUpdateFactory.newLatLng(unimi));
                enableMyLocation();
                if(lm.getLineByName(lm.getSelectedDir().getLname()) != null){
                    if(lm.getLineByName(lm.getSelectedDir().getLname()).getStations().size()>0){
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
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setOnMyLocationButtonClickListener(() ->{
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Location userLocation = task.getResult();
                        if (userLocation != null)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(userLocation.getLatitude(),
                                            userLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Toast.makeText(requireContext(), "Unable to locate user",
                                Toast.LENGTH_LONG).show();
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
                return false;
            });
        } else {
            new AlertDialog.Builder(requireContext()).setMessage(MESSAGE).setTitle("Location Permissions")
                    .setPositiveButton("Allow", (dialog, id) ->
                            requestPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_FINE_LOCATION))
                    .setNegativeButton("Deny",(dialog,id)->{}).create().show();
        }
    }

    private void setUpMap(){
        ArrayList<LatLng> polyLinePoints = new ArrayList<>();
        for(Station s : stations){
            LatLng station = new LatLng(Float.parseFloat(s.getLat()), Float.parseFloat(s.getLon()));
            polyLinePoints.add(station);
            map.addCircle(new CircleOptions().center(station).radius(10)
                    .strokeColor(Color.rgb(0,127,255))
                    .fillColor(Color.WHITE)
                    .zIndex(1));
        }
        map.addPolyline(new PolylineOptions()
                .addAll(polyLinePoints)
                .color(Color.rgb(0,127,255)));
    }
}