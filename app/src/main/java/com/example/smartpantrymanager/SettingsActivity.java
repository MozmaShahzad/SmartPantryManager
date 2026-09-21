package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//simple settings screen - toggle for expiring soon alerts
//saved using SharedPreferences so it persists between app launches

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SwitchCompat expirySwitch =findViewById(R.id.expiryAlertSwitch);

        //load saved value (defaults to on if never set before)
        boolean isEnabled = prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
        expirySwitch.setChecked(isEnabled);

        //save whenever the user toggles it
        expirySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS,isChecked).apply();
            Toast.makeText(this,
                    isChecked ? "Expiry alerts on" : "Expiry alerts off",
                    Toast.LENGTH_SHORT).show();
        });

        //Bottom nav setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_pantry) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class ));
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(SettingsActivity.this,SuggestedRecipesActivity.class ));
                return true;
            } else if(id == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }
}
