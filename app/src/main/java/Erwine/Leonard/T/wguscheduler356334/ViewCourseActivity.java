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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertDialog;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.ViewCoursePagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentFragment.FORMATTER;

/**
 * Views course information in 2 tabs: {@link Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment} and {@link Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment}.
 * This initializes the shared view model {@link EditCourseViewModel}.
 */
public class ViewCourseActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewCourseActivity.class.getName();

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
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        OneTimeObservers.subscribeOnce(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()), this::onEntityLoadSucceeded, this::onEntityLoadFailed);
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
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () ->
                    OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveCourseCompleted, this::onSaveCourseError), null);
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
                        OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveForNewAlertFinished, this::onSaveCourseError);
                        finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.newCourseAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
        }
    }

    private void onSaveForNewAlertFinished(ResourceMessageResult messages) {
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
                            OneTimeObservers.subscribeOnce(viewModel.save(true), this::onSaveForNewAlertFinished, this::onSaveCourseError);
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
        OneTimeObservers.observeOnce(viewModel.getAssessments(), this, assessments -> {
            Resources resources = getResources();
            StringBuilder sb = new StringBuilder("Course ").append(viewModel.getNumber()).append(" Report: ").append(viewModel.getTitle());
            String title = sb.toString();
            LocalDate date = viewModel.getActualStart();
            if (null != date) {
                sb.append("\nStarted: ").append(FORMATTER.format(date));
                if (null != (date = viewModel.getActualEnd())) {
                    sb.append("; Ended: ").append(FORMATTER.format(date));
                } else if (null != (date = viewModel.getExpectedEnd())) {
                    sb.append("; Expected End: ").append(FORMATTER.format(date));
                }
            } else if (null != (date = viewModel.getExpectedStart())) {
                sb.append("\nExpected Start: ").append(FORMATTER.format(date));
                if (null != (date = viewModel.getActualEnd())) {
                    sb.append("; Ended: ").append(FORMATTER.format(date));
                } else if (null != (date = viewModel.getExpectedEnd())) {
                    sb.append("; Expected End: ").append(FORMATTER.format(date));
                }
            } else if (null != (date = viewModel.getActualEnd())) {
                sb.append("\nEnded: ").append(FORMATTER.format(date));
            } else if (null != (date = viewModel.getExpectedEnd())) {
                sb.append("\nExpected End: ").append(FORMATTER.format(date));
            }
            sb.append("\nStatus:").append(resources.getString(viewModel.getStatus().displayResourceId()));
            AbstractMentorEntity<?> mentorEntity = viewModel.getSelectedMentor();
            String s;
            if (null != mentorEntity) {
                sb.append("\nMentor: ").append(mentorEntity.getName());
                if (!(s = mentorEntity.getPhoneNumber()).isEmpty()) {
                    sb.append("\n\tPhone: ").append(s);
                    if (!(s = mentorEntity.getEmailAddress()).isEmpty()) {
                        sb.append("; Email: ").append(s);
                    }
                } else if (!(s = mentorEntity.getEmailAddress()).isEmpty()) {
                    sb.append("\n\tEmail:").append(s);
                }
            }
            AbstractTermEntity<?> termEntity = viewModel.getSelectedTerm();
            s = termEntity.getName();
            String t = resources.getString(R.string.format_term, s);
            int i = t.indexOf(':');
            sb.append("\n").append((s.toLowerCase().startsWith(t.substring(0, i).toLowerCase())) ? s : t);
            date = termEntity.getStart();
            if (null != date) {
                sb.append("\n\tStart: ").append(FORMATTER.format(date));
                if (null != (date = termEntity.getEnd())) {
                    sb.append("; End: ").append(FORMATTER.format(date));
                }
            } else if (null != (date = termEntity.getEnd())) {
                sb.append("\n\tEnd: ").append(FORMATTER.format(date));
            }
            if (!assessments.isEmpty()) {
                sb.append("\nAssessments:").append(s);
                for (AssessmentEntity a : assessments) {
                    sb.append("\n\t").append(resources.getString(a.getType().displayResourceId())).append(" ")
                            .append(a.getCode()).append(" Report");
                    s = a.getName();
                    if (null != s) {
                        sb.append(": ").append(s);
                    }
                    date = a.getCompletionDate();
                    if (null != date) {
                        sb.append("\n\t\tCompleted: ").append(FORMATTER.format(date));
                        if (null != (date = a.getGoalDate())) {
                            sb.append("; Goal Date: ").append(FORMATTER.format(date));
                        }
                    } else if (null != (date = a.getGoalDate())) {
                        sb.append("\n\t\tGoal Date: ").append(FORMATTER.format(date));
                    }
                    sb.append("\n\t\tStatus:").append(resources.getString(a.getStatus().displayResourceId()));
                }
            }
            if (!(s = viewModel.getNotes()).trim().isEmpty()) {
                sb.append("\nCourse Notes:\n").append(s);
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, title);
            startActivity(shareIntent);
        });
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveCourseCompleted, this::onSaveCourseError);
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_course, R.string.message_delete_course_confirm, this).showYesNoDialog(() ->
                OneTimeObservers.subscribeOnce(viewModel.delete(false), this::onDeleteSucceeded, this::onDeleteFailed), null);
    }

    private void onEntityLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage())
                .showDialog(this::finish);
    }

    private void onSaveCourseCompleted(@NonNull ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            // TODO: Need to see if any date changes have effected any alarms
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            OneTimeObservers.subscribeOnce(viewModel.save(true), this::onSaveCourseCompleted, this::onSaveCourseError);
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

    private void onDeleteSucceeded(ResourceMessageResult messages) {
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
                            OneTimeObservers.subscribeOnce(viewModel.delete(true), this::onDeleteSucceeded, this::onDeleteFailed);
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