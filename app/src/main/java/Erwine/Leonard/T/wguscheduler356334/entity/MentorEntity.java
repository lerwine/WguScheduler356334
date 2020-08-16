package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.MentorDAO;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity {

    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";
    public static final String SAMPLE_MENTOR_SCOTT_STROMBERG = "Scott Stromberg, M.CS.";
    public static final String SAMPLE_MENTOR_ANDY_DOREN = "Andy Doren";
    public static final String SAMPLE_MENTOR_KATIE_CRAIG = "Katie Craig, MBA";
    public static final String SAMPLE_MENTOR_STEVEN_HARRIS = "Steven Harris, PhD";
    public static final String SAMPLE_MENTOR_SABRENA_PARTON = "Sabrena Parton";
    public static final String SAMPLE_MENTOR_ABIGAIL_SCHEG = "Abigail Scheg";
    public static final String SAMPLE_MENTOR_CONSTANCE_BLANSON = "Constance Blanson";
    public static final String SAMPLE_MENTOR_JEFF_EDMUNDS = "Jeff Edmunds, Ph.D.";
    public static final String SAMPLE_MENTOR_JESSICA_SHIELDS = "Jessica Shields";
    public static final String SAMPLE_MENTOR_MICHAEL_SMITH = "Michael Smith";
    public static final String SAMPLE_MENTOR_ED_LAVIERI = "Ed Lavieri";
    public static final String SAMPLE_MENTOR_MASON_MCWATTERS = "Mason McWatters";
    public static final String SAMPLE_MENTOR_KANDI_DUFF = "Kandi Duff";
    public static final String SAMPLE_MENTOR_JIM_JIVIDEN = "Jim Jividen, JD, MA";
    public static final String SAMPLE_MENTOR_NICK_MEYER = "Nick Meyer, PhD";
    public static final String SAMPLE_MENTOR_RANDY_RUTLEDGE = "Randy Rutledge";
    public static final String SAMPLE_MENTOR_MARIA_SCHENK = "Maria Schenk";
    public static final String SAMPLE_MENTOR_JOE_BARNHART = "Joe Barnhart, Ph.D.";
    public static final String SAMPLE_MENTOR_WANDA_BURWICK = "Wanda Burwick";
    public static final String SAMPLE_MENTOR_CHARLES_LIVELY = "Charles Lively";
    public static final String SAMPLE_MENTOR_MALCOLM_WABARA = "Malcolm Wabara";
    public static final String SAMPLE_MENTOR_MARK_KINKEAD = "Mark Kinkead";
    public static final String SAMPLE_MENTOR_ALVARO_ESCOBAR = "Alvaro Escobar";

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

    static HashMap<String, MentorEntity> populateSampleData(AppDb appDb) {
        MentorDAO dao = appDb.mentorDAO();
        dao.insertAllItems(
                new MentorEntity(SAMPLE_MENTOR_SCOTT_STROMBERG, "", "1-877-435-7948 ext. 4750", "sstromberg@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_ANDY_DOREN, "", "", "andy.doren@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_KATIE_CRAIG, "", "1-877-435-7948 ext. 6695", "Katie.Craig@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_STEVEN_HARRIS, "", "1-877-435-7948  x4023", "steven.harris@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_SABRENA_PARTON, "", "1-877-435-7948 ext 6467", "sabrena.parton@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_ABIGAIL_SCHEG, "", "", "abigail.scheg@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_CONSTANCE_BLANSON, "", "", "constance.blanson@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_JEFF_EDMUNDS, "", "877-435-7948, ext 5925", "jeff.edmunds@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_JESSICA_SHIELDS, "", "1.877.435.7948 Ext. 4198", "cmweb@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_MICHAEL_SMITH, "", "", "m.smith@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_ED_LAVIERI, "", "", "ed.lavieri@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_MASON_MCWATTERS, "", "1.877.435.7948 Ext. 5532", "mason.mcwatters@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_KANDI_DUFF, "", "1.877.435.7948 Ext. 4090", "kandi.duff@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_JIM_JIVIDEN, "", "801-924-4510\n1-877-435-7948 x4510", "jim.jividen@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_NICK_MEYER, "", "1-877-435-7948 ext. 2337", "nick.meyer@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_RANDY_RUTLEDGE, "", "1.877.435.7948 Ext. 8246", "randy.rutledge@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_MARIA_SCHENK, "", "1.877.435.7948 Ext. 4029", "maria.schenk@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_JOE_BARNHART, "", "1.877.435.7948 Ext. 4889", "joe.barnhart@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_WANDA_BURWICK, "", "", "wanda.burwick@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_CHARLES_LIVELY, "", "(385) 428-4645", "charles.lively@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_MALCOLM_WABARA, "", "(385) 428-5006", "malcolm.wabara@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_MARK_KINKEAD, "", "1-385-428-3617", "mark.kinkead@wgu.edu"),
                new MentorEntity(SAMPLE_MENTOR_ALVARO_ESCOBAR, "", "(385) 428-8835", "alvaro.escobar@wgu.edu")
        );
        List<MentorEntity> allItems = dao.getAllItems();
        HashMap<String, MentorEntity> allMentors = new HashMap<>();
        allItems.forEach(t -> allMentors.put(t.name, t));
        return allMentors;
    }

}
