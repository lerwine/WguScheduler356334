package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

/**
 * A fragment representing a single Mentor detail screen.
 * This fragment is either contained in a {@link MentorListActivity}
 * in two-pane mode (on tablets) or a {@link MentorDetailActivity}
 * on handsets.
 */
public class MentorDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private final TextWatcher mNotesTextChangedListener;
    private MentorEntity mItem;
    private EditMentorViewModel itemViewModel;
    private TextView mMentorNameTextView;
    private ImageButton mEditMentorNameImageButton;
    private TextView mPhoneNumbersTextView;
    private ImageButton mEditPhoneNumbersImageButton;
    private TextView mEmailAddressesTextView;
    private ImageButton mEditEmailAddressesImageButton;
    private EditText mNotesEditTextMultiLine;
    private Button mSaveChangesButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MentorDetailFragment() {
        mNotesTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        itemViewModel = MainActivity.getViewModelFactory(getActivity().getApplication()).create(EditMentorViewModel.class);
        itemViewModel.getLiveData().observe(getViewLifecycleOwner(), this::onMentorEntityChanged);
        final Bundle arguments = getArguments();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (arguments.containsKey(ARG_ITEM_ID)) {
            if (appBarLayout != null) {
                appBarLayout.setTitle(getString(R.string.loading_elipsis));
            }
            itemViewModel.load(arguments.getInt(ARG_ITEM_ID)).onErrorReturn(throwable -> {
                Log.e(getClass().getName(), "Error loading mentor", throwable);
                new AlertDialog.Builder(getContext()).setTitle(R.string.read_error_title).setMessage(getString(R.string.read_error_message, throwable.getMessage())).setCancelable(true).show();
                if (appBarLayout != null) {
                    appBarLayout.setTitle(getString(R.string.load_failed_parentheses));
                }
                return null;
            });
        }
    }

    private void onMentorEntityChanged(MentorEntity mentorEntity) {
        mItem = mentorEntity;
        if (null != mNotesEditTextMultiLine) {
            initializeChildViews();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mentor_detail, container, false);

        mMentorNameTextView = rootView.findViewById(R.id.mentorNameTextView);
        mEditMentorNameImageButton = rootView.findViewById(R.id.editMentorNameImageButton);
        mPhoneNumbersTextView = rootView.findViewById(R.id.phoneNumbersTextView);
        mEditPhoneNumbersImageButton = rootView.findViewById(R.id.editPhoneNumbersImageButton);
        mEmailAddressesTextView = rootView.findViewById(R.id.emailAddressesTextView);
        mEditEmailAddressesImageButton = rootView.findViewById(R.id.editEmailAddressesImageButton);
        mNotesEditTextMultiLine = rootView.findViewById(R.id.notesEditTextMultiLine);
        mSaveChangesButton = rootView.findViewById(R.id.saveChangesButton);

        mEditMentorNameImageButton.setOnClickListener(this::onEditMentorNameImageButtonClick);
        mEditPhoneNumbersImageButton.setOnClickListener(this::onEditPhoneNumbersImageButtonClick);
        mEditEmailAddressesImageButton.setOnClickListener(this::onEditEmailAddressesImageButtonClick);
        mSaveChangesButton.setOnClickListener(this::onSaveChangesButtonClick);
        mNotesEditTextMultiLine.addTextChangedListener(mNotesTextChangedListener);

        if (null != mItem) {
            initializeChildViews();
        }

        return rootView;
    }

    private void initializeChildViews() {
        mMentorNameTextView.setText(mItem.getName());
        mPhoneNumbersTextView.setText(mItem.getPhoneNumbers());
        mEmailAddressesTextView.setText(mItem.getEmailAddresses());
        mNotesEditTextMultiLine.setText(mItem.getNotes());
        mEditMentorNameImageButton.setEnabled(true);
        mEditPhoneNumbersImageButton.setEnabled(true);
        mEditEmailAddressesImageButton.setEnabled(true);
        mNotesEditTextMultiLine.setEnabled(true);
        mSaveChangesButton.setEnabled(true);
    }

    private void onEditMentorNameImageButtonClick(View view) {
        if (null == mItem) {
            return;
        }
    }

    private void onEditPhoneNumbersImageButtonClick(View view) {
        if (null == mItem) {
            return;
        }
    }

    private void onEditEmailAddressesImageButtonClick(View view) {
        if (null == mItem) {
            return;
        }
    }

    private void onSaveChangesButtonClick(View view) {
        if (null == mItem) {
            return;
        }
    }
}