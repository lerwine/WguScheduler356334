package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;

public class EditMentorActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentor_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";

    private boolean editInitialized;
    private EditText edit_text_mentor_name;
    private TextView text_view_mentor_name_error;
    private RecyclerView recycler_view_mentor_email;
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
        recycler_view_mentor_email = findViewById(R.id.recycler_view_mentor_email);
        button_mentor_add_address = findViewById(R.id.button_mentor_add_address);
        recycler_view_mentor_phone = findViewById(R.id.recycler_view_mentor_phone);
        button_mentor_add_phone = findViewById(R.id.button_mentor_add_phone);
        edit_text_mentor_notes = findViewById(R.id.edit_text_mentor_notes);

        edit_text_mentor_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    text_view_mentor_name_error.setVisibility(View.VISIBLE);
                } else {
                    text_view_mentor_name_error.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(EditMentorViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onMentorEntityChanged);

    }

    private void onMentorEntityChanged(MentorEntity mentorEntity) {
        if (!editInitialized) {
            edit_text_mentor_name.setText(mentorEntity.getName());
        }
    }
}