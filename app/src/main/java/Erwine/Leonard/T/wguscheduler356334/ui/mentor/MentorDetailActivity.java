package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.R;

public class MentorDetailActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentorId";

    private ViewPager mViewPager;
    private long mentorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mentor_detail);
        if (null != savedInstanceState && savedInstanceState.containsKey(EXTRAS_KEY_MENTOR_ID)) {
            mentorId = savedInstanceState.getLong(EXTRAS_KEY_MENTOR_ID);
        } else {
            mentorId = getIntent().getLongExtra(EXTRAS_KEY_MENTOR_ID, 0L);
        }

        MentorDetailPagerAdapter mentorDetailPagerAdapter = new MentorDetailPagerAdapter(this, mentorId, getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mentorDetailPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(EXTRAS_KEY_MENTOR_ID, mentorId);
        super.onSaveInstanceState(outState);
    }

}