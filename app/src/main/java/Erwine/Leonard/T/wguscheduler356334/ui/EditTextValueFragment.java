package Erwine.Leonard.T.wguscheduler356334.ui;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;

public class EditTextValueFragment extends Fragment {

    private CharSequence mLabelText = "";
    private CharSequence mErrorText;
    private CharSequence mCurrentValue = "";
    private TextView labelTextView;
    private EditText valueEditText;
    private List<TextWatcher> textWatchers = new ArrayList<>();

    public synchronized CharSequence getLabelText() {
        return (null == labelTextView) ? mLabelText : labelTextView.getText();
    }

    public synchronized void setLabelText(String labelText) {
        if (null == labelTextView) {
            mLabelText = (null == labelText) ? "" : labelText;
        } else {
            labelTextView.setText((null == labelText) ? "" : labelText);
        }
    }

    public synchronized CharSequence getErrorText() {
        return (null == valueEditText) ? mErrorText : labelTextView.getError();
    }

    public synchronized void setErrorText(String errorText) {
        if (null == valueEditText) {
            mErrorText = errorText;
        } else {
            valueEditText.setError(errorText, AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        }
    }

    public synchronized CharSequence getCurrentValue() {
        return (null == valueEditText) ? mCurrentValue : valueEditText.getText();
    }

    public synchronized void setCurrentValue(CharSequence currentValue) {
        if (null == valueEditText) {
            mCurrentValue = (null == currentValue) ? "" : currentValue;
        } else {
            valueEditText.setText((null == currentValue) ? "" : currentValue);
        }
    }

    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_text_value, container, false);
        (labelTextView = view.findViewById(R.id.labelTextView)).setText(mLabelText);
        (valueEditText = view.findViewById(R.id.valueEditText)).setText(mCurrentValue);
        valueEditText.setError(mErrorText, AppCompatResources.getDrawable(requireContext(), R.drawable.dialog_error));
        textWatchers.forEach(t -> valueEditText.addTextChangedListener(t));
        textWatchers = null;
        mLabelText = mErrorText = mCurrentValue = null;
        return view;
    }

    public synchronized TextWatcher addTextChangedListener(Consumer<String> afterTextChanged) {
        TextWatcher watcher = StringHelper.createAfterTextChangedListener(afterTextChanged);
        if (null == valueEditText) {
            textWatchers.add(watcher);
        } else {
            valueEditText.addTextChangedListener(watcher);
        }
        return watcher;
    }

    public synchronized void removeTextChangedListener(TextWatcher watcher) {
        if (null == valueEditText) {
            textWatchers.remove(watcher);
        } else {
            valueEditText.removeTextChangedListener(watcher);
        }
    }

}