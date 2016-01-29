package com.zebro.isel.zebro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LogReceiver logReceiver ;

    protected void updateNodeLocation(String gpsStream){
        Log.i("Map" , "Update Node Location "+ gpsStream);
        Toast.makeText(MapsActivity.this, gpsStream.trim().toString(), Toast.LENGTH_SHORT).show();
    }

    protected void init(){

        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        //Convert IpAddress to String
        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
        logReceiver = new LogReceiver(this , 8888 , ipAddressString );
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
        LatLng isel = new LatLng(13.736027, 100.533849);
        mMap.addMarker(new MarkerOptions().position(isel).title("ISEL Lab"));

        MarkerOptions bb8 = new MarkerOptions().position(new LatLng(13.736474, 100.534010)).title("BB-8");
        bb8.icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan));
        mMap.addMarker(bb8);

        MarkerOptions r2d2 = new MarkerOptions().position(new LatLng(13.736393, 100.533707)).title("R2D2");
        r2d2.icon(BitmapDescriptorFactory.fromResource(R.drawable.wc));
        mMap.addMarker(r2d2);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(isel)) ;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19.0f));
    }
}
