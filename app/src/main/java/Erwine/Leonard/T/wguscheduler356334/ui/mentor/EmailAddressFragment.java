package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.dummy.DummyContent;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

/**
 * A fragment representing a list of Items.
 */
public class EmailAddressFragment extends Fragment {

    public static final String ARG_EMAIL_ADDRESSES = "email-addresses";
    private ArrayList<String> emailAddresses = new ArrayList<>();
    private EmailAddressListAdapter listAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EmailAddressFragment() {
    }

    @SuppressWarnings("unused")
    public static EmailAddressFragment newInstance(ArrayList<String> emailAddresses) {
        EmailAddressFragment fragment = new EmailAddressFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_EMAIL_ADDRESSES, emailAddresses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            emailAddresses = getArguments().getStringArrayList(ARG_EMAIL_ADDRESSES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_address_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            listAdapter = new EmailAddressListAdapter(emailAddresses);
            recyclerView.setAdapter(listAdapter);
        }
        return view;
    }

    public void setEmailAddresses(String emailAddresses) {
        String[] lines = (null == emailAddresses || emailAddresses.isEmpty()) ? new String[0] : Values.REGEX_LINEBREAKN.split(emailAddresses);
        this.emailAddresses.clear();
        Collections.addAll(this.emailAddresses, lines);
        if (null != listAdapter) {
            listAdapter.setEmailAddresses(this.emailAddresses);
        }
    }
}