package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import io.reactivex.disposables.CompositeDisposable;

public class EditMentorFragment extends Fragment {

    private final CompositeDisposable compositeDisposable;
    private EditMentorViewModel mViewModel;

    public EditMentorFragment() {
        compositeDisposable = new CompositeDisposable();
    }

    public static EditMentorFragment newInstance() {
        return new EditMentorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_mentor, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(EditMentorViewModel.class);
        // TODO: Use the ViewModel
    }


}