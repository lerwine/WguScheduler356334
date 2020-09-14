package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.dummy.DummyContent;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 */
public class CourseListFragment extends Fragment {

    public static final String ARGUMENT_KEY_TERM_ID = "term_id";
    public static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final List<CourseEntity> list;
    private CourseListViewModel courseListViewModel;
    private CourseListAdapter adapter;

    public static Fragment newInstance(Long termId) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        if (null != termId) {
            args.putLong(ARGUMENT_KEY_TERM_ID, termId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private final CompositeDisposable compositeDisposable;
    private boolean stateInitialized;
    private Long termId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListFragment() {
        compositeDisposable = new CompositeDisposable();
        list = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
            if (stateInitialized) {
                if (savedInstanceState.containsKey(ARGUMENT_KEY_TERM_ID)) {
                    termId = savedInstanceState.getLong(ARGUMENT_KEY_TERM_ID);
                }
                return;
            }
        }
        Bundle args = getArguments();
        if (null != args && args.containsKey(ARGUMENT_KEY_TERM_ID)) {
            termId = args.getLong(ARGUMENT_KEY_TERM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        // Set the adapter
        adapter = new CourseListAdapter(DummyContent.ITEMS);
        RecyclerView courseListingRecyclerView = view.findViewById(R.id.courseListingRecyclerView);
        courseListingRecyclerView.setAdapter(adapter);
        view.findViewById(R.id.addCourseButton).setOnClickListener(this::onAddCourseButtonClick);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        courseListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(CourseListViewModel.class);
        if (null != termId) {
            courseListViewModel.setTermId(termId);
            courseListViewModel.getCourses().observe(getViewLifecycleOwner(), this::onCourseListChanged);
        }
    }

    private void onCourseListChanged(List<CourseEntity> courseEntities) {
        list.clear();
        list.addAll(courseEntities);
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onAddCourseButtonClick(View view) {

    }

}