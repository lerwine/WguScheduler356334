package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertDialog;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.ViewCoursePagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

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
                    OneTimeObservers.observeOnce(viewModel.getAllCourseAlerts(), alerts -> OneTimeObservers.subscribeOnce(viewModel.save(false), new SaveOperationListener(alerts))), null);
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
                        OneTimeObservers.subscribeOnce(viewModel.save(false), new SaveOperationListener());
                        finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.newCourseAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
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
        OneTimeObservers.observeOnce(viewModel.getAllCourseAlerts(), alerts -> OneTimeObservers.subscribeOnce(viewModel.save(false), new SaveOperationListener(alerts)));
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_course, R.string.message_delete_course_confirm, this).showYesNoDialog(() -> OneTimeObservers.observeOnce(viewModel.getAllAlerts(), alerts -> OneTimeObservers.subscribeOnce(viewModel.delete(false), new DeleteOperationListener(alerts))), null);
    }

    private void onEntityLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage())
                .showDialog(this::finish);
    }

    private class SaveOperationListener implements SingleObserver<ResourceMessageResult> {

        private final boolean addingNewAlert;
        private final CourseAlert[] alertsBeforeSave;


        SaveOperationListener(List<CourseAlert> alertsBeforeSave) {
            addingNewAlert = false;
            this.alertsBeforeSave = alertsBeforeSave.stream().filter(t -> null != t.getAlertDate()).toArray(CourseAlert[]::new);
        }

        SaveOperationListener() {
            addingNewAlert = true;
            this.alertsBeforeSave = new CourseAlert[0];
        }

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(ResourceMessageResult messages) {
            if (messages.isSucceeded()) {
                if (addingNewAlert) {
                    EditAlertDialog dlg = EditAlertViewModel.newCourseAlert(viewModel.getId());
                    dlg.show(getSupportFragmentManager(), null);
                } else {
                    OneTimeObservers.observeOnce(viewModel.getAllCourseAlerts(), alerts -> {
                        CourseAlert[] alertsAfterSave = alerts.stream().filter(t -> null != t.getAlertDate()).toArray(CourseAlert[]::new);
                        if (alertsAfterSave.length > 0) {
                            OneTimeObservers.subscribeOnce(DbLoader.getPreferAlertTime(), localTime -> {
                                if (alertsBeforeSave.length > 0) {
                                    Map<Long, CourseAlert> beforeSaveMap = EntityHelper.mapById(Arrays.stream(alertsBeforeSave), t -> t.getLink().getAlertId());
                                    Map<Long, CourseAlert> afterSaveMap = EntityHelper.mapById(Arrays.stream(alertsAfterSave), t -> t.getLink().getAlertId());
                                    beforeSaveMap.keySet().forEach(k -> {
                                        CourseAlert b = Objects.requireNonNull(beforeSaveMap.get(k));
                                        if (!afterSaveMap.containsKey(k)) {
                                            CourseAlertBroadcastReceiver.cancelPendingAlert(b.getLink(), ViewCourseActivity.this);
                                        }
                                    });
                                    afterSaveMap.keySet().forEach(k -> {
                                        CourseAlert a = Objects.requireNonNull(afterSaveMap.get(k));
                                        if (!(beforeSaveMap.containsKey(k) && Objects.equals(Objects.requireNonNull(beforeSaveMap.get(k)).getAlertDate(), a.getAlertDate()))) {
                                            CourseAlertBroadcastReceiver.setPendingAlert(LocalDateTime.of(a.getAlertDate(), localTime), a.getLink(), ViewCourseActivity.this);
                                        }
                                    });
                                } else {
                                    for (CourseAlert a : alertsAfterSave) {
                                        CourseAlertBroadcastReceiver.setPendingAlert(LocalDateTime.of(a.getAlertDate(), localTime), a.getLink(), ViewCourseActivity.this);
                                    }
                                }
                                finish();
                            });
                        } else if (alertsBeforeSave.length > 0) {
                            for (CourseAlert a : alertsBeforeSave) {
                                CourseAlertBroadcastReceiver.cancelPendingAlert(a.getLink(), ViewCourseActivity.this);
                            }
                            finish();
                        }
                    });
                }
            } else {
                Resources resources = getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewCourseActivity.this);
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_save_warning)
                            .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                            .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                                OneTimeObservers.subscribeOnce(viewModel.save(true), this);
                                dialog.dismiss();
                            }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
                } else {
                    builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
                }
                AlertDialog dlg = builder.setCancelable(true).create();
                dlg.show();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Error saving course", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, ViewCourseActivity.this, R.string.format_message_save_error, e.getMessage())
                    .showDialog();
        }
    }

    private class DeleteOperationListener implements SingleObserver<ResourceMessageResult> {

        private final AlertListItem[] alerts;

        DeleteOperationListener(List<AlertListItem> alerts) {
            this.alerts = alerts.stream().filter(t -> null != t.getAlertDate()).toArray(AlertListItem[]::new);
        }

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onSuccess(ResourceMessageResult messages) {
            Log.d(LOG_TAG, "Enter DeleteOperationListener.onSuccess");
            if (messages.isSucceeded()) {
                if (alerts.length > 0) {
                    for (AlertListItem a : alerts) {
                        CourseAlertBroadcastReceiver.cancelPendingAlert(a, ViewCourseActivity.this);
                    }
                }
                finish();
            } else {
                Resources resources = getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewCourseActivity.this);
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_delete_warning).setIcon(R.drawable.dialog_warning)
                            .setMessage(messages.join("\n", resources))
                            .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                                OneTimeObservers.subscribeOnce(viewModel.delete(true), this);
                                dialog.dismiss();
                            }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
                } else {
                    builder.setTitle(R.string.title_delete_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
                }
                AlertDialog dlg = builder.setCancelable(true).create();
                dlg.show();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Error deleting course", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, e.getMessage()), ViewCourseActivity.this).showDialog();
        }
    }
}