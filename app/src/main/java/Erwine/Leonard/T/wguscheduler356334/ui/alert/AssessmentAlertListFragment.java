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
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.EditAssessmentViewModel;

/**
 * A fragment representing a list of Items.
 */
public class AssessmentAlertListFragment extends Fragment {

    private static final String LOG_TAG = AssessmentAlertListFragment.class.getName();
    private final List<AssessmentAlert> items;
    private AssessmentAlertListViewModel listViewModel;
    private AssessmentAlertListAdapter adapter;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private EditAssessmentViewModel assessmentViewModel;
    private AssessmentDetails currentAssessment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentAlertListFragment() {
        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assessment_alert_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noAlertsTextView = view.findViewById(R.id.noAlertsTextView);
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        adapter = new AssessmentAlertListAdapter(items);
        alertsRecyclerView.setAdapter(adapter);
        view.findViewById(R.id.addFloatingActionButton).setOnClickListener(this::onAddFloatingActionButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assessmentViewModel = new ViewModelProvider(requireActivity()).get(EditAssessmentViewModel.class);
        listViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(AssessmentAlertListViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        listViewModel.getLiveData().observe(viewLifecycleOwner, this::onListLoaded);
        assessmentViewModel.getEntityLiveData().observe(viewLifecycleOwner, this::onAssessmentLoaded);
        assessmentViewModel.getEffectiveStartLiveData().observe(viewLifecycleOwner, this::onEffectiveStartChanged);
        assessmentViewModel.getEffectiveEndLiveData().observe(viewLifecycleOwner, this::onEffectiveEndChanged);
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

    private void onAssessmentLoaded(AssessmentDetails assessmentDetails) {
        if (null != assessmentDetails) {
            currentAssessment = assessmentDetails;
            listViewModel.setAssessment(assessmentDetails, getViewLifecycleOwner());
        }
    }

    private void onListLoaded(List<AssessmentAlert> assessmentAlerts) {
        items.clear();
        items.addAll(assessmentAlerts);
        if (assessmentAlerts.isEmpty()) {
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