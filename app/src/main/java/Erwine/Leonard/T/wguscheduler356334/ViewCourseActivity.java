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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertDialog;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.ViewCoursePagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

/**
 * Views course information in 2 tabs: {@link Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment} and {@link Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment}.
 * This initializes the shared view model {@link EditCourseViewModel}.
 */
public class ViewCourseActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewCourseActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;
    @SuppressWarnings("FieldCanBeLocal")
    private ViewCoursePagerAdapter adapter;
    private AlertDialog waitDialog;
    private FloatingActionButton addAssessmentFloatingActionButton;
    private FloatingActionButton addAlertFloatingActionButton;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;

    public ViewCourseActivity() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        addAssessmentFloatingActionButton = findViewById(R.id.addAssessmentFloatingActionButton);
        addAlertFloatingActionButton = findViewById(R.id.addAlertFloatingActionButton);
        shareFloatingActionButton = findViewById(R.id.shareFloatingActionButton);
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        deleteFloatingActionButton = findViewById(R.id.deleteFloatingActionButton);
        viewModel = new ViewModelProvider(this).get(EditCourseViewModel.class);
        viewModel.getTitleFactoryLiveData().observe(this, f -> setTitle(f.apply(getResources())));
        compositeDisposable.clear();
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        compositeDisposable.add(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onEntityLoadSucceeded, this::onEntityLoadFailed));
    }

    @Override
    public void onBackPressed() {
        confirmSave();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            confirmSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmSave() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () -> {
                compositeDisposable.clear();
                compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveCourseCompleted, this::onSaveCourseError));
            }, null);
        } else {
            finish();
        }
    }

    private void onEntityLoadSucceeded(CourseDetails entity) {
        waitDialog.dismiss();
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, (viewModel.isFromInitializedState()) ? R.string.message_course_not_found : R.string.message_course_not_restored, this).showDialog(this::finish);
            return;
        }
        long courseId = entity.getId();
        if (ID_NEW == courseId) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_course_id_not_specified, this).showDialog(this::finish);
        } else {
            adapter = new ViewCoursePagerAdapter(this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(adapter);
            TabLayout tabs = findViewById(R.id.courseListTabLayout);
            tabs.setupWithViewPager(viewPager);
            addAssessmentFloatingActionButton.setOnClickListener(this::onAddAssessmentFloatingActionButtonClick);
            addAlertFloatingActionButton.setOnClickListener(this::onAddAlertFloatingActionButtonClick);
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButton);
            saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
        }
    }

    private void onAddAssessmentFloatingActionButtonClick(View view) {
        LocalDate d = viewModel.getActualEnd();
        if (null == d && null == (d = viewModel.getExpectedEnd())) {
            d = LocalDate.now();
        }
        EditAssessmentViewModel.startAddAssessmentActivity(this, viewModel.getId(), d);
    }

    private void onAddAlertFloatingActionButtonClick(View view) {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(
                    this::finish,
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveForNewAlertFinished, this::onSaveCourseError));
                        finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.newCourseAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
        }
    }

    private void onSaveForNewAlertFinished(ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            EditAlertDialog dlg = EditAlertViewModel.newCourseAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveForNewAlertFinished, this::onSaveCourseError));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onShareFloatingActionButton(View view) {
        // TODO: Implement onShareFloatingActionButton
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveCourseCompleted, this::onSaveCourseError));
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_course, R.string.message_delete_course_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete(false).subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onEntityLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage())
                .showDialog(this::finish);
    }

    private void onSaveCourseCompleted(@NonNull ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveCourseCompleted, this::onSaveCourseError));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveCourseError(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, this, R.string.format_message_save_error, throwable.getMessage())
                .showDialog();
    }

    private void onDeleteSucceeded(ValidationMessage.ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        if (messages.isSucceeded()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_delete_warning).setIcon(R.drawable.dialog_warning)
                        .setMessage(messages.join("\n", resources))
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.delete(true).subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_delete_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

}