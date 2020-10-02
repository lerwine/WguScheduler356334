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

public class EditAlertFragment extends DialogFragment {

    private EditAlertViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_alert_fragment, container, false);
    }

    private TextView typeTextView;
    private TextView codeTextView;
    private TextView titleTextView;
    private TextView eventDateDescriptionTextView;
    private EditText daysBeforeStartEditText;
    private RadioButton daysBeforeStartRadioButton;
    private EditText daysBeforeEndEditText;
    private RadioButton daysBeforeEndRadioButton;
    private EditText specificDateEditText;
    private RadioButton specificDateRadioButton;
    private EditText messageEditText;
    private ImageButton saveImageButton;
    private ImageButton deleteImageButton;
    private ImageButton cancelImageButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeTextView = view.findViewById(R.id.typeTextView);
        codeTextView = view.findViewById(R.id.codeTextView);
        titleTextView = view.findViewById(R.id.titleTextView);
        eventDateDescriptionTextView = view.findViewById(R.id.eventDateDescriptionTextView);
        daysBeforeStartEditText = view.findViewById(R.id.daysBeforeStartEditText);
        daysBeforeStartRadioButton = view.findViewById(R.id.daysBeforeStartRadioButton);
        daysBeforeEndEditText = view.findViewById(R.id.daysBeforeEndEditText);
        daysBeforeEndRadioButton = view.findViewById(R.id.daysBeforeEndRadioButton);
        specificDateEditText = view.findViewById(R.id.specificDateEditText);
        specificDateRadioButton = view.findViewById(R.id.specificDateRadioButton);
        messageEditText = view.findViewById(R.id.messageEditText);
        saveImageButton = view.findViewById(R.id.saveImageButton);
        deleteImageButton = view.findViewById(R.id.deleteImageButton);
        cancelImageButton = view.findViewById(R.id.cancelImageButton);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAlertViewModel.class);
        viewModel.initializeViewModelState(savedInstanceState, this::getArguments);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getAlertLiveData().observe(viewLifecycleOwner, this::onAlertLoaded);
        viewModel.getInitializationFailureLiveData().observe(viewLifecycleOwner, this::onLoadFailed);
        viewModel.getAlertDateLiveData().observe(viewLifecycleOwner, this::onAlertDateChanged);
        viewModel.getBeforeStartValidationLiveData().observe(viewLifecycleOwner, this::onBeforeStartValidationChanged);
        viewModel.getBeforeEndValidationLiveData().observe(viewLifecycleOwner, this::onBeforeEndValidationChanged);
        viewModel.getSelectedDateValidationLiveData().observe(viewLifecycleOwner, this::onSelectedDateValidationLChanged);
        viewModel.getValidLiveData().observe(viewLifecycleOwner, this::onValidChanged);
    }

    private void onAlertLoaded(@NonNull AlertEntity alertEntity) {
        long id = alertEntity.getId();
        if (ID_NEW == id) {
            deleteImageButton.setEnabled(false);
            deleteImageButton.setVisibility(View.GONE);
        }
        typeTextView.setText(viewModel.getType());
        codeTextView.setText(viewModel.getCode());
        titleTextView.setText(viewModel.getTitle());
        daysBeforeStartEditText.setText(viewModel.getBeforeStartText());
        daysBeforeEndEditText.setText(viewModel.getBeforeEndText());
        LocalDate d = viewModel.getSelectedDate();
        if (null != d) {
            specificDateEditText.setText(EditAlertViewModel.DATE_FORMATTER.format(d));
        } else {
            specificDateEditText.setText("");
        }

        switch (viewModel.getDateSpecOption()) {
            case START_DATE:
                daysBeforeStartRadioButton.setChecked(true);
                daysBeforeEndRadioButton.setChecked(false);
                specificDateRadioButton.setChecked(false);
                daysBeforeStartEditText.setEnabled(true);
                daysBeforeEndEditText.setEnabled(false);
                specificDateEditText.setClickable(false);
                specificDateEditText.setEnabled(false);
                break;
            case END_DATE:
                daysBeforeStartRadioButton.setChecked(false);
                daysBeforeEndRadioButton.setChecked(true);
                specificDateRadioButton.setChecked(false);
                daysBeforeStartEditText.setEnabled(false);
                daysBeforeEndEditText.setEnabled(true);
                specificDateEditText.setClickable(false);
                specificDateEditText.setEnabled(false);
                break;
            default:
                daysBeforeStartRadioButton.setChecked(false);
                daysBeforeEndRadioButton.setChecked(false);
                specificDateRadioButton.setChecked(true);
                daysBeforeStartEditText.setEnabled(false);
                daysBeforeEndEditText.setEnabled(false);
                specificDateEditText.setClickable(true);
                specificDateEditText.setEnabled(true);
                break;
        }

        daysBeforeStartEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setBeforeStartText));
        daysBeforeEndEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setBeforeEndText));
        messageEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setMessage));
        saveImageButton.setOnClickListener(this::onSaveButtonClick);
        deleteImageButton.setOnClickListener(this::onDeleteButtonClick);
        cancelImageButton.setOnClickListener(this::onCancelButtonClick);
        daysBeforeStartRadioButton.setOnCheckedChangeListener(this::onDaysBeforeStartRadioButtonCheckedChange);
        daysBeforeEndRadioButton.setOnCheckedChangeListener(this::onDaysBeforeEndRadioButtonCheckedChange);
        specificDateRadioButton.setOnCheckedChangeListener(this::onSpecificDateRadioButtonCheckedChange);
    }

    private void onAlertDateChanged(LocalDate d) {
        if (null != d) {
            eventDateDescriptionTextView.setText(EditAlertViewModel.DATE_FORMATTER.format(d));
        } else {
            eventDateDescriptionTextView.setText("");
        }
    }

    private void onLoadFailed(@NonNull ValidationMessage.ResourceMessageResult messages) {
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
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.alert.EditAlertFragment.onCancelButtonClick
    }

    private void onBeforeStartValidationChanged(ValidationMessage.ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            daysBeforeStartEditText.setError(null);
        } else {
            daysBeforeStartEditText.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onBeforeEndValidationChanged(ValidationMessage.ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            daysBeforeEndEditText.setError(null);
        } else {
            daysBeforeEndEditText.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onSelectedDateValidationLChanged(ValidationMessage.ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            specificDateEditText.setError(null);
        } else {
            specificDateEditText.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onDaysBeforeStartRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            daysBeforeStartEditText.setEnabled(true);
            viewModel.setDateSpecOption(AlertDateOption.START_DATE);
        } else {
            daysBeforeStartEditText.setEnabled(false);
        }
    }

    private void onDaysBeforeEndRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            daysBeforeEndEditText.setEnabled(true);
            viewModel.setDateSpecOption(AlertDateOption.END_DATE);
        } else {
            daysBeforeEndEditText.setEnabled(false);
        }
    }

    private void onSpecificDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            specificDateEditText.setClickable(true);
            specificDateEditText.setEnabled(true);
            viewModel.setDateSpecOption(AlertDateOption.EXPLICIT);
        } else {
            specificDateEditText.setClickable(false);
            specificDateEditText.setEnabled(false);
        }
    }

    private void onValidChanged(Boolean isValid) {
        saveImageButton.setEnabled(null != isValid && isValid);
    }

}