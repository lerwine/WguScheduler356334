package Erwine.Leonard.T.wguscheduler356334.entity;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity {

    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer();

    //<editor-fold defaultstate="collapsed" desc="Static Members" >

    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";

    @Ignore
    private String altPhoneNumbers;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fields">

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ColumnInfo(name = COLNAME_NAME)
    private String name;
    @Ignore
    private String primaryPhone;
    @Ignore
    private String altEmailAddresses;
    private String phoneNumbers;
    @Ignore
    private String primaryEmail;
    private String emailAddresses;
    private String notes;
    @Ignore
    private LiveData<List<CourseEntity>> courses;

    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses, int id) {
        this(name, notes, phoneNumbers, emailAddresses);
        this.id = id;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    @Ignore
    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
        this.phoneNumbers = MULTI_LINE_NORMALIZER.apply(phoneNumbers);
        this.emailAddresses = MULTI_LINE_NORMALIZER.apply(emailAddresses);
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
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    public synchronized String getPhoneNumbers() {
        if (null == phoneNumbers) {
            if (primaryPhone.isEmpty())
                phoneNumbers = altPhoneNumbers;
            else if (altPhoneNumbers.isEmpty())
                phoneNumbers = primaryPhone;
            else
                phoneNumbers = primaryPhone + "\n" + altPhoneNumbers;
        }
        return phoneNumbers;
    }

    public synchronized void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = MULTI_LINE_NORMALIZER.apply(phoneNumbers);
        primaryPhone = altPhoneNumbers = null;
    }

    public synchronized String getPrimaryPhone() {
        if (null == primaryPhone)
            calculatePrimaryAndAltPhone();
        return primaryPhone;
    }

    public synchronized void setPrimaryPhone(String primaryPhone) {
        if (null == altPhoneNumbers)
            calculatePrimaryAndAltPhone();
        this.primaryPhone = SINGLE_LINE_NORMALIZER.apply(primaryPhone);
        phoneNumbers = null;
    }

    public synchronized String getAltPhoneNumbers() {
        if (null == altPhoneNumbers)
            calculatePrimaryAndAltPhone();
        return altPhoneNumbers;
    }

    public synchronized void setAltPhoneNumbers(String altPhoneNumbers) {
        if (null == primaryPhone)
            calculatePrimaryAndAltPhone();
        this.altPhoneNumbers = MULTI_LINE_NORMALIZER.apply(altPhoneNumbers);
        phoneNumbers = null;
    }

    public synchronized String getEmailAddresses() {
        if (null == emailAddresses) {
            if (primaryEmail.isEmpty())
                emailAddresses = altEmailAddresses;
            else if (altEmailAddresses.isEmpty())
                emailAddresses = primaryEmail;
            else
                emailAddresses = primaryEmail + "\n" + altEmailAddresses;
        }
        return emailAddresses;
    }

    public synchronized void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = MULTI_LINE_NORMALIZER.apply(emailAddresses);
        primaryEmail = altEmailAddresses = null;
    }

    public synchronized String getPrimaryEmail() {
        if (null == primaryEmail)
            calculatePrimaryAndAltEmail();
        return primaryEmail;
    }

    public synchronized void setPrimaryEmail(String primaryEmail) {
        if (null == altEmailAddresses)
            calculatePrimaryAndAltEmail();
        this.primaryEmail = SINGLE_LINE_NORMALIZER.apply(primaryEmail);
        emailAddresses = null;
    }

    public synchronized String getAltEmailAddresses() {
        if (null == altEmailAddresses)
            calculatePrimaryAndAltEmail();
        return altEmailAddresses;
    }

    public synchronized void setAltEmailAddresses(String altEmailAddresses) {
        if (null == primaryEmail)
            calculatePrimaryAndAltEmail();
        this.altEmailAddresses = MULTI_LINE_NORMALIZER.apply(altEmailAddresses);
        emailAddresses = null;
    }

    //</editor-fold>

    private void calculatePrimaryAndAltPhone() {
        int i = phoneNumbers.indexOf('\n');
        if (i > 0) {
            altPhoneNumbers = phoneNumbers.substring(i + 1);
            primaryPhone = phoneNumbers.substring(0, i);
        } else {
            primaryPhone = phoneNumbers;
            altPhoneNumbers = "";
        }
    }

    private void calculatePrimaryAndAltEmail() {
        int i = emailAddresses.indexOf('\n');
        if (i > 0) {
            altEmailAddresses = emailAddresses.substring(i + 1);
            primaryEmail = emailAddresses.substring(0, i);
        } else {
            primaryEmail = emailAddresses;
            altEmailAddresses = "";
        }
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
                primaryPhone.equals(that.primaryPhone) &&
                primaryEmail.equals(that.primaryEmail) &&
                altPhoneNumbers.equals(that.altPhoneNumbers) &&
                altEmailAddresses.equals(that.altEmailAddresses) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return id;
        }
        return Objects.hash(id, name, primaryPhone, primaryEmail, altPhoneNumbers, altEmailAddresses, notes);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryPhone=" + primaryPhone +
                ", phoneNumbers=" + altPhoneNumbers +
                ", primaryEmail=" + primaryEmail +
                ", emailAddresses=" + altEmailAddresses +
                ", notes='" + notes + '\'' +
                '}';
    }

    //</editor-fold>

}
