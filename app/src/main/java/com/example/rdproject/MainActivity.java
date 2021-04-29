package com.example.rdproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button jButton = findViewById(R.id.main_joinButton);
        Button csButton = findViewById(R.id.main_createButton);





        csButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, create.class);

                startActivity(intent);
            }
        });

        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, join.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "left-to-right");

            }
        });

    }

}