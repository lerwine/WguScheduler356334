package Erwine.Leonard.T.wguscheduler356334;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import java.util.Optional;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.LONG_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();

    private EditMentorViewModel viewModel;
    private EditText mentorNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailAddressEditText;
    private EditText notesEditText;
    private FloatingActionButton shareFloatingActionButton;
    private FloatingActionButton saveFloatingActionButton;
    private FloatingActionButton deleteFloatingActionButton;
    private AlertDialog waitDialog;
    //    private boolean notifiedSharingDisabled = true;
//    private boolean notifyingSharingDisabled = true;
    private Snackbar snackBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditMentorActivity() {
        Log.d(LOG_TAG, "Constructing EditMentorActivity");
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
        super.onSaveInstanceState(outState);
    }

    private void onLoadSuccess(MentorEntity entity) {
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
            viewModel.getIsValidLiveData().observe(this, b -> saveFloatingActionButton.setEnabled(b));
        } else {
            viewModel.getTitleFactory().observe(this, f -> setTitle(f.apply(getResources())));
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
            viewModel.getCanSaveLiveData().observe(this, b -> saveFloatingActionButton.setEnabled(b));
            viewModel.getCanShareLiveData().observe(this, b -> shareFloatingActionButton.setEnabled(b));
        }
        viewModel.getNameValidLiveData().observe(this, this::onNameValidChanged);
        viewModel.getPhoneValidationMessageLiveData().observe(this, (Optional<ResourceMessageFactory> resourceMessageFactory) -> {
            if (resourceMessageFactory.isPresent()) {
                ResourceMessageFactory f = resourceMessageFactory.get();
                phoneNumberEditText.setError(f.apply(getResources()), AppCompatResources.getDrawable(this,
                        (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
            } else {
                phoneNumberEditText.setError(null);
            }
        });
        viewModel.getEmailValidationMessageLiveData().observe(this, (Optional<ResourceMessageFactory> resourceMessageFactory) -> {
            if (resourceMessageFactory.isPresent()) {
                ResourceMessageFactory f = resourceMessageFactory.get();
                emailAddressEditText.setError(f.apply(getResources()), AppCompatResources.getDrawable(this,
                        (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
            } else {
                emailAddressEditText.setError(null);
            }
        });
        viewModel.getNormalizedNameLiveData().observe(this, this::onNameChanged);
        viewModel.getSharingDisabledNotificationVisibleLiveData().observe(this, this::onSharingDisabledNotificationVisibleChanged);
    }

    private synchronized void onSharingDisabledNotificationVisibleChanged(Boolean showNotification) {
        Snackbar sb = snackBar;
        if (showNotification) {
            if (null == sb) {
                snackBar = Snackbar.make(shareFloatingActionButton, "Sharing disabled until changes are saved", Snackbar.LENGTH_LONG);
                Resources resources = getResources();
                SpannableStringBuilder builder = new SpannableStringBuilder("DISMISS");
                builder.setSpan(new ForegroundColorSpan(resources.getColor(R.color.color_on_primary, null)), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                snackBar.setAction(builder, v -> snackBar.dismiss())
                        .setActionTextColor(resources.getColor(R.color.color_on_secondary, null))
                        .setBackgroundTint(resources.getColor(R.color.color_primary_variant, null))
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @SuppressLint("SwitchIntDef")
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                switch (event) {
                                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION:
                                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT:
                                        EditMentorActivity.this.onSharingDisabledSnackbarDismissed();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
            }
        } else if (null != sb) {
            snackBar = null;
            sb.dismiss();
        }
    }

    private synchronized void onSharingDisabledSnackbarDismissed() {
        snackBar = null;
        viewModel.setSharingDisabledNotificationDismissed(true);
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
        if (isValid) {
            Log.d(LOG_TAG, "Enter onNameValidChanged(true); calling mentorNameEditText.setError(null)");
            mentorNameEditText.setError(null);
        } else {
            Log.d(LOG_TAG, "Enter onNameValidChanged(false); calling mentorNameEditText.setError(R.string.message_required, R.drawable.dialog_error)");
            mentorNameEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(this, R.drawable.dialog_error));
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
        OneTimeObservers.observeOnce(viewModel.getHasChangesLiveData(), this, hasChanges -> {
            if (hasChanges) {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this)
                        .showYesNoCancelDialog(this::finish, () -> OneTimeObservers.observeOnce(viewModel.getIsValidLiveData(), this, isValid -> {
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