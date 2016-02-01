package com.zebro.isel.zebro;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class PopActivity extends AppCompatActivity {

    public void onCloseClicked (View view){
        Log.i("Close Button", "Clicked!");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels ;
        int height = dm.heightPixels ;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.8f));
    }
}
