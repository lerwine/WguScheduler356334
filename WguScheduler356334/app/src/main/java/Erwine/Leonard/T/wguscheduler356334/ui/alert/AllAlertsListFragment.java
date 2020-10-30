package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;

/**
 * A fragment representing a list of Items.
 */
public class AllAlertsListFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(AllAlertsListFragment.class);
    private final List<AlertListItem> items;
    private final TabSelectedListener tabSelectedListener;
    private AllAlertsListViewModel viewModel;
    private AllAlertsListAdapter adapter;
    private TabLayout listingSelectionTabLayout;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllAlertsListFragment() {
        items = new ArrayList<>();
        tabSelectedListener = new TabSelectedListener();
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_all_alerts_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listingSelectionTabLayout = view.findViewById(R.id.listingSelectionTabLayout);
        noAlertsTextView = view.findViewById(R.id.noAlertsTextView);
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        adapter = new AllAlertsListAdapter(items, this::onItemClicked);
        alertsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(AllAlertsListViewModel.class);
        viewModel.initializeViewModelState(savedInstanceState, tabSelectedListener, getViewLifecycleOwner());
        listingSelectionTabLayout.selectTab(listingSelectionTabLayout.getTabAt(viewModel.getPosition()));
        listingSelectionTabLayout.addOnTabSelectedListener(tabSelectedListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveViewModelState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onItemClicked(AlertListItem item) {
        if (item.isAssessment()) {
            EditAssessmentViewModel.startViewAssessmentActivity(requireContext(), item.getTargetId());
        } else {
            EditCourseViewModel.startViewCourseActivity(requireContext(), item.getTargetId());
        }
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener, Observer<List<AlertListItem>> {
        @Override
        public synchronized void onTabSelected(@NonNull TabLayout.Tab tab) {
            viewModel.setPosition(tab.getPosition(), this, getViewLifecycleOwner());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }

        @Override
        public void onChanged(List<AlertListItem> alertItems) {
            Log.d(LOG_TAG, "Enter onChanged");
            boolean wasEmpty = items.isEmpty();
            items.clear();
            if (alertItems.isEmpty()) {
                if (wasEmpty) {
                    return;
                }
                Log.d(LOG_TAG, "Empty!");
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
                noAlertsTextView.setVisibility(View.VISIBLE);
                alertsRecyclerView.setVisibility(View.GONE);
            } else {
                Log.d(LOG_TAG, "Not empty");
                if (wasEmpty) {
                    noAlertsTextView.setVisibility(View.GONE);
                    alertsRecyclerView.setVisibility(View.VISIBLE);
                }
                items.addAll(alertItems);
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }
}