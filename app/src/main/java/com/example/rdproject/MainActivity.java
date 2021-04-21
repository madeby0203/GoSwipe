package com.example.rdproject;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Boolean mark_is_cool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("SetTextI18n")
    public void testclick(View view){
        TextView testtxt = findViewById(R.id.testtxt);
        Button testbutton = findViewById(R.id.testbutton);

        if (mark_is_cool) {
            testtxt.setText("Mark is cool");
            testbutton.setText("Niet cool!");
            mark_is_cool = false;
        } else {
            testtxt.setText("Mark is niet cool");
            testbutton.setText("Wel cool!");
            mark_is_cool = true;
        }
    }

}