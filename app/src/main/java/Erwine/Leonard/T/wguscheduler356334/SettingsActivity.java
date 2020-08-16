package Erwine.Leonard.T.wguscheduler356334;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;

public class SettingsActivity extends AppCompatActivity {

    private final DbLoader dbLoader;

    public SettingsActivity() {
        dbLoader = DbLoader.getInstance(getApplication());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.settings, new SettingsFragment())
//                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.resetDatabaseButton).setOnClickListener(this::onResetDatabaseButtonClick);
        findViewById(R.id.addSampleDataButton).setOnClickListener(this::onAddSampleDataButtonClick);
    }

    @SuppressLint("CheckResult")
    private void onResetDatabaseButtonClick(View view) {
        new AlertDialog.Builder(this).setTitle(R.string.reset_database).setMessage(R.string.reset_db_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
            dbLoader.resetDatabase().subscribe(this::finish, (throwable) ->
                    new AlertDialog.Builder(this).setTitle(R.string.reset_db_error_title)
                            .setMessage(getString(R.string.reset_db_error_message, throwable.getMessage())).setCancelable(false).show()
            );
        }).setNegativeButton(R.string.no, null).show();
    }

    private void onAddSampleDataButtonClick(View view) {
        new AlertDialog.Builder(this).setTitle(R.string.add_sample_data).setMessage(R.string.add_sample_data_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
            dbLoader.populateSampleData().subscribe(this::finish, (throwable) ->
                    new AlertDialog.Builder(this).setTitle(R.string.add_sample_data_error_title)
                            .setMessage(getString(R.string.add_sample_data_error_message, throwable.getMessage())).setCancelable(false).show()
            );
        }).setNegativeButton(R.string.no, null).show();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}