package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAlertDialog extends DialogFragment {

    private static final String LOG_TAG = EditAlertDialog.class.getName();
    private EditAlertViewModel viewModel;
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

    public EditAlertDialog() {
        Log.d(LOG_TAG, "Constructing EditAlertDialog");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        daysEditText.setText(viewModel.getDaysText());
        startDateRadioButton.setText(viewModel.getStartLabelTextResourceId());
        endDateRadioButton.setText(viewModel.getEndLabelTextResourceId());
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
        LocalDate date = viewModel.getSelectedDate();
        if (null == date && (null == (date = viewModel.getCalculatedDate()))) {
            date = LocalDate.now();
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> viewModel.setSelectedDate(LocalDate.of(y, m + 1, d)), date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
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

    private void onInitializationFailure(@NonNull ResourceMessageResult messages) {
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
        OneTimeObservers.subscribeOnce(viewModel.save(false), this::onSaveOperationFinished, this::onSaveFailed);
    }

    private void onDeleteButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_alert, R.string.message_delete_alert_confirm, requireContext()).showYesNoDialog(() ->
                OneTimeObservers.subscribeOnce(viewModel.delete(), this::onDeleteSucceeded, this::onDeleteFailed), null);
    }

    private void onCancelButtonClick(View view) {
        dismiss();
    }

    private void onDaysValidationChanged(ResourceMessageFactory resourceMessageFactory) {
        if (null == resourceMessageFactory) {
            daysEditText.setError(null);
        } else {
            daysEditText.setError(resourceMessageFactory.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                    (resourceMessageFactory.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
        }
    }

    private void onSelectedDateValidationLChanged(ResourceMessageFactory resourceMessageFactory) {
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
                if (endDateRadioButton.isChecked() && !viewModel.isBeforeEndEnabled()) {
                    endDateRadioButton.setChecked(false);
                    startDateRadioButton.setChecked(true);
                }
                viewModel.setDateSpecOption((startDateRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.BEFORE_END_DATE);
                daysAfterRadioButton.setChecked(false);
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
                endDateRadioButton.setChecked(false);
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
                daysBeforeRadioButton.setChecked(false);
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
                startDateRadioButton.setChecked(false);
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
        Log.d(LOG_TAG, "Valid changed to " + isValid);
        saveImageButton.setEnabled(null != isValid && isValid);
    }

    private void onSaveOperationFinished(ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onSaveOperationFinished");
        if (messages.isSucceeded()) {
            // TODO: Create/update the alarm

            dismiss();
        } else {
            Resources resources = getResources();
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            dialog.dismiss();
                            OneTimeObservers.subscribeOnce(viewModel.save(true), this::onSaveOperationFinished, this::onSaveFailed);
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            androidx.appcompat.app.AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving assessment", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

    private void onDeleteSucceeded() {
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        dismiss();
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting assessment", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), requireContext()).showDialog();
    }

}