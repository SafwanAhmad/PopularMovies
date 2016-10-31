package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add general preferences defined in R.xml.general_prefs xml file.
        addPreferencesFromResource(R.xml.general_prefs);
    }
}
