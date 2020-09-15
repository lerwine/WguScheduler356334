package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.Values;
import io.reactivex.disposables.CompositeDisposable;

public class EditMentorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    public static final String EXTRAS_KEY_MENTOR_ID = "mentorId";

    private final CompositeDisposable compositeDisposable;
    private EditMentorViewModel viewModel;
    private EditText mentorNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailAddressEditText;
    private TextView mentorNotesTextView;
    private ImageButton saveMentorImageButton;
    private ImageButton deleteMentorImageButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditMentorActivity() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.EditMentorActivity");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mentorNameEditText = findViewById(R.id.mentorNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        mentorNotesTextView = findViewById(R.id.mentorNotesTextView);
        saveMentorImageButton = findViewById(R.id.saveMentorImageButton);
        deleteMentorImageButton = findViewById(R.id.deleteMentorImageButton);
        viewModel = new ViewModelProvider(this).get(EditMentorViewModel.class);
        viewModel.restoreState(savedInstanceState, () -> getIntent().getExtras());
        viewModel.getEntityLiveData().observe(this, this::onLoadSuccess);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.EditMentorActivity.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            verifySaveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        verifySaveChanges();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.EditMentorActivity.onSaveInstanceState");
        viewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onLoadSuccess(MentorEntity entity) {
        if (null == entity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", entity));

        if (viewModel.isFromInitializedState()) {
            mentorNameEditText.setText(viewModel.getName());
            phoneNumberEditText.setText(viewModel.getPhoneNumber());
            emailAddressEditText.setText(viewModel.getEmailAddress());
            mentorNotesTextView.setText(viewModel.getNotes());
        } else {
            mentorNameEditText.setText(entity.getName());
            phoneNumberEditText.setText(entity.getPhoneNumber());
            emailAddressEditText.setText(entity.getEmailAddress());
            mentorNotesTextView.setText(entity.getNotes());
        }

        mentorNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onMentorNameTextChanged));
        phoneNumberEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onPhoneNumberTextChanged));
        emailAddressEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onEmailAddressTextChanged));
        mentorNotesTextView.setOnClickListener(this::onMentorNotesEditTextClick);
        saveMentorImageButton.setOnClickListener(this::onSaveMentorImageButtonClick);
        if (null == viewModel.getId()) {
            deleteMentorImageButton.setVisibility(View.GONE);
            setTitle(R.string.title_activity_new_mentor);
        } else {
            setTitle(R.string.title_activity_edit_mentor);
            deleteMentorImageButton.setOnClickListener(this::onDeleteMentorImageButtonClick);
        }
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
        viewModel.getNameValidLiveData().observe(this, this::onNameValidChanged);
        viewModel.getContactValidLiveData().observe(this, this::onContactValidChanged);
        viewModel.getSavableLiveData().observe(this, this::onCanSaveLiveDataChanged);
    }

    private void onMentorNotesEditTextClick(View view) {
        EditText editText = new EditText(this);
        String text = viewModel.getNotes();
        editText.setText(text);
        editText.setSingleLine(false);
        editText.setVerticalScrollBarEnabled(true);
        editText.setBackgroundResource(android.R.drawable.edit_text);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_edit_notes))
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String s = editText.getText().toString();
                    if (!text.equals(s)) {
                        viewModel.onMentorNotesEditTextChanged(s);
                        mentorNotesTextView.setText(s);
                    }
                })
                .setCancelable(false).create();
        dlg.show();
    }

    private void onCanSaveLiveDataChanged(Boolean canSave) {
        saveMentorImageButton.setEnabled(canSave);
    }

    private void onNameValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            mentorNameEditText.setError(null);
        } else {
            mentorNameEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onContactValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            mentorNameEditText.setError(null);
        } else {
            mentorNameEditText.setError(getResources().getString(R.string.message_phone_or_email_required));
        }
    }

    private void onSaveMentorImageButtonClick(View view) {
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::finish, this::onSaveFailed));
    }

    private void onSaveFailed(Throwable throwable) {
        android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onDeleteMentorImageButtonClick(View view) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_mentor)
                .setMessage(R.string.message_delete_mentor_confirm)
                .setPositiveButton(R.string.response_yes, (dialogInterface, i1) -> {
                    compositeDisposable.clear();
                    compositeDisposable.add(viewModel.delete().subscribe(this::finish, this::onDeleteFailed));
                }).setNegativeButton(R.string.response_no, null).create();
        dialog.show();
    }

    private void onDeleteFailed(Throwable throwable) {
        android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_delete_error)
                .setMessage(getString(R.string.format_message_delete_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onCancelImageButtonClick(View view) {
        verifySaveChanges();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.title_discard_changes)
                    .setMessage(getString(R.string.message_discard_changes))
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        if (Values.notNullAnd(viewModel.getSavableLiveData().getValue())) {
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save().subscribe(this::finish, this::onSaveFailed));
                        }
                    })
                    .create();
            dlg.show();
        } else {
            finish();
        }
    }

}