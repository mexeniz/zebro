package com.zebro.isel.zebro;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by ASUS on 8/1/2559.
 */
public class Notification {
    private SharedPreferences settings ;

    private Vibrator vibrator;
    private Context context ;
    private Ringtone r ;
    private SoundPool soundPool;
    private HashMap<Integer,Integer> soundPoolMap;
    private AudioManager audioManager;
    private int streamVolume ;
    private int previousTrack = -1 ;
    private boolean isTestSound = false ;
    private static final int SOUND_VERY_NEAR = 1 ;
    private static final int SOUND_NEAR = 2 ;


    private void initSounds() {

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        streamVolume = settings.getInt("notiVolume", audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

        //AudioAttributes audioAttributes = new AudioAttributes.Builder().setLegacyStreamType(AudioAttributes.USAGE_MEDIA).build();
        //soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        soundPool = new SoundPool(4, AudioManager.STREAM_NOTIFICATION, settings.getInt("notiVolume",streamVolume));

        soundPoolMap = new HashMap<Integer, Integer>();

        soundPoolMap.put(SOUND_NEAR, soundPool.load(context, R.raw.ceres, 1));
        soundPoolMap.put(SOUND_VERY_NEAR, soundPool.load(context, R.raw.alarm1, 1));

    }

    public Notification(Context context){
        this.context = context ;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        /*veryNearPlayer = MediaPlayer.create(context,R.raw.nfc_transfer_initiated);
        nearPlayer = MediaPlayer.create(context, R.raw.nfc_initiated);*/

        settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        Log.d("Settings" , "IP "+settings.contains("densoIpAddress"));
        Log.d("Settings" , "Volume "+settings.contains("notiVolume"));
        Log.d("Settings", "Vibration " + settings.contains("notiVibration"));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(context, notification);

        initSounds();
    }

    public void vibrate(int duration){
        if (!settings.getBoolean("notiVibration" , true)) return;
        vibrator.vibrate(duration);
        Log.i("Notification", "Vibrate Duration = " + duration + " ms");
    }
    public void vibratePatternOnce(long[] pattern ){
        if (!settings.getBoolean("notiVibration" , true)) return;
        vibrator.vibrate(pattern , -1);
        Log.i("Notification", "Pattern");
    }
    public void playVeryNearNoti(){
        try {
            /*veryNearPlayer = MediaPlayer.create(context,R.raw.nfc_transfer_initiated);
            veryNearPlayer.seekTo(0);
            veryNearPlayer.start();*/
            previousTrack = soundPool.play(soundPoolMap.get(SOUND_VERY_NEAR), streamVolume, streamVolume, 1, 2, 2.0f);
            Log.i("Notification","Play Very Near sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNearNoti(){
        try {
            /*nearPlayer = MediaPlayer.create(context, R.raw.nfc_initiated);
            nearPlayer.seekTo(0);
            nearPlayer.start();*/
            previousTrack = soundPool.play(soundPoolMap.get(SOUND_NEAR), streamVolume, streamVolume, 1, 1, 1.5f);
            Log.i("Notification", "Play Near sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playSettingSound(int volume){
        try {
            if (isTestSound) return;
            soundPool.play(soundPoolMap.get(SOUND_NEAR), volume, volume, 1, 1, 1.5f);

            isTestSound = true ;

            CountDownTimer playTimer = new CountDownTimer(500,100){
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    isTestSound = false ;
                }
            };
            playTimer.start();

            Log.i("Notification", "Play Test sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playStartNoti(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(context, notification);
            r.play();
            Log.i("Notification","Play Start sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void clear(){
        if(r!=null) r.stop();
        if (previousTrack != -1)soundPool.stop(previousTrack);
        vibrator.cancel();
    }
}
