package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_TERMS, indices = {
        @Index(value = TermEntity.COLNAME_NAME, name = TermEntity.INDEX_NAME, unique = true)
})
public class TermEntity {

    public static final String INDEX_NAME = "IDX_TERM_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ColumnInfo(name = COLNAME_NAME)
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String notes;
    @Ignore
    private LiveData<List<CourseEntity>> courses;

    public TermEntity(String name, LocalDate start, LocalDate end, String notes, int id) {
        this(name, start, end, notes);
        this.id = id;
    }

    @Ignore
    public TermEntity(String name, LocalDate start, LocalDate end, String notes) {
        this.name = Values.asNonNullAndWsNormalized(name);
        this.start = start;
        this.end = end;
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    @Ignore
    public TermEntity() {
        this(null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Values.asNonNullAndWsNormalized(name);
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
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    @Ignore
    public LiveData<List<CourseEntity>> getCourses(Context context) {
        if (null == courses) {
            if (id < 1) {
                throw new IllegalStateException();
            }
            courses = DbLoader.getInstance(context).getCoursesByTermId(id);
        }
        return courses;
    }

    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(name, start, end, notes);
    }

    @SuppressWarnings("NullableProblems")
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
