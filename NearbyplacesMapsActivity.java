package com.example.emedcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.emedcare.databinding.ActivityNearbyplacesMapsBinding;

import java.io.IOException;
import java.util.List;

public class NearbyplacesMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationMarker;
    public final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_nearbyplaces_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId()) {
            case R.id.btnSearch:
                EditText txtPlace = findViewById(R.id.txtPlace);
                String location = txtPlace.getText().toString();
                List<Address> addressList;

                if (location != "") {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if (addressList != null) {

                            for (int i = 0; i < addressList.size(); i++) {

                                LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.btnHospitals: {

                mMap.clear();
                String Hospital = "hospital";
                String url = getUrl(latitude, longitude, Hospital);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.776032, 72.712672))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.691388, 72.981319))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.700484, 72.969976))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(31.577791, 74.380489))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.097591, 71.427956))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.750139, 72.784289))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.750804, 72.777530))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyplacesMapsActivity.this, "Showing Nearby Dspencery", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnBanks: {

                mMap.clear();
                String Bank = "bank";
                String url = getUrl(latitude, longitude, Bank);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.646253, 73.041310))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.657067,  73.070007))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(31.581310, 74.414870))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.011519, 71.577103))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.097591, 71.427956))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(31.581328, 74.414859))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.750397, 72.783538))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.753851, 72.775213))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.781341, 72.722604))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));




                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyplacesMapsActivity.this, "Showing Nearby Pharamacy", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnRestaurants: {
                mMap.clear();
                String Restaurant = "restuarant";
                String url = getUrl(latitude, longitude, Restaurant);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.776032, 72.712672))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.691388, 72.981319))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.700484, 72.969976))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(31.577791, 74.380489))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.097591, 71.427956))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.097591, 771.427956))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(33.750104, 72.784753))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));



                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyplacesMapsActivity.this, "Showing Nearby Hospital", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnSchools: {
                mMap.clear();
                String School = "school";
                String url = getUrl(latitude, longitude, School);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;


                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyplacesMapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnHotels: {
                mMap.clear();
                String Hotel = "hotel";
                String url = getUrl(latitude, longitude, Hotel);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(NearbyplacesMapsActivity.this, "Showing Nearby Hotels", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyCYjKCCqaFeeCO6zqfuRVudrV5FzrCTkZk");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}