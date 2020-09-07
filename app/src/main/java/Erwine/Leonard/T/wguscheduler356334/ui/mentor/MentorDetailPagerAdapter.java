package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Erwine.Leonard.T.wguscheduler356334.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MentorDetailPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_title_general, R.string.tab_title_phone, R.string.tab_title_email};
    private final Context mContext;

    public MentorDetailPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                // TODO: Need to get mentor ID, instead
                return PhoneNumberListFragment.newInstance(position);
            case 2:
                // TODO: Need to get mentor ID, instead
                return EmailAddressFragment.newInstance(position);
            default:
                // TODO: Need to get mentor ID, instead
                return MentorDetailFragment.newInstance(position + 1);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}