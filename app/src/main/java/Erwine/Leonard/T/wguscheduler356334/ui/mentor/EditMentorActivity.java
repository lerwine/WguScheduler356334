package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EditMentorActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentor_id";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";

    private boolean editInitialized;
    private TextView mentorNameErrorTextView;
    private EditText mentorNameEditText;
    private TextView contactInfoErrorTextView;
    private TabLayout editMentorTabLayout;
    private ViewPager editMentorViewPager;
    private EmailAddressessFragment emailAddressesPagerItemFragment;
    private PhoneNumbersFragment phoneNumbersPagerItemFragment;
    private NotesFragment notesEditText;
    private EditMentorViewModel itemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        setSupportActionBar(findViewById(R.id.mentorEditToolbar));
        ActionBar supportActionBar = Objects.requireNonNull(getSupportActionBar());
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_two_tone_save_24);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        editInitialized = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
        mentorNameErrorTextView = findViewById(R.id.mentorNameErrorTextView);
        mentorNameEditText = findViewById(R.id.mentorNameEditText);
        contactInfoErrorTextView = findViewById(R.id.contactInfoErrorTextView);
        editMentorTabLayout = findViewById(R.id.editMentorTabLayout);
        editMentorViewPager = findViewById(R.id.editMentorViewPager);
        emailAddressesPagerItemFragment = (EmailAddressessFragment) getSupportFragmentManager().findFragmentById(R.id.emailAddressesPagerItemFragment);
        phoneNumbersPagerItemFragment = (PhoneNumbersFragment) getSupportFragmentManager().findFragmentById(R.id.phoneNumbersPagerItemFragment);
        notesEditText = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.notesFragment);
        mentorNameEditText.addTextChangedListener(Values.textWatcherForTextChanged((t) -> {
            if (t.trim().isEmpty()) {
                mentorNameErrorTextView.setVisibility(View.VISIBLE);
            } else {
                mentorNameErrorTextView.setVisibility(View.GONE);
            }
        }));
        itemViewModel = MainActivity.getViewModelFactory(getApplication()).create(EditMentorViewModel.class);
        itemViewModel.getLiveData().observe(this, this::onMentorEntityChanged);
        editMentorTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                editMentorViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Bundle extras = getIntent().getExtras();
        if (null == extras) {
            setTitle(R.string.title_activity_new_mentor);
        } else {
            setTitle(R.string.title_activity_edit_mentor);
            int id = extras.getInt(EXTRAS_KEY_MENTOR_ID);
            itemViewModel.load(id).onErrorReturn((throwable) -> {
                new AlertDialog.Builder(this).setTitle(R.string.read_error_title)
                        .setMessage(getString(R.string.read_error_message, throwable.getMessage())).setCancelable(false).show();
                finish();
                return null;
            }).subscribe();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mentor_editor, menu);
        if (null == itemViewModel.getLiveData().getValue()) {
            MenuItem menuItem = menu.findItem(R.id.action_mentor_delete);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (itemId == R.id.action_mentor_delete) {
            new AlertDialog.Builder(this).setTitle(R.string.delete_mentor_title).setMessage(R.string.delete_mentor_confirm).setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
                itemViewModel.delete().subscribe(this::finish, (throwable) ->
                        new AlertDialog.Builder(this).setTitle(R.string.delete_error_title)
                                .setMessage(getString(R.string.delete_error_message, throwable.getMessage())).setCancelable(false).show()
                );
            }).setNegativeButton(R.string.no, null).show();
        } else if (itemId == R.id.action_mentor_cancel) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {

    }

    private void onMentorEntityChanged(MentorEntity mentorEntity) {
        if (!editInitialized) {
            mentorNameEditText.setText(mentorEntity.getName());
            emailAddressesPagerItemFragment.setEmailAddresses(mentorEntity.getEmailAddresses());
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        Context context;
        int totalTabs;

        public PagerAdapter(Context c, FragmentManager fm, int totalTabs) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            context = c;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) throws IllegalArgumentException {
            switch (position) {
                case 0:
                    return emailAddressesPagerItemFragment;
                case 1:
                    return phoneNumbersPagerItemFragment;
                case 2:
                    return notesEditText;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}