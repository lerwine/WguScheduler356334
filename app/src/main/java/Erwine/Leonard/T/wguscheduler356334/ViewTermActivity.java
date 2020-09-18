package Erwine.Leonard.T.wguscheduler356334;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.term.ViewTermPagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.StateHelper;
import io.reactivex.disposables.CompositeDisposable;

public class ViewTermActivity extends AppCompatActivity {

    private static final String LOG_TAG = ViewTermActivity.class.getName();
    public static final String EXTRAS_KEY_TERM_ID = "termId";

    private final CompositeDisposable compositeDisposable;
    private ViewTermPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private EditTermViewModel viewModel;

    public ViewTermActivity() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ViewTermActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
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
        viewModel = new ViewModelProvider(this).get(EditTermViewModel.class);
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
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (null != sectionsPagerAdapter) {
            StateHelper.saveState(EXTRAS_KEY_TERM_ID, sectionsPagerAdapter.getTermId(), outState);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if (viewModel.isChanged()) {
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.title_discard_changes)
                    .setMessage(getString(R.string.message_discard_changes))
                    .setPositiveButton(R.string.response_yes, (dialog, which) -> finish())
                    .setNegativeButton(R.string.response_no, (dialog, which) -> {
                        compositeDisposable.clear();
                        compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationSucceeded, this::onSaveFailed));
                    })
                    .setCancelable(true).create();
            dlg.show();
        } else {
            finish();
        }
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

    private void onSaveOperationSucceeded(@NonNull List<Integer> messageIds) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onDbOperationSucceeded");
        if (messageIds.isEmpty()) {
            finish();
        } else {
            Resources resources = getResources();
            android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                    .setMessage(messageIds.stream().map(resources::getString).collect(Collectors.joining("; "))).setCancelable(true).create();
            dlg.show();
        }
    }

    private void onSaveFailed(Throwable throwable) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermFragment.onSaveFailed");
        android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).setTitle(R.string.title_save_error)
                .setMessage(getString(R.string.format_message_save_error, throwable.getMessage())).setCancelable(true).create();
        dlg.show();
    }

}