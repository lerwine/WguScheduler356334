package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final Preference.OnPreferenceChangeListener onPreferEmailChangeListener;

        public SettingsFragment() {
            onPreferEmailChangeListener = this::onPreferenceChange;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final SwitchPreference preference = findPreference(getResources().getString(R.string.preference_prefer_email));
            if (null != preference) {
                preference.setOnPreferenceChangeListener(onPreferEmailChangeListener);
            }
        }

        @SuppressWarnings("SameReturnValue")
        private boolean onPreferenceChange(Preference preference, Object newValue) {
            DbLoader.getPreferEmailLiveData().postValue(null != newValue && (Boolean) newValue);
            return true;
        }

    }
}