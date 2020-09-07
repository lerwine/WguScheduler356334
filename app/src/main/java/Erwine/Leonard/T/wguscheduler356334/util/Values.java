package Erwine.Leonard.T.wguscheduler356334.util;

import android.text.Editable;
import android.text.TextWatcher;

import java.time.LocalDate;
import java.util.function.Consumer;

public class Values {


    public static TextWatcher textWatcherForTextChanged(final Consumer<String> onTextChanged) {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextChanged.accept(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        };
    }


    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalizedMultiLine(String value) {
        return StringHelper.normalizeString(value);
    }

    public static int compareDateRanges(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        if (null != start1) {
            if (null == start2) {
                return (null == end2) ? -1 : 1;
            }
            int result = start1.compareTo(start2);
            if (result != 0) {
                return result;
            }
            if (null == end1) {
                return (null == end2) ? 0 : 1;
            }
        } else if (null == end1) {
            return (null == start2 && null == end2) ? 0 : 1;
        }
        return (null == end2) ? -1 : end1.compareTo(end2);
    }

}
