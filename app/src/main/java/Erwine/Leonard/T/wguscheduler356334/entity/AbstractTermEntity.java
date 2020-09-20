package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

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
    public void setStart(LocalDate start) {
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
    public void setEnd(LocalDate end) {
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
