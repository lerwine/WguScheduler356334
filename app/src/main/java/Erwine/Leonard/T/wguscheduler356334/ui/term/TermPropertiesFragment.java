package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.FORMATTER;

/**
 * Fragment for editing the properties of a {@link TermEntity}
 */
public class TermPropertiesFragment extends Fragment {

    private static final String LOG_TAG = TermPropertiesFragment.class.getName();
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<Boolean> canSaveLiveData;
    private TermPropertiesViewModel mViewModel;
    private EditText termNameEditText;
    private EditText termStartEditText;
    private EditText termEndEditText;
    private EditText termNotesEditText;

//    public static TermPropertiesFragment newInstance(Long termId) {
//        TermPropertiesFragment fragment = new TermPropertiesFragment();
//        Bundle args = new Bundle();
//        if (null != termId) {
//            args.putLong(TermPropertiesViewModel.ARGUMENT_KEY_TERM_ID, termId);
//        }
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TermPropertiesFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment");
        compositeDisposable = new CompositeDisposable();
        canSaveLiveData = new MutableLiveData<>(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_term_properties, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartEditText = view.findViewById(R.id.termStartEditText);
        termEndEditText = view.findViewById(R.id.termEndEditText);
        termNotesEditText = view.findViewById(R.id.termNotesEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
//        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermPropertiesViewModel.class);
        mViewModel = new ViewModelProvider(requireActivity()).get(TermPropertiesViewModel.class);
        mViewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onTermLoadSuccess);
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
        mViewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onTermLoadSuccess(TermEntity termEntity) {
        if (null == termEntity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));

        if (mViewModel.isFromInitializedState()) {
            termNameEditText.setText(mViewModel.getName());
            LocalDate date = mViewModel.getStart();
            if (null != date) {
                termStartEditText.setText(FORMATTER.format(date));
            } else {
                termStartEditText.setText("");
            }
            date = mViewModel.getEnd();
            if (null != date) {
                termEndEditText.setText(FORMATTER.format(date));
            } else {
                termEndEditText.setText("");
            }
            termNotesEditText.setText(mViewModel.getNotes());
        } else {
            termNameEditText.setText(termEntity.getName());
            LocalDate date = termEntity.getStart();
            if (null != date) {
                termStartEditText.setText(FORMATTER.format(date));
            } else {
                termStartEditText.setText("");
            }
            date = termEntity.getEnd();
            if (null != date) {
                termEndEditText.setText(FORMATTER.format(date));
            } else {
                termEndEditText.setText("");
            }
            termNotesEditText.setText(termEntity.getNotes());
        }

        termNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(mViewModel::onTermNameEditTextChanged));
        termStartEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(mViewModel::onTermStartEditTextChanged));
        termStartEditText.setOnClickListener(this::onStartClick);
        termStartEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(mViewModel::onTermEndEditTextChanged));
        termEndEditText.setOnClickListener(this::onEndClick);
        termNotesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(mViewModel::onTermNotesEditTextChanged));
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        mViewModel.getNameValidLiveData().observe(viewLifecycleOwner, this::onNameValidChanged);
        mViewModel.getStartMessageLiveData().observe(viewLifecycleOwner, this::onStartValidationMessageChanged);
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
            termStartEditText.setError(null);
        } else {
            termStartEditText.setError(getResources().getString(id));
        }
    }

    private void onStartClick(View view) {
        LocalDate date = mViewModel.getStart();
        if (null == date && null == (date = mViewModel.getEnd())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onStartDateChanged, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    private void onEndClick(View view) {
        LocalDate date = mViewModel.getEnd();
        if (null == date && null == (date = mViewModel.getStart())) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), this::onEndDateChanged, date.getYear(), date.getMonthValue(), date.getDayOfMonth()).show();
    }

    void onStartDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termStartEditText.setText(FORMATTER.format(d));
        mViewModel.onStartDateChanged(d);
    }

    void onEndDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termEndEditText.setText(FORMATTER.format(d));
        mViewModel.onEndDateChanged(d);
    }

}