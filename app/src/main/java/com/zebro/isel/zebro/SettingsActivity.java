package com.zebro.isel.zebro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText densoIpAddressText ;
    private String densoIpAddress ;

    public void onCloseClicked (View view){
        Log.i("Close Button","Clicked!");
        finishActivity(RESULT_OK);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels ;
        int height = dm.heightPixels ;
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5f));

        densoIpAddressText = (EditText) findViewById(R.id.densoIpAddressText) ;
        //Show current DENSO IP Address
        densoIpAddressText.setText(getIntent().getStringExtra("densoIpAddress"));
        //Put it into intent
        densoIpAddress = densoIpAddressText.getText().toString() ;
        Intent intent = new Intent();
        intent.putExtra("densoIpAddress", densoIpAddress);
        setResult(RESULT_OK, intent);

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
                Intent intent = new Intent();
                intent.putExtra("densoIpAddress", densoIpAddress);
                setResult(RESULT_OK, intent);
            }
        });
    }
}
