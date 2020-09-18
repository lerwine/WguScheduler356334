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
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StateHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditCourseFragment extends Fragment {

    private static final String LOG_TAG = EditCourseFragment.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;
    private Button termButton;
    private EditText courseCodeEditText;
    private EditText competencyUnitsEditText;
    private EditText titleEditText;
    private Chip mentorChip;
    private Button statusButton;
    private TabLayout otherTabLayout;
    private TabItem datesTabItem;
    private TabItem notesTabItem;
    private ViewPager otherViewPager;
    private EditCoursePagerAdapter sectionsPagerAdapter;

    public static EditCourseFragment newInstance(long courseId) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.newInstance(%d)", courseId));
        return StateHelper.setIdArgs(EditCourseViewModel.ARGUMENT_KEY_COURSE_ID, courseId, new EditCourseFragment(), new Bundle());
    }

    public static EditCourseFragment newInstanceForTerm(long termId) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.newInstanceForTerm(%d)", termId));
        return StateHelper.setIdArgs(EditCourseViewModel.ARGUMENT_KEY_TERM_ID, termId, new EditCourseFragment(), new Bundle());
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditCourseFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment");
        compositeDisposable = new CompositeDisposable();
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
        datesTabItem = view.findViewById(R.id.datesTabItem);
        notesTabItem = view.findViewById(R.id.notesTabItem);
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
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.restoreState(savedInstanceState, this::getArguments).subscribe(this::onEntitySucceeded, this::onLoadFailed));
        sectionsPagerAdapter = new EditCoursePagerAdapter(requireContext(), requireActivity().getSupportFragmentManager());
        otherViewPager.setAdapter(sectionsPagerAdapter);
        otherTabLayout.setupWithViewPager(otherViewPager);
        courseCodeEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setNumber));
        competencyUnitsEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setCompetencyUnitsText));
        titleEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::setTitle));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveState(outState);
    }

    private void onLoadFailed(Throwable throwable) {

    }

    private void onEntitySucceeded(CourseEntity entity) {

    }

    private void onTermButtonClick(View view) {

    }

    private void onMentorChipClick(View view) {

    }

    private void onMentorChipCloseIconClick(View view) {

    }

    private void onStatusButtonClick(View view) {

    }

    private void onSaveCourseButtonClick(View view) {

    }

    private void onDeleteCourseButtonClick(View view) {

    }

    private void onCancelButtonClick(View view) {

    }

}