package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAlertDialog extends DialogFragment {

    private EditAlertViewModel viewModel;
    private TextView typeTextView;
    private TextView codeTextView;
    private TextView titleTextView;
    private TextView eventDateTextView;
    private EditText daysEditText;
    private TextView specificDateTextView;
    private RadioButton specificDateRadioButton;
    private RadioButton daysBeforeRadioButton;
    private RadioButton daysAfterRadioButton;
    private RadioButton startDateRadioButton;
    private RadioButton endDateRadioButton;
    private TextView calculatedDateValueTextView;
    private EditText messageEditText;
    private ImageButton saveImageButton;
    private ImageButton deleteImageButton;
    private ImageButton cancelImageButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeTextView = view.findViewById(R.id.typeTextView);
        codeTextView = view.findViewById(R.id.codeTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        eventDateTextView = view.findViewById(R.id.eventDateTextView);
        specificDateTextView = view.findViewById(R.id.specificDateTextView);
        specificDateRadioButton = view.findViewById(R.id.specificDateRadioButton);
        daysEditText = view.findViewById(R.id.daysEditText);
        daysBeforeRadioButton = view.findViewById(R.id.daysBeforeRadioButton);
        daysAfterRadioButton = view.findViewById(R.id.daysAfterRadioButton);
        startDateRadioButton = view.findViewById(R.id.startDateRadioButton);
        endDateRadioButton = view.findViewById(R.id.endDateRadioButton);
        calculatedDateValueTextView = view.findViewById(R.id.calculatedDateValueTextView);
        messageEditText = view.findViewById(R.id.messageEditText);
        saveImageButton = view.findViewById(R.id.saveImageButton);
        deleteImageButton = view.findViewById(R.id.deleteImageButton);
        cancelImageButton = view.findViewById(R.id.cancelImageButton);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAlertViewModel.class);
        viewModel.initializeViewModelState(savedInstanceState, this::getArguments);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getAlertEntityLiveData().observe(viewLifecycleOwner, this::onAlertEntityLoaded);
        viewModel.getInitializationFailureLiveData().observe(viewLifecycleOwner, this::onInitializationFailure);
        viewModel.getDaysValidationLiveData().observe(viewLifecycleOwner, this::onDaysValidationChanged);
        viewModel.getSelectedDateValidationLiveData().observe(viewLifecycleOwner, this::onSelectedDateValidationLChanged);
        viewModel.getCalculatedDateLiveData().observe(viewLifecycleOwner, this::onCalculatedDateChanged);
        viewModel.getEventDateLiveData().observe(viewLifecycleOwner, this::onEventDateChanged);
        viewModel.getValidLiveData().observe(viewLifecycleOwner, this::onValidChanged);
    }

    private void onAlertEntityLoaded(@NonNull AlertEntity alertEntity) {
        long id = alertEntity.getId();
        if (ID_NEW == id) {
            deleteImageButton.setEnabled(false);
            deleteImageButton.setVisibility(View.GONE);
        }
        typeTextView.setText(viewModel.getType());
        codeTextView.setText(viewModel.getCode());
        titleTextView.setText(viewModel.getTitle());
        daysEditText.setText(viewModel.getDaysText());
        AlertDateOption option = viewModel.getDateSpecOption();
        specificDateRadioButton.setChecked(option.isExplicit());
        daysBeforeRadioButton.setChecked(option.isBefore());
        startDateRadioButton.setChecked(option.isStart());
        daysAfterRadioButton.setChecked(option.isAfter());
        if (option.isExplicit()) {
            endDateRadioButton.setEnabled(true);
            endDateRadioButton.setChecked(false);
            specificDateTextView.setClickable(true);
            daysEditText.setClickable(false);
            daysEditText.setEnabled(false);
        } else {
            if (viewModel.isBeforeEndEnabled() || !option.isBefore()) {
                endDateRadioButton.setEnabled(true);
                endDateRadioButton.setChecked(option.isEnd());
            } else {
                endDateRadioButton.setChecked(false);
                endDateRadioButton.setEnabled(false);
            }
            specificDateTextView.setClickable(false);
            daysEditText.setClickable(true);
            daysEditText.setEnabled(true);
        }
        LocalDate d = viewModel.getSelectedDate();
        if (null != d) {
            specificDateTextView.setText(EditAlertViewModel.DATE_FORMATTER.format(d));
        } else {
            specificDateTextView.setText("");
        }

        daysEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setDaysText));
        messageEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setMessage));
        specificDateTextView.setOnClickListener(this::onSpecificDateTextViewClick);
        saveImageButton.setOnClickListener(this::onSaveButtonClick);
        deleteImageButton.setOnClickListener(this::onDeleteButtonClick);
        cancelImageButton.setOnClickListener(this::onCancelButtonClick);
        specificDateRadioButton.setOnCheckedChangeListener(this::onSpecificDateRadioButtonCheckedChange);
        daysBeforeRadioButton.setOnCheckedChangeListener(this::onDaysBeforeRadioButtonCheckedChange);
        startDateRadioButton.setOnCheckedChangeListener(this::onStartDateRadioButtonCheckedChange);
        daysAfterRadioButton.setOnCheckedChangeListener(this::onDaysAfterRadioButtonCheckedChange);
        endDateRadioButton.setOnCheckedChangeListener(this::onEndDateDateRadioButtonCheckedChange);
    }

    private void onSpecificDateTextViewClick(View view) {
        // TODO: Show date selection popup
    }

    private void onEventDateChanged(String text) {
        if (null != text) {
            eventDateTextView.setText(text);
        } else {
            eventDateTextView.setText(getResources().getString(R.string.message_missing_parentheses));
        }
    }

    private void onCalculatedDateChanged(String text) {
        if (null != text) {
            calculatedDateValueTextView.setText(text);
        } else {
            calculatedDateValueTextView.setText(getResources().getString(R.string.message_invalid_parentheses));
        }
    }

    private void onInitializationFailure(@NonNull ValidationMessage.ResourceMessageResult messages) {
        FragmentActivity activity = requireActivity();
        requireDialog().dismiss();
        AlertDialog dlg = new AlertDialog.Builder(activity).setTitle(R.string.title_read_error)
                .setIcon(R.drawable.dialog_error).setMessage(messages.join("\n", getResources()))
                .setCancelable(true).create();
        dlg.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveViewModelState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void onSaveButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertFragment.onSaveButtonClick
    }

    private void onDeleteButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertFragment.onDeleteButtonClick
    }

    private void onCancelButtonClick(View view) {
        dismiss();
    }

    private void onDaysValidationChanged(ValidationMessage.ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            daysEditText.setError(null);
        } else {
            daysEditText.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onSelectedDateValidationLChanged(ValidationMessage.ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            specificDateTextView.setError(null);
        } else {
            specificDateTextView.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onDaysBeforeRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                specificDateRadioButton.setChecked(false);
                startDateRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setDateSpecOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                viewModel.setDateSpecOption((startDateRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.BEFORE_END_DATE);
            }
            endDateRadioButton.setEnabled(viewModel.isBeforeEndEnabled());
        }
    }

    private void onStartDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                specificDateRadioButton.setChecked(false);
                daysBeforeRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setDateSpecOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                viewModel.setDateSpecOption((daysBeforeRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.AFTER_START_DATE);
            }
            endDateRadioButton.setEnabled(true);
        }
    }

    private void onDaysAfterRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            endDateRadioButton.setEnabled(true);
            if (specificDateRadioButton.isChecked()) {
                specificDateRadioButton.setChecked(false);
                endDateRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setDateSpecOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setDateSpecOption((endDateRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.AFTER_START_DATE);
            }
        }
    }

    private void onEndDateDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                specificDateRadioButton.setChecked(false);
                daysAfterRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setDateSpecOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setDateSpecOption((daysAfterRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.BEFORE_END_DATE);
            }
        }
    }

    private void onSpecificDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            endDateRadioButton.setEnabled(true);
            specificDateTextView.setClickable(true);
            daysBeforeRadioButton.setChecked(false);
            daysAfterRadioButton.setChecked(false);
            startDateRadioButton.setChecked(false);
            endDateRadioButton.setChecked(false);
            daysEditText.setClickable(false);
            daysEditText.setEnabled(false);
            viewModel.setDateSpecOption(AlertDateOption.EXPLICIT);
        }
    }

    private void onValidChanged(Boolean isValid) {
        saveImageButton.setEnabled(null != isValid && isValid);
    }

}