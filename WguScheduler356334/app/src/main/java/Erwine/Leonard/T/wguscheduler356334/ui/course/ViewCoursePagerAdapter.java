package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertListFragment;
import Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment;

/**
 * Pager adapter for {@link Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity}.
 */
public class ViewCoursePagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public ViewCoursePagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AssessmentListFragment();
            case 1:
                return new CourseAlertListFragment();
            case 2:
                return new EditCourseFragment();
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.title_assessments);
            case 1:
                return context.getResources().getString(R.string.title_alerts);
            case 2:
                return context.getResources().getString(R.string.title_activity_edit);
            default:
                throw new IllegalStateException(String.format("Unexpected title position %d", position));
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
