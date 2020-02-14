package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class weatherActivity extends AppCompatActivity {

    Button btnSearchWeather;
    EditText edtCityWeather;
    ConstraintLayout constraintLayout;
    TextView tvTempCelsius, tvMaxTemp, tvMinTemp, tvRealFeel, tvDescription;
    TextView tvMaxTemp_D, tvMinTemp_D, tvRealFeel_D;
    ImageView ivWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Temps actual");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        constraintLayout = findViewById(R.id.constraintLayout);

        edtCityWeather = findViewById(R.id.edtCityWeather);
        tvTempCelsius = findViewById(R.id.tvTempCelsius);
        tvMaxTemp = findViewById(R.id.tvMaxTemp);
        tvMinTemp = findViewById(R.id.tvMinTemp);
        tvRealFeel = findViewById(R.id.tvRealFeel);
        tvDescription = findViewById(R.id.tvDescription);

        tvMaxTemp_D = findViewById(R.id.tvMaxTemp_D);
        tvMinTemp_D = findViewById(R.id.tvMinTemp_D);
        tvRealFeel_D = findViewById(R.id.tvRealFeel_D);
        ivWeather = findViewById(R.id.ivWeather);

        hideTextViews();

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

        String City = edtCityWeather.getText().toString();
        String Url = "http://api.openweathermap.org/data/2.5/weather?q=" + City + "&APPID=c56998ed0c8e889a77a182e7b74eade4";

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
                            weatherData.getJSONObject("main").getDouble("feels_like"),
                            weatherData.getJSONArray("weather").getJSONObject(0).getString("description"),
                            weatherData.getJSONArray("weather").getJSONObject(0).getString("icon")
                    );

                    tvTempCelsius.setText((int)cityWeather.Temperature+"º");
                    tvMaxTemp.setText((int)cityWeather.MaxTemp+"º");
                    tvMinTemp.setText((int)cityWeather.MinTemp+"º");
                    tvRealFeel.setText((int)cityWeather.RealFeelTemp+"º");
                    tvDescription.setText(cityWeather.Description);

                    URL url = new URL("https://openweathermap.org/img/wn/"+ cityWeather.WeatherIcon +"@2x.png");
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ivWeather.setImageBitmap(bmp);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                revealTextViews();

                constraintLayout.setBackground(getDrawable(R.drawable.weather));

                Dialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Dialog.hide();
            }

        });
    }

    public void hideTextViews() {
        tvTempCelsius.setVisibility(View.GONE);
        tvMaxTemp.setVisibility(View.GONE);
        tvMinTemp.setVisibility(View.GONE);
        tvRealFeel.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        tvMaxTemp_D.setVisibility(View.GONE);
        tvMinTemp_D.setVisibility(View.GONE);
        tvRealFeel_D .setVisibility(View.GONE);
        ivWeather.setVisibility(View.GONE);
    }

    public void revealTextViews() {
        tvTempCelsius.setVisibility(View.VISIBLE);
        tvMaxTemp.setVisibility(View.VISIBLE);
        tvMinTemp.setVisibility(View.VISIBLE);
        tvRealFeel.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        tvMaxTemp_D.setVisibility(View.VISIBLE);
        tvMinTemp_D.setVisibility(View.VISIBLE);
        tvRealFeel_D .setVisibility(View.VISIBLE);
        ivWeather.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
