package com.backpack.couteau_tarea6;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenderPredictionFragment extends Fragment {

    private EditText nameInput;
    private Button predictButton;
    private TextView resultText;
    private ImageView genderImage;
    private ExecutorService executorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout de este fragmento
        View view = inflater.inflate(R.layout.fragment_gender_prediction, container, false);

        // Inicializar vistas
        nameInput = view.findViewById(R.id.name_input);
        predictButton = view.findViewById(R.id.predict_button);
        resultText = view.findViewById(R.id.result_text);
        genderImage = view.findViewById(R.id.gender_image);

        // Inicializar el ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Configurar botón
        predictButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            if (!name.isEmpty()) {
                // Ejecutar la tarea en un hilo separado
                executorService.execute(() -> {
                    String result = getGenderPrediction(name);
                    getActivity().runOnUiThread(() -> updateUI(result));
                });
            }
        });

        return view;
    }

    private String getGenderPrediction(String name) {
        String apiUrl = "https://api.genderize.io/?name=" + name;
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
                String gender = jsonObject.getString("gender");

                // Mostrar el género
                resultText.setText("Género estimado: " + gender);

                // Mostrar imagen y color basado en el género
                if ("male".equals(gender)) {
                    genderImage.setImageResource(R.drawable.azul);  // Imagen azul
                    resultText.setTextColor(Color.BLUE);
                } else if ("female".equals(gender)) {
                    genderImage.setImageResource(R.drawable.rosa);  // Imagen rosa
                    resultText.setTextColor(Color.BLUE);
                } else {
                    resultText.setText("No se pudo determinar el género.");
                    genderImage.setImageResource(0); // Limpiar la imagen
                    resultText.setTextColor(Color.MAGENTA); // Color por defecto
                }

            } catch (JSONException e) {
                e.printStackTrace();
                resultText.setText("Error al analizar los datos.");
                genderImage.setImageResource(0); // Limpiar la imagen
                resultText.setTextColor(Color.BLACK); // Color por defecto
            }
        } else {
            resultText.setText("No se pudo obtener la información.");
            genderImage.setImageResource(0); // Limpiar la imagen
            resultText.setTextColor(Color.BLACK); // Color por defecto
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cerrar el ExecutorService cuando el fragmento es destruido
        executorService.shutdown();
    }
}