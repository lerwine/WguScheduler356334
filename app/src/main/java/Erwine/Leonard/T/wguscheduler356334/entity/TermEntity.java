package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_TERMS, indices = {
        @Index(value = TermEntity.COLNAME_NAME, name = TermEntity.INDEX_NAME, unique = true)
})
public class TermEntity implements Comparable<TermEntity> {

    public static final String INDEX_NAME = "IDX_TERM_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";
    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Long id;

    public TermEntity(String name, LocalDate start, LocalDate end, String notes, long id) {
        this(name, start, end, notes);
        this.id = id;
    }

    @ColumnInfo(name = COLNAME_NAME)
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String notes;

    public static void applyInsertedId(TermEntity source, long id) {
        if (null != source.getId()) {
            throw new IllegalStateException();
        }
        source.id = id;
    }

    @Ignore
    public TermEntity(String name, LocalDate start, LocalDate end, String notes) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.start = start;
        this.end = end;
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Ignore
    public TermEntity() {
        this(null, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermEntity that = (TermEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id && name.equals(that.name) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                notes.equals(that.notes);
    }

    @Override
    public synchronized int hashCode() {
        if (null != id) {
            return id.hashCode();
        }
        return Objects.hash(name, start, end, notes);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
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
        Long i = that.id;
        if (null == i) {
            return (null == id) ? name.compareTo(that.name) : -1;
        }
        return (null == id) ? 1 : Long.compare(id, i);
    }

    @NonNull
    @Override
    public String toString() {
        return "TermEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", notes='" + notes + '\'' +
                '}';
    }
}
