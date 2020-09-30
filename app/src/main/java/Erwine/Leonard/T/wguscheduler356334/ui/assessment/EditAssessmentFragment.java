package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;

/**
 * Fragment for editing the properties of an {@link Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity}.
 * This assumes that the parent activity ({@link Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity} or {@link Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity})
 * calls {@link EditAssessmentViewModel#initializeViewModelState(Bundle, Supplier)}.
 */
public class EditAssessmentFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private EditAssessmentViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_assessment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditAssessmentViewModel.class);
    }
}