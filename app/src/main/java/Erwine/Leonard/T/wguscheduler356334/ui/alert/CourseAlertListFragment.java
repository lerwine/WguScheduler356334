package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;

/**
 * A fragment representing a list of Items.
 */
public class CourseAlertListFragment extends Fragment {

    private static final String LOG_TAG = CourseAlertListFragment.class.getName();
    private final List<CourseAlert> items;
    private CourseAlertListViewModel listViewModel;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private CourseAlertListAdapter adapter;
    private EditCourseViewModel courseViewModel;
    private CourseDetails currentCourse;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseAlertListFragment() {
        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_alert_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noAlertsTextView = view.findViewById(R.id.noAlertsTextView);
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        adapter = new CourseAlertListAdapter(items);
        alertsRecyclerView.setAdapter(adapter);
        view.findViewById(R.id.addFloatingActionButton).setOnClickListener(this::onAddFloatingActionButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        courseViewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        listViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(CourseAlertListViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        listViewModel.getLiveData().observe(viewLifecycleOwner, this::onListLoaded);
        courseViewModel.getEntityLiveData().observe(viewLifecycleOwner, this::onCourseLoaded);
        courseViewModel.getEffectiveStartLiveData().observe(viewLifecycleOwner, this::onEffectiveStartChanged);
        courseViewModel.getEffectiveEndLiveData().observe(viewLifecycleOwner, this::onEffectiveEndChanged);
    }

    private void onEffectiveStartChanged(LocalDate localDate) {
        if (listViewModel.setEffectiveStartDate(localDate) && null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onEffectiveEndChanged(LocalDate localDate) {
        if (listViewModel.setEffectiveEndDate(localDate) && null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onCourseLoaded(CourseDetails courseDetails) {
        if (null != courseDetails) {
            currentCourse = courseDetails;
            listViewModel.setCourse(courseDetails, getViewLifecycleOwner());
        }
    }

    private void onListLoaded(List<CourseAlert> courseAlerts) {
        items.clear();
        items.addAll(courseAlerts);
        if (courseAlerts.isEmpty()) {
            noAlertsTextView.setVisibility(View.VISIBLE);
            alertsRecyclerView.setVisibility(View.GONE);
        } else {
            noAlertsTextView.setVisibility(View.GONE);
            alertsRecyclerView.setVisibility(View.VISIBLE);
        }
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onAddFloatingActionButtonClick(View view) {
        // TODO: Display new alert popup
    }

}