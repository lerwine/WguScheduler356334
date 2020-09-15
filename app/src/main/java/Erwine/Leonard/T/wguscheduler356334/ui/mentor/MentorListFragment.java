package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.EditMentorActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

/**
 * A fragment representing a list of Items.
 */
public class MentorListFragment extends Fragment {

    private final List<MentorEntity> mItems;
    private MentorListViewModel mentorListViewModel;
    private RecyclerView mMentorListRecyclerView;
    private MentorListAdapter mAdapter;

    public MentorListFragment() {
        mItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mentor_list, container, false);

        mMentorListRecyclerView = root.findViewById(R.id.mentorListRecyclerView);
        mAdapter = new MentorListAdapter(mItems, getContext());
        mMentorListRecyclerView.setAdapter(mAdapter);

        mMentorListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) mMentorListRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(mMentorListRecyclerView.getContext(), linearLayoutManager.getOrientation());
        mMentorListRecyclerView.addItemDecoration(decoration);

        FloatingActionButton fab = root.findViewById(R.id.addMentorButton);
        fab.setOnClickListener(this::onAddMentorButtonClick);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mentorListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(MentorListViewModel.class);
        mentorListViewModel.getMentors().observe(getViewLifecycleOwner(), this::onMentorListChanged);
    }

    private void onMentorListChanged(List<MentorEntity> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddMentorButtonClick(View view) {
        Context context = requireActivity();
        Intent intent = new Intent(context, EditMentorActivity.class);
        Log.i(getClass().getName(), "Starting EditMentorActivity with no mentorId");
        context.startActivity(intent);
    }


}