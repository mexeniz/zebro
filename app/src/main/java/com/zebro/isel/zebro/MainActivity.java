package com.zebro.isel.zebro;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.zebro.isel.zebro.R.drawable.background;

public class MainActivity extends AppCompatActivity {

    static final int SET_IP_ADDRESS = 0 ;

    private RelativeLayout welcomeScreen;
    private RelativeLayout titleScreen;

    private FloatingActionButton walkFab;
    private FloatingActionButton carFab;
    private TextView titleText ;
    // Notification for vibration and sound
    private Notification notification;

    // Shaking Detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    //DENSO WSU IP Address
    protected String densoIpAddress = "192.168.10.77";

    // Fab animation parameter
    protected int showFabDuration = 1000 ;
    protected float startScale = 0.0f ;
    protected float zoomScale = 1.5f ;
    protected float endScale = 1.0f ;

    private boolean isShook = false;

    public void handleShakeEvent(int count) {
        if (!isShook) {
            isShook = true;
            Log.i("ShakeEvent", "Shaking " + count + " time.");
            notification.vibrate(500);
            notification.playNoti();
            startApp("Walk");
        }
    }

    public void onWalkClicked(View view) {
        Log.i("Walk Fab", "Clicked!");
        startApp("Walk");
    }

    public void onCarClicked(View view) {
        Log.i("Car Fab", "Clicked!");
        startApp("Car");
    }
    public void onInfoClicked(View view) {
        Log.i("Info Button", "Clicked!");
        Intent intent = new Intent(MainActivity.this, PopActivity.class);
        startActivity(intent);
    }
    public void onSettingClicked(View view){
        Log.i("Setting Button", "Clicked!");
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("densoIpAddress",densoIpAddress);
        startActivityForResult(intent, SET_IP_ADDRESS);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Result","Request:"+requestCode+" DATA:"+data.getStringExtra("densoIpAddress"));
        if (requestCode == SET_IP_ADDRESS) {
            if (resultCode == RESULT_OK) {
                densoIpAddress = data.getStringExtra("densoIpAddress") ;
            }
        }
    }
    public void startApp(String mode) {
        Log.i("Main", "Starting App in" + mode);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo mWifi = wifiMgr.getConnectionInfo();
        Log.i("Wifi",""+wifiMgr.isWifiEnabled());
        if (mWifi != null && wifiMgr.isWifiEnabled()) {
            final ProgressDialog progress;
            progress = ProgressDialog.show(this, "Connecting in "+mode.toUpperCase()+" mode", "Please wait...", true);

            Thread t;
            t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("densoIpAddress",densoIpAddress);


                    // Open Connection...
                    try {
                        //Receive Message From DENSO
                        Thread.sleep(2000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                    startActivity(intent);

                    //Move this var to time out.
                    isShook = false;
                }
            });
            t.start();
        } else {
            Toast.makeText(MainActivity.this, "Please connect to WiFi before start app.", Toast.LENGTH_SHORT).show();
            isShook = false;
        }

    }

    public void init() {
        walkFab = (FloatingActionButton) findViewById(R.id.walkFab);
        carFab = (FloatingActionButton) findViewById(R.id.carFab);
        titleText = (TextView) findViewById(R.id.title);

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

    protected void showFab() {
        //Set FAB Color
        walkFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DAF4E")));
        carFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));

        walkFab.setVisibility(View.VISIBLE);
        walkFab.setScaleX(startScale);
        walkFab.setScaleY(startScale);
        walkFab.animate().alpha(1.0f).setStartDelay(100).setDuration(showFabDuration) ;
        walkFab.animate().scaleX(zoomScale).setDuration(showFabDuration);
        walkFab.animate().scaleY(zoomScale).setDuration(showFabDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                walkFab.animate().scaleX(endScale).setDuration(showFabDuration/2);
                walkFab.animate().scaleY(endScale).setDuration(showFabDuration/2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        carFab.setVisibility(View.VISIBLE);
        carFab.setScaleX(startScale);
        carFab.setScaleY(startScale);
        carFab.animate().alpha(1.0f).setStartDelay(100).setDuration(showFabDuration) ;
        carFab.animate().scaleX(zoomScale).setDuration(showFabDuration);
        carFab.animate().scaleY(zoomScale).setDuration(showFabDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                carFab.animate().scaleX(endScale).setDuration(showFabDuration/2);
                carFab.animate().scaleY(endScale).setDuration(showFabDuration/2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //Add shaking animation
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        shake.setStartTime(2000);

        walkFab.startAnimation(shake);
        carFab.startAnimation(shake);

        walkFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                walkFab.startAnimation(shake);
                return false;
            }
        });
        carFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                carFab.startAnimation(shake);
                return false;
            }
        });

    }

    protected void welcomeAnimation() {

        // Start animation
        welcomeScreen = (RelativeLayout) findViewById(R.id.welcomeScreen);
        welcomeScreen.animate().alpha(0.0f).setStartDelay(1500).setDuration(2000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showFab();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        titleScreen = (RelativeLayout) findViewById(R.id.titleScreen);
        titleScreen.setBackgroundResource(R.drawable.background);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        welcomeAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}
