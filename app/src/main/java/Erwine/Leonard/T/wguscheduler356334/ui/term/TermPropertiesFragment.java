package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

import static Erwine.Leonard.T.wguscheduler356334.ui.term.TermViewModel.FORMATTER;

/**
 * Fragment for editing the properties of a {@link TermEntity}
 */
public class TermPropertiesFragment extends Fragment {

    private static final String LOG_TAG = TermPropertiesFragment.class.getName();
    private TermViewModel viewModel;
    private EditText termNameEditText;
    private TextView termStartTextView;
    private TextView termEndValueTextView;
    private TextView termNotesTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TermPropertiesFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_term_properties, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartTextView = view.findViewById(R.id.termStartTextView);
        termEndValueTextView = view.findViewById(R.id.termEndValueTextView);
        termNotesTextView = view.findViewById(R.id.termNotesTextView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TermViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onLoadSuccess);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onSaveInstanceState");
        viewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onLoadSuccess(TermEntity entity) {
        if (null == entity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", entity));

        if (viewModel.isFromInitializedState()) {
            termNameEditText.setText(viewModel.getName());
            LocalDate date = viewModel.getStart();
            if (null != date) {
                termStartTextView.setText(FORMATTER.format(date));
            } else {
                termStartTextView.setText("");
            }
            date = viewModel.getEnd();
            if (null != date) {
                termEndValueTextView.setText(FORMATTER.format(date));
            } else {
                termEndValueTextView.setText("");
            }
            termNotesTextView.setText(viewModel.getNotes());
        } else {
            termNameEditText.setText(entity.getName());
            LocalDate date = entity.getStart();
            if (null != date) {
                termStartTextView.setText(FORMATTER.format(date));
            } else {
                termStartTextView.setText("");
            }
            date = entity.getEnd();
            if (null != date) {
                termEndValueTextView.setText(FORMATTER.format(date));
            } else {
                termEndValueTextView.setText("");
            }
            termNotesTextView.setText(entity.getNotes());
        }

        termNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onTermNameEditTextChanged));
        termStartTextView.setOnClickListener(this::onStartClick);
        termEndValueTextView.setOnClickListener(this::onEndClick);
        termNotesTextView.setOnClickListener(this::onTermNotesEditTextClick);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getNameValidLiveData().observe(viewLifecycleOwner, this::onNameValidChanged);
        viewModel.getStartMessageLiveData().observe(viewLifecycleOwner, this::onStartValidationMessageChanged);
    }

    private void onTermNotesEditTextClick(View view) {
        Context context = requireContext();
        EditText editText = new EditText(context);
        String text = viewModel.getNotes();
        editText.setText(text);
        editText.setSingleLine(false);
        editText.setVerticalScrollBarEnabled(true);
        editText.setBackgroundResource(android.R.drawable.edit_text);
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.title_edit_notes))
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String s = editText.getText().toString();
                    if (!text.equals(s)) {
                        viewModel.onTermNotesEditTextChanged(s);
                        termNotesTextView.setText(s);
                    }
                })
                .setCancelable(false).create();
        dlg.show();
    }

    private void onNameValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            termNameEditText.setError(null);
        } else {
            termNameEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onStartValidationMessageChanged(Integer id) {
        if (null == id || id == R.string.command_ok) {
            termStartTextView.setError(null);
        } else {
            termStartTextView.setError(getResources().getString(id));
        }
    }

    private void onStartClick(View view) {
        LocalDate date = viewModel.getStart();
        if (null == date && null == (date = viewModel.getEnd())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onStartDateChanged, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onEndClick(View view) {
        LocalDate date = viewModel.getEnd();
        if (null == date && null == (date = viewModel.getStart())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onEndDateChanged, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    void onStartDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termStartTextView.setText(FORMATTER.format(d));
        viewModel.onStartDateChanged(d);
    }

    void onEndDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termEndValueTextView.setText(FORMATTER.format(d));
        viewModel.onEndDateChanged(d);
    }

}