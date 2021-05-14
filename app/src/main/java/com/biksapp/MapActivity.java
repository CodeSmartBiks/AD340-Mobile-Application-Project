package com.biksapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //local variables

    ArrayList<Cameras> cameraList = null;
    private GoogleMap map;
    double[] coordinate;
    private static final int REQUEST_CODE = 101;
    Location currentLocation;
    boolean locationPermissionGranted;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location lastKnownLocation;
    private static final String TAG = MapActivity.class.getSimpleName();
    private final LatLng defaultLocation = new LatLng(47.79469261671784, -122.30271454279551);
    private static final int DEFAULT_ZOOM = 10;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        fetchLastLocation();
//        loadCamData();
//    }
//
//    private void fetchLastLocation() {
//        ///permission check
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]
//                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE );
//        }
//        Task<Location> task= fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//
//                //if finds the location proceeds
//                if(location !=null){
//                    currentLocation= location;
//                    Toast.makeText(getApplicationContext(),currentLocation.getLatitude()
//                    + ""+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//                    SupportMapFragment supportMapFragment= (SupportMapFragment)
//                            getSupportFragmentManager().findFragmentById(R.id.map);
//                    supportMapFragment.getMapAsync(MapActivity.this);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        //looping through all the list of cam to display
//        for (int i=0; i < cameraList.size(); i++) {
//            Cameras camera = cameraList.get(i);
//            LatLng latLng = new LatLng(camera.getCoordinate()[0], camera.getCoordinate()[1]);
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//                    .title(camera.description).snippet(camera.getImageUrl());
//            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//            Marker marker= googleMap.addMarker((markerOptions));
//            marker.setTag(i);
//        }
//        //lat long for the location and adding the marker
//        LatLng latLng= new LatLng( 47.5673, -122.2641);
//        Marker marker= googleMap.addMarker(new MarkerOptions().
//                position(latLng).title("Current Location"));
//        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//        marker.setTag(0);
//
////        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
////        MarkerOptions markerOptions= new MarkerOptions().position(latLng).title("Current Location");
////        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
////        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
////        googleMap.addMarker(markerOptions);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
//        switch (requestCode){
//            case REQUEST_CODE:
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    fetchLastLocation();
//                }
//                break;
//        }
//    }
//
//    //loading the traffic cam from previous assignment
//    private void loadCamData() {
//        this.cameraList = new ArrayList<>();
//        String camUrl = " https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, camUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            JSONArray features = obj.getJSONArray("Features");
//                            for (int j = 0; j < features.length(); j++) {
//                                JSONObject point = features.getJSONObject(j);
//                                JSONArray points = point.getJSONArray("PointCoordinate");
//                                coordinate = new double[]{points.getDouble(0), points.getDouble(1)};
//                                JSONArray cameras = features.getJSONObject(j).getJSONArray("Cameras");
//                                Cameras camera = new Cameras(cameras.getJSONObject(0).getString("Description"),
//                                        cameras.getJSONObject(0).getString("ImageUrl"),
//                                        cameras.getJSONObject(0).getString("Type"), coordinate);
//                                cameraList.add(camera);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//
//}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        loadCamData();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


        for (int i=0; i < cameraList.size(); i++) {
            Cameras camera = cameraList.get(i);
            LatLng latLng = new LatLng(camera.getCoordinate()[0], camera.getCoordinate()[1]);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .title(camera.description).snippet(camera.getImageUrl());
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            Marker marker = map.addMarker((markerOptions));
            marker.setTag(i);
        }

        Marker marker= map.addMarker(new MarkerOptions().
                position(defaultLocation).title("Current Location"));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        marker.setTag(0);

    }



    private void getLocationPermission() {

        // Request location permission, so that we can get the location of the
        // device. The result of the permission request is handled by a callback,
         //onRequestPermissionsResult.

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void getDeviceLocation() {

         //Get the best and most recent location of the device, which may be null in rare
         //cases when a location is not available.

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location null");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
//loading the traffic cam from previous assignment
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
                                coordinate = new double[]{points.getDouble(0), points.getDouble(1)};
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}