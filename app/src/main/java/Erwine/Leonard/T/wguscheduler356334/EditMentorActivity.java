package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentFragment.FORMATTER;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    private static final String STATE_KEY_NOTIFIED_SHARING_DISABLED = "EditMentorActivity:notifiedSharingDisabled";
    private static final String STATE_KEY_NOTIFYING_SHARING_DISABLED = "EditMentorActivity:notifyingSharingDisabled";

    // FIXME: This is probably not getting disposed/cleared. Need to move this to view model and clear when restoring from state
    private final CompositeDisposable subscriptionCompositeDisposable;
    private EditMentorViewModel viewModel;
    private EditText mentorNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailAddressEditText;
    private EditText notesEditText;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;
    private AlertDialog waitDialog;
    private boolean notifiedSharingDisabled = true;
    private boolean notifyingSharingDisabled = true;
    private Snackbar snackBar;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditMentorActivity() {
        Log.d(LOG_TAG, "Constructing EditMentorActivity");
        subscriptionCompositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter savedInstanceState");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (null != savedInstanceState && savedInstanceState.containsKey(STATE_KEY_NOTIFIED_SHARING_DISABLED)) {
            notifyingSharingDisabled = savedInstanceState.getBoolean(STATE_KEY_NOTIFYING_SHARING_DISABLED, false);
            if (notifyingSharingDisabled)
                notifiedSharingDisabled = savedInstanceState.getBoolean(STATE_KEY_NOTIFIED_SHARING_DISABLED, false);
            if (notifyingSharingDisabled) {
                showSharingDisabledSnackbar();
            }
        }
        mentorNameEditText = findViewById(R.id.mentorNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        notesEditText = findViewById(R.id.notesEditText);
        shareFloatingActionButton = findViewById(R.id.shareFloatingActionButton);
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        deleteFloatingActionButton = findViewById(R.id.deleteFloatingActionButton);
        viewModel = new ViewModelProvider(this).get(EditMentorViewModel.class);
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        OneTimeObservers.subscribeOnce(viewModel.restoreState(savedInstanceState, () -> getIntent().getExtras()), this::onLoadSuccess, this::onLoadFailed);
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
        Log.d(LOG_TAG, "Enter onBackPressed");
        verifySaveChanges();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter onSaveInstanceState");
        viewModel.saveState(outState);
        outState.putBoolean(STATE_KEY_NOTIFIED_SHARING_DISABLED, notifiedSharingDisabled);
        outState.putBoolean(STATE_KEY_NOTIFYING_SHARING_DISABLED, null != snackBar && snackBar.isShownOrQueued());
        super.onSaveInstanceState(outState);
    }

    private void onLoadSuccess(MentorEntity entity) {
        subscriptionCompositeDisposable.clear();
        waitDialog.dismiss();
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, (viewModel.isFromInitializedState()) ? R.string.message_mentor_not_found : R.string.message_mentor_not_found, this).showDialog(this::finish);
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", entity));
        onNameChanged(entity.getName());
        if (viewModel.isFromInitializedState()) {
            mentorNameEditText.setText(viewModel.getName());
            phoneNumberEditText.setText(viewModel.getPhoneNumber());
            emailAddressEditText.setText(viewModel.getEmailAddress());
            notesEditText.setText(viewModel.getNotes());
        } else {
            mentorNameEditText.setText(entity.getName());
            phoneNumberEditText.setText(entity.getPhoneNumber());
            emailAddressEditText.setText(entity.getEmailAddress());
            notesEditText.setText(entity.getNotes());
        }

        mentorNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        phoneNumberEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setPhoneNumber));
        emailAddressEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setEmailAddress));
        saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
        if (ID_NEW == entity.getId()) {
            shareFloatingActionButton.setVisibility(View.GONE);
            deleteFloatingActionButton.setVisibility(View.GONE);
            setTitle(R.string.title_activity_new_mentor);
            subscriptionCompositeDisposable.add(viewModel.getIsValid().subscribe(b -> saveFloatingActionButton.setEnabled(b)));
        } else {
            subscriptionCompositeDisposable.add(viewModel.getTitleFactory().subscribe(f -> setTitle(f.apply(getResources()))));
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
            subscriptionCompositeDisposable.add(viewModel.getCanSave().subscribe(b -> saveFloatingActionButton.setEnabled(b)));
            subscriptionCompositeDisposable.add(viewModel.getCanShare().subscribe(b -> shareFloatingActionButton.setEnabled(b)));
        }
        subscriptionCompositeDisposable.add(viewModel.getNameValid().subscribe(this::onNameValidChanged));
        subscriptionCompositeDisposable.add(viewModel.getContactValid().subscribe(this::onContactValidChanged));
        subscriptionCompositeDisposable.add(viewModel.getNormalizedName().subscribe(this::onNameChanged));
        if (notifyingSharingDisabled) {
            notifyingSharingDisabled = notifiedSharingDisabled = false;
            showSharingDisabledSnackbar();
        }
    }

    private void showSharingDisabledSnackbar() {
        if (notifyingSharingDisabled || notifiedSharingDisabled) {
            return;
        }
        notifyingSharingDisabled = notifiedSharingDisabled = true;
        snackBar = Snackbar.make(shareFloatingActionButton, "Sharing disabled until changes are saved", Snackbar.LENGTH_LONG);
        Resources resources = getResources();
        SpannableStringBuilder builder = new SpannableStringBuilder("DISMISS");
        builder.setSpan(new ForegroundColorSpan(resources.getColor(R.color.color_on_primary, null)), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        snackBar
                .setAction(Html.toHtml(builder, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), v -> {
                    snackBar.dismiss();
                    snackBar = null;
                })
                .setActionTextColor(resources.getColor(R.color.color_on_secondary, null))
                .setBackgroundTint(resources.getColor(R.color.color_primary_variant, null))
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        notifyingSharingDisabled = false;
                    }
                }).show();
    }

    private void onNameChanged(String s) {
        Log.d(LOG_TAG, "Enter onNameChanged(" + ToStringBuilder.toEscapedString(s) + ")");
        String v = getResources().getString(R.string.format_mentor, s);
        int i = v.indexOf(':');
        setTitle((i > 0 && s.startsWith(v.substring(0, i))) ? s : v);
    }

    private void onLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading mentor", throwable);
        waitDialog.dismiss();
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog(this::finish);
    }

    private void onNameValidChanged(boolean isValid) {
        Log.d(LOG_TAG, "Enter onNameValidChanged(" + isValid + ")");
        if (isValid) {
            Log.d(LOG_TAG, "Enter onNameValidChanged(true); calling mentorNameEditText.setError(null)");
            mentorNameEditText.setError(null);
        } else {
            Log.d(LOG_TAG, "Enter onNameValidChanged(" + ToStringBuilder.toEscapedString(isValid) + "); calling mentorNameEditText.setError(R.string.message_required, R.drawable.dialog_error)");
            mentorNameEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(this, R.drawable.dialog_error));
        }
    }

    private void onContactValidChanged(boolean isValid) {
        Log.d(LOG_TAG, "Enter onContactValidChanged(" + isValid + ")");
        if (isValid) {
            Log.d(LOG_TAG, "Enter onContactValidChanged(true); calling setError(null) on phoneNumberEditText and emailAddressEditText");
            phoneNumberEditText.setError(null);
            emailAddressEditText.setError(null);
        } else {
            Log.d(LOG_TAG, "Enter onContactValidChanged(" + ToStringBuilder.toEscapedString(isValid) + "); calling setError(R.string.message_phone_or_email_required, R.drawable.dialog_error) on phoneNumberEditText and emailAddressEditText");
            phoneNumberEditText.setError(getResources().getString(R.string.message_phone_or_email_required), AppCompatResources.getDrawable(this, R.drawable.dialog_error));
            emailAddressEditText.setError(getResources().getString(R.string.message_phone_or_email_required), AppCompatResources.getDrawable(this, R.drawable.dialog_error));
        }
    }

    private void onSaveFloatingActionButtonClick(View view) {
        OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveMentorCompleted, this::onSaveMentorError);
    }

    private void onShareFloatingActionButtonClick(View view) {
        OneTimeObservers.observeOnce(viewModel.getCoursesLiveData(), this, mentorCourseListItems -> {
            StringBuilder sb = new StringBuilder("Course Mentor Information\nName: ").append(viewModel.getName());
            String s = viewModel.getPhoneNumber();
            if (!s.isEmpty()) {
                sb.append("\nPhone Number: ").append(s);
            }
            s = viewModel.getEmailAddress();
            if (!s.isEmpty()) {
                sb.append("\nEmail Address: ").append(s);
            }
            if (!mentorCourseListItems.isEmpty()) {
                Resources resources = getResources();
                sb.append("\nCourses:");
                for (MentorCourseListItem course : mentorCourseListItems) {
                    sb.append("\n").append(course.getNumber()).append(": ").append(course.getTitle());
                    LocalDate date = course.getActualStart();
                    if (null != date) {
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
                    sb.append("\n\tStatus:").append(resources.getString(course.getStatus().displayResourceId()));
                    s = course.getTermName();
                    String t = resources.getString(R.string.format_term, s);
                    int i = t.indexOf(':');
                    sb.append("\n\t").append((s.toLowerCase().startsWith(t.substring(0, i).toLowerCase())) ? s : t);
                }
            }
            if (!(s = viewModel.getNotes()).trim().isEmpty()) {
                sb.append("\nMentor Notes:\n").append(s);
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Course Mentor Information: " + viewModel.getName());
            startActivity(shareIntent);
        });
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_mentor, R.string.message_delete_mentor_confirm, this).showYesNoDialog(() ->
                OneTimeObservers.observeOnce(viewModel.getAllAlerts(), alerts -> OneTimeObservers.subscribeOnce(viewModel.delete(false), new DeleteOperationListener(alerts))), null);
    }

    private void onSaveMentorCompleted(@NonNull ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onSaveOperationSucceeded");
        if (messages.isSucceeded()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Resources resources = getResources();
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            OneTimeObservers.subscribeOnce(viewModel.save(true), this::onSaveMentorCompleted, this::onSaveMentorError);
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveMentorError(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), this).showDialog();
    }

    private void verifySaveChanges() {
        OneTimeObservers.subscribeOnce(viewModel.getHasChanges(), hasChanges -> {
            if (hasChanges) {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this)
                        .showYesNoCancelDialog(this::finish, () -> OneTimeObservers.subscribeOnce(viewModel.getIsValid(), isValid -> {
                            if (!isValid) {
                                return;
                            }
                            OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveMentorCompleted, this::onSaveMentorError);
                        }), null);
            } else {
                finish();
            }
        });
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
                        CourseAlertBroadcastReceiver.cancelPendingAlert(a, EditMentorActivity.this);
                    }
                }
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditMentorActivity.this);
                Resources resources = getResources();
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_delete_warning)
                            .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
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
            Log.e(LOG_TAG, "Error deleting mentor", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, e.getMessage()), EditMentorActivity.this).showDialog();
        }
    }
}