package Erwine.Leonard.T.wguscheduler356334;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class AddTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddTermActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditTermViewModel mViewModel;
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
        mViewModel = new ViewModelProvider(this).get(EditTermViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(mViewModel.restoreState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onTermLoadSuccess, this::onTermLoadFailed));
    }

    private void onTermLoadSuccess(TermEntity termEntity) {
        if (null == termEntity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
    }

    private void onTermLoadFailed(Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.title_read_error)
                .setMessage(getString(R.string.format_message_read_error, throwable.getMessage()))
                .setOnCancelListener(dialog -> finish())
                .setCancelable(true).create();
        dlg.show();
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
        compositeDisposable.add(mViewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onSaveOperationFinished(@NonNull List<Integer> messageIds) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onDbOperationSucceeded");
        if (messageIds.isEmpty()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                    .setMessage(messageIds.stream().map(resources::getString).collect(Collectors.joining("; "))).setCancelable(true).create();
            dlg.show();
        }
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