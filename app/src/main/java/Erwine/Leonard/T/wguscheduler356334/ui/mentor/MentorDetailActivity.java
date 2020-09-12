package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MentorDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MentorDetailActivity.class.getName();
    public static final String EXTRAS_KEY_MENTOR_ID = "mentorId";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";
    public static final String STATE_KEY_MENTOR_NAME = "mentor_name";
    public static final String STATE_KEY_MENTOR_NOTES = "mentor_notes";
    public static final String STATE_KEY_MENTOR_PHONE_NUMBERS = "mentor_notes";
    public static final String STATE_KEY_MENTOR_EMAIL_ADDRESSES = "mentor_notes";

    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private final MentorEditState editedMentor;
    private ViewPager mViewPager;
    private boolean editInitialized;
    private Long mentorId;
    private MentorEntity originalValues;
    private FloatingActionButton saveFloatingActionButton;

    public MentorDetailActivity() {
        Log.i(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity");
        dbLoader = DbLoader.getInstance(getApplication());
        editedMentor = dbLoader.getEditedMentor();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_mentor_detail);
        if (null != savedInstanceState && savedInstanceState.containsKey(STATE_KEY_EDIT_INITIALIZED)) {
            editInitialized = savedInstanceState.getBoolean(STATE_KEY_EDIT_INITIALIZED, false);
            if (savedInstanceState.containsKey(EXTRAS_KEY_MENTOR_ID)) {
                mentorId = savedInstanceState.getLong(EXTRAS_KEY_MENTOR_ID);
            } else {
                mentorId = null;
            }
        } else {
            editInitialized = false;
            Bundle arguments = getIntent().getExtras();
            if (null != arguments && arguments.containsKey(EXTRAS_KEY_MENTOR_ID)) {
                mentorId = arguments.getLong(EXTRAS_KEY_MENTOR_ID);
            } else {
                mentorId = null;
            }
        }

        if (editInitialized) {
            if (null == savedInstanceState) {
                originalValues = new MentorEntity("", "", "", "");
            } else {
                originalValues = new MentorEntity(savedInstanceState.getString(STATE_KEY_MENTOR_NAME), savedInstanceState.getString(STATE_KEY_MENTOR_NOTES),
                        savedInstanceState.getString(STATE_KEY_MENTOR_PHONE_NUMBERS), savedInstanceState.getString(STATE_KEY_MENTOR_EMAIL_ADDRESSES));
            }
        } else {
            Single<MentorEntity> op;
            if (null == mentorId) {
                Log.i(LOG_TAG, "Initializing for new mentor");
                op = dbLoader.ensureNewEditedMentor();
            } else {
                Log.i(LOG_TAG, String.format("Loading mentor with id of %d", mentorId));
                op = dbLoader.ensureEditedMentorId(mentorId);
            }
            compositeDisposable.add(op.subscribe(mentorEntity -> originalValues = new MentorEntity(mentorEntity.getName(), mentorEntity.getNotes(), mentorEntity.getPhoneNumbers(), mentorEntity.getEmailAddresses()), throwable -> {
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                        .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
                dlg.show();
            }));
        }
        MentorDetailPagerAdapter mentorDetailPagerAdapter = new MentorDetailPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mentorDetailPagerAdapter);
        saveFloatingActionButton = findViewById(R.id.saveFloatingActionButton);
        saveFloatingActionButton.setOnClickListener(this::onSaveFloatingActionButtonClick);
        findViewById(R.id.deleteFloatingActionButton).setOnClickListener(this::onDeleteFloatingActionButtonClick);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
        editedMentor.getIsValidLiveData().observe(this, this::onIsValidChanged);

//        FloatingActionButton fab = findViewById(R.id.saveFloatingActionButton);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        Log.i(LOG_TAG, "Exit onCreate");
    }

    private void onIsValidChanged(Boolean isValid) {
        saveFloatingActionButton.setEnabled(isValid);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "Enter onSaveInstanceState");
        if (null != mentorId) {
            outState.putLong(EXTRAS_KEY_MENTOR_ID, mentorId);
        }
        outState.putString(STATE_KEY_MENTOR_NAME, originalValues.getName());
        outState.putString(STATE_KEY_MENTOR_NOTES, originalValues.getNotes());
        outState.putString(STATE_KEY_MENTOR_PHONE_NUMBERS, originalValues.getPhoneNumbers());
        outState.putString(STATE_KEY_MENTOR_EMAIL_ADDRESSES, originalValues.getEmailAddresses());
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "Exit onSaveInstanceState");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            checkOnBackHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkOnBackHome();
    }

    private void checkOnBackHome() {
        MentorEntity entity = Objects.requireNonNull(dbLoader.getEditedMentor().getLiveData().getValue());
        if (originalValues.getName().equals(entity.getName()) && originalValues.getNotes().equals(entity.getNotes()) &&
                originalValues.getPhoneNumbers().equals(
                        Objects.requireNonNull(dbLoader.getPhoneNumbers().getValue()).stream().sorted().map(PhoneNumberEntity::getValue).filter(t -> !t.isEmpty())
                                .collect(Collectors.joining("\n"))
                ) && originalValues.getEmailAddresses().equals(
                Objects.requireNonNull(dbLoader.getEmailAddresses().getValue()).stream().sorted().map(EmailAddressEntity::getValue).filter(t -> !t.isEmpty())
                        .collect(Collectors.joining("\n"))
        )) {
            finish();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.title_discard_changes)
                    .setMessage(R.string.message_discard_changes).setCancelable(true).setPositiveButton(R.string.response_yes,
                            (dialog1, which) -> finish()).setNegativeButton(R.string.response_no, null).create();
            dialog.show();
        }
    }

    private synchronized void onSaveFloatingActionButtonClick(View view) {
        compositeDisposable.clear();
        Disposable d = dbLoader.saveEditedMentor(false, true, true).subscribe(this::finish,
                throwable -> {
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                            .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
                    dialog.show();
                });
        compositeDisposable.add(d);
    }

    private void onDeleteFloatingActionButtonClick(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.title_delete_mentor)
                .setMessage(getString(R.string.message_delete_mentor_confirm)).setCancelable(true).setPositiveButton(R.string.response_yes, (dialog1, which) -> {
                    compositeDisposable.clear();
                    compositeDisposable.add(dbLoader.deletedEditedMentor().subscribe(this::finish, throwable -> {
                        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.title_delete_error)
                                .setMessage(getString(R.string.format_message_delete_error, throwable.getMessage())).setCancelable(true).create();
                        dlg.show();
                    }));
                }).setNegativeButton(R.string.response_no, null).create();
        dialog.show();
    }

}