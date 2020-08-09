package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;

public class CourseListFragment extends Fragment {

    private CourseListViewModel courseListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        courseListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(CourseListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_courses, container, false);
        final TextView textView = root.findViewById(R.id.text_courses);
        courseListViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}