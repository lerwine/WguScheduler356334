package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;

public class CourseDatesFragment extends Fragment {

    private EditCourseViewModel viewModel;
    private Chip expectedStartChip;
    private Chip expectedEndChip;
    private Chip actualStartChip;
    private Chip actualEndChip;


    public CourseDatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_dates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expectedStartChip = view.findViewById(R.id.expectedStartChip);
        expectedEndChip = view.findViewById(R.id.expectedEndChip);
        actualStartChip = view.findViewById(R.id.actualStartChip);
        actualEndChip = view.findViewById(R.id.actualEndChip);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onEntityLoaded);
        expectedStartChip.setOnClickListener(this::onExpectedStartChipClick);
        expectedStartChip.setOnCloseIconClickListener(this::onExpectedStartChipCloseIconClick);
        expectedEndChip.setOnClickListener(this::onExpectedEndChipClick);
        expectedEndChip.setOnCloseIconClickListener(this::onExpectedEndChipCloseIconClick);
        actualStartChip.setOnClickListener(this::onActualStartChipClick);
        actualStartChip.setOnCloseIconClickListener(this::onActualStartChipCloseIconClick);
        actualEndChip.setOnClickListener(this::onActualEndChipClick);
        actualEndChip.setOnCloseIconClickListener(this::onActualEndChipCloseIconClick);
    }

    private void onEntityLoaded(CourseDetails entity) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.CourseDatesFragment.onEntityLoaded
    }

    private void onExpectedStartChipClick(View view) {

    }

    private void onExpectedStartChipCloseIconClick(View view) {

    }

    private void onExpectedEndChipClick(View view) {

    }

    private void onExpectedEndChipCloseIconClick(View view) {

    }

    private void onActualStartChipClick(View view) {

    }

    private void onActualStartChipCloseIconClick(View view) {

    }

    private void onActualEndChipClick(View view) {

    }

    private void onActualEndChipCloseIconClick(View view) {

    }

}