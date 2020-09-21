package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

public class EditCourseFragment extends Fragment {

    private static final String LOG_TAG = EditCourseFragment.class.getName();

    private EditCourseViewModel viewModel;
    private Button termButton;
    private EditText courseCodeEditText;
    private EditText competencyUnitsEditText;
    private EditText titleEditText;
    private Chip mentorChip;
    private Button statusButton;
    private TabLayout otherTabLayout;
    private ViewPager otherViewPager;
    private EditCoursePagerAdapter sectionsPagerAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditCourseFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCreateView");
        return inflater.inflate(R.layout.fragment_edit_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        termButton = view.findViewById(R.id.termButton);
        courseCodeEditText = view.findViewById(R.id.courseCodeEditText);
        competencyUnitsEditText = view.findViewById(R.id.competencyUnitsEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        mentorChip = view.findViewById(R.id.mentorChip);
        statusButton = view.findViewById(R.id.statusButton);
        otherTabLayout = view.findViewById(R.id.otherTabLayout);
        otherViewPager = view.findViewById(R.id.otherViewPager);

        termButton.setOnClickListener(this::onTermButtonClick);
        mentorChip.setOnClickListener(this::onMentorChipClick);
        mentorChip.setOnCloseIconClickListener(this::onMentorChipCloseIconClick);
        statusButton.setOnClickListener(this::onStatusButtonClick);
        view.findViewById(R.id.saveCourseButton).setOnClickListener(this::onSaveCourseButtonClick);
        view.findViewById(R.id.deleteCourseButton).setOnClickListener(this::onDeleteCourseButtonClick);
        view.findViewById(R.id.cancelButton).setOnClickListener(this::onCancelButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onEntityLoaded);
        viewModel.getTermsLiveData().observe(getViewLifecycleOwner(), this::onTermsLoaded);
        viewModel.getMentorsLiveData().observe(getViewLifecycleOwner(), this::onMentorsLoaded);
        sectionsPagerAdapter = new EditCoursePagerAdapter(requireContext(), requireActivity().getSupportFragmentManager());
        otherViewPager.setAdapter(sectionsPagerAdapter);
        otherTabLayout.setupWithViewPager(otherViewPager);
        courseCodeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNumber));
        competencyUnitsEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCompetencyUnitsText));
        titleEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setTitle));
    }

    private void onEntityLoaded(CourseDetails entity) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onEntityLoaded
    }

    private void onTermsLoaded(List<TermListItem> termListItems) {

    }

    private void onMentorsLoaded(List<MentorListItem> mentorListItems) {

    }

    private void onTermButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onTermButtonClick
    }

    private void onMentorChipClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onMentorChipClick
    }

    private void onMentorChipCloseIconClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onMentorChipCloseIconClick
    }

    private void onStatusButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onStatusButtonClick
    }

    private void onSaveCourseButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onSaveCourseButtonClick
    }

    private void onDeleteCourseButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onDeleteCourseButtonClick
    }

    private void onCancelButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCancelButtonClick
    }

}