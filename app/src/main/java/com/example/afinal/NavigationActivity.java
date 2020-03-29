// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageButton;

public class NavigationActivity extends AppCompatActivity {
        BubblePicker bubblePicker;

        GifImageButton search,receive,send,profile;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.navigation);
            receive=(GifImageButton)findViewById(R.id.receivemenu) ;
            send=(GifImageButton)findViewById(R.id.sendmenu) ;
            search=(GifImageButton) findViewById(R.id.searchmenu) ;
            profile=(GifImageButton)findViewById(R.id.satellite) ;


           Toast.makeText(getApplicationContext(),"Devloped by *S_H_R*",Toast.LENGTH_LONG).show();

            receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),ReceiveActivity.class);
                    startActivity(intent);
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                    startActivity(intent);
                }
            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),SendActivity.class);
                    startActivity(intent);
                }
            });

            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),profileActivity.class);
                    startActivity(intent);
                }
            });


        }
    }