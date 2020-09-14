package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

public class MentorPropertiesFragment extends Fragment {

    private static final String LOG_TAG = MentorPropertiesFragment.class.getName();
    private EditMentorViewModel viewModel;
    private EditText mentorNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailAddressEditText;
    private TextView mentorNotesTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MentorPropertiesFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorPropertiesFragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorPropertiesFragment.onCreateView");
        View view = inflater.inflate(R.layout.fragment_mentor_properties, container, false);

        mentorNameEditText = view.findViewById(R.id.mentorNameEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        emailAddressEditText = view.findViewById(R.id.emailAddressEditText);
        mentorNotesTextView = view.findViewById(R.id.mentorNotesTextView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorPropertiesFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditMentorViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onLoadSuccess);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorPropertiesFragment.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorPropertiesFragment.onSaveInstanceState");
        viewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    private void onLoadSuccess(MentorEntity entity) {
        if (null == entity) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %s", entity));

        if (viewModel.isFromInitializedState()) {
            mentorNameEditText.setText(viewModel.getName());
            phoneNumberEditText.setText(viewModel.getPhoneNumber());
            emailAddressEditText.setText(viewModel.getEmailAddress());
            mentorNotesTextView.setText(viewModel.getNotes());
        } else {
            mentorNameEditText.setText(entity.getName());
            phoneNumberEditText.setText(entity.getPhoneNumber());
            emailAddressEditText.setText(entity.getEmailAddress());
            mentorNotesTextView.setText(entity.getNotes());
        }

        mentorNameEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onMentorNameTextChanged));
        phoneNumberEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onPhoneNumberTextChanged));
        emailAddressEditText.addTextChangedListener(StringHelper.createAfterTextChangedListener(viewModel::onEmailAddressTextChanged));
        mentorNotesTextView.setOnClickListener(this::onMentorNotesEditTextClick);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        viewModel.getNameValidLiveData().observe(viewLifecycleOwner, this::onNameValidChanged);
        viewModel.getContactValidLiveData().observe(viewLifecycleOwner, this::onContactValidChanged);
    }

    private void onMentorNotesEditTextClick(View view) {
        Context context = requireContext();
        EditText editText = new EditText(context);
        String text = viewModel.getNotes();
        editText.setText(text);
        editText.setSingleLine(false);
        editText.setVerticalScrollBarEnabled(true);
        editText.setBackgroundResource(android.R.drawable.edit_text);
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.title_edit_notes))
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String s = editText.getText().toString();
                    if (!text.equals(s)) {
                        viewModel.onMentorNotesEditTextChanged(s);
                        mentorNotesTextView.setText(s);
                    }
                })
                .setCancelable(false).create();
        dlg.show();
    }

    private void onNameValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            mentorNameEditText.setError(null);
        } else {
            mentorNameEditText.setError(getResources().getString(R.string.message_required));
        }
    }

    private void onContactValidChanged(Boolean isValid) {
        if (null != isValid && isValid) {
            mentorNameEditText.setError(null);
        } else {
            mentorNameEditText.setError(getResources().getString(R.string.message_phone_or_email_required));
        }
    }

}