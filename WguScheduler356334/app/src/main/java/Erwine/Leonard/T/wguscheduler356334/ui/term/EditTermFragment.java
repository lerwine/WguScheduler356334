package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.DatePickerEditView;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(EditTermFragment.class);

    private EditTermViewModel viewModel;
    private EditText termNameEditText;
    private DatePickerEditView termStartEditView;
    private DatePickerEditView termEndEditView;
    private EditText notesEditText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditTermFragment() {
        Log.d(LOG_TAG, "Constructing");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_edit_term, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartEditView = view.findViewById(R.id.termStartEditView);
        termEndEditView = view.findViewById(R.id.termEndEditView);
        notesEditText = view.findViewById(R.id.notesEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        ObserverHelper.subscribeOnce(viewModel.getInitializedCompletable(), getViewLifecycleOwner(), this::onViewModelInitialized);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onViewModelInitialized() {
        Log.d(LOG_TAG, "onViewModelInitialized");
        termNameEditText.setText(viewModel.getName());
        termStartEditView.setSelectedDate(viewModel.getStart());
        termEndEditView.setSelectedDate(viewModel.getEnd());
        notesEditText.setText(viewModel.getNotes());
        termNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        termStartEditView.setInitialPickerDateFactory(d -> (null == d) ? termEndEditView.getSelectedDate() : d);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        termStartEditView.observeLocalDateChange(viewLifecycleOwner, new MaybeObserver<LocalDate>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull LocalDate localDate) {
                Log.d(LOG_TAG, "Enter termStartEditView.observeLocalDateChange.onSuccess(" + ToStringBuilder.toEscapedString(localDate, false) + ")");
                viewModel.setStart(localDate);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                String m = e.getMessage();
                Log.d(LOG_TAG, "Enter termStartEditView.observeLocalDateChange.onError(" + ToStringBuilder.toEscapedString(m) + ")");
                if (null == m || m.trim().isEmpty()) {
                    viewModel.setStart(ResourceMessageFactory.ofError(R.string.message_invalid_date));
                } else {
                    viewModel.setStart(ResourceMessageFactory.ofError(R.string.format_date_parse_error, e.getMessage()));
                }
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "Enter termStartEditView.observeLocalDateChange.onComplete()");
                viewModel.setStart((LocalDate) null);
            }
        });
        termEndEditView.setInitialPickerDateFactory(d -> (null == d) ? termStartEditView.getSelectedDate() : d);
        termEndEditView.observeLocalDateChange(viewLifecycleOwner, new MaybeObserver<LocalDate>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull LocalDate localDate) {
                Log.d(LOG_TAG, "Enter termEndEditView.observeLocalDateChange.onSuccess(" + ToStringBuilder.toEscapedString(localDate, false) + ")");
                viewModel.setEnd(localDate);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                String m = e.getMessage();
                Log.d(LOG_TAG, "Enter termEndEditView.observeLocalDateChange.onError(" + ToStringBuilder.toEscapedString(m) + ")");
                if (null == m || m.trim().isEmpty()) {
                    viewModel.setEnd(ResourceMessageFactory.ofError(R.string.message_invalid_date));
                } else {
                    viewModel.setEnd(ResourceMessageFactory.ofError(R.string.format_date_parse_error, e.getMessage()));
                }
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "Enter termEndEditView.observeLocalDateChange.onComplete()");
                viewModel.setEnd((LocalDate) null);
            }
        });

        viewModel.getNameValid().observe(getViewLifecycleOwner(), isValid -> {
            Log.d(LOG_TAG, "nameValidObservable.onNext(" + isValid + ")");
            if (isValid) {
                termNameEditText.setError(null);
            } else {
                termNameEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
            }
        });
        viewModel.getStartMessage().observe(getViewLifecycleOwner(),
                f -> {
                    if (null != f) {
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "startMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", level: " + f.getLevel() + ")");
                        termStartEditView.setError(m, AppCompatResources.getDrawable(requireContext(), f.getLevel().getErrorIcon()));
                    } else {
                        termStartEditView.setError(null);
                    }
                }
        );
        viewModel.getEndMessage().observe(getViewLifecycleOwner(),
                f -> {
                    if (null != f) {
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "endMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", level: " + f.getLevel().name() + ")");
                        termEndEditView.setError(m, AppCompatResources.getDrawable(requireContext(), f.getLevel().getErrorIcon()));
                    } else {
                        termEndEditView.setError(null);
                    }
                }
        );
    }

}