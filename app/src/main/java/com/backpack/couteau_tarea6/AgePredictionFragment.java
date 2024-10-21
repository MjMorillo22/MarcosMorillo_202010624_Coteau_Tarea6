package com.backpack.couteau_tarea6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AgePredictionFragment extends Fragment {

    private EditText nameInput;
    private Button predictButton;
    private TextView resultText;
    private ImageView ageImage;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout de este fragmento
        View view = inflater.inflate(R.layout.fragment_age_prediction, container, false);

        // Inicializar vistas
        nameInput = view.findViewById(R.id.name_input);
        predictButton = view.findViewById(R.id.predict_button);
        resultText = view.findViewById(R.id.result_text);
        ageImage = view.findViewById(R.id.age_image);

        // Inicializar el ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Configurar botón
        predictButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            if (!name.isEmpty()) {
                // Ejecutar la tarea en un hilo separado
                executorService.execute(() -> {
                    String result = getAgePrediction(name);
                    getActivity().runOnUiThread(() -> updateUI(result));
                });
            }
        });

        return view;
    }

    private String getAgePrediction(String name) {
        String apiUrl = "https://api.agify.io/?name=" + name;
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

    private void updateUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int age = jsonObject.getInt("age");

                // Mostrar la edad
                resultText.setText("Edad estimada: " + age);

                // Mostrar imagen y mensaje basado en la edad
                if (age <= 18) {
                    ageImage.setImageResource(R.drawable.nene);  // imagen de joven
                    resultText.append("\nJoven");
                } else if (age <= 60) {
                    ageImage.setImageResource(R.drawable.adulto);  // imagen de adulto
                    resultText.append("\nAdulto");
                } else {
                    ageImage.setImageResource(R.drawable.viejo);  // imagen de anciano
                    resultText.append("\nAnciano");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                resultText.setText("Error al analizar los datos.");
            }
        } else {
            resultText.setText("No se pudo obtener la información.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cerrar el ExecutorService cuando el fragmento es destruido
        executorService.shutdown();
    }
}