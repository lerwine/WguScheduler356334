package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity {

    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ColumnInfo(name = COLNAME_NAME)
    private String name;
    private String phoneNumbers;
    private String emailAddresses;
    private String notes;
    @Ignore
    private LiveData<List<CourseEntity>> courses;

    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses, int id) {
        this(name, notes, phoneNumbers, emailAddresses);
        this.id = id;
    }

    @Ignore
    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses) {
        this.name = Values.asNonNullAndWsNormalized(name);
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
    }

    @Ignore
    public MentorEntity(String name, String notes) {
        this(name, notes, null, null);
    }

    @Ignore
    public MentorEntity() {
        this(null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = Values.asNonNullAndWsNormalizedMultiLine(notes);
    }

    public synchronized String getPhoneNumbers() { return phoneNumbers; }

    public synchronized void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = Values.asWsNormalizedStringLines(phoneNumbers);
    }

    public synchronized String getEmailAddresses() { return emailAddresses; }

    public synchronized void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = Values.asWsNormalizedStringLines(emailAddresses);
    }

    @Ignore
    public LiveData<List<CourseEntity>> getCourses(Context context) {
        if (null == courses) {
            if (id < 1) {
                throw new IllegalStateException();
            }
            courses = DbLoader.getInstance(context).getCoursesByMentorId(id);
        }
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentorEntity that = (MentorEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id &&
                name.equals(that.name) &&
                phoneNumbers.equals(that.phoneNumbers) &&
                emailAddresses.equals(that.emailAddresses) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, name, notes, phoneNumbers, emailAddresses);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                ", emailAddresses=" + emailAddresses +
                ", notes='" + notes + '\'' +
                '}';
    }
}
