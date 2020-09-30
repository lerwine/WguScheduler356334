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

/**
 * A fragment representing a list of Items.
 */
public class AlertListFragment extends Fragment {

    private static final String LOG_TAG = AlertListFragment.class.getName();
    private final List<AlertListItem> items;
    private final TabSelectedListener tabSelectedListener;
    private AlertListAdapter adapter;
    private TabLayout listingSelectionTabLayout;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private AlertListViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlertListFragment() {
        items = new ArrayList<>();
        tabSelectedListener = new TabSelectedListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listingSelectionTabLayout = view.findViewById(R.id.listingSelectionTabLayout);
        noAlertsTextView = view.findViewById(R.id.noAlertsTextView);
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        adapter = new AlertListAdapter(items);
        alertsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(AlertListViewModel.class);
        viewModel.setPosition(0, tabSelectedListener, getViewLifecycleOwner());
        listingSelectionTabLayout.addOnTabSelectedListener(tabSelectedListener);
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener, Observer<List<AlertListItem>> {
        @Override
        public synchronized void onTabSelected(TabLayout.Tab tab) {
            viewModel.setPosition(tab.getPosition(), this, getViewLifecycleOwner());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }

        @Override
        public void onChanged(List<AlertListItem> alertListItems) {
            Log.d(LOG_TAG, "Enter onChanged");
            boolean wasEmpty = items.isEmpty();
            items.clear();
            if (alertListItems.isEmpty()) {
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
                items.addAll(alertListItems);
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }
}