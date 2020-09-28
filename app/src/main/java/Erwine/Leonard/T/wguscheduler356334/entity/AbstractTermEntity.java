package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.time.LocalDate;
import java.util.Objects;

public abstract class AbstractTermEntity<T extends AbstractTermEntity<T>> extends AbstractNotedEntity<T> implements Term {

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

    @Ignore
    protected AbstractTermEntity(@NonNull AbstractTermEntity<?> source) {
        super(source);
        this.name = source.name;
        this.start = source.start;
        this.end = source.end;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    @Override
    @Nullable
    public LocalDate getStart() {
        return start;
    }

    @Override
    public void setStart(@Nullable LocalDate start) {
        this.start = start;
    }

    @Override
    @Nullable
    public LocalDate getEnd() {
        return end;
    }

    @Override
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
