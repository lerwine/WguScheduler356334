package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StateHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 */
public class CourseListFragment extends Fragment {

    private static final String LOG_TAG = CourseListFragment.class.getName();
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";

    private final List<CourseEntity> list;
    private final CompositeDisposable compositeDisposable;
    private CourseListViewModel courseListViewModel;
    private CourseListAdapter adapter;

    public static Fragment newInstance(long termId) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.newInstance(%d)", termId));
        return StateHelper.setIdArgs(ARGUMENT_KEY_TERM_ID, termId, new CourseListFragment(), new Bundle());
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment");
        compositeDisposable = new CompositeDisposable();
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        // Set the adapter
        adapter = new CourseListAdapter(list);
        RecyclerView courseListingRecyclerView = view.findViewById(R.id.courseListingRecyclerView);
        courseListingRecyclerView.setAdapter(adapter);
        view.findViewById(R.id.addCourseButton).setOnClickListener(this::onAddCourseButtonClick);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        courseListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(CourseListViewModel.class);
        StateHelper.restoreState(ARGUMENT_KEY_TERM_ID, savedInstanceState, this::getArguments, this::onConfigureViewModel, (r, bundle) -> {
            AlertDialog dlg = new AlertDialog.Builder(requireContext())
                    .setTitle("Not Found")
                    .setMessage("Term ID not specified__")
                    .setOnCancelListener(dialog -> requireActivity().finish())
                    .setCancelable(true)
                    .create();
            dlg.show();
        });
    }

    private void onConfigureViewModel(Bundle bundle, long termId) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.onConfigureViewModel");
        courseListViewModel.setTermId(termId);
        courseListViewModel.getCourses().observe(getViewLifecycleOwner(), this::onCourseListChanged);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.onSaveInstanceState");
        StateHelper.saveState(ARGUMENT_KEY_TERM_ID, courseListViewModel.getTermId(), outState);
        super.onSaveInstanceState(outState);
    }

    private void onCourseListChanged(List<CourseEntity> courseEntities) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment.onCourseListChanged");
        list.clear();
        list.addAll(courseEntities);
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onAddCourseButtonClick(View view) {

    }

}