package com.codingblocks.game2048;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int N = 4;
    int destination=2048;

    Integer[] gridSizes={4,5,6};
    Integer[] destinations={1024,2048,4096};


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playBtn=(Button) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra(String.valueOf(R.string.gridSize), N);
                i.putExtra(String.valueOf(R.string.destination), destination);
                startActivity(i);
            }
        });


        Spinner gridSizeSpinner=(Spinner)findViewById(R.id.gridSizeSpinner);
        ArrayAdapter<Integer> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,gridSizes);

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        gridSizeSpinner.setAdapter(adapter);
        gridSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                N=gridSizes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner destinationSpinner=(Spinner)findViewById(R.id.destination_spinner);
        ArrayAdapter<Integer> adapter2=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,destinations);

        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        destinationSpinner.setAdapter(adapter2);

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destination=destinations[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
