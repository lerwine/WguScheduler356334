package Erwine.Leonard.T.wguscheduler356334;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditAssessmentActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddTermActivity.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    private final CompositeDisposable compositeDisposable;
    private EditAssessmentViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditAssessmentActivity() {
        Log.d(LOG_TAG, "Constructing EditAssessmentActivity");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        findViewById(R.id.alertImageButton).setOnClickListener(this::onAlertImageButtonClick);
        findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveImageButtonClick);
        findViewById(R.id.deleteImageButton).setOnClickListener(this::onDeleteImageButtonClick);
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            verifySaveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        verifySaveChanges();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(
                    this::finish,
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
                        finish();
                    }, null);
        } else {
            finish();
        }
    }

    private void onAlertImageButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.EditAssessmentActivity.onAlertImageButtonClick
    }

    private void onSaveImageButtonClick(View view) {
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onDeleteImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_assessment, R.string.message_delete_assessment_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onCancelImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCancelImageButtonClick");
        verifySaveChanges();
    }

    private void onSaveOperationFinished(List<Integer> messageIds) {
        Log.d(LOG_TAG, "Enter onSaveOperationFinished");
        if (messageIds.isEmpty()) {
            finish();
        } else {
            Resources resources = getResources();
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                    .setMessage(messageIds.stream().map(resources::getString).collect(Collectors.joining("; "))).setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), this)
                .showDialog(this::finish);
    }

    private void onDeleteSucceeded() {
        finish();
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

}