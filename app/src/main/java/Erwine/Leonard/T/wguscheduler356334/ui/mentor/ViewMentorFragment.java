package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class ViewMentorFragment extends Fragment {

    private ViewMentorViewModel mViewModel;
    private TextView mentorNameTextView;
    private TextView phoneNumbersTextView;
    private TextView emailAddressesTextView;
    private EditText notesEditTextTextMultiLine;

    public static ViewMentorFragment newInstance() {
        return new ViewMentorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_mentor, container, false);
        ImageButton imageButton = view.findViewById(R.id.editMentorNameImageButton);
        imageButton.setOnClickListener(this::onEditMentorNameImageButtonClick);
        mentorNameTextView = view.findViewById(R.id.mentorNameTextView);
        imageButton = view.findViewById(R.id.editPhoneNumberImageButton);
        imageButton.setOnClickListener(this::onEditPhoneNumberImageButtonClick);
        phoneNumbersTextView = view.findViewById(R.id.phoneNumbersTextView);
        imageButton = view.findViewById(R.id.editEmailAddressImageButton);
        imageButton.setOnClickListener(this::onEditEmailAddressImageButtonClick);
        emailAddressesTextView = view.findViewById(R.id.emailAddressesTextView);
        notesEditTextTextMultiLine = view.findViewById(R.id.notesEditTextTextMultiLine);
        Button button = view.findViewById(R.id.saveNotesButton);
        button.setOnClickListener(this::onSaveNotesButtonClick);
        return view;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void onEditMentorNameImageButtonClick(View v) {
        final FragmentActivity activity = getActivity();
        EditText editText = new EditText(activity);
        MentorEntity entity = Objects.requireNonNull(mViewModel.getLiveData().getValue());
        editText.setText(entity.getName());
        new AlertDialog.Builder(activity).setView(editText).setTitle(R.string.title_change_mentor_name).setCancelable(true).setNegativeButton(R.string.command_cancel, null)
                .setPositiveButton(R.string.command_ok, (dialog, which) -> mViewModel.saveName(editText.getText().toString()).subscribe(() ->
                        mentorNameTextView.setText(Objects.requireNonNull(mViewModel.getLiveData().getValue()).getName()), throwable -> {
                    Log.e(getClass().getName(), "Error saving name", throwable);
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.title_save_error)
                            .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).show();
                })).show();
    }

    private void onEditPhoneNumberImageButtonClick(View v) {

    }

    private void onEditEmailAddressImageButtonClick(View v) {

    }

    private void onSaveNotesButtonClick(View v) {
        //noinspection ResultOfMethodCallIgnored
        mViewModel.saveNotes(notesEditTextTextMultiLine.getText().toString()).subscribe(() -> requireActivity().finish(), throwable -> {
            Log.e(getClass().getName(), "Error saving notes", throwable);
            new AlertDialog.Builder(getActivity()).setTitle(R.string.title_save_error)
                    .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).show();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        mViewModel = MainActivity.getViewModelFactory(activity.getApplication()).create(ViewMentorViewModel.class);
        Bundle extras = Objects.requireNonNull(activity.getIntent().getExtras());
        int id = extras.getInt(ViewMentorActivity.EXTRAS_KEY_MENTOR_ID);
        mViewModel.getLiveData().observe(activity, this::onMentoryEntityChanged);
        mViewModel.load(id).onErrorReturn((throwable) -> {
            new AlertDialog.Builder(activity).setTitle(R.string.title_read_error)
                    .setMessage(getString(R.string.format_message_read_error, throwable.getMessage())).setCancelable(true).show();
            activity.finish();
            return null;
        }).subscribe();
    }

    private void onMentoryEntityChanged(MentorEntity mentor) {
        mentorNameTextView.setText(mentor.getName());
        phoneNumbersTextView.setText(mentor.getPhoneNumbers());
        emailAddressesTextView.setText(mentor.getEmailAddresses());
        notesEditTextTextMultiLine.setText(mentor.getNotes());
    }
}