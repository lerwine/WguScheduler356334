package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity {

    //<editor-fold defaultstate="collapsed" desc="Static Members" >

    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";
    public static final String COLNAME_PHONE_NUMBER = "phoneNumber";
    public static final String COLNAME_EMAIL_ADDRESS = "emailAddress";
    public static final String COLNAME_NOTES = "notes";

    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer();

    public static void applyInsertedId(MentorEntity source, long id) {
        if (null != source.getId()) {
            throw new IllegalStateException();
        }
        source.id = id;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fields">

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Long id;

    @ColumnInfo(name = COLNAME_NAME)
    private String name;

    @ColumnInfo(name = COLNAME_PHONE_NUMBER)
    private String phoneNumber;

    @ColumnInfo(name = COLNAME_EMAIL_ADDRESS)
    private String emailAddress;

    @ColumnInfo(name = COLNAME_NOTES)
    private String notes;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress, long id) {
        this(name, notes, phoneNumber, emailAddress);
        this.id = id;
    }

    @Ignore
    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    public synchronized String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
    }

    public synchronized String getEmailAddress() {
        return emailAddress;
    }

    public synchronized void setEmailAddress(String emailAddress) {
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    //</editor-fold>

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
                phoneNumber.equals(that.phoneNumber) &&
                emailAddress.equals(that.emailAddress) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (null != id) {
            return id.hashCode();
        }
        return Objects.hash(id, name, phoneNumber, emailAddress, notes);
    }

    @NonNull
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", emailAddress=" + emailAddress +
                ", notes='" + notes + '\'' +
                '}';
    }

    //</editor-fold>

}
