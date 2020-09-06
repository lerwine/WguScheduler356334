package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MentorDetailFragment extends Fragment {

    private static final String ARG_MENTOR_ID = "mentor_id";

    private MentorDetailViewModel mViewModel;

    public static MentorDetailFragment newInstance(int mentorId) {
        MentorDetailFragment fragment = new MentorDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MENTOR_ID, mentorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(MentorDetailViewModel.class);
        int mentorId = getArguments().getInt(ARG_MENTOR_ID);
        mViewModel.load(mentorId);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mentor_detail, container, false);
        final TextView textView = root.findViewById(R.id.mentorNameLabelTextView);
        mViewModel.getLiveData().observe(getViewLifecycleOwner(), m -> {

        });
        return root;
    }
}