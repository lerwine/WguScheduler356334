package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Intent;
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

import Erwine.Leonard.T.wguscheduler356334.EditTermActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;

public class TermListFragment extends Fragment {

    private TermListViewModel termListViewModel;
    private RecyclerView recyclerView;
    private List<TermEntity> items;
    private TermListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        items = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_term_list, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_terms);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        FloatingActionButton fab = root.findViewById(R.id.addTermActionButton);
        fab.setOnClickListener(this::onNewTermClick);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        termListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(TermListViewModel.class);
        termListViewModel.getTerms().observe(getViewLifecycleOwner(), this::onTermListChanged);
    }

    private void onTermListChanged(List<TermEntity> list) {
        items.clear();
        items.addAll(list);
        if (null == adapter) {
            adapter = new TermListAdapter(items, getContext());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void onNewTermClick(View view) {
        Intent intent = new Intent(getContext(), EditTermActivity.class);
        startActivity(intent);
    }

}