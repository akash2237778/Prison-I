package com.example.prison_i;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LivelocationOfprisoners extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMap mMap2;

    Location locationSet;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng latLong;
    LatLng latLong2;
    String addressLine;
    String addressLine2beStored;
    LatLng latLngToBeStored;
    String ObjIdParseServer;
    final Double[] Latitude = new Double[1];
    final Double[] Longitude = new Double[1];
    String RecipUsrName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String adminId;
    String prisonerID;
    String PrisonersLocationLAT;
    String PrisonersLocationLONG;
    int locBoundCheck;
    Marker m1;
    LatLng PrisonerlatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livelocation_ofprisoners);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        adminId = "oiOWeQSV5NSI2rI4vUjYQp1nvE52";   // for test purpose
        prisonerID = "TqlROxGFlxNR1TNuKjbhtlJYrPV2";



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ADMIN/"+adminId+"/prisonerData/" + prisonerID);



        databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        int NumberOfPrisoners = (int)dataSnapshot.getChildrenCount();

                                                        Iterable<DataSnapshot> chlNames = dataSnapshot.getChildren();
                                                        Log.i("No. of Prisoners", String.valueOf(NumberOfPrisoners) );

                                                            PrisonersLocationLAT = dataSnapshot.child("Location").child("latitude").getValue().toString();
                                                            PrisonersLocationLONG = dataSnapshot.child("Location").child("longitude").getValue().toString();

                                                            if(Float.valueOf(PrisonersLocationLONG) > 77.93539644 || Float.valueOf(PrisonersLocationLONG) < 77.93519482 || Float.valueOf(PrisonersLocationLAT) < 30.40486298 || Float.valueOf(PrisonersLocationLAT) > 30.40506298 )
                                                            {
                                                                locBoundCheck = 1; // Alarm needed
                                                            }else{
                                                                locBoundCheck= 0;
                                                            }

                                                            Log.d("prisonersID :: ",  PrisonersLocationLAT +"      " + PrisonersLocationLONG );

                                                            PrisonerlatLng = new LatLng(Double.valueOf(PrisonersLocationLAT),Double.valueOf(PrisonersLocationLONG));

                                                       m1 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(PrisonerlatLng).title("My Location"));
                                                      //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PrisonerlatLng, 15 ));

                                                        }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }});






    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UpdateLocationChangeInfo(location);
                Log.i("info :", "location");
                locationSet = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap2 = googleMap;

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 5000 , 0 ,locationListener );
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng mylocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).position(mylocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15 ));
            Toast.makeText(LivelocationOfprisoners.this, "Updating Location........", Toast.LENGTH_LONG).show();

        }
    }
    public void UpdateLocationChangeInfo(Location location) {
        Marker m2;

        latLong = new LatLng(location.getLatitude(), location.getLongitude());
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));

        Log.i("info", location.toString());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            addressLine = addressList.get(0).getAddressLine(0);
            // ToastMaker(addressLine);

        } catch (IOException e) {
            e.printStackTrace();
        }


        mMap.clear();
        m2 =  mMap.addMarker(new MarkerOptions().position(latLong));
        m1 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(PrisonerlatLng).title("Prisoner Location"));

    }
}