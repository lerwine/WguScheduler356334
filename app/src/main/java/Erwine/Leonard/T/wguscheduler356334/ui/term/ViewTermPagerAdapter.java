package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment;

public class ViewTermPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.title_courses, R.string.title_properties};
    private static final List<Supplier<Fragment>> TAB_FRAGMENTS = Arrays.asList(CourseListFragment::new, TermPropertiesFragment::new);
    private final Context mContext;

    public ViewTermPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < 0 || position >= TAB_FRAGMENTS.size())
            throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        return TAB_FRAGMENTS.get(position).get();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
