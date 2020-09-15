package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.R;
import io.reactivex.disposables.CompositeDisposable;

public class EditCourseFragment extends Fragment {

    private static final String LOG_TAG = EditCourseFragment.class.getName();

    private final CompositeDisposable compositeDisposable;
    private EditCourseViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditCourseFragment() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onCreateView");
        return inflater.inflate(R.layout.fragment_edit_course, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        // TODO: Use the ViewModel
    }

}