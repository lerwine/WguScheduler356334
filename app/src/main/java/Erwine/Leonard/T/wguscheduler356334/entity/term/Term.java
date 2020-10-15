package Erwine.Leonard.T.wguscheduler356334.entity.term;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.NoteColumnIncludedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageBuilder;

public interface Term extends NoteColumnIncludedEntity {

    /**
     * The name of the {@code "name"} database column, which contains the name of the term.
     */
    String COLNAME_NAME = "name";
    /**
     * The name of the {@code "start"} database column, which contains the inclusive start date for the term.
     */
    String COLNAME_START = "start";
    /**
     * The name of the {@code "end"} database column, which contains the inclusive end date for the term.
     */
    String COLNAME_END = "end";

    static void validate(ResourceMessageBuilder builder, TermEntity entity) {
        if (entity.getName().isEmpty()) {
            builder.acceptError(R.string.message_name_required);
        }
        LocalDate end = entity.getEnd();
        if (null != end) {
            LocalDate start = entity.getStart();
            if (null == start) {
                builder.acceptError(R.string.message_start_required_with_end);
            } else if (start.compareTo(end) > 0) {
                builder.acceptError(R.string.message_start_after_end);
            }
        }
    }

    /**
     * Gets the name of the term.
     *
     * @return The name of the term.
     */
    @NonNull
    String getName();

    /**
     * Sets the name of the term.
     *
     * @param name The new name of the term.
     */
    void setName(String name);

    /**
     * Gets the start date of the term.
     *
     * @return The inclusive start date of the term or {@code null} if the start date has not been determined.
     */
    @Nullable
    LocalDate getStart();

    /**
     * Sets the start date of the term.
     *
     * @param start The new inclusive start date of the term or {@code null} if the start date has not been determined.
     */
    void setStart(@Nullable LocalDate start);

    /**
     * Gets the end date of the term.
     *
     * @return The inclusive end date of the term or {@code null} if the end date has not been determined.
     */
    @Nullable
    LocalDate getEnd();

    /**
     * Sets the end date of the term.
     *
     * @param end The new inclusive end date of the term or {@code null} if the end date has not been determined.
     */
    void setEnd(@Nullable LocalDate end);

    @NonNull
    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_TERMS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.restoreState(bundle, isOriginal);
        setName(bundle.getString(stateKey(COLNAME_NAME, isOriginal), ""));
        String key = stateKey(COLNAME_START, isOriginal);
        if (bundle.containsKey(key)) {
            setStart(LocalDateConverter.toLocalDate(bundle.getLong(key)));
        } else {
            setStart(null);
        }
        key = stateKey(COLNAME_END, isOriginal);
        if (bundle.containsKey(key)) {
            setEnd(LocalDateConverter.toLocalDate(bundle.getLong(key)));
        } else {
            setEnd(null);
        }
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.saveState(bundle, isOriginal);
        bundle.putString(stateKey(COLNAME_NAME, isOriginal), getName());
        Long d = LocalDateConverter.fromLocalDate(getStart());
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_START, isOriginal), d);
        }
        d = LocalDateConverter.fromLocalDate(getEnd());
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_END, isOriginal), d);
        }
    }

    @Override
    default void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        NoteColumnIncludedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_NAME, getName())
                .append(COLNAME_START, getStart())
                .append(COLNAME_END, getEnd())
                .append(COLNAME_NOTES, getNotes());
    }

}
