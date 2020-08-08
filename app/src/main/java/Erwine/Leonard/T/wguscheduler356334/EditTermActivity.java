package Erwine.Leonard.T.wguscheduler356334;

import android.app.DatePickerDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.PrimaryKey;

import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import Erwine.Leonard.T.wguscheduler356334.db.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.terms.TermItemViewModel;

public class EditTermActivity extends AppCompatActivity {
    public static final String EXTRAS_KEY_TERM_ID = "term_id";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private EditText textEditTermName;
    private TextView nameOfTermError;
    private EditText editTextStartOfTerm;
    private TextView startOfTermError;
    private EditText editTextEndOfTerm;
    private TextView endOfTermError;
    private TermItemViewModel itemViewModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private FloatingActionButton saveButton;
    private FloatingActionButton deleteButton;
    private FloatingActionButton cancelButton;
    private boolean newTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textEditTermName = findViewById(R.id.textEditTermName);
        nameOfTermError = findViewById(R.id.nameOfTermError);
        editTextStartOfTerm = findViewById(R.id.editTextStartOfTerm);
        startOfTermError = findViewById(R.id.startOfTermError);
        editTextEndOfTerm = findViewById(R.id.editTextEndOfTerm);
        endOfTermError = findViewById(R.id.endOfTermError);

        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(TermItemViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onTermEntityChanged);

        editTextStartOfTerm.setOnClickListener(this::onStartClick);
        editTextEndOfTerm.setOnClickListener(this::onEndClick);

        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(this::onSaveClick);
        cancelButton.setOnClickListener(this::onCancelClick);
        Bundle extras = getIntent().getExtras();
        newTerm = null == extras;
        if (newTerm) {
            setTitle(R.string.title_activity_new_term);
            deleteButton.setVisibility(View.GONE);
        } else {
            setTitle(R.string.title_activity_edit_term);
            int id = extras.getInt(EXTRAS_KEY_TERM_ID);
            itemViewModel.load(id);
            deleteButton.setOnClickListener(this::onDeleteClick);
        }

        validateFields(textEditTermName.getText().toString(), startDate, endDate);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        itemViewModel.save(textEditTermName.getText().toString(), startDate, endDate);
        finish();
    }

    private void onStartDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        startDate = LocalDate.of(year, monthOfYear, dayOfMonth);
        updateStartText();
    }

    private void onEndDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        endDate = LocalDate.of(year, monthOfYear, dayOfMonth);
        updateEndText();
    }

    private void onSaveClick(View view) {
        saveAndReturn();
    }

    private void onDeleteClick(View view) {
        itemViewModel.delete();
        finish();
    }

    private void onCancelClick(View view) {
        finish();
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
        textEditTermName.setText(n);
        editTextStartOfTerm.setText(s);
        editTextEndOfTerm.setText(e);
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
                saveButton.setEnabled(false);
            } else {
                nameOfTermError.setVisibility(View.GONE);
                saveButton.setEnabled(true);
            }
            return;
        }
        if (name.trim().isEmpty()) {
            nameOfTermError.setText(getString(R.string.required));
            nameOfTermError.setVisibility(View.VISIBLE);
        } else {
            nameOfTermError.setVisibility(View.GONE);
        }
        saveButton.setEnabled(false);
    }

}