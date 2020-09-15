package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StateHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = EditTermFragment.class.getName();

    private final CompositeDisposable compositeDisposable;
    private TermViewModel viewModel;
    private ImageButton saveImageButton;
    private ImageButton deleteImageButton;

    public static Fragment newInstance(long termId) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.newInstance(%d)", termId));
        return StateHelper.setIdArgs(TermViewModel.ARGUMENT_KEY_TERM_ID, termId, new EditTermFragment(), new Bundle());
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditTermFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_edit_term, container, false);
        saveImageButton = view.findViewById(R.id.saveImageButton);
        deleteImageButton = view.findViewById(R.id.deleteImageButton);
        saveImageButton.setOnClickListener(this::onSaveTermImageButtonClick);
        deleteImageButton.setOnClickListener(this::onDeleteImageButtonClick);
        view.findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelTermEditImageButtonClick);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TermViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.restoreState(savedInstanceState, this::getArguments).subscribe(this::onTermLoadSuccess, this::onTermLoadFailed));
    }

    private void onTermLoadSuccess(TermEntity termEntity) {
        if (null == termEntity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
        viewModel.getSavableLiveData().observe(getViewLifecycleOwner(), this::onCanSaveLiveDataChanged);
    }

    private void onTermLoadFailed(Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_read_error)
                .setMessage(getString(R.string.format_message_read_error, throwable.getMessage()))
                .setOnCancelListener(dialog -> requireActivity().finish())
                .setCancelable(true).create();
        dlg.show();
    }

    private void onCanSaveLiveDataChanged(Boolean canSave) {
        Log.d(LOG_TAG, String.format("Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onOptionsItemSelected(canSave: %s)", canSave));
        saveImageButton.setEnabled(canSave);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            verifySaveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveInstanceState");
        viewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onSaveTermImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveTermImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::onDbOperationSucceeded, this::onSaveFailed));
    }

    private void onDbOperationSucceeded() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDbOperationSucceeded");
        requireActivity().finish();
    }

    private void onSaveFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveFailed");
        AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle(R.string.title_save_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onDeleteImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDeleteImageButtonClick");
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_delete_term)
                .setMessage(R.string.message_delete_term_confirm).setPositiveButton(R.string.response_yes, (dialogInterface, i1) -> {
                    compositeDisposable.clear();
                    compositeDisposable.add(viewModel.delete().subscribe(this::onDbOperationSucceeded, this::onDeleteFailed));
                }).setNegativeButton(R.string.response_no, null).create();
        dialog.show();
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDeleteFailed");
        AlertDialog dlg = new AlertDialog.Builder(requireContext()).setTitle(R.string.title_delete_error)
                .setMessage(getString(R.string.format_message_delete_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onCancelTermEditImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onCancelTermEditImageButtonClick");
        verifySaveChanges();
    }

    private void verifySaveChanges() {
        if (viewModel.isChanged()) {
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(requireContext())
                    .setTitle(R.string.title_discard_changes)
                    .setMessage(getString(R.string.message_discard_changes))
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> requireActivity().finish())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save().subscribe(() -> requireActivity().finish(), this::onSaveFailed));
                    })
                    .create();
            dlg.show();
        } else {
            requireActivity().finish();
        }
    }

}