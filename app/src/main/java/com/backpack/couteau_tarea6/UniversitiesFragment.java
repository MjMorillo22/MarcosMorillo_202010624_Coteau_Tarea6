package com.backpack.couteau_tarea6;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UniversitiesFragment extends Fragment {

    private EditText countryInput;
    private Button searchButton;
    private LinearLayout universitiesContainer;
    private ExecutorService executorService;

    // Define the base URL for the API
    private static final String API_URL = "http://universities.hipolabs.com/search?country=";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_universities, container, false);

        countryInput = view.findViewById(R.id.country_input);
        searchButton = view.findViewById(R.id.search_button);
        universitiesContainer = view.findViewById(R.id.universities_container);

        // Initialize the ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Set up the search button listener
        searchButton.setOnClickListener(v -> {
            String country = countryInput.getText().toString().trim();
            if (!country.isEmpty()) {
                searchUniversities(country);
            } else {
                // Inform the user to enter a country
                showMessage("Introducir nombre de Pais en ingles");
            }
        });

        return view;
    }

    private void searchUniversities(String country) {
        // Execute the task in a separate thread
        executorService.execute(() -> {
            String result = fetchUniversities(API_URL + Uri.encode(country));
            getActivity().runOnUiThread(() -> updateUniversitiesUI(result));
        });
    }

    private void updateUniversitiesUI(String result) {
        if (result != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                universitiesContainer.removeAllViews(); // Clear the container before adding new views

                if (jsonArray.length() == 0) {
                    TextView noResultsText = new TextView(getContext());
                    noResultsText.setText("No universities found for the entered country.");
                    universitiesContainer.addView(noResultsText);
                    return; // Return early if no results
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject university = jsonArray.getJSONObject(i);
                    String name = university.optString("name", "Unknown Name");

                    // Extract domains
                    JSONArray domainsArray = university.optJSONArray("domains");
                    String domain = (domainsArray != null && domainsArray.length() > 0) ? domainsArray.optString(0) : "No Domain Available";

                    JSONArray webPagesArray = university.optJSONArray("web_pages");
                    String website = (webPagesArray != null && webPagesArray.length() > 0) ? webPagesArray.optString(0) : "No Website Available";

                    // Add each university to the container
                    addUniversityItem(name, domain, website);
                }

            } catch (JSONException e) {
                Log.e("JSON Exception", e.toString());
                showMessage("Error parsing university data.");
            }
        } else {
            // Handle the case where no information is obtained
            universitiesContainer.removeAllViews();
            showMessage("Failed to retrieve information.");
        }
    }


    private String fetchUniversities(String apiUrl) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Check the response code
            int responseCode = urlConnection.getResponseCode();
            Log.d("API Response Code", String.valueOf(responseCode)); // Log the response code

            // If the response code is not 200 (OK), log the error
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("API Error", "Error: " + responseCode);
                return null; // Handle the error case
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("API Exception", e.toString()); // Log any exception
            return null;
        }

        return result.toString();
    }

    private void addUniversityItem(String name, String domain, String website) {
        View universityItemView = LayoutInflater.from(getContext()).inflate(R.layout.university_item, universitiesContainer, false);
        TextView nameText = universityItemView.findViewById(R.id.university_name_text);
        TextView domainText = universityItemView.findViewById(R.id.university_domain_text);
        TextView websiteText = universityItemView.findViewById(R.id.university_website_text);

        nameText.setText(name);
        domainText.setText(domain);
        websiteText.setText(website);

        // Set up the link to open the webpage
        websiteText.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });

        universitiesContainer.addView(universityItemView);
    }

    private void showMessage(String message) {
        // Display a message to the user
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Shut down the ExecutorService when the fragment is destroyed
        executorService.shutdown();
    }
}
