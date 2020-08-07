package Erwine.Leonard.T.wguscheduler356334.ui.terms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.EditTermActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.SampleData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsFragment extends Fragment {

//    private TermsViewModel termsViewModel;

    private RecyclerView recyclerView;

    //private Context context;
    private List<TermItemViewModel> items = new ArrayList<>();
    private ItemsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        termsViewModel = ViewModelProviders.of(this).get(TermsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_terms, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_terms);
        FloatingActionButton fab = root.findViewById(R.id.addTermActionButton);
        fab.setOnClickListener(this::onNewTermClick);
        initRecyclerView();
        items.addAll(SampleData.getData());

//        final TextView textView = root.findViewById(R.id.text_terms);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void onNewTermClick(View view) {
        Intent intent = new Intent(getContext(), EditTermActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ItemsAdapter(items, getContext());
        recyclerView.setAdapter(adapter);
    }
}