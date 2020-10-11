package Erwine.Leonard.T.wguscheduler356334.ui.course;

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

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

/**
 * A fragment representing a list of Items.
 */
public class CourseListFragment extends Fragment {

    private static final String LOG_TAG = CourseListFragment.class.getName();

    private final CompositeDisposable subscriptionCompositeDisposable;
    private final List<TermCourseListItem> list;
    private EditTermViewModel editTermViewModel;
    private TermCourseListAdapter adapter;
    private TextView overviewTextView;
    private TextView noCoursesTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListFragment() {
        Log.d(LOG_TAG, "Constructing CourseListFragment");
        list = new ArrayList<>();
        subscriptionCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        overviewTextView = view.findViewById(R.id.overviewTextView);
        noCoursesTextView = view.findViewById(R.id.noCoursesTextView);
        // Set the adapter
        adapter = new TermCourseListAdapter(list);
        RecyclerView courseListingRecyclerView = view.findViewById(R.id.courseListingRecyclerView);
        courseListingRecyclerView.setAdapter(adapter);

        courseListingRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) courseListingRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(courseListingRecyclerView.getContext(), linearLayoutManager.getOrientation());
        courseListingRecyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        editTermViewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        OneTimeObservers.subscribeOnce(editTermViewModel.getEntity(), this::onEntityLoaded);
    }

    private void onEntityLoaded(TermEntity termEntity) {
        long termId = termEntity.getId();
        if (ID_NEW != termId) {
            editTermViewModel.getCoursesLiveData().observe(getViewLifecycleOwner(), this::onCourseListChanged);
            subscriptionCompositeDisposable.add(editTermViewModel.getOverviewFactory().subscribe(f -> overviewTextView.setText(f.apply(getResources()))));
        }
    }

    private void onCourseListChanged(List<TermCourseListItem> courseEntities) {
        Log.d(LOG_TAG, "Enter onCourseListChanged");
        list.clear();
        if (courseEntities.isEmpty()) {
            noCoursesTextView.setVisibility(View.VISIBLE);
        } else {
            noCoursesTextView.setVisibility(View.GONE);
            list.addAll(courseEntities);
        }
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

}