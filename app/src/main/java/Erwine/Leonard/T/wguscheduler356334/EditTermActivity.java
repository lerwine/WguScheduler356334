package Erwine.Leonard.T.wguscheduler356334;

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

public class EditTermActivity extends AppCompatActivity {
    public static final String EXTRAS_KEY_TERM_ID = "term_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";
    public static final String STATE_KEY_START_DATE = "start_date";
    public static final String STATE_KEY_END_DATE = "end_date";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private EditText textEditTermName;
    private TextView nameOfTermError;
    private EditText editTextStartOfTerm;
    private TextView startOfTermError;
    private EditText editTextEndOfTerm;
    private TextView endOfTermError;
    private EditTermViewModel itemViewModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean newTerm;
    private boolean editInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        textEditTermName = findViewById(R.id.textEditTermName);
        nameOfTermError = findViewById(R.id.nameOfTermError);
        editTextStartOfTerm = findViewById(R.id.editTextStartOfTerm);
        startOfTermError = findViewById(R.id.startOfTermError);
        editTextEndOfTerm = findViewById(R.id.editTextEndOfTerm);
        endOfTermError = findViewById(R.id.endOfTermError);

        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(EditTermViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onTermEntityChanged);

        editTextStartOfTerm.setOnClickListener(this::onStartClick);
        editTextEndOfTerm.setOnClickListener(this::onEndClick);

        Bundle extras = getIntent().getExtras();
        newTerm = null == extras;
        if (newTerm) {
            setTitle(R.string.title_activity_new_term);
        } else {
            setTitle(R.string.title_activity_edit_term);
            int id = extras.getInt(EXTRAS_KEY_TERM_ID);
            itemViewModel.load(id).onErrorReturn((throwable) -> {
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.read_error_title)
                        .setMessage(getString(R.string.read_error_message, throwable.getMessage())).setCancelable(false).show();
                finish();
                return null;
            }).subscribe();
        }
        textEditTermName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateFields(charSequence.toString(), startDate, endDate);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        validateFields(textEditTermName.getText().toString(), startDate, endDate);
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
        if (newTerm) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
        validateFields(textEditTermName.getText().toString(), startDate, endDate);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (itemId == R.id.action_delete) {
            new AlertDialog.Builder(this).setTitle(R.string.delete_term_title).setMessage(R.string.delete_term_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
                itemViewModel.delete().subscribe(this::finish, (throwable) ->
                        new AlertDialog.Builder(this).setTitle(R.string.delete_error_title)
                                .setMessage(getString(R.string.delete_error_message, throwable.getMessage())).setCancelable(false).show()
                );
                finish();
            }).setNegativeButton(R.string.no, null).show();
        } else if (itemId == R.id.action_cancel) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        itemViewModel.save(textEditTermName.getText().toString(), startDate, endDate).subscribe(this::finish, (throwable) ->
                new AlertDialog.Builder(this).setTitle(R.string.save_error_title)
                        .setMessage(getString(R.string.save_error_message, throwable.getMessage())).setCancelable(false).show()
        );
    }

    private void onStartDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        startDate = LocalDate.of(year, monthOfYear, dayOfMonth);
        updateStartText();
    }

    private void onEndDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        endDate = LocalDate.of(year, monthOfYear, dayOfMonth);
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
        String n, s, e;
        if (null == termEntity) {
            n = s = e = "";
            startDate = endDate = null;
        } else {
            n = termEntity.getName();
            LocalDate date = termEntity.getStart();
            s = (null == date) ? "" : FORMATTER.format(date);
            startDate = date;
            date = termEntity.getEnd();
            e = (null == date) ? "" : FORMATTER.format(date);
            endDate = date;
        }
        if (!editInitialized) {
            textEditTermName.setText(n);
            editTextStartOfTerm.setText(s);
            editTextEndOfTerm.setText(e);
        }
        validateFields(n, startDate, endDate);
    }

    private void updateStartText() {
        LocalDate date = startDate;
        if (null == date) {
            editTextStartOfTerm.setText("");
        } else {
            editTextStartOfTerm.setText(FORMATTER.format(date));
        }
        validateFields(textEditTermName.getText().toString(), date, endDate);
    }

    private void updateEndText() {
        LocalDate date = endDate;
        if (null == date) {
            editTextEndOfTerm.setText("");
        } else {
            editTextEndOfTerm.setText(FORMATTER.format(date));
        }
        validateFields(textEditTermName.getText().toString(), startDate, date);
    }

    private void validateFields(String name, LocalDate start, LocalDate end) {
        if (null == start) {
            startOfTermError.setText(getString(R.string.required));
            startOfTermError.setVisibility(View.VISIBLE);
        } else if (null == end) {
            endOfTermError.setText(getString(R.string.required));
            endOfTermError.setVisibility(View.VISIBLE);
            startOfTermError.setVisibility(View.GONE);
        } else if (start.compareTo(end) > 0) {
            startOfTermError.setText(getString(R.string.start_after_end));
            startOfTermError.setVisibility(View.VISIBLE);
            endOfTermError.setVisibility(View.GONE);
        } else {
            startOfTermError.setVisibility(View.GONE);
            endOfTermError.setVisibility(View.GONE);
            if (name.trim().isEmpty()) {
                nameOfTermError.setText(getString(R.string.required));
                nameOfTermError.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            } else {
                nameOfTermError.setVisibility(View.GONE);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }
            return;
        }
        if (name.trim().isEmpty()) {
            nameOfTermError.setText(getString(R.string.required));
            nameOfTermError.setVisibility(View.VISIBLE);
        } else {
            nameOfTermError.setVisibility(View.GONE);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

}