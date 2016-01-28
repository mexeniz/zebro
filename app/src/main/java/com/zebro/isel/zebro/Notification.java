package com.zebro.isel.zebro;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by ASUS on 8/1/2559.
 */
public class Notification {
    private Vibrator vibrator;
    private Context context ;
    public Notification(Context context){
        this.context = context ;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(int duration){
        vibrator.vibrate(duration);
        Log.i("Notification", "Vibrate Duration = " + duration + " ms");
    }

    public void playNoti(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            Log.i("Notification","Play notify sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAlert(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            Log.i("Notification", "Play alert sound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
