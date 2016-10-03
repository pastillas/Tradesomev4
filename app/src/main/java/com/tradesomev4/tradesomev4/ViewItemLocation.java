package com.tradesomev4.tradesomev4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.direction_helpers.DirectionFinder;
import com.tradesomev4.tradesomev4.direction_helpers.DirectionFinderListener;
import com.tradesomev4.tradesomev4.direction_helpers.Route;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ViewItemLocation extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener{

    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private GoogleMap mMap;
    private List<Marker>originMarkers = new ArrayList<>();
    private List<Marker>destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private MaterialDialog progressDialog;
    private String origin;
    private String destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sendRequest();
    }

    public void sendRequest(){
        final Bundle extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                origin = user.getLatitude() + "," + user.getLongitude();

                mDatabase.child("users").child(extras.getString(EXTRAS_POSTER_ID)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        destination = user.getLatitude() + ","+ user.getLongitude();

                        try{
                            new DirectionFinder(ViewItemLocation.this, origin, destination).execute();
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
       /* LatLng camia = new LatLng(13.635281, 123.184376);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camia, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(camia)
                .title("Admiral Torrente")));*/

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
       // mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
    }

    @Override
    public void onDirectionFinderStart() {

        progressDialog = new MaterialDialog.Builder(this)
                .title("Please wait.")
                .content("Finding direction..")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .show();


        if(originMarkers != null){
            for(Marker marker : originMarkers){
                marker.remove();
            }
        }

        if(destinationMarkers != null){
            for(Marker marker : destinationMarkers){
                marker.remove();
            }
        }

        if(polylinePaths != null){
            for(Polyline polyline : polylinePaths){
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for(Route route : routes){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
            ((TextView) findViewById(R.id.tv_distance)).setText(route.distance.text);
            ((TextView) findViewById(R.id.tv_duration)).setText(route.duration.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(route.startAddress)
                .position(route.startLocation)));

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(route.endAddress)
                .position(route.endlocation)));
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.MAGENTA)
                    .width(5 );

            for(int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }
}
