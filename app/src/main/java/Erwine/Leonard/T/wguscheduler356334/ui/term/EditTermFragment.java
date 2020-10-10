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

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel.FORMATTER;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = EditTermFragment.class.getName();

    private final CompositeDisposable subscriptionCompositeDisposable;
    private EditTermViewModel viewModel;
    private EditText termNameEditText;
    private TextView termStartValueTextView;
    private TextView termEndValueTextView;
    private EditText notesEditText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditTermFragment() {
        Log.d(LOG_TAG, "Constructing TermPropertiesFragment");
        subscriptionCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_edit_term, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartValueTextView = view.findViewById(R.id.termStartValueTextView);
        termEndValueTextView = view.findViewById(R.id.termEndValueTextView);
        notesEditText = view.findViewById(R.id.notesEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        OneTimeObservers.subscribeOnce(viewModel.getEntity(), this::onLoadSuccess);
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
                termStartValueTextView.setText(FORMATTER.format(date));
            } else {
                termStartValueTextView.setText("");
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
                termStartValueTextView.setText(FORMATTER.format(date));
            } else {
                termStartValueTextView.setText("");
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
        termStartValueTextView.setOnClickListener(this::onStartClick);
        termEndValueTextView.setOnClickListener(this::onEndClick);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        subscriptionCompositeDisposable.add(viewModel.getNameValid().subscribe(isValid -> {
            Log.d(LOG_TAG, "nameValidObservable.onNext(" + isValid + ")");
            if (isValid) {
                termNameEditText.setError(null);
            } else {
                termNameEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
            }
        }));
        subscriptionCompositeDisposable.add(viewModel.getStartMessage().subscribe(
                o -> {
                    if (o.isPresent()) {
                        ResourceMessageFactory f = o.get();
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "startMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", isWarning: " + f.isWarning() + ")");
                        termStartValueTextView.setError(m, AppCompatResources.getDrawable(requireContext(), (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
                    } else {
                        termStartValueTextView.setError(null);
                    }
                }
        ));
        subscriptionCompositeDisposable.add(viewModel.getEndMessage().subscribe(
                o -> {
                    if (o.isPresent()) {
                        ResourceMessageFactory f = o.get();
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "endMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", isWarning: " + f.isWarning() + ")");
                        termEndValueTextView.setError(m, AppCompatResources.getDrawable(requireContext(), (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
                    } else {
                        termEndValueTextView.setError(null);
                    }
                }
        ));
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
        termStartValueTextView.setText(FORMATTER.format(d));
        viewModel.setStart(d);
    }

    void onEndDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate d = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        termEndValueTextView.setText(FORMATTER.format(d));
        viewModel.setEnd(d);
    }

}