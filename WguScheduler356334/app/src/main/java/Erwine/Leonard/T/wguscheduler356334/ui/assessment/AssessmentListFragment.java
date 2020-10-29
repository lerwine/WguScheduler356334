package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

/**
 * A fragment representing a list of Items.
 */
public class AssessmentListFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(AssessmentListFragment.class);
    private final ArrayList<AssessmentEntity> list;
    private AssessmentListAdapter adapter;
    private EditCourseViewModel editCourseViewModel;
    private TextView overviewTextView;
    private TextView noAssessmentsTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentListFragment() {
        Log.d(LOG_TAG, "Constructing");
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_assessment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        overviewTextView = view.findViewById(R.id.overviewTextView);
        noAssessmentsTextView = view.findViewById(R.id.noAssessmentsTextView);
        adapter = new AssessmentListAdapter(requireContext(), list);
        RecyclerView assessmentListRecyclerView = view.findViewById(R.id.assessmentListRecyclerView);
        assessmentListRecyclerView.setAdapter(adapter);

        assessmentListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) assessmentListRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(assessmentListRecyclerView.getContext(), linearLayoutManager.getOrientation());
        assessmentListRecyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by ViewCourseActivity
        editCourseViewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        editCourseViewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onEntityLoaded);
        editCourseViewModel.getOverviewFactoryLiveData().observe(getViewLifecycleOwner(),
                f -> overviewTextView.setText(f.apply(getResources())));
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onEntityLoaded(@NonNull CourseDetails entity) {
        Log.d(LOG_TAG, "Loaded course details " + entity);
        long courseId = entity.getId();
        if (ID_NEW != courseId) {
            editCourseViewModel.getAssessments().observe(getViewLifecycleOwner(), this::onAssessmentListChanged);
            editCourseViewModel.getOverviewFactoryLiveData().observe(getViewLifecycleOwner(), f -> overviewTextView.setText(f.apply(getResources())));
        }
    }

    private void onAssessmentListChanged(@NonNull List<AssessmentEntity> assessmentEntities) {
        list.clear();
        if (assessmentEntities.isEmpty()) {
            noAssessmentsTextView.setVisibility(View.VISIBLE);
        } else {
            noAssessmentsTextView.setVisibility(View.GONE);
            list.addAll(assessmentEntities);
        }
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

}