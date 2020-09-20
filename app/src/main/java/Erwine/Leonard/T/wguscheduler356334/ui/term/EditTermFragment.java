package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = EditTermFragment.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditTermViewModel viewModel;

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
        return inflater.inflate(R.layout.fragment_edit_term, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.saveImageButton).setOnClickListener(this::onSaveTermImageButtonClick);
        view.findViewById(R.id.deleteImageButton).setOnClickListener(this::onDeleteImageButtonClick);
        view.findViewById(R.id.cancelImageButton).setOnClickListener(this::onCancelTermEditImageButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditTermViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onTermLoaded);
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
        viewModel.saveViewModelState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onTermLoaded(TermEntity termEntity) {
        if (null == termEntity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));
    }

    private void onSaveTermImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveTermImageButtonClick");
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
    }

    private void onSaveOperationFinished(@NonNull List<Integer> messageIds) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDbOperationSucceeded");
        if (messageIds.isEmpty()) {
            requireActivity().finish();
        } else {
            Resources resources = getResources();
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.title_save_error)
                    .setMessage(messageIds.stream().map(resources::getString).collect(Collectors.joining("; "))).setCancelable(true).create();
            dlg.show();
        }
    }

    private void onDbOperationSucceeded() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDbOperationSucceeded");
        requireActivity().finish();
    }

    private void onSaveFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveFailed");
        new AlertHelper(R.drawable.dialog_error, R.string.title_save_error, getString(R.string.format_message_save_error, throwable.getMessage()), requireContext())
                .showDialog(() -> requireActivity().finish());
    }

    private void onDeleteImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDeleteImageButtonClick");
        new AlertHelper(R.drawable.dialog_warning, R.string.title_delete_term, R.string.message_delete_term_confirm, requireContext()).showYesNoDialog(() -> {
            compositeDisposable.clear();
            compositeDisposable.add(viewModel.delete().subscribe(this::onDbOperationSucceeded, this::onDeleteFailed));
        }, null);
    }

    private void onDeleteFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDeleteFailed");
        new AlertHelper(R.drawable.dialog_error, R.string.title_delete_error, getString(R.string.format_message_delete_error, throwable.getMessage()), requireContext()).showDialog();
    }

    private void onCancelTermEditImageButtonClick(View view) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onCancelTermEditImageButtonClick");
        verifySaveChanges();
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