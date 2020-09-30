package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Fragment for editing the properties of a {@link CourseEntity}.
 * This assumes that the parent activity ({@link Erwine.Leonard.T.wguscheduler356334.AddCourseActivity} or {@link Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity})
 * calls {@link EditCourseViewModel#initializeViewModelState(Bundle, Supplier)}.
 */
public class EditCourseFragment extends Fragment {
    private static final String LOG_TAG = EditCourseFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditCourseFragment() {
        Log.d(LOG_TAG, "Constructing EditCourseFragment");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onViewCreated");
        view.findViewById(R.id.alertImageButton).setOnClickListener(this::onAlertImageButtonClick);
        view.findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveImageButtonClick);
        view.findViewById(R.id.deleteImageButton).setOnClickListener(this::onDeleteImageButtonClick);
        view.findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
    }

    private void onAlertImageButtonClick(View view) {
        // TODO: Implement Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onAlertImageButtonClick
    }

    private void onSaveImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onDeleteImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_course, R.string.message_delete_course_confirm, requireContext()).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onCancelImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCancelImageButtonClick");
        verifySaveChanges();
    }

    private void onSaveOperationFinished(List<Integer> messageIds) {
        Log.d(LOG_TAG, "Enter onSaveOperationFinished");
        if (messageIds.isEmpty()) {
            requireActivity().finish();
        } else {
            Resources resources = getResources();
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.title_save_error)
                    .setMessage(messageIds.stream().map(resources::getString).collect(Collectors.joining("; "))).setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

    private void onDeleteSucceeded() {
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        requireActivity().finish();
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting course", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), requireContext()).showDialog();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                    () -> requireActivity().finish(),
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
                        requireActivity().finish();
                    }, null);
        } else {
            requireActivity().finish();
        }
    }

}