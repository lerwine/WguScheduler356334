package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.ViewTermPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.LONG_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;


public class ViewTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.getLogTag(ViewTermActivity.class);

    @SuppressWarnings("FieldCanBeLocal")
    private ViewTermPagerAdapter adapter;
    private EditTermViewModel viewModel;
    private AlertDialog waitDialog;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton addFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;

    public ViewTermActivity() {
        Log.d(LOG_TAG, "Constructing");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        shareFloatingActionButton = findViewById(R.id.shareFloatingActionButton);
        addFloatingActionButton = findViewById(R.id.addFloatingActionButton);
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        deleteFloatingActionButton = findViewById(R.id.deleteFloatingActionButton);
        viewModel = new ViewModelProvider(this).get(EditTermViewModel.class);
        viewModel.getTitleFactory().observe(this, f -> toolbar.setTitle(f.apply(getResources())));
        ObserverHelper.subscribeOnce(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()), this, this::onEntityLoaded, this::onEntityLoadFailed);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        viewModel.saveViewModelState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
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

    private void onEntityLoaded(TermEntity entity) {
        Log.d(LOG_TAG, "Enter onEntityLoaded(" + entity + ")");
        waitDialog.dismiss();
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, (viewModel.isFromInitializedState()) ? R.string.message_term_not_found : R.string.message_term_not_restored, this).showDialog(this::finish);
            return;
        }
        long termId = entity.getId();
        if (ID_NEW == termId) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_term_id_not_specified, this).showDialog(this::finish);
        } else {
            adapter = new ViewTermPagerAdapter(termId, this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(adapter);
            TabLayout tabs = findViewById(R.id.viewTermTabLayout);
            tabs.setupWithViewPager(viewPager);
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButtonClick);
            addFloatingActionButton.setOnClickListener(this::onAddFloatingActionButtonClick);
            saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
            viewModel.getCanSave().observe(this, b -> {
                Log.d(LOG_TAG, "getCanSaveObservable().subscribe(" + b + ")");
                saveFloatingActionButton.setEnabled(b);
            });
            viewModel.getCanShare().observe(this, b -> {
                Log.d(LOG_TAG, "getCanShareObservable().subscribe(" + b + ")");
                shareFloatingActionButton.setEnabled(b);
            });
        }
    }

    private void onEntityLoadFailed(Throwable throwable) {
        waitDialog.dismiss();
        Log.e(LOG_TAG, "Error loading term", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog(this::finish);
    }

    private synchronized void onShareFloatingActionButtonClick(View view) {
        ObserverHelper.observeOnce(viewModel.getCoursesLiveData(), this, termCourseListItems -> {
            Resources resources = getResources();
            String s = viewModel.getName();
            String t = resources.getString(R.string.format_term, s);
            int i = t.indexOf(':');
            StringBuilder sb = new StringBuilder((s.toLowerCase().startsWith(t.substring(0, i).toLowerCase())) ? s : t).append(" Report");
            String title = sb.toString();
            LocalDate date = viewModel.getStart();
            if (null != date) {
                sb.append("\n\tStart: ").append(LONG_FORMATTER.format(date));
                if (null != (date = viewModel.getEnd())) {
                    sb.append("; End: ").append(LONG_FORMATTER.format(date));
                }
            } else if (null != (date = viewModel.getEnd())) {
                sb.append("\n\tEnd: ").append(LONG_FORMATTER.format(date));
            }
            if (!termCourseListItems.isEmpty()) {
                sb.append("\nCourses:").append(s);
                for (TermCourseListItem c : termCourseListItems) {
                    sb.append("\n\t").append(c.getNumber()).append(": ").append(c.getTitle());
                    date = c.getActualStart();
                    if (null != date) {
                        sb.append("\n\t\tStarted: ").append(LONG_FORMATTER.format(date));
                        if (null != (date = c.getActualEnd())) {
                            sb.append("; Ended: ").append(LONG_FORMATTER.format(date));
                        } else if (null != (date = c.getExpectedEnd())) {
                            sb.append("; Expected End: ").append(LONG_FORMATTER.format(date));
                        }
                    } else if (null != (date = c.getExpectedStart())) {
                        sb.append("\n\t\tExpected Start: ").append(LONG_FORMATTER.format(date));
                        if (null != (date = c.getActualEnd())) {
                            sb.append("; Ended: ").append(LONG_FORMATTER.format(date));
                        } else if (null != (date = c.getExpectedEnd())) {
                            sb.append("; Expected End: ").append(LONG_FORMATTER.format(date));
                        }
                    } else if (null != (date = c.getActualEnd())) {
                        sb.append("\n\t\tEnded: ").append(LONG_FORMATTER.format(date));
                    } else if (null != (date = c.getExpectedEnd())) {
                        sb.append("\n\t\tExpected End: ").append(LONG_FORMATTER.format(date));
                    }
                    sb.append("\n\t\tStatus:").append(resources.getString(c.getStatus().displayResourceId()));
                    s = c.getMentorName();
                    if (null != s && !s.isEmpty()) {
                        sb.append("\n\tMentor: ").append(s);
                    }
                }
            }
            if (!(s = viewModel.getNotes()).trim().isEmpty()) {
                sb.append("\nTerm Notes:\n").append(s);
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, title);
            startActivity(shareIntent);
        });
    }

    private synchronized void onAddFloatingActionButtonClick(View view) {
        ObserverHelper.observeOnce(viewModel.getCoursesLiveData(), this, termCourseListItems -> {
            LocalDate nextStart = ComparisonHelper.maxWithinRange(viewModel.getStart(), viewModel.getEnd(), termCourseListItems.stream().map(t -> {
                LocalDate d = t.getActualEnd();
                return (null == d && null == (d = t.getExpectedEnd()) && null == (d = t.getActualStart())) ? t.getExpectedStart() : d;
            }), LocalDate::compareTo).map(t -> t.plusDays(1L)).orElseGet(() -> {
                LocalDate s = viewModel.getStart();
                if (null != s) {
                    return s;
                }
                s = LocalDate.now();
                LocalDate e = viewModel.getEnd();
                return (null != e && e.compareTo(s) < 0) ? e : s;
            });
            EditCourseViewModel.startAddCourseActivity(this, NEW_COURSE_REQUEST_CODE, viewModel.getId(), nextStart);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_COURSE_REQUEST_CODE && null != data && data.hasExtra(EditCourseViewModel.EXTRA_KEY_COURSE_ID)) {
            long courseId = data.getLongExtra(EditCourseViewModel.EXTRA_KEY_COURSE_ID, 0L);
            EditCourseViewModel.startViewCourseActivity(this, courseId);
        }
    }

    private static final int NEW_COURSE_REQUEST_CODE = 1;

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        ObserverHelper.subscribeOnce(viewModel.save(false), this, this::onSaveTermComplete, this::onSaveTermError);
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteFloatingActionButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_term, R.string.message_delete_term_confirm, this).showYesNoDialog(() ->
                ObserverHelper.subscribeOnce(viewModel.getAllAlerts(), this,
                        alerts -> ObserverHelper.subscribeOnce(viewModel.delete(false), this, new DeleteOperationListener(alerts))), null);
    }

    private void confirmSave() {
        ObserverHelper.observeOnce(viewModel.getHasChanges(), this, hasChanges -> {
            if (hasChanges) {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this)
                        .showYesNoCancelDialog(this::finish, () -> ObserverHelper.observeOnce(viewModel.getIsValid(), this, isValid -> {
                            if (!isValid) {
                                return;
                            }
                            ObserverHelper.subscribeOnce(viewModel.save(false), this, this::onSaveTermComplete, this::onSaveTermError);
                        }), null);
            } else {
                finish();
            }
        });
    }

    private void onSaveTermComplete(@NonNull ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onSaveTermComplete");
        if (messages.isSucceeded()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(resources.getString(R.string.format_message_save_warning, messages.join("\n", resources))).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            dialog.dismiss();
                            ObserverHelper.subscribeOnce(viewModel.save(true), this, this::onSaveTermComplete, this::onSaveTermError);
                        })
                        .setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveTermError(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving term", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, this, R.string.format_message_save_error, throwable.getMessage())
                .showDialog();
    }

    private class DeleteOperationListener implements SingleObserver<ResourceMessageResult> {

        private final AlertListItem[] alerts;

        DeleteOperationListener(List<AlertListItem> alerts) {
            this.alerts = alerts.stream().filter(t -> null != t.getAlertDate()).toArray(AlertListItem[]::new);
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onSuccess(ResourceMessageResult messages) {
            Log.d(LOG_TAG, "Enter DeleteOperationListener.onSuccess");
            if (messages.isSucceeded()) {
                if (alerts.length > 0) {
                    for (AlertListItem a : alerts) {
                        CourseAlertBroadcastReceiver.cancelPendingAlert(a, ViewTermActivity.this);
                    }
                }
                finish();
            } else {
                Resources resources = getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewTermActivity.this);
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_delete_warning).setIcon(R.drawable.dialog_warning)
                            .setMessage(resources.getString(R.string.format_message_delete_warning, messages.join("\n", resources)))
                            .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                                dialog.dismiss();
                                ObserverHelper.subscribeOnce(viewModel.delete(true), ViewTermActivity.this, this);
                            })
                            .setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
                } else {
                    builder.setTitle(R.string.title_delete_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
                }
                AlertDialog dlg = builder.setCancelable(true).create();
                dlg.show();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(LOG_TAG, "Error deleting term", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, e.getMessage()), ViewTermActivity.this).showDialog();
        }
    }

}