package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 */
public class AssessmentListFragment extends Fragment {

    private static final String LOG_TAG = AssessmentListFragment.class.getName();
    private final CompositeDisposable compositeDisposable;
    private final ArrayList<AssessmentEntity> list;
    private AssessmentListAdapter adapter;
    private EditCourseViewModel editCourseViewModel;
    private AssessmentListViewModel assessmentListViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentListFragment() {
        Log.d(LOG_TAG, "Constructing AssessmentListFragment");
        compositeDisposable = new CompositeDisposable();
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_assessment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AssessmentListAdapter(requireContext(), list);
        RecyclerView assessmentListRecyclerView = view.findViewById(R.id.assessmentListRecyclerView);
        assessmentListRecyclerView.setAdapter(adapter);

        assessmentListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) assessmentListRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(assessmentListRecyclerView.getContext(), linearLayoutManager.getOrientation());
        assessmentListRecyclerView.addItemDecoration(decoration);

        view.findViewById(R.id.addAssessmentButton).setOnClickListener(this::onAddAssessmentButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by ViewCourseActivity
        editCourseViewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        editCourseViewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onEntityLoaded);
    }

    private void onEntityLoaded(CourseDetails entity) {
        Long courseId = entity.getId();
        if (null != courseId) {
            assessmentListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(AssessmentListViewModel.class);
            assessmentListViewModel.setId(courseId);
            assessmentListViewModel.getAssessments().observe(getViewLifecycleOwner(), this::onAssessmentListChanged);
        }
    }

    private void onAssessmentListChanged(List<AssessmentEntity> assessmentEntities) {
        list.clear();
        list.addAll(assessmentEntities);
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onAddAssessmentButtonClick(View view) {
        // TODO: Implement onAddAssessmentButtonClick
    }
}