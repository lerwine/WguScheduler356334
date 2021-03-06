package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAlertDialog extends DialogFragment {

    private static final String LOG_TAG = MainActivity.getLogTag(EditAlertDialog.class);
    private final Observer<? super ResourceMessageResult> initializationFailureObserver;
    private EditAlertViewModel viewModel;
    private TextView eventDateTextView;
    private EditText daysEditText;
    private TextView specificDateTextView;
    private RadioButton specificDateRadioButton;
    private RadioButton daysBeforeRadioButton;
    private RadioButton daysAfterRadioButton;
    private RadioButton startDateRadioButton;
    private RadioButton endDateRadioButton;
    private TextView alertTimeValueTextView;
    private RadioButton defaultTimeRadioButton;
    private RadioButton explicitTimeRadioButton;
    private TextView calculatedDateValueTextView;
    private EditText messageEditText;
    private ImageButton saveImageButton;
    private ImageButton deleteImageButton;
    private ImageButton cancelImageButton;
    private TextWatcher daysEditTextWatcher;

    public EditAlertDialog() {
        Log.d(LOG_TAG, "Constructing");
        initializationFailureObserver = this::onInitializationFailure;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
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
        alertTimeValueTextView = view.findViewById(R.id.alertTimeValueTextView);
        defaultTimeRadioButton = view.findViewById(R.id.defaultTimeRadioButton);
        explicitTimeRadioButton = view.findViewById(R.id.explicitTimeRadioButton);
        calculatedDateValueTextView = view.findViewById(R.id.calculatedDateValueTextView);
        messageEditText = view.findViewById(R.id.messageEditText);
        saveImageButton = view.findViewById(R.id.saveImageButton);
        deleteImageButton = view.findViewById(R.id.deleteImageButton);
        cancelImageButton = view.findViewById(R.id.cancelImageButton);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAlertViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        ObserverHelper.subscribeOnce(viewModel.initializeViewModelState(savedInstanceState, this::getArguments), viewLifecycleOwner, this::onAlertEntityLoaded);
        viewModel.getInitializationFailureLiveData().observe(viewLifecycleOwner, initializationFailureObserver);
        viewModel.getDaysValidationMessageLiveData().observe(viewLifecycleOwner, resourceMessageFactory -> {
            if (resourceMessageFactory.isPresent()) {
                daysEditText.setError(resourceMessageFactory.get().apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                        resourceMessageFactory.get().getLevel().getErrorIcon()));
            } else {
                daysEditText.setError(null);
            }
        });
        viewModel.getSelectedDateValidationMessageLiveData().observe(viewLifecycleOwner, resourceMessageFactory -> {
            if (resourceMessageFactory.isPresent()) {
                ResourceMessageFactory f = resourceMessageFactory.get();
                specificDateTextView.setError(f.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                        f.getLevel().getErrorIcon()));
            } else {
                specificDateTextView.setError(null);
            }
        });
        viewModel.getEventDateStringLiveData().observe(viewLifecycleOwner, this::onEventDateChanged);
        viewModel.getSelectedDateStringLiveData().observe(viewLifecycleOwner, this::onSelectedDateStringChanged);
        viewModel.getEffectiveTimeStringLiveData().observe(viewLifecycleOwner, t -> alertTimeValueTextView.setText(t));
        viewModel.getEffectiveAlertDateTimeStringLiveData().observe(viewLifecycleOwner, this::onCalculatedDateChanged);
        viewModel.getSelectedOptionLiveData().observe(viewLifecycleOwner, this::onSelectedOptionChanged);
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveViewModelState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onAlertEntityLoaded(@NonNull AlertEntity alertEntity) {
        viewModel.getInitializationFailureLiveData().removeObserver(initializationFailureObserver);
        long id = alertEntity.getId();
        if (ID_NEW == id) {
            deleteImageButton.setEnabled(false);
            deleteImageButton.setVisibility(View.GONE);
        } else {
            deleteImageButton.setEnabled(true);
            deleteImageButton.setVisibility(View.VISIBLE);
        }
        daysEditText.setText(viewModel.getDaysText());
        startDateRadioButton.setText(viewModel.getStartLabelTextResourceId());
        endDateRadioButton.setText(viewModel.getEndLabelTextResourceId());
        if (viewModel.isExplicitTime()) {
            defaultTimeRadioButton.setChecked(false);
            explicitTimeRadioButton.setChecked(true);
        } else {
            defaultTimeRadioButton.setChecked(true);
            explicitTimeRadioButton.setChecked(false);
        }
        messageEditText.setText(viewModel.getCustomMessage());

        if (null == daysEditTextWatcher) {
            daysEditTextWatcher = StringHelper.textWatcherForTextChanged(viewModel::setDaysText);
            daysEditText.addTextChangedListener(daysEditTextWatcher);
            messageEditText.addTextChangedListener(StringHelper.textWatcherForTextChanged(viewModel::setCustomMessage));
            specificDateTextView.setOnClickListener(this::onSpecificDateTextViewClick);
            saveImageButton.setOnClickListener(this::onSaveButtonClick);
            deleteImageButton.setOnClickListener(this::onDeleteButtonClick);
            cancelImageButton.setOnClickListener(this::onCancelButtonClick);
            specificDateRadioButton.setOnCheckedChangeListener(this::onSpecificDateRadioButtonCheckedChange);
            daysBeforeRadioButton.setOnCheckedChangeListener(this::onDaysBeforeRadioButtonCheckedChange);
            startDateRadioButton.setOnCheckedChangeListener(this::onStartDateRadioButtonCheckedChange);
            daysAfterRadioButton.setOnCheckedChangeListener(this::onDaysAfterRadioButtonCheckedChange);
            endDateRadioButton.setOnCheckedChangeListener(this::onEndDateDateRadioButtonCheckedChange);
            defaultTimeRadioButton.setOnCheckedChangeListener(this::onDefaultTimeRadioButtonCheckedChange);
            explicitTimeRadioButton.setOnCheckedChangeListener(this::onExplicitTimeRadioButtonCheckedChange);
            alertTimeValueTextView.setOnClickListener(this::onAlertTimeValueTextViewClick);
        }
        if (ID_NEW == id) {
            viewModel.getValidLiveData().observe(getViewLifecycleOwner(), this::onCanSaveChanged);
        } else {
            viewModel.getCanSaveLiveData().observe(getViewLifecycleOwner(), this::onCanSaveChanged);
        }
    }

    private void onCanSaveChanged(Boolean canSave) {
        saveImageButton.setEnabled(canSave);
    }

    private void onSpecificDateTextViewClick(View view) {
        ObserverHelper.observeOnce(viewModel.getEffectiveAlertDateTimeValueLiveData(), getViewLifecycleOwner(), effectiveAlertDateTime -> {
            LocalDate date = effectiveAlertDateTime.map(LocalDateTime::toLocalDate).orElseGet(() -> {
                LocalDate d = viewModel.getSelectedDate();
                return (null == d) ? LocalDate.now() : d;
            });
            new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> viewModel.setSelectedDate(LocalDate.of(y, m + 1, d)), date.getYear(),
                    date.getMonthValue() - 1, date.getDayOfMonth()).show();
        });
    }

    private void onEventDateChanged(String text) {
        if (null != text) {
            eventDateTextView.setText(text);
        } else {
            eventDateTextView.setText(getResources().getString(R.string.message_missing_parentheses));
        }
    }

    private void onSelectedDateStringChanged(String text) {
        specificDateTextView.setText((null == text) ? "" : text);
    }

    private void onCalculatedDateChanged(String text) {
        if (null != text) {
            calculatedDateValueTextView.setText(text);
        } else {
            calculatedDateValueTextView.setText(getResources().getString(R.string.message_invalid_parentheses));
        }
    }

    private void onInitializationFailure(@NonNull ResourceMessageResult messages) {
        viewModel.getInitializationFailureLiveData().removeObserver(initializationFailureObserver);
        FragmentActivity activity = requireActivity();
        requireDialog().dismiss();
        AlertDialog dlg = new AlertDialog.Builder(activity).setTitle(R.string.title_read_error)
                .setIcon(R.drawable.dialog_error).setMessage(messages.join("\n", getResources()))
                .setCancelable(true).create();
        dlg.show();
    }

    private void onSaveButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveButtonClick");
        ObserverHelper.observeOnce(DbLoader.getPreferAlertTime(), getViewLifecycleOwner(), t ->
                ObserverHelper.subscribeOnce(viewModel.save(false), getViewLifecycleOwner(), new SaveOperationListener(viewModel.getOriginalEventDateTime(t).orElse(null)))
        );
    }

    private void onDeleteButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_alert, R.string.message_delete_alert_confirm, requireContext()).showYesNoDialog(() ->
                ObserverHelper.observeOnce(DbLoader.getPreferAlertTime(), getViewLifecycleOwner(), t ->
                        ObserverHelper.subscribeOnce(viewModel.delete(), this, new DeleteOperationListener(viewModel.getOriginalEventDateTime(t).orElse(null)))
                ), null);
    }

    private void onCancelButtonClick(View view) {
        dismiss();
    }

    private void onDaysBeforeRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                viewModel.setAlertDateOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                if (endDateRadioButton.isChecked() && !viewModel.isBeforeEndAllowed()) {
                    viewModel.setAlertDateOption(AlertDateOption.BEFORE_START_DATE);
                } else {
                    viewModel.setAlertDateOption((startDateRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.BEFORE_END_DATE);
                }
            }
            endDateRadioButton.setEnabled(viewModel.isBeforeEndAllowed());
        }
    }

    private void onStartDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                viewModel.setAlertDateOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                viewModel.setAlertDateOption((daysBeforeRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.AFTER_START_DATE);
            }
            endDateRadioButton.setEnabled(true);
        }
    }

    private void onDaysAfterRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            endDateRadioButton.setEnabled(true);
            if (specificDateRadioButton.isChecked()) {
                viewModel.setAlertDateOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setAlertDateOption((endDateRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.AFTER_START_DATE);
            }
        }
    }

    private void onDefaultTimeRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            explicitTimeRadioButton.setChecked(false);
            viewModel.setExplicitTime(false);
            alertTimeValueTextView.setClickable(false);
        }
    }

    private void onExplicitTimeRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            defaultTimeRadioButton.setChecked(false);
            viewModel.setExplicitTime(true);
            alertTimeValueTextView.setClickable(true);
            if (null == viewModel.getSelectedTime()) {
                onAlertTimeValueTextViewClick(alertTimeValueTextView);
            }
        }
    }

    private void onAlertTimeValueTextViewClick(View view) {
        LocalTime time = viewModel.getSelectedTime();
        if (null == time) {
            ObserverHelper.observeOnce(DbLoader.getPreferAlertTime(), getViewLifecycleOwner(), t -> {
                Context context = requireContext();
                new TimePickerDialog(context, (view1, hourOfDay, minute) -> viewModel.setSelectedTime(LocalTime.of(hourOfDay, minute)),
                        t.getHour(), t.getMinute(), DateFormat.is24HourFormat(context)).show();
            });
        } else {
            Context context = requireContext();
            new TimePickerDialog(context, (view1, hourOfDay, minute) -> viewModel.setSelectedTime(LocalTime.of(hourOfDay, minute)),
                    time.getHour(), time.getMinute(), DateFormat.is24HourFormat(context)).show();
        }
    }

    private void onEndDateDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                viewModel.setAlertDateOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setAlertDateOption((daysAfterRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.BEFORE_END_DATE);
            }
        }
    }

    private void onSpecificDateRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            viewModel.setAlertDateOption(AlertDateOption.EXPLICIT);
            if (null == viewModel.getSelectedDate()) {
                onSpecificDateTextViewClick(specificDateTextView);
            }
        }
    }

    private void onSelectedOptionChanged(@NonNull AlertDateOption o) {
        Editable editable;
        switch (o) {
            case AFTER_END_DATE:
                specificDateRadioButton.setChecked(false);
                daysBeforeRadioButton.setChecked(false);
                startDateRadioButton.setChecked(false);
                if (!daysAfterRadioButton.isChecked()) {
                    daysAfterRadioButton.setChecked(true);
                }
                endDateRadioButton.setEnabled(true);
                if (!endDateRadioButton.isChecked()) {
                    endDateRadioButton.setChecked(true);
                }
                break;
            case BEFORE_END_DATE:
                specificDateRadioButton.setChecked(false);
                daysAfterRadioButton.setChecked(false);
                startDateRadioButton.setChecked(false);
                if (!daysBeforeRadioButton.isChecked()) {
                    daysBeforeRadioButton.setChecked(true);
                }
                endDateRadioButton.setEnabled(true);
                if (!endDateRadioButton.isChecked()) {
                    endDateRadioButton.setChecked(true);
                }
                break;
            case AFTER_START_DATE:
                specificDateRadioButton.setChecked(false);
                daysBeforeRadioButton.setChecked(false);
                endDateRadioButton.setEnabled(true);
                endDateRadioButton.setChecked(false);
                if (!daysAfterRadioButton.isChecked()) {
                    daysAfterRadioButton.setChecked(true);
                }
                if (!startDateRadioButton.isChecked()) {
                    startDateRadioButton.setChecked(true);
                }
                break;
            case BEFORE_START_DATE:
                specificDateRadioButton.setChecked(false);
                daysAfterRadioButton.setChecked(false);
                endDateRadioButton.setChecked(false);
                endDateRadioButton.setEnabled(viewModel.isBeforeEndAllowed());
                if (!daysBeforeRadioButton.isChecked()) {
                    daysBeforeRadioButton.setChecked(true);
                }
                if (!startDateRadioButton.isChecked()) {
                    startDateRadioButton.setChecked(true);
                }
                break;
            default:
                daysBeforeRadioButton.setChecked(false);
                daysAfterRadioButton.setChecked(false);
                startDateRadioButton.setChecked(false);
                endDateRadioButton.setChecked(false);
                if (!specificDateRadioButton.isChecked()) {
                    specificDateRadioButton.setChecked(true);
                }
                daysEditText.setClickable(false);
                daysEditText.setEnabled(false);
                specificDateTextView.setClickable(true);
                daysEditText.setText("");
                return;
        }
        daysEditText.setClickable(true);
        daysEditText.setEnabled(true);
        if (null == (editable = daysEditText.getText()) || editable.length() == 0) {
            daysEditText.setText(viewModel.getDaysText());
        }
        specificDateTextView.setClickable(false);
    }

    private class SaveOperationListener implements SingleObserver<ResourceMessageResult> {
        @Nullable
        final LocalDateTime originalDateTime;

        SaveOperationListener(@Nullable LocalDateTime originalDateTime) {
            this.originalDateTime = originalDateTime;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onSuccess(@NonNull ResourceMessageResult messages) {
            Log.d(LOG_TAG, "Enter SaveOperationListener.onSuccess");
            if (messages.isSucceeded()) {
                ObserverHelper.observeOnce(viewModel.getEffectiveAlertDateTimeValueLiveData(), getViewLifecycleOwner(), effectiveAlertDateTime -> {
                    if (effectiveAlertDateTime.isPresent()) {
                        if (viewModel.isCourseAlert()) {
                            CourseAlertBroadcastReceiver.setPendingAlert(effectiveAlertDateTime.get(), (CourseAlertLink) viewModel.getAlertLink(), viewModel.getNotificationId(), requireContext());
                        } else {
                            AssessmentAlertBroadcastReceiver.setPendingAlert(effectiveAlertDateTime.get(), (AssessmentAlertLink) viewModel.getAlertLink(), viewModel.getNotificationId(), requireContext());
                        }
                    } else if (null != originalDateTime) {
                        AlertLink link = viewModel.getAlertLink();
                        if (viewModel.isCourseAlert()) {
                            CourseAlertBroadcastReceiver.cancelPendingAlert(link.getAlertId(), link.getTargetId(), viewModel.getNotificationId(), requireContext());
                        } else {
                            AssessmentAlertBroadcastReceiver.cancelPendingAlert(link.getAlertId(), link.getTargetId(), viewModel.getNotificationId(), requireContext());
                        }
                    }
                    dismiss();
                });
            } else {
                Resources resources = getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                if (messages.isWarning()) {
                    builder.setTitle(R.string.title_save_warning)
                            .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                            .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                                dialog.dismiss();
                                ObserverHelper.subscribeOnce(viewModel.save(true), EditAlertDialog.this.getViewLifecycleOwner(), this);
                            }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
                } else {
                    builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
                }
                AlertDialog dlg = builder.setCancelable(true).create();
                dlg.show();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(LOG_TAG, "Error saving assessment", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, e.getMessage()), requireContext())
                    .showDialog(() -> requireActivity().finish());
        }
    }

    private class DeleteOperationListener implements CompletableObserver {
        @Nullable
        private final LocalDateTime originalDateTime;

        DeleteOperationListener(@Nullable LocalDateTime originalDateTime) {
            this.originalDateTime = originalDateTime;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onComplete() {
            Log.d(LOG_TAG, "Enter DeleteOperationListener.onComplete");
            if (null != originalDateTime) {
                AlertLink link = viewModel.getAlertLink();
                if (viewModel.isCourseAlert()) {
                    CourseAlertBroadcastReceiver.cancelPendingAlert(link.getAlertId(), link.getTargetId(), viewModel.getNotificationId(), requireContext());
                } else {
                    AssessmentAlertBroadcastReceiver.cancelPendingAlert(link.getAlertId(), link.getTargetId(), viewModel.getNotificationId(), requireContext());
                }
            }
            dismiss();
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(LOG_TAG, "Error deleting alert", e);
            new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, e.getMessage()), requireContext()).showDialog();
        }
    }
}