package com.zebro.isel.zebro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import static com.zebro.isel.zebro.R.drawable.background;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout welcomeScreen ;
    private RelativeLayout titleScreen ;
    public void startApp(View view){

        Log.i("Main" , "Starting App");

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
            }
        }) ;
        t.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        // Start animation
        welcomeScreen = (RelativeLayout) findViewById(R.id.welcomeScreen);
        welcomeScreen.animate().alpha(0.0f).setDuration(2000);

        titleScreen  = (RelativeLayout) findViewById(R.id.titleScreen);
        titleScreen.setBackgroundResource(R.drawable.background);
    }

}
