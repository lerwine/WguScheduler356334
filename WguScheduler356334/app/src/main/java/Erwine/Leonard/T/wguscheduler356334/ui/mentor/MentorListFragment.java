package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;

/**
 * A fragment representing a list of Items.
 */
public class MentorListFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.getLogTag(MentorListFragment.class);

    private final List<MentorListItem> mItems;
    @SuppressWarnings("FieldCanBeLocal")
    private MentorListViewModel mentorListViewModel;
    private MentorListAdapter mAdapter;

    public MentorListFragment() {
        mItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Enter onCreateView");
        View root = inflater.inflate(R.layout.fragment_mentor_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.mentorListRecyclerView);
        mAdapter = new MentorListAdapter(mItems, getContext());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) recyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

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

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Enter onDestroy");
        super.onDestroy();
    }

    private void onMentorListChanged(List<MentorListItem> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddMentorButtonClick(View view) {
        EditMentorViewModel.startEditMentorActivity(requireContext(), null);
    }


}