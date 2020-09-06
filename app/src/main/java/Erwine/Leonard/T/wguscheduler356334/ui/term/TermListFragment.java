package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;

public class TermListFragment extends Fragment {

    private final List<TermEntity> mItems;
    private TermListViewModel mTermListViewModel;
    private RecyclerView mTermsRecyclerView;
    private TermListAdapter mAdapter;

    public TermListFragment() {
        mItems = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_term_list, container, false);
        mTermsRecyclerView = root.findViewById(R.id.termsRecyclerView);
        mAdapter = new TermListAdapter(mItems, getContext());
        mTermsRecyclerView.setAdapter(mAdapter);
        mTermsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) mTermsRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(mTermsRecyclerView.getContext(), linearLayoutManager.getOrientation());
        mTermsRecyclerView.addItemDecoration(decoration);
        FloatingActionButton fab = root.findViewById(R.id.addTermButton);
        fab.setOnClickListener(this::onAddTermButtonClick);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTermListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermListViewModel.class);
        mTermListViewModel.getTerms().observe(getViewLifecycleOwner(), this::onTermListChanged);
    }

    private void onTermListChanged(List<TermEntity> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddTermButtonClick(View view) {
//        Intent intent = new Intent(getContext(), EditTermActivity.class);
//        startActivity(intent);
    }

}