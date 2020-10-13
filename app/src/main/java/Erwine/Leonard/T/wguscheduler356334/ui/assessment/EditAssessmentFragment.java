package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditAssessmentFragment extends Fragment {

    private static final String LOG_TAG = EditAssessmentFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    @SuppressWarnings("FieldCanBeLocal")
    // TODO: Use OneTimeObserver instead
    private final CompositeDisposable compositeDisposable;
    private EditAssessmentViewModel viewModel;
    private Button courseButton;
    private EditText codeEditText;
    private Button typeButton;
    private Button statusButton;
    private Chip goalDateChip;
    private Chip completionDateChip;
    private EditText notesEditText;

    public EditAssessmentFragment() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_assessment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        courseButton = view.findViewById(R.id.courseButton);
        codeEditText = view.findViewById(R.id.codeEditText);
        typeButton = view.findViewById(R.id.typeButton);
        statusButton = view.findViewById(R.id.statusButton);
        goalDateChip = view.findViewById(R.id.goalDateChip);
        completionDateChip = view.findViewById(R.id.completionDateChip);
        notesEditText = view.findViewById(R.id.notesEditText);
        courseButton.setOnClickListener(this::onCourseButtonClick);
        typeButton.setOnClickListener(this::onTypeButtonClick);
        statusButton.setOnClickListener(this::onStatusButtonClick);
        goalDateChip.setOnClickListener(this::onGoalDateChipClick);
        goalDateChip.setOnCloseIconClickListener(this::onGoalDateChipCloseIconClick);
        completionDateChip.setOnClickListener(this::onCompletionDateChipClick);
        completionDateChip.setOnCloseIconClickListener(this::onCompletionDateChipCloseIconClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAssessmentViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getEntityLiveData().observe(viewLifecycleOwner, this::onEntityLoaded);
    }

    private void onEntityLoaded(AssessmentDetails assessmentDetails) {
        if (null == assessmentDetails) {
            return;
        }
        Log.d(LOG_TAG, String.format("Enter onEntityLoaded(%s)", assessmentDetails));
        codeEditText.setText(viewModel.getCode());
        notesEditText.setText(viewModel.getNotes());
        viewModel.setSelectedCourse(assessmentDetails.getCourse());
        onCourseChanged(viewModel.getSelectedCourse());
        onTypeChanged();
        onStatusChanged();
        onGoalDateChanged();
        onCompletionDateChanged();
        codeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCode));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
    }

    private void onCourseButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCourseButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.loadCourses().subscribe(this::onCoursesLoadedForPicker, this::onLoadCourseOptionsListError));
    }

    private void onCoursesLoadedForPicker(List<TermCourseListItem> termCourseListItems) {
        AlertHelper.showSingleSelectDialog(R.string.title_select_course, viewModel.getSelectedCourse(), termCourseListItems, requireContext(), AbstractCourseEntity::toPickerItemDescription, t -> {
            viewModel.setSelectedCourse(t);
            onCourseChanged(t);
        });
    }

    private void onLoadCourseOptionsListError(Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_read_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage()))
                .setIcon(R.drawable.dialog_error)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create();
        dlg.show();
    }

    private void onTypeButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onStatusButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_type, viewModel.getType(), Arrays.asList(AssessmentType.values()), requireContext(), t -> resources.getString(t.displayResourceId()), t -> {
            if (viewModel.getType() != t) {
                viewModel.setType(t);
                onTypeChanged();
            }
        });
    }

    private void onStatusButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onStatusButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_status, viewModel.getStatus(), Arrays.asList(AssessmentStatus.values()), requireContext(), t -> resources.getString(t.displayResourceId()), t -> {
            if (viewModel.getStatus() != t) {
                viewModel.setStatus(t);
                onStatusChanged();
            }
        });
    }

    private void onGoalDateChipClick(View view) {
        LocalDate date = viewModel.getGoalDate();
        if (null == date) {
            AbstractCourseEntity<?> course = viewModel.getSelectedCourse();
            if (null != course) {
                if (null == (date = course.getActualEnd()) && null == (date = course.getExpectedEnd())) {
                    date = LocalDate.now();
                }
            } else {
                date = LocalDate.now();
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getGoalDate())) {
                viewModel.setGoalDate(v);
                onGoalDateChanged();
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onGoalDateChipCloseIconClick(View view) {
        if (null != viewModel.getGoalDate()) {
            viewModel.setGoalDate(null);
            onGoalDateChanged();
        }
    }

    private void onCompletionDateChipClick(View view) {
        LocalDate date = viewModel.getCompletionDate();
        if (null == date && null == (date = viewModel.getGoalDate())) {
            AbstractCourseEntity<?> course = viewModel.getSelectedCourse();
            if (null != course) {
                if (null == (date = course.getActualEnd()) && null == (date = course.getExpectedEnd())) {
                    date = LocalDate.now();
                }
            } else {
                date = LocalDate.now();
            }
        }
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> {
            LocalDate v = LocalDate.of(y, m + 1, d);
            if (!v.equals(viewModel.getCompletionDate())) {
                viewModel.setCompletionDate(v);
                onCompletionDateChanged();
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onCompletionDateChipCloseIconClick(View view) {
        if (null != viewModel.getCompletionDate()) {
            viewModel.setCompletionDate(null);
            onCompletionDateChanged();
        }
    }

    private void onCourseChanged(AbstractCourseEntity<?> course) {
        if (null == course) {
            Log.d(LOG_TAG, "Enter onCourseChanged(null)");
            courseButton.setText(R.string.label_none);
        } else {
            Log.d(LOG_TAG, String.format("Enter onCourseChanged(%s)", course));
            courseButton.setText(course.getTitle());
        }
    }

    private void onTypeChanged() {
        typeButton.setText(viewModel.getType().displayResourceId());
    }

    private void onStatusChanged() {
        Log.d(LOG_TAG, "Enter onStatusChanged");
        statusButton.setText(viewModel.getStatus().displayResourceId());
        onGoalDateChanged();
        onCompletionDateChanged();
    }

    private void onCompletionDateChanged() {
        LocalDate d = viewModel.getCompletionDate();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onCompletionDateChanged(null)");
            completionDateChip.setText("");
            completionDateChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onCompletionDateChanged(%s)", d));
            completionDateChip.setText(FORMATTER.format(d));
            completionDateChip.setCloseIconVisible(true);
        }
    }

    private void onGoalDateChanged() {
        LocalDate d = viewModel.getGoalDate();
        if (null == d) {
            Log.d(LOG_TAG, "Enter onGoalDateChanged(null)");
            goalDateChip.setText("");
            goalDateChip.setCloseIconVisible(false);
        } else {
            Log.d(LOG_TAG, String.format("Enter onGoalDateChanged(%s)", d));
            goalDateChip.setText(FORMATTER.format(d));
            goalDateChip.setCloseIconVisible(true);
        }
    }

}