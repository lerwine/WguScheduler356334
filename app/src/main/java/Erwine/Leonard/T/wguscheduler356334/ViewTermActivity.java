package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment;
import Erwine.Leonard.T.wguscheduler356334.ui.term.ViewTermPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.StateHelper;

public class ViewTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    public static final String EXTRAS_KEY_TERM_ID = "termId";

    private ViewTermPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ViewTermActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        StateHelper.restoreState(EXTRAS_KEY_TERM_ID, savedInstanceState, () -> getIntent().getExtras(), this::onInitializePager, ((aBoolean, bundle) -> {
            AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle("Not Found")
                    .setMessage("Term ID not specified")
                    .setOnCancelListener(dialog -> finish())
                    .setCancelable(true)
                    .create();
            dlg.show();
        }));
//        FloatingActionButton fab = findViewById(R.id.saveFloatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void onInitializePager(Bundle bundle, long termId) {
        sectionsPagerAdapter = new ViewTermPagerAdapter(termId, this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.viewTermTabLayout);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (null != sectionsPagerAdapter) {
            StateHelper.saveState(EXTRAS_KEY_TERM_ID, sectionsPagerAdapter.getTermId(), outState);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if (null != sectionsPagerAdapter) {
            Fragment fragment = sectionsPagerAdapter.getCurrentFragmentLiveData().getValue();
            if (fragment instanceof EditTermFragment) {
                viewPager.setCurrentItem(0);
                return;
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}