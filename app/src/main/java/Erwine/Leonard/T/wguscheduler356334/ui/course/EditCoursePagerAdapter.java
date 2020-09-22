package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.R;

public class EditCoursePagerAdapter extends FragmentPagerAdapter {
    private final Context context;

    public EditCoursePagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CourseDatesFragment();
            case 1:
                return new CourseNotesFragment();
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.title_dates);
            case 1:
                return context.getResources().getString(R.string.label_notes);
            default:
                throw new IllegalStateException(String.format("Unexpected title position %d", position));
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
