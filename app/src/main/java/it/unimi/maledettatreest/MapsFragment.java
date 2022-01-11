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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.model.UsersModel;

public class MapsFragment extends Fragment{

    private final static String TAG = MainActivity.TAG_BASE + "MapsFragment";
    private final static String MESSAGE = "Allow us to show your current position on map. However, this isn't required, so it's up to you";

    private CommunicationController cc;
    private GoogleMap map;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private UsersModel um;
    private LinesModel lm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cc = CommunicationController.getInstance(requireContext());
        um = UsersModel.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
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
                map.getUiSettings().setZoomControlsEnabled(true);
                enableMyLocation();
                LatLng unimi = new LatLng(45.476956, 9.231614);
                map.setMaxZoomPreference(18);
                map.setMinZoomPreference(12);
                map.moveCamera(CameraUpdateFactory.zoomTo(map.getMinZoomLevel()));
                map.moveCamera(CameraUpdateFactory.newLatLng(unimi));
                cc.getStations(um.getSessionUser().getSid(),
                        lm.getSelectedDir().getDid(),
                        this::handleGetStationsResponse,
                        error -> cc.handleVolleyError(error,requireContext(),TAG));
            });
        }
    }

    private void handleGetStationsResponse(JSONObject response) {
        try {
            JSONArray stations = response.getJSONArray("stations");
            ArrayList<LatLng> polyLinePoints = new ArrayList<>();
            for(int i = 0; i < stations.length(); i++){
                LatLng station = new LatLng(
                        Float.parseFloat(((JSONObject)stations.get(i)).getString("lat")),
                        Float.parseFloat(((JSONObject)stations.get(i)).getString("lon")));
                polyLinePoints.add(station);
                map.addCircle(new CircleOptions().center(station).radius(10)
                                        .strokeColor(Color.rgb(0,127,255))
                                        .fillColor(Color.WHITE)
                                        .zIndex(1));
            }
            map.addPolyline(new PolylineOptions()
                                    .addAll(polyLinePoints)
                                    .color(Color.rgb(0,127,255)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setOnMyLocationButtonClickListener(() -> {
                //TODO getcurrentlocation and call super(onMyLocationButtonClick())
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
}