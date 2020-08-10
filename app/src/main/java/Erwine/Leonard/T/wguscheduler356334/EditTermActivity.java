package Erwine.Leonard.T.wguscheduler356334;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EditTermActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_TERM_ID = "term_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";
    public static final String STATE_KEY_START_DATE = "start_date";
    public static final String STATE_KEY_END_DATE = "end_date";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private EditText edit_text_term_name;
    private TextView text_view_term_name_error;
    private EditText edit_text_term_start;
    private TextView text_view_term_start_error;
    private EditText edit_text_term_end;
    private TextView text_view_term_end_error;
    private EditText edit_text_term_notes;
    private EditTermViewModel itemViewModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean editInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        Toolbar toolbar = findViewById(R.id.toolbar_term_edit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_two_tone_save_24);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (null != savedInstanceState) {
            editInitialized = savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
            long v = savedInstanceState.getLong(STATE_KEY_START_DATE, -1);
            if (v >= 0) {
                startDate = LocalDate.ofEpochDay(v);
            }
            v = savedInstanceState.getLong(STATE_KEY_END_DATE, -1);
            if (v >= 0) {
                endDate = LocalDate.ofEpochDay(v);
            }
        }
        edit_text_term_name = findViewById(R.id.edit_text_term_name);
        text_view_term_name_error = findViewById(R.id.text_view_term_name_error);
        edit_text_term_start = findViewById(R.id.edit_text_term_start);
        text_view_term_start_error = findViewById(R.id.text_view_term_start_error);
        edit_text_term_end = findViewById(R.id.edit_text_term_end);
        text_view_term_end_error = findViewById(R.id.text_view_term_end_error);
        edit_text_term_notes = findViewById(R.id.edit_text_term_notes);

        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(EditTermViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onTermEntityChanged);

        edit_text_term_start.setOnClickListener(this::onStartClick);
        edit_text_term_end.setOnClickListener(this::onEndClick);

        Bundle extras = getIntent().getExtras();
        if (null == extras) {
            setTitle(R.string.title_activity_new_term);
        } else {
            setTitle(R.string.title_activity_edit_term);
            int id = extras.getInt(EXTRAS_KEY_TERM_ID);
            itemViewModel.load(id).onErrorReturn((throwable) -> {
                new AlertDialog.Builder(this).setTitle(R.string.read_error_title)
                        .setMessage(getString(R.string.read_error_message, throwable.getMessage())).setCancelable(false).show();
                finish();
                return null;
            }).subscribe();
        }
        edit_text_term_name.addTextChangedListener(Values.textWatcherForTextChanged((t) -> validateFields(t, startDate, endDate)));
        validateFields(edit_text_term_name.getText().toString(), startDate, endDate);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        LocalDate date = startDate;
        outState.putLong(STATE_KEY_START_DATE, (null == date) ? -1L : date.toEpochDay());
        date = endDate;
        outState.putLong(STATE_KEY_END_DATE, (null == date) ? -1L : date.toEpochDay());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_term_editor, menu);
        if (null == itemViewModel.getLiveData().getValue()) {
            MenuItem menuItem = menu.findItem(R.id.action_term_delete);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
        validateFields(edit_text_term_name.getText().toString(), startDate, endDate);
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
        } else if (itemId == R.id.action_term_delete) {
            new AlertDialog.Builder(this).setTitle(R.string.delete_term_title).setMessage(R.string.delete_term_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
                itemViewModel.delete().subscribe(this::finish, (throwable) ->
                        new AlertDialog.Builder(this).setTitle(R.string.delete_error_title)
                                .setMessage(getString(R.string.delete_error_message, throwable.getMessage())).setCancelable(false).show()
                );
                finish();
            }).setNegativeButton(R.string.no, null).show();
        } else if (itemId == R.id.action_term_cancel) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    private void saveAndReturn() {
        itemViewModel.save(edit_text_term_name.getText().toString(), startDate, endDate, edit_text_term_notes.getText().toString()).subscribe(this::finish, (throwable) ->
                new AlertDialog.Builder(this).setTitle(R.string.save_error_title)
                        .setMessage(getString(R.string.save_error_message, throwable.getMessage())).setCancelable(false).show()
        );
    }

    private void onStartDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        startDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        updateStartText();
    }

    private void onEndDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        endDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        updateEndText();
    }

    private void onStartClick(View view) {
        LocalDate date = startDate;
        if (null == date && null == (date = endDate)) {
            date = LocalDate.now();
        }
        new DatePickerDialog(this, this::onStartDateSet, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onEndClick(View view) {
        LocalDate date = endDate;
        if (null == date && null == (date = startDate)) {
            date = LocalDate.now();
        }
        new DatePickerDialog(this, this::onEndDateSet, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onTermEntityChanged(TermEntity termEntity) {
        String n, s, e, t;
        if (null == termEntity) {
            n = s = e = t = "";
            startDate = endDate = null;
        } else {
            n = termEntity.getName();
            t = termEntity.getNotes();
            LocalDate date = termEntity.getStart();
            s = (null == date) ? "" : FORMATTER.format(date);
            startDate = date;
            date = termEntity.getEnd();
            e = (null == date) ? "" : FORMATTER.format(date);
            endDate = date;
        }
        if (!editInitialized) {
            edit_text_term_name.setText(n);
            edit_text_term_start.setText(s);
            edit_text_term_end.setText(e);
            edit_text_term_notes.setText(t);
        }
        validateFields(n, startDate, endDate);
    }

    private void updateStartText() {
        LocalDate date = startDate;
        if (null == date) {
            edit_text_term_start.setText("");
        } else {
            edit_text_term_start.setText(FORMATTER.format(date));
        }
        validateFields(edit_text_term_name.getText().toString(), date, endDate);
    }

    private void updateEndText() {
        LocalDate date = endDate;
        if (null == date) {
            edit_text_term_end.setText("");
        } else {
            edit_text_term_end.setText(FORMATTER.format(date));
        }
        validateFields(edit_text_term_name.getText().toString(), startDate, date);
    }

    private void validateFields(String name, LocalDate start, LocalDate end) {
        if (null == start) {
            text_view_term_start_error.setText(getString(R.string.required));
            text_view_term_start_error.setVisibility(View.VISIBLE);
        } else if (null == end) {
            text_view_term_end_error.setText(getString(R.string.required));
            text_view_term_end_error.setVisibility(View.VISIBLE);
            text_view_term_start_error.setVisibility(View.GONE);
        } else if (start.compareTo(end) > 0) {
            text_view_term_start_error.setText(getString(R.string.start_after_end));
            text_view_term_start_error.setVisibility(View.VISIBLE);
            text_view_term_end_error.setVisibility(View.GONE);
        } else {
            text_view_term_start_error.setVisibility(View.GONE);
            text_view_term_end_error.setVisibility(View.GONE);
            if (name.trim().isEmpty()) {
                text_view_term_name_error.setText(getString(R.string.required));
                text_view_term_name_error.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            } else {
                text_view_term_name_error.setVisibility(View.GONE);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }
            return;
        }
        if (name.trim().isEmpty()) {
            text_view_term_name_error.setText(getString(R.string.required));
            text_view_term_name_error.setVisibility(View.VISIBLE);
        } else {
            text_view_term_name_error.setVisibility(View.GONE);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

}