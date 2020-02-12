package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class weatherActivity extends AppCompatActivity {

    TextView tvTempCelsius, tvMaxTemp, tvMinTemp, tvRealFeel, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Temps actual");
    }

    public void chargeWeatherAPI() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);

        String City = "";
        String Url = "https://samples.openweathermap.org/data/2.5/weather?q=" + City + "&appid=b6907d289e10d714a6e88b30761fae22";

        client.get(this, Url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                /*// called before request is started
                Dialog.setMessage("Descargando platos...");
                Dialog.show();*/
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public void hideTextViews() {
        tvTempCelsius = (TextView) findViewById(R.id.tvTempCelsius);

    }

    public void revealTextViews() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
