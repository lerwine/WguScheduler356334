package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel;
import Erwine.Leonard.T.wguscheduler356334.ui.course.ViewCoursePagerAdapter;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;
import io.reactivex.disposables.CompositeDisposable;

public class ViewCourseActivity extends AppCompatActivity {

    public static final String ARGUMENT_KEY_COURSE_ID = "course_id";

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;
    private ViewCoursePagerAdapter adapter;

    public ViewCourseActivity() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        viewModel = new ViewModelProvider(this).get(EditCourseViewModel.class);
        compositeDisposable.clear();
        compositeDisposable.add(viewModel.initializeViewModelState(savedInstanceState, () -> getIntent().getExtras()).subscribe(this::onEntityLoadSucceeded, this::onEntityLoadfailed));
    }

    private void onEntityLoadSucceeded(CourseEntity entity) {
        Long courseId = entity.getId();
        if (null == courseId) {
            new AlertHelper(R.drawable.dialog_error, R.string.title_not_found, R.string.message_course_id_not_specified, this).showDialog(this::finish);
        } else {
            adapter = new ViewCoursePagerAdapter(this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(adapter);
            TabLayout tabs = findViewById(R.id.editCourseTabLayout);
            tabs.setupWithViewPager(viewPager);
        }
    }

    private void onEntityLoadfailed(Throwable throwable) {
        new AlertHelper(R.drawable.dialog_error, R.string.title_read_error, getString(R.string.format_message_read_error, throwable.getMessage()), this).showDialog(this::finish);
    }
}