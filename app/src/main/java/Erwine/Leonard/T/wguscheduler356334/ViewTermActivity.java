package Erwine.Leonard.T.wguscheduler356334;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.ui.term.ViewTermPagerAdapter;

public class ViewTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    public static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRAS_KEY_TERM_ID = "termId";
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";

    private ViewTermPagerAdapter sectionsPagerAdapter;
    private boolean stateInitialized;
    private Long termId;
    private ViewPager viewPager;

    private void restoreSavedState(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            stateInitialized = savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
            if (stateInitialized) {
                if (savedInstanceState.containsKey(ARGUMENT_KEY_TERM_ID)) {
                    termId = savedInstanceState.getLong(ARGUMENT_KEY_TERM_ID);
                }
                return;
            }
        }
        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRAS_KEY_TERM_ID)) {
            termId = intent.getLongExtra(EXTRAS_KEY_TERM_ID, -1L);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ViewTermActivity.onCreate");
        super.onCreate(savedInstanceState);
        restoreSavedState(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        sectionsPagerAdapter = new ViewTermPagerAdapter(termId, this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.getCurrentItem();
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.viewTermTabLayout);
        tabs.setupWithViewPager(viewPager);
//        FloatingActionButton fab = findViewById(R.id.saveFloatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}