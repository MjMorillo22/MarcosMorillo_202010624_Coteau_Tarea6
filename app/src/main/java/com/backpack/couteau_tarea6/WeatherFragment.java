package com.backpack.couteau_tarea6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherFragment extends Fragment {

    private TextView weatherTextView;
    private String apiKey = "ec74bb60a54a1b5dcc6779d9c9a88984"; // Reemplaza con tu clave API
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout de este fragmento
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherTextView = view.findViewById(R.id.weather_text);

        // Inicializar el ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Obtener el clima
        getWeather();

        return view;
    }

    private void getWeather() {
        // Ejecutar la tarea en un hilo separado
        executorService.execute(() -> {
            String result = fetchWeather("https://api.openweathermap.org/data/2.5/weather?q=Santo%20Domingo,DO&appid=" + apiKey + "&units=metric");
            getActivity().runOnUiThread(() -> updateWeatherUI(result));
        });
    }

    private String fetchWeather(String apiUrl) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result.toString();
    }

    private void updateWeatherUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");

                // Mostrar el clima en el TextView
                weatherTextView.setText("Temperatura: " + temperature + "°C\nDescripción: " + weatherDescription);

            } catch (JSONException e) {
                e.printStackTrace();
                weatherTextView.setText("Error al obtener el clima.");
            }
        } else {
            weatherTextView.setText("No se pudo obtener la información.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cerrar el ExecutorService cuando el fragmento es destruido
        executorService.shutdown();
    }
}