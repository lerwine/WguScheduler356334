package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

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

import java.util.ArrayList;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorListFragment extends Fragment {

    private MentorListViewModel mentorListViewModel;
    private RecyclerView recycler_view_mentors;
    private List<MentorEntity> items;
    private MentorListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        items = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_mentor_list, container, false);

        recycler_view_mentors = root.findViewById(R.id.recycler_view_mentors);
        recycler_view_mentors.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_mentors.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(recycler_view_mentors.getContext(), linearLayoutManager.getOrientation());
        recycler_view_mentors.addItemDecoration(decoration);
//        FloatingActionButton fab = root.findViewById(R.id.addMentorActionButton);
//        fab.setOnClickListener(this::onNewMentorClick);

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
            recycler_view_mentors.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

//    private void onNewMentorClick(View view) {
//        Intent intent = new Intent(getContext(), EditTermActivity.class);
//        startActivity(intent);
//    }
}