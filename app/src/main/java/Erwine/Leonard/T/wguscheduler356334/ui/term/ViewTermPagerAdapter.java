package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ui.course.CourseListFragment;

public class ViewTermPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private final long termId;
    private final MutableLiveData<Fragment> currentFragmentLiveData;

    public ViewTermPagerAdapter(long termId, Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        currentFragmentLiveData = new MutableLiveData<>();
        this.termId = termId;
        mContext = context;
    }

    public LiveData<Fragment> getCurrentFragmentLiveData() {
        return currentFragmentLiveData;
    }

    public long getTermId() {
        return termId;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof Fragment) {
            currentFragmentLiveData.postValue((Fragment) object);
        } else {
            currentFragmentLiveData.postValue(null);
        }
        super.setPrimaryItem(container, position, object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        switch (position) {
            case 0:
                return CourseListFragment.newInstance(termId);
            case 1:
                return EditTermFragment.newInstance(termId);
            default:
                throw new IllegalStateException(String.format("Unexpected pager position %d", position));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.title_courses);
            case 1:
                return mContext.getResources().getString(R.string.title_activity_edit);
            default:
                throw new IllegalStateException(String.format("Unexpected title position %d", position));
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
