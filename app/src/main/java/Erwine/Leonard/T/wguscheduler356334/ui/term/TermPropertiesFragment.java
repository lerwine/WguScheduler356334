package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import Erwine.Leonard.T.wguscheduler356334.R;

public class TermPropertiesFragment extends Fragment {

    private TermViewModel mViewModel;

    public static TermPropertiesFragment newInstance() {
        return new TermPropertiesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_term_properties, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TermViewModel.class);
        // TODO: Use the ViewModel
    }

}