package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
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

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;

/**
 * A fragment representing a list of Items.
 */
public class PhoneNumberListFragment extends Fragment {

    private final List<PhoneNumberEntity> mItems;
    //    private static final String ARG_MENTOR_ID = "mentor-id";
//    private Integer mMentorId;
    private RecyclerView phoneNumberListRecyclerView;
    private PhoneNumberListAdapter mAdapter;
    private PhoneNumberListViewModel phoneNumberListViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhoneNumberListFragment() {
        mItems = new ArrayList<>();
    }

//    public static PhoneNumberListFragment newInstance(int mentorId) {
//        PhoneNumberListFragment fragment = new PhoneNumberListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_MENTOR_ID, mentorId);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle arguments = getArguments();
//        if (null != arguments) {
//            mMentorId = arguments.getInt(ARG_MENTOR_ID);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_phone_number_list, container, false);
        phoneNumberListRecyclerView = root.findViewById(R.id.phoneNumberListRecyclerView);
        mAdapter = new PhoneNumberListAdapter(mItems, getContext());
        phoneNumberListRecyclerView.setAdapter(mAdapter);
        phoneNumberListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) phoneNumberListRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(phoneNumberListRecyclerView.getContext(), linearLayoutManager.getOrientation());
        phoneNumberListRecyclerView.addItemDecoration(decoration);
        FloatingActionButton fab = root.findViewById(R.id.addPhoneButton);
        fab.setOnClickListener(this::onAddPhoneButtonClick);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        phoneNumberListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(PhoneNumberListViewModel.class);
        phoneNumberListViewModel.getLiveData().observe(getViewLifecycleOwner(), this::onPhoneNumberListChanged);
    }

    private void onPhoneNumberListChanged(List<PhoneNumberEntity> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddPhoneButtonClick(View view) {

    }
}