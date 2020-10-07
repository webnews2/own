package com.github.webnews2.own.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.github.webnews2.own.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * This fragment is used for storing and updating the in-app settings. At the moment they are not implemented on the
 * logic side of the app. The user interface for the settings is partially set up to demonstrate which options the user
 * would have. The missing settings and functionality will be implemented later on.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    /**
     * Handles what happens when the fragment's views and therefore the user interface is going to be created.
     *
     * @param savedInstanceState if non-null, fragment can be re-constructed from previous saved state
     * @param rootKey if non-null, this fragment should be rooted at PreferenceScreen with key
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Inflate the layout for this fragment
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        // Find preference by key and set change listener
        SwitchPreference prefShowLocation = findPreference("pref_games_show_location");
        if (prefShowLocation != null) prefShowLocation.setOnPreferenceChangeListener((preference, newValue) -> {
            Snackbar.make(getView(), "Preference changed to: " + newValue, Snackbar.LENGTH_LONG).show();
            return true;
        });

        // Find preference by key and set change listener
        SwitchPreference prefShowPlatforms = findPreference("pref_games_show_platforms");
        if (prefShowPlatforms != null) prefShowPlatforms.setOnPreferenceChangeListener((preference, newValue) -> {
            Snackbar.make(getView(), "Preference changed to: " + newValue, Snackbar.LENGTH_LONG).show();
            return true;
        });
    }
}