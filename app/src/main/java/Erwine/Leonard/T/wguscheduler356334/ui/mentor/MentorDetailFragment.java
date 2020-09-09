package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A placeholder fragment containing a simple view.
 */
public class MentorDetailFragment extends Fragment {

    private static final String LOG_TAG = MentorDetailFragment.class.getName();
    private static final String ARG_MENTOR_ID = "mentor_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";
    public static final String STATE_KEY_MENTOR_NAME = "mentor_name";
    public static final String STATE_KEY_MENTOR_NOTES = "mentor_notes";
    private static final String STATE_KEY_MENTOR_ID = ARG_MENTOR_ID;

    private final CompositeDisposable compositeDisposable;
    private MentorDetailViewModel mViewModel;
    private boolean editInitialized;
    private EditText mentorNameEditText;
    private EditText notesEditTextMultiLine;

    public MentorDetailFragment() {
        Log.i(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailFragment");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailFragment.onCreateView");
        View root = inflater.inflate(R.layout.fragment_mentor_detail, container, false);
        mentorNameEditText = root.findViewById(R.id.mentorNameEditText);
        notesEditTextMultiLine = root.findViewById(R.id.notesEditTextMultiLine);
        if (null != savedInstanceState && savedInstanceState.containsKey(STATE_KEY_EDIT_INITIALIZED)) {
            editInitialized = savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
            if (editInitialized) {
                mentorNameEditText.setText(savedInstanceState.getCharSequence(STATE_KEY_MENTOR_NAME));
                notesEditTextMultiLine.setText(savedInstanceState.getCharSequence(STATE_KEY_MENTOR_NOTES));
            }
        } else {
            editInitialized = false;
        }
        Log.i(LOG_TAG, "Exit Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailFragment.onCreateView");
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(MentorDetailViewModel.class);
        mViewModel.getLiveData().observe(requireActivity(), this::onMentorEntityChanged);

        mentorNameEditText.setOnEditorActionListener(this::onMentorNameEditorAction);
        mentorNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(s -> {
            if (s.trim().isEmpty()) {
                mentorNameEditText.setError("Name is required.");
            } else {
                mentorNameEditText.setError(null);
            }
            MentorEntity mentor = mViewModel.getLiveData().getValue();
            if (null != mentor) {
                mentor.setName(s);
            }
        }));
        notesEditTextMultiLine.addTextChangedListener(StringHelper.createAfterTextChangedListener(s -> {
            MentorEntity mentor = mViewModel.getLiveData().getValue();
            if (null != mentor) {
                mentor.setNotes(s);
            }
        }));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
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

    private boolean onMentorNameEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    private void onCancelButtonClick(View view) {

    }

    private void onSaveButtonClick(View view) {

    }

}