package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class weatherActivity extends AppCompatActivity {

    Button btnSearchWeather;
    TextView tvTempCelsius, tvMaxTemp, tvMinTemp, tvRealFeel, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Temps actual");

        //hideTextViews();

        btnSearchWeather = (Button) findViewById(R.id.btnSearchWeather);
        btnSearchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeWeatherAPI();
            }
        });
    }

    public void chargeWeatherAPI() {
        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setCancelable(false);
        Dialog.setCanceledOnTouchOutside(false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);

        String City = "";
        String Url = "https://samples.openweathermap.org/data/2.5/weather?q=" + City + "&appid=b6907d289e10d714a6e88b30761fae22";

        client.get(this, Url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Dialog.setMessage("Carregant dades...");
                Dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Dialog.setMessage("Processant dades...");

                JSONObject weatherData = null;
                String str = new String(responseBody);

                try {
                    weatherData = new JSONObject(str);

                    cityWeather cityWeather = new cityWeather(
                            weatherData.getJSONObject("main").getDouble("temp"),
                            weatherData.getJSONObject("main").getDouble("temp_max"),
                            weatherData.getJSONObject("main").getDouble("temp_min"),
                            /*weatherData.getJSONObject("main").getDouble("feels_like")*/0,
                            weatherData.getJSONArray("weather").getJSONObject(0).getString("description")
                    );

                    Log.e("BRO", cityWeather.toString());

                    //tvTempCelsius.setText(String.valueOf(cityWeather.Temperature));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                revealTextViews();


                Dialog.hide();

                /*int iPlatos=0;

                try {
                    iPlatos = db.procesarPlatos(platos);
                } catch (JSONException e) {
                    icDialogos.showToastLargo(getApplicationContext(),"Se ha producido un error al procesar las mesas. " + e.getMessage());
                    return;
                }

                if (iPlatos >= 0) {
                    icDialogos.showSnackBarLargo(findViewById(android.R.id.content), "Se han procesado " + String.valueOf(iPlatos) + " platos.");
                }*/
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
        tvTempCelsius = (TextView) findViewById(R.id.tvTempCelsius);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
