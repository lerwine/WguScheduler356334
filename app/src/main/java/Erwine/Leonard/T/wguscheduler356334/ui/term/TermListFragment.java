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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;

public class TermListFragment extends Fragment {

    private final List<TermListItem> mItems;
    private TermListAdapter mAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private TermListViewModel viewModel;

    public TermListFragment() {
        mItems = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_term_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mTermsRecyclerView = view.findViewById(R.id.termsRecyclerView);
        mAdapter = new TermListAdapter(mItems, requireContext());
        mTermsRecyclerView.setAdapter(mAdapter);

        mTermsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) mTermsRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(mTermsRecyclerView.getContext(), linearLayoutManager.getOrientation());
        mTermsRecyclerView.addItemDecoration(decoration);

        FloatingActionButton fab = view.findViewById(R.id.addTermButton);
        fab.setOnClickListener(this::onAddTermButtonClick);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermListViewModel.class);
        viewModel.getTerms().observe(getViewLifecycleOwner(), this::onTermListChanged);
    }

    private void onTermListChanged(List<TermListItem> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddTermButtonClick(View view) {
        //noinspection ConstantConditions
        EditTermViewModel.startAddTermActivity(
                requireContext(),
                mItems.stream().map(AbstractTermEntity::getEnd).filter(Objects::nonNull).max(LocalDate::compareTo).map(t -> t.plusDays(1L)).orElseGet(LocalDate::now)
        );
    }

}