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
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;

/**
 * A fragment representing a list of Items.
 */
public class EmailAddressListFragment extends Fragment {

    //    private static final String ARG_MENTOR_ID = "mentor-id";
    private final List<EmailAddressEntity> mItems;
    //    private Integer mMentorId;
    private RecyclerView emailListRecyclerView;
    private EmailAddressListAdapter mAdapter;
    private EmailAddressListViewModel emailAddressListViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EmailAddressListFragment() {
        mItems = new ArrayList<>();
    }

//    public static EmailAddressListFragment newInstance(int mentorId) {
//        EmailAddressListFragment fragment = new EmailAddressListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_MENTOR_ID, mentorId);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Bundle arguments = getArguments();
//        if (null != arguments) {
//            mMentorId = arguments.getInt(ARG_MENTOR_ID);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_email_address_list, container, false);

        emailListRecyclerView = root.findViewById(R.id.emailListRecyclerView);
        mAdapter = new EmailAddressListAdapter(mItems, getContext());
        emailListRecyclerView.setAdapter(mAdapter);

        emailListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = Objects.requireNonNull((LinearLayoutManager) emailListRecyclerView.getLayoutManager());
        DividerItemDecoration decoration = new DividerItemDecoration(emailListRecyclerView.getContext(), linearLayoutManager.getOrientation());
        emailListRecyclerView.addItemDecoration(decoration);

        FloatingActionButton fab = root.findViewById(R.id.addEmailButton);
        fab.setOnClickListener(this::onAddEmailButtonClick);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        emailAddressListViewModel = MainActivity.getViewModelFactory(requireActivity().getApplication()).create(EmailAddressListViewModel.class);
        emailAddressListViewModel.getLiveData().observe(getViewLifecycleOwner(), this::onEmailAddressListChanged);
    }

    private void onEmailAddressListChanged(List<EmailAddressEntity> list) {
        mItems.clear();
        mItems.addAll(list);
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onAddEmailButtonClick(View view) {
    }
}