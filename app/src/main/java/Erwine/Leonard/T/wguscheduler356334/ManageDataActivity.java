package Erwine.Leonard.T.wguscheduler356334;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_database_integrity_check);
            if (null == s || s.trim().isEmpty()) {
                builder.setMessage(R.string.validation_succeeded).setIcon(R.drawable.star_icon);
            } else {
                builder.setMessage(s).setIcon(R.drawable.error_icon);
            }
            AlertDialog dlg = builder.setCancelable(false).setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss()).create();
            dlg.show();
        }, throwable -> {
            String s = throwable.getMessage();
            AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_database_integrity_check)
                    .setMessage((null == s || s.trim().isEmpty()) ? throwable.getClass().getName() : s)
                    .setIcon(R.drawable.error_icon)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss()).create();
            dlg.show();
        }));
    }

    @SuppressLint("CheckResult")
    private void onResetDatabaseButtonClick(View view) {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.command_reset_database).setMessage(R.string.message_reset_db_confirm).setPositiveButton(R.string.response_yes,
                (dialogInterface, i1) -> {
                    compositeDisposable.clear();
                    compositeDisposable.add(dbLoader.resetDatabase().subscribe(this::finish, (throwable) -> {
                                Log.e(getClass().getName(), "Error on dbLoader.resetDatabase()", throwable);
                                new AlertDialog.Builder(this).setTitle(R.string.title_reset_db_error)
                                        .setMessage(getString(R.string.format_message_reset_db_error, throwable.getMessage())).setCancelable(true).show();
                            }
                    ));
                }).setNegativeButton(R.string.response_no, null).create();
        dlg.show();
    }

    private void onAddSampleDataButtonClick(View view) {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.command_reset_with_sample_data).setMessage(R.string.message_add_sample_data_confirm).setPositiveButton(R.string.response_yes,
                (dialogInterface, i1) -> {
                    compositeDisposable.clear();
                    compositeDisposable.add(dbLoader.populateSampleData(getResources()).subscribe(this::finish, (throwable) -> {
                                Log.e(getClass().getName(), "Error on dbLoader.populateSampleData()", throwable);
                                new AlertDialog.Builder(this).setTitle(R.string.title_add_sample_data_error)
                                        .setMessage(getString(R.string.format_message_add_sample_data_error, throwable.getMessage())).setCancelable(true).show();
                            }
                    ));
                }).setNegativeButton(R.string.response_no, null).create();
        dlg.show();
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