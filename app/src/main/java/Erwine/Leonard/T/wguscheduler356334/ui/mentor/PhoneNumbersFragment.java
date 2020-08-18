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

/**
 * A fragment representing a list of Items.
 */
public class PhoneNumbersFragment extends Fragment {

    public static final String ARGS_KEY = "phone-numbers";

    private final LiveBoolean anyElementNonEmpty;
    private PhoneNumbersAdapter listAdapter;
    private RecyclerView phoneNumbersRecyclerView;
    private FloatingActionButton addPhoneNumberButton;

    public PhoneNumbersFragment() {
        anyElementNonEmpty = new LiveBoolean();
    }

    public synchronized String getText() {
        PhoneNumbersAdapter adapter = listAdapter;
        if (null != adapter) {
            return adapter.getText();
        }
        Bundle args = getArguments();
        return (null == args) ? "" : args.getString(ARGS_KEY, "");
    }

    public synchronized void setText(String text) {
        PhoneNumbersAdapter adapter = listAdapter;
        if (null != adapter) {
            adapter.setText(text);
        } else {
            Bundle args = getArguments();
            if (null == args) {
                args = new Bundle();
                args.putString(ARGS_KEY, text);
                setArguments(args);
            } else {
                args.putString(ARGS_KEY, text);
            }
        }
    }

    public LiveData<Boolean> isAnyElementNonEmpty() {
        return anyElementNonEmpty;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_numbers_fragment, container, false);
        phoneNumbersRecyclerView = view.findViewById(R.id.phoneNumbersRecyclerView);
        Bundle args = getArguments();
        listAdapter = new PhoneNumbersAdapter((null == args) ? "" : args.getString(ARGS_KEY, ""));
        phoneNumbersRecyclerView.setAdapter(listAdapter);
        addPhoneNumberButton = view.findViewById(R.id.addPhoneNumberButton);
        addPhoneNumberButton.setOnClickListener(this::onNewPhoneNumberClick);
        return view;
    }

    private void onNewPhoneNumberClick(View view) {
        this.listAdapter.addBlank();
    }

    private static class LiveBoolean extends LiveData<Boolean> {
        private boolean currentValue = false;

        private LiveBoolean() {
            super(false);
        }

        private synchronized void updateValue(boolean value) {
            if (value != currentValue) {
                currentValue = value;
                postValue(value);
            }
        }
    }

}