package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.ViewTermPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObserve;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentFragment.FORMATTER;

public class ViewTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();

    @SuppressWarnings("FieldCanBeLocal")
    private ViewTermPagerAdapter adapter;
    private EditTermViewModel editTermViewModel;
    private AlertDialog waitDialog;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton addFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;

    public ViewTermActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
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
        editTermViewModel = new ViewModelProvider(this).get(EditTermViewModel.class);
        editTermViewModel.getTitleFactoryLiveData().observe(this, f -> setTitle(f.apply(getResources())));
        OneTimeObserve.subscribeOnce(editTermViewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()), this::onEntityLoaded, this::onEntityLoadFailed);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        editTermViewModel.saveViewModelState(outState);
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

    private void onEntityLoaded(TermEntity entity) {
        waitDialog.dismiss();
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, (editTermViewModel.isFromInitializedState()) ? R.string.message_term_not_found : R.string.message_term_not_restored, this).showDialog(this::finish);
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
        }
    }

    private void onEntityLoadFailed(Throwable throwable) {
        waitDialog.dismiss();
        Log.e(LOG_TAG, "Error loading term", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog(this::finish);
    }

    private synchronized void onShareFloatingActionButtonClick(View view) {
        OneTimeObserve.observeOnce(editTermViewModel.getCoursesLiveData(), this, termCourseListItems -> {
            Resources resources = getResources();
            String s = editTermViewModel.getName();
            String t = resources.getString(R.string.format_term, s);
            int i = t.indexOf(':');
            StringBuilder sb = new StringBuilder((s.toLowerCase().startsWith(t.substring(0, i).toLowerCase())) ? s : t).append(" Report");
            String title = sb.toString();
            LocalDate date = editTermViewModel.getStart();
            if (null != date) {
                sb.append("\n\tStart: ").append(FORMATTER.format(date));
                if (null != (date = editTermViewModel.getEnd())) {
                    sb.append("; End: ").append(FORMATTER.format(date));
                }
            } else if (null != (date = editTermViewModel.getEnd())) {
                sb.append("\n\tEnd: ").append(FORMATTER.format(date));
            }
            if (!termCourseListItems.isEmpty()) {
                sb.append("\nCourses:").append(s);
                for (TermCourseListItem c : termCourseListItems) {
                    sb.append("\n\t").append(c.getNumber()).append(": ").append(c.getTitle());
                    date = c.getActualStart();
                    if (null != date) {
                        sb.append("\n\t\tStarted: ").append(FORMATTER.format(date));
                        if (null != (date = c.getActualEnd())) {
                            sb.append("; Ended: ").append(FORMATTER.format(date));
                        } else if (null != (date = c.getExpectedEnd())) {
                            sb.append("; Expected End: ").append(FORMATTER.format(date));
                        }
                    } else if (null != (date = c.getExpectedStart())) {
                        sb.append("\n\t\tExpected Start: ").append(FORMATTER.format(date));
                        if (null != (date = c.getActualEnd())) {
                            sb.append("; Ended: ").append(FORMATTER.format(date));
                        } else if (null != (date = c.getExpectedEnd())) {
                            sb.append("; Expected End: ").append(FORMATTER.format(date));
                        }
                    } else if (null != (date = c.getActualEnd())) {
                        sb.append("\n\t\tEnded: ").append(FORMATTER.format(date));
                    } else if (null != (date = c.getExpectedEnd())) {
                        sb.append("\n\t\tExpected End: ").append(FORMATTER.format(date));
                    }
                    sb.append("\n\t\tStatus:").append(resources.getString(c.getStatus().displayResourceId()));
                    s = c.getMentorName();
                    if (null != s && !s.isEmpty()) {
                        sb.append("\n\tMentor: ").append(s);
                    }
                }
            }
            if (!(s = editTermViewModel.getNotes()).trim().isEmpty()) {
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
        OneTimeObserve.observeOnce(editTermViewModel.getCoursesLiveData(), this, termCourseListItems -> {
            LocalDate nextStart = ComparisonHelper.maxWithinRange(editTermViewModel.getStart(), editTermViewModel.getEnd(), termCourseListItems.stream().map(t -> {
                LocalDate d = t.getActualEnd();
                return (null == d && null == (d = t.getExpectedEnd()) && null == (d = t.getActualStart())) ? t.getExpectedStart() : d;
            }), LocalDate::compareTo).map(t -> t.plusDays(1L)).orElseGet(() -> {
                LocalDate s = editTermViewModel.getStart();
                if (null != s) {
                    return s;
                }
                s = LocalDate.now();
                LocalDate e = editTermViewModel.getEnd();
                return (null != e && e.compareTo(s) < 0) ? e : s;
            });
            EditCourseViewModel.startAddCourseActivity(this, editTermViewModel.getId(), nextStart);
        });
    }

    private void onSaveFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveFloatingActionButtonClick");
        OneTimeObserve.subscribeOnce(editTermViewModel.save(false), this::onSaveTermComplete, this::onSaveTermError);
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_term, R.string.message_delete_term_confirm, this).showYesNoDialog(() ->
                OneTimeObserve.subscribeOnce(editTermViewModel.delete(false), this::onDeleteTermComplete, this::onDeleteTermError), null);
    }

    private void confirmSave() {
        if (editTermViewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () ->
                    OneTimeObserve.subscribeOnce(editTermViewModel.save(false), this::onSaveTermComplete, this::onSaveTermError), null);
        } else {
            finish();
        }
    }

    private void onSaveTermComplete(ValidationMessage.ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onSaveOperationFinished");
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
                            OneTimeObserve.subscribeOnce(editTermViewModel.save(true), this::onSaveTermComplete, this::onSaveTermError);
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

    private void onDeleteTermComplete(ValidationMessage.ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onDeleteTermComplete");
        if (messages.isSucceeded()) {
            finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_delete_warning).setIcon(R.drawable.dialog_warning)
                        .setMessage(resources.getString(R.string.format_message_delete_warning, messages.join("\n", resources)))
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            dialog.dismiss();
                            OneTimeObserve.subscribeOnce(editTermViewModel.delete(true), this::onDeleteTermComplete, this::onDeleteTermError);
                        })
                        .setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_delete_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onDeleteTermError(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting term", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

}