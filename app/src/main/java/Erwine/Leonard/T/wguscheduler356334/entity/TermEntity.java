package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.TermDAO;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_TERMS, indices = {
        @Index(value = TermEntity.COLNAME_NAME, name = TermEntity.INDEX_NAME, unique = true)
})
public class TermEntity {

    public static final String INDEX_NAME = "IDX_TERM_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";
    public static final String SAMPLE_TERM_1 = "Term 1";
    public static final String SAMPLE_TERM_2 = "Term 2";
    public static final String SAMPLE_TERM_3 = "Term 3";
    public static final String SAMPLE_TERM_4 = "Term 4";
    public static final String SAMPLE_TERM_5 = "Term 5";
    public static final String SAMPLE_TERM_6 = "Term 6";
    public static final String SAMPLE_TERM_7 = "Term 7";
    public static final String SAMPLE_TERM_8 = "Term 8";
    public static final String SAMPLE_TERM_9 = "Term 9";
    public static final String SAMPLE_TERM_10 = "Term 10";

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

    public static void populateSampleData(AppDb appDb) {
        TermDAO dao = appDb.termDAO();
        dao.insertAllItems(
                new TermEntity(SAMPLE_TERM_1, LocalDate.of(2015, 5, 1), LocalDate.of(2015, 10, 31), ""),
                new TermEntity(SAMPLE_TERM_2, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 6, 30), ""),
                new TermEntity(SAMPLE_TERM_3, LocalDate.of(2016, 7, 1), LocalDate.of(2016, 12, 31), ""),
                new TermEntity(SAMPLE_TERM_4, LocalDate.of(2017, 2, 1), LocalDate.of(2017, 7, 31), ""),
                new TermEntity(SAMPLE_TERM_5, LocalDate.of(2017, 8, 1), LocalDate.of(2018, 1, 31), ""),
                new TermEntity(SAMPLE_TERM_6, LocalDate.of(2018, 2, 1), LocalDate.of(2018, 7, 31), ""),
                new TermEntity(SAMPLE_TERM_7, LocalDate.of(2018, 8, 1), LocalDate.of(2019, 1, 31), ""),
                new TermEntity(SAMPLE_TERM_8, LocalDate.of(2019, 2, 1), LocalDate.of(2019, 7, 31), ""),
                new TermEntity(SAMPLE_TERM_9, LocalDate.of(2019, 10, 1), LocalDate.of(2020, 3, 31), ""),
                new TermEntity(SAMPLE_TERM_10, LocalDate.of(2020, 4, 1), LocalDate.of(2020, 9, 30), "")
        );
        List<TermEntity> allItems = dao.getAllItems();
        HashMap<String, SampleData> allTerms = new HashMap<>();
        allItems.forEach(t -> allTerms.put(t.name, new SampleData(t)));
        CourseEntity.populateSampleData(appDb, allTerms);
    }

    static class SampleData {
        private TermEntity term;
        private CourseEntity.SampleData[] courses;

        private SampleData(TermEntity term) {
            this.term = term;
        }

        TermEntity getTerm() {
            return term;
        }

        CourseEntity.SampleData[] getCourses() {
            return courses;
        }
    }
}
