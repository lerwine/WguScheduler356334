package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 */
public class AssessmentAlertListFragment extends Fragment {

    private static final String LOG_TAG = AssessmentAlertListFragment.class.getName();
    private final CompositeDisposable compositeDisposable;
    private final List<AssessmentAlert> items;
    private AssessmentAlertListViewModel listViewModel;
    private AssessmentAlertListAdapter adapter;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private EditAssessmentViewModel assessmentViewModel;
    private AssessmentDetails currentAssessment;
    private long editAlertId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentAlertListFragment() {
        compositeDisposable = new CompositeDisposable();
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
        adapter = new AssessmentAlertListAdapter(items, this::onEditAlert);
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

    private void onEditAlert(AssessmentAlert assessmentAlert) {
        if (assessmentViewModel.isChanged()) {
            editAlertId = assessmentAlert.getAlert().getId();
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                    () -> requireActivity().finish(),
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(assessmentViewModel.save(false).subscribe(this::onSaveForEditAlertFinished, this::onSaveFailed));
                        requireActivity().finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.existingAssessmentAlertEditor(assessmentAlert.getAlert().getId(), assessmentViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        }
    }

    private void onAddFloatingActionButtonClick(View view) {
        if (assessmentViewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                    () -> requireActivity().finish(),
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(assessmentViewModel.save(false).subscribe(this::onSaveForNewAlertFinished, this::onSaveFailed));
                        requireActivity().finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(assessmentViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        }
    }

    private void onSaveForNewAlertFinished(ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            EditAlertDialog dlg = EditAlertViewModel.newAssessmentAlert(assessmentViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(assessmentViewModel.save(true).subscribe(this::onSaveForNewAlertFinished, this::onSaveFailed));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveForEditAlertFinished(ValidationMessage.ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            EditAlertDialog dlg = EditAlertViewModel.existingAssessmentAlertEditor(editAlertId, assessmentViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(assessmentViewModel.save(true).subscribe(this::onSaveForEditAlertFinished, this::onSaveFailed));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

}