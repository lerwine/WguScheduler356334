package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Fragment for editing the properties of an {@link Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity}.
 * This assumes that the parent activity ({@link Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity} or {@link Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity})
 * calls {@link EditAssessmentViewModel#initializeViewModelState(Bundle, Supplier)}.
 */
public class EditAssessmentFragment extends Fragment {

    private static final String LOG_TAG = EditAssessmentFragment.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());

    private final CompositeDisposable compositeDisposable;
    private EditAssessmentViewModel viewModel;

    public EditAssessmentFragment() {
        Log.d(LOG_TAG, "Constructing EditAssessmentFragment");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_assessment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveImageButtonClick);
        view.findViewById(R.id.deleteImageButton).setOnClickListener(this::onDeleteImageButtonClick);
        view.findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelImageButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAssessmentViewModel.class);
    }

    private void onSaveImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onSaveImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onDeleteImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_assessment, R.string.message_delete_assessment_confirm, requireContext()).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::onDeleteSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onCancelImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter onCancelImageButtonClick");
        verifySaveChanges();
    }

    private void onSaveOperationFinished(ValidationMessage.ResourceMessageResult messages) {
        Log.d(LOG_TAG, "Enter onSaveOperationFinished");
        if (messages.isSucceeded()) {
            requireActivity().finish();
        } else {
            Resources resources = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if (messages.isWarning()) {
                builder.setTitle(R.string.title_save_warning)
                        .setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_warning)
                        .setPositiveButton(R.string.response_yes, (dialog, which) -> {
                            dialog.dismiss();
                            compositeDisposable.clear();
                            compositeDisposable.add(viewModel.save(true).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
                        }).setNegativeButton(R.string.response_no, (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle(R.string.title_save_error).setMessage(messages.join("\n", resources)).setIcon(R.drawable.dialog_error);
            }
            AlertDialog dlg = builder.setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error saving assessment", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

    private void onDeleteSucceeded() {
        Log.d(LOG_TAG, "Enter onDeleteSucceeded");
        requireActivity().finish();
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error deleting assessment", throwable);
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), requireContext()).showDialog();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, requireContext()).showYesNoCancelDialog(
                    () -> requireActivity().finish(),
                    () -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save(false).subscribe(this::onSaveOperationFinished, this::onSaveFailed));
                    }, null);
        } else {
            requireActivity().finish();
        }
    }

}