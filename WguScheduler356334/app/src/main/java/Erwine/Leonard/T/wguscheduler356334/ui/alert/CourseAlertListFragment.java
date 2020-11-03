package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;

/**
 * A fragment representing a list of Items.
 */
public class CourseAlertListFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(CourseAlertListFragment.class);
    private final List<CourseAlert> items;
//    private CourseAlertListViewModel listViewModel;
    private TextView overviewTextView;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private CourseAlertListAdapter adapter;
    private EditCourseViewModel courseViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseAlertListFragment() {
        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        return inflater.inflate(R.layout.fragment_course_alert_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        overviewTextView = view.findViewById(R.id.overviewTextView);
        noAlertsTextView = view.findViewById(R.id.noAlertsTextView);
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        adapter = new CourseAlertListAdapter(items, this::onEditAlert);
        alertsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        courseViewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        courseViewModel.getCourseAlertsLiveData().observe(viewLifecycleOwner, this::onListLoaded);
        courseViewModel.getOriginalValuesLiveData().observe(viewLifecycleOwner, this::onCourseLoaded);
        courseViewModel.getEffectiveStartLiveData().observe(viewLifecycleOwner, this::onEffectiveStartChanged);
        courseViewModel.getEffectiveEndLiveData().observe(viewLifecycleOwner, this::onEffectiveEndChanged);
    }

    private void onEffectiveStartChanged(LocalDate localDate) {
        adapter.notifyDataSetChanged();
    }

    private void onEffectiveEndChanged(LocalDate localDate) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onCourseLoaded(CourseDetails courseDetails) {
        if (null != courseDetails) {
            courseViewModel.getOverviewFactoryLiveData().observe(getViewLifecycleOwner(),
                    f -> overviewTextView.setText(f.apply(getResources())));
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

    private void doEditAlert(long editAlertId) {
        EditAlertDialog dlg = EditAlertViewModel.existingCourseAlertEditor(editAlertId, courseViewModel.getId());
        dlg.show(getParentFragmentManager(), null);
    }

    private void onEditAlert(CourseAlert courseAlert) {
        ObserverHelper.observeOnce(courseViewModel.getHasChangesLiveData(), this, hasChanges -> {
            long editAlertId = courseAlert.getAlert().getId();
            if (hasChanges) {
                new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                        () -> doEditAlert(editAlertId),
                        () -> ObserverHelper.subscribeOnce(courseViewModel.save(false), getViewLifecycleOwner(),
                                m -> onSaveForEditAlertFinished(m, editAlertId), this::onSaveCourseError), null);
            } else {
                doEditAlert(editAlertId);
            }
        });
    }

    private void onSaveForEditAlertFinished(@NonNull ResourceMessageResult messages, long editAlertId) {
        if (messages.isSucceeded()) {
            doEditAlert(editAlertId);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            ObserverHelper.subscribeOnce(courseViewModel.save(true), getViewLifecycleOwner(),
                                    m -> onSaveForEditAlertFinished(m, editAlertId), this::onSaveCourseError);
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveCourseError(@NonNull Throwable throwable) {
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

}