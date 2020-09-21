package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.util.AlertHelper;

public class CourseNotesFragment extends Fragment {

    private TextView notesTextView;
    private EditCourseViewModel viewModel;

    public CourseNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notesTextView = view.findViewById(R.id.notesTextView);
        view.findViewById(R.id.editNotesFloatingActionButton).setOnClickListener(this::onEditNotesFloatingActionButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get shared view model, which is initialized by AddCourseActivity and ViewCourseActivity
        viewModel = new ViewModelProvider(requireActivity()).get(EditCourseViewModel.class);
        viewModel.getEntityLiveData().observe(getViewLifecycleOwner(), this::onEntityLoaded);
    }

    private void onEntityLoaded(CourseDetails entity) {
        if (null != entity) {
            notesTextView.setText(viewModel.getNotes());
        }
    }

    private void onEditNotesFloatingActionButtonClick(View view) {
        AlertHelper.showEditMultiLineTextDialog(R.string.title_edit_notes, viewModel.getNotes(), requireContext(), s -> {
            notesTextView.setText(s);
            viewModel.setNotes(s);
        });
    }
}