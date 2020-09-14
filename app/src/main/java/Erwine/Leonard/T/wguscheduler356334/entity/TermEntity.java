package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_TERMS, indices = {
        @Index(value = TermEntity.COLNAME_NAME, name = TermEntity.INDEX_NAME, unique = true)
})
public final class TermEntity extends AbstractNotedEntity<TermEntity> implements Comparable<TermEntity>, HasTermProperties {

    /**
     * The name of the unique index for the {@link #name} database column.
     */
    public static final String INDEX_NAME = "IDX_TERM_NAME";
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
    private TermEntity(Long id, String name, LocalDate start, LocalDate end, String notes) {
        super(id, notes);
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.start = start;
        this.end = end;
    }

    /**
     * Initializes a new {@code TermEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     *
     * @param name  The name of the term.
     * @param start The inclusive start date of the term, which can be {@code null} if no start date has been determined.
     * @param end   The inclusive end date of the term, which can be {@code null} if no end date has been determined.
     * @param notes User notes to be associated with the term.
     * @param id    The value of the {@link #COLNAME_ID primary key column}.
     */
    public TermEntity(String name, LocalDate start, LocalDate end, String notes, long id) {
        this(id, name, start, end, notes);
    }

    /**
     * Initializes a new {@code TermEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     *
     * @param name  The name of the term.
     * @param start The inclusive start date of the term, which can be {@code null} if no start date has been determined.
     * @param end   The inclusive end date of the term, which can be {@code null} if no end date has been determined.
     * @param notes User notes to be associated with the term.
     */
    @Ignore
    public TermEntity(String name, LocalDate start, LocalDate end, String notes) {
        this(null, name, start, end, notes);
    }

    /**
     * Initializes a new {@code TermEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     */
    @Ignore
    public TermEntity() {
        this(null, null, null, null, null);
    }

    /**
     * Gets the name of the term.
     *
     * @return The name of the term, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    @Override
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
    @Override
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
    @Override
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
    protected boolean equalsEntity(@NonNull TermEntity other) {
        return name.equals(other.name) &&
                Objects.equals(start, other.start) &&
                Objects.equals(end, other.end) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(name, start, end, getNotes());
    }

    @SuppressWarnings(value = "UnnecessaryLocalVariable")
    @Override
    public synchronized int compareTo(TermEntity o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
        TermEntity that = (TermEntity) o;
        LocalDate date = that.start;
        int result = Values.compareDateRanges(start, end, that.start, that.end);
        if (result != 0) {
            return result;
        }
        Long i = that.getId();
        if (null == i) {
            return (null == getId()) ? name.compareTo(that.name) : -1;
        }
        return (null == getId()) ? 1 : Long.compare(getId(), i);
    }

    @NonNull
    @Override
    public String toString() {
        return "TermEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", notes='" + getNotes() + '\'' +
                '}';
    }
}
