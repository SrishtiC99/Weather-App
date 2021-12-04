package com.srishti.android.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingPB;
    private RelativeLayout homeRL;
    private TextView cityNameTV;
    private TextInputEditText cityNameET;
    private ImageView searchIV, backgroundIV;
    private TextView temperatureTV;
    private ImageView weatherIconIV;
    private TextView weatherConditionTV;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingPB = findViewById(R.id.loading_pb);
        homeRL = findViewById(R.id.home_rl);
        cityNameTV = findViewById(R.id.city_name_tv);
        cityNameET = findViewById(R.id.city_name_et);
        searchIV = findViewById(R.id.search_iv);
        backgroundIV = findViewById(R.id.background_image_iv);
        temperatureTV = findViewById(R.id.temperature_TV);
        weatherIconIV = findViewById(R.id.weather_icon_iv);
        weatherConditionTV = findViewById(R.id.weather_condition_tv);
        recyclerView = findViewById(R.id.weather_rv);
    }
}