package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.AlertListFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ViewAssessmentPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public ViewAssessmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AlertListFragment();
            case 1:
                return new EditAssessmentFragment();
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.title_activity_edit);
            case 1:
                return context.getResources().getString(R.string.title_alerts);
            default:
                throw new IllegalStateException(String.format("Unexpected title position %d", position));
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}