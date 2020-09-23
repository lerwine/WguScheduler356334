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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
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
        Log.d(LOG_TAG, "Constructing EditMentorActivity");
        compositeDisposable = new CompositeDisposable();
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
        mentorNotesTextView = findViewById(R.id.mentorNotesTextView);
        editMentorNotesFloatingActionButton = findViewById(R.id.editMentorNotesFloatingActionButton);
        saveMentorImageButton = findViewById(R.id.saveMentorImageButton);
        deleteMentorImageButton = findViewById(R.id.deleteMentorImageButton);
        viewModel = new ViewModelProvider(this).get(EditMentorViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.restoreState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onLoadSuccess, this::onLoadFailed));
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
        if (null == entity) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_mentor_not_found, this).showDialog(this::finish);
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", entity));
        onNameChanged(entity.getName());
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
            setTitle(R.string.format_mentor);
            deleteMentorImageButton.setOnClickListener(this::onDeleteMentorImageButtonClick);
        }
        findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
        viewModel.getNameValidLiveData().observe(this, this::onNameValidChanged);
        viewModel.getContactValidLiveData().observe(this, this::onContactValidChanged);
        viewModel.getNameLiveData().observe(this, this::onNameChanged);
    }

    private void onNameChanged(String s) {
        String v = getResources().getString(R.string.format_mentor, s);
        int i = v.indexOf(':');
        setTitle((i > 0 && s.startsWith(v.substring(0, i))) ? s : v);
    }

    private void onLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog(this::finish);
    }

    private void onEditMentorNotesFloatingActionButtonClick(View view) {
        AlertHelper.showEditMultiLineTextDialog(R.string.title_edit_notes, viewModel.getNotes(), this, s -> {
            viewModel.setNotes(s);
            mentorNotesTextView.setText(s);
        });
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
        Log.d(LOG_TAG, "Enter onSaveOperationSucceeded");
        if (message.isEmpty()) {
            finish();
        } else {
            new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, message, this).showDialog();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), this).showDialog();
    }

    private void onDeleteMentorImageButtonClick(View view) {
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_mentor, R.string.message_delete_mentor_confirm, this).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::finish, this::onDeleteFailed));
        }, null);
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting mentor", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), this).showDialog();
    }

    private void onCancelImageButtonClick(View view) {
        verifySaveChanges();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(() -> {
                compositeDisposable.clear();
                compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
            }, this::finish, null);
        } else {
            finish();
        }
    }

}