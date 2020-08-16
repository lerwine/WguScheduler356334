package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

/**
 * A fragment representing a list of Items.
 */
public class PhoneNumbersFragment extends Fragment {

    private IndexedStringList phoneNumbersList;
    private PhoneNumbersAdapter listAdapter;
    private RecyclerView phoneNumbersRecyclerView;
    private FloatingActionButton addPhoneNumberButton;

    public PhoneNumbersFragment() {
        phoneNumbersList = new IndexedStringList();
    }

    public LiveData<Boolean> isAnyElementNonEmpty() {
        return phoneNumbersList.isAnyElementNonEmpty();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_numbers_fragment, container, false);
        phoneNumbersRecyclerView = view.findViewById(R.id.phoneNumbersRecyclerView);
        addPhoneNumberButton = view.findViewById(R.id.addPhoneNumberButton);
        addPhoneNumberButton.setOnClickListener(this::onNewPhoneNumberClick);
        return view;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        String[] lines = (null == phoneNumbers || phoneNumbers.isEmpty()) ? new String[0] : Values.REGEX_LINEBREAKN.split(phoneNumbers);
        this.phoneNumbersList.clear();
        this.phoneNumbersList.addValue(lines);
        if (null == listAdapter) {
            listAdapter = new PhoneNumbersAdapter(this.phoneNumbersList);
            phoneNumbersRecyclerView.setAdapter(listAdapter);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void onNewPhoneNumberClick(View view) {
        this.phoneNumbersList.addValue("");
        listAdapter.notifyDataSetChanged();
    }

}