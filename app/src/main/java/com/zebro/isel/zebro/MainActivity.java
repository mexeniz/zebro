package com.zebro.isel.zebro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static com.zebro.isel.zebro.R.drawable.background;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout welcomeScreen ;
    private RelativeLayout titleScreen ;

    // Notification for vibration and sound
    private Notification notification;

    // Shaking Detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private boolean isShook = false ;
    public void handleShakeEvent(int count){
        if (!isShook) {
            isShook = true ;
            Log.i("ShakeEvent", "Shaking " + count + " time.");
            notification.vibrate(500);
            notification.playNoti();
            startApp();
        }
    }
    public void onStartClicked(View view) {
        Log.i("Start Button", "Clicked!");
        startApp();
    }
    public void startApp(){
        Log.i("Main" , "Starting App");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getActiveNetworkInfo();

        if (mWifi != null && mWifi.isConnected()) {
            final ProgressDialog progress;
            progress = ProgressDialog.show(this,"Connecting","Please wait..." ,true);

            Thread t ;
            t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                    // Open Connection...
                    try{
                        //Receive Message From DENSO
                        Thread.sleep(2000);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    progress.dismiss();
                    startActivity(intent);

                    //Move this var to time out.
                    isShook = false ;
                }
            }) ;
            t.start();
        }else{
            Toast.makeText(MainActivity.this, "Please connect to WiFi before start app.", Toast.LENGTH_SHORT).show();
            isShook = false ;
        }

    }
    public void init(){
        // Notification initialization
        notification = new Notification(this.getApplicationContext());

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }
        });
    }
    public void welcomeAnimation(){

        // Start animation
        welcomeScreen = (RelativeLayout) findViewById(R.id.welcomeScreen);
        welcomeScreen.animate().alpha(0.0f).setDuration(1500);

        titleScreen  = (RelativeLayout) findViewById(R.id.titleScreen);
        titleScreen.setBackgroundResource(R.drawable.background);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        try{
            Thread.sleep(4000);
        }catch (Exception e){
            e.printStackTrace();
        }

        welcomeAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
