package Erwine.Leonard.T.wguscheduler356334;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

public class AddTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddTermActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditTermViewModel viewModel;
    private AlertDialog waitDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddTermActivity() {
        Log.d(LOG_TAG, "Constructing AddTermActivity");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveTermImageButtonClick);
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelTermEditImageButtonClick);
        viewModel = new ViewModelProvider(this).get(EditTermViewModel.class);
        compositeDisposable.clear();
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        compositeDisposable.add(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onTermLoadSuccess, this::onTermLoadFailed));
    }

    private void onTermLoadSuccess(TermEntity entity) {
        waitDialog.dismiss();
        if (null != entity) {
            Log.d(LOG_TAG, String.format("Loaded %s", entity));
        } else {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_term_not_restored, this).showDialog(this::finish);
        }
    }

    private void onTermLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading term", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage()).showDialog(this::finish);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            confirmSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        confirmSave();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter onSaveInstanceState");
        viewModel.saveViewModelState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onSaveTermImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveTermImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onSaveOperationFinished(@NonNull ValidationMessage.ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onDbOperationSucceeded");
        if (messages.isSucceeded()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(resources.getString(R.string.format_message_save_warning, messages.join("\n", resources)))
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            dialog.dismiss();
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
                        })
                        .setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources));
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving term", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), this).showDialog();
    }

    private void onCancelTermEditImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCancelTermEditImageButtonClick");
        finish();
    }

    private void confirmSave() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () -> {
                compositeDisposable.clear();
                compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
            }, null);
        } else {
            finish();
        }
    }

}