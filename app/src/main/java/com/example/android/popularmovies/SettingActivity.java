package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 *  A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * Created by safwanx on 10/30/16.
 */

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add general preferences defined in R.xml.general_prefs xml file.
        addPreferencesFromResource(R.xml.general_prefs);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sorting_order_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference)
    {
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
        ListPreference listPreference = (ListPreference)preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);

        if( prefIndex >= 0 )
        {
            //Set the summary field
            listPreference.setSummary(listPreference.getEntries()[prefIndex]);
        }
        return true;
    }
}
