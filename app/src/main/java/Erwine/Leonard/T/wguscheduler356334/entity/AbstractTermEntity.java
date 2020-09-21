package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

public abstract class AbstractTermEntity<T extends AbstractTermEntity<T>> extends AbstractNotedEntity<T> implements NoteColumnIncludedEntity {

    /**
     * The name of the {@link #name "name"} database column, which contains the name of the term.
     */
    public static final String COLNAME_NAME = "name";
    /**
     * The name of the {@link #start "start"} database column, which contains the inclusive start date for the term.
     */
    public static final String COLNAME_START = "start";
    /**
     * The name of the {@link #end "end"} database column, which contains the inclusive end date for the term.
     */
    public static final String COLNAME_END = "end";

    public static final String STATE_KEY_ID = AppDb.TABLE_NAME_TERMS + "." + COLNAME_ID;
    public static final String STATE_KEY_NAME = AppDb.TABLE_NAME_TERMS + "." + COLNAME_NAME;
    public static final String STATE_KEY_START = AppDb.TABLE_NAME_TERMS + "." + COLNAME_START;
    public static final String STATE_KEY_END = AppDb.TABLE_NAME_TERMS + "." + COLNAME_END;
    public static final String STATE_KEY_NOTES = AppDb.TABLE_NAME_TERMS + "." + COLNAME_NOTES;
    public static final String STATE_KEY_ORIGINAL_NAME = "o:" + STATE_KEY_NAME;
    public static final String STATE_KEY_ORIGINAL_START = "o:" + STATE_KEY_START;
    public static final String STATE_KEY_ORIGINAL_END = "o:" + STATE_KEY_END;
    public static final String STATE_KEY_ORIGINAL_NOTES = "o:" + STATE_KEY_NOTES;

    @ColumnInfo(name = COLNAME_NAME, collate = ColumnInfo.NOCASE)
    private String name;
    @ColumnInfo(name = COLNAME_START)
    private LocalDate start;
    @ColumnInfo(name = COLNAME_END)
    private LocalDate end;

    @Ignore
    protected AbstractTermEntity(Long id, String name, LocalDate start, LocalDate end, String notes) {
        super(id, notes);
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.start = start;
        this.end = end;
    }

    protected AbstractTermEntity(@NonNull AbstractTermEntity<?> source) {
        super(source);
        this.name = source.name;
        this.start = source.start;
        this.end = source.end;
    }

    protected AbstractTermEntity(@NonNull Bundle bundle, boolean original) {
        super(STATE_KEY_ID, (original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, bundle);
        name = bundle.getString((original) ? STATE_KEY_ORIGINAL_NAME : STATE_KEY_NAME, "");
        String key = (original) ? STATE_KEY_ORIGINAL_START : STATE_KEY_START;
        if (bundle.containsKey(key)) {
            start = LocalDate.ofEpochDay(bundle.getLong(key));
        }
        key = (original) ? STATE_KEY_ORIGINAL_END : STATE_KEY_END;
        if (bundle.containsKey(key)) {
            end = LocalDate.ofEpochDay(bundle.getLong(key));
        }
    }

    public void saveState(@NonNull Bundle bundle, boolean original) {
        Long id = getId();
        if (null != id) {
            bundle.putLong(STATE_KEY_ID, getId());
        }
        bundle.putString((original) ? STATE_KEY_ORIGINAL_NAME : STATE_KEY_NAME, name);
        LocalDate d = start;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_START : STATE_KEY_START, d.toEpochDay());
        }
        d = end;
        if (null != d) {
            bundle.putLong((original) ? STATE_KEY_ORIGINAL_END : STATE_KEY_END, d.toEpochDay());
        }
        bundle.putString((original) ? STATE_KEY_ORIGINAL_NOTES : STATE_KEY_NOTES, name);
    }

    /**
     * Gets the name of the term.
     *
     * @return The name of the term, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the term.
     *
     * @param name The new name of the term.
     */
    public void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    /**
     * Gets the start date of the term.
     *
     * @return The inclusive start date of the term or {@code null} if the start date has not been determined.
     */
    @Nullable
    public LocalDate getStart() {
        return start;
    }

    /**
     * Sets the start date of the term.
     *
     * @param start The new inclusive start date of the term or {@code null} if the start date has not been determined.
     */
    public void setStart(@Nullable LocalDate start) {
        this.start = start;
    }

    /**
     * Gets the end date of the term.
     *
     * @return The inclusive end date of the term or {@code null} if the end date has not been determined.
     */
    @Nullable
    public LocalDate getEnd() {
        return end;
    }

    /**
     * Sets the end date of the term.
     *
     * @param end The new inclusive end date of the term or {@code null} if the end date has not been determined.
     */
    public void setEnd(@Nullable LocalDate end) {
        this.end = end;
    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return name.equals(other.getName()) &&
                Objects.equals(start, other.getStart()) &&
                Objects.equals(end, other.getEnd()) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(name, start, end, getNotes());
    }
}
