package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment;

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
                return new EditCourseFragment();
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
