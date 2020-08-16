package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EmailAddressessFragment extends Fragment {

    private IndexedStringList emailAddressesList;
    private EmailAddressesAdapter listAdapter;
    private RecyclerView emailAddressesRecyclerView;
    private FloatingActionButton addEmailAddressButton;

    public EmailAddressessFragment() {
        emailAddressesList = new IndexedStringList();
    }

    public LiveData<Boolean> isAnyElementNonEmpty() {
        return emailAddressesList.isAnyElementNonEmpty();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_addresses_fragment, container, false);
        emailAddressesRecyclerView = view.findViewById(R.id.emailAddressesRecyclerView);
        addEmailAddressButton = view.findViewById(R.id.addEmailAddressButton);
        addEmailAddressButton.setOnClickListener(this::onNewEmailAddressClick);
        return view;
    }

    public void setEmailAddresses(String emailAddressesList) {
        String[] lines = (null == emailAddressesList || emailAddressesList.isEmpty()) ? new String[0] : Values.REGEX_LINEBREAKN.split(emailAddressesList);
        this.emailAddressesList.clear();
        this.emailAddressesList.addValue(lines);
        if (null == listAdapter) {
            listAdapter = new EmailAddressesAdapter(this.emailAddressesList);
            emailAddressesRecyclerView.setAdapter(listAdapter);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void onNewEmailAddressClick(View view) {
        this.emailAddressesList.addValue("");
        listAdapter.notifyDataSetChanged();
    }

}