package Erwine.Leonard.T.wguscheduler356334;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Adds a new course using {@link Erwine.Leonard.T.wguscheduler356334.ui.course.CoursePropertiesFragment}.
 * This initializes the shared view model {@link EditCourseViewModel}.
 */
public class AddCourseActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddCourseActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddCourseActivity() {
        Log.d(LOG_TAG, "Constructing AddCourseActivity");
        compositeDisposable = new CompositeDisposable();
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
        findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveImageButtonClick);
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
        viewModel = new ViewModelProvider(this).get(EditCourseViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onCourseLoadSuccess, this::onCourseLoadFailed));
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
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () -> {
                compositeDisposable.clear();
                compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
            }, null);
        } else {
            finish();
        }
    }

    private void onCourseLoadSuccess(CourseDetails entity) {
        Log.d(LOG_TAG, "Enter onCourseLoadSuccess");
    }

    private void onCourseLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage()).showDialog(this::finish);
    }

    private void onSaveImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onCancelImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCancelImageButtonClick");
        confirmSave();
    }

    private void onSaveOperationFinished(@NonNull List<Integer> messageIds) {
        Log.d(LOG_TAG, String.format("Enter onSaveOperationFinished with %d messages", messageIds.size()));
        if (messageIds.isEmpty()) {
            finish();
        } else {
            Resources resources = getResources();
            new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, messageIds.stream().map(resources::getString).collect(Collectors.joining("; ")), this)
                    .showDialog();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, this, R.string.format_message_save_error, throwable.getMessage())
                .showDialog();
    }

}