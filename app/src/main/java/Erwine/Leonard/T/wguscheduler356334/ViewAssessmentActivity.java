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

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertDialog;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.ViewAssessmentPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentFragment.FORMATTER;

public class ViewAssessmentActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewAssessmentActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditAssessmentViewModel viewModel;
    @SuppressWarnings("FieldCanBeLocal")
    private ViewAssessmentPagerAdapter adapter;
    private AlertDialog waitDialog;
    private FloatingActionButton addFloatingActionButton;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;
    private TermEntity termEntity;

    public ViewAssessmentActivity() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment);
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
                compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
            }, null);
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

    private void onAddFloatingActionButtonClick(View view) {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(
                    this::finish,
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveForNewAlertFinished, this::onSaveFailed));
                        finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
        }
    }

    private void onSaveForNewAlertFinished(ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(viewModel.getId());
            dlg.show(getSupportFragmentManager(), null);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveForNewAlertFinished, this::onSaveFailed));
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
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.getCurrentTerm().subscribe(this::onTermLoadedForSharing, this::onLoadForSharingFailed));
    }

    private void onTermLoadedForSharing(TermEntity termEntity) {
        this.termEntity = termEntity;
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.getCourseMentor().subscribe(this::onCourseMentorLoadedForSharing, this::onLoadForSharingFailed));
    }

    private void onCourseMentorLoadedForSharing(MentorEntity mentorEntity) {
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
            sb.append("\nCompleted: ").append(FORMATTER.format(date));
            if (null != (date = viewModel.getGoalDate())) {
                sb.append("; Goal Date: ").append(FORMATTER.format(date));
            }
        } else if (null != (date = viewModel.getGoalDate())) {
            sb.append("\nGoal Date: ").append(FORMATTER.format(date));
        }
        sb.append("\nStatus:").append(resources.getString(viewModel.getStatus().displayResourceId()));
        AbstractCourseEntity<?> course = viewModel.getSelectedCourse();
        sb.append("\nCourse ").append(course.getNumber()).append(": ").append(course.getTitle())
                .append("\n\tStatus:").append(resources.getString(course.getStatus().displayResourceId()));
        if (null != (date = course.getActualStart())) {
            sb.append("\n\tStarted: ").append(FORMATTER.format(date));
            if (null != (date = course.getActualEnd())) {
                sb.append("; Ended: ").append(FORMATTER.format(date));
            } else if (null != (date = course.getExpectedEnd())) {
                sb.append("; Expected End: ").append(FORMATTER.format(date));
            }
        } else if (null != (date = course.getExpectedStart())) {
            sb.append("\n\tExpected Start: ").append(FORMATTER.format(date));
            if (null != (date = course.getActualEnd())) {
                sb.append("; Ended: ").append(FORMATTER.format(date));
            } else if (null != (date = course.getExpectedEnd())) {
                sb.append("; Expected End: ").append(FORMATTER.format(date));
            }
        } else if (null != (date = course.getActualEnd())) {
            sb.append("\n\tEnded: ").append(FORMATTER.format(date));
        } else if (null != (date = course.getExpectedEnd())) {
            sb.append("\n\tExpected End: ").append(FORMATTER.format(date));
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
            sb.append("\n\tStart: ").append(FORMATTER.format(date));
            if (null != (date = termEntity.getEnd())) {
                sb.append("; End: ").append(FORMATTER.format(date));
            }
        } else if (null != (date = termEntity.getEnd())) {
            sb.append("\n\tEnd: ").append(FORMATTER.format(date));
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

    private void onLoadForSharingFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading term and mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog();
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_assessment, R.string.message_delete_assessment_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onDeleteSucceeded(Integer count) {
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        if (null == count || count < 1) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.message_delete_term_fail), this).showDialog();
        } else {
            finish();
        }
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting assessment", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

    private void onEntityLoadFailed(Throwable throwable) {
        waitDialog.dismiss();
        Log.e(LOG_TAG, "Error loading course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, this, R.string.format_message_read_error, throwable.getMessage())
                .showDialog(this::finish);
    }

    private void onSaveOperationSucceeded(ValidationMessage.ResourceMessageResult messages) {
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
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
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