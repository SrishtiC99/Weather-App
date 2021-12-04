package com.srishti.android.weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private ArrayList<WeatherModel> weatherInfoList;
    private WeatherAdapter adapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // It will hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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

        weatherInfoList = new ArrayList<>();
        adapter = new WeatherAdapter(this, weatherInfoList);
        recyclerView.setAdapter(adapter);

        String cityName = getUserLocation();
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityNameET.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter City Name", Toast.LENGTH_LONG).show();
                }
                else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissioms granted...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private String getUserLocation(){
        // Insure location permission is granted
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String cityName = getCityName(location.getLongitude(), location.getLatitude());
        return cityName;

    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for(Address address : addresses){
                if(address != null){
                    String city = address.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    }else{
                        Toast.makeText(this, "CITY NOT FOUND", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }


    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=24a33c3a19c1408f96371843210412&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherInfoList.clear();

                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temp + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weatherIconIV);
                    weatherConditionTV.setText(condition);
                    /*if(isDay == 1){
                        Picasso.get().load("").into(backgroundIV);
                    }
                    else{
                        Picasso.get().load("").into(backgroundIV);
                    }*/

                    JSONObject forecastObj = response.getJSONObject("forecase");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for(int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temperature = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherInfoList.add(new WeatherModel(time, temperature, img, wind));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid City Name", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}