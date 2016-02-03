package com.zebro.isel.zebro;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LogReceiver logReceiver ;

    private static final double NEARLIMIT = 30; // in meter
    private static final double NEARESTLIMIT = 10; // in meter

    private static final int NORMAL_COLOR = Color.GRAY;
    private static final int NEAR_COLOR = Color.GREEN;
    private static final int NEAREST_COLOR = Color.RED;


    protected void updateNodeLocation(String gpsStream){
        //Log.i("Map" , "Update Node Location "+ gpsStream);
        gpsStream = gpsStream.substring(0,gpsStream.indexOf("END"));
        System.out.println("Update Node Location " + gpsStream);

        final String inputForThread = gpsStream;
        // EDITING UI MUST RUN ON UI THREAD !!!!!!!
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                String gpsStream = new String(inputForThread);
                String myloc = "";
                if (gpsStream.indexOf(" ") < 0) {
                    myloc = gpsStream;
                    gpsStream = "";
                } else {
                    myloc = gpsStream.substring(0, gpsStream.indexOf(" "));
                    gpsStream = gpsStream.substring(gpsStream.indexOf(" ") + 1);
                }
                System.out.println("MY LOCATION : " + myloc);
                double lat = Double.parseDouble(myloc.substring(myloc.indexOf(",") + 1, myloc.lastIndexOf(",")));
                double lng = Double.parseDouble(myloc.substring(myloc.lastIndexOf(",") + 1));
                String Caption = "My Location";

                System.out.println("PASS CREATE CAPTION " + Caption);
                LatLng myloc_latlng = new LatLng(lat, lng);
                System.out.println("PASS CREATE MY LATLNG");
                mMap.addMarker(new MarkerOptions().position(myloc_latlng).title(Caption));
                System.out.println("PASS ADD MARKER");

                String nodeloc = "";

                int status = 0; // 0 = normal / 1 = near / 2 = nearest

                while (!gpsStream.equals("")) {
                    if (gpsStream.indexOf(" ") < 0) {
                        nodeloc = gpsStream;
                        gpsStream = "";
                    } else {
                        nodeloc = gpsStream.substring(0, gpsStream.indexOf(" "));
                        gpsStream = gpsStream.substring(gpsStream.indexOf(" ") + 1);
                    }

                    System.out.println("NODE LOCATION : " + nodeloc);
                    lat = Double.parseDouble(nodeloc.substring(nodeloc.indexOf(",") + 1, nodeloc.lastIndexOf(",")));
                    lng = Double.parseDouble(nodeloc.substring(nodeloc.lastIndexOf(",") + 1));
                    Caption = "Neighbor";

                    LatLng node_latlng = new LatLng(lat, lng);
                    //mMap.addMarker(new MarkerOptions().position(node_latlng).title(Caption));
                    Circle circle;

                    double distance = calculateDistance(myloc_latlng, node_latlng);
                    System.out.println("Distance : " + distance);

                    if (distance <= NEARESTLIMIT) {
                        //System.out.println("CAUTION : VERY NEAR");
                        status = 2;
                        circle = mMap.addCircle(new CircleOptions()
                                .center(node_latlng)
                                .radius(2)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(NEAREST_COLOR));
                    } else if (distance <= NEARLIMIT) {
                        //System.out.println("CAUTION : NEAR");
                        if (status == 0) status = 1;
                        circle = mMap.addCircle(new CircleOptions()
                                .center(node_latlng)
                                .radius(2)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(NEAR_COLOR));
                    }else{
                        circle = mMap.addCircle(new CircleOptions()
                                .center(node_latlng)
                                .radius(2)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(NORMAL_COLOR));
                    }
                }

                // FOR NOTIFICATION
                if (status == 2) {
                    System.out.println("VERY NEAR");
                } else if (status == 1) {
                    System.out.println("NEAR");
                } else {
                    System.out.println("NORMAL");
                }


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc_latlng, 19.0f));
            }
        });

    }

    protected static double calculateDistance(LatLng l1, LatLng l2){
        Location locationA = new Location("point A");
        locationA.setLatitude(l1.latitude);
        locationA.setLongitude(l1.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(l2.latitude);
        locationB.setLongitude(l2.longitude);
        double distance = locationA.distanceTo(locationB) ;
        return distance;
    }

    protected void init(){
        logReceiver = new LogReceiver(this , 8888 , getIntent().getStringExtra("densoIpAddress") );
        //logReceiver = new LogReceiver(this , 8888 , "192.168.1.36" );  // THIS IS FOR DEBUG
        logReceiver.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
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
        /*
        LatLng isel = new LatLng(13.736027, 100.533849);
        mMap.addMarker(new MarkerOptions().position(isel).title("ISEL Lab"));

        MarkerOptions bb8 = new MarkerOptions().position(new LatLng(13.736474, 100.534010)).title("BB-8");
        bb8.icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan));
        mMap.addMarker(bb8);

        MarkerOptions r2d2 = new MarkerOptions().position(new LatLng(13.736393, 100.533707)).title("R2D2");
        r2d2.icon(BitmapDescriptorFactory.fromResource(R.drawable.wc));
        mMap.addMarker(r2d2);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(isel, 19.0f)) ;
        */
    }
    @Override
    protected void onPause(){
        super.onPause();
        //Log.i("PPP", "PPP");
        //finish();
        logReceiver.kill();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Log.i("SSS","SSS");
        logReceiver.kill();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        logReceiver.kill();
        //Log.d("DDD","DDD");
        finish();
    }

}
