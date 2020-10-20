package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAlertDialog extends DialogFragment {

    private static final String LOG_TAG = EditAlertDialog.class.getName();
    private final Observer<Boolean> canSaveObserver = this::onCanSaveChanged;
    private LiveData<Boolean> canSaveObserved;
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
        alertTimeValueTextView = view.findViewById(R.id.alertTimeValueTextView);
        defaultTimeRadioButton = view.findViewById(R.id.defaultTimeRadioButton);
        explicitTimeRadioButton = view.findViewById(R.id.explicitTimeRadioButton);
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
        viewModel.getDaysValidationMessageLiveData().observe(viewLifecycleOwner, resourceMessageFactory -> {
            if (resourceMessageFactory.isPresent()) {
                daysEditText.setError(resourceMessageFactory.get().apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                        (resourceMessageFactory.get().isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
            } else {
                daysEditText.setError(null);
            }
        });
        viewModel.getSelectedDateValidationMessageLiveData().observe(viewLifecycleOwner, resourceMessageFactory -> {
            if (resourceMessageFactory.isPresent()) {
                ResourceMessageFactory f = resourceMessageFactory.get();
                specificDateTextView.setError(f.apply(getResources()), AppCompatResources.getDrawable(requireContext(),
                        (f.isWarning()) ? R.drawable.dialog_warning : R.drawable.dialog_error));
            } else {
                specificDateTextView.setError(null);
            }
        });
        viewModel.getEventDateStringLiveData().observe(viewLifecycleOwner, this::onEventDateChanged);
        viewModel.getEffectiveTimeStringLiveData().observe(viewLifecycleOwner, t -> alertTimeValueTextView.setText(t));
        viewModel.getEffectiveAlertDateTimeStringLiveData().observe(viewLifecycleOwner, this::onCalculatedDateChanged);
    }

    private void onAlertEntityLoaded(@NonNull AlertEntity alertEntity) {
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
        AlertDateOption option = viewModel.getAlertDateOption();
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
            if (viewModel.isBeforeEndAllowed() || !option.isBefore()) {
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
        } else if (null != canSaveObserved) {
            canSaveObserved.removeObserver(canSaveObserver);
        }
        if (ID_NEW == id) {
            canSaveObserved = viewModel.getValidLiveData();
        } else {
            canSaveObserved = viewModel.getCanSaveLiveData();
        }
        viewModel.getCanSaveLiveData().observe(getViewLifecycleOwner(), canSaveObserver);
    }

    private void onCanSaveChanged(Boolean canSave) {
        saveImageButton.setEnabled(canSave);
    }

    private void onSpecificDateTextViewClick(View view) {
        OneTimeObservers.observeOnce(viewModel.getEffectiveAlertDateTimeValueLiveData(), getViewLifecycleOwner(), effectiveAlertDateTime -> {
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
        OneTimeObservers.observeOnce(DbLoader.getPreferAlertTime(), t ->
                OneTimeObservers.subscribeOnce(viewModel.save(false), new SaveOperationListener(viewModel.getOriginalEventDateTime(t).orElse(null)))
        );
    }

    private void onDeleteButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_alert, R.string.message_delete_alert_confirm, requireContext()).showYesNoDialog(() ->
                OneTimeObservers.subscribeOnce(viewModel.delete(), new DeleteOperationListener()), null);
    }

    private void onCancelButtonClick(View view) {
        dismiss();
    }

    private void onDaysBeforeRadioButtonCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if (specificDateRadioButton.isChecked()) {
                specificDateRadioButton.setChecked(false);
                startDateRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setAlertDateOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                if (endDateRadioButton.isChecked() && !viewModel.isBeforeEndAllowed()) {
                    endDateRadioButton.setChecked(false);
                    startDateRadioButton.setChecked(true);
                }
                viewModel.setAlertDateOption((startDateRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.BEFORE_END_DATE);
                daysAfterRadioButton.setChecked(false);
            }
            endDateRadioButton.setEnabled(viewModel.isBeforeEndAllowed());
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
                viewModel.setAlertDateOption(AlertDateOption.BEFORE_START_DATE);
            } else {
                viewModel.setAlertDateOption((daysBeforeRadioButton.isChecked()) ? AlertDateOption.BEFORE_START_DATE : AlertDateOption.AFTER_START_DATE);
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
                viewModel.setAlertDateOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setAlertDateOption((endDateRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.AFTER_START_DATE);
                daysBeforeRadioButton.setChecked(false);
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
            OneTimeObservers.observeOnce(DbLoader.getPreferAlertTime(), t -> {
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
                specificDateRadioButton.setChecked(false);
                daysAfterRadioButton.setChecked(true);
                specificDateTextView.setClickable(false);
                daysEditText.setClickable(true);
                daysEditText.setEnabled(true);
                viewModel.setAlertDateOption(AlertDateOption.AFTER_END_DATE);
            } else {
                viewModel.setAlertDateOption((daysAfterRadioButton.isChecked()) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.BEFORE_END_DATE);
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
            viewModel.setAlertDateOption(AlertDateOption.EXPLICIT);
        }
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
                OneTimeObservers.observeOnce(viewModel.getEffectiveAlertDateTimeValueLiveData(), getViewLifecycleOwner(), effectiveAlertDateTime -> {
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
                                OneTimeObservers.subscribeOnce(viewModel.save(true), this);
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
        Integer notificationId;

        DeleteOperationListener() {
            OneTimeObservers.observeOnce(viewModel.getEffectiveAlertDateTimeValueLiveData(), getViewLifecycleOwner(), effectiveAlertDateTime -> {
                if (effectiveAlertDateTime.isPresent()) {
                    notificationId = viewModel.getNotificationId();
                }
            });
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
        }

        @Override
        public void onComplete() {
            Log.d(LOG_TAG, "Enter DeleteOperationListener.onComplete");
            if (null != notificationId) {
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