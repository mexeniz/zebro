package com.zebro.isel.zebro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private EditText densoIpAddressText ;
    private SeekBar volumeBar ;
    private Switch vibSwitch ;
    private SharedPreferences settings;

    private String densoIpAddress ;
    private int volume ;
    private boolean vibration ;
    public void onCloseClicked (View view){
        Log.i("Close Button", "Clicked!");
        finishActivity(RESULT_OK);
        finish();
    }
    private void saveSettings(){

        SharedPreferences.Editor editor = settings.edit() ;
        editor.clear();
        editor.putInt("notiVolume",volume);
        editor.putBoolean("notiVibration",vibration);
        editor.putString("densoIpAddress",densoIpAddress);
        editor.commit();
        Log.i("Setting","saved");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = getSharedPreferences(MainActivity.PREFS_NAME , 0);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels ;
        int height = dm.heightPixels ;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5f));

        densoIpAddressText = (EditText) findViewById(R.id.densoIpAddressText) ;
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        vibSwitch = (Switch) findViewById(R.id.vibSwitch);

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        // Default
        int defaultVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        Log.d("Settings" , "IP "+settings.contains("densoIpAddress"));
        Log.d("Settings" , "Volume "+settings.contains("notiVolume"));
        Log.d("Settings", "Vibration " + settings.contains("notiVibration"));
        densoIpAddress = settings.getString("densoIpAddress", "192.168.10.77");
        volume = settings.getInt("notiVolume", defaultVolume);
        vibration = settings.getBoolean("notiVibration", true);

        //Show current DENSO IP Address

        densoIpAddressText.setText(densoIpAddress);

        densoIpAddressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                densoIpAddress = densoIpAddressText.getText().toString();
                Log.d("Setting", "IP:" + densoIpAddress);
                saveSettings();
            }
        });



        volumeBar.setMax(100);
        volumeBar.setProgress(volume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Noti Volume", "value = " + progress);
                volume = progress ;
                saveSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vibSwitch.setChecked(vibration);
        vibSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibration = isChecked ;
                saveSettings();
                Log.d("Vib Noti" , "Value = " + isChecked);
            }
        });
    }
    @Override
    protected void onStop(){
        super.onStop();
        saveSettings();
    }
}
