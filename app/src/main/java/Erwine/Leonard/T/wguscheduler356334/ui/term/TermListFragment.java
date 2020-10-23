package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String LOG_TAG = MainActivity.getLogTag(TermListFragment.class);
    private static final int NEW_TERM_REQUEST_CODE = 1;
    private final List<TermListItem> mItems;
    private TermListAdapter mAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private TermListViewModel viewModel;

    public TermListFragment() {
        mItems = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_TERM_REQUEST_CODE && null != data && data.hasExtra(EditTermViewModel.EXTRA_KEY_TERM_ID)) {
            long termId = data.getLongExtra(EditTermViewModel.EXTRA_KEY_TERM_ID, 0L);
            EditTermViewModel.startViewTermActivity(requireContext(), termId);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onTermListChanged(List<TermListItem> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddTermButtonClick(View view) {
        EditTermViewModel.startAddTermActivity(
                this,
                NEW_TERM_REQUEST_CODE,
                mItems.stream().map(AbstractTermEntity::getEnd).filter(Objects::nonNull).max(LocalDate::compareTo).map(t -> t.plusDays(1L)).orElseGet(LocalDate::now)
        );
    }

}