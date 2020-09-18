package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditMentorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditMentorViewModel viewModel;
    private EditText mentorNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailAddressEditText;
    private TextView mentorNotesTextView;
    private FloatingActionButton editMentorNotesFloatingActionButton;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mentorNameEditText = findViewById(R.id.mentorNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        mentorNotesTextView = findViewById(R.id.mentorNotesTextView);
        editMentorNotesFloatingActionButton = findViewById(R.id.editMentorNotesFloatingActionButton);
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

        mentorNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        phoneNumberEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setPhoneNumber));
        emailAddressEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setEmailAddress));
        editMentorNotesFloatingActionButton.setOnClickListener(this::onEditMentorNotesFloatingActionButtonClick);
        saveMentorImageButton.setOnClickListener(this::onSaveMentorImageButtonClick);
        if (null == entity.getId()) {
            deleteMentorImageButton.setVisibility(View.GONE);
            setTitle(R.string.title_activity_new_mentor);
        } else {
            setTitle(R.string.title_activity_edit_mentor);
            deleteMentorImageButton.setOnClickListener(this::onDeleteMentorImageButtonClick);
        }
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
        viewModel.getNameValidLiveData().observe(this, this::onNameValidChanged);
        viewModel.getContactValidLiveData().observe(this, this::onContactValidChanged);
    }

    private void onEditMentorNotesFloatingActionButtonClick(View view) {
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
                        viewModel.setNotes(s);
                        mentorNotesTextView.setText(s);
                    }
                })
                .setCancelable(false).create();
        dlg.show();
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
        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
    }

    private void onSaveOperationSucceeded(@NonNull String message) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.EditMentorActivity.onSaveOperationSucceeded");
        if (message.isEmpty()) {
            finish();
        } else {
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                    .setMessage(message).setCancelable(true).create();
            dlg.show();
        }
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
                })
                .setNegativeButton(R.string.response_no, null)
                .setCancelable(true).create();
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
                    .setPositiveButton(R.string.response_yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.response_no, (dialog, which) -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
                    })
                    .create();
            dlg.show();
        } else {
            finish();
        }
    }

}