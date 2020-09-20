package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment;

public class ViewTermPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final long termId;

    public ViewTermPagerAdapter(long termId, Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.termId = termId;
        this.context = context;
    }

    public long getTermId() {
        return termId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CourseListFragment();
            case 1:
                return new EditTermFragment();
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.title_courses);
            case 1:
                return context.getResources().getString(R.string.title_activity_edit);
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
