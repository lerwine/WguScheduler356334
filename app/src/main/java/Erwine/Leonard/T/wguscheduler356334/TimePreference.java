package Erwine.Leonard.T.wguscheduler356334;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

public class TimePreference extends DialogPreference {
    public static int DEFAULT_HOUR = 7;
    public static int DEFAULT_MINUTE = 0;
    public static int DEFAULT_VALUE = (DEFAULT_HOUR * 60) + DEFAULT_MINUTE;

    private int time;
    private int dialogLayoutResource = R.layout.pref_dialog_time;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        persistInt(time);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        setTime(getPersistedInt((null == defaultValue) ? DEFAULT_VALUE : (int) defaultValue));
    }

    @Override
    public int getDialogLayoutResource() {
        return dialogLayoutResource;
    }
}