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

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.DatePickerEditView;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = EditTermFragment.class.getName();

    private EditTermViewModel viewModel;
    private EditText termNameEditText;
    private DatePickerEditView termStartEditText;
    private DatePickerEditView termEndEditText;
    private EditText notesEditText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditTermFragment() {
        Log.d(LOG_TAG, "Constructing TermPropertiesFragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_edit_term, container, false);

        termNameEditText = view.findViewById(R.id.termNameEditText);
        termStartEditText = view.findViewById(R.id.termStartEditView);
        termEndEditText = view.findViewById(R.id.termEndEditView);
        notesEditText = view.findViewById(R.id.notesEditText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        OneTimeObservers.observeOnce(viewModel.getEntity(), this::onLoadSuccess);
    }

    private void onLoadSuccess(TermEntity entity) {
        Log.d(LOG_TAG, String.format("Loaded %s", entity));
        termNameEditText.setText(viewModel.getName());
        termStartEditText.setSelectedDate(viewModel.getStart());
        termEndEditText.setSelectedDate(viewModel.getEnd());
        notesEditText.setText(viewModel.getNotes());
        termNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        termStartEditText.setInitialPickerDateFactory(d -> (null == d) ? termEndEditText.getSelectedDate() : d);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        termStartEditText.observeLocalDateChange(viewLifecycleOwner, new MaybeObserver<LocalDate>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(LocalDate localDate) {
                Log.d(LOG_TAG, "Enter termStartEditText.observeLocalDateChange.onSuccess(" + ToStringBuilder.toEscapedString(localDate, false) + ")");
                viewModel.setStart(localDate);
            }

            @Override
            public void onError(Throwable e) {
                String m = e.getMessage();
                Log.d(LOG_TAG, "Enter termStartEditText.observeLocalDateChange.onError(" + ToStringBuilder.toEscapedString(m) + ")");
                if (null == m || m.trim().isEmpty()) {
                    viewModel.setStart(ResourceMessageFactory.ofError(R.string.message_invalid_date));
                } else {
                    viewModel.setStart(ResourceMessageFactory.ofError(R.string.format_date_parse_error, e.getMessage()));
                }
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "Enter termStartEditText.observeLocalDateChange.onComplete()");
                viewModel.setStart((LocalDate) null);
            }
        });
        termEndEditText.setInitialPickerDateFactory(d -> (null == d) ? termStartEditText.getSelectedDate() : d);
        termEndEditText.observeLocalDateChange(viewLifecycleOwner, new MaybeObserver<LocalDate>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(LocalDate localDate) {
                Log.d(LOG_TAG, "Enter termEndEditText.observeLocalDateChange.onSuccess(" + ToStringBuilder.toEscapedString(localDate, false) + ")");
                viewModel.setEnd(localDate);
            }

            @Override
            public void onError(Throwable e) {
                String m = e.getMessage();
                Log.d(LOG_TAG, "Enter termEndEditText.observeLocalDateChange.onError(" + ToStringBuilder.toEscapedString(m) + ")");
                if (null == m || m.trim().isEmpty()) {
                    viewModel.setEnd(ResourceMessageFactory.ofError(R.string.message_invalid_date));
                } else {
                    viewModel.setEnd(ResourceMessageFactory.ofError(R.string.format_date_parse_error, e.getMessage()));
                }
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG, "Enter termEndEditText.observeLocalDateChange.onComplete()");
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
                o -> {
                    if (o.isPresent()) {
                        ResourceMessageFactory f = o.get();
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "startMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", isWarning: " + f.isWarning() + ")");
                        termStartEditText.setError(m, AppCompatResources.getDrawable(requireContext(), (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
                    } else {
                        termStartEditText.setError(null);
                    }
                }
        );
        viewModel.getEndMessage().observe(getViewLifecycleOwner(),
                o -> {
                    if (o.isPresent()) {
                        ResourceMessageFactory f = o.get();
                        String m = f.apply(getResources());
                        Log.d(LOG_TAG, "endMessageMaybe.onSuccess(message: " + ToStringBuilder.toEscapedString(m) + ", isWarning: " + f.isWarning() + ")");
                        termEndEditText.setError(m, AppCompatResources.getDrawable(requireContext(), (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
                    } else {
                        termEndEditText.setError(null);
                    }
                }
        );
    }

}