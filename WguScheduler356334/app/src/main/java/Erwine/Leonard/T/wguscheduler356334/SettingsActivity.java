package Erwine.Leonard.T.wguscheduler356334;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.time.LocalTime;

import Erwine.Leonard.T.wguscheduler356334.db.LocalTimeConverter;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.getLogTag(SettingsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        //        private final Preference.OnPreferenceChangeListener onPreferEmailChangeListener;
        private final Preference.OnPreferenceChangeListener prefAlertTimeChangeListener;

        public SettingsFragment() {
//            onPreferEmailChangeListener = this::onPreferenceChange;
            prefAlertTimeChangeListener = this::onAlertTimeChange;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Resources resources = getResources();
//            final SwitchPreference preference = findPreference(resources.getString(R.string.preference_prefer_email));
//            if (null != preference) {
//                preference.setOnPreferenceChangeListener(onPreferEmailChangeListener);
//            }
            final TimePreference timePreference = findPreference(resources.getString(R.string.preference_alert_time));
            if (null != timePreference) {
                timePreference.setOnPreferenceChangeListener(prefAlertTimeChangeListener);
                int minutes = timePreference.getTime();
                setSummary(timePreference, LocalTime.of(minutes / 60, minutes % 60));
            }
        }

//        @SuppressWarnings("SameReturnValue")
//        private boolean onPreferenceChange(Preference preference, Object newValue) {
//            DbLoader.getPreferEmail().postValue(null != newValue && (Boolean) newValue);
//            return true;
//        }

        private boolean onAlertTimeChange(Preference preference, Object newValue) {
            int value = (null == newValue) ? TimePreference.DEFAULT_VALUE : (int) newValue;
            LocalTime time = LocalTime.of(value / 60, value % 60);
//            DbLoader.getPreferAlertTime().postValue(time);
            setSummary(preference, time);
            return true;
        }

        private void setSummary(Preference preference, LocalTime time) {
            preference.setSummary(getResources().getString(R.string.format_alarm_time_summary, LocalTimeConverter.MEDIUM_FORMATTER.format(time)));
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                dialogFragment = TimePreferenceDialog.newInstance(preference.getKey());
            }

            // If it was one of our custom Preferences, show its dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getParentFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }
}