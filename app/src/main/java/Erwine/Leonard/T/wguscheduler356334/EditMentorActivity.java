package Erwine.Leonard.T.wguscheduler356334;

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

import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class EditMentorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    private static final String STATE_KEY_NOTIFIED_SHARING_DISABLED = "EditMentorActivity:notifiedSharingDisabled";
    private static final String STATE_KEY_NOTIFYING_SHARING_DISABLED = "EditMentorActivity:notifyingSharingDisabled";

    private final CompositeDisposable transientCompositeDisposable;
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
        transientCompositeDisposable = new CompositeDisposable();
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
        transientCompositeDisposable.clear();
        waitDialog = new AlertHelper(R.drawable.dialog_busy, R.string.title_loading, R.string.message_please_wait, this).createDialog();
        waitDialog.show();
        transientCompositeDisposable.add(viewModel.restoreState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onLoadSuccess, this::onLoadFailed));
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
            subscriptionCompositeDisposable.add(viewModel.getValid().subscribe(this::onCanSaveChanged));
        } else {
            subscriptionCompositeDisposable.add(viewModel.getTitleFactory().subscribe(f -> setTitle(f.apply(getResources()))));
            shareFloatingActionButton.setOnClickListener(this::onShareFloatingActionButtonClick);
            deleteFloatingActionButton.setOnClickListener(this::onDeleteFloatingActionButtonClick);
            subscriptionCompositeDisposable.add(viewModel.getCanSaveObservable().subscribe(this::onCanSaveChanged));
            subscriptionCompositeDisposable.add(viewModel.getCanShareObservable().subscribe(this::onCanShareChanged));
        }
        subscriptionCompositeDisposable.add(viewModel.getNameValid().subscribe(this::onNameValidChanged));
        subscriptionCompositeDisposable.add(viewModel.getContactValid().subscribe(this::onContactValidChanged));
        subscriptionCompositeDisposable.add(viewModel.getCurrentName().subscribe(this::onNameChanged));
        if (notifyingSharingDisabled) {
            notifyingSharingDisabled = notifiedSharingDisabled = false;
            showSharingDisabledSnackbar();
        }
    }

    private void onCanSaveChanged(boolean canSave) {
        Log.d(LOG_TAG, "Enter onCanSaveChanged(" + canSave + ")");
        saveFloatingActionButton.setEnabled(canSave);
    }

    private void onCanShareChanged(boolean canShare) {
        Log.d(LOG_TAG, "Enter onCanShareChanged(" + canShare + ")");
        if (canShare == shareFloatingActionButton.isEnabled()) {
            return;
        }
        shareFloatingActionButton.setEnabled(canShare);
        if (canShare) {
            if (null != snackBar) {
                if (snackBar.isShownOrQueued()) {
                    snackBar.dismiss();
                }
                snackBar = null;
            }
        } else {
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

//    private void onChangedChanged(Boolean isChanged) {
//        CompositeDisposable d = new CompositeDisposable();
//        d.add(viewModel.getValid().last(false).subscribe(isValid -> {
//            if (Boolean.TRUE.equals(isChanged)) {
//                Log.d(LOG_TAG, "Enter onChangedChanged(true); isValid=" + ToStringBuilder.toEscapedString(isValid) + "; disabling shareFloatingActionButton; calling saveFloatingActionButton.setEnabled(" + Boolean.TRUE.equals(isValid) + ")");
//                saveFloatingActionButton.setEnabled(Boolean.TRUE.equals(isValid));
//                setSharingEnabled(false);
//            } else {
//                Log.d(LOG_TAG, "Enter onChangedChanged(" + ToStringBuilder.toEscapedString(isChanged) + "); isValid=" + ToStringBuilder.toEscapedString(isValid) + "; disabling saveFloatingActionButton; calling shareFloatingActionButton.setEnabled(" + Boolean.TRUE.equals(isValid) + ")");
//                saveFloatingActionButton.setEnabled(false);
//                setSharingEnabled(Boolean.TRUE.equals(isValid));
//            }
//        }));
//    }

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
        transientCompositeDisposable.clear();
        transientCompositeDisposable.add(viewModel.save(false).subscribe(this::onSaveMentorCompleted, this::onSaveMentorError));
    }

    private void onShareFloatingActionButtonClick(View view) {
        // TODO: Implement onShareFloatingActionButton
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_mentor, R.string.message_delete_mentor_confirm, this).showYesNoDialog(() -> {
            transientCompositeDisposable.clear();
            transientCompositeDisposable.add(viewModel.delete(false).subscribe(this::onDeleteMentorFinished, this::onDeleteMentorError));
        }, null);
    }

    private void onSaveMentorCompleted(@NonNull ValidationMessage.ResourceMessageResult messages) {
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
                            transientCompositeDisposable.clear();
                            transientCompositeDisposable.add(viewModel.save(true).subscribe(this::onSaveMentorCompleted, this::onSaveMentorError));
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

    private void onDeleteMentorFinished(ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Resources resources = getResources();
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_delete_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            transientCompositeDisposable.clear();
                            transientCompositeDisposable.add(viewModel.delete(true).subscribe(this::onDeleteMentorFinished, this::onDeleteMentorError));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_delete_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onDeleteMentorError(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this)
                    .showYesNoCancelDialog(this::finish, () -> {
                        transientCompositeDisposable.clear();
                        transientCompositeDisposable.add(viewModel.save(false).subscribe(this::onSaveMentorCompleted, this::onSaveMentorError));
                    }, null);
        } else {
            finish();
        }
    }

}