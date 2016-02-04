package com.zebro.isel.zebro;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    public static final int SET_IP_ADDRESS = 0 ;
    //Mode Parameter
    public static final int CAR_MODE = 1 ;
    public static final int BIKE_MODE = 2 ;
    public static final int WALK_MODE = 3 ;
    public static final int DISABLE_MODE = 4 ;

    private RelativeLayout welcomeScreen;
    private RelativeLayout titleScreen;

    private FloatingActionButton walkFab;
    private FloatingActionButton carFab;
    private FloatingActionButton bikeFab;
    private FloatingActionButton disableFab;
    private TextView zTitle ;
    private TextView eTitle ;
    private TextView bTitle ;
    private TextView rTitle ;
    private TextView oTitle ;
    private static final String[] colorPattern = {"#FAFAFA","#F5F5F5","#EEEEEE","#E0E0E0","#BDBDBD","#9E9E9E","#757575","#616161","#424242","#212121"
            ,"#212121","#212121","#212121","#212121","#212121","#424242","#616161","#757575","#9E9E9E","#BDBDBD","#E0E0E0","#EEEEEE","#F5F5F5","#FAFAFA",};
    private int[] colorIndex = {0,0,0,0,0};
    private int countTick ;
    // Notification for vibration and sound
    private Notification notification;

    // Shaking Detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    //DENSO WSU IP Address
    public static final String PREFS_NAME = "MyPrefsFile" ;
    protected String densoIpAddress = "192.168.10.77";

    // Fab animation parameter
    protected int showFabDuration = 1500 ;
    protected float startScale = 0.0f ;
    protected float zoomScale = 1.1f ;
    protected float endScale = 1.0f ;

    private boolean isShook = false;

    public void handleShakeEvent(int count) {
        if (!isShook) {
            isShook = true;
            Log.i("ShakeEvent", "Shaking " + count + " time.");
            notification.vibrate(500);
            notification.playStartNoti();
            startApp(DISABLE_MODE);
        }
    }
    public void onCarClicked(View view) {
        Log.i("Car Fab", "Clicked!");
        startApp(CAR_MODE);
    }
    public void onBikeClicked(View view) {
        Log.i("BIKE Fab", "Clicked!");
        startApp(BIKE_MODE);
    }
    public void onWalkClicked(View view) {
        Log.i("Walk Fab", "Clicked!");
        startApp(WALK_MODE);
    }
    public void onDisableClicked(View view) {
        Log.i("Disable Fab", "Clicked!");
        startApp(DISABLE_MODE);
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
        Log.i("Result", "Request:" + requestCode + " DATA:" + data.getStringExtra("densoIpAddress"));
        if (requestCode == SET_IP_ADDRESS) {
            if (resultCode == RESULT_OK) {
                densoIpAddress = data.getStringExtra("densoIpAddress") ;
            }
        }
    }
    public void startApp(final int mode) {
        Log.i("Main", "Starting App in code" + mode);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo mWifi = wifiMgr.getConnectionInfo();
        Log.i("Wifi", "" + wifiMgr.isWifiEnabled());
        if (mWifi != null && wifiMgr.isWifiEnabled()) {
            String modeText = "" ;
            switch (mode){
                case CAR_MODE :
                    modeText = "CAR" ;
                case BIKE_MODE :
                    modeText = "BIKE" ;
                case WALK_MODE :
                    modeText = "WALK" ;
                case DISABLE_MODE :
                    modeText = "DISABLE" ;
            }
            final ProgressDialog progress;
            progress = ProgressDialog.show(this, "Connecting in "+modeText+" mode", "Please wait...", true);

            Thread t;
            t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("densoIpAddress",densoIpAddress);
                    intent.putExtra("mode",mode);

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
    protected void changeTitleColor(){
        for(int i = 0 ; i < colorIndex.length ; i++){
            if(colorIndex[i] >= colorPattern.length) colorIndex[i] = colorPattern.length - 1;
        }
        //Log.i("Color", colorIndex[0]+" "+colorIndex[1]+" "+colorIndex[2]+" "+colorIndex[3]+" "+colorIndex[4]);
        zTitle.setTextColor(Color.parseColor(colorPattern[colorIndex[0]]));
        eTitle.setTextColor(Color.parseColor(colorPattern[colorIndex[1]]));
        bTitle.setTextColor(Color.parseColor(colorPattern[colorIndex[2]]));
        rTitle.setTextColor(Color.parseColor(colorPattern[colorIndex[3]]));
        oTitle.setTextColor(Color.parseColor(colorPattern[colorIndex[4]]));

        if (countTick == 0){
            colorIndex[0]++;
        }
        else if (countTick == 1){
            colorIndex[0]++;
            colorIndex[1]++;
        }
        else if (countTick == 2){
            colorIndex[0]++;
            colorIndex[1]++;
            colorIndex[2]++;
        }
        else if (countTick == 3){
            colorIndex[0]++;
            colorIndex[1]++;
            colorIndex[2]++;
            colorIndex[3]++;
        }
        else if (countTick >= 4){
            colorIndex[0]++;
            colorIndex[1]++;
            colorIndex[2]++;
            colorIndex[3]++;
            colorIndex[4]++;
        }
    }
    protected void shakeFab(final FloatingActionButton fab){
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        shake.setStartTime(2000);
        fab.startAnimation(shake);
    }
    protected void animateFab(final FloatingActionButton fab){
        fab.setVisibility(View.VISIBLE);
        fab.setScaleX(startScale);
        fab.setScaleY(startScale);
        fab.animate().alpha(1.0f).setStartDelay(100).setDuration(showFabDuration) ;
        fab.animate().scaleX(zoomScale).setDuration(showFabDuration);
        fab.animate().scaleY(zoomScale).setDuration(showFabDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fab.animate().scaleX(endScale).setDuration(showFabDuration / 2);
                fab.animate().scaleY(endScale).setDuration(showFabDuration / 2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        shakeFab(fab);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                shakeFab(fab);
                return false;
            }
        });
    }
    protected void showFab() {
        //Set FAB Color
        walkFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DAF4E")));
        carFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
        bikeFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
        disableFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DAF4E")));

        animateFab(walkFab);
        animateFab(carFab);
        animateFab(bikeFab);
        animateFab(disableFab);

        countTick = 0 ;
        CountDownTimer colorTimer = new CountDownTimer(150*(colorPattern.length + 40), 150) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i("Tick",""+countTick+" time left="+millisUntilFinished);
                changeTitleColor();
                countTick++;
            }

            @Override
            public void onFinish() {
                changeTitleColor();
                countTick = 0 ;
                for(int i = 0 ; i < colorIndex.length ; i++){
                    colorIndex[i] = 0 ;
                }
                this.start();
            }
        };
        colorTimer.start();

        CountDownTimer shakeTimer = new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Log.i("Shake Timer","Fabs are shaking!");
                shakeFab(carFab);
                shakeFab(bikeFab);
                shakeFab(walkFab);
                shakeFab(disableFab);
                this.start();
            }
        };
        shakeTimer.start();
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

    public void init() {
        walkFab = (FloatingActionButton) findViewById(R.id.walkFab);
        bikeFab = (FloatingActionButton) findViewById(R.id.bikeFab);
        carFab = (FloatingActionButton) findViewById(R.id.carFab);
        disableFab = (FloatingActionButton) findViewById(R.id.disableFab);

        //Character Binding
        zTitle = (TextView) findViewById(R.id.zTitle);
        eTitle = (TextView) findViewById(R.id.eTitle);
        bTitle = (TextView) findViewById(R.id.bTitle);
        rTitle = (TextView) findViewById(R.id.rTitle);
        oTitle = (TextView) findViewById(R.id.oTitle);

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME , 0);
        if(settings.contains("densoIpAddress")){
            densoIpAddress = settings.getString("densoIpAddress" , "192.168.10.77");
        }
        else{
            densoIpAddress = "192.168.10.84" ;
        }
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
    @Override
    public void onStop(){
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = settings.edit() ;
        Log.d("Settings","Set Denso IP Address = "+densoIpAddress);
        editor.putString("densoIpAddress" , densoIpAddress);

        editor.commit() ;
    }
}
