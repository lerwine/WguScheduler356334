package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import Erwine.Leonard.T.wguscheduler356334.EditTermActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.TermEntity;

public class TermsFragment extends Fragment {

    private TermsViewModel termsViewModel;
    private RecyclerView recyclerView;
    private List<TermEntity> items;
    private TermsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        items = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_terms, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_terms);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = root.findViewById(R.id.addTermActionButton);
        fab.setOnClickListener(this::onNewTermClick);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        termsViewModel = ViewModelProviders.of(this).get(TermsViewModel.class);
        termsViewModel = MainActivity.getViewModelFactory(getActivity().getApplication()).create(TermsViewModel.class);
        termsViewModel.getTerms().observe(getViewLifecycleOwner(), this::onTermListChanged);
    }

    private void onTermListChanged(List<TermEntity> list) {
        items.clear();
        items.addAll(list);
        if (null == adapter) {
            adapter = new TermsAdapter(items, getContext());
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