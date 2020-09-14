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
import io.reactivex.disposables.CompositeDisposable;

public class EditTermFragment extends Fragment {

    private static final String LOG_TAG = EditTermFragment.class.getName();
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";

    private final CompositeDisposable compositeDisposable;
    private TermPropertiesViewModel mViewModel;
    private ImageButton saveImageButton;
    private ImageButton deleteImageButton;
    private Long termId;

    public static EditTermFragment newInstance(Long termId) {
        EditTermFragment fragment = new EditTermFragment();
        Bundle args = new Bundle();
        if (null != termId) {
            args.putLong(ARGUMENT_KEY_TERM_ID, termId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditTermFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment");
        compositeDisposable = new CompositeDisposable();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onCreate");
//        super.onCreate(savedInstanceState);
//        if (null != savedInstanceState) {
//            stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
//            if (stateInitialized) {
//                if (savedInstanceState.containsKey(ARGUMENT_KEY_TERM_ID)) {
//                    termId = savedInstanceState.getLong(ARGUMENT_KEY_TERM_ID);
//                }
//                return;
//            }
//        }
//        Bundle args = getArguments();
//        if (null != args && args.containsKey(ARGUMENT_KEY_TERM_ID)) {
//            termId = args.getLong(ARGUMENT_KEY_TERM_ID);
//        }
//    }

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
//        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermPropertiesViewModel.class);
        mViewModel = new ViewModelProvider(requireActivity()).get(TermPropertiesViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(mViewModel.restoreState(savedInstanceState, () -> getArguments()).subscribe(this::onTermLoadSuccess, this::onTermLoadFailed));
//        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermViewModel.class);
//        if (null != termId) {
//            compositeDisposable.clear();
//            compositeDisposable.add(mViewModel.load(termId).subscribe(this::onTermLoadSuccess, this::onTermLoadFailed));
//        }
    }

    private void onTermLoadSuccess(TermEntity termEntity) {
        if (null == termEntity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", termEntity));

        mViewModel.getSavableLiveData().observe(getViewLifecycleOwner(), this::onCanSaveLiveDataChanged);
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
        saveImageButton.setEnabled(canSave);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveInstanceState");
        mViewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onSaveTermImageButtonClick(View view) {
        compositeDisposable.clear();
        compositeDisposable.add(mViewModel.save().subscribe(this::onDbOperationSucceeded, this::onSaveFailed));
    }

    private void onDbOperationSucceeded() {
        requireActivity().finish();
    }

    private void onSaveFailed(Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle(R.string.title_save_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onDeleteImageButtonClick(View view) {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(R.string.title_delete_term).setMessage(R.string.message_delete_mentor_confirm).setPositiveButton(R.string.response_yes, (dialogInterface, i1) -> {
            compositeDisposable.clear();
            compositeDisposable.add(mViewModel.delete().subscribe(this::onDbOperationSucceeded, this::onDeleteFailed));
        }).setNegativeButton(R.string.response_no, null).create();
        dialog.show();
    }

    private void onDeleteFailed(Throwable throwable) {
        AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle(R.string.title_delete_error)
                .setMessage(getString(R.string.format_message_delete_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

    private void onCancelTermEditImageButtonClick(View view) {
        requireActivity().finish();
    }
}