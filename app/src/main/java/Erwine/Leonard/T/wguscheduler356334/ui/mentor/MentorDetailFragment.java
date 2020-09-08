package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A placeholder fragment containing a simple view.
 */
public class MentorDetailFragment extends Fragment {

    private static final String ARG_MENTOR_ID = "mentor_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";
    public static final String STATE_KEY_MENTOR_NAME = "mentor_name";
    public static final String STATE_KEY_MENTOR_NOTES = "mentor_notes";
    private static final String STATE_KEY_MENTOR_ID = ARG_MENTOR_ID;

    private final CompositeDisposable compositeDisposable;
    private MentorDetailViewModel mViewModel;
    private boolean editInitialized;
    private long mentorId;
    private EditText mentorNameEditText;
    private EditText notesEditTextMultiLine;

    public MentorDetailFragment() {
        compositeDisposable = new CompositeDisposable();
    }

    public static MentorDetailFragment newInstance(long mentorId) {
        MentorDetailFragment fragment = new MentorDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_MENTOR_ID, mentorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mentor_detail, container, false);
        if (null != savedInstanceState && savedInstanceState.containsKey(STATE_KEY_EDIT_INITIALIZED)) {
            editInitialized = savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
            mentorId = savedInstanceState.getLong(STATE_KEY_MENTOR_ID);
        } else {
            editInitialized = false;
            Bundle arguments = getArguments();
            mentorId = (null == arguments) ? 0L : arguments.getLong(ARG_MENTOR_ID);
        }
        mentorNameEditText = root.findViewById(R.id.mentorNameEditText);
        notesEditTextMultiLine = root.findViewById(R.id.notesEditTextMultiLine);
//        root.findViewById(R.id.cancelButton).setOnClickListener(this::onCancelButtonClick);
//        root.findViewById(R.id.saveButton).setOnClickListener(this::onSaveButtonClick);
        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(MentorDetailViewModel.class);
        if (editInitialized && null != savedInstanceState) {
            mentorNameEditText.setText(savedInstanceState.getCharSequence(STATE_KEY_MENTOR_NAME));
            notesEditTextMultiLine.setText(savedInstanceState.getCharSequence(STATE_KEY_MENTOR_NOTES));
        }
        compositeDisposable.add(mViewModel.getEntity(mentorId).subscribe(this::onMentorEntityChanged));
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        outState.putLong(STATE_KEY_MENTOR_ID, mentorId);
        outState.putCharSequence(STATE_KEY_MENTOR_NAME, mentorNameEditText.getText());
        outState.putCharSequence(STATE_KEY_MENTOR_NOTES, notesEditTextMultiLine.getText());
        super.onSaveInstanceState(outState);
    }

    private void onMentorEntityChanged(MentorEntity mentorEntity) {
        if (null != mentorEntity && !editInitialized) {
            editInitialized = true;
            mentorNameEditText.setText(mentorEntity.getName());
            notesEditTextMultiLine.setText(mentorEntity.getNotes());
        }
    }

    private void onCancelButtonClick(View view) {

    }

    private void onSaveButtonClick(View view) {

    }

}