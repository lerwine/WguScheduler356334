package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

public class ManageDataActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final DbLoader dbLoader;

    public ManageDataActivity() {
        dbLoader = DbLoader.getInstance(getApplication());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.dbIntegrityCheckButton).setOnClickListener(this::onDbIntegrityCheckButtonClick);
        findViewById(R.id.resetDatabaseButton).setOnClickListener(this::onResetDatabaseButtonClick);
        findViewById(R.id.addSampleDataButton).setOnClickListener(this::onAddSampleDataButtonClick);
    }

    private void onDbIntegrityCheckButtonClick(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_database_integrity_check)
                .setMessage(R.string.message_in_progress)
                .setCancelable(false).create();
        dialog.show();
        compositeDisposable.clear();
        compositeDisposable.add(dbLoader.checkDbIntegrity().subscribe((s) -> {
            dialog.dismiss();
            if (null == s || s.trim().isEmpty()) {
                new AlertHelper(R.drawable.dialog_success, R.string.title_database_integrity_check, R.string.validation_succeeded, this).showDialog();
            } else {
                new AlertHelper(R.drawable.dialog_error, R.string.title_database_integrity_check, s, this).showDialog();
            }
        }, throwable -> {
            dialog.dismiss();
            String s = throwable.getMessage();
            new AlertHelper(R.drawable.dialog_error, R.string.title_database_integrity_check, (null == s || s.trim().isEmpty()) ? throwable.getClass().getName() : s, this).showDialog();
        }));
    }

    private void onResetDatabaseButtonClick(View view) {
        new AlertHelper(R.drawable.dialog_warning, R.string.command_reset_database, R.string.message_reset_db_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(dbLoader.resetDatabase().subscribe(this::finish, (throwable) -> {
                        Log.e(getClass().getName(), "Error on dbLoader.resetDatabase()", throwable);
                        new AlertHelper(R.drawable.dialog_error, R.string.title_reset_db_error, getString(R.string.format_message_reset_db_error, throwable.getMessage()), this)
                                .showDialog();
                    }
            ));
        }, null);
    }

    private void onAddSampleDataButtonClick(View view) {
        new AlertHelper(R.drawable.dialog_warning, R.string.command_reset_with_sample_data, R.string.message_add_sample_data_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(dbLoader.populateSampleData(getResources()).subscribe(this::finish, (throwable) -> {
                        Log.e(getClass().getName(), "Error on dbLoader.populateSampleData()", throwable);
                        new AlertHelper(R.drawable.dialog_error, R.string.title_add_sample_data_error, getString(R.string.format_message_add_sample_data_error, throwable.getMessage()), this)
                                .showDialog();
                    }
            ));
        }, null);
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

}