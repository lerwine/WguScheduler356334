package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MentorDetailActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentorId";
    public static final String STATE_KEY_EDIT_INITIALIZED = "edit_initialized";

    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private ViewPager mViewPager;
    private boolean editInitialized;
    private Long mentorId;

    public MentorDetailActivity() {
        dbLoader = DbLoader.getInstance(getApplication());
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        if (!editInitialized) {
            if (null == mentorId) {
                dbLoader.ensureNewEditedMentor();
            } else {
                dbLoader.ensureEditedMentorId(mentorId);
            }
        }
        MentorDetailPagerAdapter mentorDetailPagerAdapter = new MentorDetailPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mentorDetailPagerAdapter);
        findViewById(R.id.saveFloatingActionButton).setOnClickListener(this::onSaveFloatingActionButtonClick);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.saveFloatingActionButton);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (null != mentorId) {
            outState.putLong(EXTRAS_KEY_MENTOR_ID, mentorId);
        }
        outState.putBoolean(STATE_KEY_EDIT_INITIALIZED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private synchronized void onSaveFloatingActionButtonClick(View view) {
        compositeDisposable.clear();
        Disposable d = dbLoader.saveEditedMentor(false, true, true).subscribe(this::finish,
                throwable -> new AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                        .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).show());
        compositeDisposable.add(d);
    }

    // TODO: Add delete FloatingActionButton

    private void onDeleteFloatingActionButtonClick(View view) {
        // TODO: Implement confirmation for Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorDetailActivity.onDeleteFloatingActionButtonClick
    }

}