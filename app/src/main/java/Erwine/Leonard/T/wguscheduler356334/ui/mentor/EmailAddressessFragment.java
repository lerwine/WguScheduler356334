package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;

public class EmailAddressessFragment extends Fragment {

    public static final String ARGS_KEY = "email-addresses";

    private final LiveBoolean anyElementNonEmpty;
    private EmailAddressesAdapter listAdapter;
    private RecyclerView emailAddressesRecyclerView;
    private FloatingActionButton addEmailAddressButton;

    public EmailAddressessFragment() {
        anyElementNonEmpty = new LiveBoolean();
    }

    public synchronized String getText() {
        EmailAddressesAdapter adapter = listAdapter;
        if (null != adapter) {
            return adapter.getText();
        }
        Bundle args = getArguments();
        return (null == args) ? "" : args.getString(ARGS_KEY, "");
    }

    public synchronized void setText(String text) {
        EmailAddressesAdapter adapter = listAdapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_addresses_fragment, container, false);
        emailAddressesRecyclerView = view.findViewById(R.id.emailAddressesRecyclerView);
        Bundle args = getArguments();
        listAdapter = new EmailAddressesAdapter((null == args) ? "" : args.getString(ARGS_KEY, ""));
        emailAddressesRecyclerView.setAdapter(listAdapter);
        addEmailAddressButton = view.findViewById(R.id.addEmailAddressButton);
        addEmailAddressButton.setOnClickListener(this::onNewEmailAddressClick);
        return view;
    }

    private void onNewEmailAddressClick(View view) {
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