package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.R;

public class ViewTermActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_TERM_ID = "termId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        ViewTermPagerAdapter sectionsPagerAdapter = new ViewTermPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.viewTermTabLayout);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.saveFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}