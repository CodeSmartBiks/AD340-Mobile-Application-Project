package com.biksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrafficCamMap extends AppCompatActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    ArrayList<Cameras> cameraList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_cam_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        //loadCamData();
    }

    //
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                            + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(TrafficCamMap.this::onMapReady);
                    loadCamData();
                }
            }
        });
    }
    private void loadCamData() {
        this.cameraList = new ArrayList<>();
        String camUrl = " https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, camUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray features = obj.getJSONArray("Features");
                            for (int j = 0; j < features.length(); j++) {
                                JSONObject point = features.getJSONObject(j);
                                JSONArray points = point.getJSONArray("PointCoordinate");
                                double[] coordinate = {points.getDouble(0), points.getDouble(1)};
                                JSONArray cameras = features.getJSONObject(j).getJSONArray("Cameras");
                                Cameras camera = new Cameras(cameras.getJSONObject(0).getString("Description"),
                                        cameras.getJSONObject(0).getString("ImageUrl"),
                                        cameras.getJSONObject(0).getString("Type"), coordinate);
                                cameraList.add(camera);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onMapReady(GoogleMap googleMap) {
        for (int i = 0; i < cameraList.size(); i++) {
            Cameras camera = cameraList.get(i);
            LatLng position = new LatLng(camera.getCoords()[0], camera.getCoords()[1]);
            MarkerOptions markerOptions = new MarkerOptions().position(position)
                    .title(camera.description).snippet(camera.getImageUrl());
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            Marker marker = googleMap.addMarker((markerOptions));
            marker.setTag(i);
        }
    }
}