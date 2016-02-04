package com.zebro.isel.zebro;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LogReceiver logReceiver ;
    private Notification notification;

    private int zebroColor ;
    private static final String color1 = "#616161" ;
    private static final String color2 = "#9E9E9E" ;
    private TextView zText ;
    private TextView eText ;
    private TextView bText ;
    private TextView rText ;
    private TextView oText ;

    private static final double NEARLIMIT = 30; // in meter
    private static final double NEARESTLIMIT = 10; // in meter

    private static final int NORMAL_COLOR = Color.GRAY;
    private static final int NEAR_COLOR = Color.GREEN;
    private static final int NEAREST_COLOR = Color.RED;

    // Vibration Pattern Parameter
    private static final int dot = 200;
    private static final int gap = 200;


    // TYPE : 1 = car , 2 = bike , 3 = person , 4 = handicap
    private HashMap<Integer,gps_location> ipMapType;

    protected void animateText(){
        if(zebroColor == 0){
            zText.setTextColor(Color.parseColor(color1));
            eText.setTextColor(Color.parseColor(color2));
            bText.setTextColor(Color.parseColor(color1));
            rText.setTextColor(Color.parseColor(color2));
            oText.setTextColor(Color.parseColor(color1));
            zebroColor = 1 ;
        }else if (zebroColor == 1){
            zText.setTextColor(Color.parseColor(color2));
            eText.setTextColor(Color.parseColor(color1));
            bText.setTextColor(Color.parseColor(color2));
            rText.setTextColor(Color.parseColor(color1));
            oText.setTextColor(Color.parseColor(color2));
            zebroColor = 0 ;
        }
    }

    protected void updateNodeLocation(String gpsStream){
        //Log.i("Map" , "Update Node Location "+ gpsStream);
        gpsStream = gpsStream.substring(0,gpsStream.indexOf("END"));
        System.out.println("Update Node Location " + gpsStream);

        final String inputForThread = gpsStream;
        // EDITING UI MUST RUN ON UI THREAD !!!!!!!
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animateText();
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

                // mMap.addMarker(new MarkerOptions().position(myloc_latlng).title(Caption));

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(myloc_latlng)
                        .radius(3)
                        .strokeColor(Color.WHITE)
                        .fillColor(Color.parseColor("#448AFF")));

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

                    double distance = calculateDistance(myloc_latlng, node_latlng);
                    System.out.println("Distance : " + distance);

                    String node_ip = nodeloc.substring(0, nodeloc.indexOf(","));
                    int ip_int = Integer.parseInt(node_ip.substring(0, node_ip.indexOf("."))) * 256;
                    node_ip = node_ip.substring(node_ip.indexOf(".") + 1);
                    ip_int += Integer.parseInt(node_ip.substring(0,node_ip.indexOf("."))) * 256;
                    node_ip = node_ip.substring(node_ip.indexOf(".") + 1);
                    ip_int += Integer.parseInt(node_ip.substring(0,node_ip.indexOf("."))) * 256;
                    node_ip = node_ip.substring(node_ip.indexOf(".") + 1);
                    ip_int += Integer.parseInt(node_ip.substring(0, node_ip.indexOf("/")));
                    node_ip = node_ip.substring(node_ip.indexOf("/") + 1);
                    int type = Integer.parseInt(node_ip);

                    gps_location temp;

                    float deg = 0;

                    if(ipMapType.containsKey(ip_int)){
                        temp = ipMapType.get(ip_int);

                        deg = calculateDegree(node_latlng, temp.latlng);
                        System.out.println("DEGREE : " + deg);

                        temp.type = type;
                        temp.latlng = node_latlng;

                    }else{
                        temp = new gps_location();
                        temp.latlng = node_latlng;
                        temp.type = type;
                    }

                    ipMapType.put(ip_int, temp);


                    System.out.println("MAP SIZE : " + ipMapType.size());
                    if(type == 1) { // CAR
                        if (distance <= NEARESTLIMIT) {
                            //System.out.println("CAUTION : VERY NEAR");
                            status = 2;
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    .rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vn_nav));
                            mMap.addMarker(marker);
                        } else if (distance <= NEARLIMIT) {
                            //System.out.println("CAUTION : NEAR");
                            if (status == 0) status = 1;

                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    .rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.n_nav));
                            mMap.addMarker(marker);
                        } else {
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    .rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.f_nav));
                            mMap.addMarker(marker);
                        }
                    }else if(type == 2) { // BIKE
                        if (distance <= NEARESTLIMIT) {
                            //System.out.println("CAUTION : VERY NEAR");
                            status = 2;
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vn_bike));
                            mMap.addMarker(marker);
                        } else if (distance <= NEARLIMIT) {
                            //System.out.println("CAUTION : NEAR");
                            if (status == 0) status = 1;

                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.n_bike));
                            mMap.addMarker(marker);
                        } else {
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.f_bike));
                            mMap.addMarker(marker);
                        }
                    }else if(type == 3) { // PEOPLE
                        if (distance <= NEARESTLIMIT) {
                            //System.out.println("CAUTION : VERY NEAR");
                            status = 2;
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vn_account));
                            mMap.addMarker(marker);
                        } else if (distance <= NEARLIMIT) {
                            //System.out.println("CAUTION : NEAR");
                            if (status == 0) status = 1;

                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.n_account));
                            mMap.addMarker(marker);
                        } else {
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.f_account));
                            mMap.addMarker(marker);
                        }
                    }else if(type == 4) { // HANDICAP
                        if (distance <= NEARESTLIMIT) {
                            //System.out.println("CAUTION : VERY NEAR");
                            status = 2;
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vn_wheelchair));
                            mMap.addMarker(marker);
                        } else if (distance <= NEARLIMIT) {
                            //System.out.println("CAUTION : NEAR");
                            if (status == 0) status = 1;

                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.n_wheelchair));
                            mMap.addMarker(marker);
                        } else {
                            MarkerOptions marker = new MarkerOptions()
                                    .position(node_latlng)
                                    .title(Caption)
                                    .snippet(lat + " " + lng)
                                    //.rotation(deg)
                                    .anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.f_wheelchair));
                            mMap.addMarker(marker);
                        }
                    }
                }

                // FOR NOTIFICATION
                notification.clear();
                if (status == 2) {
                    System.out.println("VERY NEAR");
                    long[] vibrate_pattern = {0 , dot , gap ,dot , gap ,dot , gap ,dot , gap ,dot , gap  } ;
                    notification.vibratePatternOnce(vibrate_pattern);
                    notification.playVeryNearNoti();
                } else if (status == 1) {
                    System.out.println("NEAR");
                    long[] vibrate_pattern = {0 , dot , gap ,dot , gap } ;
                    notification.vibratePatternOnce(vibrate_pattern);
                    notification.playNearNoti();
                } else {
                    System.out.println("NORMAL");
                    notification.vibrate(150);
                   // notification.playNearNoti(); /////////////// DONT HAVE
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

    protected static float calculateDegree(LatLng cur, LatLng prev){
        Location locationA = new Location("point A");
        locationA.setLatitude(cur.latitude);
        locationA.setLongitude(cur.longitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(prev.latitude);
        locationB.setLongitude(prev.longitude);

        float degree = locationB.bearingTo(locationA) ;
        return degree;
    }

    protected void init(){
        //Toggling color text
        zebroColor = 0 ;
        zText = (TextView) findViewById(R.id.zText);
        eText = (TextView) findViewById(R.id.eText);
        bText = (TextView) findViewById(R.id.bText);
        rText = (TextView) findViewById(R.id.rText);
        oText = (TextView) findViewById(R.id.oText);

        ipMapType = new HashMap<Integer, gps_location>();

        notification = new Notification(getApplicationContext());
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

    private class gps_location{
        LatLng latlng;
        public int type;
    }
}

