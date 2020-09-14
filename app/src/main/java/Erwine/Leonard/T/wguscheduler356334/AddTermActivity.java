package Erwine.Leonard.T.wguscheduler356334;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.ui.term.TermViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class AddTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddTermActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private TermViewModel mViewModel;
    private ImageButton saveImageButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddTermActivity() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.AddTermActivity");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        saveImageButton = findViewById(R.id.saveImageButton);
        saveImageButton.setOnClickListener(this::onSaveTermImageButtonClick);
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelTermEditImageButtonClick);
        mViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        mViewModel.getSavableLiveData().observe(this, this::onCanSaveLiveDataChanged);
    }

    private void onCanSaveLiveDataChanged(Boolean canSave) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onCanSaveLiveDataChanged(canSave: %s)", canSave));
        saveImageButton.setEnabled(canSave);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onSaveInstanceState");
        mViewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onSaveTermImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onSaveTermImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(mViewModel.save().subscribe(this::onDbOperationSucceeded, this::onSaveFailed));
    }

    private void onDbOperationSucceeded() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onDbOperationSucceeded");
        finish();
    }

    private void onSaveFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onSaveFailed");
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onCancelTermEditImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onCancelTermEditImageButtonClick");
        finish();
    }

}