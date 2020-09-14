package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import Erwine.Leonard.T.wguscheduler356334.R;

public class CoursePropertiesFragment extends Fragment {

    private EditCourseViewModel mViewModel;

    public static CoursePropertiesFragment newInstance() {
        return new CoursePropertiesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_properties, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EditCourseViewModel.class);
        // TODO: Use the ViewModel
    }

}