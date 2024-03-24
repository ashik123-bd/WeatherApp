package com.example.weather;

// Add the following import statement
import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    final String APP_ID = "eaca8964987733af06f42c767d2b6d89";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String Location_provider = LocationManager.GPS_PROVIDER;
    TextView NameOfCity, WeatherState, Temperature;
    ImageView weatherIcon;
    RelativeLayout mCityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WeatherState = findViewById(R.id.weatherCondition);
        Temperature = findViewById(R.id.temperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        mCityFinder = findViewById(R.id.cityFinder);
        NameOfCity = findViewById(R.id.cityName);

        mCityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityFinder.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }*/
   @Override
   protected  void onResume(){
       super.onResume();

       Intent mIntent = getIntent();
       String city = mIntent.getStringExtra("city");
       if (city != null){
           getWeatherForNewCity(city);
       }else{
           getWeatherForCurrentLocation();
       }
   }

    private void getWeatherForNewCity(String city) {
       RequestParams params = new RequestParams();
       params.put("q",city);
       params.put("appid",APP_ID);
       LetsDoSomeNetworking(params);

    }


    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());
                RequestParams params = new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                LetsDoSomeNetworking(params);//super.onSuccess(statusCode, headers, response);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_provider, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Location Accessed", Toast.LENGTH_SHORT).show();
                    getWeatherForCurrentLocation();
                } else {
                    // user denied the permission
                }

            }
        }
    }

    private void LetsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherData weatherData = WeatherData.fromJson(response);
                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(MainActivity.this,"Failed to get weather data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherData weather) {
        Temperature.setText(weather.getTemperature());
        NameOfCity.setText(weather.getCity());
        WeatherState.setText(weather.getWeatherType());

        int resourceID = getResources().getIdentifier(weather.getIcon(),"drawable",getPackageName());
        weatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
