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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

public class EditAssessmentFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(EditAssessmentFragment.class);

    private EditAssessmentViewModel viewModel;
    private Button courseButton;
    private EditText codeEditText;
    private EditText nameEditText;
    private Button typeButton;
    private Button statusButton;
    private Chip goalDateChip;
    private Chip completionDateChip;
    private EditText notesEditText;

    public EditAssessmentFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_edit_assessment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        courseButton = view.findViewById(R.id.courseButton);
        codeEditText = view.findViewById(R.id.codeEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
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
        viewModel.getCourseDisplayLiveData().observe(viewLifecycleOwner, f -> courseButton.setText(f.apply(getResources())));
        viewModel.getTypeDisplayLiveData().observe(viewLifecycleOwner, typeButton::setText);
        viewModel.getStatusDisplayLiveData().observe(viewLifecycleOwner, statusButton::setText);
        viewModel.getGoalDateDisplayLiveData().observe(viewLifecycleOwner, goalDateChip::setText);
        viewModel.getShowGoalDateCloseIconLiveData().observe(viewLifecycleOwner, goalDateChip::setCloseIconVisible);
        viewModel.getCompletionDateDisplayLiveData().observe(viewLifecycleOwner, completionDateChip::setText);
        viewModel.getShowCompletionDateCloseIconLiveData().observe(viewLifecycleOwner, completionDateChip::setCloseIconVisible);
        viewModel.getCodeValidLiveData().observe(viewLifecycleOwner, v -> {
            if (v) {
                codeEditText.setError(getResources().getString(R.string.message_required), AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
            } else {
                codeEditText.setError(null);
            }
        });
        ObserverHelper.subscribeOnce(viewModel.getInitializedCompletable(), viewLifecycleOwner, this::onViewModelInitialized);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onViewModelInitialized() {
        Log.d(LOG_TAG, "Enter onEntityLoaded");
        codeEditText.setText(viewModel.getCode());
        nameEditText.setText(viewModel.getName());
        notesEditText.setText(viewModel.getNotes());
        codeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCode));
        nameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setName));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
    }

    private void onCourseButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCourseButtonClick");
        ObserverHelper.subscribeOnce(viewModel.loadCourses(), getViewLifecycleOwner(), this::onCoursesLoadedForPicker, this::onLoadCourseOptionsListError);
    }

    private void onCoursesLoadedForPicker(@NonNull List<TermCourseListItem> termCourseListItems) {
        ObserverHelper.observeOnce(viewModel.getSelectedCourseLiveData(), this,
                abstractCourseEntity -> AlertHelper.showSingleSelectDialog(R.string.title_select_course, abstractCourseEntity, termCourseListItems, requireContext(),
                        AbstractCourseEntity::toPickerItemDescription, t -> viewModel.setSelectedCourse(t)), true);
    }

    private void onLoadCourseOptionsListError(@NonNull Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_read_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage()))
                .setIcon(R.drawable.dialog_error)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create();
        dlg.show();
    }

    private void onTypeButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onTypeButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_type, viewModel.getType(), Arrays.asList(AssessmentType.values()), requireContext(),
                t -> resources.getString(t.displayResourceId()),
                t -> viewModel.setType(t));
    }

    private void onStatusButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onStatusButtonClick");
        Resources resources = getResources();
        AlertHelper.showSingleSelectDialog(R.string.title_select_status, viewModel.getStatus(), Arrays.asList(AssessmentStatus.values()), requireContext(),
                t -> resources.getString(t.displayResourceId()),
                t -> viewModel.setStatus(t));
    }

    private void onGoalDateChipClick(View view) {
        LocalDate date = viewModel.getGoalDate();
        if (null != date) {
            showGoalDatePicker(date);
        } else {
            ObserverHelper.observeOnce(viewModel.getSelectedCourseLiveData(), this, course -> {
                LocalDate d;
                if (null == course || (null == (d = course.getActualEnd()) && null == (d = course.getExpectedEnd()))) {
                    showGoalDatePicker(LocalDate.now());
                } else {
                    showGoalDatePicker(d);
                }
                showGoalDatePicker(LocalDate.now());
            }, true);
        }
    }

    private void showGoalDatePicker(@NonNull LocalDate date) {
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> viewModel.setGoalDate(LocalDate.of(y, m + 1, d)),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onGoalDateChipCloseIconClick(View view) {
        viewModel.setGoalDate(null);
    }

    private void onCompletionDateChipClick(View view) {
        LocalDate date = viewModel.getCompletionDate();
        if (null != date || null != (date = viewModel.getGoalDate())) {
            showCompletionDatePicker(date);
        } else {
            ObserverHelper.observeOnce(viewModel.getSelectedCourseLiveData(), this, course -> {
                LocalDate d;
                if (null == course || (null == (d = course.getActualEnd()) && null == (d = course.getExpectedEnd()))) {
                    showCompletionDatePicker(LocalDate.now());
                } else {
                    showCompletionDatePicker(d);
                }
            }, true);
        }
    }

    private void showCompletionDatePicker(@NonNull LocalDate date) {
        new DatePickerDialog(requireActivity(), (datePicker, y, m, d) -> viewModel.setCompletionDate(LocalDate.of(y, m + 1, d)),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void onCompletionDateChipCloseIconClick(View view) {
        viewModel.setCompletionDate(null);
    }

}