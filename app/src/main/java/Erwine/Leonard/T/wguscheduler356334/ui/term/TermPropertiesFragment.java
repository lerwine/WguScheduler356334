package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

import static Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel.FORMATTER;

/**
 * Fragment for editing the properties of a {@link TermEntity}.
 * This is included in the layouts for {@link EditTermFragment} and {@link Erwine.Leonard.T.wguscheduler356334.AddTermActivity}.
 * This assumes that the containing {@link Fragment} or {@link android.app.Activity} invokes {@link EditTermViewModel#initializeViewModelState(Bundle, Supplier)}
 */
public class TermPropertiesFragment extends Fragment {

    private static final String LOG_TAG = TermPropertiesFragment.class.getName();
    private EditTermViewModel viewModel;
    private EditText termNameEditText;
    private TextView termStartTextView;
    private TextView termEndValueTextView;
    private EditText notesEditText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TermPropertiesFragment() {
        Log.d(LOG_TAG, "Constructing TermPropertiesFragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_term_properties, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartTextView = view.findViewById(R.id.termStartTextView);
        termEndValueTextView = view.findViewById(R.id.termEndValueTextView);
        notesEditText = view.findViewById(R.id.notesEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onLoadSuccess);
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
            notesEditText.setText(viewModel.getNotes());
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
            notesEditText.setText(entity.getNotes());
        }

        termNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        termStartTextView.setOnClickListener(this::onStartClick);
        termEndValueTextView.setOnClickListener(this::onEndClick);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getNameValidLiveData().observe(viewLifecycleOwner, this::onNameValidChanged);
        viewModel.getStartMessageLiveData().observe(viewLifecycleOwner, this::onStartValidationMessageChanged);
    }

    private void onNameValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            termNameEditText.setError(null);
        } else {
            termNameEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    private void onStartValidationMessageChanged(Integer id) {
        if (null == id) {
            termStartTextView.setError(null);
        } else {
            termStartTextView.setError(getResources().getString(id), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    private void onStartClick(View view) {
        LocalDate date = viewModel.getStart();
        if (null == date && null == (date = viewModel.getEnd())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onStartDateChanged, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onEndClick(View view) {
        LocalDate date = viewModel.getEnd();
        if (null == date && null == (date = viewModel.getStart())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onEndDateChanged, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    void onStartDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termStartTextView.setText(FORMATTER.format(d));
        viewModel.setStart(d);
    }

    void onEndDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termEndValueTextView.setText(FORMATTER.format(d));
        viewModel.setEnd(d);
    }

}