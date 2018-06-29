package jp.iwanagat85.overlaytranslator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setSummaries(sharedPreferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummaries(sharedPreferences);
    }

    private void setSummaries(final SharedPreferences preferences) {

        String[] languages = getContext().getResources().getStringArray(R.array.values_language_list);

        findPreference(Constant.KEY_TRANS_SOURCE_LNG)
                .setSummary(preferences.getString(Constant.KEY_TRANS_SOURCE_LNG, languages[0]));
        findPreference(Constant.KEY_TRANS_TARGET_LNG)
                .setSummary(preferences.getString(Constant.KEY_TRANS_TARGET_LNG, languages[1]));

    }

}