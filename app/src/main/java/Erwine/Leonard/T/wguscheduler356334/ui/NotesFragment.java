package Erwine.Leonard.T.wguscheduler356334.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import Erwine.Leonard.T.wguscheduler356334.R;

public class NotesFragment extends Fragment {

    public static final String ARGS_KEY = "notes";

    private EditText notesEditText;

    public synchronized String getText() {
        EditText text = notesEditText;
        if (null != text) {
            return text.getText().toString();
        }
        Bundle args = getArguments();
        return (null == args) ? "" : args.getString(ARGS_KEY, "");
    }

    public synchronized void setText(String text) {
        EditText editText = notesEditText;
        if (null != editText) {
            editText.setText(text);
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

    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        notesEditText = (EditText) inflater.inflate(R.layout.notes_fragment, container, false);
        Bundle args = getArguments();
        if (null != args) {
            notesEditText.setText(args.getString(ARGS_KEY, ""));
        }
        return notesEditText;
    }
}