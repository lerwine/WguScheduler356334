package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;

public class AddCourseActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddCourseActivity.class.getName();

    private EditCourseViewModel viewModel;
    private AlertDialog waitDialog;
    private FloatingActionButton saveFloatingActionButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddCourseActivity() {
        Log.d(LOG_TAG, "Constructing AddCourseActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        viewModel = new ViewModelProvider(this).get(EditCourseViewModel.class);
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        OneTimeObservers.subscribeOnce(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()), this::onCourseLoadSuccess, this::onCourseLoadFailed);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter onCreate");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            confirmSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "Enter onCreate");
        confirmSave();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter onSaveInstanceState");
        viewModel.saveViewModelState(outState);
        super.onSaveInstanceState(outState);
    }

    private void confirmSave() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () ->
                    OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveOperationFinished, this::onSaveFailed), null);
        } else {
            finish();
        }
    }

    private void onCourseLoadSuccess(CourseDetails entity) {
        waitDialog.dismiss();
        if (null != entity) {
            Log.d(LOG_TAG, String.format("Loaded %s", entity));
            saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
        } else {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_course_not_restored, this).showDialog(this::finish);
        }
    }

    private void onCourseLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage()).showDialog(this::finish);
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveImageButtonClick");
        OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveOperationFinished, this::onSaveFailed);
    }

    private void onSaveOperationFinished(@NonNull ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            Intent intent = new Intent();
            intent.putExtra(EditCourseViewModel.EXTRA_KEY_COURSE_ID, viewModel.getId());
            setResult(1, intent);
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            OneTimeObservers.subscribeOnce(viewModel.save(true), this::onSaveOperationFinished, this::onSaveFailed);
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, this, R.string.format_message_save_error, throwable.getMessage())
                .showDialog();
    }

}