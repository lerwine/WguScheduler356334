package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

public class AssessmentPropertiesFragment extends Fragment {

    private static final String LOG_TAG = AssessmentPropertiesFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    private final CompositeDisposable compositeDisposable;
    private EditAssessmentViewModel viewModel;
    private Button courseButton;
    private EditText codeEditText;
    private Button typeButton;
    private Button statusButton;
    private Chip goalDateChip;
    private Chip completionDateChip;
    private EditText notesEditText;

    public AssessmentPropertiesFragment() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment_properties, container, false);
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
        completionDateChip.setOnClickListener(this::onCmpletionDateChipClick);
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

        codeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCode));
        notesEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNotes));
        codeEditText.setText(viewModel.getCode());
        notesEditText.setText(viewModel.getNotes());
    }

    private void onCourseButtonClick(View view) {

    }

    private void onTypeButtonClick(View view) {

    }

    private void onStatusButtonClick(View view) {

    }

    private void onGoalDateChipClick(View view) {

    }

    private void onGoalDateChipCloseIconClick(View view) {

    }

    private void onCmpletionDateChipClick(View view) {

    }

    private void onCompletionDateChipCloseIconClick(View view) {

    }

}