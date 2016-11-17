package com.example.android.popularmovies;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.general_prefs);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sorting_order_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        //Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        //Also trigger the listener immediately with the preference's
        //current value.
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        //For list preferences, look up the correct display value in the preference's
        //'entries' list (since they have separate labels/values).
        ListPreference listPreference = (ListPreference) preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);

        if (prefIndex >= 0) {
            //Set the summary field
            listPreference.setSummary(listPreference.getEntries()[prefIndex]);
        }
        return true;
    }

}
