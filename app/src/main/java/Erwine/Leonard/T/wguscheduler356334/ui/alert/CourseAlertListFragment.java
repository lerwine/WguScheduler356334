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
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 */
public class CourseAlertListFragment extends Fragment {

    private static final String LOG_TAG = CourseAlertListFragment.class.getName();
    private final CompositeDisposable compositeDisposable;
    private final List<CourseAlert> items;
    private CourseAlertListViewModel listViewModel;
    private TextView overviewTextView;
    private TextView noAlertsTextView;
    private RecyclerView alertsRecyclerView;
    private CourseAlertListAdapter adapter;
    private EditCourseViewModel courseViewModel;
    private CourseDetails currentCourse;
    private long editAlertId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseAlertListFragment() {
        compositeDisposable = new CompositeDisposable();
        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        listViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(CourseAlertListViewModel.class);
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        listViewModel.getLiveData().observe(viewLifecycleOwner, this::onListLoaded);
        courseViewModel.getEntityLiveData().observe(viewLifecycleOwner, this::onCourseLoaded);
        courseViewModel.getEffectiveStartLiveData().observe(viewLifecycleOwner, this::onEffectiveStartChanged);
        courseViewModel.getEffectiveEndLiveData().observe(viewLifecycleOwner, this::onEffectiveEndChanged);
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

    private void onCourseLoaded(CourseDetails courseDetails) {
        if (null != courseDetails) {
            currentCourse = courseDetails;
            listViewModel.setCourse(courseDetails, getViewLifecycleOwner());
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

    private void onEditAlert(CourseAlert courseAlert) {
        if (courseViewModel.isChanged()) {
            editAlertId = courseAlert.getAlert().getId();
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                    () -> requireActivity().finish(),
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(courseViewModel.save(false).subscribe(this::onSaveForEditAlertFinished, this::onSaveCourseError));
                        requireActivity().finish();
                    }, null);
        } else {
            EditAlertDialog dlg = EditAlertViewModel.existingCourseAlertEditor(courseAlert.getAlert().getId(), courseViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        }
    }

    private void onSaveForEditAlertFinished(ResourceMessageResult messages) {
        if (messages.isSucceeded()) {
            EditAlertDialog dlg = EditAlertViewModel.existingCourseAlertEditor(editAlertId, courseViewModel.getId());
            dlg.show(getParentFragmentManager(), null);
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            compositeDisposable.clear();
                            compositeDisposable.add(courseViewModel.save(true).subscribe(this::onSaveForEditAlertFinished, this::onSaveCourseError));
                            dialog.dismiss();
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveCourseError(Throwable throwable) {
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

}