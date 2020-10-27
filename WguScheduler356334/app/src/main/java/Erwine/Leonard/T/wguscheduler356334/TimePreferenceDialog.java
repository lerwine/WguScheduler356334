package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class TimePreferenceDialog extends PreferenceDialogFragmentCompat {

    private static final String LOG_TAG = MainActivity.getLogTag(TimePreferenceDialog.class);

    private TimePicker timePicker;

    public static TimePreferenceDialog newInstance(String key) {
        final TimePreferenceDialog fragment = new TimePreferenceDialog();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        Log.d(LOG_TAG, "Enter onBindDialogView");
        super.onBindDialogView(view);
        timePicker = view.findViewById(R.id.prefDialogTime);
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            int minutes = ((TimePreference) preference).getTime();
            boolean is24hour = DateFormat.is24HourFormat(requireContext());
            timePicker.setIs24HourView(is24hour);
            timePicker.setHour(minutes / 60);
            timePicker.setMinute(minutes % 60);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        Log.d(LOG_TAG, "Enter onDialogClosed(" + positiveResult + ")");
        if (positiveResult) {
            // generate value to save
            int hours = timePicker.getHour();
            int minutes = timePicker.getMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;
            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);
                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.setTime(minutesAfterMidnight);
                }
            }
        }
    }
}
