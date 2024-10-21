package com.backpack.couteau_tarea6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.google.android.material.navigationrail.NavigationRailView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the initial fragment (Toolbox)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ToolboxFragment()).commit();
        }

        // Setup the Navigation Rail View
        NavigationRailView navigationRail = findViewById(R.id.navigation_rail);
        navigationRail.setOnItemSelectedListener(navListener);
    }

    // Listener for fragment navigation
    private final NavigationRailView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_toolbox:
                selectedFragment = new ToolboxFragment();
                break;
            case R.id.nav_gender:
                selectedFragment = new GenderPredictionFragment();
                break;
            case R.id.nav_age:
                selectedFragment = new AgePredictionFragment();
                break;
            case R.id.nav_universities:
                selectedFragment = new UniversitiesFragment();
                break;
            case R.id.nav_weather:
                selectedFragment = new WeatherFragment();
                break;
            case R.id.nav_news:
                selectedFragment = new NewsFragment();
                break;
            case R.id.nav_about:
                selectedFragment = new AboutFragment();
                break;

            default:
                return false;
        }

        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        return true;
    };
}
