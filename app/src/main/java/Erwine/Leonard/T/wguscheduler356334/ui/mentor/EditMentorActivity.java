package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EditMentorActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentor_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";

    private boolean editInitialized;
    private EditText edit_text_mentor_name;
    private TextView text_view_mentor_name_error;
    private EmailAddressFragment fragment_mentor_email;
    private FloatingActionButton button_mentor_add_address;
    private RecyclerView recycler_view_mentor_phone;
    private FloatingActionButton button_mentor_add_phone;
    private EditText edit_text_mentor_notes;
    private EditMentorViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        Toolbar toolbar = findViewById(R.id.toolbar_mentor_edit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_two_tone_save_24);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        editInitialized = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
        edit_text_mentor_name = findViewById(R.id.edit_text_mentor_name);
        text_view_mentor_name_error = findViewById(R.id.text_view_mentor_name_error);
        button_mentor_add_address = findViewById(R.id.button_mentor_add_address);
        recycler_view_mentor_phone = findViewById(R.id.recycler_view_mentor_phone);
        button_mentor_add_phone = findViewById(R.id.button_mentor_add_phone);
        edit_text_mentor_notes = findViewById(R.id.edit_text_mentor_notes);
        fragment_mentor_email = (EmailAddressFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_mentor_email);
//        recycler_view_mentor_email.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recycler_view_mentor_email.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(recycler_view_mentor_email.getContext(), linearLayoutManager.getOrientation());
//        recycler_view_mentor_email.addItemDecoration(decoration);

        recycler_view_mentor_phone.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_mentor_phone.setLayoutManager(linearLayoutManager);
//        decoration = new DividerItemDecoration(recycler_view_mentor_phone.getContext(), linearLayoutManager.getOrientation());
//        recycler_view_mentor_phone.addItemDecoration(decoration);

        edit_text_mentor_name.addTextChangedListener(Values.textWatcherForTextChanged((t) -> {
            if (t.trim().isEmpty()) {
                text_view_mentor_name_error.setVisibility(View.VISIBLE);
            } else {
                text_view_mentor_name_error.setVisibility(View.GONE);
            }
        }));
        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(EditMentorViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onMentorEntityChanged);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mentor_editor, menu);
        if (null == itemViewModel.getLiveData().getValue()) {
            MenuItem menuItem = menu.findItem(R.id.action_mentor_delete);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (itemId == R.id.action_mentor_delete) {
            new AlertDialog.Builder(this).setTitle(R.string.delete_mentor_title).setMessage(R.string.delete_mentor_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
                itemViewModel.delete().subscribe(this::finish, (throwable) ->
                        new AlertDialog.Builder(this).setTitle(R.string.delete_error_title)
                                .setMessage(getString(R.string.delete_error_message, throwable.getMessage())).setCancelable(false).show()
                );
                finish();
            }).setNegativeButton(R.string.no, null).show();
        } else if (itemId == R.id.action_mentor_cancel) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {

    }

    private void onMentorEntityChanged(MentorEntity mentorEntity) {
        if (!editInitialized) {
            edit_text_mentor_name.setText(mentorEntity.getName());
            fragment_mentor_email.setEmailAddresses(mentorEntity.getEmailAddresses());
        }
    }
}