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
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.MentorDAO;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity {

    //<editor-fold defaultstate="collapsed" desc="Static Members" >

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
    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM, StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM, StringNormalizationOption.REMOVE_BLANK_LINES);

    static HashMap<String, MentorEntity> populateSampleData(AppDb appDb) {
        MentorDAO dao = appDb.mentorDAO();
        dao.insertAllItems(
                new MentorEntity(SAMPLE_MENTOR_SCOTT_STROMBERG, "", IndexedStringList.of("1-877-435-7948 ext. 4750"), IndexedStringList.of("sstromberg@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_ANDY_DOREN, "", IndexedStringList.of(""), IndexedStringList.of("andy.doren@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_KATIE_CRAIG, "", IndexedStringList.of("1-877-435-7948 ext. 6695"), IndexedStringList.of("Katie.Craig@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_STEVEN_HARRIS, "", IndexedStringList.of("1-877-435-7948  x4023"), IndexedStringList.of("steven.harris@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_SABRENA_PARTON, "", IndexedStringList.of("1-877-435-7948 ext 6467"), IndexedStringList.of("sabrena.parton@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_ABIGAIL_SCHEG, "", IndexedStringList.of(""), IndexedStringList.of("abigail.scheg@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_CONSTANCE_BLANSON, "", IndexedStringList.of(""), IndexedStringList.of("constance.blanson@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_JEFF_EDMUNDS, "", IndexedStringList.of("877-435-7948, ext 5925"), IndexedStringList.of("jeff.edmunds@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_JESSICA_SHIELDS, "", IndexedStringList.of("1.877.435.7948 Ext. 4198"), IndexedStringList.of("cmweb@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_MICHAEL_SMITH, "", IndexedStringList.of(""), IndexedStringList.of("m.smith@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_ED_LAVIERI, "", IndexedStringList.of(""), IndexedStringList.of("ed.lavieri@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_MASON_MCWATTERS, "", IndexedStringList.of("1.877.435.7948 Ext. 5532"), IndexedStringList.of("mason.mcwatters@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_KANDI_DUFF, "", IndexedStringList.of("1.877.435.7948 Ext. 4090"), IndexedStringList.of("kandi.duff@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_JIM_JIVIDEN, "", IndexedStringList.of("801-924-4510\n1-877-435-7948 x4510"), IndexedStringList.of("jim.jividen@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_NICK_MEYER, "", IndexedStringList.of("1-877-435-7948 ext. 2337"), IndexedStringList.of("nick.meyer@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_RANDY_RUTLEDGE, "", IndexedStringList.of("1.877.435.7948 Ext. 8246"), IndexedStringList.of("randy.rutledge@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_MARIA_SCHENK, "", IndexedStringList.of("1.877.435.7948 Ext. 4029"), IndexedStringList.of("maria.schenk@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_JOE_BARNHART, "", IndexedStringList.of("1.877.435.7948 Ext. 4889"), IndexedStringList.of("joe.barnhart@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_WANDA_BURWICK, "", IndexedStringList.of(""), IndexedStringList.of("wanda.burwick@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_CHARLES_LIVELY, "", IndexedStringList.of("(385) 428-4645"), IndexedStringList.of("charles.lively@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_MALCOLM_WABARA, "", IndexedStringList.of("(385) 428-5006"), IndexedStringList.of("malcolm.wabara@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_MARK_KINKEAD, "", IndexedStringList.of("1-385-428-3617"), IndexedStringList.of("mark.kinkead@wgu.edu")),
                new MentorEntity(SAMPLE_MENTOR_ALVARO_ESCOBAR, "", IndexedStringList.of("(385) 428-8835"), IndexedStringList.of("alvaro.escobar@wgu.edu"))
        );
        List<MentorEntity> allItems = dao.getAllItems();
        HashMap<String, MentorEntity> allMentors = new HashMap<>();
        allItems.forEach(t -> allMentors.put(t.name, t));
        return allMentors;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fields">

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ColumnInfo(name = COLNAME_NAME)
    private String name;
    private IndexedStringList phoneNumbers_obsolete;
    @Ignore
    private String primaryPhone;
    private String phoneNumbers;
    private IndexedStringList emailAddresses_obsolete;
    @Ignore
    private String primaryEmail;
    private String emailAddresses;
    private String notes;
    @Ignore
    private LiveData<List<CourseEntity>> courses;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public MentorEntity(String name, String notes, IndexedStringList phoneNumbers_obsolete, IndexedStringList emailAddresses_obsolete, int id) {
        this(name, notes, phoneNumbers_obsolete, emailAddresses_obsolete);
        this.id = id;
    }

    @Ignore
    public MentorEntity(String name, String notes, IndexedStringList phoneNumbers_obsolete, IndexedStringList emailAddresses_obsolete) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
        this.phoneNumbers_obsolete = phoneNumbers_obsolete;
        this.emailAddresses_obsolete = emailAddresses_obsolete;
    }

    @Ignore
    public MentorEntity(String name, String notes) {
        this(name, notes, null, null);
    }

    @Ignore
    public MentorEntity() {
        this(null, null, null, null);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">

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
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public synchronized IndexedStringList getPhoneNumbers_obsolete() {
        return phoneNumbers_obsolete;
    }

    public synchronized void setPhoneNumbers_obsolete(IndexedStringList phoneNumbers_obsolete) {
        this.phoneNumbers_obsolete = phoneNumbers_obsolete;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public synchronized IndexedStringList getEmailAddresses_obsolete() {
        return emailAddresses_obsolete;
    }

    public synchronized void setEmailAddresses_obsolete(IndexedStringList emailAddresses_obsolete) {
        this.emailAddresses_obsolete = emailAddresses_obsolete;
    }

    //</editor-fold>

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

    //<editor-fold desc="Overrides">

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
                phoneNumbers_obsolete.equals(that.phoneNumbers_obsolete) &&
                emailAddresses_obsolete.equals(that.emailAddresses_obsolete) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, name, notes, phoneNumbers_obsolete, emailAddresses_obsolete);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumbers=" + phoneNumbers_obsolete +
                ", emailAddresses=" + emailAddresses_obsolete +
                ", notes='" + notes + '\'' +
                '}';
    }

    //</editor-fold>

}
