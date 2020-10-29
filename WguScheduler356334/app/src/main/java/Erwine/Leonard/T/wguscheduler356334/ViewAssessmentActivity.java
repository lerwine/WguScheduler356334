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
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.AssessmentAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertDialog;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.ViewAssessmentPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.LONG_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class ViewAssessmentActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.getLogTag(ViewAssessmentActivity.class);

    private EditAssessmentViewModel viewModel;
    @SuppressWarnings("FieldCanBeLocal")
    private ViewAssessmentPagerAdapter adapter;
    private AlertDialog waitDialog;
    private FloatingActionButton addFloatingActionButton;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;

    public ViewAssessmentActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

        }
        addFloatingActionButton = findViewById(R.id.addFloatingActionButton);
        shareFloatingActionButton = findViewById(R.id.shareFloatingActionButton);
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        deleteFloatingActionButton = findViewById(R.id.deleteFloatingActionButton);
        viewModel = new ViewModelProvider(this).get(EditAssessmentViewModel.class);
        viewModel.getTitleFactoryLiveData().observe(this, f -> toolbar.setTitle(f.apply(getResources())));
        viewModel.getSubTitleLiveData().observe(this, toolbar::setSubtitle);
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        ObserverHelper.subscribeOnce(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()), this::onEntityLoadSucceeded, this::onEntityLoadFailed);
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

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void confirmSave() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () ->
                    ObserverHelper.observeOnce(viewModel.getAllAlerts(), alerts -> ObserverHelper.subscribeOnce(viewModel.save(false), new SaveOperationListener(alerts))), null);
        } else {
            finish();
        }
    }

    private void onEntityLoadSucceeded(AssessmentDetails entity) {
        waitDialog.dismiss();
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, (viewModel.isFromInitializedState()) ? R.string.message_assessment_not_found : R.string.message_assessment_not_restored, this).showDialog(this::finish);
            return;
        }
        long assessmentId = entity.getId();
        if (ID_NEW == assessmentId) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_assessment_id_not_specified, this).showDialog(this::finish);
        } else {
            adapter = new ViewAssessmentPagerAdapter(this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(adapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
            addFloatingActionButton.setOnClickListener(this::onAddFloatingActionButtonClick);
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButton);
            saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
        }
    }

    private void doAddAlert() {
        EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(viewModel.getId());
        dlg.show(getSupportFragmentManager(), null);
    }

    private void onAddFloatingActionButtonClick(View view) {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(
                    this::doAddAlert,
                    () -> ObserverHelper.subscribeOnce(viewModel.save(false), new SaveOperationListener() {
                        @Override
                        public void onSuccess(ResourceMessageResult messages) {
                            super.onSuccess(messages);
                            if (!messages.isError()) {
                                doAddAlert();
                            }
                        }
                    }), null);
        } else {
            doAddAlert();
        }
    }

    private void onShareFloatingActionButton(View view) {
        ObserverHelper.subscribeOnce(viewModel.getCurrentTerm(), termEntity -> {
            if (null == viewModel.getMentorId()) {
                onShareAssessment(termEntity, null);
            } else {
                ObserverHelper.subscribeOnce(viewModel.getCourseMentor(), mentorEntity -> onShareAssessment(termEntity, mentorEntity), throwable -> {
                    Log.e(LOG_TAG, "Error loading mentor", throwable);
                    new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog();
                });
            }
        }, throwable -> {
            Log.e(LOG_TAG, "Error loading term", throwable);
            new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog();
        });
    }

    private void onShareAssessment(TermEntity termEntity, MentorEntity mentorEntity) {
        Resources resources = getResources();
        StringBuilder sb = new StringBuilder(resources.getString(viewModel.getType().displayResourceId())).append(" ")
                .append(viewModel.getCode()).append(" Report");
        String title = sb.toString();
        String s = viewModel.getName();
        if (!s.isEmpty()) {
            sb.append(": ").append(s);
        }
        LocalDate date = viewModel.getCompletionDate();
        if (null != date) {
            sb.append("\nCompleted: ").append(LONG_FORMATTER.format(date));
            if (null != (date = viewModel.getGoalDate())) {
                sb.append("; Goal Date: ").append(LONG_FORMATTER.format(date));
            }
        } else if (null != (date = viewModel.getGoalDate())) {
            sb.append("\nGoal Date: ").append(LONG_FORMATTER.format(date));
        }
        sb.append("\nStatus:").append(resources.getString(viewModel.getStatus().displayResourceId()));
        AbstractCourseEntity<?> course = viewModel.getSelectedCourse();
        sb.append("\nCourse ").append(course.getNumber()).append(": ").append(course.getTitle())
                .append("\n\tStatus:").append(resources.getString(course.getStatus().displayResourceId()));
        if (null != (date = course.getActualStart())) {
            sb.append("\n\tStarted: ").append(LONG_FORMATTER.format(date));
            if (null != (date = course.getActualEnd())) {
                sb.append("; Ended: ").append(LONG_FORMATTER.format(date));
            } else if (null != (date = course.getExpectedEnd())) {
                sb.append("; Expected End: ").append(LONG_FORMATTER.format(date));
            }
        } else if (null != (date = course.getExpectedStart())) {
            sb.append("\n\tExpected Start: ").append(LONG_FORMATTER.format(date));
            if (null != (date = course.getActualEnd())) {
                sb.append("; Ended: ").append(LONG_FORMATTER.format(date));
            } else if (null != (date = course.getExpectedEnd())) {
                sb.append("; Expected End: ").append(LONG_FORMATTER.format(date));
            }
        } else if (null != (date = course.getActualEnd())) {
            sb.append("\n\tEnded: ").append(LONG_FORMATTER.format(date));
        } else if (null != (date = course.getExpectedEnd())) {
            sb.append("\n\tExpected End: ").append(LONG_FORMATTER.format(date));
        }
        if (null != mentorEntity) {
            sb.append("\nMentor:").append(mentorEntity.getName());
            if (!(s = mentorEntity.getPhoneNumber()).isEmpty()) {
                sb.append("\n\tPhone:").append(s);
                if (!(s = mentorEntity.getEmailAddress()).isEmpty()) {
                    sb.append("; Email:").append(s);
                }
            } else if (!(s = mentorEntity.getEmailAddress()).isEmpty()) {
                sb.append("\n\tEmail:").append(s);
            }
        }
        s = termEntity.getName();
        String t = resources.getString(R.string.format_term, s);
        int i = t.indexOf(':');
        sb.append("\n").append((s.toLowerCase().startsWith(t.substring(0, i).toLowerCase())) ? s : t);
        date = termEntity.getStart();
        if (null != date) {
            sb.append("\n\tStart: ").append(LONG_FORMATTER.format(date));
            if (null != (date = termEntity.getEnd())) {
                sb.append("; End: ").append(LONG_FORMATTER.format(date));
            }
        } else if (null != (date = termEntity.getEnd())) {
            sb.append("\n\tEnd: ").append(LONG_FORMATTER.format(date));
        }
        if (!(s = viewModel.getNotes()).trim().isEmpty()) {
            sb.append("\nAssessment Notes:\n").append(s);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, title);
        startActivity(shareIntent);
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        ObserverHelper.observeOnce(viewModel.getAllAlerts(), alerts -> ObserverHelper.subscribeOnce(viewModel.save(false), new SaveOperationListener(alerts)));
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_assessment, R.string.message_delete_assessment_confirm, this).showYesNoDialog(() ->
                ObserverHelper.observeOnce(viewModel.getAllAlerts(), alerts -> ObserverHelper.subscribeOnce(viewModel.delete(), new DeleteOperationListener(alerts))), null);
    }

    private void onEntityLoadFailed(Throwable throwable) {
        waitDialog.dismiss();
        Log.e(LOG_TAG, "Error loading course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage())
                .showDialog(this::finish);
    }

    private class SaveOperationListener implements SingleObserver<ResourceMessageResult> {

        private final boolean addingNewAlert;
        private final AssessmentAlert[] alertsBeforeSave;

        SaveOperationListener(List<AssessmentAlert> alertsBeforeSave) {
            addingNewAlert = false;
            this.alertsBeforeSave = alertsBeforeSave.stream().filter(t -> null != t.getAlertDate()).toArray(AssessmentAlert[]::new);
        }

        SaveOperationListener() {
            addingNewAlert = true;
            alertsBeforeSave = new AssessmentAlert[0];
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        private void updateAlerts(AssessmentAlert[] alertsAfterSave, LocalTime defaultAlertTime) {
            if (alertsBeforeSave.length > 0) {
                Map<Long, AssessmentAlert> beforeSaveMap = EntityHelper.mapById(Arrays.stream(alertsBeforeSave), t -> t.getLink().getAlertId());
                Map<Long, AssessmentAlert> afterSaveMap = EntityHelper.mapById(Arrays.stream(alertsAfterSave), t -> t.getLink().getAlertId());
                beforeSaveMap.keySet().forEach(k -> {
                    AssessmentAlert b = Objects.requireNonNull(beforeSaveMap.get(k));
                    if (!afterSaveMap.containsKey(k)) {
                        AssessmentAlertBroadcastReceiver.cancelPendingAlert(b.getLink(), b.getAlert().getNotificationId(), ViewAssessmentActivity.this);
                    }
                });
                afterSaveMap.keySet().forEach(k -> {
                    AssessmentAlert a = Objects.requireNonNull(afterSaveMap.get(k));
                    AlertEntity alert = a.getAlert();
                    LocalTime alertTime = alert.getAlertTime();
                    if (!(beforeSaveMap.containsKey(k) && Objects.equals(Objects.requireNonNull(beforeSaveMap.get(k)).getAlertDate(), a.getAlertDate()) &&
                            Objects.equals(Objects.requireNonNull(beforeSaveMap.get(k)).getAlert().getAlertTime(), alertTime))) {
                        AssessmentAlertBroadcastReceiver.setPendingAlert(LocalDateTime.of(a.getAlertDate(), (null == alertTime) ? defaultAlertTime : alertTime), a.getLink(),
                                alert.getNotificationId(), ViewAssessmentActivity.this);
                    }
                });
            } else {
                for (AssessmentAlert a : alertsAfterSave) {
                    AlertEntity alert = a.getAlert();
                    LocalTime alertTime = alert.getAlertTime();
                    AssessmentAlertBroadcastReceiver.setPendingAlert(LocalDateTime.of(a.getAlertDate(), (null == alertTime) ? defaultAlertTime : alertTime), a.getLink(),
                            alert.getNotificationId(), ViewAssessmentActivity.this);
                }
            }
        }

        @Override
        public void onSuccess(ResourceMessageResult messages) {
            if (messages.isSucceeded()) {
                if (addingNewAlert) {
                    EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(viewModel.getId());
                    dlg.show(getSupportFragmentManager(), null);
                } else {
                    ObserverHelper.observeOnce(viewModel.getAllAlerts(), alerts -> {
                        AssessmentAlert[] alertsAfterSave = alerts.stream().filter(t -> null != t.getAlertDate()).toArray(AssessmentAlert[]::new);
                        if (alertsAfterSave.length > 0) {
                            ObserverHelper.observeOnce(DbLoader.getPreferAlertTime(), ViewAssessmentActivity.this, defaultAlertTime -> {
                                updateAlerts(alertsAfterSave, defaultAlertTime);
                                finish();
                            });
                        } else {
                            if (alertsBeforeSave.length > 0) {
                                for (AssessmentAlert a : alertsBeforeSave) {
                                    AssessmentAlertBroadcastReceiver.cancelPendingAlert(a.getLink(), a.getAlert().getNotificationId(), ViewAssessmentActivity.this);
                                }
                            }
                            finish();
                        }
                    });
                }
            } else {
                Resources resources = getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewAssessmentActivity.this);
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_save_warning)
                            .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                            .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                                ObserverHelper.subscribeOnce(viewModel.save(true), this);
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
        public void onError(@NonNull Throwable e) {
            Log.e(LOG_TAG, "Error saving course", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, ViewAssessmentActivity.this, R.string.format_message_save_error, e.getMessage())
                    .showDialog();
        }
    }

    private class DeleteOperationListener implements SingleObserver<Integer> {

        private final AssessmentAlert[] alerts;

        DeleteOperationListener(List<AssessmentAlert> alerts) {
            this.alerts = alerts.stream().filter(t -> null != t.getAlertDate()).toArray(AssessmentAlert[]::new);
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onSuccess(@NonNull Integer count) {
            Log.d(LOG_TAG, "Enter DeleteOperationListener.onSuccess");
            if (count < 1) {
                new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.message_delete_term_fail), ViewAssessmentActivity.this).showDialog();
            } else {
                if (alerts.length > 0) {
                    for (AssessmentAlert a : alerts) {
                        AssessmentAlertBroadcastReceiver.cancelPendingAlert(a.getLink(), a.getAlert().getNotificationId(), ViewAssessmentActivity.this);
                    }
                }
                finish();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(LOG_TAG, "Error deleting assessment", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, e.getMessage()), ViewAssessmentActivity.this).showDialog();
        }
    }
}