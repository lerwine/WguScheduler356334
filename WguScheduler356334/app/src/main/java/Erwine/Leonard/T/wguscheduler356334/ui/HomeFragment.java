package Erwine.Leonard.T.wguscheduler356334.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.mentor.EditMentorViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;

public class HomeFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(HomeFragment.class);
    private TextView currentTextView;
    private FrameLayout currentFrameLayout;
    private Button currentTermButton;
    private TextView currentTermStartedOnLabelTextView;
    private TextView currentTermStartedOnDateTextView;
    private TextView currentTermEndsOnDateTextView;
    private TextView currentCourseTextView;
    private Button currentCourseButton;
    private TextView currentCourseStartedOnLabelTextView;
    private TextView currentCourseStartedOnDateTextView;
    private TextView currentCourseEndsOnLabelTextView;
    private TextView currentCourseEndsOnDateTextView;
    private TextView currentMentorTextView;
    private Button currentMentorButton;
    private TextView currentMentorPhoneNumberLabelTextView;
    private TextView currentMentorPhoneNumberTextView;
    private TextView currentMentorEmailAddressLabelTextView;
    private TextView currentMentorEmailAddressTextView;
    private TextView nextTextView;
    private FrameLayout nextFrameLayout;
    private Button nextTermButton;
    private TextView nextTermStartsOnLabelTextView;
    private TextView nextTermEndsOnLabelTextView;
    private TextView nextTermStartsOnDateTextView;
    private TextView nextTermEndsOnDateTextView;
    private TextView nextCourseTextView;
    private Button nextCourseButton;
    private TextView nextCourseStartsOnLabelTextView;
    private TextView nextCourseStartsOnDateTextView;
    private TextView nextCourseEndsOnLabelTextView;
    private TextView nextCourseEndsOnDateTextView;
    private TextView nextMentorTextView;
    private Button nextMentorButton;
    private TextView nextMentorPhoneNumberLabelTextView;
    private TextView nextMentorPhoneNumberTextView;
    private TextView nextMentorEmailAddressLabelTextView;
    private TextView nextMentorEmailAddressTextView;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTextView = view.findViewById(R.id.currentTextView);
        currentFrameLayout = view.findViewById(R.id.currentFrameLayout);
        currentTermButton = view.findViewById(R.id.currentTermButton);
        currentTermStartedOnLabelTextView = view.findViewById(R.id.currentTermStartedOnLabelTextView);
        currentTermStartedOnDateTextView = view.findViewById(R.id.currentTermStartedOnDateTextView);
        currentTermEndsOnDateTextView = view.findViewById(R.id.currentTermEndsOnDateTextView);
        currentCourseTextView = view.findViewById(R.id.currentCourseTextView);
        currentCourseButton = view.findViewById(R.id.currentCourseButton);
        currentCourseStartedOnLabelTextView = view.findViewById(R.id.currentCourseStartedOnLabelTextView);
        currentCourseStartedOnDateTextView = view.findViewById(R.id.currentCourseStartedOnDateTextView);
        currentCourseEndsOnLabelTextView = view.findViewById(R.id.currentCourseEndsOnLabelTextView);
        currentCourseEndsOnDateTextView = view.findViewById(R.id.currentCourseEndsOnDateTextView);
        currentMentorTextView = view.findViewById(R.id.currentMentorTextView);
        currentMentorButton = view.findViewById(R.id.currentMentorButton);
        currentMentorPhoneNumberLabelTextView = view.findViewById(R.id.currentMentorPhoneNumberLabelTextView);
        currentMentorPhoneNumberTextView = view.findViewById(R.id.currentMentorPhoneNumberTextView);
        currentMentorEmailAddressLabelTextView = view.findViewById(R.id.currentMentorEmailAddressLabelTextView);
        currentMentorEmailAddressTextView = view.findViewById(R.id.currentMentorEmailAddressTextView);
        nextTextView = view.findViewById(R.id.nextTextView);
        nextFrameLayout = view.findViewById(R.id.nextFrameLayout);
        nextTermButton = view.findViewById(R.id.nextTermButton);
        nextTermStartsOnLabelTextView = view.findViewById(R.id.nextTermStartsOnLabelTextView);
        nextTermEndsOnLabelTextView = view.findViewById(R.id.nextTermEndsOnLabelTextView);
        nextTermStartsOnDateTextView = view.findViewById(R.id.nextTermStartsOnDateTextView);
        nextTermEndsOnDateTextView = view.findViewById(R.id.nextTermEndsOnDateTextView);
        nextCourseTextView = view.findViewById(R.id.nextCourseTextView);
        nextCourseButton = view.findViewById(R.id.nextCourseButton);
        nextCourseStartsOnLabelTextView = view.findViewById(R.id.nextCourseStartsOnLabelTextView);
        nextCourseStartsOnDateTextView = view.findViewById(R.id.nextCourseStartsOnDateTextView);
        nextCourseEndsOnLabelTextView = view.findViewById(R.id.nextCourseEndsOnLabelTextView);
        nextCourseEndsOnDateTextView = view.findViewById(R.id.nextCourseEndsOnDateTextView);
        nextMentorTextView = view.findViewById(R.id.nextMentorTextView);
        nextMentorButton = view.findViewById(R.id.nextMentorButton);
        nextMentorPhoneNumberLabelTextView = view.findViewById(R.id.nextMentorPhoneNumberLabelTextView);
        nextMentorPhoneNumberTextView = view.findViewById(R.id.nextMentorPhoneNumberTextView);
        nextMentorEmailAddressLabelTextView = view.findViewById(R.id.nextMentorEmailAddressLabelTextView);
        nextMentorEmailAddressTextView = view.findViewById(R.id.nextMentorEmailAddressTextView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(HomeViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();

        homeViewModel.getCurrentHeadingResourceLiveData().observe(viewLifecycleOwner, this::onCurrentHeadingResourceChanged);
        homeViewModel.getShowCurrentLiveData().observe(viewLifecycleOwner, b -> currentFrameLayout.setVisibility((Boolean.TRUE.equals(b)) ? View.VISIBLE : View.GONE));
        homeViewModel.getCurrentTermNameLiveData().observe(viewLifecycleOwner, currentTermButton::setText);
        homeViewModel.getCurrentTermStartLabelLiveData().observe(viewLifecycleOwner, currentTermStartedOnLabelTextView::setText);
        homeViewModel.getCurrentTermStartDateFactoryLiveData().observe(viewLifecycleOwner, f -> currentTermStartedOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getCurrentTermEndDateFactoryLiveData().observe(viewLifecycleOwner, f -> currentTermEndsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getShowCurrentCourseLiveData().observe(viewLifecycleOwner, this::onShowCurrentCourseChanged);
        homeViewModel.getCurrentCourseNameFactoryLiveData().observe(viewLifecycleOwner, f -> currentCourseButton.setText(f.apply(getResources())));
        homeViewModel.getShowCurrentCourseDatesLiveData().observe(viewLifecycleOwner, this::onShowCurrentCourseDatesChanged);
        homeViewModel.getCurrentCourseStartLabelLiveData().observe(viewLifecycleOwner, currentCourseStartedOnLabelTextView::setText);
        homeViewModel.getCurrentCourseStartDateFactoryLiveData().observe(viewLifecycleOwner, f -> currentCourseStartedOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getCurrentCourseEndDateFactoryLiveData().observe(viewLifecycleOwner, f -> currentCourseEndsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getShowCurrentMentorLiveData().observe(viewLifecycleOwner, this::onShowCurrentMentorChanged);
        homeViewModel.getCurrentMentorNameLiveData().observe(viewLifecycleOwner, currentMentorButton::setText);
        homeViewModel.getCurrentMentorPhoneNumberFactoryLiveData().observe(viewLifecycleOwner, f -> currentMentorPhoneNumberTextView.setText(f.apply(getResources())));
        homeViewModel.getCurrentMentorEmailAddressFactoryLiveData().observe(viewLifecycleOwner, f -> currentMentorEmailAddressTextView.setText(f.apply(getResources())));
        homeViewModel.getShowNextLiveData().observe(viewLifecycleOwner, this::onShowNextChanged);
        homeViewModel.getShowNextTermLiveData().observe(viewLifecycleOwner, this::onShowNextTermChanged);
        homeViewModel.getNextTermLabelLiveData().observe(viewLifecycleOwner, nextTextView::setText);
        homeViewModel.getNextTermNameTextLiveData().observe(viewLifecycleOwner, nextTermButton::setText);
        homeViewModel.getNextTermStartLabelLiveData().observe(viewLifecycleOwner, nextTermStartsOnLabelTextView::setText);
        homeViewModel.getNextTermStartDateFactoryLiveData().observe(viewLifecycleOwner, f -> nextTermStartsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getNextTermEndDateFactoryLiveData().observe(viewLifecycleOwner, f -> nextTermEndsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getShowNextCourseHeadingLiveData().observe(viewLifecycleOwner, b -> {
            Log.d(LOG_TAG, "showNextCourseHeading changed to " + b);
            nextCourseTextView.setVisibility((Boolean.TRUE.equals(b)) ? View.VISIBLE : View.GONE);
        });
        homeViewModel.getShowNextCourseLiveData().observe(viewLifecycleOwner, this::onShowNextCourseChanged);
        homeViewModel.getNextCourseNameFactoryLiveData().observe(viewLifecycleOwner, f -> nextCourseButton.setText(f.apply(getResources())));
        homeViewModel.getShowNextCourseDatesLiveData().observe(viewLifecycleOwner, this::onShowNextCourseDatesChanged);
        homeViewModel.getNextCourseStartLabelLiveData().observe(viewLifecycleOwner, nextCourseStartsOnLabelTextView::setText);
        homeViewModel.getNextCourseStartDateFactoryLiveData().observe(viewLifecycleOwner, f -> nextCourseStartsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getNextCourseEndDateFactoryLiveData().observe(viewLifecycleOwner, f -> nextCourseEndsOnDateTextView.setText(f.apply(getResources())));
        homeViewModel.getShowNextMentorLiveData().observe(viewLifecycleOwner, this::onShowNextMentorChanged);
        homeViewModel.getNextMentorNameLiveData().observe(viewLifecycleOwner, nextMentorButton::setText);
        homeViewModel.getNextMentorPhoneNumberFactoryLiveData().observe(viewLifecycleOwner, f -> nextMentorPhoneNumberTextView.setText(f.apply(getResources())));
        homeViewModel.getNextMentorEmailAddressFactoryLiveData().observe(viewLifecycleOwner, f -> nextMentorEmailAddressTextView.setText(f.apply(getResources())));
        currentTermButton.setOnClickListener(this::onCurrentTermButtonClick);
        currentCourseButton.setOnClickListener(this::onCurrentCourseButtonClick);
        currentMentorButton.setOnClickListener(this::onCurrentMentorButtonClick);
        nextTermButton.setOnClickListener(this::onNextTermButtonClick);
        nextCourseButton.setOnClickListener(this::onNextCourseButtonClick);
        nextMentorButton.setOnClickListener(this::onNextMentorButtonClick);
    }

    private void onCurrentHeadingResourceChanged(@StringRes @Nullable Integer resourceId) {
        if (null == resourceId) {
            currentTextView.setVisibility(View.GONE);
        } else {
            currentTextView.setVisibility(View.VISIBLE);
            currentTextView.setText(resourceId);
        }
    }

    private void onShowCurrentCourseChanged(Boolean showCurrentCourse) {
        boolean enabled = Boolean.TRUE.equals(showCurrentCourse);
        int visibility = (enabled) ? View.VISIBLE : View.GONE;
        currentCourseTextView.setVisibility(visibility);
        currentCourseButton.setVisibility(visibility);
        currentCourseButton.setEnabled(enabled);
    }

    private void onShowCurrentCourseDatesChanged(Boolean showCurrentCourseDates) {
        int visibility = (Boolean.TRUE.equals(showCurrentCourseDates)) ? View.VISIBLE : View.GONE;
        currentCourseStartedOnLabelTextView.setVisibility(visibility);
        currentCourseEndsOnLabelTextView.setVisibility(visibility);
        currentCourseStartedOnDateTextView.setVisibility(visibility);
        currentCourseEndsOnDateTextView.setVisibility(visibility);
    }

    private void onShowNextChanged(Boolean showNext) {
        Log.d(LOG_TAG, "Enter onShowNextChanged(" + showNext + ")");
        int visibility = (Boolean.TRUE.equals(showNext)) ? View.VISIBLE : View.GONE;
        nextTextView.setVisibility(visibility);
        nextFrameLayout.setVisibility(visibility);
    }

    private void onShowCurrentMentorChanged(Boolean showCurrentMentor) {
        boolean enabled = Boolean.TRUE.equals(showCurrentMentor);
        int visibility = (enabled) ? View.VISIBLE : View.GONE;
        currentMentorTextView.setVisibility(visibility);
        currentMentorButton.setVisibility(visibility);
        currentMentorButton.setEnabled(enabled);
        currentMentorPhoneNumberLabelTextView.setVisibility(visibility);
        currentMentorEmailAddressLabelTextView.setVisibility(visibility);
        currentMentorPhoneNumberTextView.setVisibility(visibility);
        currentMentorEmailAddressTextView.setVisibility(visibility);
    }

    private void onShowNextTermChanged(Boolean showNextTerm) {
        Log.d(LOG_TAG, "Enter onShowNextTermChanged(" + showNextTerm + ")");
        boolean enabled = Boolean.TRUE.equals(showNextTerm);
        int visibility = (enabled) ? View.VISIBLE : View.GONE;
        nextTermButton.setEnabled(enabled);
        nextTermButton.setVisibility(visibility);
        nextTermStartsOnLabelTextView.setVisibility(visibility);
        nextTermEndsOnLabelTextView.setVisibility(visibility);
        nextTermStartsOnDateTextView.setVisibility(visibility);
        nextTermEndsOnDateTextView.setVisibility(visibility);
    }

    private void onShowNextCourseChanged(Boolean showNextCourse) {
        Log.d(LOG_TAG, "Enter onShowNextCourseChanged(" + showNextCourse + ")");
        boolean enabled = Boolean.TRUE.equals(showNextCourse);
        int visibility = (enabled) ? View.VISIBLE : View.GONE;
        nextCourseButton.setEnabled(enabled);
        nextCourseButton.setVisibility(visibility);
    }

    private void onShowNextCourseDatesChanged(Boolean showNextCourseDates) {
        int visibility = (Boolean.TRUE.equals(showNextCourseDates)) ? View.VISIBLE : View.GONE;
        nextCourseStartsOnLabelTextView.setVisibility(visibility);
        nextCourseEndsOnLabelTextView.setVisibility(visibility);
        nextCourseStartsOnDateTextView.setVisibility(visibility);
        nextCourseEndsOnDateTextView.setVisibility(visibility);
    }

    private void onShowNextMentorChanged(Boolean showNextMentor) {
        boolean enabled = Boolean.TRUE.equals(showNextMentor);
        int visibility = (enabled) ? View.VISIBLE : View.GONE;
        nextMentorTextView.setVisibility(visibility);
        nextMentorButton.setVisibility(visibility);
        nextMentorButton.setEnabled(enabled);
        nextMentorPhoneNumberLabelTextView.setVisibility(visibility);
        nextMentorEmailAddressLabelTextView.setVisibility(visibility);
        nextMentorPhoneNumberTextView.setVisibility(visibility);
        nextMentorEmailAddressTextView.setVisibility(visibility);
    }

    private void onCurrentTermButtonClick(View view) {
        AbstractTermEntity<?> term = homeViewModel.getCurrentTerm();
        if (null != term) {
            EditTermViewModel.startViewTermActivity(requireContext(), term.getId());
        }
    }

    private void onCurrentCourseButtonClick(View view) {
        CourseDetails course = homeViewModel.getCurrentCourse();
        if (null != course) {
            EditCourseViewModel.startViewCourseActivity(requireContext(), course.getId());
        }
    }

    private void onCurrentMentorButtonClick(View view) {
        CourseDetails course = homeViewModel.getCurrentCourse();
        if (null != course) {
            AbstractMentorEntity<?> mentor = course.getMentor();
            if (null != mentor) {
                EditMentorViewModel.startEditMentorActivity(requireContext(), mentor.getId());
            }
        }
    }

    private void onNextTermButtonClick(View view) {
        AbstractTermEntity<?> term = homeViewModel.getNextTerm();
        if (null != term) {
            EditTermViewModel.startViewTermActivity(requireContext(), term.getId());
        }
    }

    private void onNextCourseButtonClick(View view) {
        CourseDetails course = homeViewModel.getNextCourse();
        if (null != course) {
            EditCourseViewModel.startViewCourseActivity(requireContext(), course.getId());
        }
    }

    private void onNextMentorButtonClick(View view) {
        CourseDetails course = homeViewModel.getNextCourse();
        if (null != course) {
            AbstractMentorEntity<?> mentor = course.getMentor();
            if (null != mentor) {
                EditMentorViewModel.startEditMentorActivity(requireContext(), mentor.getId());
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

}