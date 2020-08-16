package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

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

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorListFragment extends Fragment {

    private MentorListViewModel mentorListViewModel;
    private RecyclerView mentorsRecyclerView;
    private List<MentorEntity> items;
    private MentorListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        items = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_mentor_list, container, false);

        mentorsRecyclerView = root.findViewById(R.id.mentorsRecyclerView);
        mentorsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mentorsRecyclerView.getLayoutManager();
        DividerItemDecoration decoration = new DividerItemDecoration(mentorsRecyclerView.getContext(), linearLayoutManager.getOrientation());
        mentorsRecyclerView.addItemDecoration(decoration);
        FloatingActionButton fab = root.findViewById(R.id.addMentorButton);
        fab.setOnClickListener(this::onNewMentorClick);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mentorListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(MentorListViewModel.class);
        mentorListViewModel.getMentors().observe(getViewLifecycleOwner(), this::onMentorListChanged);
    }

    private void onMentorListChanged(List<MentorEntity> list) {
        items.clear();
        items.addAll(list);
        if (null == adapter) {
            adapter = new MentorListAdapter(items, getContext());
            mentorsRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void onNewMentorClick(View view) {
        Intent intent = new Intent(getContext(), EditMentorActivity.class);
        startActivity(intent);
    }
}